/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.template.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifosplatform.template.domain.Template;
import org.mifosplatform.template.domain.TemplateEntity;
import org.mifosplatform.template.domain.TemplateType;

public class TemplateData {

    private final List<Map<String, Object>> entities;
    private final List<Map<String, Object>> types;
    private final Template template;

    private TemplateData(final Template template) {
    	
        this.template = template;
        this.entities = getAllEntites();
        this.types = getAllTypes();
    }

    public static TemplateData template(final Template template) {
        return new TemplateData(template);
    }

    public static TemplateData template() {
        return new TemplateData(null);
    }

    private List<Map<String, Object>> getAllEntites() {
        final List<Map<String, Object>> l = new ArrayList<>();
        for (final TemplateEntity e : TemplateEntity.values()) {
            final Map<String, Object> m = new HashMap<>();
            m.put("id", e.getId());
            m.put("name", e.getName());
            l.add(m);
        }
        return l;
    }

    private List<Map<String, Object>> getAllTypes() {
        final List<Map<String, Object>> l = new ArrayList<>();
        for (final TemplateType e : TemplateType.values()) {
            final Map<String, Object> m = new HashMap<>();
            m.put("id", e.getId());
            m.put("name", e.getName());
            l.add(m);
        }
        return l;
    }

	public List<Map<String, Object>> getEntities() {
		return entities;
	}

	public List<Map<String, Object>> getTypes() {
		return types;
	}

	public Template getTemplate() {
		return template;
	}
    
    
}
