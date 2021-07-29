package org.mifosplatform.crm.ticketmaster.subcategory.service;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.infrastructure.core.api.JsonCommand;

/**
 * @Written by H
 * for adding Subcategory items
 *
 */
public interface SubcategoryWritePlatformService {

	CommandProcessingResult createSubcategory(JsonCommand command);
}
