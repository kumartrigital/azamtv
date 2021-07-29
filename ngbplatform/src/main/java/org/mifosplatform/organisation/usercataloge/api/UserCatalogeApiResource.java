package org.mifosplatform.organisation.usercataloge.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.salescataloge.data.SalesCatalogeData;
import org.mifosplatform.organisation.salescataloge.service.SalesCatalogeReadPlatformService;
import org.mifosplatform.organisation.usercataloge.data.UserCatalogeData;
import org.mifosplatform.organisation.usercataloge.service.UserCatalogeReadPlatformService;
import org.mifosplatform.useradministration.data.AppUserData;
import org.mifosplatform.useradministration.service.AppUserReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/usercataloge")
@Component
@Scope("singleton")
public class UserCatalogeApiResource {
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));

	private final static  String RESOURCE_TYPE = "USERCATALOGE";
	
	private final ToApiJsonSerializer apiJsonSerializer;
	private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	private final PlatformSecurityContext context;
	private final UserCatalogeReadPlatformService userCatalogeReadPlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final SalesCatalogeReadPlatformService salesCatalogeReadPlatformService;
	private final AppUserReadPlatformService appUserReadPlatformService;
	
	@Autowired
	public UserCatalogeApiResource(ToApiJsonSerializer apiJsonSerializer,
		   PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
		   final PlatformSecurityContext context,final UserCatalogeReadPlatformService userCatalogeReadPlatformService,
		   final ApiRequestParameterHelper apiRequestParameterHelper,final SalesCatalogeReadPlatformService 
		   salesCatalogeReadPlatformService,final AppUserReadPlatformService appUserReadPlatformService) {
		this.apiJsonSerializer = apiJsonSerializer;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
	    this.context = context;
	    this.userCatalogeReadPlatformService = userCatalogeReadPlatformService;
	    this.apiRequestParameterHelper = apiRequestParameterHelper;
	    this.salesCatalogeReadPlatformService = salesCatalogeReadPlatformService;
	    this.appUserReadPlatformService = appUserReadPlatformService;
	}

	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String UserCatalogeDetails(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final SearchSqlQuery searchUserCataloge = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<UserCatalogeData> usercatalogeDatas = userCatalogeReadPlatformService.retrieveUserCataloge(searchUserCataloge);
		return apiJsonSerializer.serialize(usercatalogeDatas);
	}

	@GET
	@Path("{userId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveUserCatalogeDetails(@Context final UriInfo uriInfo ,@PathParam("userId") final Long userId,final String username){
		/*this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		UserCatalogeData usercatalogeData = this.userCatalogeReadPlatformService.retrieveUserCataloge(userId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		if(settings.isTemplate()){
			usercatalogeData = this.handleTemplateData(usercatalogeData);
		}
		return this.apiJsonSerializer.serialize(settings,usercatalogeData,RESPONSE_DATA_PARAMETERS);
		*/
		
        this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
        AppUserData appUserData = this.appUserReadPlatformService.retrieveUsers(userId);
        UserCatalogeData usercatalogeData1 = new UserCatalogeData();//this.handleTemplateData(userId);
        usercatalogeData1.setUserId(userId);
        usercatalogeData1.setUsername(appUserData.username());
        final UserCatalogeData usercatalogeData = this.handleTemplateData(usercatalogeData1);
				
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, usercatalogeData,RESPONSE_DATA_PARAMETERS);
		
	}
	
	/*@GET
	@Path("user/{userId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveUserCatalogeDetailsOfUser(@Context final UriInfo uriInfo ,@PathParam("userId") final Long userId){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		List<UserCatalogeData> usercatalogeDatas = this.userCatalogeReadPlatformService.retrieveUserCatalogeOfUser(userId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		
		return this.apiJsonSerializer.serialize(settings,usercatalogeDatas,RESPONSE_DATA_PARAMETERS);
		
	}*/
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createUserCataloge(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createUserCataloge().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
			
	}
	
	@PUT
	@Path("{userId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateUserCataloge(@PathParam("userId") final Long userId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateUserCataloge(userId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@DELETE
	@Path("{usercatalogeId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteUserCataloge(@PathParam("usercatalogeId") final Long usercatalogeId) {

		final CommandWrapper command = new CommandWrapperBuilder().deleteUserCataloge(usercatalogeId).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveTemplateData(@Context final UriInfo uriInfo){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		
		final UserCatalogeData usercatalogeData = this.handleTemplateData(null);
				
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, usercatalogeData,RESPONSE_DATA_PARAMETERS);
		
	}

	/*private UserCatalogeData handleTemplateData(UserCatalogeData usercatalogeData) {
		if(usercatalogeData == null){
			usercatalogeData = new UserCatalogeData();
		}
		usercatalogeData.setSalesCatalogeDatas(this.salesCatalogeReadPlatformService.retrieveSalesCatalogesForDropdown());
		usercatalogeData.setAppUserDatas(this.appUserReadPlatformService.retrieveAppUserDataForDropdown());
		return usercatalogeData;
	}*/
	
	private UserCatalogeData handleTemplateData(UserCatalogeData usercatalogeData)
	{
		if(usercatalogeData == null){
			usercatalogeData = new UserCatalogeData();
	    }
		usercatalogeData.setAppUserDatas(this.appUserReadPlatformService.retrieveAppUserDataForDropdown());
		final List<SalesCatalogeData> availableSalescatalog = this.salesCatalogeReadPlatformService.retrieveSalesCatalogesForDropdown();
		List<SalesCatalogeData> selectedSalesCataloges = new ArrayList<>();
		//Long userId=this.context.authenticatedUser().getId();
		if(availableSalescatalog != null){
			selectedSalesCataloges = this.userCatalogeReadPlatformService.retrieveSelectedSalesCataloges(usercatalogeData.getUserId());
			//selectedSalesCataloges = this.userCatalogeReadPlatformService.retrieveSelectedSalesCatalogeForPlanCategory(userId);

			int size = availableSalescatalog.size();
			final int selectedsize = selectedSalesCataloges.size();
				for (int i = 0; i < selectedsize; i++)
	     			{
					final Long selected = selectedSalesCataloges.get(i).getId();
					for (int j = 0; j < size; j++) {
						final Long avialble = availableSalescatalog.get(j).getId();
						if (selected.equals(avialble)) {
							availableSalescatalog.remove(j);
							size--;
						}
					}
				}
	     }
		 usercatalogeData.setSelectedSalesCatalogeDatas(selectedSalesCataloges);
		 usercatalogeData.setAvailaableSalesCatalogeDatas(availableSalescatalog);
		
		return usercatalogeData;
	}

	@GET
	@Path("salesPlanCategory")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveUserCatalogeDetail(@Context final UriInfo uriInfo,@QueryParam("category") final String category){
		
        this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
        Long userId = this.context.authenticatedUser().getId(); 
        final List<SalesCatalogeData> allPlanDatas = userCatalogeReadPlatformService.retrievePlans(userId,category);
        SalesCatalogeData salescatalogedata = new SalesCatalogeData(allPlanDatas);
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, salescatalogedata,RESPONSE_DATA_PARAMETERS);
		
	}
	
	

}
