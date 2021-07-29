package org.mifosplatform.portfolio.association.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.portfolio.association.data.AssociationData;
import org.mifosplatform.portfolio.association.data.HardwareAssociationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class HardwareAssociationReadplatformServiceImpl implements HardwareAssociationReadplatformService{
	
	
	 private final JdbcTemplate jdbcTemplate;
	 @SuppressWarnings("unused")
	private final ConfigurationRepository configurationRepository;
	
	  
	    @Autowired
	    public HardwareAssociationReadplatformServiceImpl(final ConfigurationRepository configurationRepository, 
	    		final TenantAwareRoutingDataSource dataSource)
	    {
	        this.jdbcTemplate = new JdbcTemplate(dataSource);
	        this.configurationRepository=configurationRepository;
	    }

		/*@Override
		public List<HardwareAssociationData> retrieveClientHardwareDetails(Long clientId)
		{

              try
              {
            	  String sql=null;
            	  Configuration configurationProperty=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_DEVICE_AGREMENT_TYPE);
            	  HarderwareMapper mapper = new HarderwareMapper();
            	  
            	  if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_SALE)){
			       sql = "select " + mapper.schema();
			      
            	  }else if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)){
            		  
            		  sql = "select " + mapper.ownDeviceSchema();
            	  }
			      return this.jdbcTemplate.query(sql, mapper, new Object[] {clientId});

		}catch(EmptyResultDataAccessException accessException){
			return null;
		}
		}*/
	/*	private static final class HarderwareMapper implements RowMapper<HardwareAssociationData> {

			public String schema() {
				return "  a.id AS id, a.serial_no AS serialNo  FROM b_allocation a  WHERE    NOT EXISTS (SELECT * FROM  b_association s" +
						" WHERE  s.hw_serial_no=a.serial_no) and a.client_id=?";

			}
			
			public String ownDeviceSchema() {
				return "  o.id AS id, o.serial_number  AS serialNo FROM  b_owned_hardware o WHERE NOT EXISTS (SELECT *" +
						" FROM b_association s WHERE s.hw_serial_no = o.serial_number ) AND o.client_id = ?";

			}

			@Override
			public HardwareAssociationData mapRow(final ResultSet rs,
					@SuppressWarnings("unused") final int rowNum)
					throws SQLException {
				Long id = rs.getLong("id");
				String serialNo = rs.getString("serialNo");
				
				HardwareAssociationData associationData=new HardwareAssociationData(id,serialNo,null,null,null);
				return associationData; 
			}
		}*/
		@Override
		public List<HardwareAssociationData> retrieveClientAllocatedPlan(Long clientId,String itemCode) {
            try
            {

          	  PlanMapper mapper = new PlanMapper();

			String sql = "select " + mapper.schema();
			return this.jdbcTemplate.query(sql, mapper, new Object[] {clientId,itemCode});

		}catch(EmptyResultDataAccessException accessException){
			return null;
		}
		}
		private static final class PlanMapper implements RowMapper<HardwareAssociationData> {

			public String schema() {
				return " o.id AS id, o.plan_id AS planId,hm.item_code as itemCode  FROM b_orders o,b_hw_plan_mapping hm, b_plan_master p  WHERE NOT EXISTS  (SELECT *FROM b_association a" +
						" WHERE  a.order_id=o.id  AND a.client_id = o.client_id  AND a.is_deleted = 'N') AND o.client_id =? AND hm.plan_code = p.plan_code" +
						" AND o.plan_id = p.id and hm.item_code=?  and o.id =(select max(id) from b_orders where client_id=o.client_id )";


			}

			@Override
			public HardwareAssociationData mapRow(final ResultSet rs,
					 final int rowNum)
					throws SQLException {
				Long id = rs.getLong("id");
				Long planId = rs.getLong("planId");
				Long orderId=rs.getLong("id");
				String itemCode = rs.getString("itemCode");
				HardwareAssociationData associationData=new HardwareAssociationData(id,null,planId,orderId,itemCode);

				return associationData; 

			}
		}
		public List<AssociationData> retrieveClientAssociationDetails(Long clientId) {
            try
            {

          	  HarderwareAssociationMapper mapper = new HarderwareAssociationMapper();
			  String sql = "select " + mapper.schema();
			   return this.jdbcTemplate.query(sql, mapper, new Object[] {clientId});

		    }catch(EmptyResultDataAccessException accessException){
			return null;
		  }
		}
		private static final class HarderwareAssociationMapper implements RowMapper<AssociationData> {

			public String schema() {
				return "a.id as id,a.order_id AS orderId,p.plan_code as planCode,i.item_code as itemCode, a.hw_serial_no AS serialNum "
                       +" FROM b_association a,b_plan_master p,b_allocation al,b_item_master i"
                       +" where a.plan_id=p.id and a.hw_serial_no=al.serial_no and al.item_master_id=i.id and a.client_id = ?";

			}

			@Override
			public AssociationData mapRow(final ResultSet rs,
					 final int rowNum)
					throws SQLException {
				Long id= rs.getLong("id");
				Long orderId = rs.getLong("orderId");
				String planCode = rs.getString("planCode");
				String itemCode = rs.getString("itemCode");
				String serialNum = rs.getString("serialNum");
				
				return  new AssociationData(orderId,id,planCode,itemCode,serialNum,null);

			}
		}
		
        @Transactional 
		@Override
		public List<AssociationData> retrieveHardwareData(Long clientId) {
			try
            {
			 AssociationMapper mapper = new AssociationMapper();
			 final String sql = "select " + mapper.schema();
			 return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId,clientId });

		    }catch(EmptyResultDataAccessException accessException){
			return null;
		  }
		}
		
		private static final class AssociationMapper implements RowMapper<AssociationData> {

			public String schema() {
				return " 'ALLOT' as allocationType,b.serial_no AS serialNum,b.provisioning_serialno as provisionNum, pdm.property_code as propertyCode, ba.order_id as orderId FROM  b_item_detail b  LEFT JOIN "+
						"b_propertydevice_mapping pdm ON (b.serial_no = pdm.serial_number and b.client_id = pdm.client_id) Left JOIN b_association ba on (b.serial_no = ba.hw_serial_no) where  b.client_id=? union" +
						" select  'OWNED' as allocationType,o.serial_number  AS serialNum, o.provisioning_serial_number  AS provisionNum, pdm.property_code as propertyCode, ba.order_id as orderId"+
						" FROM b_owned_hardware o LEFT JOIN b_propertydevice_mapping pdm ON (o.serial_number = pdm.serial_number and o.client_id = pdm.client_id) Left JOIN b_association ba on (o.serial_number = ba.hw_serial_no)" +
						" WHERE o.client_id = ? and o.is_deleted = 'N'";
 
			}
			
			@Override
			public AssociationData mapRow(final ResultSet rs,
					final int rowNum)
					throws SQLException {
				final String serialNum = rs.getString("serialNum");				
				final String provisionNumber = rs.getString("provisionNum");
				final String allocationType =rs.getString("allocationType");
				final String propertyCode =rs.getString("propertycode");
				final Long orderId = rs.getLong("orderId");
				return new AssociationData(serialNum,provisionNumber,allocationType,propertyCode,orderId); 
			}
		}
		@Override
		public List<AssociationData> retrieveplanData(Long clientId) {
			
			try
            {
          	  AssociationPlanMapper mapper = new AssociationPlanMapper();
			  String sql = "select " + mapper.schema();
			   return this.jdbcTemplate.query(sql, mapper, new Object[] {clientId});

		    }catch(EmptyResultDataAccessException accessException){
			return null;
		  }
		}
		
		private static final class AssociationPlanMapper implements RowMapper<AssociationData> {

			public String schema() {
				return "p.plan_code as planCode,p.id as id,o.id as orderId from b_orders o,b_plan_master p" +
						" where o.plan_id=p.id and NOT EXISTS(Select * from  b_association a WHERE   a.order_id =o.id and a.is_deleted='N') and o.client_id=? ";
			}

			@Override
			public AssociationData mapRow(final ResultSet rs,
					 final int rowNum)
					throws SQLException {
				Long planId= rs.getLong("id");
				String planCode = rs.getString("planCode");
			    Long id=rs.getLong("orderId");
				return new AssociationData(planId,planCode,id);
			}
		}
 @Override
