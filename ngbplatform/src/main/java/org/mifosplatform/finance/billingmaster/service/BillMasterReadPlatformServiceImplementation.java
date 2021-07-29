package org.mifosplatform.finance.billingmaster.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.finance.chargeorder.data.BillDetailsData;
import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.finance.payments.domain.PaymentRepository;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class BillMasterReadPlatformServiceImplementation implements BillMasterReadPlatformService {
	
	private final JdbcTemplate jdbcTemplate;
	private final CrmServices crmServices;
	private final PaginationHelper<FinancialTransactionsData> paginationHelper = new PaginationHelper<FinancialTransactionsData>();
	private final PaginationHelper<BillDetailsData> pagination = new PaginationHelper<BillDetailsData>();


	@Autowired
	public BillMasterReadPlatformServiceImplementation(final TenantAwareRoutingDataSource dataSource,final CrmServices crmServices) {

		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.crmServices=crmServices;
	}

	@Override
	public List<FinancialTransactionsData> retrieveFinancialData(final Long clientId) {

		final FinancialTransactionsMapper financialTransactionsMapper = new FinancialTransactionsMapper();
		final String sql = "select " + financialTransactionsMapper.financialTransactionsSchema();
		return this.jdbcTemplate.query(sql, financialTransactionsMapper,
				new Object[] { clientId });

	}

	private static final class FinancialTransactionsMapper implements
			RowMapper<FinancialTransactionsData> {

		@Override
		public FinancialTransactionsData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			
			final Long transactionId = resultSet.getLong("transId");
			final String transactionType = resultSet.getString("transType");
			final BigDecimal amount = resultSet.getBigDecimal("amount");
			final LocalDate transDate = JdbcSupport.getLocalDate(resultSet, "transDate");
			final String transactionCategory = resultSet.getString("transType");
			final String description = resultSet.getString("description");
			final String planCode = resultSet.getString("plan_code");
			
			return new FinancialTransactionsData(null,null, transactionId, transDate, transactionType, null, null, amount, null, 
					transactionCategory, false, planCode, description);
		}

		public String financialTransactionsSchema() {
			
			return "SQL_CALC_FOUND_ROWS v.* from billdetails_v v where v.client_id=? ";
		}
	}

	@Override
	public Page<FinancialTransactionsData> retrieveInvoiceFinancialData(final SearchSqlQuery searchTransactionHistory, final Long clientId) {
		
		final FinancialInvoiceTransactionsMapper financialTransactionsMapper = new FinancialInvoiceTransactionsMapper();
		StringBuilder sqlBuilder = new StringBuilder(200);
	        sqlBuilder.append("select ");
	        sqlBuilder.append(financialTransactionsMapper.financialTransactionsSchema());
	        String sqlSearch = searchTransactionHistory.getSqlSearch();
	        String extraCriteria = "";
		    if (sqlSearch != null) {
		    	sqlSearch=sqlSearch.trim();
		    	extraCriteria = " and (v.transType like '%"+sqlSearch+"%' OR "
		    				+ " DATE_FORMAT(v.transDate,'%d %M %Y') like '%"+sqlSearch+"%' OR v.tran_type like '%"+sqlSearch+"%' OR "
		    				+" v.dr_amt like '%"+sqlSearch+"%' OR v.cr_amt like '%"+sqlSearch+"%' )" ;
		    }
		    
	        sqlBuilder.append(extraCriteria);
	        
	        sqlBuilder.append(" order by  transId desc ");
	        
	        if (searchTransactionHistory.isLimited()) {
	            sqlBuilder.append(" limit ").append(searchTransactionHistory.getLimit());
	        }

	        if (searchTransactionHistory.isOffset()) {
	            sqlBuilder.append(" offset ").append(searchTransactionHistory.getOffset());
	        }
		
			return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
		            new Object[] {clientId}, financialTransactionsMapper);
			
	}

	private static final class FinancialInvoiceTransactionsMapper implements
			RowMapper<FinancialTransactionsData> {

		@Override
		public FinancialTransactionsData mapRow(final ResultSet resultSet, final int rowNum)
						throws SQLException {
			final Long transactionId = resultSet.getLong("TransId");
			final String transactionType = resultSet.getString("TransType");
			final BigDecimal debitAmount = resultSet.getBigDecimal("Dr_amt");
			final BigDecimal creditAmount = resultSet.getBigDecimal("Cr_amt");
			final String userName = resultSet.getString("username");
			final String transactionCategory = resultSet.getString("tran_type");
			final boolean flag = resultSet.getBoolean("flag");
			final LocalDate transDate = JdbcSupport.getLocalDate(resultSet,"TransDate");

			return new FinancialTransactionsData(null ,null,transactionId, transDate, transactionType, debitAmount, creditAmount, null, userName, 
					transactionCategory, flag, null, null);
		}

		public String financialTransactionsSchema() {

			return " SQL_CALC_FOUND_ROWS v.* from  fin_trans_vw  v where v.client_id=? ";
		}
	}

	@Override
	public Page<FinancialTransactionsData> retrieveInvoiceFinancialDataByOfficeId(final SearchSqlQuery searchTransactionHistory, final Long officeId) {
		
		final FinancialInvoiceTransactionsMapperForOfficeData financialTransactionsMapper = new FinancialInvoiceTransactionsMapperForOfficeData();
		StringBuilder sqlBuilder = new StringBuilder(200);
	        sqlBuilder.append("select ");
	        sqlBuilder.append(financialTransactionsMapper.financialTransactionsSchema());
	        String sqlSearch = searchTransactionHistory.getSqlSearch();
	        String extraCriteria = "";
		    if (sqlSearch != null) {
		    	sqlSearch=sqlSearch.trim();
		    	extraCriteria = " and (v.transType like '%"+sqlSearch+"%' OR "
		    				+ " DATE_FORMAT(v.transDate,'%d %M %Y') like '%"+sqlSearch+"%' OR v.tran_type like '%"+sqlSearch+"%' OR "
		    				+" v.dr_amt like '%"+sqlSearch+"%' OR v.cr_amt like '%"+sqlSearch+"%' )" ;
		    }
		    
	        sqlBuilder.append(extraCriteria);
	        
	        sqlBuilder.append(" order by  client_id desc ");
	        
	        if (searchTransactionHistory.isLimited()) {
	            sqlBuilder.append(" limit ").append(searchTransactionHistory.getLimit());
	        }

	        if (searchTransactionHistory.isOffset()) {
	            sqlBuilder.append(" offset ").append(searchTransactionHistory.getOffset());
	        }
		
			return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
		            new Object[] {officeId}, financialTransactionsMapper);
			
	}

	private static final class FinancialInvoiceTransactionsMapperForOfficeData implements
			RowMapper<FinancialTransactionsData> {

		@Override
		public FinancialTransactionsData mapRow(final ResultSet resultSet, final int rowNum)
						throws SQLException {
			final Long clientId = resultSet.getLong("client_id");
			final Long transactionId = resultSet.getLong("TransId");
			final String transactionType = resultSet.getString("TransType");
			final BigDecimal debitAmount = resultSet.getBigDecimal("Dr_amt");
			final BigDecimal creditAmount = resultSet.getBigDecimal("Cr_amt");
			final String userName = resultSet.getString("username");
			final String transactionCategory = resultSet.getString("tran_type");
			final boolean flag = resultSet.getBoolean("flag");
			final LocalDate transDate = JdbcSupport.getLocalDate(resultSet,"TransDate");

			return new FinancialTransactionsData(null,clientId, transactionId, transDate, transactionType, debitAmount, creditAmount, null, userName, 
					transactionCategory, flag, null, null);
		}

		public String financialTransactionsSchema() {

			return " SQL_CALC_FOUND_ROWS v.* from  fin_trans_vw  v inner join m_client c on v.client_id=c.id where c.office_id=? ";
		}
	}

	@Override

	public List<FinancialTransactionsData> getFinancialTransactionData(final Long id) {

		final TransactionDataMapper mapper = new TransactionDataMapper();
		final String sql = "select " + mapper.billSchema() + " and b.id =" + id;

		return   this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class TransactionDataMapper implements RowMapper<FinancialTransactionsData> {

		public String billSchema() {
			return "be.id,be.transaction_id as transaction_id,be.Transaction_type as Transaction_type, " +
				"be.description as description,be.Amount as Amount,be.Transaction_date as Transaction_date" +
				" from b_bill_master b,b_bill_details be where b.id = be.bill_id";
	    }

	    @Override
	    public FinancialTransactionsData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
	    	
	    	final Long transctionId = resultSet.getLong("transaction_id");
	    	final String transactionType = resultSet.getString("Transaction_type");
	        final BigDecimal amount = resultSet.getBigDecimal("Amount");
	        final LocalDate transactionDate = JdbcSupport.getLocalDate(resultSet, "Transaction_date");
	        return new FinancialTransactionsData(transctionId, transactionType, transactionDate, amount);

		}
	}

	@Override
	public Page<BillDetailsData> retrieveStatments(SearchSqlQuery searchCodes,Long clientId) {

		
		final BillStatmentMapper mapper = new BillStatmentMapper();
		final String sql = "select " + mapper.billStatemnetSchema() + " and b.is_deleted='N'";
		StringBuilder sqlBuilder = new StringBuilder(200);
		sqlBuilder.append(sql);
		String sqlSearch=searchCodes.getSqlSearch();
		  String extraCriteria = "";
		    if (sqlSearch != null) {
		    	sqlSearch=sqlSearch.trim();
		    	extraCriteria = "  and (b.Due_amount like '%"+sqlSearch+"%' or DATE_FORMAT(b.bill_date,'%d %M %Y') like '%"+sqlSearch+"%' " +
		    						" or DATE_FORMAT(b.due_date,'%d %M %Y') like '%"+sqlSearch+"%' )"; 
		    }
		    
		    sqlBuilder.append(extraCriteria);
		
		if (searchCodes.isLimited()) {
            sqlBuilder.append(" limit ").append(searchCodes.getLimit());
        }
        if (searchCodes.isOffset()) {
            sqlBuilder.append(" offset ").append(searchCodes.getOffset());
        }

		//return this.jdbcTemplate.query(sql, mapper, new Object[] {});
		return this.pagination.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
	            new Object[] {clientId}, mapper);

	}

	private static final class BillStatmentMapper implements RowMapper<BillDetailsData> {

		@Override
		public BillDetailsData mapRow(final ResultSet resultSet, final int rowNum)  throws SQLException {
			
			final Long id = resultSet.getLong("id");
			final BigDecimal amount = resultSet.getBigDecimal("dueAmount");
			final LocalDate billDate = JdbcSupport.getLocalDate(resultSet, "billDate");
			final LocalDate dueDate = JdbcSupport.getLocalDate(resultSet, "dueDate");

			return new BillDetailsData(id,billDate,dueDate,amount);
		}

		public String billStatemnetSchema() {
			
			return  "b.id as id,b.bill_date as billDate,b.due_date as dueDate,b.Due_amount as dueAmount from b_bill_master b where b.Client_id=? ";

		}
	}

		@Override
		public BigDecimal retrieveClientBalance(final Long clientId) {
         
			BigDecimal previousBalance = BigDecimal.ZERO;
			final ClientBalanceMapper mapper = new ClientBalanceMapper();
			final String sql = "select " + mapper.billStatemnetSchema();
			try{
				previousBalance =  this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { clientId });
				if( previousBalance == null ){
					previousBalance = BigDecimal.ZERO;
				}
			}catch(EmptyResultDataAccessException accessException){
				return previousBalance;
			}
			
			return previousBalance;
		}

		private static final class ClientBalanceMapper implements
				RowMapper<BigDecimal> {

			@Override
			public BigDecimal mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

				final BigDecimal amount = resultSet.getBigDecimal("dueAmount");
				return amount;
			}
			
			public String billStatemnetSchema() {

				final String result = " IFNULL(b.Due_amount,0) as dueAmount  from b_bill_master b  where b.Client_id=? and id=(Select max(id) from b_bill_master a where a.client_id=b.client_id and a.is_deleted='N')";

				return result;
			}
		}

		@Override
		public List<FinancialTransactionsData> retrieveSingleInvoiceData(final Long invoiceId) {
			final InvoiceDataMapper mapper = new InvoiceDataMapper();
			final String sql = "select " + mapper.invoiceSchema();
			return this.jdbcTemplate.query(sql, mapper,
					new Object[] { invoiceId,invoiceId,invoiceId });

		}

		private static final class InvoiceDataMapper implements
				RowMapper<FinancialTransactionsData> {

			@Override
			public FinancialTransactionsData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
				   final Long chargeId = resultSet.getLong("chargeId");
				   final String chargeType = resultSet.getString("chargeType");
				   final String chargeDescription = resultSet.getString("chargeDescription");
				   final BigDecimal chargeAmount = resultSet.getBigDecimal("chargeAmount");   
				   final LocalDate chargeStartDate = JdbcSupport.getLocalDate(resultSet, "chargeStartDate");
				   final LocalDate chargeEndDate =JdbcSupport.getLocalDate(resultSet, "chargeEndDate");
				   final BigDecimal taxAmount = resultSet.getBigDecimal("taxAmount");
				   final BigDecimal discountAmount = resultSet.getBigDecimal("discountAmount");
				   final BigDecimal netChargeAmount = resultSet.getBigDecimal("netChargeAmount");
				   final Long orderId = resultSet.getLong("orderId");

				return new FinancialTransactionsData(chargeId, chargeType, chargeDescription, chargeAmount, taxAmount, discountAmount,
						netChargeAmount, chargeStartDate, chargeEndDate, orderId);
			}

			public String invoiceSchema() {

				return  "* from (SELECT c.id as chargeId,c.order_id AS orderId, c.charge_type AS chargeType,ch.charge_description AS chargeDescription, " +
						" c.charge_start_date AS chargeStartDate, c.charge_end_date AS chargeEndDate,c.charge_amount AS chargeAmount,'' AS taxAmount,'' AS discountAmount, '' AS netChargeAmount "+
						" FROM b_charge_codes ch,b_charge c WHERE c.charge_code = ch.charge_code AND c.billitem_id = ? union all " +
                        " Select c.id,  '' AS orderId,'Tax' AS chargeType,ct.tax_code  AS chargeDescription,c.charge_start_date AS chargeStartDate, c.charge_end_date AS chargeEndDate, " +
                        " '' AS chargeAmount,ct.tax_amount AS taxAmount,'' AS discountAmount,'' AS netChargeAmount from b_charge c,b_charge_tax ct " +
                        " WHERE c.id=ct.charge_id  AND c.billitem_id = ? union all " +
                        "SELECT c.id,  '' AS orderId,  'Discount' AS chargeType,c.discount_code AS chargeDescription,c.charge_start_date AS chargeStartDate,c.charge_end_date AS chargeEndDate,'' AS chargeAmount ,'' AS taxAmount,"+
                        " c.discount_amount AS discountAmount,c.netcharge_amount AS netChargeAmount from b_charge c WHERE c.billitem_id = ?) A order by 2,1 ";
				}
			}

		@Override
		public List<BillDetailsData> retrieveStatementDetails(final Long billId) {


			final StatementMapper statementMapper = new StatementMapper();
			final String sql = "select " + statementMapper.financialTransactionsSchema();
			return this.jdbcTemplate.query(sql, statementMapper,new Object[] { billId});

		}

		private static final class StatementMapper implements
				RowMapper<BillDetailsData> {

			@Override
			public BillDetailsData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
				final Long id = resultSet.getLong("id");
				final Long clietId = resultSet.getLong("clietId");
				final LocalDate dueDate = JdbcSupport.getLocalDate(resultSet, "dueDate");
				final String transactionType = resultSet.getString("transactionType");
				final BigDecimal dueAmount = resultSet.getBigDecimal("dueAmount");
				final BigDecimal amount = resultSet.getBigDecimal("amoount");

				return new BillDetailsData(id, clietId, dueDate, transactionType, dueAmount, amount, null);
			}

			public String financialTransactionsSchema() {

				return  "b.id AS id,b.Client_id as clietId,b.Due_date AS dueDate,b.Due_amount AS dueAmount,bd.Transaction_type as transactionType,"
						+" bd.Amount as amoount FROM b_bill_master b, b_bill_details bd where  b.id = bd.Bill_id and b.id =?";

			}
		}
		
		@Override
		public Page<FinancialTransactionsData> retrieveSampleData(final SearchSqlQuery searchTransactionHistory, final Long clientId, final String type) {
			final FinancialTypeMapper financialTypeMapper = new FinancialTypeMapper();
		
				StringBuilder sqlBuilder = new StringBuilder(200);
		        sqlBuilder.append("select ");
		        sqlBuilder.append(financialTypeMapper.financialTypeSchema() + "and transType like '%" + type + "%'");
		   
		        if (searchTransactionHistory.isLimited()) {
		            sqlBuilder.append(" limit ").append(searchTransactionHistory.getLimit());
		        }

		        if (searchTransactionHistory.isOffset()) {
		            sqlBuilder.append(" offset ").append(searchTransactionHistory.getOffset());
		        }
			
				return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
			            new Object[] {clientId}, financialTypeMapper);
				
		}

		private static final class FinancialTypeMapper implements
				RowMapper<FinancialTransactionsData> {

			@Override
			public FinancialTransactionsData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
				final Long transactionId = resultSet.getLong("TransId");
				final String transactionType = resultSet.getString("TransType");
				final BigDecimal debitAmount = resultSet.getBigDecimal("DebitAmount");
				final BigDecimal creditAmount = resultSet.getBigDecimal("CreditAmount");
				final String userName = resultSet.getString("username");
				final String transactionCategory = resultSet.getString("tran_type");
				final boolean flag = resultSet.getBoolean("flag");
				final LocalDate transDate = JdbcSupport.getLocalDate(resultSet, "TransDate");
			    /*final String receiptNumber = "R-1092";*/
			
				
			    return new  FinancialTransactionsData(null,null, transactionId, transDate, transactionType, debitAmount, creditAmount, null, 
							userName, transactionCategory, flag, null, null);
			
			    /*FinancialTransactionsData ft = new FinancialTransactionsData(null, transactionId, transDate, transactionType, debitAmount, creditAmount, null, 
							userName, transactionCategory, flag, null, null);
			
	
			    ft .setReceiptNumber(receiptNumber);*/
				/*
				return FinancialTransactionsData;*/
				
				
			}
			
			public String financialTypeSchema() {

				return " ft.username as username,ft.TransId as TransId,ft.TransType as TransType,ft.Dr_amt as DebitAmount,ft.Cr_amt as CreditAmount,"
						+"ft.tran_type as tran_type,ft.flag as flag,ft.TransDate as TransDate from fin_trans_vw as ft  where client_id=? ";

			}
		}
		
		@Override
		
		public	Page<FinancialTransactionsData> retrieveStatementsData(SearchSqlQuery searchFinancialTransactionNew, Long clientId,
				String type, String fromDate, String toDate) {
		    
			
			final FinancialTypeNewMapper financialTypeMapper = new FinancialTypeNewMapper();
			
			StringBuilder sqlBuilder = new StringBuilder(200);
	        sqlBuilder.append("SELECT ");
	        sqlBuilder.append(financialTypeMapper.financialTypeSchem());
	        
	        if(fromDate != null && toDate != null) {
	        	sqlBuilder.append(" and date_format(bp.payment_date,'%Y-%m-%d') between '"+fromDate+"' and '"+toDate+"' ");
		    }
	        sqlBuilder.append(" ) a ");
	        sqlBuilder.append(" WHERE a.client_id ="+clientId );
	       /* if(fromDate != null && toDate != null && type != null) {
	        	sqlBuilder.append(" and tckt.status='"+type+"' and DATE_FORMAT(bp.payment_date,'%Y-%m-%d') between '"+fromDate+"' and '"+toDate+"' ");
		    }*/
	        
	        if (searchFinancialTransactionNew.isLimited()) {
	            sqlBuilder.append(" limit ").append(searchFinancialTransactionNew.getLimit());
	        }

	        if (searchFinancialTransactionNew.isOffset()) {
	            sqlBuilder.append(" offset ").append(searchFinancialTransactionNew.getOffset());
	        }
		
			return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
		            new Object[] {}, financialTypeMapper);
			
	     }
		
		private static final class FinancialTypeNewMapper implements
		RowMapper<FinancialTransactionsData> {

	    @Override
	    public FinancialTransactionsData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		final Long transactionId = resultSet.getLong("TransId");
		final String transactionType = resultSet.getString("TransType");
		final BigDecimal debitAmount = resultSet.getBigDecimal("DebitAmount");
		final BigDecimal creditAmount = resultSet.getBigDecimal("CreditAmount");
		final String userName = resultSet.getString("username");
		final String transactionCategory = resultSet.getString("tran_type");
		final boolean flag = resultSet.getBoolean("flag");
		final LocalDate transDate = JdbcSupport.getLocalDate(resultSet, "TransDate");
	    final String receiptNo=resultSet.getString("receiptNo");
	    final Long clientPoId=resultSet.getLong("clientPoId");
	    final String cancelRemark=resultSet.getString("cancelRemark");
	
		
	
	    FinancialTransactionsData ft = new FinancialTransactionsData(null, null,transactionId, transDate, transactionType, debitAmount, creditAmount, null, 
					userName, transactionCategory, flag, null, null);

	    ft .setReceiptNo(receiptNo);
	    ft .setClientPoId(clientPoId);
	    ft .setCancelRemark(cancelRemark);
		return ft;
		
		
	   }
	
	   public String financialTypeSchem() {

		return   " * from (SELECT bp.id AS transId,bp.receipt_no AS receiptNo,ma.username AS username,bp.client_id AS client_id,mc.po_id as clientPoid,bp.cancel_remark AS cancelRemark, "+
				 " (SELECT mca.code_value FROM m_code_value mca WHERE ((mca.code_id = 11) AND (bp.paymode_id = mca.id))) AS tran_type, "+
				 " CAST(bp.payment_date AS DATE) AS transDate,'PAYMENT' AS transType, 0 AS DebitAmount,bp.amount_paid AS CreditAmount,bp.is_deleted AS flag "+
				 " FROM b_payments bp JOIN m_appuser ma on bp.createdby_id=ma.id JOIN m_client mc ON bp.client_id= mc.id WHERE ISNULL(bp.ref_id) " + 
				 " UNION ALL  "+
				 " SELECT bp.id AS transId,bp.receipt_no AS receiptNo,ma.username AS username, bp.client_id AS client_id,mc.po_id as clientPoid,bp.cancel_remark AS cancelRemark, "+
				 " (SELECT mca.code_value FROM m_code_value mca WHERE ((mca.code_id = 11) AND (bp.paymode_id = mca.id))) AS tran_type, "+
				 " CAST(bp.payment_date AS DATE) AS transDate,'PAYMENT CANCELLED' AS transType,ABS(bp.amount_paid) AS DebitAmount,0 AS CreditAmount,bp.is_deleted AS flag "+
				 " FROM b_payments bp JOIN m_appuser ma on bp.createdby_id=ma.id JOIN m_client mc ON bp.client_id= mc.id WHERE (bp.is_deleted = 1) AND (bp.ref_id IS NOT NULL) ";
    
	      }
		}
		
		
		      
		
		
		
		
		@Override
		public List<FinancialTransactionsData> retriveDataForDownload(final Long clientId, final String startDate, final String endDate){
			
			StringBuilder builder = new StringBuilder(200);
			builder.append("select ft.username as username,ft.transId as TransId,ft.transType as TransType,ft.Dr_amt as DebitAmount,ft.Cr_amt as CreditAmount,"
						+"ft.tran_type as tran_type,ft.flag as flag,ft.transDate as TransDate from fin_trans_vw as ft where client_id=" + clientId);
			
			builder.append(" and date_format(transDate,'%Y-%m-%d') between '" + startDate + "' and '" + endDate + "' order by transDate asc");
			final FinancialTypeMapper mapper = new FinancialTypeMapper();
			return jdbcTemplate.query(builder.toString(), mapper, new Object[]{});
		}


		@Override
		public List<BillDetailsData> retrieveStatements(SearchSqlQuery searchCodes,Long clientId) {
			final BilldetailStatmentMapper mapper = new BilldetailStatmentMapper();
			final String sql = "select " + mapper.billStatemnetdetailSchema() + "where b.Client_id=?  and b.is_deleted='N'";
			StringBuilder sqlBuilder = new StringBuilder(200);
			sqlBuilder.append(sql);
			/*String sqlSearch=searchCodes.getSqlSearch();
			  String extraCriteria = "";
			    if (sqlSearch != null) {
			    	sqlSearch=sqlSearch.trim();
			    	extraCriteria = "  and (b.Due_amount like '%"+sqlSearch+"%' or DATE_FORMAT(b.bill_date,'%d %M %Y') like '%"+sqlSearch+"%' " +
			    						" or DATE_FORMAT(b.due_date,'%d %M %Y') like '%"+sqlSearch+"%' )"; 
			    }
			    
			    sqlBuilder.append(extraCriteria);*/
			/*
			if (searchCodes.isLimited()) {
	            sqlBuilder.append(" limit ").append(searchCodes.getLimit());
	        }
	        if (searchCodes.isOffset()) {
	            sqlBuilder.append(" offset ").append(searchCodes.getOffset());
	        }*/

			//return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	        return this.jdbcTemplate.query(sql, mapper,
					new Object[] { clientId });

		}

		private static final class BilldetailStatmentMapper implements RowMapper <BillDetailsData> {

			@Override
			public BillDetailsData mapRow(final ResultSet resultSet, final int rowNum)  throws SQLException {
				
				final Long id = resultSet.getLong("id");
				final BigDecimal amount = resultSet.getBigDecimal("dueAmount");
				final LocalDate billDate = JdbcSupport.getLocalDate(resultSet, "billDate");
				final LocalDate dueDate = JdbcSupport.getLocalDate(resultSet, "dueDate");
				final String poId=resultSet.getString("poId");

				return new BillDetailsData(id,billDate,dueDate,amount,poId);
			}

			public String billStatemnetdetailSchema() {
				
				return  "b.id as id,b.bill_date as billDate,b.due_date as dueDate,b.Due_amount as dueAmount, c.po_id as poId from b_bill_master b join m_client c on b.Client_id=c.id ";

			}
		}

}