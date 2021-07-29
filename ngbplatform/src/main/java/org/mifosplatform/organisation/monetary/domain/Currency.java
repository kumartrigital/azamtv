package org.mifosplatform.organisation.monetary.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;
public class Currency{}
/*@Entity
@Table(name = "m_currency")
public class Currency extends AbstractPersistable<Long> {

	@Column(name = "code", nullable = false, length = 3)
	private String code;

	@Column(name = "decimal_places", nullable = false)
	private Integer decimalPlaces;

	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@Column(name = "internationalized_name_code", nullable = false, length = 50)
	private String nameCode;

	@Column(name = "display_symbol", nullable = true, length = 10)
	private String displaySymbol;
	
	@Column(name = "country_code", nullable = false, length = 50)
	private String countryCode;
	
	@Column(name = "country_name", nullable = false, length = 50)
	private String countryName;
	
	@Column(name = "type", nullable = false, length = 50)
	private String type;

	public Currency() {
		
	}
	
	private Currency(String code, String name,int decimalPlaces, 
			String nameCode,String displaySymbol,String countryCode,String countryName,String type) {
		this.code = code;
		this.name = name;
		this.decimalPlaces = decimalPlaces;
		this.nameCode = nameCode;
		this.displaySymbol = displaySymbol;
		this.countryCode = countryCode;
		this.countryName = countryName;
		this.type = type;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Integer getDecimalPlaces() {
		return this.decimalPlaces;
	}
	
	public void setDecimalPlaces(Integer decimalPlaces) {
		this.decimalPlaces = decimalPlaces;
	}

	public String getNameCode() {
		return nameCode;
	}
	
	public void setNameCode(String nameCode) {
		this.nameCode = nameCode;
	}

	public String getDisplaySymbol() {
		return displaySymbol;
	}
	
	public void setDisplaySymbole(String displaySymbol) {
		this.displaySymbol = displaySymbol;
	}
	
	public String getCountryCode() {
		return countryCode;
	}
	
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public String getCountryName() {
		return countryName;
	}
	
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	
	public static Currency formJson(JsonCommand command) {
		String code = command.stringValueOfParameterNamed("code");
		String name = command.stringValueOfParameterNamed("name");
		Integer decimalPlaces = command.integerValueOfParameterNamed("decimalPlaces");
		String nameCode  = command.stringValueOfParameterNamed("nameCode");
		String displaySymbol  = command.stringValueOfParameterNamed("displaySymbol");
		String countryCode  = command.stringValueOfParameterNamed("countryCode");
		String countryName  = command.stringValueOfParameterNamed("countryName");
		String type  = command.stringValueOfParameterNamed("type");
		
		return new Currency(code, name, decimalPlaces,
				nameCode, displaySymbol,countryCode,countryName,type);
	}

	
}*/