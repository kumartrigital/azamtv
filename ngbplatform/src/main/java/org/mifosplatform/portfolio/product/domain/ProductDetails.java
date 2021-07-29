package org.mifosplatform.portfolio.product.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_product_detail")
public class ProductDetails extends AbstractPersistable<Long> {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "param_name")
	private String paramName;
	
	@Column(name = "param_type")
	private String paramType;
	
	@Column(name = "param_value")
	private String paramValue;
	
	@Column(name = "param_category")
	private String paramCategory;

	@Column(name = "is_deleted")
	private String isDeleted = "N";

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
	
	public ProductDetails() {
	}
	
	public ProductDetails(final String paramName,final String paramType,final String paramValue,final String paramCategory) {

		this.paramName = paramName;
		this.paramType = paramType;
		this.paramValue = paramValue;
		this.paramCategory = paramCategory;

	}
	
	
	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	public String getparamCategory() {
		return paramCategory;
	}

	public void setParamCategory(String paramCategory) {
		this.paramCategory = paramCategory;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void update(Product product) {
		this.product = product;
	}

	public void delete() {

		this.isDeleted = "Y";

	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

}
