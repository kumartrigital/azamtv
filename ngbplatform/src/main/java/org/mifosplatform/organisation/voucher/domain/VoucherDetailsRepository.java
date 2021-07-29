package org.mifosplatform.organisation.voucher.domain;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author ashokreddy
 *
 */
public interface VoucherDetailsRepository
		extends JpaRepository<VoucherDetails, Long>, JpaSpecificationExecutor<VoucherDetails> {

	// and voucherDetails.clientId is null
	@Query("from VoucherDetails voucherDetails where voucherDetails.pinNo =:pinNumber and voucherDetails.status != 'USED' and voucherDetails.isDeleted = 'N'")
	VoucherDetails findOneByPinNumber(@Param("pinNumber") String pinNumber);

	@Query("from VoucherDetails voucherDetails where voucherDetails.pinNo =:pinNumber  and voucherDetails.isDeleted = 'N'")
	VoucherDetails findOneByPinNumberForUSedMessage(@Param("pinNumber") String pinNumber);

	
	@Query("select VoucherDetails from VoucherDetails voucherDetails, Voucher voucher where voucher.officeId = :officeId and voucher.pinValue =:amount and voucher.serialNo = voucherDetails.serialNo and voucherDetails.status = 'NEW' and voucherDetails.isDeleted = 'N'")
	VoucherDetails findOneDetailsByAmount(@Param("officeId") Long office, @Param("amount") String amount);

	@Transactional
	@Modifying
	@Query(value = "update b_pin_details pd set pd.office_id = :toOffice, pd.sale_ref_no = :saleRefNo , pd.status = 'ALLOCATED' "
			+ "where pd.office_id=:fromOffice and pd.status ='NEW' and pin_id in (select id from b_pin_master where pin_value= :pinValue) limit :quantity ", nativeQuery = true)
	void updateVoucherOffice(@Param("toOffice") Long toOffice, @Param("quantity") Long quantity,
			@Param("saleRefNo") Long saleRefNo, @Param("fromOffice") Long fromOffice,
			@Param("pinValue") BigDecimal pinValue);

	@Transactional
	@Modifying
	@Query(value = "update b_pin_details pd set pd.office_id = :toOffice, pd.sale_ref_no = :saleRefNo , pd.status = 'ALLOCATED' "
			+ "where pd.office_id=:fromOffice and pd.status ='NEW' and pin_id in (select pm.id from b_pin_master pm left join  b_plan_pricing pp on pm.price_id=pp.id  "
			+ "WHERE pm.pin_type= 'PRODUCT' and pm.price_id= :priceId) limit :quantity ", nativeQuery = true)
	void updateProductTypeVoucherOffice(@Param("toOffice") Long toOffice, @Param("quantity") Long quantity,
			@Param("saleRefNo") Long saleRefNo, @Param("fromOffice") Long fromOffice,@Param("priceId") Long priceId);

	@Transactional
	@Modifying
	@Query(value = "update b_pin_details pd set pd.export_req_id = :exportReqId, pd.status = 'EXPORTED' where pd.pin_no=:pinNum ", nativeQuery = true)
	void updateExportReqId(@Param("exportReqId") String exportReqId, @Param("pinNum") String pinNum);

	@Query(value = "select a.pin_type from b_pin_master a join b_pin_details b on a.id=b.pin_id where b.pin_no=:pinNumber", nativeQuery = true)
	String findVoucherType(@Param("pinNumber")String pinNumber);




	
}
