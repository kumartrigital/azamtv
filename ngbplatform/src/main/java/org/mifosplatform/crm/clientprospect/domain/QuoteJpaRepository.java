package org.mifosplatform.crm.clientprospect.domain;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuoteJpaRepository extends JpaRepository<Quote, Long>,
JpaSpecificationExecutor<Quote> {

	@Query("from Quote quote where quote.id =:quoteId and is_deleted='N'")
	Quote findQuoteCheckDeletedStatus(@Param("quoteId") Long quoteId);

	
	@Query("from Quote quote where quote.leadId =:leadId and is_deleted='N'")
	Quote findQuotesByLeadId(@Param("leadId") Long leadId);
	
	
	@Query("from Quote quote where quote.leadId =:leadId and is_deleted='N'")
	List<Quote> findQuotessByLeadId(@Param("leadId") Long leadId);
	
	

@Query("from Quote quote where quote.id =:quoteId and is_deleted='N'")
Quote findQuotesByQuoteId(@Param("quoteId") Long quoteId);
}
