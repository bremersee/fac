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
 * @author Christian Bremer
 */
public interface FailedAccessDao {

    FailedAccess save(FailedAccess failedAccess);

    FailedAccess getById(Serializable id);

    FailedAccess getByResourceIdAndRemoteHost(String resourceId, String remoteHost);

    boolean removeById(Serializable id);

    boolean removeByResourceIdAndRemoteHost(String resourceId, String remoteHost);

    long count(String searchValue);

    List<? extends FailedAccess> find(String searchValue, Integer firstResult, Integer maxResults,
            ComparatorItem comparatorItem);

    List<? extends FailedAccess> findObsolete(long removeFailedAccessEntriesAfterMillis);

}
