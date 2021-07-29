package org.mifosplatform.logistics.grn.service;

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
import org.mifosplatform.logistics.itemdetails.data.InventoryGrnData;
import org.mifosplatform.logistics.itemdetails.domain.InventoryGrnRepository;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class GrnReadPlatformServiceImp implements GrnReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final InventoryGrnRepository inventoryGrnRepository;
	private final PlatformSecurityContext context;
	private final PaginationHelper<InventoryGrnData> paginationHelper = new PaginationHelper<InventoryGrnData>();

	@Autowired
	public GrnReadPlatformServiceImp(final TenantAwareRoutingDataSource dataSource,
			InventoryGrnRepository inventoryGrnRepository, final PlatformSecurityContext context) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.inventoryGrnRepository = inventoryGrnRepository;
		this.context = context;

	}

	@Override
	public Collection<InventoryGrnData> retriveGrnDetails() {

		GrnMapperForDetails grn = new GrnMapperForDetails();
		String sql = "select g.id as id, f.name as officeName, g.purchase_date as purchaseDate, g.supplier_id as supplierId, g.item_master_id as itemMasterId, g.orderd_quantity as orderdQuantity, g.received_quantity as receivedQuantity, im.item_description as itemDescription, s.supplier_description as supplierDescription from b_grn g left outer join m_office f on g.office_id=f.id left outer join b_item_master im on g.item_master_id = im.id left outer join b_supplier s on g.supplier_id=s.id";
		return jdbcTemplate.query(sql, grn, new Object[] {});
	}

	public boolean validateForExist(final Long grnId) {

		boolean exist = inventoryGrnRepository.exists(grnId);
		if (!exist) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * @Override public Page<InventoryGrnData> retriveGrnDetails(Long limit, Long
	 * offset) { GrnMapperForDetails grn = new GrnMapperForDetails(); String sql =
	 * "select SQL_CALC_FOUND_ROWS g.id as id, f.name as officeName, g.purchase_date as purchaseDate, g.supplier_id as supplierId, g.item_master_id as itemMasterId, g.orderd_quantity as orderdQuantity, g.received_quantity as receivedQuantity, im.item_description as itemDescription, s.supplier_description as supplierDescription from b_grn g left outer join m_office f on g.office_id=f.id left outer join b_item_master im on g.item_master_id = im.id left outer join b_supplier s on g.supplier_id=s.id limit ? offset ?"
	 * ; return this.paginationHelper.fetchPage(this.jdbcTemplate,
	 * "SELECT FOUND_ROWS()",sql, new Object[] {limit,offset}, grn); }
	 */

	public Page<InventoryGrnData> retriveGrnDetails(SearchSqlQuery searchGrn) {

		final AppUser user = this.context.authenticatedUser();
		final String hierarchy = user.getOffice().getHierarchy();
		final String hierarchySearchString = hierarchy + "%";

		GrnMapperForDetails grn = new GrnMapperForDetails();

		String sql = "SQL_CALC_FOUND_ROWS g.id as id, f.id as officeId, f.name as officeName, g.purchase_date as purchaseDate,  g.item_amount as itemAmount,"
				+ "g.supplier_id as supplierId, g.item_master_id as itemMasterId, g.orderd_quantity as orderdQuantity, "
				+ "g.received_quantity as receivedQuantity, g.po_no as purchaseNo, im.item_description as itemDescription, "
				+ "s.supplier_description as supplierDescription, stock_quantity as availableQuantity "
				+ "from b_grn g left outer join m_office f on g.office_id=f.id "
				+ "left outer join b_item_master im on g.item_master_id = im.id "
				+ "left outer join b_supplier s on g.supplier_id=s.id ";

		StringBuilder sqlBuilder = new StringBuilder(200);
		sqlBuilder.append("select ");
		sqlBuilder.append(sql);
		sqlBuilder.append(" where g.id IS NOT NULL and f.hierarchy like ? ");

		String sqlSearch = searchGrn.getSqlSearch();
		String extraCriteria = "";
		if (sqlSearch != null) {
			sqlSearch = sqlSearch.trim();
			extraCriteria = " and (f.name like '%" + sqlSearch + "%' OR" + " g.id like '%" + sqlSearch + "%' OR"
					+ " g.purchase_date like '%" + sqlSearch + "%' OR" + " s.supplier_description like '%" + sqlSearch
					+ "%' OR" + " im.item_description like '%" + sqlSearch + "%')";
		}
		sqlBuilder.append(extraCriteria);

		/*
		 * if (StringUtils.isNotBlank(extraCriteria)) {
		 * sqlBuilder.append(extraCriteria); }
		 */

		sqlBuilder.append(" ORDER BY g.id desc");

		if (searchGrn.isLimited()) {
			sqlBuilder.append(" limit ").append(searchGrn.getLimit());
		}

		if (searchGrn.isOffset()) {
			sqlBuilder.append(" offset ").append(searchGrn.getOffset());
		}

		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
				new Object[] { hierarchySearchString }, grn);
	}

	@Override
	public InventoryGrnData retriveGrnDetailTemplate(final Long grnId) {

		try {
			GrnMapper grn = new GrnMapper();

			final String sql = "select g.id as id, g.purchase_date as purchaseDate, g.supplier_id as supplierId, g.item_master_id as itemMasterId, g.item_amount as itemAmount, "
					+ "g.po_no as purchaseNo,g.item_amount as itemAmount ,g.office_id as officeId,g.orderd_quantity as orderdQuantity, g.received_quantity as receivedQuantity,"
					+ " im.item_description as itemDescription,im.item_code as itemcode, im.units as units, s.supplier_description as supplierDescription,f.name as officeName from b_grn g left outer join b_item_master im "
					+ " on g.item_master_id = im.id left outer join b_supplier s on g.supplier_id = s.id left outer join m_office f ON g.office_id = f.id where g.id = ?";
			return jdbcTemplate.queryForObject(sql, grn, new Object[] { grnId });

		} catch (EmptyResultDataAccessException accessException) {
			return null;
		}
	}

	@Override
	public Collection<InventoryGrnData> retriveGrnIds() {
		GrnIds rowMapper = new GrnIds();
		String sql = "select id,(select item_description from b_item_master where id=item_master_id) as itemDescription from b_grn "
				+ "  where orderd_quantity>received_quantity order by id desc";
		return jdbcTemplate.query(sql, rowMapper);
	}

	private class GrnMapperForDetails implements RowMapper<InventoryGrnData> {

		@Override
		public InventoryGrnData mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			LocalDate purchaseDate = JdbcSupport.getLocalDate(rs, "purchaseDate");
			Long supplierId = rs.getLong("supplierId");
			Long itemMasterId = rs.getLong("itemMasterId");
			Long orderedQuantity = rs.getLong("orderdQuantity");
			Long receivedQuantity = rs.getLong("receivedQuantity");
			String purchaseNo = rs.getString("purchaseNo");
			String itemDescription = rs.getString("itemDescription");
			String supplierName = rs.getString("supplierDescription");
			String officeName = rs.getString("officeName");
			Long availableQuantity = rs.getLong("availableQuantity");
			Long officeId = rs.getLong("officeId");
			Long itemAmount = rs.getLong("itemAmount");
			InventoryGrnData inventoryGrnData = new InventoryGrnData(id, purchaseDate, supplierId, itemMasterId,
					orderedQuantity, receivedQuantity, itemDescription, supplierName, officeName, purchaseNo,
					availableQuantity, itemAmount);
			inventoryGrnData.setOfficeId(officeId);
			return inventoryGrnData;
		}

	}

	private class GrnMapper implements RowMapper<InventoryGrnData> {

		@Override
		public InventoryGrnData mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			LocalDate purchaseDate = JdbcSupport.getLocalDate(rs, "purchaseDate");
			Long supplierId = rs.getLong("supplierId");
			Long itemMasterId = rs.getLong("itemMasterId");
			Long orderedQuantity = rs.getLong("orderdQuantity");
			Long receivedQuantity = rs.getLong("receivedQuantity");
			String itemDescription = rs.getString("itemDescription");
			String supplierName = rs.getString("supplierDescription");
			String purchaseNo = rs.getString("purchaseNo");
			Long officeId = rs.getLong("officeId");
			String units = rs.getString("units");
			String officeName = rs.getString("officeName");
			String itemcode = rs.getString("itemcode");
			InventoryGrnData inventoryGrnData = new InventoryGrnData(id, purchaseDate, itemMasterId, orderedQuantity,
					receivedQuantity, itemDescription, supplierName, purchaseNo, supplierId, officeId, units,
					officeName);
			inventoryGrnData.setItemcode(itemcode);
			return inventoryGrnData;
		}

	}

	private class GrnIds implements RowMapper<InventoryGrnData> {

		@Override
		public InventoryGrnData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String itemDescription = rs.getString("itemDescription");
			return new InventoryGrnData(id, itemDescription);
		}

	}

	@Override
	public Collection<InventoryGrnData> retriveGrnIdswithItemId(final Long itemId) {
		GrnIdswithItemMapper rowMapper = new GrnIdswithItemMapper();
		String sql = "SELECT bg.id as id,bm.id as itemId," + "bm.item_description as itemDescription "
				+ " FROM b_grn bg,b_item_master bm WHERE bg.item_master_id = bm.id and bm.id = " + itemId;
		return jdbcTemplate.query(sql, rowMapper);
	}

	private class GrnIdswithItemMapper implements RowMapper<InventoryGrnData> {

		@Override
		public InventoryGrnData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long itemId = rs.getLong("itemId");
			final String itemDescription = rs.getString("itemDescription");
			return new InventoryGrnData(id, itemId, itemDescription);
		}

	}

	@Override
	public List<InventoryGrnData> retriveGrnDetailsByofficeIdAndOrderStatus(final Long officeId,
			final Integer orderStatus) {
		// TODO Auto-generated method stub
		GrnDetailsByofficeIdAndOrderStatusMapper rowMapper = new GrnDetailsByofficeIdAndOrderStatusMapper();

		String sql = "select bg.id as id,bg.purchase_date as purchaseDate,bg.supplier_id as supplierId,bg.item_master_id as itemMasterId,bg.orderd_quantity as orderdQuantity,"
				+ " bg.received_quantity as receivedQuantity, bg.stock_quantity as stockQuantity,bg.office_id as officeId, bg.po_no as purchaseNo, bg.item_amount as itemAmount, bg.order_status as orderStatus "
				+ " from b_grn AS bg where bg.office_id =" + officeId + " AND bg.order_status =" + orderStatus;
		return jdbcTemplate.query(sql, rowMapper);
	}

	private class GrnDetailsByofficeIdAndOrderStatusMapper implements RowMapper<InventoryGrnData> {

		@Override
		public InventoryGrnData mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			LocalDate purchaseDate = JdbcSupport.getLocalDate(rs, "purchaseDate");
			Long supplierId = rs.getLong("supplierId");
			Long itemMasterId = rs.getLong("itemMasterId");
			Long orderedQuantity = rs.getLong("orderdQuantity");
			Long receivedQuantity = rs.getLong("receivedQuantity");
			Long stockQuantity = rs.getLong("stockQuantity");
			String purchaseNo = rs.getString("purchaseNo");
			Long officeId = rs.getLong("officeId");
			Long itemAmount = rs.getLong("itemAmount");
			Integer orderStatus = rs.getInt("orderStatus");
			return new InventoryGrnData(id, purchaseDate, supplierId, itemMasterId, orderedQuantity, receivedQuantity,
					stockQuantity, purchaseNo, officeId, itemAmount, orderStatus);
		}

	}

	@Override
	public List<InventoryGrnData> retriveGrnDetailsByofficeIdAndItemType(Long officeId, Long itemType) {
		// TODO Auto-generated method stub
		GrnDetailsByofficeIdAndItemType rowMapper = new GrnDetailsByofficeIdAndItemType();

		String sql = "select bg.id as id,bg.purchase_date as purchaseDate,bg.supplier_id as supplierId,bg.item_master_id as itemMasterId,bg.orderd_quantity as orderdQuantity,"
				+ " bg.received_quantity as receivedQuantity, bg.stock_quantity as stockQuantity,bg.office_id as officeId, bg.po_no as purchaseNo, bg.item_amount as itemAmount, bg.order_status as orderStatus, m.item_class as itemType "
				+ " from b_grn AS bg inner join b_item_master m on bg.item_master_id=m.id where m.item_class="
				+ itemType + " AND bg.office_id =" + officeId;
		return jdbcTemplate.query(sql, rowMapper);
	}

	private class GrnDetailsByofficeIdAndItemType implements RowMapper<InventoryGrnData> {

		@Override
		public InventoryGrnData mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			LocalDate purchaseDate = JdbcSupport.getLocalDate(rs, "purchaseDate");
			Long supplierId = rs.getLong("supplierId");
			Long itemMasterId = rs.getLong("itemMasterId");
			Long orderedQuantity = rs.getLong("orderdQuantity");
			Long receivedQuantity = rs.getLong("receivedQuantity");
			Long stockQuantity = rs.getLong("stockQuantity");
			String purchaseNo = rs.getString("purchaseNo");
			Long officeId = rs.getLong("officeId");
			Long itemAmount = rs.getLong("itemAmount");
			Integer orderStatus = rs.getInt("orderStatus");
			Long itemType = rs.getLong("itemType");
			return new InventoryGrnData(id, purchaseDate, supplierId, itemMasterId, orderedQuantity, receivedQuantity,
					stockQuantity, purchaseNo, officeId, itemAmount, orderStatus, itemType);
		}

	}

	@Override
	public InventoryGrnData retriveAllGrnDetailsByPoNo(String purchaseNo) {
		// TODO Auto-generated method stub
		GrnDetailsByPoNo rowMapper = new GrnDetailsByPoNo();

		String sql = "SELECT bg.id as id,bg.purchase_date as purchaseDate,bg.supplier_id as supplierId,"
				+ "bg.item_master_id as itemMasterId,bg.orderd_quantity as orderdQuantity, bg.received_quantity as receivedQuantity, "
				+ " bg.stock_quantity as stockQuantity,bg.office_id as officeId, bg.po_no as purchaseNo, bg.item_amount as itemAmount, bg.order_status as orderStatus"
				+ " from b_grn bg where bg.po_no='" + purchaseNo + "'";
		return jdbcTemplate.queryForObject(sql, rowMapper);
	}

	private class GrnDetailsByPoNo implements RowMapper<InventoryGrnData> {

		@Override
		public InventoryGrnData mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			LocalDate purchaseDate = JdbcSupport.getLocalDate(rs, "purchaseDate");
			Long supplierId = rs.getLong("supplierId");
			Long itemMasterId = rs.getLong("itemMasterId");
			Long orderedQuantity = rs.getLong("orderdQuantity");
			Long receivedQuantity = rs.getLong("receivedQuantity");
			Long stockQuantity = rs.getLong("stockQuantity");
			String purchaseNo = rs.getString("purchaseNo");
			Long officeId = rs.getLong("officeId");
			Long itemAmount = rs.getLong("itemAmount");
			Integer orderStatus = rs.getInt("orderStatus");
			return new InventoryGrnData(id, purchaseDate, supplierId, itemMasterId, orderedQuantity, receivedQuantity,
					stockQuantity, purchaseNo, officeId, itemAmount, orderStatus, null);
		}

	}

	@Override
	public InventoryGrnData retriveGrnIdByPoNo(String poNo) {
		// TODO Auto-generated method stub
		GrnIdByPoNo rowMapper = new GrnIdByPoNo();

		String sql = "SELECT bg.id as grnId" + " from b_grn bg where bg.po_no='" + poNo + "' and bg.order_status=0";
		return jdbcTemplate.queryForObject(sql, rowMapper);
	}

	private class GrnIdByPoNo implements RowMapper<InventoryGrnData> {

		@Override
		public InventoryGrnData mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long grnId = rs.getLong("grnId");
			return new InventoryGrnData(grnId, null, null, null, null, null, null, null, null, null, null);
		}

	}

	@Override
	public List<String> getPoNoListOnKeyStroke(String purchaseNo) {
		// TODO Auto-generated method stub
		PoNoListMapper rowMapper = new PoNoListMapper();

		String sql = "SELECT bg.po_no as purchaseNo from b_grn bg where bg.po_no like '%"+purchaseNo+"%'";
		return jdbcTemplate.query(sql, rowMapper);
	}
	private class PoNoListMapper implements RowMapper<String> {

		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {

		
			String purchaseNo = rs.getString("purchaseNo");
			return purchaseNo;
		}

	}

}
