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

package org.bremersee.fac;

import java.io.Serializable;
import java.util.Date;

import org.bremersee.fac.model.AccessResultDto;
import org.bremersee.fac.model.BooleanDto;
import org.bremersee.fac.model.FailedAccessDto;
import org.bremersee.pagebuilder.model.PageDto;
import org.bremersee.pagebuilder.model.PageRequestDto;

/**
 * <p>
 * A failed access counter stores failed access entries of a resource for a
 * remote host, that wants to access the resource. The resource may be, for
 * example, the login page. When the login has failed, this may be stored in the
 * failed access counter. When the counter of the failed access entry has
 * reached a threshold the login can be blocked for a while.<br/>
 * It is a small protection against brute-force-attacks.
 * </p>
 * 
 * @author Christian Bremer
 */
public interface FailedAccessCounter {

    /**
     * Returns the threshold of the counter.
     * 
     * @return the threshold of the counter
     */
    int getFailedAccessCounterThreshold();

    /**
     * Returns the lifetime of a failed access entry.
     * 
     * @return the lifetime of a failed access entry
     */
    long getRemoveFailedAccessEntriesAfterMillis();

    /**
     * Returns the interval of removing obsolete failed access entries.
     * 
     * @return the interval of removing obsolete failed access entries
     */
    long getRemoveFailedEntriesInterval();

    /**
     * Returns the date of the last removing of obsolete failed access entries.
     * 
     * @return the date of the last removing of obsolete failed access entries
     */
    Date getLastRemovingOfFailedEntries();

    /**
     * Returns the duration (in millis) of the last removing of obsolete failed
     * access entries.
     * 
     * @return the duration (in millis) of the last removing of obsolete failed
     *         access entries
     */
    long getLastRemovingOfFailedEntriesDuration();

    /**
     * Returns the size of the last removing of obsolete failed access entries.
     * 
     * @return the size of the last removing of obsolete failed access entries
     */
    int getLastRemovingOfFailedEntriesSize();

    /**
     * Returns the total size of the last removing of obsolete failed access
     * entries.
     * 
     * @return the total size of the last removing of obsolete failed access
     *         entries
     */
    long getRemovedFailedEntriesTotalSize();

    /**
     * Finds failed access entries.
     * 
     * @param pageRequest
     *            the page request (can be {@code null})
     * @return a page with the found entries
     */
    PageDto getFailedAccessEntries(PageRequestDto pageRequest);

    /**
     * Gets the specified access failed entry or {@code null}, if there is no
     * such entry.
     * 
     * @param id
     *            the id of the resource failed entry
     * @return the access failed entry or {@code null}
     */
    FailedAccessDto getFailedAccessEntry(Serializable id);

    /**
     * Gets the specified access failed entry or {@code null}, if there is no
     * such entry.
     * 
     * @param resourceId
     *            the resource of the access failed entry
     * @param remoteHost
     *            the remote host of the failed access entry
     * @return the access failed entry or {@code null}
     */
    FailedAccessDto getFailedAccessEntry(String resourceId, String remoteHost);

    /**
     * Removes the specified access failed entry.
     * 
     * @param resourceId
     *            the resource of the access failed entry
     * @param remoteHost
     *            the remote host of the failed access entry
     * @return {@code true} if the resource was removed from the store otherwise
     *         {@code false}
     */
    BooleanDto removeFailedAccessEntry(String resourceId, String remoteHost);

    /**
     * Removes obsolete failed access entries.
     */
    void removeObsoleteFailedAccessEntries();

    /**
     * Tells the access failed store that the access from the remote host to the
     * specified resource was successfully. If the counter of the failed access
     * entry is already higher than the threshold of the store, the access to
     * the resource will stay blocked, otherwise the access is granted.
     * 
     * @param resourceId
     *            the resource
     * @param remoteHost
     *            the remote host
     * @param timeInMillis
     *            the current time stamp (may be {@code null})
     * @return the access result
     */
    AccessResultDto accessSucceeded(String resourceId, String remoteHost, Long timeInMillis);

    /**
     * Stores a failed access entry for the specified resource and remote host.
     * If an entry already exists, the counter will be incremented. If the
     * counter of the failed access entry is lower than the threshold of the
     * store, the access to the resource is not blocked otherwise it is blocked.
     * 
     * @param resourceId
     *            the resource
     * @param remoteHost
     *            the remote host
     * @param timeInMillis
     *            the current time stamp (may be {@code null})
     * @return the access result
     */
    AccessResultDto accessFailed(String resourceId, String remoteHost, Long timeInMillis); // TODO ResourceDescription?

    /**
     * Returns whether the access to the specified resource is granted for the
     * specified remote host or not.
     * 
     * @param resourceId
     *            the resource
     * @param remoteHost
     *            the remote host
     * @return the access result
     */
    AccessResultDto isAccessGranted(String resourceId, String remoteHost);

}
