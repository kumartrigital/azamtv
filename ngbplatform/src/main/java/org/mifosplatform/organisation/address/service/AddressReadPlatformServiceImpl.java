package org.mifosplatform.organisation.address.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.address.data.AddressLocationDetails;
import org.mifosplatform.organisation.address.data.CityDetailsData;
import org.mifosplatform.organisation.address.data.CountryDetails;
import org.mifosplatform.organisation.address.domain.AddressEnum;
import org.mifosplatform.portfolio.order.data.AddressStatusEnumaration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class AddressReadPlatformServiceImpl implements AddressReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final PaginationHelper<AddressLocationDetails> paginationHelper = new PaginationHelper<AddressLocationDetails>();

	@Autowired
	public AddressReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<AddressData> retrieveAddressDetailsBy(final Long clientId, String addressType) {

		try {
			context.authenticatedUser();
			final AddressMapper mapper = new AddressMapper();
			String sql = null;
			if (addressType == null) {
				sql = "select " + mapper.schema() + " where is_deleted='n' and a.client_id=" + clientId;
			} else {
				sql = "select " + mapper.schema() + " where is_deleted='n' and a.address_key like'" + addressType
						+ "%' and a.client_id=" + clientId;
			}
			return this.jdbcTemplate.query(sql, mapper, new Object[] {});
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class AddressMapper implements RowMapper<AddressData> {

		public String schema() {
			return "a.address_id as id,a.client_id as clientId,a.address_key as addressKey,a.address_no as addressNo,a.street as street,a.zip as zip,a.city as city,a.district as district,"
					+ "a.state as state,a.country as country from b_client_address a";

		}

		@Override
		public AddressData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long clientId = rs.getLong("clientId");
			final String addressKey = rs.getString("addressKey");
			final String addressNo = rs.getString("addressNo");
			final String street = rs.getString("street");
			final String zip = rs.getString("zip");
			final String city = rs.getString("city");
			final String state = rs.getString("state");
			final String country = rs.getString("country");
			final String district = rs.getString("district");

			return new AddressData(id, clientId, null, addressNo, street, zip, city, state, country, district,
					addressKey, null);

		}
	}

	@Override
	public List<AddressData> retrieveSelectedAddressDetails(final String selectedname) {

		final AddressMapper mapper = new AddressMapper();
		final String sql = "select " + mapper.schema()
				+ " where a.city=? or a.state =? or a.country =? or a.district =? and a.is_deleted='n'";

		return this.jdbcTemplate.query(sql, mapper, new Object[] { selectedname, selectedname, selectedname });
	}

	@Override
	public List<AddressData> retrieveAddressDetails() {

		context.authenticatedUser();
		final AddressMapper mapper = new AddressMapper();

		final String sql = "select " + mapper.schema() + " where is_deleted='n'";

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	@Override
	public List<String> retrieveCountryDetails() {
		context.authenticatedUser();
		final AddressMapper1 mapper = new AddressMapper1();

		final String sql = "select " + mapper.sqlschema("country_name", "country");

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	private static final class AddressMapper1 implements RowMapper<String> {

		public String sqlschema(final String placeholder, final String tablename) {
			return placeholder + " as data from b_" + tablename + " ";

		}

		@Override
		public String mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final String country = rs.getString("data");
			return country;

		}

	}

	@Override
	public List<String> retrieveStateDetails() {
		context.authenticatedUser();
		final AddressMapper1 mapper = new AddressMapper1();

		final String sql = "select " + mapper.sqlschema("state_name", "state");

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	@Override
	public List<String> retrieveCityDetails() {
		context.authenticatedUser();
		final AddressMapper1 mapper = new AddressMapper1();

		final String sql = "select " + mapper.sqlschema("city_name", "city") + " where is_delete = 'N'";

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	@Override
	public List<String> retrieveDistrictDetails() {
		context.authenticatedUser();
		final AddressMapper1 mapper = new AddressMapper1();

		final String sql = "select " + mapper.sqlschema("district_name", "district") + " where is_delete = 'N'";

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	@Override
	public List<AddressData> retrieveCityDetails(final String selectedname) {
		context.authenticatedUser();
		final DataMapper mapper = new DataMapper();

		final String sql = "select " + mapper.schema(selectedname);

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	private static final class DataMapper implements RowMapper<AddressData> {

		public String schema(final String placeHolder) {
			return "id as id," + placeHolder + "_name as data from b_" + placeHolder;

		}

		@Override
		public AddressData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String data = rs.getString("data");

			// String serviceDescription = rs.getString("service_description");
			return new AddressData(id, data);

		}
	}

	@Override
	public List<EnumOptionData> addressType() {

		final EnumOptionData primary = AddressStatusEnumaration.enumOptionData(AddressEnum.PRIMARY);
		final EnumOptionData billing = AddressStatusEnumaration.enumOptionData(AddressEnum.BILLING);
		final List<EnumOptionData> categotyType = Arrays.asList(primary, billing);
		return categotyType;
	}

	@Override
	public AddressData retrieveAdressBy(final String cityName) {
		try {

			context.authenticatedUser();
			String sql;
			final retrieveMapper mapper = new retrieveMapper();
			sql = "SELECT  " + mapper.schema() + "and c.is_delete = 'N'";

			return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { cityName });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class retrieveMapper implements RowMapper<AddressData> {

		public String schema() {
			return " c.city_name as cityName, d.district_name As districtName,s.state_name as stateName,co.country_name as countryName"
					+ "  FROM b_city c,b_state s,b_country co, b_district d  WHERE c.parent_code = d.id and d.parent_code = s.id and s.parent_code = co.id"
					+ "  and c.city_name =?";

		}

		@Override
		public AddressData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final String city = rs.getString("cityName");
			final String state = rs.getString("stateName");
			final String country = rs.getString("countryName");
			final String district = rs.getString("districtName");
			return new AddressData(city, state, country, district);
		}
	}

	@Override
	public List<AddressData> retrieveAdressBystate(String stateName) {
		try {

			context.authenticatedUser();
			String sql;
			final retrieveMapperstate mapper = new retrieveMapperstate();
			sql = "SELECT  " + mapper.schema() + "and c.is_delete = 'N'";

			return this.jdbcTemplate.query(sql, mapper, new Object[] { stateName });

		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	private static final class retrieveMapperstate implements RowMapper<AddressData> {

		public String schema() {
			return "c.city_name as cityName, d.district_name As districtName,s.state_name as stateName,co.country_name as countryName\n"
					+ "FROM b_city c,b_state s,b_country co, b_district d  WHERE c.parent_code = d.id and d.parent_code = s.id and s.parent_code = co.id\n"
					+ "and s.state_name = ?";

		}

		@Override
		public AddressData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final String city = rs.getString("cityName");
			final String state = rs.getString("stateName");
			final String country = rs.getString("countryName");
			final String district = rs.getString("districtName");
			return new AddressData(city, state, country, district);
		}
	}

	@Override
	public List<CountryDetails> retrieveCountries() {
		try {
			context.authenticatedUser();
			final CountryMapper mapper = new CountryMapper();

			final String sql = "select " + mapper.schema();

			return this.jdbcTemplate.query(sql, mapper, new Object[] {});
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class CountryMapper implements RowMapper<CountryDetails> {

		public String schema() {
			return " c.id as id,c.country_name as countryName FROM b_country c";

		}

		@Override
		public CountryDetails mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String countryName = rs.getString("countryName");

			return new CountryDetails(id, countryName);

		}
	}

	@Override
	public List<AddressData> retrieveClientAddressDetails(final Long clientId) {
		try {
			context.authenticatedUser();
			final AddressMapper mapper = new AddressMapper();

			final String sql = "select " + mapper.schema() + " where a.is_deleted='n' and a.client_id=?";

			return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Page<AddressLocationDetails> retrieveAllAddressLocations(final SearchSqlQuery searchAddresses) {
		try {
			context.authenticatedUser();
			final AddressLocationMapper locationMapper = new AddressLocationMapper();

			final StringBuilder sqlBuilder = new StringBuilder(200);
			sqlBuilder.append("select ");
			sqlBuilder.append(locationMapper.schema());
			String sqlSearch = searchAddresses.getSqlSearch();
			String extraCriteria = "";
			if (sqlSearch != null) {
				sqlSearch = sqlSearch.trim();
				extraCriteria = "  where country_name like '%" + sqlSearch + "%' ";
			}

			sqlBuilder.append(extraCriteria);

			/*
			 * if (searchAddresses.isLimited()) {
			 * sqlBuilder.append(" limit ").append(searchAddresses.getLimit()); }
			 */
			/*
			 * if (searchAddresses.isOffset()) {
			 * sqlBuilder.append(" offset ").append(searchAddresses.getOffset()); }
			 */
			return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
					new Object[] {}, locationMapper);
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}

	}

	public static final class AddressLocationMapper implements RowMapper<AddressLocationDetails> {
		public String schema() {

			return "country.id as countryId,country.country_code as countryCode,country.country_name as counryName,"
					+ "district.id as districtId,district.district_code as districtCode,district.district_name as districtName,"
					+ "state.id as stateId,state.state_code as stateCode,state.state_name as stateName,"
					+ "city.id as cityId,city.city_code as cityCode,city.city_name as cityName "
					+ "from b_country country "
					+ "left join b_state state on (state.parent_code=country.id and state.is_delete='N') "
					+ "left join b_district  district on (district.parent_code = state.id and  district.is_delete = 'N')"
					+ "left join  b_city city on (city.parent_code = district.id and state.is_delete='N' and city.is_delete='N')"
					+ "where country.is_active='Y'";

		}

		@Override
		public AddressLocationDetails mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final String countryCode = rs.getString("countryCode");
			final String countryName = rs.getString("counryName");
			final String cityCode = rs.getString("cityCode");
			final String cityName = rs.getString("cityName");
			final String stateCode = rs.getString("stateCode");
			final String stateName = rs.getString("stateName");
			final String districtCode = rs.getString("districtCode");
			final String districtName = rs.getString("districtName");
			final Long cityId = rs.getLong("cityId");
			final Long countryId = rs.getLong("countryId");
			final Long stateId = rs.getLong("stateId");
			final Long districtId = rs.getLong("districtId");

			return new AddressLocationDetails(countryCode, countryName, cityCode, cityName, stateCode, stateName,
					districtCode, districtName, countryId, stateId, cityId, districtId);
		}
	}

	@Override
	public List<CityDetailsData> retrieveCitywithCodeDetails() {

		try {
			context.authenticatedUser();
			final CityMapper mapper = new CityMapper();
			final String sql = "select " + mapper.schema();
			return this.jdbcTemplate.query(sql, mapper, new Object[] {});
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class CityMapper implements RowMapper<CityDetailsData> {

		public String schema() {
			return " city_name as cityName,city_code as cityCode from b_city where is_delete = 'N'";

		}

		@Override
		public CityDetailsData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final String cityName = rs.getString("cityName");
			final String cityCode = rs.getString("cityCode");
			return new CityDetailsData(cityName, cityCode);

		}

	}

	@Override
	public List<CityDetailsData> retrieveAddressDetailsByCityName(final String cityName) {

		try {
			context.authenticatedUser();
			final CityDetailMapper mapper = new CityDetailMapper();
			final String sql = "select " + mapper.schema()
					+ " where cc.is_delete ='N' and bc.is_active='Y' and cc.city_name like '%" + cityName
					+ "%' order by bc.id LIMIT 15";
			return this.jdbcTemplate.query(sql, mapper, new Object[] {});
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class CityDetailMapper implements RowMapper<CityDetailsData> {

		public String schema() {
			return " bc.country_name as countryName,d.district_name as districtName, bs.state_name as stateName,city_name as cityName,cc.city_code as cityCode"
					+ " from b_city cc join b_state bs on (cc.parent_code = bs.id) join b_district d ON (cc.parent_code = d.id) join b_country bc on (bc.id = bs.parent_code) ";

		}

		@Override
		public CityDetailsData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final String cityName = rs.getString("cityName");
			final String cityCode = rs.getString("cityCode");
			final String state = rs.getString("stateName");
			final String country = rs.getString("countryName");
			final String district = rs.getString("districtName");
			return new CityDetailsData(cityName, cityCode, state, country, district);

		}
	}

	@Override
	public AddressData retriveAddressByCity(String city) {
		try {
			context.authenticatedUser();
			final DistrictDetailMapper mapper = new DistrictDetailMapper();
			final String sql = "select " + mapper.schema()
					+ " where ci.is_delete ='N' and c.is_active='Y' and ci.city_name='" + city + "'";
			return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {});
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class DistrictDetailMapper implements RowMapper<AddressData> {

		public String schema() {
			return " c.country_name as countryName,d.district_name as districtName from b_country c inner join b_state s inner join b_district d inner join  b_city ci"
					+ " on c.id=s.parent_code and s.id=d.parent_code and d.id=ci.parent_code ";

		}

		@Override
		public AddressData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final String country = rs.getString("countryName");
			final String district = rs.getString("districtName");
			return new AddressData(country, district);

		}
	}

}
