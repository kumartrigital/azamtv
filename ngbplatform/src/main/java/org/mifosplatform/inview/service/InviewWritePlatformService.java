package org.mifosplatform.inview.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;

public interface InviewWritePlatformService {

	public String createClient(JsonCommand command, String vouchercode, String itemCode);

	public void topUpforPaywizard(JsonCommand command, Long clientId);

	public void addMovieForPaywizard(JsonCommand command);

	String retrackForPaywizardRestCall (String username);

}
