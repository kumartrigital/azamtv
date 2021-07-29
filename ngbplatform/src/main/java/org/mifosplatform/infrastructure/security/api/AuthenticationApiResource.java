/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.security.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifosplatform.billing.loginhistory.domain.LoginHistory;
import org.mifosplatform.billing.loginhistory.domain.LoginHistoryRepository;
import org.mifosplatform.crm.userchat.service.UserChatReadplatformReadService;
import org.mifosplatform.infrastructure.configuration.data.LicenseData;
import org.mifosplatform.infrastructure.configuration.service.LicenseUpdateService;
import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.security.data.AuthenticatedUserData;
import org.mifosplatform.useradministration.data.RoleData;
import org.mifosplatform.useradministration.domain.AppUser;
import org.mifosplatform.useradministration.service.RoleReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.sun.jersey.core.util.Base64;

@Path("/authentication")
@Component
@Scope("singleton")
public class AuthenticationApiResource {

	private final DaoAuthenticationProvider customAuthenticationProvider;
	private final ToApiJsonSerializer<AuthenticatedUserData> apiJsonSerializerService;
	private final RoleReadPlatformService roleReadPlatformService;
	private final LicenseUpdateService licenseUpdateService;
	private final UserChatReadplatformReadService userChatReadplatformReadService;
	private final LoginHistoryRepository loginHistoryRepository;

	@Autowired
	public AuthenticationApiResource(
			@Qualifier("customAuthenticationProvider") final DaoAuthenticationProvider customAuthenticationProvider,
			final ToApiJsonSerializer<AuthenticatedUserData> apiJsonSerializerService,
			final RoleReadPlatformService roleReadPlatformService,
			final UserChatReadplatformReadService userChatReadplatformReadService,
			final LoginHistoryRepository loginHistoryRepository, final LicenseUpdateService licenseUpdateService) {

		this.customAuthenticationProvider = customAuthenticationProvider;
		this.apiJsonSerializerService = apiJsonSerializerService;
		this.roleReadPlatformService = roleReadPlatformService;
		this.licenseUpdateService = licenseUpdateService;
		this.userChatReadplatformReadService = userChatReadplatformReadService;
		this.loginHistoryRepository = loginHistoryRepository;
	}

	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public String authenticate(@QueryParam("username") final String username,
			@QueryParam("password") final String password, @Context HttpServletRequest req) {

		Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
		Authentication authenticationCheck = customAuthenticationProvider.authenticate(authentication);
		MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
		System.out.println(tenant.getLicensekey());
		String notificationMessage = null;
		LicenseData licenseData = this.licenseUpdateService.getLicenseDetails(tenant.getLicensekey());
		int days = Days.daysBetween(DateUtils.getLocalDateOfTenant(), new LocalDate(licenseData.getKeyDate()))
				.getDays();

		if (days < 7) {
			notificationMessage = "License will be exipired on "
					+ new SimpleDateFormat("dd-MMM-yyyy").format(licenseData.getKeyDate()) + ". Please Update";
		}
		Collection<String> permissions = new ArrayList<String>();
		AuthenticatedUserData authenticatedUserData = new AuthenticatedUserData(username, permissions);

		if (authenticationCheck.isAuthenticated()) {

			String ipAddress = req.getRemoteHost(); /** Returns IpAddress of user */
			String session = req.getSession().getId(); /** creates session and returns sessionId */
			int maxTime = req.getSession().getMaxInactiveInterval();
			/**
			 * Condition to Login History Calls When Session is New One
			 * 
			 * @author rakesh
			 */
			if (req.getSession().isNew() && !username.equalsIgnoreCase("selfcare")) {
				LoginHistory loginHistory = new LoginHistory(ipAddress, null, session, DateUtils.getDateOfTenant(),
						null, username, "ACTIVE");
				this.loginHistoryRepository.save(loginHistory);
				Long loginHistoryId = loginHistory.getId();
				req.getSession().setAttribute("lId", loginHistoryId);
			}

			Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(
					authenticationCheck.getAuthorities());
			for (GrantedAuthority grantedAuthority : authorities) {
				permissions.add(grantedAuthority.getAuthority());
			}
			AppUser principal = (AppUser) authenticationCheck.getPrincipal();
			byte[] base64EncodedAuthenticationKey = Base64.encode(username + ":" + password);

			Collection<RoleData> roles = this.roleReadPlatformService.retrieveAll();
			Long unreadMessages = this.userChatReadplatformReadService.getUnreadMessages(username);

			authenticatedUserData = new AuthenticatedUserData(username, roles, permissions, principal.getId(),
					principal.getOffice().getId(), new String(base64EncodedAuthenticationKey), unreadMessages,
					ipAddress, session, maxTime, (Long) req.getSession().getAttribute("lId"), principal.getRoles(),
					notificationMessage);
		}

		return this.apiJsonSerializerService.serialize(authenticatedUserData);
	}
}