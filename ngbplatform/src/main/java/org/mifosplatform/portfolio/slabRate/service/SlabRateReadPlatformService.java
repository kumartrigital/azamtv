package org.mifosplatform.portfolio.slabRate.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.portfolio.slabRate.data.SlabRateData;

public interface SlabRateReadPlatformService {

public List<SlabRateData> retrieveSlabRates();

public List<SlabRateData> retrieveSlabRatesbyId(String slabFrom, String slabTo);

}
