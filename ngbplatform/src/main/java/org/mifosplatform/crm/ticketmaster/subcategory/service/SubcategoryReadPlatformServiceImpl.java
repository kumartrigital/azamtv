package org.mifosplatform.crm.ticketmaster.subcategory.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.ticketmaster.subcategory.data.SubcategoryDataT;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.broadcaster.data.BroadcasterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;



/**
 * @written by H 
 * for retrevial of sub category
 *
 */


@Service
public class SubcategoryReadPlatformServiceImpl implements SubcategoryReadPlatformService{

	private final  JdbcTemplate jdbcTemplate;

	private final PaginationHelper<SubcategoryDataT> paginationHelper = new PaginationHelper<SubcategoryDataT>();
	
	
	@Autowired
	public SubcategoryReadPlatformServiceImpl(
			final TenantAwareRoutingDataSource dataSource) {
		
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		
	}
	
	@Override
	public SubcategoryDataT retrieveSubcategory(Long id) {
		try {
			SubcategoryMapper subcategoryMapper = new SubcategoryMapper();
			String sql = "SELECT " + subcategoryMapper.schema() + " WHERE s.id = ?";
			return jdbcTemplate.queryForObject(sql, subcategoryMapper, new Object[] { id });
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}
	}

	/*@Override
	public List<SubcategoryDataT> retrieveSubcategory(String subcategoryData) {
		
		try {
			
			SubcategoryMapper subcategoryMapper = new SubcategoryMapper();
			String sql = "SELECT " + subcategoryMapper.schema() ;
			return jdbcTemplate.query(sql, subcategoryMapper ,new Object[] {});
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}
	}
	*/
	
	
	@Override
	public Page<SubcategoryDataT> retrieveSubcategory(SearchSqlQuery searchSubcategory) {
		SubcategoryMapper subcategoryMapper = new SubcategoryMapper();

		final StringBuilder sqlBuilder = new StringBuilder(200);
		sqlBuilder.append("select ");
		sqlBuilder.append(subcategoryMapper.schema());
		sqlBuilder.append(" where id IS NOT NULL ");

		String sqlSearch = searchSubcategory.getSqlSearch();
		String extraCriteria = null;
		if (sqlSearch != null) {
			sqlSearch = sqlSearch.trim();
			extraCriteria = " and (id like '%" + sqlSearch + "%' OR" + " maincategory like '%" + sqlSearch + "%' OR"
					+ " subcategory like '%" + sqlSearch + "%' OR" + " timetaken like '%" + "%' )";
		}

		if (null != extraCriteria) {
			sqlBuilder.append(extraCriteria);
		}

		/*sqlBuilder.append(" and is_deleted = 'N' ");*/
		
		if (searchSubcategory.isLimited()) {
			sqlBuilder.append(" limit ").append(searchSubcategory.getLimit());
		}

		if (searchSubcategory.isOffset()) {
			sqlBuilder.append(" offset ").append(searchSubcategory.getOffset());
		}

		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
				new Object[] {}, subcategoryMapper);
	}

	protected static final class SubcategoryMapper implements RowMapper<SubcategoryDataT> {

		@Override
		public SubcategoryDataT mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Integer maincategory = rs.getInt("maincategory");
			final String subcategory = rs.getString("subcategory");
			final Integer timetaken = rs.getInt("timetaken");
			
			return new SubcategoryDataT(id,maincategory,subcategory,timetaken);

		}
	
	public String schema() {
		return "s.id as id,s.main_category as maincategory,s.sub_category as subcategory, s.time_taken as timetaken FROM b_sub_category s";
	}
	
	}
	
}