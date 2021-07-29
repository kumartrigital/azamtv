package org.mifosplatform.organisation.officeadjustments.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.finance.officebalance.data.OfficeBalanceData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class OfficeAdjustmentsReadPaltformServiceImp implements OfficeAdjustmentsReadPaltformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;

	@Autowired
	public OfficeAdjustmentsReadPaltformServiceImp(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<OfficeBalanceData> retrieveOfficeBalance(final Long officeId) {

		final OfficeBalanceMapper mapper = new OfficeBalanceMapper();
		final String sql = "select " + mapper.schema() + " where ob.office_id=?";
		return this.jdbcTemplate.query(sql, mapper, new Object[] { officeId });
	}

	public static final class OfficeBalanceMapper implements RowMapper<OfficeBalanceData> {

		@Override
		public OfficeBalanceData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			
			final Long id = JdbcSupport.getLong(rs, "id");
			final Long officeId = JdbcSupport.getLong(rs, "officeId");
			final BigDecimal balanceAmount = rs.getBigDecimal("balanceAmount");
			return new OfficeBalanceData(id, officeId, balanceAmount);

		}

		public String schema() {
			return "ob.id as id, ob.office_id as officeId ,ob.balance_amount as balanceAmount from m_office_balance ob";
		}
	}

}
