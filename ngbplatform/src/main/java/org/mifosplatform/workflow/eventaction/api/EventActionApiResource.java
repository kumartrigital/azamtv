/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.workflow.eventaction.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.EventActionData;
import org.mifosplatform.workflow.eventaction.service.EventActionReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/eventaction")
@Component
@Scope("singleton")
public class EventActionApiResource {

    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("eventaction", "entityName", "actionName", "json",
            "resourceId", "clientId"));
    private final String resourceNameForPermissions = "EVENTACTIONS";

    private final PlatformSecurityContext context;
    private final EventActionReadPlatformService eventActionReadPlatformService;
    private final DefaultToApiJsonSerializer<EventActionData> toApiJsonSerializer;

    @Autowired
    public EventActionApiResource(final PlatformSecurityContext context, final EventActionReadPlatformService eventActionReadPlatformService,
            final DefaultToApiJsonSerializer<EventActionData> toApiJsonSerializer) {
        this.context = context;
        this.eventActionReadPlatformService = eventActionReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllEventActions(@Context final UriInfo uriInfo,@QueryParam("sqlSearch") final String sqlSearch,
			@QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset,
			@QueryParam("statusType") final String statusType) {

        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
        final SearchSqlQuery searchTicketMaster = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
        final Page<EventActionData> data = this.eventActionReadPlatformService.retriveAllEventActions(searchTicketMaster,statusType);
        
        return this.toApiJsonSerializer.serialize(data);
    }

}