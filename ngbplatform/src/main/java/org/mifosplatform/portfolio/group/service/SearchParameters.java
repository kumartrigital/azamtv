package org.mifosplatform.portfolio.group.service;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.accounting.journalentry.api.DateParam;

public final class SearchParameters {

    private final String sqlSearch;
    private final Long officeId;
    private final String externalId;
    private final String name;
    private final String hierarchy;
    private final String firstname;
    private final String lastname;
    private final Integer offset;
    private final Integer limit;
	private final String orderBy;
    private final String sortOrder;
    private final String groupName;
    private final String status;
    private Date fromDataParam;
    private Date toDateParam;
    private final String category;
    private final Long assignedTo;
    private final Long closedBy;
    private final Long createdBy;
    private final String emailId;
    private final String source;
    private final String phone;
    private final String searchType;
    private final String city;
    private final String address;
	private final Long clientId;
	
    
    public static SearchParameters from(final String sqlSearch, final Long officeId, final String externalId, final String name,
            final String hierarchy) {
        return new SearchParameters(sqlSearch, officeId, externalId, name, hierarchy, null, null, null, null, null, null,null,null,
        		null,null,null,null,null,null,null,null,null,null,null,null,null);
    }
    

	public static SearchParameters forTickets(String searchText,DateParam fromDateParam, DateParam toDateParam, Long assignedTo,
			 Long closedBy, String category,String status, Integer limit, Integer offset, String name, Long createdBy, String emailId, String source,
			 String phone, String searchType, String city, String address, String externalId) {
		
		return new SearchParameters(searchText,null,externalId,name,null,null,null, offset, limit, null, null,null, status,fromDateParam,toDateParam,
				assignedTo,closedBy,category,createdBy,emailId,source,phone,searchType,city,address,null);
	}

    public static SearchParameters forClients(final String sqlSearch, final Long officeId, final String externalId,
            final String displayName, final String firstname, final String lastname, final String hierarchy, final Integer offset,
            final Integer limit, final String orderBy, final String sortOrder,final String groupName,final String status) {

        Integer maxLimitAllowed = 200;
        if (limit != null && limit < maxLimitAllowed && limit > 0) {
            maxLimitAllowed = limit;
        }

        return new SearchParameters(sqlSearch, officeId, externalId, displayName, hierarchy, firstname, lastname, offset, maxLimitAllowed,
                orderBy, sortOrder,groupName,status,null,null,null,null,null,null,null,null,null,null,null,null,null);
    }
    
    public static SearchParameters newforClients(String sqlSearch,Long officeId, String externalId, String firstname, String lastname, String email, String phone, 
			String city, String categoryType,Long clientId,Integer offset,Integer limit) {
		
    	return new SearchParameters(sqlSearch, officeId, externalId, null, null, firstname, lastname, offset, limit,
                null, null,null,null,null,null,null,null,categoryType,null,email,null,phone,null,city,null,clientId);
	}

    private SearchParameters(final String sqlSearch, final Long officeId, final String externalId, final String name,
            final String hierarchy, final String firstname, final String lastname, final Integer offset, final Integer limit,
            final String orderBy, final String sortOrder,final String groupName,final String status, DateParam fromDateParam,
            DateParam toDateParam, Long assignedTo, Long closedBy, String category, Long createdBy, String emailId, String source,
            String phone,String searchType, String city, String address,final Long clientId) {
    	
        this.sqlSearch = sqlSearch;
        this.officeId = officeId;
        this.externalId = externalId;
        this.name = name;
        this.hierarchy = hierarchy;
        this.firstname = firstname;
        this.lastname = lastname;
        this.offset = offset;
        this.limit = limit;
        this.orderBy = orderBy;
        this.sortOrder = sortOrder;
        this.groupName=groupName;
        this.status=status;
        this.fromDataParam=fromDateParam != null?fromDateParam.getDate():null;
        this.toDateParam=toDateParam != null?toDateParam.getDate():null;
        this.assignedTo=assignedTo;
        this.closedBy=closedBy;
        this.category=category;
        this.createdBy = createdBy;
        this.emailId = emailId;
        this.source = source;
        this.phone = phone;
        this.searchType = searchType;
        this.city = city;
        this.address = address;
        this.clientId= clientId;
        
    }

    public String getGroupName() {
		return groupName;
	}
    public boolean isOrderByRequested() {
        return StringUtils.isNotBlank(this.orderBy) && StringUtils.isNotBlank(this.sortOrder);
    }

    public String getSqlSearch() {
        return this.sqlSearch;
    }

    public Long getOfficeId() {
        return this.officeId;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public String getName() {
        return this.name;
    }

    public String getHierarchy() {
        return this.hierarchy;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public Integer getOffset() {
        return this.offset;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public String getSortOrder() {
        return this.sortOrder;
    }

    public boolean isLimited() {
        return this.limit != null && this.limit.intValue() > 0;
    }

    public boolean isOffset() {
        return this.offset != null;
    }
    
	 public Date getFromDataParam() {
		return fromDataParam;
	}


	public Date getToDateParam() {
		return toDateParam;
	}


	public String getCategory() {
		return category;
	}


	public Long getAssignedTo() {
		return assignedTo;
	}


	public Long getClosedBy() {
		return closedBy;
	}


	public static SearchParameters forPagination(final Integer offset, final Integer limit, final String orderBy,
            final String sortOrder) {

        Integer maxLimitAllowed = getCheckedLimit(limit);

        return new SearchParameters(null, null, null, null, null, null, null, offset, maxLimitAllowed, orderBy, sortOrder,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    }

	public static Integer getCheckedLimit(final Integer limit) {

        final Integer maxLimitAllowed = 200;
        // default to max limit first off
        Integer checkedLimit = maxLimitAllowed;

        if (limit != null && limit > 0) {
            checkedLimit = limit;
        } else if (limit != null) {
            // unlimited case: limit provided and 0 or less
            checkedLimit = null;
        }

        return checkedLimit;
    }

	 public boolean isSortOrderProvided() {
        return StringUtils.isNotBlank(this.sortOrder);
    }
	public boolean isGroupNameProvided(){
		return StringUtils.isNotBlank(this.groupName);
	}

	public String getStatus() {
		return status;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public String getEmailId() {
		return emailId;
	}

	public String getSource() {
		return source;
	}

	public String getPhone() {
		return phone;
	}

	public String getSearchType() {
		return searchType;
	}

	public String getCity() {
		return city;
	}

	public String getAddress() {
		return address;
	}


	

	


	
	
}