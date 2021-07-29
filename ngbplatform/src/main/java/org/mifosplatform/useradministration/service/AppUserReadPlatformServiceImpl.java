/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.useradministration.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.organisation.salescataloge.data.SalesCatalogeData;
import org.mifosplatform.organisation.usercataloge.data.UserCatalogeData;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.useradministration.data.AppUserData;
import org.mifosplatform.useradministration.data.RoleData;
import org.mifosplatform.useradministration.domain.AppUser;
import org.mifosplatform.useradministration.domain.AppUserRepository;
import org.mifosplatform.useradministration.domain.Role;
import org.mifosplatform.useradministration.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class AppUserReadPlatformServiceImpl implements AppUserReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final RoleReadPlatformService roleReadPlatformService;
    private final AppUserRepository appUserRepository;
    
    private final PaginationHelper<AppUserData> paginationHelper = new PaginationHelper<AppUserData>();

    @Autowired
    public AppUserReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource,
            final OfficeReadPlatformService officeReadPlatformService, final RoleReadPlatformService roleReadPlatformService,
            final AppUserRepository appUserRepository) {
        this.context = context;
        this.officeReadPlatformService = officeReadPlatformService;
        this.roleReadPlatformService = roleReadPlatformService;
        this.appUserRepository = appUserRepository;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public PlatformSecurityContext getContext() {
        return this.context;
    }

    @Override
    @Cacheable(value = "users", key = "T(org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat(#root.target.context.authenticatedUser().getOffice().getHierarchy())")
    public Collection<AppUserData> retrieveAllUsers() {

        final AppUser currentUser = context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final AppUserMapper mapper = new AppUserMapper();
        final String sql = "select " + mapper.schema();

        return this.jdbcTemplate.query(sql, mapper, new Object[] { hierarchySearchString });
    }

    @Override
    @Cacheable(value = "users", key = "T(org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat(#root.target.context.authenticatedUser().getOffice().getHierarchy())")
    public Page<AppUserData> retrieveUsers(SearchSqlQuery searchUsers) {

        final AppUser currentUser = context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final AppUserMapper mapper = new AppUserMapper();
        
        StringBuilder sqlBuilder = new StringBuilder();
		
		sqlBuilder.append("SELECT ");
		sqlBuilder.append(mapper.schema());
        sqlBuilder.append(" and u.id IS NOT NULL ");
		String sqlSearch = searchUsers.getSqlSearch();
        String extraCriteria = null;
        
	    if (sqlSearch != null) {
	    	sqlSearch = sqlSearch.trim();
	    	extraCriteria = " and (u.id like '%"+sqlSearch+"%' OR" 
	    			+ " u.username like '%"+sqlSearch+"%' OR"
	    			+ " u.firstname like '%"+sqlSearch+"%' OR"
	    			+ " u.lastname like '%"+sqlSearch+"%' OR"
	    			+ " u.email like '%"+sqlSearch+"%' OR"
	    			+ " u.office_id like '%"+sqlSearch+"%' OR"
	    			+ " o.name like '%"+sqlSearch+"%' )";
	    }
	   
	    if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }
	    sqlBuilder.append(" order by u.username ");
	    
		if (searchUsers.isLimited()) {
			sqlBuilder.append(" limit ").append(searchUsers.getLimit());
	    }

	    if (searchUsers.isOffset()) {
	        sqlBuilder.append(" offset ").append(searchUsers.getOffset());
	    }
	    
        return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
	            new Object[] {hierarchySearchString}, mapper);
    }

    
    @Override
    public Collection<AppUserData> retrieveSearchTemplate() {
        final AppUser currentUser = context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final AppUserLookupMapper mapper = new AppUserLookupMapper();
        final String sql = "select " + mapper.schema();

        return this.jdbcTemplate.query(sql, mapper, new Object[] { hierarchySearchString });
    }

    @Override
    public AppUserData retrieveNewUserDetails() {

        final Collection<OfficeData> offices = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
        final Collection<RoleData> availableRoles = this.roleReadPlatformService.retrieveAll();

        return AppUserData.template(offices, availableRoles);
    }

    @Override
    public AppUserData retrieveUser(final Long userId) {

        final AppUser user = this.appUserRepository.findOne(userId);
        if (user == null || user.isDeleted()) { throw new UserNotFoundException(userId); }

        Collection<RoleData> availableRoles = this.roleReadPlatformService.retrieveAll();

        final Collection<RoleData> selectedUserRoles = new ArrayList<RoleData>();
        final Set<Role> userRoles = user.getRoles();
        for (final Role role : userRoles) {
            selectedUserRoles.add(role.toData());
        }

        availableRoles.removeAll(selectedUserRoles);

        return AppUserData.instance(user.getId(), user.getUsername(), user.getEmail(), user.getOffice().getId(),
                user.getOffice().getName(), user.getFirstname(), user.getLastname(), availableRoles, selectedUserRoles);
    }
    @Override
    public AppUserData retrieveUserByUsername(final String username) {
    	
        final AppUser user = this.appUserRepository.findByUsername(username);
        if (user == null || user.isDeleted()) { throw new UserNotFoundException(username); }

        Collection<RoleData> availableRoles = this.roleReadPlatformService.retrieveAll();

        final Collection<RoleData> selectedUserRoles = new ArrayList<RoleData>();
        final Set<Role> userRoles = user.getRoles();
        for (final Role role : userRoles) {
            selectedUserRoles.add(role.toData());
        }

        availableRoles.removeAll(selectedUserRoles);

        return AppUserData.instance(user.getId(), user.getUsername(), user.getEmail(), user.getOffice().getId(),
                user.getOffice().getName(), user.getFirstname(), user.getLastname(), availableRoles, selectedUserRoles);
    }
    @Override
    public AppUserData retrieveUserByEmail(final String email) {
    	
        final AppUser user = this.appUserRepository.findByEmail(email);
        if (user == null || user.isDeleted()) { throw new UserNotFoundException(email); }

        Collection<RoleData> availableRoles = this.roleReadPlatformService.retrieveAll();

        final Collection<RoleData> selectedUserRoles = new ArrayList<RoleData>();
        final Set<Role> userRoles = user.getRoles();
        for (final Role role : userRoles) {
            selectedUserRoles.add(role.toData());
        }

        availableRoles.removeAll(selectedUserRoles);

        return AppUserData.instance(user.getId(), user.getUsername(), user.getEmail(), user.getOffice().getId(),
                user.getOffice().getName(), user.getFirstname(), user.getLastname(), availableRoles, selectedUserRoles);
    }
    
    

    private static final class AppUserMapper implements RowMapper<AppUserData> {

        @Override
        public AppUserData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

            final Long id = resultSet.getLong("id");
            final String username = resultSet.getString("username");
            final String firstname = resultSet.getString("firstname");
            final String lastname = resultSet.getString("lastname");
            final String email = resultSet.getString("email");
            final Long officeId = JdbcSupport.getLong(resultSet, "officeId");
            final String officeName = resultSet.getString("officeName");

            return AppUserData.instance(id, username, email, officeId, officeName, firstname, lastname, null, null);
        }

        public String schema() {
            return " u.id as id, u.username as username, u.firstname as firstname, u.lastname as lastname, u.email as email,"
                    + " u.office_id as officeId, o.name as officeName from m_appuser u "
                    + " join m_office o on o.id = u.office_id where o.hierarchy like ? and u.is_deleted=0 ";


        }

    }

    private static final class AppUserLookupMapper implements RowMapper<AppUserData> {

        @Override
        public AppUserData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

            final Long id = resultSet.getLong("id");
            final String username = resultSet.getString("username");

            return AppUserData.dropdown(id, username);
        }

        public String schema() {
            return " u.id as id, u.username as username from m_appuser u "
                    + " join m_office o on o.id = u.office_id where o.hierarchy like ? and u.is_deleted=0 order by u.username";
        }
    }

	@Override
	public List<AppUserData> retrieveAppUserDataForDropdown() {
		try{
			AppUserDataForDropdown appuserMapper = new AppUserDataForDropdown();
			String sql = "SELECT "+appuserMapper.schema()+" WHERE ud.is_deleted = 'N'";
			return jdbcTemplate.query(sql, appuserMapper,new Object[]{});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}	
	
	}  
    private static final class AppUserDataForDropdown implements RowMapper<AppUserData>{
		
		public String  schema(){
			return "ud.id as id, ud.username as username from m_appuser ud";
			
		}
			
		@Override
		public AppUserData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final Long id= rs.getLong("id");
			final String username = rs.getString("username");
			return new AppUserData(id,username, username, id, username, username, username, null, null, null);
		}	
		
	}

	@Override
	public AppUserData retrieveUsers(Long userId) {
		
		this.context.authenticatedUser();
		AppUserMapper1 mapper = new AppUserMapper1();
		String sql = "SELECT "+mapper.schema()+"  where ud.id = ?";
		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {userId});
		
	}

	private static final class AppUserMapper1 implements RowMapper<AppUserData>{
		
		public String  schema(){
			return "ud.id as id, ud.username as username from m_appuser ud";
			
		}
			
		@Override
		public AppUserData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final Long id= rs.getLong("id");
			final String username = rs.getString("username");
			return new AppUserData(id,username, null, null, null, null, null, null, null, null);
		}	
		
	}
	
	
	
	
	
	

}