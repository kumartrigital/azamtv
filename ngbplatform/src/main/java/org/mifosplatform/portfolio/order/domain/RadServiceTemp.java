package org.mifosplatform.portfolio.order.domain;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.api.JsonCommand;

@Entity
@Table(name = "rm_services")
public class RadServiceTemp  {
	
	@Id
	@GeneratedValue
	@Column(name = "srvid")
	private Long srvid;

	@Column(name = "srvname")
	private String srvname;

	@Column(name = "descr")
	private String descr;

	@Column(name = "downrate")
	private Long downrate;

	@Column(name = "uprate")
	private Long uprate;

	@Column(name = "limitdl", nullable = false)
	private boolean limitdl;

	@Column(name = "limitul", nullable = false)
	private boolean limitul;

	@Column(name = "limitcomb", nullable = false)
	private boolean limitcomb;

	@Column(name = "limitexpiration", nullable = false)
	private boolean limitexpiration;

	@Column(name = "limituptime", nullable = false)
	private boolean limituptime;

	@Column(name = "poolname")
	private String poolname;

	@Column(name = "unitprice")
	private BigDecimal unitprice;

	@Column(name = "unitpriceadd")
	private BigDecimal unitpriceadd;

	@Column(name = "timebaseexp", nullable = false)
	private boolean timebaseexp;

	@Column(name = "timebaseonline", nullable = false)
	private boolean timebaseonline;

	@Column(name = "timeunitexp")
	private Long timeunitexp;

	@Column(name = "timeunitonline")
	private Long timeunitonline;

	@Column(name = "trafficunitdl")
	private Long trafficunitdl;

	@Column(name = "trafficunitul")
	private Long trafficunitul;

	@Column(name = "trafficunitcomb")
	private Long trafficunitcomb;

	@Column(name = "inittimeexp")
	private Long inittimeexp;

	@Column(name = "inittimeonline")
	private Long inittimeonline;

	@Column(name = "initdl")
	private Long initdl;

	@Column(name = "initul")
	private Long initul;

	@Column(name = "inittotal")
	private Long inittotal;

	@Column(name = "srvtype", nullable = false)
	private boolean srvtype;

	@Column(name = "timeaddmodeexp", nullable = false)
	private boolean timeaddmodeexp;

	@Column(name = "timeaddmodeonline", nullable = false)
	private boolean timeaddmodeonline;

	@Column(name = "trafficaddmode", nullable = false)
	private boolean trafficaddmode;

	@Column(name = "monthly", nullable = false)
	private boolean monthly;

	@Column(name = "enaddcredits", nullable = false)
	private boolean enaddcredits;

	@Column(name = "minamount")
	private Long minamount;

	@Column(name = "minamountadd")
	private Long minamountadd;

	@Column(name = "resetcounters", nullable = false)
	private boolean resetcounters;

	@Column(name = "pricecalcdownload", nullable = false)
	private boolean pricecalcdownload;

	@Column(name = "pricecalcupload", nullable = false)
	private boolean pricecalcupload;

	@Column(name = "pricecalcuptime", nullable = false)
	private boolean pricecalcuptime;

	@Column(name = "unitpricetax")
	private BigDecimal unitpricetax;

	@Column(name = "unitpriceaddtax")
	private BigDecimal unitpriceaddtax;

	@Column(name = "enableburst", nullable = false)
	private boolean enableburst;

	@Column(name = "dlburstlimit")
	private Long dlburstlimit;

	@Column(name = "ulburstlimit")
	private Long ulburstlimit;

	@Column(name = "dlburstthreshold")
	private Long dlburstthreshold;

	@Column(name = "ulburstthreshold")
	private Long ulburstthreshold;

	@Column(name = "dlbursttime")
	private Long dlbursttime;

	@Column(name = "ulbursttime")
	private Long ulbursttime;

	@Column(name = "enableservice")
	private Long enableservice;

