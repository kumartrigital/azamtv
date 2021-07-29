/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.codes.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.codes.data.CodeData;
import org.mifosplatform.infrastructure.codes.exception.CodeNotFoundException;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.plan.data.BillRuleData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class CodeReadPlatformServiceImpl implements CodeReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final PaginationHelper<CodeData> paginationHelper = new PaginationHelper<CodeData>();
    
    @Autowired
    public CodeReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class CodeMapper implements RowMapper<CodeData> {

        public String schema() {
            return " c.id as id, c.code_name as code_name, c.code_description as codeDescription,c.is_system_defined as systemDefined,c.module as module from m_code c ";
        }

        @Override
        public CodeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String code_name = rs.getString("code_name");
            final String codeDescription = rs.getString("codeDescription");
            final boolean systemDefined = rs.getBoolean("systemDefined");
            final String module = rs.getString("module");

            return CodeData.instance(id, code_name,codeDescription, systemDefined, module);
        }
    }

    @Override
    public Page<CodeData> retrieveAllCodes(SearchSqlQuery searchCodes) {
        context.authenticatedUser();

        final CodeMapper rm = new CodeMapper();
        StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(rm.schema());
        sqlBuilder.append(" order by c.code_name");
        //final String sql = "select " + rm.schema() + " order by c.code_name";
        if (searchCodes.isLimited()) {
            sqlBuilder.append(" limit ").append(searchCodes.getLimit());
        }
        if (searchCodes.isOffset()) {
            sqlBuilder.append(" offset ").append(searchCodes.getOffset());
        }
        //return this.jdbcTemplate.query(sql, rm, new Object[] {});
        return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
	            new Object[] {}, rm);
    }

    @Override
    public CodeData retrieveCode(final Long codeId) {
        try {
            context.authenticatedUser();

            final CodeMapper rm = new CodeMapper();
            final String sql = "select " + rm.schema() + " where c.id = ?";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { codeId });
        } catch (EmptyResultDataAccessException e) {
            throw new CodeNotFoundException(codeId);
        }
    }
    
    @Override
    public CodeData retriveCode(final String codeName) {
        try {
            this.context.authenticatedUser();

            final CodeMapper rm = new CodeMapper();
            final String sql = "select " + rm.schema() + " where c.code_name = ?";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { codeName });
        } catch (final EmptyResultDataAccessException e) {
            throw new CodeNotFoundException(codeName);
        }
    }
    

	/*
	 *Retrieve billing Rules
	 */
	@Override
	public List<BillRuleData> retrievebillRules(String enumName) {

		context.authenticatedUser();

		final BillRuleDataMapper mapper = new BillRuleDataMapper();

		final String sql = "select " + mapper.schema(enumName);

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class BillRuleDataMapper implements RowMapper<BillRuleData> {

		public String schema(String enumName) {
			return " b.enum_id AS id,b.enum_message_property AS billingRule,b.enum_value AS value FROM r_enum_value b" +
					" WHERE enum_name = '"+enumName+"'";

		}

		@Override
		public BillRuleData mapRow(final ResultSet rs,final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String billrules = rs.getString("billingRule");
			final String value = rs.getString("value");
			return new BillRuleData(id, billrules,value);
			
		}
	}

	@Override
	public CodeData retrieveCodeIdOnCodeNameAndCodeValue(String codeName, String codeValue) {

		context.authenticatedUser();

		final CodeDataMapper mapper = new CodeDataMapper();

		final String sql = "select " + mapper.schema()+" where c.code_name='"+codeName+"' and cv.code_value='"+codeValue+"'";

		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {});
		
		
	}

	private static final class CodeDataMapper implements RowMapper<CodeData> {

		public String schema() {
			return " cv.id, cv.code_id, c.code_description from m_code_value cv join m_code c on c.id=cv.code_id " ;
		}

		@Override
		public CodeData mapRow(final ResultSet rs,final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long codeId = rs.getLong("code_id");
			final String description = rs.getString("code_description");
			return new CodeData(id,null,description,false,null,codeId);
		}
	}
}