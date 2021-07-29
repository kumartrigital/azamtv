/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.monetary.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.broadcaster.data.BroadcasterData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class CurrencyReadPlatformServiceImpl implements CurrencyReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final CurrencyMapper currencyRowMapper;
    private final PaginationHelper<CurrencyData> paginationHelper = new PaginationHelper<CurrencyData>();

    @Autowired
    public CurrencyReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.currencyRowMapper = new CurrencyMapper();
    }

    @Override
    public Collection<CurrencyData> retrieveAllowedCurrencies() {

        context.authenticatedUser();

        final String sql = "select " + currencyRowMapper.schema() + " from m_organisation_currency c order by c.name";

        return this.jdbcTemplate.query(sql, currencyRowMapper, new Object[] {});
    }

    @Override
    public Collection<CurrencyData> retrieveAllPlatformCurrencies() {

        final String sql = "select " + currencyRowMapper.schema() + " from m_currency c order by c.name ";

        return this.jdbcTemplate.query(sql, currencyRowMapper, new Object[] {});
    }

    private static final class CurrencyMapper implements RowMapper<CurrencyData> {

        @Override
        public CurrencyData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

        	final Long id = rs.getLong("id");
            final String code = rs.getString("code");
            final String name = rs.getString("name");
            final int decimalPlaces = JdbcSupport.getInteger(rs, "decimalPlaces");
            final String displaySymbol = rs.getString("displaySymbol");
            final String nameCode = rs.getString("nameCode");

            return new CurrencyData(id, code, name, decimalPlaces, displaySymbol, nameCode, null, null, null);
        }

        public String schema() {
            return " c.id as id, c.code as code, c.name as name, c.decimal_places as decimalPlaces, c.display_symbol as displaySymbol, c.internationalized_name_code as nameCode ";
        }
    }
    
  //this is to retrive a perticular record 
  	@Override
  	public CurrencyData retriveCurrencies(Long currenciesId) {
  		try{
  		CurrencyMappers currencyMapper = new CurrencyMappers();
  		String sql = "SELECT "+currencyMapper.schema()+" WHERE c.id = ?";
  		return jdbcTemplate.queryForObject(sql, currencyMapper,new Object[]{currenciesId});
  		}catch(EmptyResultDataAccessException ex){
  			return null;
  		}
  	}
    
    
    @Override
	public Page<CurrencyData> retriveCurrencies(SearchSqlQuery searchCurrency) {
    	CurrencyMappers currencyMapper = new CurrencyMappers();
		
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(currencyMapper.schema());
        sqlBuilder.append(" where id IS NOT NULL and c.is_deleted = 'N'");
        
        String sqlSearch = searchCurrency.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ " code like '%"+sqlSearch+"%' OR"
	    			+ " type like '%"+sqlSearch+"%' OR"
	    			+ " decimal_places like '%"+sqlSearch+"%' OR"
	    			+ " name like '%"+sqlSearch+"%' OR"
	    			+ " country_code like '%"+sqlSearch+"%' OR"
	    			+ " country_name like '%"+sqlSearch+"%' OR"
	    			+ " display_symbol like '%"+sqlSearch+"%')";
	    }
        
        if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }
        //sqlBuilder.append(" and is_deleted = 'N' ");

        if (searchCurrency.isLimited()) {
            sqlBuilder.append(" limit ").append(searchCurrency.getLimit());
        }

        if (searchCurrency.isOffset()) {
            sqlBuilder.append(" offset ").append(searchCurrency.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, currencyMapper);
	}
	
	private class CurrencyMappers implements RowMapper<CurrencyData> {
	    @Override
		public CurrencyData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final String code = rs.getString("code");
			final String name = rs.getString("name");
		    final int decimalPlaces = rs.getInt("decimalPlaces");
		    final String displaySymbol = rs.getString("displaySymbol");
		    final String nameCode = rs.getString("nameCode");
		    //final String displayLabel = rs.getString("displayLabel");
			final String type = rs.getString("type");
		    //final String internationalizedNameCode = rs.getString("internationalizedNameCode");
			final String countryCode = rs.getString("countryCode");
			final String countryName = rs.getString("countryName");
			
			
			return new CurrencyData(id, code, name, decimalPlaces, displaySymbol, nameCode, /*displayLabel,*/ type, /*internationalizedNameCode,*/ countryCode, countryName);
		}
	    
		public String schema() {
			
			return " c.id AS id, c.code AS Code, c.type AS type, c.decimal_places AS decimalPlaces, c.name AS name, " +
				   " c.internationalized_name_code AS nameCode, c.country_code AS countryCode, " +
				   " c.country_name AS countryName,c.display_symbol AS displaySymbol from m_currency c ";
			
		}
   
}
	@Override
	public Long findMaxofId(String currenyType) {
		return this.jdbcTemplate.queryForLong("select max(id+1)  from m_currency where type = '"+currenyType+"' ");
	}

	@Override
	public List<CurrencyData> retrieveCurrency() {

		try{
		CurrencyDataMapper currencyDataMapper = new CurrencyDataMapper();
		String sql = "SELECT "+currencyDataMapper.schema()+" from m_currency mc ";
		return jdbcTemplate.query(sql, currencyDataMapper,new Object[]{});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}	
		
	}
	
	private class CurrencyDataMapper implements RowMapper<CurrencyData> {
	    @Override
		public CurrencyData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final String code = rs.getString("code");
			final String name = rs.getString("name");
			final int decimalPlaces = rs.getInt("decimalPlaces");
		  
			return new CurrencyData(id, code, name,decimalPlaces, null, null, 
					null, null, null);
		}
	    
		public String schema() {
			
			return " mc.id AS id, mc.code AS code, mc.name AS name,mc.decimal_places as decimalPlaces";
			
		}
	}

	@Override
	public Collection<CurrencyData> retrieveAllPlatformnonCurrencies() {
		
		/* final String sql = "select " + currencyRowMapper.schema() + " from m_currency c where c.id > 1000 order by c.name ";
		 
	       return this.jdbcTemplate.query(sql, currencyRowMapper, new Object[] {});*/
		 try{
			 
		   CurrencyMapperNew currencyMapperNew=new CurrencyMapperNew();
		   final String sql = "select " + currencyMapperNew.schema() + " from m_currency c where c.id > 1000 order by c.name  ";
			 return this.jdbcTemplate.query(sql, currencyMapperNew, new Object[] {});
		  }catch(EmptyResultDataAccessException e){
		return null;
	  }
	}
	       
	}
     class CurrencyMapperNew  implements RowMapper<CurrencyData> {

	           @Override
	           public CurrencyData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

	           	final Long id = rs.getLong("id");
	               final String code = rs.getString("code");
	               final String name = rs.getString("name");
	               final int decimalPlaces = JdbcSupport.getInteger(rs, "decimalPlaces");
	               final String displaySymbol = rs.getString("displaySymbol");
	               final String nameCode = rs.getString("nameCode");

	               return new CurrencyData(id, code, name, decimalPlaces, displaySymbol, nameCode, null, null, null);
	          }

	           public String schema() {
	               return " c.id as id, c.code as code, c.name as name, c.decimal_places as decimalPlaces, c.display_symbol as displaySymbol, c.internationalized_name_code as nameCode ";
	           }
	       }
	       
	       
	       
	       
	
	
	
	

	