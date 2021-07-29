package org.mifosplatform.organisation.channel.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.mifosplatform.celcom.domain.SearchTypeEnum;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.organisation.channel.domain.LanguageEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.sun.xml.bind.annotation.OverrideAnnotationOf;

@Service
public class ChannelReadPlatformServiceImpl implements ChannelReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<ChannelData> paginationHelper = new PaginationHelper<ChannelData>();
	private final PlatformSecurityContext context;
	
	
	@Autowired
	public ChannelReadPlatformServiceImpl( final TenantAwareRoutingDataSource dataSource,final PlatformSecurityContext context) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}

	//this is to retrive a particular record 
	@Override
	public ChannelData retrieveChannel(Long channelId) {

		try{
      	ChannelMapper channelMapper = new ChannelMapper();
		String sql = "SELECT "+channelMapper.schema()+" WHERE c.is_deleted = 'N' AND c.id = ?";
		return jdbcTemplate.queryForObject(sql, channelMapper,new Object[]{channelId});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
	
	}

	@Override
	public Page<ChannelData> retrieveChannel(SearchSqlQuery searchChannel) {
		ChannelMapper channelMapper = new ChannelMapper();
		
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(channelMapper.schema());
        sqlBuilder.append(" where c.id IS NOT NULL and c.is_deleted = 'N'");
        
        String sqlSearch = searchChannel.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ " channel_name like '%"+sqlSearch+"%' OR"
	    			+ " channel_category like '%"+sqlSearch+"%' OR"
	    			+ " language like '%"+sqlSearch+"%' OR"
	    			+ " channel_type like '%"+sqlSearch+"%' OR"
	    			+ " is-local_channel like '%"+sqlSearch+"%' OR"
	    			+ " is_hd_channel like '%"+sqlSearch+"%' OR"
	    			+ " channel_sequence like '%"+sqlSearch+"%' OR"
	    			+ " broadcaster_id like '%"+sqlSearch+"%' OR"
	    			+ " brc_name like '%"+sqlSearch+"%')";
	    }
        
        if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }


        if (searchChannel.isLimited()) {
            sqlBuilder.append(" limit ").append(searchChannel.getLimit());
        }

        if (searchChannel.isOffset()) {
            sqlBuilder.append(" offset ").append(searchChannel.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, channelMapper);
	}
	
	
	private class ChannelMapper implements RowMapper<ChannelData> {
	    @Override
		public ChannelData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final String channelName = rs.getString("channelName");
			final String channelCategory = rs.getString("channelCategory");
			final String language = rs.getString("language");
		    final String channelType = rs.getString("channelType");
			final Boolean isLocalChannel = rs.getBoolean("isLocalChannel");
		    final Boolean isHdChannel = rs.getBoolean("isHdChannel");
			final Long channelSequence = rs.getLong("channelSequence");
			final Long broadcasterId = rs.getLong("broadcasterId");
			final String broadcasterName = rs.getString("broadcasterName");
			
			return new ChannelData(id, channelName, channelCategory, language,channelType, isLocalChannel, isHdChannel, channelSequence,broadcasterId,broadcasterName);
		}
	    
		public String schema() {
			
			return " c.id AS id, c.channel_name AS channelName, c.channel_category AS channelCategory,c.language AS language, c.channel_type AS channelType, " +
				   " c.is_local_channel AS isLocalChannel, c.is_hd_channel AS isHdChannel, c.channel_sequence AS channelSequence,c.broadcaster_id AS broadcasterId,br.brc_name AS broadcasterName FROM b_channel c "+
				   " left join b_broadcaster br ON c.broadcaster_id = br.id ";
			
		}
	}


	@Override
	public List<ChannelData> retrieveChannelsForDropdown() {

		try{
			ChannelDropdownMapper channelMapper = new ChannelDropdownMapper();
		String sql = "SELECT "+channelMapper.schema()+" WHERE c.is_deleted = 'N'";
		return jdbcTemplate.query(sql, channelMapper,new Object[]{});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
		
	}
	private class ChannelDropdownMapper implements RowMapper<ChannelData> {
	    @Override
		public ChannelData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final String channelName = rs.getString("channelName");
		   
			
			return new ChannelData(id,channelName);
		}
	    
		public String schema() {
			
			return " c.id AS id,c.channel_name AS channelName from b_channel c ";
			
		}
	}
	@Override
	public List<LanguageEnum> retrieveLanguageEnum() {
		
		List<LanguageEnum> Language = new ArrayList<LanguageEnum>();
		for(int i=0;i<=15;i++){
			Language.add(LanguageEnum.fromInt(i));
		}
		/*final LanguageEnum Telugu = LanguageEnum.fromInt(0);
		final LanguageEnum English = LanguageEnum.fromInt(1);
		final LanguageEnum Bengali = LanguageEnum.fromInt(2);
		final LanguageEnum Assameese = LanguageEnum.fromInt(3);
		final LanguageEnum Urdu = LanguageEnum.fromInt(4);
		final LanguageEnum Hindi = LanguageEnum.fromInt(5);
		final LanguageEnum Odia = LanguageEnum.fromInt(6);
		final LanguageEnum Gujrati = LanguageEnum.fromInt(7);
		final LanguageEnum Marathi = LanguageEnum.fromInt(8);
		final LanguageEnum Punjabi = LanguageEnum.fromInt(9);
		final LanguageEnum Tamil = LanguageEnum.fromInt(10);
		final LanguageEnum Malayalam = LanguageEnum.fromInt(11);
		final LanguageEnum Bhojpuri = LanguageEnum.fromInt(12);
		final LanguageEnum Kannada = LanguageEnum.fromInt(13);
		final LanguageEnum German = LanguageEnum.fromInt(14);
		final LanguageEnum Rajasthani = LanguageEnum.fromInt(15);
		final List<LanguageEnum> LanguageEnum = Arrays.asList(Telugu,English,Bengali,Assameese,Urdu,Hindi,Odia,Gujrati,Marathi,Punjabi,Tamil,Malayalam,Bhojpuri,Kannada,German,Rajasthani);*/
		return Language;
	}

	@Override
	public List<ChannelData> retrieveChannelName(Long broadcasterId) {

		try{
      	ChannelNameMapper channelNameMapper = new ChannelNameMapper();
		String sql = "SELECT "+channelNameMapper.schema()+" WHERE c.broadcaster_id = ?";
		return jdbcTemplate.query(sql, channelNameMapper,new Object[]{broadcasterId});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
		
	}
	
	private class ChannelNameMapper implements RowMapper<ChannelData> {
	    @Override
		public ChannelData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final String channelName = rs.getString("channelName");
		    final String channelType = rs.getString("channelType");
			
			return new ChannelData(id, channelName, channelType);
		}
	    
		public String schema() {
			
			return " c.id, channel_name as channelName, if(is_hd_channel = 'N', 'SD', 'HD') as 'ChannelType' from b_channel c join b_broadcaster b ON b.id = c.broadcaster_id ";
			
		}
	}
	

}