public AssociationData retrieveSingleDetails(Long id) {
  
	 try{
		 Mapper mapper = new Mapper();
		 String sql=" select *from (select a.id AS id,a.client_id AS clientId,a.order_id AS orderId,a.allocation_type as allocationType," +
			  		" i.id as itemId,a.hw_serial_no AS serialNo,p.plan_code AS planCode,id.serial_no AS serialNum,p.id AS planId,i.item_code AS itemCode," +
			  		" os.id as saleId " +
			  		" FROM b_association a,b_plan_master p,b_item_detail id,b_item_master i, b_onetime_sale os WHERE p.id = a.plan_id " +
			  		" AND a.order_id = ? AND id.serial_no = a.hw_serial_no AND id.item_master_id = i.id   AND a.is_deleted = 'N' and " +
			  		" os.item_id =i.id and os.client_id = a.client_id group by id" +
			  		" union " +
			  		" select a.id AS id,a.client_id AS clientId,a.order_id AS orderId,a.allocation_type as allocationType,i.id AS itemId,a.hw_serial_no AS serialNo," +
			  		" p.plan_code AS planCode,o.serial_number AS serialNum, p.id AS planId,i.item_code AS itemCode, null as saleId FROM b_association a," +
			  		" b_plan_master p,b_owned_hardware o,b_item_master i WHERE p.id = a.plan_id AND a.order_id =? AND o.serial_number = a.hw_serial_no " +
			  		" AND o.item_type = i.id AND a.is_deleted = 'N' GROUP BY id) a limit 1";
		 
				   return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {id,id});

			    }catch(EmptyResultDataAccessException accessException){
				return null;
			  }
		}
		
		private static final class Mapper implements RowMapper<AssociationData> {

		/*	public String schema() {
				return "  a.id AS id,a.client_id AS clientId,a.order_id AS orderId,a.allocation_type as allocationType,i.id as itemId,a.hw_serial_no AS serialNo,p.plan_code AS planCode,id.serial_no AS serialNum," +
					   " p.id AS planId,i.item_code AS itemCode,os.id as saleId FROM b_association a,b_plan_master p,b_item_detail id,b_item_master i, b_onetime_sale os" +
					   "  WHERE p.id = a.plan_id AND a.order_id = ? AND id.serial_no = a.hw_serial_no AND id.item_master_id = i.id   AND a.is_deleted = 'N' and " +
					   "  os.item_id =i.id and os.client_id = a.client_id group by id";

			}
			

			public String ownDeviceSchema() {
				return "  a.id AS id,a.client_id AS clientId,a.order_id AS orderId,a.allocation_type as allocationType,i.id AS itemId,a.hw_serial_no AS serialNo,p.plan_code AS planCode," +
						" o.serial_number AS serialNum, p.id AS planId,i.item_code AS itemCode, null as saleId FROM b_association a,b_plan_master p,b_owned_hardware o," +
						" b_item_master i WHERE p.id = a.plan_id AND a.order_id =? AND o.serial_number = a.hw_serial_no AND o.item_type = i.id " +
						" AND a.is_deleted = 'N' GROUP BY id";

			}*/

			@Override
			public AssociationData mapRow(final ResultSet rs,final int rowNum)
					throws SQLException {
				final Long id= rs.getLong("id");
				final Long clientId=rs.getLong("clientId");
				final Long orderId = rs.getLong("orderId");
				final String planCode = rs.getString("planCode");
				final String itemCode = rs.getString("itemCode");
				final String provNum = rs.getString("serialNo");
				final String serialNum = rs.getString("serialNum");
				final Long planId=rs.getLong("planId");
				final Long saleId=rs.getLong("saleId");
				final Long itemId=rs.getLong("itemId");
				final String allocationType =rs.getString("allocationType");
				return  new AssociationData(orderId,planCode,provNum,id,planId,clientId,serialNum,itemCode,saleId,itemId,allocationType);

			}

		}
		
 @Transactional
 @Override
