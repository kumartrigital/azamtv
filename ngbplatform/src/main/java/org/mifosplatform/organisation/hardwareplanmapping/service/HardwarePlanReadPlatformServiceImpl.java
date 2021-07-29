package org.mifosplatform.organisation.hardwareplanmapping.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.hardwareplanmapping.data.HardwareMappingDetailsData;
import org.mifosplatform.organisation.hardwareplanmapping.data.HardwarePlanData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class HardwarePlanReadPlatformServiceImpl implements HardwarePlanReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;

	@Autowired
	public HardwarePlanReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<HardwarePlanData> retrieveAllHardwareMappings(Long itemClass) {

		final Mapper mapper = new Mapper();
		String sql = "select " + mapper.schema();
		if(itemClass != null){
			sql = sql + " where h.item_class = '"+itemClass+"' ";
		}
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	@Override
	public HardwarePlanData retrieveSingleHardwareMapping(final Long id) {
		
		final Mapper mapper = new Mapper();
		final String sql ="select "+ mapper.schema()+ " where h.id = "+id;
		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {});
	}


	private static final class Mapper implements RowMapper<HardwarePlanData> {

		public String schema() {
			return "h.id as id,h.item_class as itemClass," +
					"(select e.enum_value from r_enum_value e where e.enum_id =h.item_class and e.enum_name = 'item_class') as itemClassName, h.provisioning_id as provisioningId,ne.system_code as provisioningValue" +
					" from b_hw_plan_mapping h JOIN b_network_element ne ON ne.id = h.provisioning_id";

		}

		@Override
		public HardwarePlanData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long itemClass = rs.getLong("itemClass");
			final String itemClassName = rs.getString("itemClassName");
			final Long provisioningId = rs.getLong("provisioningId");
			final String provisioningValue = rs.getString("provisioningValue");
			
			return new HardwarePlanData(id,itemClass,itemClassName,provisioningId,provisioningValue);
		}
	}

}
