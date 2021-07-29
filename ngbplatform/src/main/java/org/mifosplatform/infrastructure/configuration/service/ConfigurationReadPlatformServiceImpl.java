/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.configuration.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.configuration.data.ConfigurationData;
import org.mifosplatform.infrastructure.configuration.data.ConfigurationPropertyData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigurationReadPlatformServiceImpl implements ConfigurationReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final RowMapper<ConfigurationPropertyData> rowMap;

    @Autowired
    public ConfigurationReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        rowMap = new GlobalConfigurationRowMapper();
    }

    @Override
    public ConfigurationData retrieveGlobalConfiguration() {

        context.authenticatedUser();
        final String sql = "SELECT c.id as id, c.name, c.enabled, c.value, c.module, c.description FROM c_configuration c order by c.id";
        final List<ConfigurationPropertyData> globalConfiguration = this.jdbcTemplate.query(sql, rowMap , new Object[] {});

        return new ConfigurationData(globalConfiguration);
    }

    private static final class GlobalConfigurationRowMapper implements RowMapper<ConfigurationPropertyData> {

        @Override
        public ConfigurationPropertyData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

            final String name = rs.getString("name");
            final boolean enabled = rs.getBoolean("enabled");
            final String value = rs.getString("value");
            final Long id = rs.getLong("id");
            final String module=rs.getString("module");
            final String description=rs.getString("description");

            return new ConfigurationPropertyData(id,name, enabled,value,module,description);
        }
    }

    @Transactional
    @Override
    public ConfigurationPropertyData retrieveGlobalConfiguration(final Long configId) {

        this.context.authenticatedUser();
        
        final String sql = "SELECT c.id as id,c.id, c.name, c.enabled, c.value, c.module, c.description FROM c_configuration c where c.id=? order by c.id";
        final ConfigurationPropertyData globalConfiguration = this.jdbcTemplate.queryForObject(sql, this.rowMap , new Object[] {configId});

        return globalConfiguration;
    }
    
    @Override
    public ConfigurationPropertyData retrieveGlobalConfigurationByName(final String configName) {
    	
    	this.context.authenticatedUser();
    	
    	final String sql = "SELECT c.id ,c.name, c.enabled, c.value, c.module, c.description FROM c_configuration c where c.name=? order by c.id";
    	final ConfigurationPropertyData globalConfiguration = this.jdbcTemplate.queryForObject(sql, this.rowMap , new Object[] {configName});
    	
    	return globalConfiguration;
    }

}