package org.mifosplatform.logistics.grv.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.grv.data.GRVData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class GRVReadPlatformServiceImpl implements GRVReadPlatformService{
	
	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;
	
	@Autowired
	public GRVReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource) {
		
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	

	
	@Override
	public List<GRVData> retriveGrvIds() {
		final String sql = "select id as grvId,(select item_description from b_item_master where id=item_master_id) as itemDescription, item_master_id as itemMasterId from b_grv where orderd_quantity>received_quantity order by requested_date desc";//"select id as mrnId,(select item_description from b_item_master where id=item_master_id) as itemDescription, item_master_id as itemMasterId from b_mrn order by requested_date desc";
		GRVIDsMapper rowMapper = new GRVIDsMapper();
		return jdbcTemplate.query(sql,rowMapper);
	}
	
	
	
	@Override
	public List<String> retriveSerialNumbersForItems(final Long grvId) {
		try{
			
			String sql = "select idt.serial_no as serialNumber from b_grv grv left join b_item_detail idt on idt.item_master_id = grv.item_master_id" +
					     "  where grv.id = ? and idt.client_id is null and idt.quality<>'Good' and idt.office_id=grv.from_office";
		
		
		final GRVSerialMapper rowMapper = new GRVSerialMapper();
		return jdbcTemplate.query(sql,rowMapper,new Object[]{ grvId });

		}catch(EmptyResultDataAccessException ex){
			return null;
		}
	}
	
	@Override
	public String retriveSerialNumbersFromGrv(final String serialNumber,final Long grvId) {
		try{
			
			String sql = "select idt.serial_no as serialNumber from b_grv grv left join b_item_detail idt on idt.item_master_id = grv.item_master_id" +
						 " where grv.id = ? and idt.client_id is null and idt.office_id=grv.from_office and idt.serial_no=?";
		
		
		final GRVSerialMapper rowMapper = new GRVSerialMapper();
		return jdbcTemplate.queryForObject(sql,rowMapper,new Object[]{grvId,serialNumber});

		}catch(EmptyResultDataAccessException ex){
			return null;
		}
	}
	
	@Override
	public GRVData retriveSingleGrvData(Long grvId) {
		final String sql = "select grv.id as grvId, grv.requested_date as requestedDate, (select item_description from b_item_master where id=grv.item_master_id) as item," +
		          "  (select name from m_office where id=grv.from_office) as fromOffice, (select name from m_office where id = grv.to_office) as toOffice," +
		          "   grv.orderd_quantity as orderdQuantity, grv.received_quantity as receivedQuantity, grv.status as status from b_grv grv where grv.id=?";
		final GRVIDMapper rowMapper = new GRVIDMapper();
		return jdbcTemplate.queryForObject(sql,rowMapper,new Object[]{grvId});
		}
	
	private final class GRVIDMapper implements RowMapper<GRVData>{
		@Override
		public GRVData mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		final String id = rs.getString("grvId");
		final LocalDate requestedDate =JdbcSupport.getLocalDate(rs,"requestedDate");
		final String fromOffice = rs.getString("fromOffice");
		final String toOffice = rs.getString("toOffice");
		final Long orderdQuantity = rs.getLong("orderdQuantity");
		final Long receivedQuantity = rs.getLong("receivedQuantity");
		final String status = rs.getString("status");
		final String itemDescription = rs.getString("item");
		/*final String notes = rs.getString("notes");*/
		
		return new GRVData(id, requestedDate, fromOffice, toOffice, orderdQuantity, receivedQuantity, status,itemDescription/*, notes*/);
		}
	}
	
	
	private final class GRVIDsMapper implements RowMapper<GRVData>{
		@Override
		public GRVData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long grvId = rs.getLong("grvId");
			final Long itemMasterId = rs.getLong("itemMasterId");
			final String itemDescription = rs.getString("itemDescription");
			return new GRVData(grvId,itemDescription,itemMasterId,null);
		}
	}
  
	
    private final class GRVSerialMapper implements RowMapper<String>{
		
		@Override
		public String mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			return 	rs.getString("serialNumber");
			
		}
	}
		
}
  
	


	


