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

import java.text.SimpleDateFormat;

import org.bremersee.fac.model.FailedAccess;
import org.bremersee.fac.model.FailedAccessDto;
import org.ldaptive.LdapEntry;

/**
 * @author Christian Bremer
 */
public interface FailedAccessLdapMapper {

    /**
     * Transforms a LDAP entry into a failed access entry.
     * 
     * @param entry
     *            the LDAP entry
     * @return the failed access entry
     */
    FailedAccessDto mapToFailedAccessLdapEntity(LdapEntry entry);

    /**
     * Transforms a failed access entry into a LDAP entry.
     * 
     * @param dn
     *            the distinguished name
     * @param failedAccess
     *            the failed access antry
     * @return the LDAP entry
     */
    LdapEntry mapFromFailedAccessLdapEntity(String dn, FailedAccess failedAccess);

    /**
     * Returns the date formatter to transform date objects.
     */
    SimpleDateFormat getSimpleDateFormat();

    /**
     * Gets the dn for a failed access entry.
     *
     * @param parentDn
     *            the parent dn
     * @param failedAccessEntry
     *            the failed access entry
     * @return the dn for failed access entry
     */
    String getDnForFailedAccessLdapEntity(String parentDn, FailedAccess failedAccessEntry);

    /**
     * Gets the name of the LDAP object class that represents service registry
     * entries.
     *
     * @return Registered service object class.
     */
    String getObjectClass();

    /**
     * Gets the name of the LDAP attribute that stores the failed access entry
     * unique identifier.
     *
     * @return the failed access entry unique ID attribute name
     */
    String getIdAttribute();

    /**
     * Gets the name of the LDAP attribute that stores the modification date.
     * 
     * @return the name of the LDAP attribute that stores the modification date
     */
    String getModificationDateAttribute();

    /**
     * Gets the name of the LDAP attribute that stores the resource ID.
     * 
     * @return the name of the LDAP attribute that stores the resource ID
     */
    String getResourceIdAttribute();

    /**
     * Gets the name of the LDAP attribute that stores the remote host.
     * 
     * @return the name of the LDAP attribute that stores the remote host
     */
    String getRemoteHostAttribute();

}
