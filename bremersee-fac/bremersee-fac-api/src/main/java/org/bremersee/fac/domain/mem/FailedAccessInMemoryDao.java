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

package org.bremersee.fac.domain.mem;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bremersee.comparator.ObjectComparator;
import org.bremersee.comparator.ObjectComparatorFactory;
import org.bremersee.comparator.model.ComparatorItem;
import org.bremersee.fac.domain.FailedAccessDao;
import org.bremersee.fac.model.FailedAccess;
import org.bremersee.fac.model.FailedAccessDto;
import org.bremersee.utils.TagUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * This DAO implementation uses a {@link ConcurrentHashMap} to store the failed
 * access entries.
 * </p>
 *
 * @author Christian Bremer
 */
@SuppressWarnings("ALL")
@Named("failedAccessInMemoryDao")
public class FailedAccessInMemoryDao implements FailedAccessDao {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, FailedAccessDto> map = new ConcurrentHashMap<>();

    private ObjectComparatorFactory objectComparatorFactory = ObjectComparatorFactory.newInstance();

    @PostConstruct
    public void init() {
        log.info("Initialzing " + getClass().getSimpleName() + " ...");
        log.info("objectComparatorFactory = " + objectComparatorFactory.getClass().getSimpleName());
        log.info(getClass().getSimpleName() + " successfully initialized.");
    }

    public void setObjectComparatorFactory(final ObjectComparatorFactory objectComparatorFactory) {
        if (objectComparatorFactory != null) {
            this.objectComparatorFactory = objectComparatorFactory;
        }
    }

    private String createKey(final FailedAccess failedAccess) {
        final FailedAccess fa = failedAccess == null ? new FailedAccessDto() : failedAccess;
        return createKey(fa.getResourceId(), fa.getRemoteHost());
    }

    private String createKey(final String resourceId, final String remoteHost) {
        return resourceId + "@" + remoteHost;
    }

    @Override
    public FailedAccessDto save(final FailedAccess failedAccess) {

        final String key = createKey(failedAccess);
        FailedAccessDto entity = map.get(key);
        if (entity == null) {
            FailedAccessDto e = new FailedAccessDto(failedAccess);
            e.setId(createKey(failedAccess));
            map.put(key, e);
            entity = e;
        } else {
            entity.update(failedAccess);
        }
        return entity;
    }

    @Override
    public FailedAccessDto getById(final Serializable id) {
        return map.get(Validate.notNull(id, "ID must not be null."));
    }

    @Override
    public FailedAccessDto getByResourceIdAndRemoteHost(final String resourceId, final String remoteHost) {
        return map.get(createKey(resourceId, remoteHost));
    }

    @Override
    public boolean removeById(final Serializable id) {

        return id != null && map.remove(id) != null;
    }

    @Override
    public boolean removeByResourceIdAndRemoteHost(final String resourceId, String remoteHost) {

        return map.remove(createKey(resourceId, remoteHost)) != null;
    }

    @Override
    public long count(final String searchValue) {
        return (long) find(searchValue).size();
    }

    @Override
    public List<? extends FailedAccessDto> find(final String searchValue, final Integer firstResult, // NOSONAR
                                                final Integer maxResults, final ComparatorItem comparatorItem) {

        final int first = firstResult == null || firstResult < 0 ? 0 : firstResult;
        final int max = maxResults == null || maxResults <= 0 ? Integer.MAX_VALUE : maxResults;

        final List<FailedAccessDto> entities = find(searchValue);

        if (comparatorItem != null && StringUtils.isNotBlank(comparatorItem.getField())) {
            ObjectComparator objectComparator = objectComparatorFactory.newObjectComparator(comparatorItem);
            entities.sort(objectComparator);
        }

        if (first == 0 && max == Integer.MAX_VALUE) {
            return entities;
        }

        final List<FailedAccessDto> resultList = new ArrayList<>();
        int tmp = (long) max + (long) first > (long) Integer.MAX_VALUE ? Integer.MAX_VALUE
                : max + first;
        for (int i = first; i < tmp && i < entities.size(); i++) {
            resultList.add(entities.get(i));
        }

        return resultList;
    }

    private List<FailedAccessDto> find(final String searchValue) {
        List<FailedAccessDto> entities = new ArrayList<>();
        for (FailedAccessDto entity : map.values()) {
            if (StringUtils.isBlank(searchValue)) {
                entities.add(entity);
            } else {
                String[] tags = TagUtils.buildTags(searchValue);
                for (String tag : tags) {
                    String lowerTag = tag.toLowerCase();
                    if (entity.getResourceId().toLowerCase().contains(lowerTag) // NOSONAR
                            || entity.getRemoteHost().contains(lowerTag)) {
                        entities.add(entity);
                        break;
                    }
                }
            }
        }
        return entities;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.domain.FailedAccessDao#findObsolete(long)
     */
    @Override
    public List<? extends FailedAccessDto> findObsolete(final long removeFailedAccessEntriesAfterMillis) {
        List<FailedAccessDto> entities = new ArrayList<>();
        for (FailedAccessDto entity : map.values()) {
            if (entity.getModificationDate().getTime() < System.currentTimeMillis()
                    - removeFailedAccessEntriesAfterMillis) {
                entities.add(entity);
            }
        }
        return entities;
    }

}
