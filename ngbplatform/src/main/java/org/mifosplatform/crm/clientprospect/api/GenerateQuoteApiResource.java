package org.mifosplatform.crm.clientprospect.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.FileUtils;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.data.QuoteData;
import org.mifosplatform.crm.clientprospect.service.QuoteReadPlatformService;
import org.mifosplatform.crm.clientprospect.service.QuoteWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Path("/quotes")
@Component
@Scope("singleton")
public class GenerateQuoteApiResource {
	private final String RESOURCETYPE = "QUOTES";
	
	private final PlatformSecurityContext context;
	private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
	private final ToApiJsonSerializer apiJsonSerializer;

	private final  QuoteReadPlatformService quoteReadPlatformService;
	final private ApiRequestParameterHelper apiRequestParameterHelper;
     private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

	private final QuoteWritePlatformService quoteWritePlatformService;

	@Autowired
	public GenerateQuoteApiResource(final PlatformSecurityContext context,final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,
			ToApiJsonSerializer apiJsonSerializer,final  QuoteReadPlatformService quoteReadPlatformService,final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final QuoteWritePlatformService quoteWritePlatformService){
		this.context = context;
		this.commandSourceWritePlatformService = commandSourceWritePlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
	    this.quoteReadPlatformService = quoteReadPlatformService;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.quoteWritePlatformService = quoteWritePlatformService;
		
	}




	@POST
	@Path("{leadId}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public String createProspects(@PathParam("leadId") final Long leadId,final String jsonRequestBody) {
	
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createQuote().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return apiJsonSerializer.serialize(result);
	}

	@GET
	@Path("plans/{serviceId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrivePlans(@Context final UriInfo uriInfo ,@PathParam("serviceId") final Long serviceId){
		
		List<QuoteData> quoteData = this.quoteReadPlatformService.retrievePlans(serviceId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(quoteData);
		
	}

	@GET
	@Path("planpricing")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrievePlansPricing(@QueryParam("planId") final Long planId, @QueryParam("chargecode") final String chargecode){
	final List<QuoteData> quoteDatas = quoteReadPlatformService.retrievePlansPricing(planId,chargecode);
	return apiJsonSerializer.serialize(quoteDatas);
	
	}
	@GET
	@Path("viewquotes/{leadId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrivequotes(@Context final UriInfo uriInfo ,@PathParam("leadId") final Long leadId, @QueryParam("quoteId") final Long quoteId){
		
		List<QuoteData> quoteData = this.quoteReadPlatformService.retrivequotes(leadId,quoteId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(quoteData);
		
	}
	/*@GET
	@Path("download/{leadId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON,"application/pdf","application/csv"})
	public Response retrieveQuoteData(@PathParam("leadId") final Long leadId,@Context final UriInfo uriInfo, 
			@QueryParam("downloadType") final String downloadType)throws IOException	{
	
		*//**
		 * have to convert from and to date to format like 2014-06-15
		 * 
		 *//*
	
		
		List<QuoteData> quoteData = this.quoteReadPlatformService.retrivequotes(leadId);
		
		
		String fileLocation = null;
		File file = null;
		
		if(downloadType.equalsIgnoreCase("csv")){
			
			StringBuilder builder = new StringBuilder();
			
			List<QuoteData> quoteDatas = this.quoteReadPlatformService.retrivequotes(leadId);
			builder.append("AccountNumber, ");
			builder.append(clientData.getAccountNo()+", ");
			
			builder.append("ClientName, ");
			builder.append(clientData.getDisplayName());
			builder.append("\n\n");
			
			builder.append("QuoteId, LeadId,Status,ServiceCode, PlanName, PlanRecurirngCharge, PlanOnetimeCharge \n");
			
			for(QuoteData data: quoteData){
				builder.append(data.getQuoteId()+", ");
				builder.append(data.getLeadId()+", ");
				builder.append(data.getStatus()+", ");
				builder.append(data.getServiceCode()+", ");
				builder.append(data.getPlanName()+", ");
				builder.append(data.getPlanRecurirngCharge()+",");
				builder.append(data.getPlanOnetimeCharge());
				builder.append("\n");
			}
			 fileLocation = System.getProperty("java.io.tmpdir")+File.separator + "billing"+File.separator+"quotegeneration_data"+System.currentTimeMillis()+".csv";
			 
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
		        String genaratePdf = fileLocation + File.separator + "quotegeneration_data"+System.currentTimeMillis()+".pdf";
		        try{
		        	
		        	Document document = new Document(PageSize.B0.rotate());
		        	PdfWriter.getInstance(document, new FileOutputStream(new File(fileLocation +"quotegeneration_data"+System.currentTimeMillis()+".pdf")));
		        	document.open();
		        	
		        	PdfPTable table = new PdfPTable(7);
		            table.setWidthPercentage(100);
		            
		                table.addCell("QuoteId");table.addCell("LeadId");table.addCell("Status");
		                table.addCell("ServiceCode");table.addCell("PlanName");
		                table.addCell("PlanRecurirngCharge"); table.addCell("PlanOnetimeCharge");
		            	
		    			
		                for (QuoteData data: quoteData) {
		                	table.addCell(data.getQuoteId()+"");
		                	table.addCell(data.getLeadId()+"");
		                	table.addCell(data.getStatus()+"");
			                table.addCell(data.getServiceCode()+"");
			                table.addCell(data.getPlanName());
			                table.addCell(data.getPlanRecurirngCharge()+""); 
			                table.addCell(data.getPlanOnetimeCharge()+"");
		                }
		                table.completeRow();
		                document.add(table);
		                document.close();
		               
		        }catch (Exception e) {
		            throw new PlatformDataIntegrityException("error.msg.exception.error", e.getMessage());
		        }
		        file = new File(genaratePdf);
				if(!file.exists()){
					file.createNewFile();
				}
				FileUtils.writeStringToFile(file, builder.toString());
		}
        final ResponseBuilder response = Response.ok(file);
        response.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        if(downloadType.equalsIgnoreCase("csv")){
        	response.header("Content-Type", "application/csv");
        }else if(downloadType.equalsIgnoreCase("pdf")){
        	response.header("Content-Type", "application/pdf");
        }
        return response.build();    
		
	}*/
	
	@DELETE
	@Path("{leadId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteQuotation(@PathParam("leadId") final Long leadId) {

		final CommandWrapper command = new CommandWrapperBuilder().deleteQuotation(leadId).build();
		final CommandProcessingResult result = commandsSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	
	@PUT
	@Path("{quoteId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateQuotation(@PathParam("quoteId") final Long quoteId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateQuotation(quoteId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = commandsSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	

	@PUT
	@Path("status/{leadId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateQuotationStatus(@PathParam("leadId") final Long leadId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateQuotationStatus(leadId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = commandsSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("status")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String statusquotes(@QueryParam("leadId") final Long leadId){
		
		List<QuoteData> quoteData = this.quoteReadPlatformService.statusquotes(leadId);
		return this.apiJsonSerializer.serialize(quoteData);
	}
		
		@GET
		@Path("{leadId}/print")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public Response print(@PathParam("leadId") final Long leadId) {
				
			 final String printFileName=this.quoteWritePlatformService.generateQuoteStatementPdf(leadId);
			 final File file = new File(printFileName);
			 final ResponseBuilder response = Response.ok(file);
			 response.header("Content-Disposition", "attachment; filename=\"" +file.getName()+ "\"");
			 response.header("Content-Type", "application/pdf");
			 return response.build();
		}
	
	

}