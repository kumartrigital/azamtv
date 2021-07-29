/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.configuration.data;

import java.util.Date;
import java.util.List;

/**
 * Immutable data object for global configuration.
 */
public class ConfigurationData {

    @SuppressWarnings("unused")
    private final List<ConfigurationPropertyData> globalConfiguration;
    private String clientConfiguration;

    public ConfigurationData(final List<ConfigurationPropertyData> globalConfiguration) {
        this.globalConfiguration = globalConfiguration;
    }

	public String getClientConfiguration() {
		return clientConfiguration;
	}
    
	public void setClientConfiguration(final String clientConfiguration){
		this.clientConfiguration = clientConfiguration;
	}

	
}