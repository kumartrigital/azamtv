package org.mifosplatform.cms.media.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.cms.media.data.MediaAssetData;
import org.mifosplatform.cms.media.data.MediaassetAttribute;
import org.mifosplatform.cms.media.data.MediaassetAttributeData;
import org.mifosplatform.cms.media.domain.MediaEnum;
import org.mifosplatform.cms.media.domain.MediaTypeEnumaration;
import org.mifosplatform.cms.mediadetails.data.MediaLocationData;
import org.mifosplatform.finance.payments.data.McodeData;
import org.mifosplatform.infrastructure.core.data.MediaEnumoptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class MediaAssetReadPlatformServiceImpl implements MediaAssetReadPlatformService {

	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public MediaAssetReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	@Override
	public List<MediaAssetData> retrievemediaAssetdata(final Long pageNo, final String clientType) {

		final AllMediaAssestDataMapper mediaAssestDataMapper = new AllMediaAssestDataMapper();
		String sql = null;
		if (clientType != null) {
			sql = "select " + mediaAssestDataMapper.mediaAssestDataSchema() + " where m.clientType ='" + clientType
					+ "' LIMIT ?, 10";
		} else {
			sql = "select " + mediaAssestDataMapper.mediaAssestDataSchema() + " LIMIT ?, 10";
		}
		return this.jdbcTemplate.query(sql, mediaAssestDataMapper, new Object[] { "", pageNo });

	}

	private static final class AllMediaAssestDataMapper implements RowMapper<MediaAssetData> {

		@Override
		public MediaAssetData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final Long mediaId = resultSet.getLong("mediaId");
			final String mediaTitle = resultSet.getString("title");
			final String mediaImage = resultSet.getString("image");
			final BigDecimal rating = resultSet.getBigDecimal("rating");
			final Long eventId = resultSet.getLong("eventId");
			final String assetTag = resultSet.getString("assetTag");
			final String quality = resultSet.getString("quality");
			final String optType = resultSet.getString("optType");
			final BigDecimal price = resultSet.getBigDecimal("price");

			return new MediaAssetData(mediaId, mediaTitle, mediaImage, rating, eventId, assetTag, quality, optType,
					price);
		}

		public String mediaAssestDataSchema() {

			return " *,? as assetTag from mvAll_vw m  ";
		}
	}

	private static final class MediaAssestDataMapper implements RowMapper<MediaAssetData> {

		@Override
		public MediaAssetData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final Long mediaId = resultSet.getLong("mediaId");
			final String mediaTitle = resultSet.getString("title");
			final String mediaImage = resultSet.getString("image");
			final BigDecimal rating = resultSet.getBigDecimal("rating");
			final Long eventId = resultSet.getLong("eventId");
			final String assetTag = resultSet.getString("assetTag");

			return new MediaAssetData(mediaId, mediaTitle, mediaImage, rating, eventId, assetTag, null, null, null);
		}

		public String mediaAssestDataSchema() {

			return " *,? as assetTag from mvAll_vw m  ";
		}
		/*
		 * public String mediaAssestDataSchemaforCommingMovice() {
		 * 
		 * return
		 * "m.id AS mediaId, m.title AS title,m.image AS image,m.rating AS rating,0 as eventId,assetTag  FROM b_media_asset m "
		 * ; }
		 * 
		 * public String mediaAssestDataSchemaforWatchedMovies() {
		 * 
		 * return
		 * "  m.id AS mediaId,m.title AS title,m.image AS image, m.rating AS rating,'W' as assetTag,m.release_date,ed.event_Id as eventId, COUNT(eo.id) FROM b_media_asset m"
		 * +" inner join b_mod_detail ed on m.id=ed.media_id  inner JOIN b_modorder eo    ON (eo.event_id = ed.event_id)  ORDER BY 6 DESC LIMIT ?, 10"
		 * ; }
		 * 
		 * public String mediaAssestDataSchemaforPromotionalMovies() {
		 * 
		 * return
		 * " ed.event_id,ma.id AS mediaId,ma.title AS title,ma.image AS image, ed.event_Id as eventId,ma.rating AS rating,? as assetTag FROM b_media_asset ma inner join b_mod_detail ed"
		 * +"  on ed.media_id = ma.id  Where ed.event_id in (Select event_id  from b_mod_master em  inner join b_mod_detail ed on em.id=ed.event_id"
		 * +"  group by ed.event_id  having count(ed.event_id)>1) LIMIT ?, 10"; }
		 */
	}

	@Override
	public List<MediaAssetData> retrievemediaAssetdatabyNewRealease(final Long pageNo) {
		final MediaAssestDataMapper mediaAssestDataMapper = new MediaAssestDataMapper();
		String sql = "select *, ? as assetTag from mvNewRelease_vw  LIMIT ?, 10";
		return this.jdbcTemplate.query(sql, mediaAssestDataMapper, new Object[] { "N", pageNo });
	}

	@Override
	public List<MediaAssetData> retrievemediaAssetdatabyRating(final Long pageNo) {
		final MediaAssestDataMapper mediaAssestDataMapper = new MediaAssestDataMapper();
		String sql = "select *,? as assetTag from mvHighRate_vw  LIMIT ?, 10";
		return this.jdbcTemplate.query(sql, mediaAssestDataMapper, new Object[] { "R", pageNo });
	}

	@Override
	public List<MediaAssetData> retrieveAllmediaAssetdata() {
		final MediaAssestDataMapper mediaAssestDataMapper = new MediaAssestDataMapper();
		final String sql = "select " + mediaAssestDataMapper.mediaAssestDataSchema();
		return this.jdbcTemplate.query(sql, mediaAssestDataMapper, new Object[] { "As" });
	}

	@Override
	public Long retrieveNoofPages(final String query) {
		final NOOfPages pages = new NOOfPages();
		final String sql = "select " + pages.mediaAssestDataSchemaForPages(query);
		return this.jdbcTemplate.queryForObject(sql, pages, new Object[] {});
	}

	private static final class NOOfPages implements RowMapper<Long> {

		public Long mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			return resultSet.getLong("no_ofpages");
		}

		public String mediaAssestDataSchemaForPages(final String query) {
			return " ceil(count(0)/10) as no_ofpages from (" + query + ")a";
		}
	}

	@Override
	public MediaAssetData retrievemediaAsset(final Long id) {
		try {
			final AssestDataMapper mapper = new AssestDataMapper();
			final String sql = "Select " + mapper.scheme() + "  m.id=?";
			return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { id });
		} catch (EmptyResultDataAccessException accessException) {
			return null;
		}
	}

	@Override
	public List<MediaAssetData> retrievemediaAssetdatabyDiscountedMovies(final Long pageNo) {
		final MediaAssestDataMapper mediaAssestDataMapper = new MediaAssestDataMapper();
		String sql = "select *, ? as assetTag from mvDiscount_vw  LIMIT ?, 10 ";
		return this.jdbcTemplate.query(sql, mediaAssestDataMapper, new Object[] { "D", pageNo });
	}

	@Override
	public List<MediaAssetData> retrievemediaAssetdatabyPromotionalMovies(final Long pageNo) {
		final MediaAssestDataMapper mediaAssestDataMapper = new MediaAssestDataMapper();
		String sql = "select *,? as assetTag from mvPromotion_vw LIMIT ?, 10";
		return this.jdbcTemplate.query(sql, mediaAssestDataMapper, new Object[] { "P", pageNo });
	}

	@Override
	public List<MediaAssetData> retrievemediaAssetdatabyComingSoonMovies(final Long pageNo) {
		final MediaAssestDataMapper mediaAssestDataMapper = new MediaAssestDataMapper();
		String sql = "select  *,? as assetTag from mvComing_vw LIMIT ?, 10;";
		return this.jdbcTemplate.query(sql, mediaAssestDataMapper, new Object[] { "C", pageNo });
	}

	@Override
	public List<MediaAssetData> retrievemediaAssetdatabyMostWatchedMovies(final Long pageNo) {
		final MediaAssestDataMapper mediaAssestDataMapper = new MediaAssestDataMapper();
		String sql = "select *,? as assetTag  from mvWatched_vw Limit ?,10 ";
		return this.jdbcTemplate.query(sql, mediaAssestDataMapper, new Object[] { "W", pageNo });
	}

	@Override
	public List<MediaAssetData> retrievemediaAssetdatabySearching(final Long pageNo, final String filterType) {
		final MediaAssestDataMapper mediaAssestDataMapper = new MediaAssestDataMapper();
		final String sql = "select " + mediaAssestDataMapper.mediaAssestDataSchema()
				+ "  where upper(m.title) like upper('%" + filterType
				+ "%')  GROUP BY m.mediaId  having  media_count = 1  LIMIT ?, 10";
		return this.jdbcTemplate.query(sql, mediaAssestDataMapper, new Object[] { "A", pageNo });

	}

	@Override
	public List<MediaAssetData> retrieveAllAssetdata() {
		final MediaDataMapper mediaAssestDataMapper = new MediaDataMapper();
		final String sql = "select " + mediaAssestDataMapper.mediaAssestDataSchema();
		return this.jdbcTemplate.query(sql, mediaAssestDataMapper, new Object[] {});

	}

	@Override
	public List<MediaAssetData> retrieveAllMediaTemplatedata() {
		return null;
	}

	@Override
	public List<MediaassetAttribute> retrieveMediaAttributes() {
		final MediaAttributeMapper mapper = new MediaAttributeMapper();
		final String sql = "select " + mapper.scheme() + " m.code_name='MediaAttribute'";
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class MediaAttributeMapper implements RowMapper<MediaassetAttribute> {

		@Override
		public MediaassetAttribute mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final Long mediaId = resultSet.getLong("id");
			final String mediaName = resultSet.getString("codeValue");

			return new MediaassetAttribute(mediaId, mediaName);
		}

		public String scheme() {
			return " mc.id as id,mc.code_value as codeValue FROM m_code_value mc,m_code m where mc.code_id=m.id and ";
		}
	}

	private static final class MediaDataMapper implements RowMapper<MediaAssetData> {

		@Override
		public MediaAssetData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final Long mediaId = resultSet.getLong("id");
			final String mediaTitle = resultSet.getString("mediaTitle");
			final String status = resultSet.getString("status");
			final String EventCategory = resultSet.getString("EventCategory");
			final String mediaCategory = resultSet.getString("mediaCategory");
			final String contentProviderValue = resultSet.getString("contentProviderValue");
			final BigDecimal share = resultSet.getBigDecimal("share");
			final LocalDate releaseDate = JdbcSupport.getLocalDate(resultSet, "releaseDate");
			final Double price = resultSet.getDouble("price");

			return new MediaAssetData(mediaId, mediaTitle, status, releaseDate, share, EventCategory, mediaCategory,
					contentProviderValue, price);
		}

		public String mediaAssestDataSchema() {

			return " m.id AS id,m.title AS mediaTitle,m.status AS status,"
					+ "(select code_value from m_code_value v where v.id=m.type) as EventCategory,"
					+ "(select code_value from m_code_value v where v.id=m.category_id) as mediaCategory,"
					+ "(select code_value from m_code_value v where v.id=m.content_provider) as contentProviderValue,"
					+ "(select price from b_mod_pricing mp where mp.event_id=bm.id) as price,"
					+ "m.cp_share as share,m.release_date as releaseDate FROM b_media_asset m, b_mod_master bm "
					+ " where m.title=bm.event_description ";
		}
	}

	@Override
	public List<MediaassetAttribute> retrieveMediaFormatType() {
		final MediaAttributeMapper mapper = new MediaAttributeMapper();
		final String sql = "select " + mapper.scheme() + " m.code_name='MediaFormat'";
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class AssestDataMapper implements RowMapper<MediaAssetData> {

		@Override
		public MediaAssetData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final Long mediaId = resultSet.getLong("id");
			final String mediatitle = resultSet.getString("title");
			final String type = resultSet.getString("type");
			final String genre = resultSet.getString("genre");
			final Long catageoryId = resultSet.getLong("catageoryId");
			final LocalDate releaseDate = JdbcSupport.getLocalDate(resultSet, "releaseDate");
			final String subject = resultSet.getString("subject");
			final String overview = resultSet.getString("overview");
			final String image = resultSet.getString("image");
			final Long contentProvider = resultSet.getLong("contentProvider");
			final String rated = resultSet.getString("rated");
			final BigDecimal rating = resultSet.getBigDecimal("rating");
			final Long ratingCount = resultSet.getLong("ratingCount");
			final String status = resultSet.getString("status");
			final String duration = resultSet.getString("duration");
			final BigDecimal cpShareValue = resultSet.getBigDecimal("cpShareValue");

			return new MediaAssetData(mediaId, mediatitle, type, genre, catageoryId, releaseDate, subject, overview,
					image, contentProvider, rated, rating, ratingCount, status, duration, cpShareValue);
		}

		public String scheme() {

			return " m.id as id,m.title as title,m.type as type,m.category_id as catageoryId,m.genre as genre,m.release_date as releaseDate,"
					+ "m.overview as overview,m.subject as subject,m.image as image,m.content_provider as contentProvider,m.rated as rated, "
					+ "m.rating as rating,m.rating_count as ratingCount,m.status as status,m.duration as duration,m.cp_share as cpShareValue FROM b_media_asset m where m.is_deleted='N' and ";
		}
	}

	@Override
	public List<MediaEnumoptionData> retrieveMediaTypeData() {
		final MediaEnumoptionData movies = MediaTypeEnumaration.enumOptionData(MediaEnum.MOVIES);
		final MediaEnumoptionData tvSerails = MediaTypeEnumaration.enumOptionData(MediaEnum.TV_SERIALS);
		final MediaEnumoptionData comingSoon = MediaTypeEnumaration.enumOptionData(MediaEnum.COMING_SOON);

		final List<MediaEnumoptionData> categotyType = Arrays.asList(movies, tvSerails, comingSoon);
		return categotyType;
	}

	@Override
	public List<McodeData> retrieveMedaiCategory() {
		context.authenticatedUser();
		final SystemDataMapper mapper = new SystemDataMapper();
		final String sql = "select " + mapper.schema();
		return this.jdbcTemplate.query(sql, mapper, new Object[] { "Asset Category" });
	}

	private static final class SystemDataMapper implements RowMapper<McodeData> {
		public String schema() {
			return " mc.id as id,mc.code_value as codeValue from m_code m,m_code_value mc where m.id = mc.code_id and m.code_name=?";
		}

		@Override
		public McodeData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final String codeValue = resultSet.getString("codeValue");
			return new McodeData(id, codeValue);
		}
	}

	@Override
	public List<McodeData> retrieveLanguageCategeories() {
		context.authenticatedUser();
		final SystemDataMapper mapper = new SystemDataMapper();
		final String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] { "Asset language" });
	}

	@Override
	public List<MediaassetAttributeData> retrieveMediaassetAttributesData(final Long mediaId) {

		try {
			final MediaassetAttributesDataMapper mapper = new MediaassetAttributesDataMapper();
			final String sql = "select " + mapper.scheme() + mediaId;
			return this.jdbcTemplate.query(sql, mapper, new Object[] {});

		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class MediaassetAttributesDataMapper implements RowMapper<MediaassetAttributeData> {

		public String scheme() {
			return " ma.id as id,ma.attribute_type as attributeType,ma.attribute_name as attributeName,ma.attribute_value as attributeValue,"
					+ "ma.attribute_nickname as attributeNickname,ma.attribute_image as attributeImage from b_mediaasset_attributes ma "
					+ "where ma.media_id= ";
		}

		@Override
		public MediaassetAttributeData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final Long id = resultSet.getLong("id");
			final String attributeType = resultSet.getString("attributeType");
			final Long attributeName = resultSet.getLong("attributeName");
			final String attributeValue = resultSet.getString("attributeValue");
			final String attributeNickname = resultSet.getString("attributeNickname");
			final String attributeImage = resultSet.getString("attributeImage");

			return new MediaassetAttributeData(attributeType, attributeName, attributeValue, attributeNickname,
					attributeImage, id);
		}
	}

	@Override
	public List<MediaLocationData> retrievemediaAssetLocationdata(final Long mediaId) {
		try {
			final MediaLocationDataMapper mapper = new MediaLocationDataMapper();
			final String sql = "select " + mapper.scheme() + mediaId;
			return this.jdbcTemplate.query(sql, mapper, new Object[] {});

		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class MediaLocationDataMapper implements RowMapper<MediaLocationData> {

		public String scheme() {
			return " ml.id as id,ml.language_id as languageId,ml.format_type as formatType,ml.location as location from "
					+ "b_mediaasset_location ml where ml.media_id= ";
		}

		@Override
		public MediaLocationData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long languageId = resultSet.getLong("languageId");
			final String formatType = resultSet.getString("formatType");
			final String location = resultSet.getString("location");

			return new MediaLocationData(languageId, formatType, location);
		}
	}

	@Override
	public List<McodeData> retrieveContentProviders() {
		context.authenticatedUser();
		final SystemDataMapper mapper = new SystemDataMapper();
		final String sql = "select " + mapper.schema();
		return this.jdbcTemplate.query(sql, mapper, new Object[] { "Content Provider" });
	}

	@Override
	public List<String> getAllMovieItemDetails() {
		final MovieItemMapper MovieItemMapper = new MovieItemMapper();
		final String sql = "select " + MovieItemMapper.scheme();
		return this.jdbcTemplate.query(sql, MovieItemMapper);
	}

	private static final class MovieItemMapper implements RowMapper<String> {

		public String scheme() {
			return " pw.movie_name as movieName,pw.price as price ,pw.ngb_item_id as NGBItemId,pw.item_code as itemCode,pw.item_type as itemType from pw_movie_item_details pw ";
		}

		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			String movieName = rs.getString("movieName");
			String NGBItemId = rs.getString("NGBItemId");
			String itemCode = rs.getString("itemCode");
			String itemType = rs.getString("itemType");
			Double price = rs.getDouble("price");

			return movieName;
		}

	}

	@Override
	public MediaAssetData retrieveMediaAssestByOverview(String overview) {
		// TODO Auto-generated method stub
		try {
		this.context.authenticatedUser();
		final OverViewMapper overViewMapper = new OverViewMapper();
		final String sql = "select " + overViewMapper.scheme();
		return this.jdbcTemplate.queryForObject(sql, overViewMapper,new Object[] {overview});
		}catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class OverViewMapper implements RowMapper<MediaAssetData> {

		public String scheme() {
			return "m.id as id, m.title as title, m.category_id as categoryId, m.release_date as releaseDate, m.image as image, "
					+ "m.status as status from b_media_asset m where overview = ? ";
		}

		@Override
		public MediaAssetData mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long  mediaId = rs.getLong("id");
			String movieName = rs.getString("title");
			String status = rs.getString("status");
			LocalDate releaseDate = JdbcSupport.getLocalDate(rs, "releaseDate");
			return new MediaAssetData(mediaId,movieName,status,releaseDate, null,
					null, null, null,null) ;
		}		

	}
}