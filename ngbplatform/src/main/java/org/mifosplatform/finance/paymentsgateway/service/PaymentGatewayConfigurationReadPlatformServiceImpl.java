package org.mifosplatform.finance.paymentsgateway.service;

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

/**
 * 
 * @author ashokreddy
 *
 */
@Service
public class PaymentGatewayConfigurationReadPlatformServiceImpl implements PaymentGatewayConfigurationReadPlatformService{

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final RowMapper<ConfigurationPropertyData> rowMap;

    @Autowired
    public PaymentGatewayConfigurationReadPlatformServiceImpl(final PlatformSecurityContext context,
    		final TenantAwareRoutingDataSource dataSource) {
    	
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        rowMap = new GlobalConfigurationRowMapper();
    }

    @Override
    public ConfigurationData retrievePaymentGatewayConfiguration() {

        context.authenticatedUser();

        final String sql = "SELECT c.id as id, c.name, c.enabled, c.value,  c.description FROM c_paymentgateway_conf c order by c.id";

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
            final String description = rs.getString("description");

            return new ConfigurationPropertyData(id,name, enabled,value, null, description);
        }
    }

    @Transactional
    @Override
    public ConfigurationPropertyData retrievePaymentGatewayConfiguration(final Long configId) {

        this.context.authenticatedUser();
        
        final String sql = "SELECT c.id as id,c.id, c.name, c.enabled, c.value, c.description FROM c_paymentgateway_conf c where c.id=? order by c.id";
        final ConfigurationPropertyData globalConfiguration = this.jdbcTemplate.queryForObject(sql, this.rowMap , new Object[] {configId});

        return globalConfiguration;
    }


}
