package org.mifosplatform.crm.ticketmaster.service;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.ticketmaster.data.ClientTicketData;
import org.mifosplatform.crm.ticketmaster.data.SubCategoryData;
import org.mifosplatform.crm.ticketmaster.data.TicketMasterData;
import org.mifosplatform.crm.ticketmaster.data.UsersData;
import org.mifosplatform.crm.ticketmaster.domain.PriorityType;
import org.mifosplatform.crm.ticketmaster.domain.PriorityTypeEnum;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class TicketMasterReadPlatformServiceImpl implements TicketMasterReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final PaginationHelper<ClientTicketData> paginationHelper = new PaginationHelper<ClientTicketData>();

	@Autowired
	public TicketMasterReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<UsersData> retrieveUsers() {
		context.authenticatedUser();

		final UserMapper mapper = new UserMapper();

		final String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class UserMapper implements RowMapper<UsersData> {

		public String schema() {
			return "u.id as id,u.username as username from m_appuser u where u.is_deleted=0";

		}

		@Override
		public UsersData mapRow(ResultSet resultSet, int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final String username = resultSet.getString("username");

			final UsersData data = new UsersData(id, username);

			return data;

		}

	}

	@Override
	public Page<ClientTicketData> retrieveAssignedTicketsForNewClient(SearchSqlQuery searchTicketMaster,
			String statusType, String fromDate, String toDate, String type) {
		final AppUser user = this.context.authenticatedUser();

		// final String hierarchy = user.getOffice().getHierarchy();

		// final String hierarchySearchString = hierarchy + "%";

		final UserTicketsMapperForNewClient mapper = new UserTicketsMapperForNewClient();

		StringBuilder sqlBuilder = new StringBuilder(200);
		sqlBuilder.append("select ");
		sqlBuilder.append(mapper.userTicketSchema());
		if (fromDate != null && toDate != null && statusType != null) {
			sqlBuilder.append(
					" and tckt.status = '" + statusType + "' and DATE_FORMAT(tckt.ticket_date,'%Y-%m-%d') between '"
							+ fromDate + "' and '" + toDate + "' and tckt.type = '" + type + "'");
		}
		sqlBuilder.append(" Union ALL ");
		sqlBuilder.append(mapper.officeTicketSchema());
		
		if (fromDate != null && toDate != null && statusType != null) {
			sqlBuilder.append(
					" and tckt.status = '" + statusType + "' and DATE_FORMAT(tckt.ticket_date,'%Y-%m-%d') between '"
							+ fromDate + "' and '" + toDate + "' and tckt.type = '" + type + "'");
		}


		String sqlSearch = searchTicketMaster.getSqlSearch();
		String extraCriteria = "";
		/*
		 * if(fromDate != null && toDate != null) {
		 * sqlBuilder.append(" and tckt.ticket_date between '"+fromDate+"' and '"
		 * +toDate+"' "); }
		 */
		if (sqlSearch != null) {
			sqlSearch = sqlSearch.trim();
			extraCriteria = " and ((select display_name from m_client where id = tckt.client_id) like '%" + sqlSearch
					+ "%' OR"
					+ " (select mcv.code_value from m_code_value mcv where mcv.id = tckt.problem_code) like '%"
					+ sqlSearch + "%' OR" + " tckt.status like '%" + sqlSearch + "%' OR tckt.ticket_date like '%"
					+ sqlSearch + "%' OR"
					+ " (select user.username from m_appuser user where tckt.assigned_to = user.id) like '%" + sqlSearch
					+ "%')";
		}
	
		/*
		 * if(statusType != null){ extraCriteria =" or tckt.status='"+statusType+"'"; }
		 */
		sqlBuilder.append(extraCriteria);

		if (searchTicketMaster.isLimited()) {
			sqlBuilder.append(" limit ").append(searchTicketMaster.getLimit());
		}

		if (searchTicketMaster.isOffset()) {
			sqlBuilder.append(" offset ").append(searchTicketMaster.getOffset());
		}

		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
				new Object[] {}, mapper);

	}

	@Override
	public List<TicketMasterData> retrieveClientTicketDetails(final Long clientId) {
		try {
			final ClientTicketMappe mapper = new ClientTicketMappe();

			final String sql = "select " + mapper.clientOrderLookupSchema()
					+ " and tckt.client_id= ? order by tckt.id DESC ";

			return jdbcTemplate.query(sql, mapper, new Object[] { clientId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	private static final class ClientTicketMappe implements RowMapper<TicketMasterData> {

		public String clientOrderLookupSchema() {

			return "tckt.id as id, tckt.priority as priority, tckt.ticket_date as ticketDate, tckt.team_user_id as userId,tckt.source_of_ticket as sourceOfTicket, "
					+ " tckt.problem_code as problemCode, tckt.status_code as statusCode, tckt.due_date as dueDate,"
					+ "(SELECT code_value FROM m_code_value mcv WHERE  tckt.status_code = mcv.id) AS ticketstatus,"
					+ " tckt.description as description,tckt.resolution_description as resolutionDescription, "
					+ " (select code_value from m_code_value mcv where tckt.problem_code=mcv.id)as problemDescription,"
					+ " tckt.status as status, "
					+ "(select d.id from m_document d where d.parent_entity_id=tckt.client_id and d.child_entity_id = tckt.id and d.parent_entity_type = 'clientTicket') as DocumentId,"
					+ "(select d.file_name from m_document d where d.parent_entity_id=tckt.client_id and d.child_entity_id = tckt.id and d.parent_entity_type = 'clientTicket') as fileName,"
					+ " (select m_appuser.username from m_appuser "
					+ " inner join b_ticket_details td on td.assigned_to = m_appuser.id"
					+ " where td.id = (select max(id) from b_ticket_details where b_ticket_details.ticket_id = tckt.id)) as assignedTo,"
					+ " (select comments FROM b_ticket_details details where details.ticket_id =tckt.id and "
					+ " details.id=(select max(id) from b_ticket_details where b_ticket_details.ticket_id = tckt.id)) as lastComment"
					+ " from b_ticket_master tckt, m_appuser user where tckt.assigned_to = user.id";
		}

		@Override
		public TicketMasterData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final String priority = resultSet.getString("priority");
			final String status = resultSet.getString("status");
			final String LastComment = resultSet.getString("LastComment");
			final String problemDescription = resultSet.getString("problemDescription");
			final String assignedTo = resultSet.getString("assignedTo");
			final Integer userId = resultSet.getInt("userId");
			final LocalDate ticketDate = JdbcSupport.getLocalDate(resultSet, "ticketDate");
			// final int userId = new Integer(usersId);
			final String sourceOfTicket = resultSet.getString("sourceOfTicket");
			final Date dueDate = resultSet.getTimestamp("dueDate");
			final String description = resultSet.getString("description");
			final String resolutionDescription = resultSet.getString("resolutionDescription");
			final String ticketstatus = resultSet.getString("ticketstatus");
			final Integer problemCode = resultSet.getInt("problemCode");
			final Integer statusCode = resultSet.getInt("statusCode");
			final Long DocumentId = resultSet.getLong("DocumentId");
			final String fileName = resultSet.getString("fileName");

			return new TicketMasterData(id, priority, status, userId, ticketDate, LastComment, problemDescription,
					assignedTo, sourceOfTicket, dueDate, description, resolutionDescription, problemCode, statusCode,
					ticketstatus, DocumentId, fileName);
		}
	}

	private static final class ClientTicketMapper implements RowMapper<TicketMasterData> {

		public String clientOrderLookupSchema() {

			/*
			 * return
			 * "tckt.id as id, tckt.priority as priority, tckt.ticket_date as ticketDate, tckt.assigned_to as userId,tckt.source_of_ticket as sourceOfTicket, "
			 * +" tckt.problem_code as problemCode, tckt.status_code as statusCode, tckt.due_date as dueDate,"
			 * +
			 * "(SELECT td.team_user_id FROM  b_ticket_details td  WHERE td.id = (SELECT MAX(id) FROM b_ticket_details WHERE  b_ticket_details.ticket_id = tckt.id)) AS teamUserId,"
			 * +
			 * "(SELECT td.team_id  FROM  b_ticket_details td  WHERE td.id = (SELECT MAX(id) FROM b_ticket_details WHERE  b_ticket_details.ticket_id = tckt.id)) AS teamId,"
			 * +
			 * "(SELECT code_value FROM m_code_value mcv WHERE  tckt.status_code = mcv.id) AS ticketstatus,"
			 * +
			 * " tckt.description as description,tckt.resolution_description as resolutionDescription, "
			 * +
			 * " (select code_value from m_code_value mcv where tckt.problem_code=mcv.id)as problemDescription,"
			 * + " tckt.status as status, " + " (select m_appuser.username from m_appuser "
			 * + " inner join b_ticket_details td on td.assigned_to = m_appuser.id" +
			 * " where td.id = (select max(id) from b_ticket_details where b_ticket_details.ticket_id = tckt.id)) as assignedTo,"
			 * +
			 * " (select comments FROM b_ticket_details details where details.ticket_id =tckt.id and "
			 * +
			 * " details.id=(select max(id) from b_ticket_details where b_ticket_details.ticket_id = tckt.id)) as lastComment"
			 * +
			 * " from b_ticket_master tckt, m_appuser user where tckt.assigned_to = user.id"
			 * ;
			 */

			return " btm.id AS id, btm.priority AS priority,btm.ticket_date AS ticketDate,btm.problem_code AS problemCode, btm.status AS status,btm.sub_category AS subCategoryes,"
					+ " btm.due_date AS dueDate,btd.created_date,btd.comments AS LastComment, btd.team_id as teamId,"
					+ "(select team_code from b_team bt where bt.id=btd.team_id) as teamCode,btd.team_user_id as teamUserId,"
					+ "(SELECT mau.username  FROM m_appuser mau where btd.team_user_id=mau.id) as teamUser from b_ticket_master btm,"
					+ "(Select ticket_id,created_date,comments,team_id,team_user_id from b_ticket_details btd1 where btd1.id = (Select max(id) from "
					+ "b_ticket_details btds where btd1.ticket_id=btds.ticket_id) ) btd "
					+ "where btm.id=btd.ticket_id";

			/*
			 * return
			 * " btm.id AS id, btm.priority AS priority,btm.ticket_date AS ticketDate,"+
			 * " btm.problem_code AS problemCode,"+
			 * " btm.status AS status,btm.sub_category AS subCategoryes,"+
			 * " btm.due_date AS dueDate,btd.created_date,btd.comments AS LastComment,"+
			 * " (SELECT mau.username  FROM m_appuser mau where btd.assigned_to=mau.id) as assigned_to"
			 * +
			 * " from b_office_ticket btm,(Select ticket_id,created_date,comments,assigned_to from b_office_ticket_detail btd1 "
			 * +
			 * " where btd1.id = (Select max(id) from b_office_ticket_detail btds where btd1.ticket_id=btds.ticket_id) ) btd "
			 * + " where btm.id=btd.ticket_id and ";
			 */

		}

		@Override
		public TicketMasterData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final String priority = resultSet.getString("priority");
			final String status = resultSet.getString("status");
			final String LastComment = resultSet.getString("LastComment");
			// final String problemDescription = resultSet.getString("problemDescription");
			// final String assignedTo = resultSet.getString("assignedTo");
			// final String usersId = resultSet.getString("userId");
			final LocalDate ticketDate = JdbcSupport.getLocalDate(resultSet, "ticketDate");
			// final int userId = new Integer(usersId);
			// final String sourceOfTicket = resultSet.getString("sourceOfTicket");
			final Date dueDate = resultSet.getTimestamp("dueDate");
			// final String description = resultSet.getString("description");
			// final String resolutionDescription =
			// resultSet.getString("resolutionDescription");
			// final String ticketstatus = resultSet.getString("ticketstatus");
			final Integer problemCode = resultSet.getInt("problemCode");
			// final Integer statusCode = resultSet.getInt("statusCode");
			final String teamCode = resultSet.getString("teamCode");
			final String teamUser = resultSet.getString("teamUser");
			final Long teamUserId = resultSet.getLong("teamUserId");
			final Long teamId = resultSet.getLong("teamId");
			final String subCategoryes = resultSet.getString("subCategoryes");

			return new TicketMasterData(id, priority, status, null, ticketDate, LastComment, null, null, null, dueDate,
					null, null, problemCode, null, null, teamCode, teamUser, teamUserId, teamId, subCategoryes);
		}
	}

	@Override
	public TicketMasterData retrieveSingleTicketDetails(final Long clientId, final Long ticketId) {
		try {
			final ClientTicketMapper mapper = new ClientTicketMapper();
			final String sql = "select " + mapper.clientOrderLookupSchema() + " and btd.ticket_id='" + ticketId
					+ "' and btm.client_id='" + clientId + "'";
			return jdbcTemplate.queryForObject(sql, mapper, new Object[] {});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	@Override
	public List<EnumOptionData> retrievePriorityData() {
		EnumOptionData low = PriorityTypeEnum.priorityType(PriorityType.LOW);
		EnumOptionData medium = PriorityTypeEnum.priorityType(PriorityType.MEDIUM);
		EnumOptionData high = PriorityTypeEnum.priorityType(PriorityType.HIGH);
		List<EnumOptionData> priorityType = Arrays.asList(low, medium, high);
		return priorityType;
	}

	@Override
	public List<TicketMasterData> retrieveClientTicketHistory(final Long ticketId) {

		context.authenticatedUser();
		final TicketDataMapper mapper = new TicketDataMapper();
		String sql = "select " + mapper.schema() + " btd.ticket_id='" + ticketId + "  order by t.id DESC";
		return this.jdbcTemplate.query(sql, mapper, new Object[] { ticketId });
	}

	private static final class TicketDataMapper implements RowMapper<TicketMasterData> {

		public String schema() {
			return " t.id AS id,t.created_date AS createDate,user.username AS assignedTo,t.comments as description,"
					+ " t.attachments AS attachments ,tt.team_code  AS teamCode FROM b_ticket_master tm , b_ticket_details t  "
					+ " inner join m_appuser user on user.id = t.assigned_to "
					+ " join b_team tt ON tt.user_id= t.assigned_to";

		}

		@Override
		public TicketMasterData mapRow(ResultSet resultSet, int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final LocalDate createdDate = JdbcSupport.getLocalDate(resultSet, "createDate");
			final String assignedTo = resultSet.getString("assignedTo");
			final String description = resultSet.getString("description");
			final String attachments = resultSet.getString("attachments");
			final String teamCode = resultSet.getString("teamCode");
			String fileName = null;
			if (attachments != null) {
				File file = new File(attachments);
				fileName = file.getName();
			}
			final TicketMasterData data = new TicketMasterData(id, createdDate, assignedTo, description, fileName,
					teamCode);

			return data;
		}

	}

	private static final class UserTicketsMapperForNewClient implements RowMapper<ClientTicketData> {

		public String userTicketSchema() {

			/*
			 * return
			 * " SQL_CALC_FOUND_ROWS tckt.id AS id,tckt.client_id AS clientId,mct.display_name as clientName,tckt.priority AS priority,"
			 * +
			 * "tckt.status AS status,tckt.ticket_date AS ticketDate,tckt.closed_date AS closedDate,"
			 * +
			 * "(SELECT user.username FROM m_appuser user WHERE tckt.createdby_id = user.id) AS created_user,"
			 * + "tckt.assigned_to AS userId,"+
			 * "(SELECT comments FROM b_ticket_details x WHERE tckt.id = x.ticket_id AND x.id = (SELECT max(id) FROM b_ticket_details y WHERE tckt.id = y.ticket_id)) AS LastComment,"
			 * +
			 * "(SELECT mcv.code_value FROM m_code_value mcv WHERE mcv.id = tckt.problem_code) AS problemDescription,"
			 * +
			 * "(SELECT user.username FROM m_appuser user WHERE tckt.assigned_to = user.id) AS assignedTo,"
			 * +
			 * "CONCAT(TIMESTAMPDIFF(day, tckt.ticket_date, Now()), ' d ', MOD(TIMESTAMPDIFF(hour, tckt.ticket_date, Now()), 24), ' hr ',"
			 * +
			 * "MOD(TIMESTAMPDIFF(minute, tckt.ticket_date, Now()), 60), ' min ') AS timeElapsed,"
			 * +
			 * "IFNull((SELECT user.username FROM m_appuser user WHERE tckt.lastmodifiedby_id = user.id),'Null') AS closedby_user "
			 * +
			 * "FROM b_ticket_master tckt left join m_client mct on mct.id = tckt.client_id "
			 * + "left join m_office o on o.id = mct.office_id ";
			 */
			return " SQL_CALC_FOUND_ROWS tckt.id AS id,tckt.client_id AS clientId,mct.display_name as clientName,tckt.priority AS priority,"
					+ "tckt.status AS status,tckt.ticket_date AS ticketDate,tckt.closed_date AS closedDate, tckt.type AS type,"
					+ "(SELECT user.username FROM m_appuser user WHERE tckt.createdby_id = user.id) AS created_user,"
					+ "tckt.assigned_to AS userId,"
					+ "(SELECT comments FROM b_ticket_details x WHERE tckt.id = x.ticket_id AND x.id = (SELECT max(id) FROM b_ticket_details y WHERE tckt.id = y.ticket_id)) AS LastComment,"
					+ "(SELECT mcv.code_value FROM m_code_value mcv WHERE mcv.id = tckt.problem_code) AS problemDescription,"
					+ "(SELECT user.username FROM m_appuser user WHERE tckt.assigned_to = user.id) AS assignedTo,"
					+ "CONCAT(TIMESTAMPDIFF(day, tckt.ticket_date, Now()), ' d ', MOD(TIMESTAMPDIFF(hour, tckt.ticket_date, Now()), 24), ' hr ',"
					+ "MOD(TIMESTAMPDIFF(minute, tckt.ticket_date, Now()), 60), ' min ') AS timeElapsed,"
					+ "IFNull((SELECT user.username FROM m_appuser user WHERE tckt.lastmodifiedby_id = user.id),'Null') AS closedby_user "
					+ "FROM b_ticket_master tckt left join m_client mct on mct.id = tckt.client_id "
					+ "left join m_office o on o.id = mct.office_id where tckt.id IS NOT NULL";

		}

		public String officeTicketSchema() {

			return "select tckt.id AS id,tckt.office_id AS clientId,o.name AS clientName,tckt.priority AS priority,tckt.status AS status, "
					+ "tckt.ticket_date AS ticketDate,tckt.closed_date AS closedDate, tckt.type AS type,"
					+ "(SELECT user.username FROM m_appuser user WHERE  tckt.createdby_id = user.id) AS created_user,tckt.assigned_to AS userId, "
					+ "(SELECT comments FROM b_ticket_details x WHERE  tckt.id = x.ticket_id AND x.id = (SELECT Max(id) "
					+ "FROM b_ticket_details y WHERE  tckt.id = y.ticket_id)) AS LastComment, "
					+ "(SELECT mcv.code_value FROM m_code_value mcv WHERE  mcv.id = tckt.problem_code) AS problemDescription, "
					+ "(SELECT user.username FROM m_appuser user WHERE  tckt.assigned_to = user.id) AS assignedTo, "
					+ "Concat(Timestampdiff(day, tckt.created_date, Now()), ' d ', MOD( "
					+ "Timestampdiff(hour, tckt.created_date, Now()), 24), ' hr ', MOD( "
					+ "Timestampdiff(minute, tckt.created_date, Now()), 60), ' min ') AS "
					+ "timeElapsed,Ifnull((SELECT user.username FROM m_appuser user "
					+ "WHERE tckt.lastmodifiedby_id = user.id), 'Null') AS closedby_user "
					+ "FROM b_office_ticket tckt LEFT JOIN m_office o ON o.id = tckt.office_id where tckt.id IS NOT NULL";
			


		}

		@Override
		public ClientTicketData mapRow(ResultSet resultSet, int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final String priority = resultSet.getString("priority");
			final String status = resultSet.getString("status");
			final Long userId = resultSet.getLong("userId");
			final LocalDate ticketDate = JdbcSupport.getLocalDate(resultSet, "ticketDate");
			final String lastComment = resultSet.getString("LastComment");
			final String problemDescription = resultSet.getString("problemDescription");
			final String assignedTo = resultSet.getString("assignedTo");
			final Long clientId = resultSet.getLong("clientId");
			final String timeElapsed = resultSet.getString("timeElapsed");
			final String clientName = resultSet.getString("clientName");
			final String createUser = resultSet.getString("created_user");
			final String closedByuser = resultSet.getString("closedby_user");
			final LocalDate closedDate = JdbcSupport.getLocalDate(resultSet, "closedDate");
			final String type = resultSet.getString("type");

			ClientTicketData clientticketData = new ClientTicketData(id, priority, status, userId, ticketDate,
					lastComment, problemDescription, assignedTo, clientId, timeElapsed, clientName, createUser,
					closedByuser, type);
			clientticketData.setClosedDate(closedDate);
			return clientticketData;
		}

	}

	@Override
	public TicketMasterData retrieveTicket(final Long clientId, final Long ticketId) {

		try {
			final ClientTicketMappe mapper = new ClientTicketMappe();
			final String sql = "select " + mapper.clientOrderLookupSchema() + " and tckt.client_id= ? and tckt.id=?";

			return jdbcTemplate.queryForObject(sql, mapper, new Object[] { clientId, ticketId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<SubCategoryData> retrieveSubCategory(final int codeValue) {
		context.authenticatedUser();

		final SubCategoryMapper mapper = new SubCategoryMapper();
		System.out.println("Service " + codeValue);
		final String sql = "select " + mapper.schema(codeValue) + " where sub.main_category ='" + codeValue + "'";

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class SubCategoryMapper implements RowMapper<SubCategoryData> {

		long codeValue;

		public String schema(int codeValue) {
			this.codeValue = codeValue;
			return "sub.id as id,sub.sub_category as subcategory from b_sub_category sub ";
		}

		@Override
		public SubCategoryData mapRow(ResultSet resultSet, int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final String subCat = resultSet.getString("subcategory");
			return new SubCategoryData(id, this.codeValue, subCat);

		}

	}

	@Override
	public List<TicketMasterData> retrieveOfficeTicketDetails(final Long officeId) {
		try {
			final OfficeTicketMapper mapper = new OfficeTicketMapper();

			final String sql = "select " + mapper.officeOrderLookupSchema()
					+ " and btm.office_id= ? order by btm.id DESC ";

			return jdbcTemplate.query(sql, mapper, new Object[] { officeId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	@Override
	public List<TicketMasterData> retrieveAllOfficeTicketDetails() {
		try {
			final OfficeTicketMapper mapper = new OfficeTicketMapper();

			final String sql = "select " + mapper.officeOrderLookupSchema() + " order by btm.id DESC ";

			return jdbcTemplate.query(sql, mapper, new Object[] {});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	@Override
	public TicketMasterData retrieveSingleOfficeTicketDetails(final Long officeId, final Long ticketId) {
		try {
			final OfficeTicketMapper mapper = new OfficeTicketMapper();

			final String sql = "select " + mapper.officeOrderLookupSchema()
					+ " and btm.office_id= ? and btm.id=? order by btm.id DESC ";

			return jdbcTemplate.queryForObject(sql, mapper, new Object[] { officeId, ticketId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	private static final class OfficeTicketMapper implements RowMapper<TicketMasterData> {

		public String officeOrderLookupSchema() {

			/*
			 * return
			 * "tckt.id as id, tckt.priority as priority, tckt.ticket_date as ticketDate, tckt.assigned_to as userId,tckt.source_of_ticket as sourceOfTicket, "
			 * +" tckt.problem_code as problemCode, tckt.status_code as statusCode, tckt.due_date as dueDate,"
			 * +
			 * "(SELECT code_value FROM m_code_value mcv WHERE  tckt.status_code = mcv.id) AS ticketstatus,tckt.sub_category as subCategory,"
			 * +
			 * " tckt.description as description,tckt.resolution_description as resolutionDescription, "
			 * +
			 * " (select code_value from m_code_value mcv where tckt.problem_code=mcv.id)as problemDescription,"
			 * + " tckt.status as status, " + " (select m_appuser.username from m_appuser "
			 * + " inner join b_office_ticket_detail td on td.assigned_to = m_appuser.id" +
			 * " where td.id = (select max(id) from b_office_ticket_detail where b_office_ticket_detail.ticket_id = tckt.id)) as assignedTo,"
			 * +
			 * " (select comments FROM b_office_ticket_detail details where details.ticket_id =tckt.id and "
			 * +
			 * " details.id=(select max(id) from b_office_ticket_detail where b_office_ticket_detail.ticket_id = tckt.id)) as lastComment"
			 * +
			 * " from b_office_ticket tckt, m_appuser user where tckt.assigned_to = user.id"
			 * ;
			 */
			return "btm.id AS id,btm.ticket_no as ticketNumber,btm.priority AS priority,btm.ticket_date AS ticketDate,"
					+ " btm.problem_code AS problemCode,btm.description as description, btm.title as title, "
					+ " btm.status AS status,btm.sub_category AS subCategory,btm.status_code as statusCode,btm.source_of_ticket as sourceOfTicket,"
					+ " btm.due_date AS dueDate,btd.created_date as Comment_date,btd.comments AS LastComment,btm.resolution_description as resolutionDescription,"
					+ "(select d.id from m_document d where d.parent_entity_id=btm.office_id and d.child_entity_id = btm.id and d.parent_entity_type = 'officeTicket') as DocumentId,"
					+ " (select d.file_name from m_document d where d.parent_entity_id=btm.office_id and d.child_entity_id = btm.id and d.parent_entity_type = 'officeTicket') as fileName,"
					+ " (SELECT code_value FROM m_code_value mcv WHERE  btm.status_code = mcv.id) AS ticketstatus, "
					+ " (select code_value from m_code_value mcv where btm.problem_code=mcv.id) as problemDescription, "
					+ " (SELECT mau.username  FROM m_appuser mau where btd.assigned_to=mau.id) as assignedTo"
					+ " from b_office_ticket btm,(Select ticket_id,created_date,comments,assigned_to from b_office_ticket_detail btd1 "
					+ " where btd1.id = (Select max(id) from b_office_ticket_detail btds where btd1.ticket_id=btds.ticket_id) ) btd "
					+ " where btm.id=btd.ticket_id ";

		}

		@Override
		public TicketMasterData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final String ticketNumber = resultSet.getString("ticketNumber");
			final String priority = resultSet.getString("priority");
			final String status = resultSet.getString("status");
			final String LastComment = resultSet.getString("LastComment");
			final String problemDescription = resultSet.getString("problemDescription");
			final String assignedTo = resultSet.getString("assignedTo");
			// final String usersId = resultSet.getString("userId");
			final LocalDate ticketDate = JdbcSupport.getLocalDate(resultSet, "ticketDate");
			// final int userId = new Integer(usersId);
			final String sourceOfTicket = resultSet.getString("sourceOfTicket");
			final Date dueDate = resultSet.getTimestamp("dueDate");
			final String description = resultSet.getString("description");
			final String title = resultSet.getString("title");
			final String resolutionDescription = resultSet.getString("resolutionDescription");
			final String fileName = resultSet.getString("fileName");
			final String ticketstatus = resultSet.getString("ticketstatus");
			final Integer problemCode = resultSet.getInt("problemCode");
			final Integer statusCode = resultSet.getInt("statusCode");
			final String subcategory = resultSet.getString("subCategory");
			final LocalDate Comment_date = JdbcSupport.getLocalDate(resultSet, "Comment_date");
			final Long DocumentId = resultSet.getLong("DocumentId");

			TicketMasterData ticketMasterData = new TicketMasterData(id, priority, status, null, ticketDate,
					LastComment, problemDescription, assignedTo, sourceOfTicket, dueDate, description,
					resolutionDescription, problemCode, statusCode, null, null, null, null, null, null);
			ticketMasterData.setSubCategoryStatus(subcategory);
			ticketMasterData.setCommentDate(Comment_date);
			ticketMasterData.setTicketNumber(ticketNumber);
			ticketMasterData.setTitle(title);
			ticketMasterData.setFileName(fileName);
			ticketMasterData.setDocumentId(DocumentId);
			return ticketMasterData;
		}
	}

	private class TicketHistoryMapper implements RowMapper<TicketMasterData> {
		@Override
		public TicketMasterData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String priority = rs.getString("priority");
			final LocalDate ticketDate = JdbcSupport.getLocalDate(rs, "ticketDate");
			final String problemDescription = rs.getString("problemDescription");
			final String status = rs.getString("status");
			final String subCategorys = rs.getString("subCategorys");
			final LocalDate Comment_date = JdbcSupport.getLocalDate(rs, "Comment_date");
			final String comments = rs.getString("comments");
			final String teamCode = rs.getString("teamCode");
			final String assignedTo = rs.getString("assignedTo");

			return new TicketMasterData(id, priority, ticketDate, problemDescription, status, subCategorys,
					Comment_date, comments, teamCode, assignedTo);
		}

		public String schema() {

			return "td.id,tm.priority,tm.ticket_date as ticketDate,"
					+ "(SELECT code_value FROM m_code_value mcv WHERE  tm.problem_code = mcv.id) AS problemDescription, tm.status,tm.sub_category as subCategorys,"
					+ "td.created_date as Comment_date, td.comments,"
					+ "(select team_code from b_team bt where bt.id=td.team_id) as teamCode,"
					+ "(SELECT m_appuser.username FROM m_appuser WHERE td.team_user_id = m_appuser.id ) AS assignedTo"
					+ " from b_ticket_master tm,b_ticket_details td where tm.id=td.ticket_id ";
		}
	}

	@Override
	public List<TicketMasterData> retrieveTicketsDetails(Long ticketId) {
		try {
			TicketHistoryMapper ticketHistoryMapper = new TicketHistoryMapper();
			String sql = "SELECT " + ticketHistoryMapper.schema() + "  AND tm.id=? order by td.id desc ";
			return jdbcTemplate.query(sql, ticketHistoryMapper, new Object[] { ticketId });
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}

	}

	@Override
	public List<TicketMasterData> retrieveTicketDetailsByOfficeId(Long officeId) {
		// TODO Auto-generated method stub
		try {
			final TicketMapperForOffice mapper = new TicketMapperForOffice();

			final String sql = "select btm.id AS id,btm.client_id as clientId,btm.priority AS priority,btm.ticket_date AS ticketDate,"
					+ " btm.problem_code AS problemCode,btm.description as description,"
					+ "btm.status AS status,btm.sub_category AS subCategory,btm.status_code as statusCode,btm.source_of_ticket as sourceOfTicket,"
					+ "btm.due_date AS dueDate,btm.created_date as commentedDate,btm.resolution_description as resolutionDescription,"
					+ "(select code_value from m_code_value mcv where btm.problem_code=mcv.id)as problemDescription, "
					+ "btm.assigned_to as assignedTo,btm.closed_date as closedDate,btm.createdby_id as createdBy,btm.team_id as teamId "
					+ "from b_ticket_master btm inner join  m_client c  on btm.client_id=c.id where c.office_id= ? order by btm.id DESC ";

			return jdbcTemplate.query(sql, mapper, new Object[] { officeId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	private static final class TicketMapperForOffice implements RowMapper<TicketMasterData> {

		@Override
		public TicketMasterData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final Long clientId = resultSet.getLong("clientId");
			final String priority = resultSet.getString("priority");
			final String status = resultSet.getString("status");
			// final String LastComment = resultSet.getString("lastComment");
			final String problemDescription = resultSet.getString("problemDescription");
			final String assignedTo = resultSet.getString("assignedTo");
			final LocalDate ticketDate = JdbcSupport.getLocalDate(resultSet, "ticketDate");
			final String sourceOfTicket = resultSet.getString("sourceOfTicket");
			final Date dueDate = resultSet.getTimestamp("dueDate");
			final String description = resultSet.getString("description");
			final String resolutionDescription = resultSet.getString("resolutionDescription");
			final Integer problemCode = resultSet.getInt("problemCode");
			final String subcategory = resultSet.getString("subCategory");
			final LocalDate commentedDate = JdbcSupport.getLocalDate(resultSet, "commentedDate");
			final LocalDate closedDate = JdbcSupport.getLocalDate(resultSet, "closedDate");
			final Long createdBy = resultSet.getLong("createdBy");

			TicketMasterData ticketMasterData = new TicketMasterData(id, clientId, priority, status, null,
					problemDescription, ticketDate, assignedTo, sourceOfTicket, dueDate, description,
					resolutionDescription, problemCode, subcategory, commentedDate, closedDate, createdBy);
			return ticketMasterData;
		}
	}

}