	@Column(name = "dlquota")
	private Long dlquota;

	@Column(name = "ulquota")
	private Long ulquota;

	@Column(name = "combquota")
	private Long combquota;

	@Column(name = "timequota")
	private Long timequota;

	@Column(name = "priority")
	private Long priority;

	@Column(name = "nextsrvid")
	private Long nextsrvid;

	@Column(name = "dailynextsrvid")
	private Long dailynextsrvid;

	@Column(name = "availucp", nullable = false)
	private boolean availucp;

	@Column(name = "renew", nullable = false)
	private boolean renew;

	@Column(name = "policymapdl")
	private String policymapdl;

	@Column(name = "policymapul")
	private String policymapul;

	@Column(name = "custattr")
	private String custattr;

	public RadServiceTemp() {
		// TODO Auto-generated constructor stub

	}

	public RadServiceTemp(String srvname, long downrate, long uprate,long trafficunitdl, boolean islimitcomb, boolean limitexpiration,
			boolean renew, String value, long nextsrvid) {
		
		this.srvname =srvname;
		this.downrate = downrate;
		this.uprate = downrate;
		this. trafficunitdl = trafficunitdl;
		this.limitcomb = islimitcomb;
		this.limitexpiration = limitexpiration;
		this.renew = renew;
		this.descr = srvname;
		if(this.limitcomb){
			this.limitul = false;
			this.limitdl = false;
		}else{
			this.limitul = true;
			this.limitdl = true;
		}
		this.limituptime = false;
		this.poolname="";
		  this.unitprice=BigDecimal.ZERO;
		  this.unitpriceadd=BigDecimal.ZERO;
		  this.timebaseexp=true;
		  this.timebaseonline=false;
		  this.timeunitexp=Long.valueOf(0);
		  this.timeunitonline=Long.valueOf(0);
		  this.trafficunitdl=Long.valueOf(0);
		  this.trafficunitul=Long.valueOf(0);
		  this.trafficunitcomb=Long.valueOf(0);
		  this.inittimeexp=Long.valueOf(0);
		  this.inittimeonline=Long.valueOf(0);
		  this.initdl=Long.valueOf(0);
		  this.initul=Long.valueOf(0);
		  this.inittotal=Long.valueOf(0);
		  this.srvtype=false;
		  this.timeaddmodeexp=false;
		  this.timeaddmodeonline=false;
		  this.trafficaddmode=false;
		  this.monthly=false;
		  this.enaddcredits=false;
		  this.minamount=Long.valueOf(1);
		  this.minamountadd=Long.valueOf(1);
		  this.resetcounters=false;
		  this.pricecalcdownload=false;
		  this.pricecalcupload=false;
		  this.pricecalcuptime=false;
		  this.unitpricetax=BigDecimal.ZERO;
		  this.unitpriceaddtax=BigDecimal.ZERO;
		  this.enableburst=false;
		  this.dlburstlimit=Long.valueOf(0);
		  this.ulburstlimit=Long.valueOf(0);
		  this.dlburstthreshold=Long.valueOf(0);
		  this.ulburstthreshold=Long.valueOf(0);
		  this.dlbursttime=Long.valueOf(0);
		  this.ulbursttime=Long.valueOf(0);
		  this.enableservice=Long.valueOf(1);
		  this.dlquota=Long.valueOf(0);
		  this.ulquota=Long.valueOf(0);
		  this.combquota=Long.valueOf(0);
		  this.timequota=Long.valueOf(0);
		  this.priority = Long.valueOf(8);
		  this.nextsrvid=nextsrvid;
		  this.dailynextsrvid=nextsrvid;
		  //this.disnextsrvid=nextsrvid;
		  this.availucp=false;
		  //this.carryover=false;
		  this.policymapdl="";
		  this.policymapul="";
		  this.custattr="";
		  //this.gentftp=
		  //this.cmcfg=
		/*  this.advcmcfg="";
		  this.addamount=
		  this.ignstatip=*/
		

	
	}
	
	

