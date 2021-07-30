package org.mifosplatform.finance.billingmaster.service;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.LocaleUtils;
import org.mifosplatform.finance.billingmaster.domain.BillDetail;
import org.mifosplatform.finance.billingmaster.domain.BillMaster;
import org.mifosplatform.finance.billingmaster.domain.BillMasterRepository;
import org.mifosplatform.finance.chargeorder.exceptions.BillingOrderNoRecordsFoundException;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.organisation.message.domain.BillingMessage;
import org.mifosplatform.organisation.message.domain.BillingMessageRepository;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplate;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateRepository;
import org.mifosplatform.organisation.message.exception.BillingMessageTemplateNotFoundException;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;


/**
 * @author ranjith
 * 
 */
@Service
public class BillWritePlatformServiceImpl implements BillWritePlatformService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(BillWritePlatformServiceImpl.class);
	private final BillMasterRepository billMasterRepository;
	private final TenantAwareRoutingDataSource dataSource;
    private final ClientRepository clientRepository;
    private final BillingMessageTemplateRepository messageTemplateRepository;
    private final BillingMessageRepository messageDataRepository;

	
	@Autowired
	public BillWritePlatformServiceImpl(final BillMasterRepository billMasterRepository,final TenantAwareRoutingDataSource dataSource,
			final ClientRepository clientRepository,final BillingMessageTemplateRepository messageTemplateRepository,
		    final BillingMessageRepository messageDataRepository) {

		this.dataSource = dataSource;
		this.billMasterRepository = billMasterRepository;
		this.clientRepository = clientRepository;
		this.messageTemplateRepository = messageTemplateRepository;
		this.messageDataRepository = messageDataRepository;
		
	}

	@Override
	public CommandProcessingResult updateBillMaster(final List<BillDetail> billDetails, final BillMaster billMaster, final BigDecimal clientBalance) {
		
		try{
		BigDecimal chargeAmount = BigDecimal.ZERO;
		BigDecimal adjustmentAmount = BigDecimal.ZERO;
		BigDecimal paymentAmount = BigDecimal.ZERO;
		BigDecimal dueAmount = BigDecimal.ZERO;
		BigDecimal taxAmount = BigDecimal.ZERO;
		BigDecimal oneTimeSaleAmount = BigDecimal.ZERO;
		BigDecimal serviceTransferAmount =BigDecimal.ZERO;
		BigDecimal depositRefundAmount =BigDecimal.ZERO;
		
		for (final BillDetail billDetail : billDetails) {
			if ("SERVICE_CHARGES".equalsIgnoreCase(billDetail.getTransactionType())) {
				if (billDetail.getAmount() != null)
					chargeAmount = chargeAmount.add(billDetail.getAmount());
				
			} else if ("TAXES".equalsIgnoreCase(billDetail.getTransactionType())) {
				if (billDetail.getAmount() != null)
					taxAmount = taxAmount.add(billDetail.getAmount());

			} else if ("ADJUSTMENT_CREDIT".equalsIgnoreCase(billDetail.getTransactionType())) {
				if (billDetail.getAmount() != null)
					adjustmentAmount = adjustmentAmount.add(billDetail.getAmount());
				
			}else if ("ADJUSTMENT_DEBIT".equalsIgnoreCase(billDetail.getTransactionType())) {
				if (billDetail.getAmount() != null)
					adjustmentAmount = adjustmentAmount.add(billDetail.getAmount());
				
			}else if (billDetail.getTransactionType().contains("PAYMENT")) {
				if (billDetail.getAmount() != null)
					paymentAmount = paymentAmount.add(billDetail.getAmount());

			} else if ("ONETIME_CHARGES".equalsIgnoreCase(billDetail.getTransactionType())) {
				if (billDetail.getAmount() != null)
					oneTimeSaleAmount = oneTimeSaleAmount.add(billDetail.getAmount());

			}else if ("SERVICE_TRANSFER".equalsIgnoreCase(billDetail.getTransactionType())) {
				if (billDetail.getAmount() != null)
					serviceTransferAmount = serviceTransferAmount.add(billDetail.getAmount());
					
			} else if ("DEPOSIT&REFUND".equalsIgnoreCase(billDetail.getTransactionType())) {
				if (billDetail.getAmount() != null)
					depositRefundAmount = depositRefundAmount.add(billDetail.getAmount());
				}
		}
	  dueAmount = chargeAmount.add(taxAmount).add(oneTimeSaleAmount).add(clientBalance).add(depositRefundAmount)
			      .add(serviceTransferAmount).subtract(paymentAmount).subtract(adjustmentAmount);
	  billMaster.setChargeAmount(chargeAmount.add(oneTimeSaleAmount).add(serviceTransferAmount));
	  billMaster.setAdjustmentAmount(adjustmentAmount);
	  billMaster.setTaxAmount(taxAmount);
	  billMaster.setPaidAmount(paymentAmount);
	  billMaster.setDueAmount(dueAmount);
	  billMaster.setPreviousBalance(clientBalance);
	  billMaster.setDepositRefundAmount(depositRefundAmount);
	  this.billMasterRepository.save(billMaster);
	  return new CommandProcessingResult(billMaster.getId(),billMaster.getClientId());
	}catch(DataIntegrityViolationException dve){
		LOGGER.error("unable to retrieve data" + dve.getLocalizedMessage());
		return CommandProcessingResult.empty();
	}
}

	@Transactional
	@Override
	public String generateStatementPdf(final Long billId)  {
		
		try {
			BillMaster billMaster = this.billMasterRepository.findOne(billId);

			if ("invoice".equalsIgnoreCase(billMaster.getFileName())) {
				final String fileLocation = FileUtils.MIFOSX_BASE_DIR;
				/** Recursively create the directory if it does not exist **/
				if (!new File(fileLocation).isDirectory()) {
					new File(fileLocation).mkdirs();
				}
				final String statementDetailsLocation = fileLocation+ File.separator +"StatementPdfFiles";
				if (!new File(statementDetailsLocation).isDirectory()) {
					new File(statementDetailsLocation).mkdirs();
				}
				final String printStatementLocation = statementDetailsLocation+ File.separator +billMaster.getClientId()+"_"+DateUtils.getLocalDateOfTenant()+".pdf";
				final String jpath = fileLocation + File.separator + "jasper";
				final MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
				final String jfilepath = jpath + File.separator + "Statement_"+ tenant.getTenantIdentifier() + ".jasper";
				File destinationFile = new File(jfilepath);
				if (!destinationFile.exists()) {
					File sourceFile = new File(this.getClass().getClassLoader().getResource("Files/Statement.jasper").getFile());
					FileUtils.copyFileUsingApacheCommonsIO(sourceFile,destinationFile);
				}
				final Connection connection = this.dataSource.getConnection();
				Map<String, Object> parameters = new HashMap<String, Object>();
				final Integer id = Integer.valueOf(billMaster.getId().toString());
				parameters.put("param1", id);
				parameters.put("SUBREPORT_DIR", jpath + "" + File.separator);
				parameters.put(JRParameter.REPORT_LOCALE, getLocale(tenant)); 
				/* This realPath parameter holds the location path of company image #rakesh# */
	            parameters.put("realPath",this.getClass().getClassLoader().getResource("Files").getFile());
				final JasperPrint jasperPrint = JasperFillManager.fillReport(jfilepath, parameters, connection);
				JasperExportManager.exportReportToPdfFile(jasperPrint,printStatementLocation);
				billMaster.setFileName(printStatementLocation);
				this.billMasterRepository.save(billMaster);
				connection.close();
				System.out.println("Filling report successfully...");
			}
			return billMaster.getFileName();
		} catch (final DataIntegrityViolationException ex) {

			LOGGER.error("Filling report failed...\r\n" + ex.getLocalizedMessage());
			System.out.println("Filling report failed...");
			ex.printStackTrace();
			return null;

		} catch (final JRException | JRRuntimeException e) {

			LOGGER.error("Filling report failed...\r\n" + e.getLocalizedMessage());
			System.out.println("Filling report failed...");
			e.printStackTrace();
			return null;

		} catch (final Exception e) {

			LOGGER.error("Filling report failed...\r\n" + e.getLocalizedMessage());
			System.out.println("Filling report failed...");
			e.printStackTrace();
			return null;
		}
	}

	@Transactional
	@Override
	public String generateInovicePdf(final Long invoiceId) {
		
		final String fileLocation = FileUtils.MIFOSX_BASE_DIR ;
		/** Recursively create the directory if it does not exist **/
		if (!new File(fileLocation).isDirectory()) {
			new File(fileLocation).mkdirs();
		}
		final String InvoiceDetailsLocation = fileLocation + File.separator +"InvoicePdfFiles";
		if (!new File(InvoiceDetailsLocation).isDirectory()) {
			 new File(InvoiceDetailsLocation).mkdirs();
		}
		final String printInvoiceLocation = InvoiceDetailsLocation +File.separator +invoiceId+"_"+DateUtils.getLocalDateOfTenant()+".pdf";
		final Integer id = Integer.valueOf(invoiceId.toString());
		try {
			
			final String jpath = fileLocation+File.separator+"jasper"; 
			final MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
			final String jasperfilepath =jpath+File.separator+"Invoicereport_"+tenant.getTenantIdentifier()+".jasper";
			File destinationFile=new File(jasperfilepath);
		      if(!destinationFile.exists()){
		    	File sourceFile=new File(this.getClass().getClassLoader().getResource("Files/Invoicereport.jasper").getFile());
		    	FileUtils.copyFileUsingApacheCommonsIO(sourceFile,destinationFile);
		       }
			final Connection connection = this.dataSource.getConnection();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("param1", id);
			parameters.put(JRParameter.REPORT_LOCALE, getLocale(tenant)); 
			/* This realPath parameter holds the location path of company image #rakesh# */
            parameters.put("realPath",this.getClass().getClassLoader().getResource("Files").getFile());
		   final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperfilepath, parameters, connection);
		   JasperExportManager.exportReportToPdfFile(jasperPrint,printInvoiceLocation);
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
		return printInvoiceLocation;	
   }
	
	@Transactional
    @Override
    public String generatePaymentPdf(final Long paymentId)  {
        
        final String fileLocation = FileUtils.MIFOSX_BASE_DIR ;
        /** Recursively create the directory if it does not exist **/
        if (!new File(fileLocation).isDirectory()) {
            new File(fileLocation).mkdirs();
        }
        final String PaymentDetailsLocation = fileLocation + File.separator +"PaymentPdfFiles";
        if (!new File(PaymentDetailsLocation).isDirectory()) {
             new File(PaymentDetailsLocation).mkdirs();
        }
        final String printPaymentLocation = PaymentDetailsLocation +File.separator +paymentId+"_"+DateUtils.getLocalDateOfTenant()+".pdf";
        final Integer id = Integer.valueOf(paymentId.toString());
        try {
            
            final String jpath = fileLocation+File.separator+"jasper";
            final MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
            final String jasperfilepath =jpath+File.separator+"Paymentreport_"+tenant.getTenantIdentifier()+".jasper";
            File destinationFile=new File(jasperfilepath);
              if(!destinationFile.exists()){
                File sourceFile=new File(this.getClass().getClassLoader().getResource("Files/Paymentreport.jasper").getFile());
                FileUtils.copyFileUsingApacheCommonsIO(sourceFile,destinationFile);
              }
            final Connection connection = this.dataSource.getConnection();
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("param1", id);
            parameters.put(JRParameter.REPORT_LOCALE, getLocale(tenant));
            /* This realPath parameter holds the location path of company image #rakesh# */
            parameters.put("realPath",this.getClass().getClassLoader().getResource("Files").getFile());
           final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperfilepath, parameters, connection);
           JasperExportManager.exportReportToPdfFile(jasperPrint,printPaymentLocation);
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
        return printPaymentLocation;    
    }

	@Transactional
	@Override
    public String generateLeaseAggrement(final String mobileNo)  {
        
        final String fileLocation = FileUtils.MIFOSX_BASE_DIR ;
        /** Recursively create the directory if it does not exist **/
        if (!new File(fileLocation).isDirectory()) {
            new File(fileLocation).mkdirs();
        }
        final String PaymentDetailsLocation = fileLocation + File.separator +"PaymentPdfFiles";
        if (!new File(PaymentDetailsLocation).isDirectory()) {
             new File(PaymentDetailsLocation).mkdirs();
        }
        final String printPaymentLocation = PaymentDetailsLocation +File.separator +mobileNo+"_"+DateUtils.getLocalDateOfTenant()+".pdf";
        try {
            
            final String jpath = fileLocation+File.separator+"jasper";
            final MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
            final String jasperfilepath =jpath+File.separator+"leaseAgrrement"+tenant.getTenantIdentifier()+".jasper";
            File destinationFile=new File(jasperfilepath);
              if(!destinationFile.exists()){
                File sourceFile=new File(this.getClass().getClassLoader().getResource("Files/leaseAgrrement.jasper").getFile());
                FileUtils.copyFileUsingApacheCommonsIO(sourceFile,destinationFile);
              }
            final Connection connection = this.dataSource.getConnection();
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("param1", mobileNo);
            parameters.put(JRParameter.REPORT_LOCALE, getLocale(tenant));

            parameters.put("realPath",this.getClass().getClassLoader().getResource("Files").getFile());
           final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperfilepath, parameters, connection);
           JasperExportManager.exportReportToPdfFile(jasperPrint,printPaymentLocation);
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
        return printPaymentLocation;    
    }


	@Transactional
    @Override
    public String generateItemsalePdf(final Long id)  {
        
        final String fileLocation = FileUtils.MIFOSX_BASE_DIR ;
        /** Recursively create the directory if it does not exist **/
        if (!new File(fileLocation).isDirectory()) {
            new File(fileLocation).mkdirs();
        }
        final String PaymentDetailsLocation = fileLocation + File.separator +"PaymentPdfFiles";
        if (!new File(PaymentDetailsLocation).isDirectory()) {
             new File(PaymentDetailsLocation).mkdirs();
        }
        final String printPaymentLocation = PaymentDetailsLocation +File.separator +id+"_"+DateUtils.getLocalDateOfTenant()+".pdf";
        final Integer itemsaleid = Integer.valueOf(id.toString());
        try {
            
            final String jpath = fileLocation+File.separator+"jasper";
            final MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
            final String jasperfilepath =jpath+File.separator+"ItemSale"+tenant.getTenantIdentifier()+".jasper";
            File destinationFile=new File(jasperfilepath);
              if(!destinationFile.exists()){
                File sourceFile=new File(this.getClass().getClassLoader().getResource("Files/ItemSale.jasper").getFile());
                FileUtils.copyFileUsingApacheCommonsIO(sourceFile,destinationFile);
              }
            final Connection connection = this.dataSource.getConnection();
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("param1", id);
            parameters.put(JRParameter.REPORT_LOCALE, getLocale(tenant));
            /* This realPath parameter holds the location path of company image #rakesh# */
            parameters.put("realPath",this.getClass().getClassLoader().getResource("Files").getFile());
           final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperfilepath, parameters, connection);
           JasperExportManager.exportReportToPdfFile(jasperPrint,printPaymentLocation);
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
        return printPaymentLocation;    
    }


	@Transactional
	@Override
	public void sendPdfToEmail(final String printFileName, final Long clientId,final String templateName) {
		
		//context.authenticatedUser();
		final Client client = this.clientRepository.findOne(clientId);
		final String clientEmail = client.getEmail();
		if(clientEmail == null){
			final String msg = "Please provide email first";
			throw new BillingOrderNoRecordsFoundException(msg, client);
		}
		final BillingMessageTemplate messageTemplate = this.messageTemplateRepository.findByTemplateDescription(templateName);
		if(messageTemplate !=null){
		  String header = messageTemplate.getHeader().replace("<PARAM1>", client.getDisplayName().isEmpty()?client.getFirstname():client.getDisplayName());
		  BillingMessage  billingMessage = new BillingMessage(header, messageTemplate.getBody(), messageTemplate.getFooter(), clientEmail, clientEmail, 
		    		messageTemplate.getSubject(), "N", messageTemplate, messageTemplate.getMessageType(), printFileName);
		    this.messageDataRepository.save(billingMessage);
	    }else{
	    	throw new BillingMessageTemplateNotFoundException(templateName);
	    }
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

