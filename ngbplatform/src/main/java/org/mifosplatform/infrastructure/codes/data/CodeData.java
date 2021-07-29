/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.codes.data;

/**
 * Immutable data object representing a code.
 */
public class CodeData {

    private final Long id;
    private final String name;
    private final boolean systemDefined;
    private final String description;
    private final String module;
    private Long codeId;
    public static CodeData instance(final Long id, final String name, final String description,final boolean systemDefined, final String module) {
        return new CodeData(id, name, description,systemDefined,module);
    }

    private CodeData(final Long id, final String name, final String description,final boolean systemDefined, final String module) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.systemDefined = systemDefined;
        this.module = module;
    }

    public CodeData(final Long id, final String name, final String description,final boolean systemDefined, final String module, Long codeId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.systemDefined = systemDefined;
        this.module = module;
        this.codeId=codeId;
    }
    
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isSystemDefined() {
		return systemDefined;
	}

	public String getDescription() {
		return description;
	}

	public String getModule() {
		return module;
	}

	public void setCodeId(Long codeId) {
		this.codeId = codeId;
	}

	public Long getCodeId() {
		 return this.id;       
   }
}