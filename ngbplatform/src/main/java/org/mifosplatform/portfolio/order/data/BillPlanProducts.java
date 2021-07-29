package org.mifosplatform.portfolio.order.data;

public class BillPlanProducts {


    private String planPoId,productPoId,purchasedProductPoId,
    packageId;
    
    public BillPlanProducts(){
        
    }


    public BillPlanProducts(final String productPoId,
            final String purchasedProductPoId, final String packageId,String planPoId) {
        this.productPoId = productPoId;
        this.purchasedProductPoId = purchasedProductPoId;
        this.packageId = packageId;
        this.planPoId = planPoId;
    }

    public String getPlanPoId() {
        return planPoId;
    }

    public void setPlanPoId(String planPoId) {
        this.planPoId = planPoId;
    }

    public String getProductPoId() {
        return productPoId;
    }

    public void setProductPoId(String productPoId) {
        this.productPoId = productPoId;
    }

    public String getPurchasedProductPoId() {
        return purchasedProductPoId;
    }

    public void setPurchasedProductPoId(String purchasedProductPoId) {
        this.purchasedProductPoId = purchasedProductPoId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

}
