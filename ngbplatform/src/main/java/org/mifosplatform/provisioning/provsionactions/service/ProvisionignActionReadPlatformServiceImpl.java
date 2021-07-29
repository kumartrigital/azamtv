package org.mifosplatform.provisioning.provsionactions.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.provisioning.provsionactions.data.ProvisioningActionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ProvisionignActionReadPlatformServiceImpl implements ProvisionignActionReadPlatformService{
	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext  securityContext;

	@Autowired
	public ProvisionignActionReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource,final PlatformSecurityContext context){
		this.jdbcTemplate=new JdbcTemplate(dataSource);
		this.securityContext=context;
	}

  @Override
  public List<ProvisioningActionData> getAllProvisionActions() {
      try{
    	  
    	  this.securityContext.authenticatedUser();
    	  final ProvisioningActionMapper mapper=new ProvisioningActionMapper();
    	  final String sql="select "+mapper.schema();
    	  return this.jdbcTemplate.query(sql,mapper,new Object[]{});
    	  
      }catch(EmptyResultDataAccessException accessException){
    	  return null;
      }
       
  }
  
  private class ProvisioningActionMapper implements RowMapper<ProvisioningActionData>{
	  
	  public String schema(){
		  return " p.id as id,p.action as action,p.provision_type as provisionType,p.provisioning_system as provisioningSys," +
		  		 " p.is_enable as isEnable from b_provisioning_actions p where p.is_delete='N' ";
	  }

	@Override
	public ProvisioningActionData mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		
		final Long id=rs.getLong("id");
		final String provisiontype=rs.getString("provisionType");
		final String action=rs.getString("action");
		final String provisioningSystem=rs.getString("provisioningSys");
		final String isEnable=rs.getString("isEnable");
		
		return new ProvisioningActionData(id,provisiontype,action,provisioningSystem,isEnable);
	}
	  
  }

}
