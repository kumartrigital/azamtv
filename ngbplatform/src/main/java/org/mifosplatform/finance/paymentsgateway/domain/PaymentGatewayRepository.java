package org.mifosplatform.finance.paymentsgateway.domain;

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

public interface PaymentGatewayRepository
		extends JpaRepository<PaymentGateway, Long>, JpaSpecificationExecutor<PaymentGateway> {

	@Query("from PaymentGateway paymentGateway where paymentGateway.paymentId=:paymentId")
	PaymentGateway findPaymentDetailsByPaymentId(@Param("paymentId") String paymentId);

	
	
	/*
	 * @Modifying(clearAutomatically = true)
	 * 
	 * @Query(value =
	 * "update b_paymentgateway set t_details =:tdetails ,t_status =:tstatus where payment_id =:paymentId"
	 * ,nativeQuery = true) int updateTransactionstatus(@Param("tdetails") String
	 * tdetails,@Param("tstatus") String tstatus,@Param("paymentId") String
	 * paymentId);
	 */
}
