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

import java.io.Serializable;
import java.util.List;

import org.bremersee.comparator.model.ComparatorItem;
import org.bremersee.fac.model.FailedAccess;

/**
 * <p>
 * DAO of a failed access entry.
 * </p>
 * 
 * @author Christian Bremer
 */
public interface FailedAccessDao {

    /**
     * Saves the failed access entry and returns the persisted instance.
     * 
     * @param failedAccess
     *            the failed access entry
     * @return the persisted instance
     */
    FailedAccess save(FailedAccess failedAccess);

    /**
     * Returns the failed access entry with the specified ID or {@code null} if
     * there is no such entry.
     */
    FailedAccess getById(Serializable id);

    /**
     * Returns the failed access entry with the specified resource ID and remote
     * host. If there is no such entry, {@code null} will be returned.
     */
    FailedAccess getByResourceIdAndRemoteHost(String resourceId, String remoteHost);

    /**
     * Removes the failed access entry with the specified ID.
     */
    boolean removeById(Serializable id);

    /**
     * Removes the failed access entry with the specified resource ID and remote
     * host.
     */
    boolean removeByResourceIdAndRemoteHost(String resourceId, String remoteHost);

    /**
     * Counts the failed access entries.
     * 
     * @param searchValue
     *            a search value (can be {@code null})
     * @return the size
     */
    long count(String searchValue);

    /**
     * Returns the requested failed access entries.
     * 
     * @param searchValue
     *            a search value (can be {@code null})
     * @param firstResult
     *            the first result number (starting with 0, can be {@code null})
     * @param maxResults
     *            the maximum size (can be {@code null})
     * @param comparatorItem
     *            a comparator item for sorting (can be {@code null})
     * @return the found failed access entries
     */
    List<? extends FailedAccess> find(String searchValue, Integer firstResult, Integer maxResults,
            ComparatorItem comparatorItem);

    /**
     * Returns obsolete failed access entries.
     * 
     * @param removeFailedAccessEntriesAfterMillis
     *            a threshold in millis
     * @return the obsolete failed access entries
     */
    List<? extends FailedAccess> findObsolete(long removeFailedAccessEntriesAfterMillis);

}
