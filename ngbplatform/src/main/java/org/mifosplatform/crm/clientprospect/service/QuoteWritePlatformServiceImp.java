package org.mifosplatform.crm.clientprospect.service;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.LocaleUtils;
import org.mifosplatform.crm.clientprospect.domain.ClientProspect;
import org.mifosplatform.crm.clientprospect.domain.Quote;
import org.mifosplatform.crm.clientprospect.domain.QuoteJpaRepository;
import org.mifosplatform.crm.clientprospect.domain.QuoteOrder;
import org.mifosplatform.crm.clientprospect.exception.QuotationNotFoundException;
import org.mifosplatform.crm.clientprospect.serialization.QuoteCommandFromApiJsonDeserializer;
import org.mifosplatform.finance.billingmaster.domain.BillMaster;
import org.mifosplatform.finance.billingmaster.service.BillWritePlatformServiceImpl;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.service.domain.ServiceMaster;
import org.mifosplatform.portfolio.service.domain.ServiceMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
@Service
public class QuoteWritePlatformServiceImp implements QuoteWritePlatformService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(QuoteWritePlatformServiceImp.class);

	private final PlatformSecurityContext context;
	private final QuoteCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final QuoteJpaRepository quoteJpaRepository;
	private final PlanRepository planRepository;
	private final FromJsonHelper fromApiJsonHelper;
	private final  ServiceMasterRepository serviceMasterRepository;
	private final TenantAwareRoutingDataSource dataSource;
	
	@Autowired
	public QuoteWritePlatformServiceImp(PlatformSecurityContext context,
			QuoteCommandFromApiJsonDeserializer apiJsonDeserializer,final QuoteJpaRepository quoteJpaRepository,
			final PlanRepository planRepository,final FromJsonHelper fromApiJsonHelper,final ServiceMasterRepository serviceMasterRepository,final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
	    this.quoteJpaRepository = quoteJpaRepository;
	    this.fromApiJsonHelper = fromApiJsonHelper;
	    this.planRepository = planRepository;
	    this.serviceMasterRepository = serviceMasterRepository;
	    this.dataSource = dataSource;
	}
	
	
	
	
	@Override
	public CommandProcessingResult createQuote(JsonCommand command) {

		try{
			
		this.context.authenticatedUser();
		this.apiJsonDeserializer.validateForCreate(command.json());
		Quote quote = Quote.formJson(command);
		final JsonArray servicePlanArray = command.arrayOfParameterNamed("servicePlanDetails");
	    final Set<QuoteOrder> selectedServicePlans = assembleSetOfPlans(servicePlanArray);
	    quote.addServicePlan(selectedServicePlans);
		this.quoteJpaRepository.saveAndFlush(quote);
		return new CommandProcessingResultBuilder().withEntityId(quote.getId()).build();
		
		}catch (DataIntegrityViolationException dve) {
		        handleDataIntegrityIssues(command, dve);
		        return  CommandProcessingResult.empty();
		}
	}
	
	private Set<QuoteOrder> assembleSetOfPlans(JsonArray servicePlanArray) {

        final Set<QuoteOrder> allServicePlans = new HashSet<>();
        String[]  servicePlans = null;
        if (servicePlanArray.size() > 0) {
			servicePlans = new String[servicePlanArray.size()];
			for(int i = 0; i < servicePlanArray.size(); i++){
				servicePlans[i] = servicePlanArray.get(i).toString();
			}
            for (final String servicePlan : servicePlans) {
            	final JsonElement element = fromApiJsonHelper.parse(servicePlan);
				String planId = this.fromApiJsonHelper.extractStringNamed("planId", element);
				String serviceId = this.fromApiJsonHelper.extractStringNamed("serviceId", element);
				BigDecimal planRecurirngCharge = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("planRecurirngCharge", element);
				BigDecimal planonetimeCharge = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("planonetimeCharge", element);
				String chargeCode = this.fromApiJsonHelper.extractStringNamed("chargeCode", element);
                final Plan plan = this.planRepository.findOne(Long.valueOf(planId));
                final ServiceMaster serviceDetail =this.serviceMasterRepository.findOne(Long.valueOf(serviceId));
                
                if (plan != null && serviceDetail != null) { 
           QuoteOrder quoteorder = new QuoteOrder(serviceDetail.getServiceCode(),plan.getDescription(),planRecurirngCharge,planonetimeCharge,chargeCode);
                allServicePlans.add(quoteorder);
                }
            }
        }

        return allServicePlans;
    }
	
	private void handleDataIntegrityIssues(JsonCommand command, DataIntegrityViolationException dve) {
		
        final Throwable realCause = dve.getMostSpecificCause();
   	  throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                 "Unknown data integrity issue with resource.");
		
	}


	@Override
	public CommandProcessingResult deleteQuotation(JsonCommand command, Long leadId) {
		try{
			this.context.authenticatedUser();
			Quote quote = this.retrieveQuotation(leadId);
			if(quote.getIsDeleted()=='Y'){
				throw new QuotationNotFoundException(leadId);
			}
			quote.delete();
			this.quoteJpaRepository.saveAndFlush(quote);
			return new CommandProcessingResultBuilder().withEntityId(leadId).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}




	@Override
	public CommandProcessingResult updateQuotation(JsonCommand command, Long quoteId) {
		
		
		 try{
			   
			   this.context.authenticatedUser();
			   this.apiJsonDeserializer.validateForCreate(command.json());
			   final Quote quote = this.retrieveQuotation(quoteId);
			   final Map<String, Object> changes = quote.update(command);
			   
			   final JsonArray servicePlanArray = command.arrayOfParameterNamed("servicePlanDetails");
			   final Set<QuoteOrder> selectedServicePlans = assembleSetOfPlans(servicePlanArray);
			    quote.addServicePlan(selectedServicePlans);
			   this.quoteJpaRepository.saveAndFlush(quote);
			   /*if(!changes.isEmpty()){
				   this.salescatalogeRepository.save(salescataloge);
			   }*/
			   return new CommandProcessingResultBuilder() //
		       .withCommandId(command.commandId()) //
		       .withEntityId(quoteId) //
		       .with(changes) //
		       .build();
			}catch (DataIntegrityViolationException dve) {
				handleDataIntegrityIssues(command, dve);
			      return new CommandProcessingResult(Long.valueOf(-1));
			  }
	}
	


	private Quote retrieveQuotation(Long quoteId) {
		Quote quote = this.quoteJpaRepository.findQuotesByQuoteId(quoteId);
		if (quote == null) { throw new QuotationNotFoundException(quoteId); }
		return quote;
	}

	private List<Quote> retrieveQuotations(Long leadId) {
		List<Quote> quote = this.quoteJpaRepository.findQuotessByLeadId(leadId);
		if (quote == null) { throw new QuotationNotFoundException(leadId); }
		return quote;
	}


	@Override
	public CommandProcessingResult updateQuotationStatus(JsonCommand command, Long entityId) {
		try {
			context.authenticatedUser();
			//final List<Quote> quotes = this.retrieveQuotations(entityId);
			Long quoteId = command.longValueOfParameterNamed("quoteId");
			Quote quote = this.quoteJpaRepository.findOne(quoteId);
			String quoteStatus = command.stringValueOfParameterNamed("quoteStatus");
			quote.setQuoteStatus(quoteStatus);
		   this.quoteJpaRepository.saveAndFlush(quote);
		
		
			return new CommandProcessingResultBuilder() //
					.withCommandId(command.commandId()) //
					.build();
			
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
		}
		return new CommandProcessingResultBuilder().withEntityId(-1L).build();
	}




	@Transactional
	@Override
	public String generateQuoteStatementPdf(final Long leadId)  {
        
        final String fileLocation = FileUtils.MIFOSX_BASE_DIR ;
        /** Recursively create the directory if it does not exist **/
        if (!new File(fileLocation).isDirectory()) {
            new File(fileLocation).mkdirs();
        }
        final String PaymentDetailsLocation = fileLocation + File.separator +"QuotationPdfFiles";
        if (!new File(PaymentDetailsLocation).isDirectory()) {
             new File(PaymentDetailsLocation).mkdirs();
        }
        final String printquoteLocation = PaymentDetailsLocation +File.separator +leadId+"_"+DateUtils.getLocalDateOfTenant()+".pdf";
        final Long id = Long.valueOf(leadId.toString());
        try {
            
            final String jpath = fileLocation+File.separator+"jasper";
            final MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
            final String jasperfilepath =jpath+File.separator+"Quotation_"+tenant.getTenantIdentifier()+".jasper";
            File destinationFile=new File(jasperfilepath);
              if(!destinationFile.exists()){
                File sourceFile=new File(this.getClass().getClassLoader().getResource("Files/Quotation.jasper").getFile());
                FileUtils.copyFileUsingApacheCommonsIO(sourceFile,destinationFile);
              }
            final Connection connection = this.dataSource.getConnection();
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("leadId", id);
            parameters.put(JRParameter.REPORT_LOCALE, getLocale(tenant));
           final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperfilepath, parameters, connection);
           JasperExportManager.exportReportToPdfFile(jasperPrint,printquoteLocation);
           connection.close();
           System.out.println("Filling report successfully...");
           
           }catch (final DataIntegrityViolationException ex) {
             LOGGER.error("Filling report failed...\r\n" + ex.getLocalizedMessage());
             System.out.println("Filling report failed...");
             ex.printStackTrace();
           } catch (final JRException  | JRRuntimeException e) {
            LOGGER.error("Filling report failed...\r\n" + e.getLocalizedMessage());
            System.out.println("Filling report failed...");
             e.printStackTrace();
          } catch (final Exception e) {
            LOGGER.error("Filling report failed...\r\n" + e.getLocalizedMessage());
            System.out.println("Filling report failed...");
            e.printStackTrace();
        }
        return printquoteLocation;    
    }
	
	/**
	 * @param tenant
	 * @return Locale 
	 */
	 public Locale getLocale(MifosPlatformTenant tenant) {

		Locale locale = LocaleUtils.toLocale(tenant.getLocaleName());
		if (locale == null) {
			locale = Locale.getDefault();
		}
		return locale;
	}
}
