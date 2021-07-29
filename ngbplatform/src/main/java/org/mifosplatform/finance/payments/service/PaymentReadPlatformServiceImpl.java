package org.mifosplatform.finance.payments.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.invoice.data.InvoiceData;
import org.mifosplatform.finance.payments.data.McodeData;
import org.mifosplatform.finance.payments.data.PaymentData;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentReadPlatformServiceImpl implements PaymentReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;

	@Autowired
	public PaymentReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private static final class PaymodeMapper implements RowMapper<McodeData> {

		public String codeScheme() {
			return "b.id,code_value from m_code a, m_code_value b where a.id = b.code_id ";
		}

		@Override
		public McodeData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			final String paymodeCode = rs.getString("code_value");

			return McodeData.instance(id, paymodeCode);
		}

	}

	@Transactional
	@Override
	public Collection<McodeData> retrievemCodeDetails(final String codeName) {
		final PaymodeMapper mapper = new PaymodeMapper();
		final String sql = "select " + mapper.codeScheme() + " and code_name=?";

		return this.jdbcTemplate.query(sql, mapper, new Object[] { codeName });
	}

	@Override
	public McodeData retrieveSinglePaymode(final Long paymodeId) {
		final PaymodeMapper mapper = new PaymodeMapper();
		final String sql = "select " + mapper.codeScheme() + " and b.id=" + paymodeId;

		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {});
	}

	@Override
	public McodeData retrievePaymodeCode(final JsonCommand command) {
		final PaymodeMapper1 mapper = new PaymodeMapper1();
		final String sql = "select id from m_code where code_name='" + command.stringValueOfParameterNamed("code_id")
				+ "'";

		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {});
	}

	private static final class PaymodeMapper1 implements RowMapper<McodeData> {

		@Override
		public McodeData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			return McodeData.instance1(id);
		}

	}

	@Override
	public List<PaymentData> retrivePaymentsData(final Long clientId) {
		final String sql = "select (select display_name from m_client where id = p.client_id) as clientName, (select code_value from m_code_value where id = p.paymode_id) as payMode, p.payment_date as paymentDate, p.amount_paid as amountPaid, p.is_deleted as isDeleted, p.bill_id as billNumber, p.receipt_no as receiptNo,p.currency as currency from b_payments p where p.client_id=?";
		final PaymentsMapper pm = new PaymentsMapper();
		return jdbcTemplate.query(sql, pm, new Object[] { clientId });
	}

	private class PaymentsMapper implements RowMapper<PaymentData> {
		@Override
		public PaymentData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final String clientName = rs.getString("clientName");
			final String payMode = rs.getString("payMode");
			final LocalDate paymentDate = JdbcSupport.getLocalDate(rs, "paymentDate");
			final BigDecimal amountPaid = rs.getBigDecimal("amountPaid");
			final Boolean isDeleted = rs.getBoolean("isDeleted");
			final Long billNumber = rs.getLong("billNumber");
			final String receiptNumber = rs.getString("receiptNo");
			final String currency = rs.getString("currency");
			return new PaymentData(clientName, payMode, paymentDate, amountPaid, isDeleted, billNumber, receiptNumber,currency);
		}
	}

	@Transactional
	@Override
	public Long getOnlinePaymode(String paymodeId) {
		try {
			context.authenticatedUser();
			final Mapper mapper = new Mapper();
			final String sql = "select id from m_code_value where code_value  LIKE '" + paymodeId + "'";
			return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {});

		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class Mapper implements RowMapper<Long> {

		@Override
		public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			return id;
		}

	}

	@Override
	public List<PaymentData> retrieveClientPaymentDetails(final Long clientId) {

		try {
			context.authenticatedUser();
			final InvoiceMapper mapper = new InvoiceMapper();
			final String sql = "select " + mapper.schema();
			return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });
		} catch (EmptyResultDataAccessException accessException) {
			return null;
		}

	}

	private static final class InvoiceMapper implements RowMapper<PaymentData> {

		public String schema() {
			return "  p.id AS id,p.payment_date AS paymentdate,p.amount_paid AS amount,p.receipt_no AS recieptNo,p.amount_paid - (ifnull((SELECT SUM(amount)"
					+ "  FROM b_credit_distribution WHERE payment_id = p.id),0)) AS availAmount,p.currency as currency FROM b_payments p left join b_credit_distribution cd on p.client_id = cd.client_id"
					+ "  WHERE p.client_id =? AND p.invoice_id IS NULL GROUP BY p.id ";
		}

		@Override
		public PaymentData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final LocalDate paymentdate = JdbcSupport.getLocalDate(rs, "paymentdate");
			final BigDecimal amount = rs.getBigDecimal("amount");
			final BigDecimal availAmount = rs.getBigDecimal("availAmount");
			final String recieptNo = rs.getString("recieptNo");
			final String currency=rs.getString("currency");

			return new PaymentData(id, paymentdate, amount, recieptNo, availAmount,currency);

		}
	}

	@Override
	public List<PaymentData> retrieveDepositDetails(Long id) {

		try {
			context.authenticatedUser();
			DepositMapper mapper = new DepositMapper();

			String sql = "select " + mapper.schema();

			return this.jdbcTemplate.query(sql, mapper, new Object[] { id });
		} catch (EmptyResultDataAccessException accessException) {
			return null;
		}

	}

	private static final class DepositMapper implements RowMapper<PaymentData> {

		public String schema() {
			return "bdr.id as id, bdr.transaction_date as transactionDate, bdr.debit_amount as debitAmount from b_deposit_refund bdr "
					+ "where bdr.client_id = ? and transaction_type = 'Deposit' and bdr.payment_id is NULL";

		}

		@Override
		public PaymentData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			Date transactionDate = rs.getDate("transactionDate");
			BigDecimal debitAmount = rs.getBigDecimal("debitAmount");
			return new PaymentData(id, transactionDate, debitAmount);

		}
	}

	@Override
	public BigDecimal getPaymentDetails(final Long officeId) {
		final onlinePaymentMapper mapper = new onlinePaymentMapper();
		final String sql = "select sum(amount_paid) as onlinePayment from b_paymentgateway where status = 'Success' and obs_id = "
				+ officeId;
		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {});

	}

	final class onlinePaymentMapper implements RowMapper<BigDecimal> {

		@Override
		public BigDecimal mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final BigDecimal onlinePayments = rs.getBigDecimal("onlinePayment");
			return onlinePayments;
		}
	}

	@Override
	public BigDecimal getVoucherDetails(final Long officeId) {
		final voucherPaymentMapper mapper = new voucherPaymentMapper();
		final String sql = "select  sum(charge_amount) as voucherPayment from b_itemsale where status ='Completed'"
				+ " and type = '' and purchase_by = " + officeId;
		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {});

	}

	final class voucherPaymentMapper implements RowMapper<BigDecimal> {

			@Override
			public BigDecimal mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				final BigDecimal onlinePayments  = rs.getBigDecimal("voucherPayment");
				return  onlinePayments;
			}
	}
	
	
/*@Override
	public PaymentData retriveClientPoid(Long id) {
   	 try{
			   this.context.authenticatedUser();
   	final PaymentMapperNew mapper = new PaymentMapperNew();
		final String sql = "select distinct" + mapper.schema()+" where c.id = ?";
		return   jdbcTemplate.queryForObject(sql.toString(),mapper,new Object[]{id});	
	  }catch(EmptyResultDataAccessException e){
	return null;
	  }
    }	

    private static final class PaymentMapperNew implements RowMapper<PaymentData> {
		
		public String schema(){
   		
   		return " c.id as clientId,c.po_id as clientPoid from m_client c join b_payments p on p.client_id=c.id ";
   			
		}
		  @Override
	      public PaymentData mapRow(final ResultSet rs,final int rowNum) throws SQLException {
	          final long id = rs.getLong("id");
	          final long clientPoid = rs.getLong("clientPoid");
	          PaymentData paymentData= new PaymentData(id, clientPoid);
	          return paymentData;
	      }
      }
*/
}