	public boolean isLimitul() {
		return limitul;
	}
	
	public boolean isLimitdl() {
		return limitdl;
	}

	public void setLimitul(boolean limitul) {
		this.limitul = limitul;
	}

	public static RadServiceTemp fromJson(JSONObject jsonObject) {
		try {
			long downrate = jsonObject.getLong("downrate");
			long uprate = jsonObject.getLong("uprate");
			long trafficunitdl = jsonObject.getLong("trafficunitdl");
			boolean islimitcomb = jsonObject.getBoolean("limitcomb");
			boolean limitexpiration = jsonObject.getBoolean("limitexpiration");
			boolean renew = jsonObject.getBoolean("renew");
			String srvname = jsonObject.getString("srvname");
			String value = jsonObject.getString("value");
			long nextsrvid = jsonObject.getLong("nextsrvid");
			
			return new RadServiceTemp(srvname, downrate, uprate, trafficunitdl,
					islimitcomb, limitexpiration, renew, value,nextsrvid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public Long getserviceId() {
		return this.srvid;
	}
	
	public Map<String, Object> update(final JsonCommand command) {
		
		final Map<String, Object> actualChanges = new ConcurrentHashMap<String, Object>(1);
		final String srvNameParam = "srvname";
		
		if (command.isChangeInStringParameterNamed(srvNameParam,this.srvname)) {
			final String newValue = command.stringValueOfParameterNamed(srvNameParam);
			actualChanges.put(srvNameParam, newValue);
			this.srvname = StringUtils.defaultIfEmpty(newValue, null);	
		}
		
		final String downrate = "downrate";
		if (command.isChangeInLongParameterNamed(downrate, this.downrate)) {
			final Long newValue = command.longValueOfParameterNamed(downrate);
			actualChanges.put(downrate, newValue);
			this.downrate = newValue;	
		}
		
		final String uprate = "uprate";
		if (command.isChangeInLongParameterNamed(uprate, this.uprate)) {
			final Long newValue = command.longValueOfParameterNamed(uprate);
			actualChanges.put(uprate, newValue);
			this.uprate = newValue;	
		}
		
		final String trafficunitdl = "trafficunitdl";
		if (command.isChangeInLongParameterNamed(trafficunitdl, this.trafficunitdl)) {
			final Long newValue = command.longValueOfParameterNamed(trafficunitdl);
			actualChanges.put(trafficunitdl, newValue);
			this.trafficunitdl = newValue;	
		}
		
		final String islimitcomb= "islimitcomb";
		if (command.isChangeInBooleanParameterNamed(islimitcomb, this.limitcomb)) {
			final Boolean newValue = command.booleanObjectValueOfParameterNamed(islimitcomb);
			actualChanges.put(islimitcomb, newValue);
			this.limitcomb = newValue;	
		}
		
		final String limitexpiration= "limitexpiration";
		if (command.isChangeInBooleanParameterNamed(limitexpiration, this.limitexpiration)) {
			final Boolean newValue = command.booleanObjectValueOfParameterNamed(limitexpiration);
			actualChanges.put(limitexpiration, newValue);
			this.limitexpiration = newValue;	
		}
		
		final String renew= "renew";
		if (command.isChangeInBooleanParameterNamed(islimitcomb, this.renew)) {
			final Boolean newValue = command.booleanObjectValueOfParameterNamed(renew);
			actualChanges.put(renew, newValue);
			this.renew = newValue;	
		}
		
		final String nextsrvid = "nextsrvid";
		if (command.isChangeInLongParameterNamed(uprate, this.nextsrvid)) {
			final Long newValue = command.longValueOfParameterNamed(nextsrvid);
			actualChanges.put(nextsrvid, newValue);
			this.nextsrvid = newValue;	
		}
		
		return actualChanges;
	}

}
