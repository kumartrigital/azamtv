package org.mifosplatform.celcom.domain;

public enum SearchTypeEnum {
	EQUALS(0,"EQUALS"),
	STARTS_WITH(1,"STARTS_WITH"),
	ENDS_WITH(2,"ENDS_WITH"),
	CONTAINS(3,"CONTAINS");
	private final Integer id;
	private final String Name;

    private SearchTypeEnum(final Integer id, final String Name) {
        this.id = id;
		this.Name = Name;
    }

    public Integer getValue() {
        return this.id;
    }

	public String getCode() {
		return Name;
	}
	public static SearchTypeEnum fromInt(final Integer frequency) {

		SearchTypeEnum searchTypeEnum = SearchTypeEnum.EQUALS;
		switch (frequency) {
		case 1:
			searchTypeEnum = SearchTypeEnum.STARTS_WITH;
			break;
		case 2:
			searchTypeEnum = SearchTypeEnum.ENDS_WITH;
			break;
		case 3:
			searchTypeEnum = SearchTypeEnum.CONTAINS;
			break;
		default:
			searchTypeEnum = SearchTypeEnum.EQUALS;
			break;
		}
		return searchTypeEnum;
	}
	
}
