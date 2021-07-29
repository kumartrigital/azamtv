package org.mifosplatform.finance.financialtransaction.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.FileUtils;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.finance.billingmaster.service.BillMasterReadPlatformService;
import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;




@Path("/financialTransactions")
@Component
@Scope("singleton")
public class FinancialTransactionApiResource {
	private  final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("transactionId","transactionDate","transactionType","amount",
			"invoiceId","chrageAmount","taxAmount","discountAmount","netChargeAmount","chargeType","amount","billDate","dueDate","id","transaction",
			"chargeStartDate","chargeEndDate"));
	private BillMasterReadPlatformService billMasterReadPlatformService;
	private PlatformSecurityContext context;
	private final DefaultToApiJsonSerializer<FinancialTransactionsData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final String resourceNameForPermissions = "financialTransactions";
    private final ClientReadPlatformService clientReadPlatformService;
    private final CrmServices crmServices;
    @Autowired
    public FinancialTransactionApiResource(final BillMasterReadPlatformService billMasterReadPlatformService,final PlatformSecurityContext context,
    		final DefaultToApiJsonSerializer<FinancialTransactionsData> toApiJsonSerializer,final ApiRequestParameterHelper apiRequestParameterHelper,
    		final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
    		final ClientReadPlatformService clientReadPlatformService, final CrmServices crmServices){
    	this.apiRequestParameterHelper=apiRequestParameterHelper;
    	this.billMasterReadPlatformService=billMasterReadPlatformService;
    	this.commandsSourceWritePlatformService=portfolioCommandSourceWritePlatformService;
    	this.context=context;
    	this.toApiJsonSerializer=toApiJsonSerializer;
    	this.clientReadPlatformService =clientReadPlatformService;
    	this.crmServices=crmServices;
    
    }
	@GET
	@Path("{clientId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveTransactionalData(@PathParam("clientId") final Long clientId,@Context final UriInfo uriInfo, 
	@QueryParam("sqlSearch") final String sqlSearch, @QueryParam("limit") final Integer limit, 
	@QueryParam("offset") final Integer offset, @QueryParam("key") final String key)	{
	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	
	final SearchSqlQuery searchFinancialTransaction =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
	this.crmServices.getClientBills(key,clientId.toString());
	Page<FinancialTransactionsData> transactionData = this.billMasterReadPlatformService.retrieveInvoiceFinancialData(searchFinancialTransaction,clientId);
	return this.toApiJsonSerializer.serialize(transactionData);    
	}
	
	@GET
	@Path("officeid/{officeId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveTransactionalDataByOfficeId(@PathParam("officeId") final Long officeId,@Context final UriInfo uriInfo, 
	@QueryParam("sqlSearch") final String sqlSearch, @QueryParam("limit") final Integer limit, 
	@QueryParam("offset") final Integer offset, @QueryParam("key") final String key)	{
	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	
	final SearchSqlQuery searchFinancialTransaction =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
	this.crmServices.getClientBills(key,officeId.toString());
	Page<FinancialTransactionsData> transactionData = this.billMasterReadPlatformService.retrieveInvoiceFinancialDataByOfficeId(searchFinancialTransaction,officeId);
	return this.toApiJsonSerializer.serialize(transactionData);    
	}
	
	@GET
	@Path("{invoiceId}/invoice")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveInvoiceData(@PathParam("invoiceId") final Long invoiceId,@Context final UriInfo uriInfo) {
	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	List<FinancialTransactionsData> transactionData = this.billMasterReadPlatformService.retrieveSingleInvoiceData(invoiceId);
	FinancialTransactionsData data=new FinancialTransactionsData(transactionData);
    final ApiRequestJsonSerializationSettings settings=apiRequestParameterHelper.process(uriInfo.getQueryParameters());
    return this.toApiJsonSerializer.serialize(settings,data,RESPONSE_DATA_PARAMETERS);
	}
	
	@GET
	@Path("{clientId}/type")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveSampleData(@PathParam("clientId") final Long clientId,@Context final UriInfo uriInfo, @QueryParam("type") final String type,
	 @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset)	{
	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	final SearchSqlQuery searchFinancialTransaction =SearchSqlQuery.forSearch(null, offset,limit );
	Page<FinancialTransactionsData> transactionData = this.billMasterReadPlatformService.retrieveSampleData(searchFinancialTransaction,clientId,type);
	return this.toApiJsonSerializer.serialize(transactionData);    
	}
	
	
	@GET
	@Path("{clientId}/payments")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveStatementsData(@PathParam("clientId") final Long clientId,@Context final UriInfo uriInfo, @QueryParam("type") final String type,
	 @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset,
	 @QueryParam("fromDate") final String fromDate,
	 @QueryParam("toDate") final String toDate)	{
	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	final SearchSqlQuery searchFinancialTransactionNew =SearchSqlQuery.forSearch(null, offset,limit );
	Page<FinancialTransactionsData> transactionData = this.billMasterReadPlatformService.retrieveStatementsData(searchFinancialTransactionNew,clientId,type,fromDate,toDate);
	return this.toApiJsonSerializer.serialize(transactionData);    
	}
	
	
	@GET
	@Path("download/{clientId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON,"application/pdf","application/csv"})
	public Response retrieveFinancialTransactionalData(@PathParam("clientId") final Long clientId,@Context final UriInfo uriInfo, 
			@QueryParam("downloadType") final String downloadType,@QueryParam("fromDate") final Long start, @QueryParam("toDate") final Long end)throws IOException	{
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		/**
		 * have to convert from and to date to format like 2014-06-15
		 * 
		 */
		Date fDate = new Date(start);
		Date tDate = new Date(end);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		String fromDate = df.format(fDate);
		String toDate = df.format(tDate);
		
		List<FinancialTransactionsData> financialTransactionsData = this.billMasterReadPlatformService.retriveDataForDownload(clientId ,fromDate, toDate);
		
		
		String fileLocation = null;
		File file = null;
		
		if(downloadType.equalsIgnoreCase("csv")){
			
			StringBuilder builder = new StringBuilder();
			
			ClientData clientData = this.clientReadPlatformService.retrieveOne(clientId);
			builder.append("AccountNumber, ");
			builder.append(clientData.getAccountNo()+", ");
			
			builder.append("ClientName, ");
			builder.append(clientData.getDisplayName());
			builder.append("\n\n");
			
			builder.append("UserName, TransactionId, TransactionDate, TransactionType, TransactionCategory, DebitAmount, CreditAmount \n");
			
			for(FinancialTransactionsData data: financialTransactionsData){
				builder.append(data.getUserName()+", ");
				builder.append(data.getTransactionId()+", ");
				builder.append(data.getTransDate()+", ");
				builder.append(data.getTransactionType()+", ");
				builder.append(data.getTransactionCategory()+", ");
				builder.append(data.getAmount()+", ");
				builder.append(data.getCreditAmount());
				builder.append("\n");
			}
			 fileLocation = System.getProperty("java.io.tmpdir")+File.separator + "billing"+File.separator+"financial_transaction_data"+System.currentTimeMillis()+".csv";
			 
			 String dirLocation = System.getProperty("java.io.tmpdir")+File.separator + "billing";
				File dir = new File(dirLocation);
				if(!dir.exists()){
					dir.mkdir();
				}
				
				 file = new File(fileLocation);
				if(!file.exists()){
					file.createNewFile();
				}
				FileUtils.writeStringToFile(file, builder.toString());
				
		}else if(downloadType.equalsIgnoreCase("pdf")){
			
			fileLocation = org.mifosplatform.infrastructure.core.service.FileUtils.MIFOSX_BASE_DIR + File.separator + "";
		        if (!new File(fileLocation).isDirectory()) {
		            new File(fileLocation).mkdirs();
		        }
		        String genaratePdf = fileLocation + File.separator + "financial_transaction_data"+System.currentTimeMillis()+".pdf";
		        try{
		        	
		        	Document document = new Document(PageSize.B0.rotate());
		        	PdfWriter.getInstance(document, new FileOutputStream(new File(fileLocation +"financial_transaction_data"+System.currentTimeMillis()+".pdf")));
		        	document.open();
		        	
		        	PdfPTable table = new PdfPTable(7);
		            table.setWidthPercentage(100);
		            
		                table.addCell("UserName");table.addCell("TransactionId");table.addCell("TransactionDate");
		                table.addCell("TransactionType");table.addCell("TransactionCategory");
		                table.addCell("DebitAmount"); table.addCell("CreditAmount");
		                
		                for (FinancialTransactionsData data: financialTransactionsData) {
		                	table.addCell(data.getUserName());table.addCell(data.getTransactionId()+"");table.addCell(data.getTransDate()+"");
			                table.addCell(data.getTransactionType());table.addCell(data.getTransactionCategory());
			                table.addCell(data.getAmount()+""); table.addCell(data.getCreditAmount()+"");
		                }
		                table.completeRow();
		                document.add(table);
		                document.close();

		        }catch (Exception e) {
		            throw new PlatformDataIntegrityException("error.msg.exception.error", e.getMessage());
		        }
		        file = new File(genaratePdf);
				/*if(!file.exists()){
					file.createNewFile();
				}
				FileUtils.writeStringToFile(file, builder.toString());*/
		}
        final ResponseBuilder response = Response.ok(file);
        response.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        if(downloadType.equalsIgnoreCase("csv")){
        	response.header("Content-Type", "application/csv");
        }else if(downloadType.equalsIgnoreCase("pdf")){
        	response.header("Content-Type", "application/pdf");
        }
        return response.build();    
		
	}

}
