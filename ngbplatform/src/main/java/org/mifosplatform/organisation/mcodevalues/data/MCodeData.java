package org.mifosplatform.organisation.mcodevalues.data;


public class MCodeData {

		private Long id;
		private String mCodeValue;
		private Long orderPossition;
		
		public MCodeData() {
			// TODO Auto-generated constructor stub
		}
		
		public MCodeData(final Long id, final String mCodeValue,final Long orderPossition){
			this.id = id;
			this.mCodeValue = mCodeValue;
			this.orderPossition = orderPossition;
		
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getmCodeValue() {
			return mCodeValue;
		}

		public void setmCodeValue(String mCodeValue) {
			this.mCodeValue = mCodeValue;
		}

		public Long getOrderPossition() {
			return orderPossition;
		}

		public void setOrderPossition(Long orderPossition) {
			this.orderPossition = orderPossition;
		}
		
		
}