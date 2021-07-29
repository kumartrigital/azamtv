package org.mifosplatform.scheduledjobs.dataupload.service;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.scheduledjobs.dataupload.data.UploadStatusData;

public interface DataUploadReadPlatformService {

    Page<UploadStatusData> retrieveAllUploadStatusData(SearchSqlQuery searchUploads);
}
