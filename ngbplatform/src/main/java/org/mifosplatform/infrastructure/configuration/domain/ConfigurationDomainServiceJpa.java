/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.configuration.domain;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.cache.domain.CacheType;
import org.mifosplatform.infrastructure.cache.domain.PlatformCache;
import org.mifosplatform.infrastructure.cache.domain.PlatformCacheRepository;
import org.mifosplatform.infrastructure.configuration.exception.ConfigurationPropertyNotFoundException;
import org.mifosplatform.useradministration.domain.Permission;
import org.mifosplatform.useradministration.domain.PermissionRepository;
import org.mifosplatform.useradministration.exception.PermissionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigurationDomainServiceJpa implements ConfigurationDomainService {

    private final PermissionRepository permissionRepository;
    private final ConfigurationRepository globalConfigurationRepository;
    private final PlatformCacheRepository cacheTypeRepository;

    @Autowired
    public ConfigurationDomainServiceJpa(final PermissionRepository permissionRepository, final ConfigurationRepository globalConfigurationRepository,
    		  final PlatformCacheRepository cacheTypeRepository) {
        this.permissionRepository = permissionRepository;
        this.globalConfigurationRepository = globalConfigurationRepository;
        this.cacheTypeRepository = cacheTypeRepository;
    }

    @Override
    public boolean isMakerCheckerEnabledForTask(final String taskPermissionCode) {
        if (StringUtils.isBlank(taskPermissionCode)) { throw new PermissionNotFoundException(taskPermissionCode); }

        final Permission thisTask = this.permissionRepository.findOneByCode(taskPermissionCode);
        if (thisTask == null) { throw new PermissionNotFoundException(taskPermissionCode); }

       
        final Configuration property = this.globalConfigurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_MAKER_CHECKER);
        if (property == null) { throw new ConfigurationPropertyNotFoundException(ConfigurationConstants.CONFIG_PROPERTY_MAKER_CHECKER); }

        return thisTask.hasMakerCheckerEnabled() && property.isEnabled();
    }
    
    @Override
    public boolean isEhcacheEnabled() {
        return this.cacheTypeRepository.findOne(Long.valueOf(1)).isEhcacheEnabled();
        
    }
    
    @Transactional
    @Override
    public void updateCache(final CacheType cacheType) {
        final PlatformCache cache = this.cacheTypeRepository.findOne(Long.valueOf(1));
        cache.update(cacheType);

        this.cacheTypeRepository.save(cache);
    }
    
    @Override
    public boolean isConstraintApproachEnabledForDatatables() {
        final Configuration property = this.globalConfigurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_CONSTAINT_APPROACH_FOR_DATATABLES);
        return property.isEnabled();
    }
    
}