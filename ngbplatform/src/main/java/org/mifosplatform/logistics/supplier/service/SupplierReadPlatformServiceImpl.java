package org.mifosplatform.logistics.supplier.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.logistics.supplier.data.SupplierData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class SupplierReadPlatformServiceImpl implements SupplierReadPlatformService {
	
	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<SupplierData> paginationHelper = new PaginationHelper<SupplierData>();
	
	@Autowired
	public SupplierReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	private class SupplierMapper implements RowMapper<SupplierData> {
		@Override
		public SupplierData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			final String supplierCode = rs.getString("supplierCode");
			final String supplierDescription = rs.getString("supplierDescription");
			final String supplierAddress = rs.getString("supplierAddress");
			return new SupplierData(id, supplierCode, supplierDescription, supplierAddress);
		}
		
		public String schema() {
			
			return " SQL_CALC_FOUND_ROWS id as id, supplier_code as supplierCode, "
					+ "supplier_description as supplierDescription, supplier_address as supplierAddress "
						 + " from b_supplier ";
			
		}
	}

	@Override
	public List<SupplierData> retrieveSupplier() {
		SupplierMapper supplierMapper = new SupplierMapper();
		String sql = "select id as id, supplier_code as supplierCode, supplier_description as supplierDescription, supplier_address as supplierAddress "
						 + " from b_supplier";
		return jdbcTemplate.query(sql, supplierMapper,new Object[]{});
	}
	
	@Override
	public Page<SupplierData> retrieveSupplier(SearchSqlQuery searchSupplier) {
		SupplierMapper supplierMapper = new SupplierMapper();
		
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(supplierMapper.schema());
        sqlBuilder.append(" where id IS NOT NULL ");
        
        String sqlSearch = searchSupplier.getSqlSearch();
        String extraCriteria = "";
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ " supplier_code like '%"+sqlSearch+"%' OR"
	    			+ " supplier_description like '%"+sqlSearch+"%' OR"
	    			+ " supplier_address like '%"+sqlSearch+"%')";
	    }
            sqlBuilder.append(extraCriteria);
        
        /*if (StringUtils.isNotBlank(extraCriteria)) {
            sqlBuilder.append(extraCriteria);
        }*/


        if (searchSupplier.isLimited()) {
            sqlBuilder.append(" limit ").append(searchSupplier.getLimit());
        }

        if (searchSupplier.isOffset()) {
            sqlBuilder.append(" offset ").append(searchSupplier.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, supplierMapper);
	}
	
	@Override
	public List<SupplierData> retrieveSupplier(Long supplierId) {
		
		SupplierMapper supplierMapper = new SupplierMapper();
		String sql = "select id as id, supplier_code as supplierCode, supplier_description as supplierDescription, supplier_address as supplierAddress "
						 + " from b_supplier where id=?";
		return jdbcTemplate.query(sql, supplierMapper,new Object[]{supplierId});
	}

	@Override
	public List<SupplierData> retrieveSupplierForDropdown(String supplierDescription) {
		
		SupplierMapper supplierMapper = new SupplierMapper();
		String supplierDescriptionString = supplierDescription+'%'; 
		String sql = "select id as id, supplier_code as supplierCode, supplier_description as supplierDescription, supplier_address as supplierAddress "
						 + " from b_supplier where supplier_description like ? order by supplierCode ";
		return jdbcTemplate.query(sql, supplierMapper,new Object[]{supplierDescriptionString});
		
		
	}
	
}
