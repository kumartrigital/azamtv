/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.data;

/**
 * Immutable data object representing a possible value for a given resultset
 * column.
 */
public class ResultsetColumnValueData {

    private final int id;
    private final String value;
    @SuppressWarnings("unused")
    private final Integer codeId;

    public ResultsetColumnValueData(final int id, final String value) {
        this.id = id;
        this.value = value;
        this.codeId =null;
    }
    
    public ResultsetColumnValueData(final int id, final String value, final int codeId) {
        this.id = id;
        this.value = value;
        this.codeId = codeId;
    }
    
    public boolean matches(final String match) {
        return match.equalsIgnoreCase(this.value);
    }

    public boolean codeMatches(final Integer match) {
        return match.intValue() == this.id;
    }
}