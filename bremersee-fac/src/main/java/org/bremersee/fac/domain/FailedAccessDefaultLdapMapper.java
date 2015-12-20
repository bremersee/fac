/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.fac.domain;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.bremersee.fac.model.FailedAccess;
import org.bremersee.fac.model.FailedAccessDto;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Bremer
 *
 */
@Named("defaultLdapFailedAccessMapper")
public class FailedAccessDefaultLdapMapper implements FailedAccessLdapMapper {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private SimpleDateFormat simpleDateFormat;

    private String objectClass = "facEntry";

    private String idAttribute = "uid";

    private String resourceIdAttribute = "facResourceId";

    private String remoteHostAttribute = "facRemoteHost";

    private String counterAttribute = "facValue";

    private String creationDateAttribute = "facCreationDate";

    private String modificationDateAttribute = "facModificationDate";

    @PostConstruct
    public void init() {
        log.info("Initialzing " + getClass().getSimpleName() + " ...");
        if (simpleDateFormat == null) {
            String simpleDateFormatPattern = "yyyyMMddHHmmssSSS";
            log.info("simpleDateFormatPattern = " + simpleDateFormatPattern);
            simpleDateFormat = new SimpleDateFormat(simpleDateFormatPattern, Locale.UK);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
        log.info("objectClass = " + objectClass);
        log.info("idAttribute = " + idAttribute);
        log.info("resourceIdAttribute = " + resourceIdAttribute);
        log.info("remoteHostAttribute = " + remoteHostAttribute);
        log.info("counterAttribute = " + counterAttribute);
        log.info("creationDateAttribute = " + creationDateAttribute);
        log.info("modificationDateAttribute = " + modificationDateAttribute);
        log.info(getClass().getSimpleName() + " successfully initialized.");
    }

    @Override
    public SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    public void setSimpleDateFormatPattern(String simpleDateFormatPattern) {
        if (StringUtils.isNotBlank(simpleDateFormatPattern)) {
            log.info("simpleDateFormatPattern = " + simpleDateFormatPattern);
            simpleDateFormat = new SimpleDateFormat(simpleDateFormatPattern, Locale.UK);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
    }

    @Override
    public String getResourceIdAttribute() {
        return resourceIdAttribute;
    }

    public void setResourceIdAttribute(String resourceIdAttribute) {
        this.resourceIdAttribute = resourceIdAttribute;
    }

    @Override
    public String getRemoteHostAttribute() {
        return remoteHostAttribute;
    }

    public void setRemoteHostAttribute(String remoteHostAttribute) {
        this.remoteHostAttribute = remoteHostAttribute;
    }

    public String getCounterAttribute() {
        return counterAttribute;
    }

    public void setCounterAttribute(String counterAttribute) {
        this.counterAttribute = counterAttribute;
    }

    public String getCreationDateAttribute() {
        return creationDateAttribute;
    }

    public void setCreationDateAttribute(String creationDateAttribute) {
        this.creationDateAttribute = creationDateAttribute;
    }

    @Override
    public String getModificationDateAttribute() {
        return modificationDateAttribute;
    }

    public void setModificationDateAttribute(String modificationDateAttribute) {
        this.modificationDateAttribute = modificationDateAttribute;
    }

    @Override
    public String getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(String objectClass) {
        this.objectClass = objectClass;
    }

    @Override
    public String getIdAttribute() {
        return idAttribute;
    }

    public void setIdAttribute(String idAttribute) {
        this.idAttribute = idAttribute;
    }

    @Override
    public FailedAccessDto mapToFailedAccessLdapEntity(LdapEntry entry) {

        FailedAccessDto entity = new FailedAccessDto();
        entity.setCounter(Integer.parseInt(getString(entry, counterAttribute, "0")));
        try {
            entity.setCreationDate(simpleDateFormat
                    .parse(getString(entry, creationDateAttribute, simpleDateFormat.format(new Date()))));
        } catch (Exception pe) {
            log.warn("Setting creation date failed. Using current date.", pe);
            entity.setCreationDate(new Date());
        }
        try {
            entity.setModificationDate(simpleDateFormat
                    .parse(getString(entry, modificationDateAttribute, simpleDateFormat.format(new Date()))));
        } catch (Exception pe) {
            log.warn("Setting modification date failed. Using current date.", pe);
            entity.setModificationDate(new Date());
        }
        entity.setId(getString(entry, idAttribute, null));
        entity.setRemoteHost(getString(entry, remoteHostAttribute, null));
        entity.setResourceId(getString(entry, resourceIdAttribute, null));

        return entity;
    }

    @Override
    public LdapEntry mapFromFailedAccessLdapEntity(String dn, FailedAccess failedAccess) {

        FailedAccessDto dto;
        if (failedAccess instanceof FailedAccessDto) {
            dto = (FailedAccessDto) failedAccess;
        } else {
            dto = new FailedAccessDto(failedAccess);
        }

        if (StringUtils.isBlank(dto.getId())) {
            dto.setId(dto.getResourceId() + ":" + dto.getRemoteHost());
        }

        final String newDn = getDnForFailedAccessLdapEntity(dn, dto);
        if (log.isDebugEnabled()) {
            log.debug("Creating entry " + newDn);
        }

        final Collection<LdapAttribute> attrs = new ArrayList<LdapAttribute>();
        attrs.add(new LdapAttribute(this.idAttribute, dto.getId()));
        attrs.add(new LdapAttribute(this.resourceIdAttribute, dto.getResourceId()));
        attrs.add(new LdapAttribute(this.remoteHostAttribute, dto.getRemoteHost()));
        attrs.add(new LdapAttribute(this.counterAttribute, Integer.toString(dto.getCounter())));
        attrs.add(new LdapAttribute(this.creationDateAttribute, simpleDateFormat.format(dto.getCreationDate())));
        attrs.add(
                new LdapAttribute(this.modificationDateAttribute, simpleDateFormat.format(dto.getModificationDate())));

        attrs.add(new LdapAttribute("objectClass", "top", this.objectClass));

        return new LdapEntry(newDn, attrs);
    }

    @Override
    public String getDnForFailedAccessLdapEntity(final String parentDn, final FailedAccess entity) {
        return String.format("%s=%s,%s", this.idAttribute, entity.getId(), parentDn);
    }

    private String getString(final LdapEntry entry, final String attribute, final String nullValue) {
        final LdapAttribute attr = entry.getAttribute(attribute);
        if (attr == null) {
            return nullValue;
        }

        String v = null;
        if (attr.isBinary()) {
            final byte[] b = attr.getBinaryValue();
            v = new String(b, Charset.forName("UTF-8"));
        } else {
            v = attr.getStringValue();
        }

        if (StringUtils.isNotBlank(v)) {
            return v;
        }
        return nullValue;
    }
}
