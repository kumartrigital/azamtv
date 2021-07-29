/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.data;

import java.util.List;

/**
 * Immutable data object representing datatable data.
 * 
 * @author hugo
 * 
 */
public class DatatableData {

	private String applicationTableName;
	private String registeredTableName;
	private List<ResultsetColumnHeaderData> columnHeaderData;

	public static DatatableData create(final String applicationTableName,
			final String registeredTableName,
			final List<ResultsetColumnHeaderData> columnHeaderData) {
		return new DatatableData(applicationTableName, registeredTableName,columnHeaderData);
	}

	public DatatableData(final String applicationTableName,
			final String registeredTableName,
			final List<ResultsetColumnHeaderData> columnHeaderData) {
		this.applicationTableName = applicationTableName;
		this.registeredTableName = registeredTableName;
		this.columnHeaderData = columnHeaderData;
	}

	/**
	 * @return the applicationTableName
	 */
	public String getApplicationTableName() {
		return applicationTableName;
	}

	/**
	 * @return the registeredTableName
	 */
	public String getRegisteredTableName() {
		return registeredTableName;
	}

	/**
	 * @return the columnHeaderData
	 */
	public List<ResultsetColumnHeaderData> getColumnHeaderData() {
		return columnHeaderData;
	}

}