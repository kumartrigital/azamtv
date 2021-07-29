package org.mifosplatform.organisation.voucher.service;

import java.math.BigDecimal;
import java.util.List;

import javax.ws.rs.core.StreamingOutput;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.voucher.data.ExportVoucherData;
import org.mifosplatform.organisation.voucher.data.VoucherData;
import org.mifosplatform.organisation.voucher.data.VoucherPinConfigValueData;
import org.mifosplatform.organisation.voucher.data.VoucherRequestData;
import org.mifosplatform.organisation.voucher.domain.VoucherDetails;

/**
 * 
 * @author ashokreddy
 *
 */
public interface VoucherReadPlatformService {

	String retrieveIndividualPin(String pinId);
	
	//Page<VoucherData> getAllVoucherById(SearchSqlQuery searchTicketMaster, String statusType, Long id);

	List<EnumOptionData> pinCategory();

	List<EnumOptionData> pinType();

	Long retrieveMaxNo(Long minNo, Long maxNo);

	StreamingOutput retrieveVocherDetailsCsv(Long batchId);

	List<VoucherData> retrivePinDetails(String pinNumber, String pinType);

	Page<VoucherData> getAllData(SearchSqlQuery searchVouchers);

//	List<VoucherData> getVocherDetailsByPurchaseNo(String purchaseNo);

	StreamingOutput retrieveVocherDetailsCsvByBatchName(String batchName);

	VoucherPinConfigValueData getVoucherPinConfigValues(String configVoucherpinValues);

	List<VoucherData> getAllVoucherByStatus(String statusType, Long quantity);

	StreamingOutput retrieveVocherDetailsCsvByStatus(String status);

	Long retriveQuantityByStatus(String status,Long fromOffice,BigDecimal unitPrice,Long pinValue,Boolean isProduct);

	Long retriveQuantityBySaleRefId(Long saleRefId);



	List<VoucherData> retrieveVocherDetailsBySaleRefId(Long saleRefId, Integer quantity);

	VoucherRequestData retrieveVocherRequestDetails(Long saleRefId);

	List<VoucherData> retrieveVocherDetails(Long saleRefId);

	Page<VoucherData> getAllVoucherByOfficeId(Long officeId);

	VoucherData retriveVoucherPinDetails(String pinNumber, Long officeId);

	List<ExportVoucherData> retrieveExportRequestDetails(Long officeId, Long salereqNo);

	ExportVoucherData exportVoucherDetails(String requestId);

	ExportVoucherData retrieveExportRequestDetailsByRequestId(String requestId);

	List<VoucherData> retriveVoucherDetailsByRequestId(String requestId);

	VoucherData retriveVoucherPinDetailsWithOutOffice(String voucherId);

	Page<VoucherData> getAllVoucherById(SearchSqlQuery searchVoucher, String statusType, Long id);

	VoucherData retriveVoucherPinDetailsWithPriceValue(String pinNumber, Long officeId, BigDecimal eventValue);


	VoucherData retriveVoucherDetailsOfficeIdAndType(Long officeId, String pinType);

	VoucherData retriveVoucherDetailsOfficeIdAndValueType(Long officeId, String pinType, BigDecimal value);

	void batchUpdate(List<VoucherData> voucherList,String exportReqId);
	


	


}
