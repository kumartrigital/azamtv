package org.mifosplatform.organisation.smartsearch.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.accounting.journalentry.api.DateParam;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.smartsearch.data.AdvanceSearchData;
import org.mifosplatform.organisation.smartsearch.service.AdvanceSearchReadPlafformService;
import org.mifosplatform.portfolio.group.service.SearchParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;


@Path("/advancesearch")
@Component
@Scope("singleton")
public class AdvanceSearchApiResource {
	
	private final String RESOURCENAME_FOR_PERMISSION="ADVANCESEARCH";
	private final AdvanceSearchReadPlafformService  advanceSearchReadPlafformService; 
	private final DefaultToApiJsonSerializer<AdvanceSearchData> apiJsonSerializer;
	private final PlatformSecurityContext securityContext;
	
	@Autowired
	public AdvanceSearchApiResource(final AdvanceSearchReadPlafformService advanceSearchReadPlafformService,
			final DefaultToApiJsonSerializer<AdvanceSearchData> apiJsonSerializer,final PlatformSecurityContext context){
		
		this.advanceSearchReadPlafformService=advanceSearchReadPlafformService;
		this.apiJsonSerializer=apiJsonSerializer;
		this.securityContext=context;
		
	}

	 @GET
	 @Consumes({ MediaType.APPLICATION_JSON })
	 @Produces({ MediaType.APPLICATION_JSON })
	 public String retrieveSearchData(@Context final UriInfo uriInfo, @QueryParam("searchText") final String searchText,
	            @QueryParam("fromDate") final DateParam fromDateParam, @QueryParam("toDate") final DateParam toDateParam,
	            @QueryParam("assignedTo") final Long assignedTo, @QueryParam("closedBy") final Long closedBy,
	            @QueryParam("category") final String category,@QueryParam("status") final String status,
	            @QueryParam("limit") final Integer limit,@QueryParam("offset") final Integer offset,
	            @QueryParam("name") final String name, @QueryParam("createdBy") final Long createdBy,
	            @QueryParam("emailId") final String emailId, @QueryParam("source") final String source,
	            @QueryParam("phone") final String phone, @QueryParam("searchType") final String searchType,
	            @QueryParam("city") final String city,@QueryParam("address") final String address,
	            @QueryParam("externalId") final String externalId) {
		 
		 this.securityContext.authenticatedUser().validateHasReadPermission(RESOURCENAME_FOR_PERMISSION);
		 final SearchParameters searchParameters = SearchParameters.forTickets(searchText,fromDateParam,toDateParam,assignedTo,closedBy,category,status,
				 limit,offset,name,createdBy,emailId,source,phone,searchType,city,address,externalId);
	     Page<AdvanceSearchData>  searchDatas  =this.advanceSearchReadPlafformService.retrieveAllSearchData(searchParameters);
		return this.apiJsonSerializer.serialize(searchDatas);
	    }
}