public List<HardwareAssociationData> retrieveClientAllocatedHardwareDetails(Long clientId) {
	 
	 try{
		 final String sql=" SELECT *FROM (SELECT a.id AS id,a.serial_no AS serialNo,a.provisioning_serialno AS provSerialNum" +
          	  		      " FROM b_item_detail a, b_allocation l WHERE     a.serial_no = l.serial_no AND l.client_id = ? AND l.is_deleted = 'N'" +
          	  		      " AND a.client_id IS NULL" +
          	  		      " UNION " +
          	  		      " SELECT o.id AS id, o.serial_number AS serialNo, o.provisioning_serial_number AS provSerialNum " +
          	  		      " FROM b_owned_hardware o WHERE o.client_id = ? AND o.is_deleted = 'N' AND o.id = (SELECT max(id)" +
          	  		      " FROM b_owned_hardware a WHERE a.client_id = o.client_id)) a";
          	 // Configuration configurationProperty=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_DEVICE_AGREMENT_TYPE);
          	ClientHarderwareMapper mapper = new ClientHarderwareMapper();
          	/*  
          	  if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_SALE)){
			       sql = "select " + mapper.schema();
			      
          	  }else if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)){
          		  
          		  sql = "select " + mapper.ownDeviceSchema();
          	  }*/
			      return this.jdbcTemplate.query(sql, mapper, new Object[] {clientId,clientId});

		}catch(EmptyResultDataAccessException accessException){
			return null;
		}
		}
		private static final class ClientHarderwareMapper implements RowMapper<HardwareAssociationData> {
/*
			public String schema() {
				return " max(a.id) AS id,a.serial_no AS serialNo,a.provisioning_serialno AS provSerialNum  " +
					   " FROM b_item_detail a, b_allocation l where a.serial_no = l.serial_no and l.client_id = ? " +
					   " and l.is_deleted = 'Y' and a.client_id is null";

			}
			
			public String ownDeviceSchema() {
				return " o.id as id ,o.serial_number as serialNo,o.provisioning_serial_number as provSerialNum   FROM b_owned_hardware o" +
						" where o.client_id = ? and o.is_deleted='Y' and o.id=(select max(id) from b_owned_hardware a where a.client_id= o.client_id )";

			}*/

			@Override
			public HardwareAssociationData mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				
					Long id= rs.getLong("id");
					String serialNo = rs.getString("serialNo");
					String provSerialNum = rs.getString("provSerialNum");
					
					return  new HardwareAssociationData(id, serialNo,provSerialNum);

				}
			}
		@Override
		public List<AssociationData> retrieveClientAssociationDetails(Long clientId,String serialNumber) {

            try
            {

          	  HarderwareAssociation mapper = new HarderwareAssociation();
			  String sql = "select " + mapper.schema();
			
				 sql = sql + " and a.hw_serial_no= ?";
			  
			   return this.jdbcTemplate.query(sql, mapper, new Object[] {clientId,serialNumber});

		    }catch(EmptyResultDataAccessException accessException){
			return null;
		  }
		}
		private static final class HarderwareAssociation implements RowMapper<AssociationData> {

			public String schema() {
				return "a.id as id,a.order_id AS orderId, a.hw_serial_no AS serialNum FROM b_association a where a.client_id = ?";

			}

			@Override
			public AssociationData mapRow(final ResultSet rs, final int rowNum)
					throws SQLException {
				Long id= rs.getLong("id");
				Long orderId = rs.getLong("orderId");
				String serialNum = rs.getString("serialNum");
				
				return  new AssociationData(orderId,id,null,null,serialNum,null);

			}
		}
		
		
		
		
}
