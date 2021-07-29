/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.useradministration.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.message.service.MessageGmailBackedPlatformEmailService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.useradministration.data.AppUserData;
import org.mifosplatform.useradministration.domain.AppUser;
import org.mifosplatform.useradministration.domain.AppUserRepository;
import org.mifosplatform.useradministration.exception.UserNotFoundException;
import org.mifosplatform.useradministration.service.AppUserReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Path("/users")
@Component
@Scope("singleton")
public class UsersApiResource {

    /**
     * The set of parameters that are supported in response for
     * {@link AppUserData}.
     */
    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "officeId", "officeName", "username",
            "firstname", "lastname", "email", "allowedOffices", "availableRoles", "selectedRoles"));


    private static final String RESOURCENAMEFORPERMISSIONS = "USER";
    private final PlatformSecurityContext context;
    private final AppUserReadPlatformService readPlatformService;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final DefaultToApiJsonSerializer<AppUserData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final AppUserRepository appUserRepository;

    @Autowired
    public UsersApiResource(final PlatformSecurityContext context, final AppUserReadPlatformService readPlatformService,
            final OfficeReadPlatformService officeReadPlatformService, final DefaultToApiJsonSerializer<AppUserData> toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService
            ,final AppUserRepository appUserRepository,
            final MessageGmailBackedPlatformEmailService messageGmailBackedPlatformEmailService) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.officeReadPlatformService = officeReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.appUserRepository = appUserRepository;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveUsers(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch, @QueryParam("limit") final Integer limit,
	         @QueryParam("offset") final Integer offset) {

        context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
        final SearchSqlQuery searchUsers =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
        final Page<AppUserData> users = this.readPlatformService.retrieveUsers(searchUsers);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(users);
    }

    @GET
    @Path("{userId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveUser(@PathParam("userId") final Long userId, @Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        AppUserData user = this.readPlatformService.retrieveUser(userId);
        if (settings.isTemplate()) {
            final Collection<OfficeData> offices = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
            user = AppUserData.template(user, offices);
        }

        return this.toApiJsonSerializer.serialize(settings, user, RESPONSE_DATA_PARAMETERS);
    }
    @GET
    @Path("verifyusername/{username}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveUserByUsername(@PathParam("username") final String username, @Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        AppUserData user = this.readPlatformService.retrieveUserByUsername(username);
        if (settings.isTemplate()) {
            final Collection<OfficeData> offices = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
            user = AppUserData.template(user, offices);
        }
        return this.toApiJsonSerializer.serialize(settings, user, RESPONSE_DATA_PARAMETERS);
    }
    @GET
    @Path("verifyemail/{email}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveUserByEmail(@PathParam("email") final String email, @Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        AppUserData user = this.readPlatformService.retrieveUserByEmail(email);
        if (settings.isTemplate()) {
            final Collection<OfficeData> offices = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
            user = AppUserData.template(user, offices);
        }
        return this.toApiJsonSerializer.serialize(settings, user, RESPONSE_DATA_PARAMETERS);
    }
    
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String newUserTemplateDetails(@Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);

        final AppUserData user = this.readPlatformService.retrieveNewUserDetails();

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, user, RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createUser(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createUser() //
                .withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{userId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateUser(@PathParam("userId") final Long userId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateUser(userId) //
                .withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{userId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteUser(@PathParam("userId") final Long userId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .deleteUser(userId) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
    
    @PUT
    @Path("generatekey/{email}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String generateSecretKey(@PathParam("email") final String email) {
		AppUser user = this.appUserRepository.findByEmail(email);
		
		CommandWrapper commandRequest= null;
		if(Optional.ofNullable(user).isPresent()) {
		   commandRequest = new CommandWrapperBuilder() .generateKey(user.getId()) .build();
		}else {
			throw new PlatformDataIntegrityException("error.email.not.found.exception", "email not found exception", "email not found exception ");
		}
			
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

		return this.toApiJsonSerializer.serialize(result);
	}
    @PUT
    @Path("validate/{userid}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
	public String validateKey(@PathParam("userid") final Long userId,final String apiRequestBodyAsJson) {
    	
    	
		AppUser user = this.appUserRepository.findOne(userId);
		CommandWrapper commandRequest= null;
		if(Optional.ofNullable(user).isPresent()) {
		   commandRequest = new CommandWrapperBuilder() //
	                .validateKey(userId) .withJson(apiRequestBodyAsJson)
	                //
	                .build();
		}else {
			throw new UserNotFoundException(userId);
		}
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

		final CommandWrapper commandRequest1 = new CommandWrapperBuilder() //
                .updateUser(userId) //
                .withJson(apiRequestBodyAsJson) //
                .build();
		
        final CommandProcessingResult result1 = this.commandsSourceWritePlatformService.logCommandSource(commandRequest1);
        
		return this.toApiJsonSerializer.serialize(result1);
	}
    
    
    
}