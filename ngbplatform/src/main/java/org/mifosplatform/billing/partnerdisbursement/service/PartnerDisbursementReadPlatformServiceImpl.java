package org.mifosplatform.billing.partnerdisbursement.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Date;
import java.util.List;

import org.mifosplatform.billing.partnerdisbursement.data.PartnerDisbursementData;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.dataqueries.service.GenericDataService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PartnerDisbursementReadPlatformServiceImpl implements
PartnerDisbursementReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final PaginationHelper<PartnerDisbursementData> paginationHelper = new PaginationHelper<PartnerDisbursementData>();

	@Autowired
	public PartnerDisbursementReadPlatformServiceImpl(
			final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource,
			final GenericDataService genericDataService) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	
	@Override
	public Page<PartnerDisbursementData> getAllData(SearchSqlQuery search, String sourceType, String partnerType) {
		try {

			context.authenticatedUser();
			RetrieveRandomMapper mapper = new RetrieveRandomMapper();
			StringBuilder sqlBuilder = new StringBuilder();
			
			sqlBuilder.append("SELECT ");
			sqlBuilder.append(mapper.schema());
			sqlBuilder.append(" where st.id IS NOT NULL ");
			
	        if(sourceType != null){
	        	sqlBuilder.append(" and (st.comm_source ='"+sourceType+"') ");
		    }
	        if(partnerType != null){
	        	sqlBuilder.append(" and (o.name ='"+partnerType+"') ");
		    }
			
			if (search.isLimited()) {
				sqlBuilder.append(" limit ").append(search.getLimit());
		    }

		    if (search.isOffset()) {
		        sqlBuilder.append(" offset ").append(search.getOffset());
		    }
		    
			return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
		            new Object[] {}, mapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class RetrieveRandomMapper implements
			RowMapper<PartnerDisbursementData> {

		public String schema() {

				return " st.id AS id, o.name as partnerName, st.created_dt as transDate, st.comm_source as source, st.share_amount as shareAmount,"
                       + " st.share_type as commisionType,st.amt as commissionAmount,c.netcharge_amount as netChargeAmount,c.charge_amount as chargeAmount" 
                       + " FROM b_office_commission st JOIN b_charge c ON st.charge_id = c.id JOIN m_office o ON  o.id= st.office_id ";


		}

		@Override
		public PartnerDisbursementData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {

			final Long id = rs.getLong("id");
			final String partnerName = rs.getString("partnerName");
			final Date transDate = rs.getDate("transDate");
			final String source = rs.getString("source");
			final String percentage = rs.getString("commisionType");
			final Double shareAmount = rs.getDouble("shareAmount");
			final Double commissionAmount = rs.getDouble("commissionAmount");
			final Double chargeAmount = rs.getDouble("chargeAmount");
			final Double netAmount = rs.getDouble("netChargeAmount");

			return new PartnerDisbursementData(id, partnerName, transDate,
					source, percentage, commissionAmount,chargeAmount, netAmount,shareAmount);

		}
	}

	@Override
	public List<PartnerDisbursementData> getPatnerData() {
		try {
			context.authenticatedUser();
			PartnerDataMapper mapper = new PartnerDataMapper();
			String sql="SELECT "+ mapper.schema();
			return this.jdbcTemplate.query(sql, mapper, new Object[] {});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	private static final class PartnerDataMapper implements RowMapper<PartnerDisbursementData> {

		public String schema() {
			return " o.id as id,o.name as partnerName from m_office o inner join m_office_additional_info af where o.id=af.office_id";

		}

		@Override
		public PartnerDisbursementData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			Long id = rs.getLong("id");
			String partnerName = rs.getString("partnerName");
			return new PartnerDisbursementData(id, partnerName);
		}
	}
}
