package org.mifosplatform.portfolio.jvtransaction.domain;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.crm.ticketmaster.domain.TicketMaster;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "m_jv_transactions")
public class JVTransaction extends AbstractAuditableCustom<AppUser, Long>{
		

		@Column(name = "office_id", length = 65536)
		private Long officeId;
		
		@Column(name = "client_id", length = 65536)
		private Long clientId;

		@Column(name = "jv_date")
		private Date jvDate;

		@Column(name = "start_date")
		private Date startDate;
		
		@Column(name = "end_date")
		private Date endDate;

		@Column(name = "jv_description")
		private String jvDescription;
		
		@Column(name = "ref_id")
		private Long refId;
		
		@Column(name = "trans_Amount")
		private BigDecimal transAmount;

		@Column(name = "trans_Type")
		private String transType;

	
		public JVTransaction(){
			
		}
		
		public JVTransaction(Long clientId,Long orderId,LocalDate jvDate,LocalDate startDate,LocalDate endDate,BigDecimal transAmount){
			try {
				if(startDate==null){
					this.startDate=null;
				}else{
					this.startDate= new SimpleDateFormat("yyyy-MM-dd").parse(startDate.toString());
				}
				if(endDate==null){
					this.endDate=null;
				}else{
					this.endDate= new SimpleDateFormat("yyyy-MM-dd").parse(endDate.toString());
				}
				this.jvDate= new SimpleDateFormat("yyyy-MM-dd").parse(jvDate.toString()); 
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.transAmount=transAmount;
			this.clientId=clientId;
			this.officeId=orderId;
			this.jvDescription="renewal for 1 month";
			this.transType="cash";
			
		}
		
		public static JVTransaction fromJson(final JsonCommand command) throws ParseException {
			return null;
		}


}
