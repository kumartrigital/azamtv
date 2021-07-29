package org.mifosplatform.organisation.partner.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.domain.Base64EncodedImage;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.documentmanagement.exception.DocumentManagementException;
import org.mifosplatform.infrastructure.security.exception.NoAuthorizationException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeAdditionalInfo;
import org.mifosplatform.organisation.office.domain.OfficeAdditionalInfoRepository;
import org.mifosplatform.organisation.office.domain.OfficeAddress;
import org.mifosplatform.organisation.office.domain.OfficeAddressRepository;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.organisation.partner.serialization.PartnersCommandFromApiJsonDeserializer;
import org.mifosplatform.provisioning.provisioning.api.ProvisioningApiConstants;
import org.mifosplatform.provisioning.provisioning.service.ProvisioningWritePlatformService;
import org.mifosplatform.provisioning.provsionactions.domain.ProvisionActions;
import org.mifosplatform.provisioning.provsionactions.domain.ProvisioningActionsRepository;
import org.mifosplatform.useradministration.api.UsersApiResource;
import org.mifosplatform.useradministration.domain.AppUser;
import org.mifosplatform.useradministration.domain.AppUserRepository;
import org.mifosplatform.useradministration.domain.Role;
import org.mifosplatform.useradministration.domain.RoleRepository;
import org.mifosplatform.useradministration.exception.RoleNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hugo
 *
 */
@Service
public class PartnersWritePlatformServiceImp implements PartnersWritePlatformService {

	private final static Logger LOGGER = LoggerFactory.getLogger(PartnersWritePlatformServiceImp.class);
	private final PlatformSecurityContext context;
	private final PartnersCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final OfficeRepository officeRepository;
    private final RoleRepository roleRepository;
    private final UsersApiResource userApiResource;
    private final AppUserRepository userRepository;
    private final ProvisioningActionsRepository provisioningActionsRepository;
    private final ProvisioningWritePlatformService provisioningWritePlatformService;
    private final OfficeAddressRepository addressRepository;
    private final OfficeAdditionalInfoRepository officeAdditionalInfoRepository;

	@Autowired
	public PartnersWritePlatformServiceImp(final PlatformSecurityContext context,final PartnersCommandFromApiJsonDeserializer apiJsonDeserializer,
			final OfficeRepository officeRepository,final RoleRepository roleRepository,final UsersApiResource userApiResource,final AppUserRepository userRepository,
			final OfficeAddressRepository addressRepository,final OfficeAdditionalInfoRepository officeAdditionalInfoRepository,
			final ProvisioningActionsRepository provisioningActionsRepository,final ProvisioningWritePlatformService provisioningWritePlatformService) {
		
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.officeRepository = officeRepository;
		this.roleRepository = roleRepository;
		this.provisioningActionsRepository=provisioningActionsRepository;
		this.provisioningWritePlatformService = provisioningWritePlatformService;
		this.userApiResource = userApiResource;
		this.userRepository = userRepository;
		this.addressRepository = addressRepository;
		this.officeAdditionalInfoRepository = officeAdditionalInfoRepository;

	}

	
	@Transactional
	@Override
	public CommandProcessingResult createNewPartner(final JsonCommand command) {

		try {

			final AppUser currentUser = this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			Long parentId = null;
			if (command.parameterExists("parentId")) {
				parentId = command.longValueOfParameterNamed("parentId");
			}
			final Office parentOffice = this.validateUserPriviledgeOnOfficeAndRetrieve(currentUser,parentId);
			Office office = Office.fromPartner(parentOffice, command);
			final BigDecimal creditlimit = command.bigDecimalValueOfParameterNamed("creditlimit");
			final String loginName = command.stringValueOfParameterNamed("loginName");
			final String password = command.stringValueOfParameterNamed("password");
			final String repeatPassword = command.stringValueOfParameterNamed("repeatPassword");
			final String currency = command.stringValueOfParameterNamed("currency");
			//final String businessType = command.stringValueOfParameterNamed("businessType");
			//final String externalId = command.stringValueOfParameterNamed("externalId");
			//final String officeType = command.stringValueOfParameterNamed("officeType");
			//final LocalDate openingDate = command.localDateValueOfParameterNamed("openingDate");
			
			final boolean isCollective= command.booleanPrimitiveValueOfParameterNamed("isCollective");
			final String contactName = command.stringValueOfParameterNamed("contactName");
			OfficeAddress address =OfficeAddress.fromJson(command,office);
			OfficeAdditionalInfo additionalInfo = new OfficeAdditionalInfo(office,creditlimit,currency,isCollective,contactName);
			office.setOfficeAddress(address);
			office.setOfficeAdditionalInfo(additionalInfo);
			this.officeRepository.save(office);
			office.generateHierarchy();
			this.officeRepository.saveAndFlush(office);
			
			//create user
		    final String roleName = command.stringValueOfParameterNamed("roleName");
		    final String[]  roles= arrayOfRole(roleName);
		    JSONObject json = new JSONObject();
		    json.put("username", loginName);
		    json.put("password", password);
		    json.put("repeatPassword", repeatPassword);
		    json.put("firstname",office.getName());
		    json.put("lastname", office.getName());
		    json.put("sendPasswordToEmail",Boolean.FALSE);
		    json.put("email",address.getEmail());
		    json.put("officeId", office.getId());
		    json.put("roles", new JSONArray(roles));
	        final String result=this.userApiResource.createUser(json.toString());
	        JSONObject resultJson = new JSONObject(result);
	        final String userId=resultJson.getString("resourceId");
	        
           ProvisionActions provisionActions=this.provisioningActionsRepository.findOneByProvisionType(ProvisioningApiConstants.PROV_EVENT_CREATE_AGENT);
			
            if(provisionActions != null && provisionActions.isEnable() == 'Y'){
				
				this.provisioningWritePlatformService.postDetailsForProvisioning(Long.valueOf(0),office.getId(),ProvisioningApiConstants.REQUEST_CREATE_AGENT,
						               provisionActions.getProvisioningSystem(),null);
			}
			
	        return new CommandProcessingResultBuilder().withCommandId(command.commandId())
					       .withEntityId(additionalInfo.getId()).withOfficeId(office.getId()).withResourceIdAsString(userId).build();
		} catch (final DataIntegrityViolationException e) {
			handleDataIntegrityIssues(command, e);
			return new CommandProcessingResult(Long.valueOf(-1l));
		} catch (JSONException e) {
			e.printStackTrace();
			return new CommandProcessingResult(Long.valueOf(-1l));
		}

	}

