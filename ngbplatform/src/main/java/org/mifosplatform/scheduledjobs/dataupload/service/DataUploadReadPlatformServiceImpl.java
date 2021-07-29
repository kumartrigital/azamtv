package org.mifosplatform.scheduledjobs.dataupload.service;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.joda.time.LocalDate;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.scheduledjobs.dataupload.data.UploadStatusData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class DataUploadReadPlatformServiceImpl implements DataUploadReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final PaginationHelper<UploadStatusData> paginationHelper = new PaginationHelper<UploadStatusData>();
    
    @Autowired
    public DataUploadReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class UploadStatusMapper implements RowMapper<UploadStatusData> {

        public String schema() {
            return " u.id as Id ,u.upload_process as uploadProcess,u.upload_filepath as uploadFilePath,u.total_records as totalRecords,u.process_date as processDate," +
            		"u.process_status as processStatus,u.process_records as processRecords,u.unprocess_records as unprocessRecords,u.error_message as errorMessage " +
            		"from b_data_uploads u ";
        }

        @Override
        public UploadStatusData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
        	
         final Long id = rs.getLong("Id");
         final String uploadProcess=rs.getString("uploadProcess");
       	 final String uploadFilePath=rs.getString("uploadFilePath");
       	 final File  uploadFile=new File(uploadFilePath);
       	 final String fileName=uploadFile.getName();
       	 final String filePath=uploadFilePath.substring(0,11).concat("......").concat(File.separator+fileName);
         final LocalDate processDate= JdbcSupport.getLocalDate(rs,"processDate");
       	 final String processStatus=rs.getString("processStatus");
       	 final Long processRecords=rs.getLong("processRecords");
       	 final Long totalRecords=rs.getLong("totalRecords");
       	 final String errorMessage=rs.getString("errorMessage");
       	 final Long unprocessRecords=rs.getLong("unprocessRecords");
        
       	 return new UploadStatusData(id,uploadProcess,filePath,processDate,processStatus,processRecords,unprocessRecords,errorMessage,totalRecords);
       	
        }
    }

    @Override
    public Page<UploadStatusData> retrieveAllUploadStatusData(final SearchSqlQuery searchUploads) {
        
    	context.authenticatedUser();
        final UploadStatusMapper rm = new UploadStatusMapper();
       
    	StringBuilder sqlBuilder = new StringBuilder(200);
    	sqlBuilder.append("select ").append(rm.schema()).append(" order by u.id desc");

    	if (searchUploads.isLimited()) {
            sqlBuilder.append(" limit ").append(searchUploads.getLimit());
        }

        if (searchUploads.isOffset()) {
            sqlBuilder.append(" offset ").append(searchUploads.getOffset());
        }
        
        return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
	            new Object[] {}, rm);
    }
}