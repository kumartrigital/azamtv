package org.mifosplatform.portfolio.product.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.domain.Channel;
import org.mifosplatform.organisation.channel.exception.ChannelNotFoundException;
import org.mifosplatform.portfolio.product.domain.Product;
import org.mifosplatform.portfolio.product.domain.ProductDetails;
import org.mifosplatform.portfolio.product.domain.ProductDetailsRepository;
import org.mifosplatform.portfolio.product.domain.ProductRepository;
import org.mifosplatform.portfolio.product.exception.ProductNotFoundException;
import org.mifosplatform.portfolio.product.serialization.ProductCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.service.domain.ServiceDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class ProductWritePlatformServiceImpl implements ProductWritePlatformService{

	private final PlatformSecurityContext context;
	private final ProductCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final ProductRepository productRepository;
	private final FromJsonHelper fromApiJsonHelper;
	private final ProductDetailsRepository productDetailsRepository;
	
	private final static Logger logger = LoggerFactory.getLogger(ProductWritePlatformServiceImpl.class);

	@Autowired
	public ProductWritePlatformServiceImpl(PlatformSecurityContext context,
		   ProductCommandFromApiJsonDeserializer fromApiJsonDeserializer,ProductRepository productRepository,
		   FromJsonHelper fromApiJsonHelper,ProductDetailsRepository productDetailsRepository) {
		
		this.context = context;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.productRepository = productRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.productDetailsRepository = productDetailsRepository;
		
	}
	
	@Transactional
	@Override
	public CommandProcessingResult createNewProduct(JsonCommand command) {
		
		try {
			  this.context.authenticatedUser();
			  this.fromApiJsonDeserializer.validateForCreate(command.json());
			  Product product = Product.fromJson(command);
			  final JsonArray productChildArray = command.arrayOfParameterNamed("productArray").getAsJsonArray();
			  product= this.assembleDetails(productChildArray,product);
			  productRepository.saveAndFlush(product);
			   return new CommandProcessingResult(product.getId());
		   }
			catch (DataIntegrityViolationException dve) {
				 handleCodeDataIntegrityIssues(command, dve);
				return  CommandProcessingResult.empty();
			}
		
	}

	private Product assembleDetails(JsonArray productChildArray, Product product) {
		
		String[]  childProducts = null;
		childProducts = new String[productChildArray.size()];
		if(productChildArray.size() > 0){
			for(int i = 0; i < productChildArray.size(); i++){
				childProducts[i] = productChildArray.get(i).toString();
			}
	
		for (final String childProduct : childProducts) {
			final JsonElement element = fromApiJsonHelper.parse(childProduct);
			final String paramName = fromApiJsonHelper.extractStringNamed("paramName", element);
			final String paramType = fromApiJsonHelper.extractStringNamed("paramType", element);
			final String paramValue = fromApiJsonHelper.extractStringNamed("paramValue", element);
			final String paramCategory = fromApiJsonHelper.extractStringNamed("paramCategory", element);
			
			ProductDetails productDetails = new ProductDetails(paramName, paramType,paramValue,paramCategory);
			product.addDetails(productDetails);
			
		}	 
	}	
	
	return product;
}

	private void handleCodeDataIntegrityIssues(JsonCommand command, DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("`product_code_key`")) {
            final String name = command.stringValueOfParameterNamed("productCode");
            throw new PlatformDataIntegrityException("error.msg.code.duplicate.name", "A code with name'"
                    + name + "'already exists", "displayName", name);
        }

        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    
		
	}

	@Override
	public CommandProcessingResult updateProduct(JsonCommand command, Long productId) {

		   try{
			   
			   this.context.authenticatedUser();
			   this.fromApiJsonDeserializer.validateForCreate(command.json());
			   Product product = this.retrieveProduct(productId);
			   List<ProductDetails> details=new ArrayList<>(product.getProductDetails());
		       final JsonArray productChildArray = command.arrayOfParameterNamed("productArray").getAsJsonArray();
		       String[] products =null;
		       products=new String[productChildArray.size()];
			   for(int i=0; i<productChildArray.size();i++){
				   products[i] =productChildArray.get(i).toString();
			   }
			   
			   for (String productData : products) {
					  
				    final JsonElement element = fromApiJsonHelper.parse(productData);
				    final Long id = fromApiJsonHelper.extractLongNamed("id", element);
					final String paramName = fromApiJsonHelper.extractStringNamed("paramName", element);
					final String paramType = fromApiJsonHelper.extractStringNamed("paramType", element);
					final String paramValue = fromApiJsonHelper.extractStringNamed("paramValue", element);
					final String paramCategory = fromApiJsonHelper.extractStringNamed("paramCategory", element);
					if(id != null){
					ProductDetails productDetails =this.productDetailsRepository.findOne(id);
					
					if(productDetails != null){
						productDetails.setParamName(paramName);
						productDetails.setParamType(paramType);
						productDetails.setParamValue(paramValue);
						productDetails.setParamCategory(paramCategory);
						this.productDetailsRepository.saveAndFlush(productDetails);
						if(details.contains(productDetails)){
						   details.remove(productDetails);
						}
					}
					}else {
						ProductDetails newDetails = new ProductDetails(paramName, paramType,paramValue,paramCategory);
						product.addDetails(newDetails);
					}
					
			  }
			   product.getProductDetails().removeAll(details);
			   
			   final Map<String, Object> changes = product.update(command);
			   if(!changes.isEmpty()){
				   this.productRepository.save(product);
			   }
			   return new CommandProcessingResultBuilder() //
		       .withCommandId(command.commandId()) //
		       .withEntityId(productId) //
		       .with(changes) //
		       .build();
			}catch (DataIntegrityViolationException dve) {
				handleCodeDataIntegrityIssues(command, dve);
			      return new CommandProcessingResult(Long.valueOf(-1));
			  }
			
	
	}

	private Product retrieveProduct(Long productId) {
		Product product = this.productRepository.findOne(productId);
		if (product == null) { throw new ProductNotFoundException(productId); }
		return product;
	
	}

	@Override
	public CommandProcessingResult deleteProduct(Long productId) {

		try{
			this.context.authenticatedUser();
			Product product = this.retrieveProduct(productId);
			if(product.getIsDeleted()=='Y'){
				throw new ProductNotFoundException(productId);
			}
			product.delete();
			this.productRepository.saveAndFlush(product);
			return new CommandProcessingResultBuilder().withEntityId(productId).build();
			
		}catch(DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
	
	
	}


	
	
}
