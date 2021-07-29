package org.mifosplatform.logistics.mrn.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.mrn.data.InventoryTransactionHistoryData;
import org.mifosplatform.logistics.mrn.data.MRNDetailsData;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class MRNDetailsReadPlatformServiceImp implements MRNDetailsReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;

	private final PaginationHelper<MRNDetailsData> paginationHelper = new PaginationHelper<MRNDetailsData>();
	private final PaginationHelper<InventoryTransactionHistoryData> paginationHelper2 = new PaginationHelper<InventoryTransactionHistoryData>();

	@Autowired
	public MRNDetailsReadPlatformServiceImp(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private final class MRNDetailsMapper implements RowMapper<MRNDetailsData> {
		@Override
		public MRNDetailsData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final String id = rs.getString("mrnId");
			final LocalDate requestedDate = JdbcSupport.getLocalDate(rs, "requestedDate");
			final String fromOffice = rs.getString("fromOffice");
			final String toOffice = rs.getString("toOffice");
			final Long orderdQuantity = rs.getLong("orderdQuantity");
			final Long receivedQuantity = rs.getLong("receivedQuantity");
			final String status = rs.getString("status");
			final String itemDescription = rs.getString("item");
			final String itemClass = rs.getString("itemClass");
			final Long fromOfficeNum = rs.getLong("fromOfficeId");
			final Long toOfficeNum = rs.getLong("toOfficeId");
			// final String notes = rs.getString("notes");

			MRNDetailsData mrnDetailsData = new MRNDetailsData(id, requestedDate, fromOffice, toOffice, orderdQuantity,
					receivedQuantity, status, itemDescription/* ,notes */);
			mrnDetailsData.setItemClass(itemClass);
			mrnDetailsData.setFromOfficeNum(fromOfficeNum);
			mrnDetailsData.setToOfficeNum(toOfficeNum);

			return mrnDetailsData;
		}
	}

	private final class MRNitemDetailsMapper implements RowMapper<MRNDetailsData> {
		@Override
		public MRNDetailsData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final String id = rs.getString("id");
			final LocalDate requestedDate = JdbcSupport.getLocalDate(rs, "requestedDate");
			final String fromOffice = rs.getString("fromOffice");
			final String toOffice = rs.getString("toOffice");
			final Long orderdQuantity = rs.getLong("orderdQuantity");
			final Long receivedQuantity = rs.getLong("receivedQuantity");
			final String status = rs.getString("status");
			final String itemDescription = rs.getString("item");
			final String itemClass = rs.getString("itemClass");
			final Long fromOfficeNum = rs.getLong("fromOfficeId");
			final Long toOfficeNum = rs.getLong("toOfficeId");
			final BigDecimal chargeAmount = rs.getBigDecimal("chargeAmount");

			MRNDetailsData mrnDetailsData = new MRNDetailsData(id, requestedDate, fromOffice, toOffice, orderdQuantity,
					receivedQuantity, status, itemDescription, chargeAmount);
			mrnDetailsData.setItemClass(itemClass);
			mrnDetailsData.setFromOfficeNum(fromOfficeNum);
			mrnDetailsData.setToOfficeNum(toOfficeNum);

			return mrnDetailsData;
		}
	}

	private final class MRNDetailsMrnIDsMapper implements RowMapper<MRNDetailsData> {
		@Override
		public MRNDetailsData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long mrnId = rs.getLong("mrnId");
			final Long itemMasterId = rs.getLong("itemMasterId");
			final String itemDescription = rs.getString("itemDescription");
			return new MRNDetailsData(mrnId, itemDescription, itemMasterId, null);
		}
	}

	private final class MRNDetailsSerialMapper implements RowMapper<String> {

		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getString("serialNumber");

		}
	}

	private final class MRNDetailsHistoryMapper implements RowMapper<InventoryTransactionHistoryData> {
		@Override
		public InventoryTransactionHistoryData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final String itemDescription = rs.getString("itemDescription");
			final String serialNumber = rs.getString("serialNumber");
			final LocalDate transactionDate = JdbcSupport.getLocalDate(rs, "transactionDate");
			final String fromOffice = rs.getString("source");
			final String toOffice = rs.getString("destination");
			final String refType = rs.getString("refType");
			final String movement = rs.getString("movement");
			return new InventoryTransactionHistoryData(transactionDate, itemDescription, fromOffice, toOffice,
					serialNumber, refType, movement);
		}
	}

	@Override
	public Page<MRNDetailsData> retriveMRNDetails(SearchSqlQuery searchMRNDetails) {
		this.context.authenticatedUser();
		final AppUser currentUser = context.authenticatedUser();

		String hierarchy = currentUser.getOffice().getHierarchy();
		String hierarchySearchString = hierarchy + "%";

		final String sql = "Select Concat(" + "'MRN ('" + ",mrn.id,')') as mrnId, mrn.requested_date as requestedDate,"
				+ "(select item_description from b_item_master where id=mrn.item_master_id) as item,"
				+ "(select enum_value from r_enum_value e join b_item_master b on b.item_class = e.enum_id where b.id=mrn.item_master_id and e.enum_name = 'item_class') as itemClass, "
				+ " (select name from m_office where id=mrn.from_office) as fromOffice, (select name from m_office  where id = mrn.to_office) as toOffice,"
				+ " (select id from m_office where id=mrn.from_office) as fromOfficeId, (select id from m_office  where id = mrn.to_office) as toOfficeId,"
				+ "mrn.orderd_quantity as orderdQuantity, mrn.received_quantity as receivedQuantity, mrn.status as status ,mrn.created_date as createdDate"
				+ "  from b_mrn mrn left join m_office mo on mrn.to_office=mo.id where mo.hierarchy like '"
				+ hierarchySearchString + "'";

		MRNDetailsMapper rowMapper = new MRNDetailsMapper();
		StringBuilder sqlBuilder = new StringBuilder(200);
		sqlBuilder.append(sql).append(" and mrn.status = 'Completed' | 'New' | 'Pending' ");

		String sqlSearch = searchMRNDetails.getSqlSearch();
		String extraCriteria = "";
		if (sqlSearch != null) {
			sqlSearch = sqlSearch.trim();
			extraCriteria = " and ((select item_description from b_item_master where id=mrn.item_master_id) like '%"
					+ sqlSearch + "%' OR "
					+ " (select enum_value from r_enum_value e join b_item_master b on b.item_class = e.enum_id where b.id=mrn.item_master_id and e.enum_name = 'item_class') like '%"
					+ sqlSearch + "%' OR " + " (select name from m_office where id=mrn.from_office) like '%" + sqlSearch
					+ "%' OR " + " (select name from m_office where id = mrn.to_office) like '%" + sqlSearch + "%' OR "
					+ " (select id from m_office where id=mrn.from_office) like '%" + sqlSearch + "%' OR "
					+ " (select id from m_office where id = mrn.to_office) like '%" + sqlSearch + "%' OR "
					+ " mrn.orderd_quantity like '%" + sqlSearch + "%' OR " + " mrn.received_quantity like '%"
					+ sqlSearch + "%' OR " + " mrn.requested_date like '%" + sqlSearch + "%' OR "
					+ " mrn.created_date like '%" + sqlSearch + "%' OR " + " mrn.status like '%" + sqlSearch
					+ "%' OR Concat(" + "'MRN ('" + ",mrn.id,')') like '%" + sqlSearch + "%') ";
		}

		sqlBuilder.append(extraCriteria);
		final String itemSql = "Union all Select Concat(" + "'Item Sale ('"
				+ ",its.id,')') as id,its.purchase_date as requestedDate,"
				+ "(select item_description from b_item_master where id=its.item_id) as item, "
				+ "(select enum_value from r_enum_value e join b_item_master b on b.item_class = e.enum_id where b.id=its.item_id and e.enum_name = 'item_class') as itemClass, "
				+ "(select name from m_office where id = its.purchase_from) as fromOffice,(select name from m_office where id = its.purchase_by) as toOffice, "
				+ "(select id from m_office where id = its.purchase_from) as fromOfficeId,(select id from m_office where id = its.purchase_by) as toOfficeId, "
				+ " its.order_quantity as orderdQuantity,"
				+ "its.received_quantity as receivedQuantity, its.status as status, its.created_date as createdDate  from b_itemsale its left join m_office mo on its.purchase_by=mo.id where mo.hierarchy like '"
				+ hierarchySearchString + "'";

		sqlBuilder.append(itemSql).append(" and its.status = 'Completed' | 'New' | 'Pending' ");
		String extraCriteriaForItemsale = "";
		if (sqlSearch != null) {
			sqlSearch = sqlSearch.trim();
			extraCriteriaForItemsale = "and ((select item_description from b_item_master where id=its.item_id) like '%"
					+ sqlSearch + "%' OR "
					+ " (select enum_value from r_enum_value e join b_item_master b on b.item_class = e.enum_id where b.id=its.item_id and e.enum_name = 'item_class') like '%"
					+ sqlSearch + "%' OR " + " (select name from m_office where id=its.purchase_by) like '%" + sqlSearch
					+ "%' OR its.status like  '%" + sqlSearch + "%' OR "
					+ " (select name from m_office where id = its.purchase_from) like '%" + sqlSearch + "%' OR "
					+ " (select id from m_office where id=its.purchase_by) like '%" + sqlSearch + "%' OR "
					+ " (select id from m_office where id = its.purchase_from) like '%" + sqlSearch + "%' OR "
					+ " its.order_quantity like '%" + sqlSearch + "%' OR its.received_quantity like '%" + sqlSearch
					+ "%' OR " + " its.created_date like '%" + sqlSearch + "%' OR " + " its.status like '%" + sqlSearch
					+ "%' OR " + " its.purchase_date like '%" + sqlSearch + "%' OR " + " Concat(" + "'Item Sale ('"
					+ ",its.id,')') like '%" + sqlSearch + "%') ";

		}
		/* sqlBuilder.append(extraCriteriaForItemsale).append("order by 2 desc"); */
		sqlBuilder.append(extraCriteriaForItemsale);

		final String grvsql = "Union all Select Concat(" + "'GRV ('"
				+ ",grv.id,')') as grvId, grv.requested_date as requestedDate,"
				+ "(select item_description from b_item_master where id=grv.item_master_id) as item,"
				+ "(select enum_value from r_enum_value e join b_item_master b on b.item_class = e.enum_id where b.id=grv.item_master_id and e.enum_name = 'item_class') as itemClass, "
				+ " (select name from m_office where id=grv.from_office) as fromOffice, (select name from m_office where id = grv.to_office) as toOffice,"
				+ " (select id from m_office where id=grv.from_office) as fromOfficeId, (select id from m_office where id = grv.to_office) as toOfficeId,"
				+ "grv.orderd_quantity as orderdQuantity, grv.received_quantity as receivedQuantity, grv.status as status, grv.created_date as createdDate from b_grv grv left join m_office mo on grv.to_office=mo.id where mo.hierarchy like '"
				+ hierarchySearchString + "'";

		sqlBuilder.append(grvsql).append(" and grv.status = 'Completed' | 'New' | 'Pending' ");
		String extraCriteriaForGrv = "";
		if (sqlSearch != null) {
			sqlSearch = sqlSearch.trim();
			extraCriteriaForGrv = " and ((select item_description from b_item_master where id=grv.item_master_id) like '%"
					+ sqlSearch + "%' OR "
					+ " (select enum_value from r_enum_value e join b_item_master b on b.item_class = e.enum_id where b.id=grv.item_master_id and e.enum_name = 'item_class')  like '%"
					+ sqlSearch + "%' OR " + " (select name from m_office where id=grv.from_office) like '%" + sqlSearch
					+ "%' OR " + " (select name from m_office where id = grv.to_office) like '%" + sqlSearch + "%' OR "
					+ " (select id from m_office where id = grv.to_office) like '%" + sqlSearch + "%' OR "
					+ " (select id from m_office where id = grv.to_office) like '%" + sqlSearch + "%' OR "
					+ " grv.status like '%" + sqlSearch + "%' OR grv.orderd_quantity like '%" + sqlSearch + "%' OR "
					+ " grv.created_date like '%" + sqlSearch + "%' OR " + " grv.requested_date like '%" + sqlSearch
					+ "%' OR " + " grv.received_quantity like '%" + sqlSearch + "%' OR Concat(" + "'GRV ('"
					+ ",grv.id,')') like '%" + sqlSearch + "%') ";
		}
		sqlBuilder.append(extraCriteriaForGrv).append("order by createdDate desc");

		if (searchMRNDetails.isLimited()) {
			sqlBuilder.append(" limit ").append(searchMRNDetails.getLimit());
		}

		if (searchMRNDetails.isOffset()) {
			sqlBuilder.append(" offset ").append(searchMRNDetails.getOffset());
		}

		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
				new Object[] {}, rowMapper);
	}

	@Override
	public InventoryTransactionHistoryData retriveSingleMovedMrn(Long mrnId) {
		final String sql = "select id as id,ref_id as mrnId,ref_type as refType,(select item_description from b_item_master where id = item_master_id) as itemDescription, serial_number as serialNumber, transaction_date as transactionDate, 'From Office to To Office' movement,(select name from m_office where id = from_office) as source, (select name from m_office where id = to_office) as destination from b_item_history where id = ?";
		MRNDetailsHistoryMapper rowMapper = new MRNDetailsHistoryMapper();
		return jdbcTemplate.queryForObject(sql, rowMapper, new Object[] { mrnId });
	}

	@Override
	public Page<MRNDetailsData> retriveMRNDetailsByOfficeId(SearchSqlQuery searchMRNDetails, Long officeId) {
		this.context.authenticatedUser();
		// final AppUser currentUser = context.authenticatedUser();
		MRNitemDetailsMapper rowMapper = new MRNitemDetailsMapper();

		StringBuilder sqlBuilder = new StringBuilder();
		String sql = "SELECT CONCAT('Item Sale (', its.id, ')') AS id,its.purchase_date AS requestedDate,(SELECT item_description FROM b_item_master WHERE id = its.item_id) AS item,(SELECT enum_value FROM r_enum_value e JOIN b_item_master b ON b.item_class = e.enum_id WHERE b.id = its.item_id AND e.enum_name = 'item_class') AS itemClass,(SELECT name FROM m_office WHERE id = its.purchase_from) AS fromOffice,(SELECT name FROM m_office WHERE id = its.purchase_by) AS toOffice, (SELECT id FROM m_office WHERE id = its.purchase_from) AS fromOfficeId, (SELECT id FROM m_office WHERE id = its.purchase_by) AS toOfficeId,its.order_quantity AS orderdQuantity,its.received_quantity AS receivedQuantity,its.status AS status,its.created_date AS createdDate,its.charge_amount As chargeAmount"
				+ " FROM b_itemsale its LEFT JOIN m_office mo ON its.purchase_by = mo.id "
				+ "WHERE its.status = 'Completed' | 'New' | 'Pending'AND its.type = 'STB_SALE'AND its.purchase_by ="
				+ officeId;

		sqlBuilder.append(sql);
		/*
		 * if (searchMRNDetails.isLimited()) {
		 * sqlBuilder.append(" limit ").append(searchMRNDetails.getLimit()); }
		 * 
		 * if (searchMRNDetails.isOffset()) {
		 * sqlBuilder.append(" offset ").append(searchMRNDetails.getOffset()); }
		 */

		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
				new Object[] {}, rowMapper);
	}

	@Override
	public List<MRNDetailsData> retriveMRNDetails() {
		final String sql = "select mrn.id as mrnId, mrn.requested_date as requestedDate, (select item_description from b_item_master where id=mrn.item_master_id) as item,(select name from m_office where id=mrn.from_office) as fromOffice, (select name from m_office where id = mrn.to_office) as toOffice, mrn.orderd_quantity as orderdQuantity, mrn.received_quantity as receivedQuantity, mrn.status as status from b_mrn mrn";
		MRNDetailsMapper rowMapper = new MRNDetailsMapper();
		return jdbcTemplate.query(sql, rowMapper);
	}

	@Override
	public Collection<MRNDetailsData> retriveMrnIds() {
		final String sql = "select id as mrnId,(select item_description from b_item_master where id=item_master_id) as itemDescription, item_master_id as itemMasterId from b_mrn where orderd_quantity>received_quantity order by requested_date desc";// "select
																																																														// id
																																																														// as
																																																														// mrnId,(select
																																																														// item_description
																																																														// from
																																																														// b_item_master
																																																														// where
																																																														// id=item_master_id)
																																																														// as
																																																														// itemDescription,
																																																														// item_master_id
																																																														// as
																																																														// itemMasterId
																																																														// from
																																																														// b_mrn
																																																														// order
																																																														// by
																																																														// requested_date
																																																														// desc";
		MRNDetailsMrnIDsMapper rowMapper = new MRNDetailsMrnIDsMapper();
		return jdbcTemplate.query(sql, rowMapper);
	}

	@Override
	public List<String> retriveSerialNumbers(final Long mrnId) {
		try {

			final String sql = "select idt.serial_no as serialNumber from b_mrn ots left join b_item_detail idt on idt.item_master_id = ots.item_master_id"
					+ "  where ots.id = ? and idt.client_id is null and idt.office_id=ots.from_office";

			final MRNDetailsSerialMapper rowMapper = new MRNDetailsSerialMapper();
			return jdbcTemplate.query(sql, rowMapper, new Object[] { mrnId });

		} catch (EmptyResultDataAccessException accessException) {
			return null;
		}
	}

	@Override
	public String retriveSerialNumbersFromMrn(final String serialNumber, final Long mrnId) {
		try {

			final String sql = "select idt.serial_no as serialNumber from b_mrn ots left join b_item_detail idt on idt.item_master_id = ots.item_master_id"
					+ "  where ots.id = ? and idt.client_id is null and idt.office_id=ots.from_office and idt.serial_no=?";

			final MRNDetailsSerialMapper rowMapper = new MRNDetailsSerialMapper();
			return jdbcTemplate.queryForObject(sql, rowMapper, new Object[] { mrnId, serialNumber });

		} catch (EmptyResultDataAccessException accessException) {
			return null;
		}
	}

	@Override
	public Page<InventoryTransactionHistoryData> retriveHistory(SearchSqlQuery searchItemHistory) {

		/*
		 * final String sql =
		 * 
		 * "  SQL_CALC_FOUND_ROWS  * FROM ( " +
		 * " Select 'Item Detail' as refType, (SELECT item_description FROM b_item_master  "
		 * +
		 * " WHERE id = item_master_id) AS itemDescription, serial_no  AS serialNumber, created_date AS transactionDate,"
		 * +
		 * " 'From Supplier to To Office' AS movement, (SELECT b.supplier_description FROM b_grn a, b_supplier b "
		 * +
		 * " WHERE a.supplier_id = b.id AND a.id = grn_id ) AS source, (SELECT name FROM m_office WHERE id = location_id) AS destination"
		 * + " from b_item_detail " +
		 * 
		 * " UNION ALL " +
		 * " SELECT ref_type AS refType, (SELECT item_description FROM b_item_master WHERE id = item_master_id)"
		 * +
		 * " AS itemDescription,serial_number AS serialNumber,transaction_date AS transactionDate,'From Office to To Office' movement,"
		 * +
		 * " (SELECT name FROM m_office WHERE id = from_office) AS source, (SELECT name FROM m_office WHERE id = to_office) AS destination"
		 * + " FROM b_item_history WHERE ref_type = 'MRN' " +
		 * 
		 * " UNION ALL" +
		 * " SELECT ref_type AS refType, (SELECT item_description FROM b_item_master WHERE id = item_master_id) AS itemDescription, serial_number AS serialNumber,"
		 * +
		 * " transaction_date AS transactionDate, 'From Office to To Client' movement, (SELECT name FROM m_office WHERE id = from_office) AS source,"
		 * +
		 * " (SELECT concat(id, ' - ', display_name) FROM m_client WHERE id = to_office) AS destination "
		 * + " FROM b_item_history WHERE ref_type = 'Allocation'" +
		 * 
		 * " UNION ALL " +
		 * " SELECT ref_type AS refType,(SELECT item_description FROM b_item_master WHERE id = item_master_id) AS itemDescription,serial_number AS serialNumber,"
		 * +
		 * " transaction_date AS transactionDate, 'From Client to To Office' movement, (SELECT concat(id, ' - ', display_name) FROM m_client "
		 * +
		 * " WHERE id = from_office) AS source, (SELECT name FROM m_office WHERE id = to_office) AS destination "
		 * + " FROM b_item_history WHERE ref_type = 'De Allocation') a ";
		 */
		final String sql = "  SQL_CALC_FOUND_ROWS  * FROM (" + " Select 'Item Detail' as refType,"
				+ " im.item_description AS itemDescription,id.serial_no  AS serialNumber,"
				+ " id.created_date AS transactionDate,'From Supplier to To Office' AS movement,"
				+ " sup.supplier_description AS source,name as destination" + " from b_item_detail id"
				+ " left join b_item_master im on id.item_master_id=im.id" + " left join b_grn gr on gr.id = id.grn_id"
				+ " left join b_supplier sup on  gr.supplier_id = sup.id"
				+ " left join m_office ofi on id.location_id=ofi.id" + " UNION ALL "
				+ " SELECT ref_type AS refType,im.item_description AS itemDescription,"
				+ " ih.serial_number AS serialNumber,ih.transaction_date AS transactionDate,"
				+ " 'From Office to To Office' movement,ofi1.name AS source,"
				+ " ofi2.name AS destination FROM b_item_history ih"
				+ " left join b_item_master im on ih.item_master_id=im.id"
				+ " left join m_office ofi1 on ih.from_office=ofi1.id"
				+ " left join m_office ofi2 on ih.to_office=ofi2.id" + " WHERE ref_type = 'MRN'" + " UNION ALL "
				+ " SELECT ref_type AS refType,im.item_description AS itemDescription,"
				+ " ih.serial_number AS serialNumber,ih.transaction_date AS transactionDate,"
				+ " 'From Office to To Office' movement,ofi1.name AS source,"
				+ " ofi2.name AS destination FROM b_item_history ih"
				+ " left join b_item_master im on ih.item_master_id=im.id"
				+ " left join m_office ofi1 on ih.from_office=ofi1.id"
				+ " left join m_office ofi2 on ih.to_office=ofi2.id" + " WHERE ref_type = 'GRV'" + " UNION ALL "
				+ " SELECT ref_type AS refType,im.item_description AS itemDescription,"
				+ " ih.serial_number AS serialNumber,ih.transaction_date AS transactionDate,"
				+ " 'From Office to To Client' movement,ofi.name AS source,"
				+ " concat(cli.id, ' - ', display_name) AS destination FROM b_item_history ih"
				+ " left join b_item_master im on ih.item_master_id=im.id"
				+ " left join m_office ofi on ih.from_office=ofi.id" + " left join m_client cli on ih.to_office=cli.id"
				+ " WHERE ref_type = 'Allocation'" + " UNION ALL "
				+ " SELECT ref_type AS refType,im.item_description AS itemDescription,"
				+ " serial_number AS serialNumber,transaction_date AS transactionDate,"
				+ " 'From Client to To Office' movement,concat(cli.id, ' - ', display_name) AS source,"
				+ " ofi.name AS destination FROM b_item_history  ih"
				+ " left join b_item_master im on ih.item_master_id=im.id"
				+ " left join m_office ofi on ih.from_office=ofi.id" + " left join m_client cli on ih.to_office=cli.id"
				+ " WHERE ref_type = 'De Allocation' ) a ";

		MRNDetailsHistoryMapper detailsHistoryMapper = new MRNDetailsHistoryMapper();

		final StringBuilder sqlBuilder = new StringBuilder(200);
		sqlBuilder.append("select ").append(sql);

		String sqlSearch = searchItemHistory.getSqlSearch();
		String extraCriteria = "";
		if (sqlSearch != null) {
			sqlSearch = sqlSearch.trim();
			extraCriteria = " where serialNumber LIKE '%" + sqlSearch + "%' order by transactionDate";
		}
		sqlBuilder.append(extraCriteria);
		if (searchItemHistory.isLimited()) {
			sqlBuilder.append(" limit ").append(searchItemHistory.getLimit());
		}

		if (searchItemHistory.isOffset()) {
			sqlBuilder.append(" offset ").append(searchItemHistory.getOffset());
		}

		return this.paginationHelper2.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
				new Object[] {}, detailsHistoryMapper);
	}

	@Override
	public MRNDetailsData retriveSingleMrnDetail(final Long mrnId) {

		final String sql = "select mrn.id as mrnId, mrn.requested_date as requestedDate, (select item_description from b_item_master where id=mrn.item_master_id) as item,"
				+ "  (select name from m_office where id=mrn.from_office) as fromOffice, (select name from m_office where id = mrn.to_office) as toOffice,"
				+ "   mrn.orderd_quantity as orderdQuantity, mrn.received_quantity as receivedQuantity, mrn.status as status from b_mrn mrn where mrn.id=?";
		final MRNDetailsMapper rowMapper = new MRNDetailsMapper();
		return jdbcTemplate.queryForObject(sql, rowMapper, new Object[] { mrnId });
	}

	@Override
	public List<String> retriveSerialNumbersForItems(final Long itemsaleId, final String serialNumber) {
		try {
			String sql = " select idt.serial_no as serialNumber from b_itemsale bi left join b_item_detail idt on"
					+ " idt.item_master_id = bi.item_id where bi.id = ? and idt.client_id is null and idt.office_id=bi.purchase_from";

			if (serialNumber != null) {
				sql += " and idt.serial_no='" + serialNumber + "'";
			}
			final MRNDetailsSerialMapper rowMapper = new MRNDetailsSerialMapper();
			return jdbcTemplate.query(sql, rowMapper, new Object[] { itemsaleId });

		} catch (EmptyResultDataAccessException ex) {
			return null;
		}
	}

	@Override
	public List<String> retriveCartonNumber(Long mrnId) {
		try {

			/*
			 * final String sql =
			 * "select idt.cartoon_no as cartoonNumber from b_mrn ots left join b_item_detail idt on idt.item_master_id = ots.item_master_id"
			 * +
			 * "  where ots.id = ? and idt.client_id is null and idt.office_id=ots.from_office"
			 * ;
			 */

			final String sql = "select a.from_office,b.serial_no,b.cartoon_no as cartoonNumber from b_mrn a left join b_item_detail b on a.item_master_id=b.item_master_id "
					+ "where b.office_id = a.from_office and b.client_id is null and a.id =  ?";

			final MRNDetailsSerialMapper1 rowMapper = new MRNDetailsSerialMapper1();
			return jdbcTemplate.query(sql, rowMapper, new Object[] { mrnId });

		} catch (EmptyResultDataAccessException accessException) {
			return null;
		}
	}

	private final class MRNDetailsSerialMapper1 implements RowMapper<String> {

		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getString("cartoonNumber");

		}
	}

	@Override
	public List<String> retriveCartoonNumbersForItems(final Long itemsaleId, final String cartoonNumber) {
		try {
			String sql = " select idt.cartoon_no as cartoonNumber from b_itemsale bi left join b_item_detail idt on"
					+ " idt.item_master_id = bi.item_id where bi.id = ? and idt.client_id is null and idt.office_id=bi.purchase_from";

			if (cartoonNumber != null) {
				sql += " and idt.cartoon_no='" + cartoonNumber + "'";
			}
			final MRNDetailsCartoonMappers rowMapper = new MRNDetailsCartoonMappers();
			return jdbcTemplate.query(sql, rowMapper, new Object[] { itemsaleId });

		} catch (EmptyResultDataAccessException ex) {
			return null;
		}
	}

	private final class MRNDetailsCartoonMappers implements RowMapper<String> {

		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getString("cartoonNumber");

		}
	}

}
