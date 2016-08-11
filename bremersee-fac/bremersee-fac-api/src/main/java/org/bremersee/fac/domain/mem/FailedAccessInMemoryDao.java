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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.bremersee.comparator.ObjectComparator;
import org.bremersee.comparator.ObjectComparatorFactory;
import org.bremersee.comparator.model.ComparatorItem;
import org.bremersee.fac.domain.FailedAccessDao;
import org.bremersee.fac.model.FailedAccess;
import org.bremersee.fac.model.FailedAccessDto;
import org.bremersee.utils.TagUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This DAO implementation uses a {@link ConcurrentHashMap} to store the failed
 * access entries.
 * </p>
 * 
 * @author Christian Bremer
 */
@Named("failedAccessInMemoryDao")
public class FailedAccessInMemoryDao implements FailedAccessDao {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, FailedAccessDto> map = new ConcurrentHashMap<String, FailedAccessDto>();

    protected ObjectComparatorFactory objectComparatorFactory = ObjectComparatorFactory.newInstance();

    @PostConstruct
    public void init() {
        log.info("Initialzing " + getClass().getSimpleName() + " ...");
        log.info("objectComparatorFactory = " + objectComparatorFactory.getClass().getSimpleName());
        log.info(getClass().getSimpleName() + " successfully initialized.");
    }

    public void setObjectComparatorFactory(ObjectComparatorFactory objectComparatorFactory) {
        if (objectComparatorFactory != null) {
            this.objectComparatorFactory = objectComparatorFactory;
        }
    }

    private String createKey(FailedAccess failedAccess) {
        if (failedAccess == null) {
            failedAccess = new FailedAccessDto();
        }
        return createKey(failedAccess.getResourceId(), failedAccess.getRemoteHost());
    }

    private String createKey(String resourceId, String remoteHost) {
        return resourceId + "@" + remoteHost;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.domain.FailedAccessDao#save(org.bremersee.fac.model.
     * FailedAccess)
     */
    @Override
    public FailedAccessDto save(FailedAccess failedAccess) {

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

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.domain.FailedAccessDao#getById(java.io.Serializable)
     */
    @Override
    public FailedAccessDto getById(Serializable id) {
        return map.get(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.domain.FailedAccessDao#getByResourceIdAndRemoteHost(
     * java.lang.String, java.lang.String)
     */
    @Override
    public FailedAccessDto getByResourceIdAndRemoteHost(String resourceId, String remoteHost) {
        return map.get(createKey(resourceId, remoteHost));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.domain.FailedAccessDao#removeById(java.io.Serializable)
     */
    @Override
    public boolean removeById(Serializable id) {

        if (id == null) {
            return false;
        }
        return map.remove(id) != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.domain.FailedAccessDao#removeByResourceIdAndRemoteHost(
     * java.lang.String, java.lang.String)
     */
    @Override
    public boolean removeByResourceIdAndRemoteHost(String resourceId, String remoteHost) {

        return map.remove(createKey(resourceId, remoteHost)) != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.domain.FailedAccessDao#count(java.lang.String)
     */
    @Override
    public long count(String searchValue) {
        return Integer.valueOf(find(searchValue).size()).longValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.domain.FailedAccessDao#find(java.lang.String,
     * java.lang.Integer, java.lang.Integer,
     * org.bremersee.comparator.model.ComparatorItem)
     */
    @Override
    public List<? extends FailedAccessDto> find(String searchValue, Integer firstResult, Integer maxResults,
            ComparatorItem comparatorItem) {

        if (firstResult == null || firstResult < 0) {
            firstResult = 0;
        }
        if (maxResults == null || maxResults <= 0) {
            maxResults = Integer.MAX_VALUE;
        }

        List<FailedAccessDto> entities = find(searchValue);

        if (comparatorItem != null && StringUtils.isNotBlank(comparatorItem.getField())) {
            ObjectComparator objectComparator = objectComparatorFactory.newObjectComparator(comparatorItem);
            Collections.sort(entities, objectComparator);
        }

        if (firstResult == 0 && maxResults == Integer.MAX_VALUE) {
            return entities;
        }

        List<FailedAccessDto> resultList = new ArrayList<>();
        int tmp = Long.valueOf((long) maxResults + (long) firstResult) > (long) Integer.MAX_VALUE ? Integer.MAX_VALUE
                : maxResults + firstResult;
        for (int i = firstResult; i < tmp && i < entities.size(); i++) {
            resultList.add(entities.get(i));
        }

        return resultList;
    }

    private List<FailedAccessDto> find(String searchValue) {
        List<FailedAccessDto> entities = new ArrayList<>();
        for (FailedAccessDto entity : map.values()) {
            if (StringUtils.isBlank(searchValue)) {
                entities.add(entity);
            } else {
                String[] tags = TagUtils.buildTags(searchValue);
                for (String tag : tags) {
                    String lowerTag = tag.toLowerCase();
                    if (entity.getResourceId().toLowerCase().contains(lowerTag)
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
    public List<? extends FailedAccessDto> findObsolete(long removeFailedAccessEntriesAfterMillis) {
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
