/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.configuration.data;

/**
 * Immutable data object for global configuration property.
 */
public class ConfigurationPropertyData {

    private String name;
    private String value;
    private boolean enabled;
    private Long id;
    private String module;
    private String description;
    
    public ConfigurationPropertyData(final Long id, final String name, final boolean enabled, final String value) {
    	
        this.id = id;
    	this.name = name;
        this.enabled = enabled;
        this.value = value;
       
    }
  public ConfigurationPropertyData(final Long id, final String name, final boolean enabled, final String value, final String module, final String description) {
    	
        this.id = id;
    	this.name = name;
        this.enabled = enabled;
        this.value = value;
        this.module=module;
        this.description=description;
    }

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
}