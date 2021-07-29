package org.mifosplatform.organisation.usercataloge.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.salescatalogemapping.domain.SalesCatalogeMapping;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_user_cataloge")
public class UserCataloge extends AbstractPersistable<Long>{
	
	@Column(name="user_id", nullable=false)
	private Long userId;
	
	@Column(name="cataloge_id", nullable=false)
	private Long catalogeId;
	
	@Column(name="is_deleted")
	private char isDeleted;

	public UserCataloge() {}
	
	public UserCataloge(Long userId, Long catalogeId) {
		this.userId = userId;
		this.catalogeId = catalogeId;
		this.isDeleted = 'N';
	}

	public UserCataloge(Long id) {
         this.catalogeId = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getCatalogeId() {
		return catalogeId;
	}

	public void setCatalogeId(Long catalogeId) {
		this.catalogeId = catalogeId;
	}

	

	/*public void addSalesCataloge(Set<UserCataloge> selectedSalesCataloges) {
		for(UserCataloge userCataloge:this.usercataloge){
			boolean isExist =false;
			for(UserCataloge selectedSalesCataloge:selectedSalesCataloges){
				if(userCataloge.getCatalogeId()==(selectedSalesCataloge.getCatalogeId()) 
						&& userCataloge.getIsDeleted() =='N'){
					isExist=true;
					selectedSalesCataloges.remove(selectedSalesCataloges);break;
				}
			}
			if(!isExist){
				userCataloge.delete();
			}
		}
		for(UserCataloge selectedSalesCataloge:selectedSalesCataloges){
			selectedSalesCataloge.update(this);
			this.usercataloge.add(selectedSalesCataloge);
		}
	}
	
	
	private void update(final UserCataloge userCataloge) {
		this.usercataloge = usercataloge;
	}*/

	public static UserCataloge formJson(JsonCommand command) {
		
		Long userId  = command.longValueOfParameterNamed("userId");
		
		return new UserCataloge(userId);
	}
	
	public Map<String, Object> update(JsonCommand command) {
		
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
				
				final String userIdNamedParamName = "userId";
			    //final String salesCatalogeDetailsParamName = "salesCatalogeDetails";
				
				
				if(command.isChangeInLongParameterNamed(userIdNamedParamName, this.userId)){
					final Long newValue = command.longValueOfParameterNamed(userIdNamedParamName);
					actualChanges.put(userIdNamedParamName, newValue);
					this.userId = newValue;
				}
				
				/*if(command.isChangeInArrayParameterNamed(salesCatalogeDetailsParamName, getSalesCatalogeAsIdStringArray())){
					final String[] newValue = command.arrayValueOfParameterNamed(salesCatalogeDetailsParamName);
					actualChanges.put(salesCatalogeDetailsParamName, newValue);
				}*/
				
				return actualChanges;
   }
	
	
	
	/*private String[] getSalesCatalogeAsIdStringArray() {
		final List<String> roleIds = new ArrayList<>();	
		for (final UserCataloge details : this.usercataloge) {
       		roleIds.add(details.getId().toString());
       	}
       	return roleIds.toArray(new String[roleIds.size()]);
	}*/

	public char getIsDeleted() {
		return isDeleted;
	}
	
	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void delete() {
		if(this.isDeleted != 'Y'){
		this.isDeleted = 'Y';
	}
		}

	
}