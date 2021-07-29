package org.mifosplatform.logistics.supplier.data;

public class SupplierData {
	
	final private Long id;
	final private String supplierCode;
	final private String supplierDescription;
	final private String supplierAddress;
	
	public SupplierData(Long id,String supplierCode,String supplierDescription,String supplierAddress) {
		this.id = id;
		this.supplierCode = supplierCode;
		this.supplierDescription = supplierDescription;
		this.supplierAddress = supplierAddress;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	

	/**
	 * @return the supplierCode
	 */
	public String getSupplierCode() {
		return supplierCode;
	}

	
	/**
	 * @return the supplierDescription
	 */
	public String getSupplierDescription() {
		return supplierDescription;
	}

	
	/**
	 * @return the supplierAddress
	 */
	public String getSupplierAddress() {
		return supplierAddress;
	}

	
}
