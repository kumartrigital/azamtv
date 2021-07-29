package org.mifosplatform.portfolio.slabRate.data;

public class SlabRateData {

	private Long SlabId;
	private String SlabFrom;
	private String slabTo;
	private Float Rate;
	private boolean Isdeleted;
	
	public SlabRateData(){
		
	}
	public SlabRateData(Long SlabId,String SlabFrom,String slabTo,Float Rate,boolean Isdeleted){
		this.SlabId=SlabId;
		this.SlabFrom=SlabFrom;
		this.slabTo=slabTo;
		this.Rate=Rate;
		this.Isdeleted=Isdeleted;
		
	}
	public Long getSlabId() {
		return SlabId;
	}
	public void setSlabId(Long slabId) {
		SlabId = slabId;
	}
	public String getSlabFrom() {
		return SlabFrom;
	}
	public void setSlabFrom(String slabFrom) {
		SlabFrom = slabFrom;
	}
	public String getSlabTo() {
		return slabTo;
	}
	public void setSlabTo(String slabTo) {
		this.slabTo = slabTo;
	}
	public Float getRate() {
		return Rate;
	}
	public void setRate(Float rate) {
		Rate = rate;
	}
	public boolean isIsdeleted() {
		return Isdeleted;
	}
	public void setIsdeleted(boolean isdeleted) {
		Isdeleted = isdeleted;
	}
}