	 private Office validateUserPriviledgeOnOfficeAndRetrieve(final AppUser currentUser, final Long officeId) {

	        final Long currentUserOfficeId = currentUser.getOffice().getId();
	        final Office currentUserOffice = this.officeRepository.findOne(currentUserOfficeId);
	        if (currentUserOffice == null) { throw new OfficeNotFoundException(currentUserOfficeId); }

	        if (currentUserOffice.doesNotHaveAnOfficeInHierarchyWithId(officeId)) { throw new NoAuthorizationException(
	                "User does not have sufficient priviledges to act on the provided office."); }

	        Office officeToReturn = currentUserOffice;
	        if (!currentUserOffice.identifiedBy(officeId)) {
	            officeToReturn = this.officeRepository.findOne(officeId);
	            if (officeToReturn == null) { throw new OfficeNotFoundException(officeId); }
	        }

	        return officeToReturn;
	    }


	private String[] arrayOfRole(final String name) {
		
		  final Role role = this.roleRepository.findOneByName(name);
          if (role == null) { throw new RoleNotFoundException(Long.valueOf(name)); }
          String[] roles={role.getId().toString()};
          return roles;
	
	}
	

	private void handleDataIntegrityIssues(final JsonCommand command,final DataIntegrityViolationException dve) {

		final Throwable realCause = dve.getMostSpecificCause();
		LOGGER.error(dve.getMessage(), dve);
		if(realCause.getMessage().contains("name_org")) {
	            final String name = command.stringValueOfParameterNamed("partnerName");
	            throw new PlatformDataIntegrityException("error.msg.partner.duplicate.name", "Partner with name `" + name + "` already exists",
	                    "name", name);
	         }else if(realCause.getMessage().contains("externalid_org")) {
	        	 final String externalId=command.stringValueOfParameterNamed("externalId");
	            throw new PlatformDataIntegrityException("error.msg.office or partner.duplicate.externalId", "office or partner with externalId `" + externalId + "` already exists",
	                    "externalId", externalId);
	        }else if(realCause.getMessage().contains("phonenumber_org")) {
	        	 final String phoneNumber=command.stringValueOfParameterNamed("phoneNumber");
		            throw new PlatformDataIntegrityException("error.msg.office or partner.duplicate.phoneNumber", "office or partner with phoneNumber `" + phoneNumber + "` already exists",
		                    "phoneNumber", phoneNumber);
		        }else if(realCause.getMessage().contains("emailid_org")) {
		        	 final String email=command.stringValueOfParameterNamed("email");
			            throw new PlatformDataIntegrityException("error.msg.office or partner.duplicate.email", "office or partner with email `" + email + "` already exists",
			                    "email", email);
			        }
		
		throw new PlatformDataIntegrityException("error.msg.could.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());
	}
	
	
	
	@Transactional
	@Override
	public CommandProcessingResult saveOrUpdatePartnerImage(final Long officeId, final String imageName,final InputStream inputStream) {
		try {

			final Office office =this.officeRepository.findOne(officeId);
			final OfficeAddress officeAddress = this.addressRepository.findOneWithPartnerId(office);
			if (officeAddress == null) {
				throw new OfficeNotFoundException(officeId);
			}
			final String imageUploadLocation = setupForPartnerImageUpdate(officeId, officeAddress);
			final String imageLocation = FileUtils.saveToFileSystem(inputStream, imageUploadLocation, imageName);
			officeAddress.setCompanyLogo(imageLocation);
			this.addressRepository.save(officeAddress);
			return new CommandProcessingResult(officeId);

		} catch (IOException ioe) {
			LOGGER.error(ioe.getMessage(), ioe);
			throw new DocumentManagementException(imageName);
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult saveOrUpdatePartnerImage(final Long officeId, final Base64EncodedImage encodedImage) {

		try {
			final Office office =this.officeRepository.findOne(officeId);
			final OfficeAddress officeAddress = this.addressRepository.findOneWithPartnerId(office);
			if (officeAddress == null) {
				throw new OfficeNotFoundException(officeId);
			}
			final String imageUploadLocation = setupForPartnerImageUpdate(officeId, officeAddress);
			final String imageLocation = FileUtils.saveToFileSystem(encodedImage, imageUploadLocation, "image");
			officeAddress.setCompanyLogo(imageLocation);
			this.addressRepository.save(officeAddress);
			return new CommandProcessingResult(officeId);
			// return updatePartnerImage(partnerId, officeAddress, imageLocation);
		} catch (IOException ioe) {
			LOGGER.error(ioe.getMessage(), ioe);
			throw new DocumentManagementException("image");
		}
	}

	private String setupForPartnerImageUpdate(final Long partnerId,final OfficeAddress officeAddress) {

		final String imageUploadLocation = FileUtils.generatePartnersImageDirectory(partnerId);
		// delete previous image from the file system
		if (StringUtils.isNotEmpty(officeAddress.getCompanyLogo())) {
			FileUtils.deleteClientImage(partnerId,officeAddress.getCompanyLogo());
		}
		/** Recursively create the directory if it does not exist **/
		if (!new File(imageUploadLocation).isDirectory()) {
			new File(imageUploadLocation).mkdirs();
		}
		return imageUploadLocation;
	}


	@Transactional
	@Override
	public CommandProcessingResult updatePartner(final JsonCommand command,final Long partnerId) {
		
		try {
			final AppUser currentUser = context.authenticatedUser();
			this.apiJsonDeserializer.validateForUpdate(command.json());
			
		    Long parentId = null;
            if (command.parameterExists("parentId")) {
                parentId = command.longValueOfParameterNamed("parentId");
            }

            final OfficeAdditionalInfo officeAdditionalInfo=this.officeAdditionalInfoRepository.findOne(partnerId);
            final Office office = this.validateUserPriviledgeOnOfficeAndRetrieve(currentUser, officeAdditionalInfo.getOffice().getId());
            if(office==null){
            	throw new OfficeNotFoundException(partnerId);
            }
           // final Office office = officeAdditionalInfo.getOffice();
            //update office
            final Map<String, Object> officeChanges = office.update(command);
            if (officeChanges.containsKey("parentId")) {
                final Office parent =this.validateUserPriviledgeOnOfficeAndRetrieve(currentUser, parentId);
                office.update(parent);
            }
            
            //update officeAddress
           final  OfficeAddress officeAddress  = this.addressRepository.findOneWithPartnerId(office);
           final Map<String, Object> addressChanges = officeAddress.update(command);
           if(!addressChanges.isEmpty()){
           office.setOfficeAddress(officeAddress);
		   }
           
           //update additonal info
           final Map<String, Object> infoChanges  = officeAdditionalInfo.update(command);
           office.setOfficeAdditionalInfo(officeAdditionalInfo);
           if(!infoChanges.isEmpty()){
        	   office.setOfficeAdditionalInfo(officeAdditionalInfo);
           }
           
           this.officeRepository.saveAndFlush(office);
           
           //update user
           final Long  userId = command.longValueOfParameterNamed("userId");
           final String loginName = command.stringValueOfParameterNamed("loginName");
           final AppUser user=this.userRepository.findOne(userId);
           if(!user.getUsername().equalsIgnoreCase(loginName)){
        	   user.setUsername(loginName);
           }else if(!user.getEmail().equalsIgnoreCase(officeAddress.getEmail())){
        	   user.setEmail(officeAddress.getEmail());
           }else{}
           this.userRepository.saveAndFlush(user);
            
	        return new CommandProcessingResultBuilder().withCommandId(command.commandId())
				       .withEntityId(officeAdditionalInfo.getId()).withOfficeId(office.getId()).with(officeChanges).build();
			
		}  catch (final DataIntegrityViolationException e) {
			handleDataIntegrityIssues(command, e);
			return new CommandProcessingResult(Long.valueOf(-1l));
		} 
	}

}