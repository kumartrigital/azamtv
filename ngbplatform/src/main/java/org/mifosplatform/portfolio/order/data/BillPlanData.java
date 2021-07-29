package org.mifosplatform.portfolio.order.data;

import java.util.List;

import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequest;
import org.springframework.stereotype.Service;


public class BillPlanData {
    private String clientPoId;
    private String clientServicePoId;
    private String servicePoId;
    private List<BillPlanProducts> billPlanProducts;
    
    private String productPoId;
    private String purchaedProductPoId;
    private String dealPoId;
    private String planPoId;
    
    private String packagePoId;
    private String orderPoId;
    
    private List<String> planPoIds;
    
    
    
    
    public BillPlanData(String clientPoId, String clientServicePoId) {
		this.clientPoId = clientPoId;
		this.clientServicePoId = clientServicePoId;
	}

	public List<BillPlanProducts> getBillPlanProducts() {
        return billPlanProducts;
    }

    public void setBillPlanProducts(List<BillPlanProducts> billPlanProducts) {
        this.billPlanProducts = billPlanProducts;
    }

    public void fromJsonForCelcom(ProvisioningRequest provisioningRequest) {
        provisioningRequest.getClientId();
        
        
    }

    public String celcomRequestInput() {
        int i =0;
        StringBuilder sb = new StringBuilder("<COB_OP_CUST_BILL_PLANS_inputFlist>");
        sb.append("<POID>0.0.0.1 /account "+this.clientPoId+" 0</POID>");
        sb.append("<ACCOUNT_OBJ>0.0.0.1 /account "+this.clientPoId+" 0</ACCOUNT_OBJ>");
        sb.append("<PROGRAM_NAME>COB|CELCOM</PROGRAM_NAME>");
        
        sb.append("<SERVICE_OBJ>0.0.0.1 /service/tv "+this.clientServicePoId+" 0</SERVICE_OBJ>");
        for(String planPoId:this.planPoIds){
            sb.append("<PLAN_LIST_CODE>");
            sb.append("<PLAN elem=\""+i+"\">");
            for(BillPlanProducts billPlanProduct:this.billPlanProducts){
                if(billPlanProduct.getPlanPoId().equalsIgnoreCase(planPoId)){
                    sb.append("<PRODUCT_OBJ>0.0.0.1 /product "+billPlanProduct.getProductPoId()+" 1</PRODUCT_OBJ>");
                    sb.append("<OFFERING_OBJ>0.0.0.1 /purchased_product "+billPlanProduct.getPurchasedProductPoId()+" 0</OFFERING_OBJ>");
                    sb.append("<PACKAGE_ID>"+billPlanProduct.getPackageId()+"</PACKAGE_ID>");
                }
            }
            sb.append("</PLAN>");
            sb.append("</PLAN_LIST_CODE>");i++;
        }
        sb.append("</COB_OP_CUST_BILL_PLANS_inputFlist>");
        System.out.println(sb.toString());
        return sb.toString();
    }

	public List<String> getPlanPoIds() {
		return planPoIds;
	}

	public void setPlanPoIds(List<String> planPoIds) {
		this.planPoIds = planPoIds;
	}

}
