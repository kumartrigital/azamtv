package org.mifosplatform.organisation.usercataloge.data;

import java.util.List;

import org.mifosplatform.organisation.salescataloge.data.SalesCatalogeData;
import org.mifosplatform.useradministration.data.AppUserData;

public class UserCatalogeData {

	private Long id;
	private Long userId;
	private Long catalogeId;
	private String username;
	private String name;
	
	//for template purpose
	private List<SalesCatalogeData> salesCatalogeDatas;
	private List<AppUserData> appUserDatas;
	
	private List<SalesCatalogeData> availableCatalog;
	private List<SalesCatalogeData> selectedCatalog;
	
	public UserCatalogeData() {
	}
	
	public UserCatalogeData(Long id, Long userId, Long catalogeId,String username,String name) {
		this.id = id;
		this.userId = userId;
		this.catalogeId = catalogeId;
		this.username = username;
		this.name = name;
		
	}

	/*public UserCatalogeData(final List<SalesCatalogeData> salesCatalogeDatas,final List<AppUserData> appUserDatas) {
		this.salesCatalogeDatas = salesCatalogeDatas;
		this.appUserDatas = appUserDatas;
	}*/

	public UserCatalogeData(final List<SalesCatalogeData> salesCatalogeDatas,final List<AppUserData> appUserDatas) {
		this.availableCatalog = availableCatalog;
		this.selectedCatalog = selectedCatalog;
	}

	public Long getId() {
		return id;
	}


	public Long getUserId() {
		return userId;
	}


	public Long getCatalogeId() {
		return catalogeId;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getName() {
		return name;
	}

	/*public List<SalesCatalogeData> getSalesCatalogeDatas() {
		return salesCatalogeDatas;
	}
	
	public void setSalesCatalogeDatas(List<SalesCatalogeData> salesCatalogeDatas) {
		this.salesCatalogeDatas = salesCatalogeDatas;
		
	}

	public List<AppUserData> getAppUserDatas() {
		return appUserDatas;
	}
	public void setAppUserDatas(List<AppUserData> appUserDatas) {
		this.appUserDatas = appUserDatas;
		
	}*/

	public List<AppUserData> getAppUserDatas() {
		return appUserDatas;
	}
	public void setAppUserDatas(List<AppUserData> appUserDatas) {
		this.appUserDatas = appUserDatas;
		
	}
	
	public List<SalesCatalogeData> getAvailaableSalesCatalogeDatas() {
		return availableCatalog;
	}
	
	public void setAvailaableSalesCatalogeDatas(List<SalesCatalogeData> salesCatalogeDatas) {
		this.availableCatalog = salesCatalogeDatas;
		
	}

	public List<SalesCatalogeData> getSelectedSalesCatalogeDatas() {
		return selectedCatalog;
	}
	public void setSelectedSalesCatalogeDatas(List<SalesCatalogeData> salesCatalogeDatas) {
		this.selectedCatalog = salesCatalogeDatas;
		
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	
}
