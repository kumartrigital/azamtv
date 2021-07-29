package org.mifosplatform.portfolio.order.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "b_order_line")
public class OrderLine extends AbstractAuditableCustom<AppUser, Long>  {

/*@Id
@GeneratedValue
@Column(name="id")
private Long id;
*/

    @ManyToOne
    @JoinColumn(name="order_id")
	private Order orders;

	/*@Column(name = "service_id")
	private Long serviceId;*/

    @Column(name = "product_id")
	private Long productId;
    
    @Column(name = "product_poid")
	private Long productPoId;
    
    
	@Column(name = "service_type")
	private String serviceType;

	@Column(name = "service_status")
	private Long serviceStatus;

	@Column(name = "purchase_product_poid")
	private Long purchaseProductPoId;
	
	@Column(name = "is_deleted")
	private char isDeleted;

	public OrderLine()
	{}

	public OrderLine(final Long productId,final Long productPoId, final String serviceType,final Long serviceStatus,final char isdeleted )
	 {
		this.orders=null;
		this.productId=productId;
		this.productPoId = productPoId;
		this.serviceStatus=serviceStatus;
		this.isDeleted=isdeleted;
		this.serviceType=serviceType;

	 }
	
    public OrderLine(final String serviceCode)
     {
	this.serviceType=serviceCode;
	 }

	 public Order getOrderId() {
		return orders;
	 }
  
	 /*public Long getServiceId() {
		return serviceId;
	 }*/
	 
	 public Long getProductId() {
			return productId;
	}
	
	 public String getServiceType() {
		return serviceType;
	 }


	public Long getServiceStatus() {
		return serviceStatus;
	}


	public char isDeleted() {
		return isDeleted;
	}
	public  void update(Order order)
	{
		this.orders=order;

	}

	public void delete() {

		this.isDeleted='y';
		


	}

	public Long getPurchaseProductPoId() {
		return purchaseProductPoId;
	}

	public void setPurchaseProductPoId(Long purchaseProductPoId) {
		this.purchaseProductPoId = purchaseProductPoId;
	}

	public Long getProductPoId() {
		return productPoId;
	}

	public void setProductPoId(Long productPoId) {
		this.productPoId = productPoId;
	}


	

}
