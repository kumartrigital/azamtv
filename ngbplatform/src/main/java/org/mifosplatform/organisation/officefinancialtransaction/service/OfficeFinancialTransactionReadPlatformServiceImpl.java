package org.mifosplatform.organisation.officefinancialtransaction.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.joda.time.LocalDate;
import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class OfficeFinancialTransactionReadPlatformServiceImpl implements OfficeFinancialTransactionReadPlatformService{

	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public OfficeFinancialTransactionReadPlatformServiceImpl(final PlatformSecurityContext context,
															final TenantAwareRoutingDataSource dataSource) {
						this.context = context;
						this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public Collection<FinancialTransactionsData> retreiveOfficeFinancialTransactionsData(final Long officeId) {
		
		context.authenticatedUser();
		final OfficeFinancialTransactionMapper mapper = new OfficeFinancialTransactionMapper(); 
		final String sql = "select v.* from  office_fin_trans_vw v where v.office_id=" + officeId + " order by  transDate desc ";
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}
	
	private static final class OfficeFinancialTransactionMapper implements RowMapper<FinancialTransactionsData> {

		@Override
		public FinancialTransactionsData mapRow(final ResultSet resultSet, final int rowNum)throws SQLException {
			final Long officeId = resultSet.getLong("office_id");
			final Long transactionId = resultSet.getLong("TransId");
			final String transactionType = resultSet.getString("TransType");
			final BigDecimal debitAmount = resultSet.getBigDecimal("Dr_amt");
			final BigDecimal creditAmount =resultSet.getBigDecimal("Cr_amt");
			final String userName = resultSet.getString("username");
			final String transactionCategory = resultSet.getString("tran_type");
			final boolean flag = resultSet.getBoolean("flag");
			final LocalDate transDate = JdbcSupport.getLocalDate(resultSet, "TransDate");

			return new FinancialTransactionsData(officeId,null, transactionId, transDate, transactionType, debitAmount, creditAmount, 
					null, userName, transactionCategory, flag, null, null);
		}
     }
}
