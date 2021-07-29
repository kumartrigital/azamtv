package org.mifosplatform.portfolio.plan.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.portfolio.product.data.ProductData;
import org.mifosplatform.portfolio.product.domain.Product;

public class PlanDetailData {
	
	private Long dealPoId;
	private Long productPoId;
	
	
	public PlanDetailData(Long dealPoId, Long productPoId) {
		this.dealPoId = dealPoId;
		this.productPoId = productPoId;
	}
		
}
