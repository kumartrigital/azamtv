/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.useradministration.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.useradministration.data.AppUserData;

public interface AppUserReadPlatformService {

    Collection<AppUserData> retrieveAllUsers();
    
    Page<AppUserData> retrieveUsers(SearchSqlQuery searchUsers);

    Collection<AppUserData> retrieveSearchTemplate();

    AppUserData retrieveNewUserDetails();

    AppUserData retrieveUser(Long userId);

	List<AppUserData> retrieveAppUserDataForDropdown();

	AppUserData retrieveUsers(Long userId);
	
	AppUserData retrieveUserByUsername(String username);
	
	AppUserData retrieveUserByEmail( String email);
}