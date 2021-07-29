/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.mifosplatform.useradministration.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.security.domain.PlatformUser;
import org.mifosplatform.infrastructure.security.exception.NoAuthorizationException;
import org.mifosplatform.infrastructure.security.service.PlatformPasswordEncoder;
import org.mifosplatform.infrastructure.security.service.RandomPasswordGenerator;
import org.mifosplatform.organisation.office.domain.Office;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Entity
@Table(name = "m_appuser", uniqueConstraints = @UniqueConstraint(columnNames = { "username" }, name = "username_org"))
public class AppUser extends AbstractPersistable<Long> implements PlatformUser {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static Logger LOGGER = LoggerFactory.getLogger(AppUser.class);

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "firstname", nullable = false, length = 100)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 100)
    private String lastname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nonexpired", nullable = false)
    private boolean accountNonExpired;

    @Column(name = "nonlocked", nullable = false)
    private final boolean accountNonLocked;

    @Column(name = "nonexpired_credentials", nullable = false)
    private final boolean credentialsNonExpired;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "firsttime_login_remaining", nullable = false)
    private boolean firstTimeLoginRemaining;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @ManyToOne
    @JoinColumn(name = "office_id")
    private Office office;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "m_appuser_role", joinColumns = @JoinColumn(name = "appuser_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
    
    
	
	  @Column(name = "secret_key") 
	  private String secretKey ;
	  
	  @Column(name = "secret_key_expiry_time")
	  private Date secretKeyExpiryTime;
	  
	  @Column(name = "secret_key_status") 
	  private Boolean secretKeyStatus;
	 
    

    public static AppUser fromJson(final Office userOffice, final Set<Role> allRoles, final JsonCommand command) {

        final String username = command.stringValueOfParameterNamed("username");
        String password = command.stringValueOfParameterNamed("password");
        final Boolean sendPasswordToEmail = command.booleanObjectValueOfParameterNamed("sendPasswordToEmail");
        if (sendPasswordToEmail.booleanValue()) {
            password = new RandomPasswordGenerator(13).generate();
        }

        final boolean userEnabled = true;
        final boolean userAccountNonExpired = true;
        final boolean userCredentialsNonExpired = true;
        final boolean userAccountNonLocked = true;

        final Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("DUMMY_ROLE_NOT_USED_OR_PERSISTED_TO_AVOID_EXCEPTION"));

        final User user = new User(username, password, userEnabled, userAccountNonExpired, userCredentialsNonExpired, userAccountNonLocked,
                authorities);

        final String email = command.stringValueOfParameterNamed("email");
        final String firstname = command.stringValueOfParameterNamed("firstname");
        final String lastname = command.stringValueOfParameterNamed("lastname");

        return new AppUser(userOffice, user, allRoles, email, firstname, lastname);
    }

    protected AppUser() {
        this.accountNonLocked = false;
        this.credentialsNonExpired = false;
        this.roles = new HashSet<Role>();
    }

    public AppUser(final Office office, final User user, final Set<Role> roles, final String email, final String firstname,
            final String lastname) {
        this.office = office;
        this.email = email.trim();
        this.username = user.getUsername().trim();
        this.firstname = firstname.trim();
        this.lastname = lastname.trim();
        this.password = user.getPassword().trim();
        this.accountNonExpired = user.isAccountNonExpired();
        this.accountNonLocked = user.isAccountNonLocked();
        this.credentialsNonExpired = user.isCredentialsNonExpired();
        this.enabled = user.isEnabled();
        this.roles = roles;
        this.firstTimeLoginRemaining = true;
    }

    public AppUser(String email, String username, String firstname, String lastname, String password,
			boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled,
			boolean firstTimeLoginRemaining, boolean deleted, Office office, Set<Role> roles, String secretKey,
			Date secretKeyExpiryTime, Boolean secretKeyStatus) {
		super();
		this.email = email;
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
		this.password = password;
		this.accountNonExpired = accountNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.credentialsNonExpired = credentialsNonExpired;
		this.enabled = enabled;
		this.firstTimeLoginRemaining = firstTimeLoginRemaining;
		this.deleted = deleted;
		this.office = office;
		this.roles = roles;
		this.secretKey = secretKey;
		this.secretKeyExpiryTime = secretKeyExpiryTime;
		this.secretKeyStatus = secretKeyStatus;
	}

	public void updatePassword(final String encodePassword) {
        this.password = encodePassword;
        this.firstTimeLoginRemaining = false;
    }

    public void changeOffice(final Office differentOffice) {
        this.office = differentOffice;
    }

    public void updateRoles(final Set<Role> allRoles) {
        if (!allRoles.isEmpty()) {
            this.roles.clear();
            this.roles = allRoles;
        }
    }

    public Map<String, Object> update(final JsonCommand command, final PlatformPasswordEncoder platformPasswordEncoder) {

        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(7);

        // unencoded password provided
        final String passwordParamName = "password";
        final String passwordEncodedParamName = "passwordEncoded";
        if (command.hasParameter(passwordParamName)) {
            if (command.isChangeInPasswordParameterNamed(passwordParamName, this.password, platformPasswordEncoder, this.getId())) {
                final String passwordEncodedValue = command.passwordValueOfParameterNamed(passwordParamName, platformPasswordEncoder,
                        this.getId());
                actualChanges.put(passwordEncodedParamName, passwordEncodedValue);
                updatePassword(passwordEncodedValue);
            }
        }

        if (command.hasParameter(passwordEncodedParamName)) {
            if (command.isChangeInStringParameterNamed(passwordEncodedParamName, this.password)) {
                final String newValue = command.stringValueOfParameterNamed(passwordEncodedParamName);
                actualChanges.put(passwordEncodedParamName, newValue);
                updatePassword(newValue);
            }
        }

        final String officeIdParamName = "officeId";
        if (command.isChangeInLongParameterNamed(officeIdParamName, this.office.getId())) {
            final Long newValue = command.longValueOfParameterNamed(officeIdParamName);
            actualChanges.put(officeIdParamName, newValue);
        }

        final String rolesParamName = "roles";
        if (command.isChangeInArrayParameterNamed(rolesParamName, getRolesAsIdStringArray())) {
            final String[] newValue = command.arrayValueOfParameterNamed(rolesParamName);
            actualChanges.put(rolesParamName, newValue);
        }

        final String usernameParamName = "username";
        if (command.isChangeInStringParameterNamed(usernameParamName, this.username)) {
            final String newValue = command.stringValueOfParameterNamed(usernameParamName);
            actualChanges.put(usernameParamName, newValue);
            this.username = newValue;
        }

        final String firstnameParamName = "firstname";
        if (command.isChangeInStringParameterNamed(firstnameParamName, this.firstname)) {
            final String newValue = command.stringValueOfParameterNamed(firstnameParamName);
            actualChanges.put(firstnameParamName, newValue);
            this.firstname = newValue;
        }

        final String lastnameParamName = "lastname";
        if (command.isChangeInStringParameterNamed(lastnameParamName, this.lastname)) {
            final String newValue = command.stringValueOfParameterNamed(lastnameParamName);
            actualChanges.put(lastnameParamName, newValue);
            this.lastname = newValue;
        }

        final String emailParamName = "email";
        if (command.isChangeInStringParameterNamed(emailParamName, this.email)) {
            final String newValue = command.stringValueOfParameterNamed(emailParamName);
            actualChanges.put(emailParamName, newValue);
            this.email = newValue;
        }

        return actualChanges;
    }

    private String[] getRolesAsIdStringArray() {
        final List<String> roleIds = new ArrayList<String>();

        for (final Role role : this.roles) {
            roleIds.add(role.getId().toString());
        }

        return roleIds.toArray(new String[roleIds.size()]);
    }

    /**
     * Delete is a <i>soft delete</i>. Updates flag so it wont appear in
     * query/report results.
     * 
     * Any fields with unique constraints and prepended with id of record.
     */
    public void delete() {
        this.deleted = true;
        this.enabled = false;
        this.accountNonExpired = false;
        this.firstTimeLoginRemaining = true;
        this.username = this.getId() + "_DELETED_" + this.username;
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.populateGrantedAuthorities();
    }

    private List<GrantedAuthority> populateGrantedAuthorities() {
        final List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        for (final Role role : this.roles) {
            final Collection<Permission> permissions = role.getPermissions();
            for (final Permission permission : permissions) {
                grantedAuthorities.add(new SimpleGrantedAuthority(permission.getCode()));
            }
        }
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return this.email;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public Office getOffice() {
        return this.office;
    }
    
    public void setEmail(String email) {
		this.email = email;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean canNotApproveLoanInPast() {
        return hasNotPermissionForAnyOf("ALL_FUNCTIONS", "APPROVEINPAST_LOAN");
    }

    public boolean canNotRejectLoanInPast() {
        return hasNotPermissionForAnyOf("ALL_FUNCTIONS", "REJECTINPAST_LOAN");
    }

    public boolean canNotWithdrawByClientLoanInPast() {
        return hasNotPermissionForAnyOf("ALL_FUNCTIONS", "WITHDRAWINPAST_LOAN");
    }

    public boolean canNotDisburseLoanInPast() {
        return hasNotPermissionForAnyOf("ALL_FUNCTIONS", "DISBURSEINPAST_LOAN");
    }

    public boolean canNotMakeRepaymentOnLoanInPast() {
        return hasNotPermissionForAnyOf("ALL_FUNCTIONS", "REPAYMENTINPAST_LOAN");
    }

    public boolean hasNotPermissionForReport(final String reportName) {

        if (hasNotPermissionForAnyOf("ALL_FUNCTIONS", "ALL_FUNCTIONS_READ", "REPORTING_SUPER_USER", "READ_" + reportName)) return true;

        return false;
    }

    public boolean hasNotPermissionForDatatable(final String datatable, final String accessType) {

        final String matchPermission = accessType + "_" + datatable;

        if ("READ".equalsIgnoreCase(accessType)) {

            if (hasNotPermissionForAnyOf("ALL_FUNCTIONS", "ALL_FUNCTIONS_READ", matchPermission)) return true;

            return false;
        }

        if (hasNotPermissionForAnyOf("ALL_FUNCTIONS", matchPermission)) return true;

        return false;
    }

    public boolean hasNotPermissionForAnyOf(final String... permissionCodes) {
        boolean hasNotPermission = true;
        for (final String permissionCode : permissionCodes) {
            final boolean checkPermission = this.hasPermissionTo(permissionCode);
            if (checkPermission) {
                hasNotPermission = false;
                break;
            }
        }
        return hasNotPermission;
    }

    public boolean hasPermissionForAnyOf(final String... permissionCodes) {
        boolean hasNotPermission = false;
        for (final String permissionCode : permissionCodes) {
            final boolean checkPermission = this.hasPermissionTo(permissionCode);
            if (checkPermission) {
                hasNotPermission = true;
                break;
            }
        }
        return hasNotPermission;
    }
    
    public void validateHasReadPermission(final String resourceType) {

        final String authorizationMessage = "User has no authority to view " + resourceType.toLowerCase() + "s";
        final String matchPermission = "READ_" + resourceType.toUpperCase();

        if (!(hasNotPermissionForAnyOf("ALL_FUNCTIONS", "ALL_FUNCTIONS_READ", matchPermission))) return;

        throw new NoAuthorizationException(authorizationMessage);
    }

    private boolean hasNotPermissionTo(final String permissionCode) {
        return !hasPermissionTo(permissionCode);
    }

    private boolean hasPermissionTo(final String permissionCode) {
        boolean hasPermission = hasAllFunctionsPermission();
        if (!hasPermission) {
            for (final Role role : this.roles) {
                if (role.hasPermissionTo(permissionCode)) {
                    hasPermission = true;
                    break;
                }
            }
        }
        return hasPermission;
    }

    private boolean hasAllFunctionsPermission() {
        boolean match = false;
        for (final Role role : this.roles) {
            if (role.hasPermissionTo("ALL_FUNCTIONS")) {
                match = true;
                break;
            }
        }
        return match;
    }

    public boolean hasIdOf(final Long userId) {
        return getId().equals(userId);
    }

    private boolean hasNotAnyPermission(final List<String> permissions) {
        return !hasAnyPermission(permissions);
    }

    private boolean hasAnyPermission(final List<String> permissions) {
        boolean hasAtLeastOneOf = false;

        for (final String permissionCode : permissions) {
            if (hasPermissionTo(permissionCode)) {
                hasAtLeastOneOf = true;
                break;
            }
        }

        return hasAtLeastOneOf;
    }

    public void validateHasPermissionTo(final String function, final List<String> allowedPermissions) {
        if (hasNotAnyPermission(allowedPermissions)) {
            final String authorizationMessage = "User has no authority to: " + function;
            throw new NoAuthorizationException(authorizationMessage);
        }
    }

    public void validateHasPermissionTo(final String function) {
        if (hasNotPermissionTo(function)) {
            final String authorizationMessage = "User has no authority to: " + function;
            LOGGER.info("Unauthorized access: userId: " + this.getId() + " action: " + function + " allowed: " + getAuthorities());
            throw new NoAuthorizationException(authorizationMessage);
        }
    }

    public void validateHasCheckerPermissionTo(final String function) {

        final String checkerPermissionName = function.toUpperCase() + "_CHECKER";
        if (hasNotPermissionTo("CHECKER_SUPER_USER") && hasNotPermissionTo(checkerPermissionName)) {
            final String authorizationMessage = "User has no authority to be a checker for: " + function;
            throw new NoAuthorizationException(authorizationMessage);
        }
    }

    public void validateHasDatatableReadPermission(final String datatable) {
        if (hasNotPermissionForDatatable(datatable, "READ")) { throw new NoAuthorizationException("Not authorised to read datatable: "
                + datatable); }
    }

	public boolean isSuperUser() {
		  return hasAllFunctionsPermission();
	}

	
	  public String getSecretKey() { return secretKey; }
	  
	  public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
	  
	  public Date getSecretKeyExpiryTime() { return secretKeyExpiryTime; }
	  
	  public void setSecretKeyExpiryTime(Date secretKeyExpiryTime) {
	  this.secretKeyExpiryTime = secretKeyExpiryTime; }
	  
	  public Boolean getSecretKeyStatus() { return secretKeyStatus; }
	  
	  public void setSecretKeyStatus(Boolean secretKeyStatus) {
	  this.secretKeyStatus = secretKeyStatus; }
	 
	public static AppUser fromPartner(final Office office, Set<Role> allRoles,
			final String partnerName, final String loginName, final String password,final String email) {
	
        final boolean userEnabled = true;
        final boolean userAccountNonExpired = true;
        final boolean userCredentialsNonExpired = true;
        final boolean userAccountNonLocked = true;

        final Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("DUMMY_ROLE_NOT_USED_OR_PERSISTED_TO_AVOID_EXCEPTION"));

        final User user = new User(loginName, password, userEnabled, userAccountNonExpired, userCredentialsNonExpired, userAccountNonLocked,
                authorities);
        
        return new AppUser(office, user, allRoles, email, partnerName, partnerName);
	}
    
}