package org.mifosplatform.organisation.mapping.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.organisation.mapping.data.ChannelMappingData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ChannelMappingReadPlatFormServiceImpl implements ChannelMappingReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<ChannelMappingData> paginationHelper = new PaginationHelper<ChannelMappingData>();
	
	@Autowired
	public ChannelMappingReadPlatFormServiceImpl(final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate =  new JdbcTemplate(dataSource);
	}

	//this is to retrive a particular record 
	@Override
	public ChannelMappingData retrieveChannelMapping(Long channelmappingId) {
		
		try{
      	ChannelMappingMapper channelMappingMapper = new ChannelMappingMapper();
		String sql = "SELECT "+channelMappingMapper.schema()+" AND m.id = ?";
		return jdbcTemplate.queryForObject(sql, channelMappingMapper,new Object[]{channelmappingId});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
	
	
	}

	@Override
	public Page<ChannelMappingData> retrieveChannelMapping(SearchSqlQuery searchChannelMapping) {

		ChannelMappingMapper channelMappingMapper = new ChannelMappingMapper();
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(channelMappingMapper.schema());
        
        String sqlSearch = searchChannelMapping.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ " product_id like '%"+sqlSearch+"%' OR"
	    			+ " channel_id like '%"+sqlSearch+"%' OR"
	    			+ " product_code like '%"+sqlSearch+"%' OR"
	    			+ " channel_name like '%"+sqlSearch+"%')";
	    }
        
        if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }
        sqlBuilder.append(" GROUP BY product_id ");

        if (searchChannelMapping.isLimited()) {
            sqlBuilder.append(" limit ").append(searchChannelMapping.getLimit());
        }

        if (searchChannelMapping.isOffset()) {
            sqlBuilder.append(" offset ").append(searchChannelMapping.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, channelMappingMapper);
	
	}
	

	private class ChannelMappingMapper implements RowMapper<ChannelMappingData> {
	    @Override
		public ChannelMappingData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final Long productId = rs.getLong("productId");
			final Long channelId = rs.getLong("channelId");
			final String productCode = rs.getString("productCode");
			final String productDescription = rs.getString("productDescription");
			final String channelName = rs.getString("channelName");
			final LocalDate fromDate = JdbcSupport.getLocalDate(rs,"fromDate");
			final LocalDate toDate = JdbcSupport.getLocalDate(rs,"toDate");
			
			return new ChannelMappingData(id, productId, channelId,productCode,productDescription,channelName,fromDate,toDate);
			
		}
	    
		public String schema() {
			
			return " m.id AS id,m.product_id as productId, s.product_code AS productCode,s.product_description AS productDescription," +
				   " m.channel_id as channelId, c.channel_name AS channelName,m.created_date AS fromDate,m.lastmodified_date AS toDate FROM b_prd_ch_mapping m " +
				   " join b_channel c on c.id = m.channel_id join b_product s on s.id = m.product_id "+
				   " where m.is_deleted = 'N' ";
			/*return" m.id AS id,m.product_id as productId,s.product_code AS productCode,s.product_description AS productDescription," +
			"  m.channel_id as channelId, c.channel_name AS channelName,m.created_date AS fromDate,m.lastmodified_date AS toDate, " +
			" pd.param_value as paramvalue,(select code_value from m_code_value mcv where mcv.id = pd.param_name) as paramName " +
			" FROM b_prd_ch_mapping m join b_channel c on c.id = m.channel_id join b_product s on s.id = m.product_id " +
			"  left join b_product_detail pd on pd.product_id = s.id where m.is_deleted = 'N' " ;*/
			
		}
	}


	@Override
	public List<ChannelData> retrieveSelectedChannels(Long productId) {
		try{
			ChannelMapper channelMapper = new ChannelMapper();
			String sql = "SELECT "+channelMapper.schema()+" AND m.product_id = ? And m.is_deleted = 'N'";
			return jdbcTemplate.query(sql, channelMapper,new Object[]{productId});
			}catch(EmptyResultDataAccessException ex){
				return null;
			}
		}
	private class ChannelMapper implements RowMapper<ChannelData> {
	    @Override
		public ChannelData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final String channelName = rs.getString("channelName");
			return new ChannelData(id,channelName);
		}
		public String schema() {
			
			return "c.id AS id, c.channel_name AS channelName from b_channel c join b_prd_ch_mapping m ON m.channel_id = c.id";
		}
	}
	@Override
	public List<ChannelMappingData> retriveproductmapping(Long productId) {
		try{
			ProductMapper productMapper = new ProductMapper();
			String sql = "SELECT "+productMapper.schema()+ " AND m.id = ?";
			return jdbcTemplate.query(sql, productMapper,new Object[]{productId});
			}catch(EmptyResultDataAccessException ex){
				return null;
			}
		}
	private class ProductMapper implements RowMapper<ChannelMappingData> {
	    @Override
		public ChannelMappingData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final Long productId = rs.getLong("productId");
			final Long channelId = rs.getLong("channelId");
			final String productCode = rs.getString("productCode");
			final String productDescription = rs.getString("productDescription");
			final String channelName = rs.getString("channelName");
			final LocalDate fromDate = JdbcSupport.getLocalDate(rs,"fromDate");
			final LocalDate toDate = JdbcSupport.getLocalDate(rs,"toDate");
			final String paramvalue = rs.getString("paramvalue");
			final String paramName = rs.getString("paramName");
			
			ChannelMappingData channelMappingData=	new ChannelMappingData(id, productId, channelId,productCode,productDescription,channelName,fromDate,toDate);
			channelMappingData.setParamvalue(paramvalue);
			channelMappingData.setParamName(paramName);
			return channelMappingData;
		}
	
		public String schema() {
			
				return" m.id AS id,m.product_id as productId,s.product_code AS productCode,s.product_description AS productDescription," +
				"  m.channel_id as channelId, c.channel_name AS channelName,m.created_date AS fromDate,m.lastmodified_date AS toDate, " +
				" pd.param_value as paramvalue,(select code_value from m_code_value mcv where mcv.id = pd.param_name) as paramName " +
				" FROM b_prd_ch_mapping m join b_channel c on c.id = m.channel_id join b_product s on s.id = m.product_id " +
				"  left join b_product_detail pd on pd.product_id = s.id where m.is_deleted = 'N' " ;
				
			}
}
}