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

package org.bremersee.fac.domain.mongo;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bremersee.comparator.model.ComparatorItem;
import org.bremersee.comparator.spring.ComparatorSpringUtils;
import org.bremersee.fac.domain.FailedAccessDao;
import org.bremersee.fac.model.FailedAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

/**
 * @author Christian Bremer
 *
 */
@Named("failedAccessMongoDao")
public class FailedAccessMongoDao implements FailedAccessDao {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired(required = false)
    protected FailedAccessMongoRepository repository;

    public void setRepository(FailedAccessMongoRepository repository) {
        this.repository = repository;
    }
    
    @PostConstruct
    public void init() {
        log.info("Initializing " + getClass().getSimpleName() + " ...");
        Validate.notNull(repository, "repository must not be null");
        log.info(getClass().getSimpleName() + " successfully initialized.");
    }

    /* (non-Javadoc)
     * @see org.bremersee.fac.domain.FailedAccessDao#save(org.bremersee.fac.model.FailedAccess)
     */
    @Override
    public FailedAccess save(FailedAccess failedAccess) {

        if (log.isDebugEnabled()) {
            log.debug("Persisting " + failedAccess + " ...");
        }
        Validate.notNull(failedAccess, "failedAccess must not be null");
        Validate.notBlank(failedAccess.getResourceId(), "resourceId must not be null or blank");
        Validate.notBlank(failedAccess.getRemoteHost(), "remoteHost must not be null or blank");
        Validate.isTrue(failedAccess.getCounter() >= 0, "counter must be equal or greater than 0");
        
        FailedAccessMongoDoc e = failedAccess.getId() == null ? null : repository.findOne(failedAccess.getId().toString());
        if (e == null) {
            e = new FailedAccessMongoDoc(failedAccess);
        } else {
            e.update(failedAccess);
        }
        
        return repository.save(e);
    }

    /* (non-Javadoc)
     * @see org.bremersee.fac.domain.FailedAccessDao#getById(java.io.Serializable)
     */
    @Override
    public FailedAccess getById(Serializable id) {

        Validate.notNull(id, "id must not be null");
        return repository.findOne(id.toString());
    }

    /* (non-Javadoc)
     * @see org.bremersee.fac.domain.FailedAccessDao#getByResourceIdAndRemoteHost(java.lang.String, java.lang.String)
     */
    @Override
    public FailedAccess getByResourceIdAndRemoteHost(String resourceId, String remoteHost) {

        Validate.notBlank(resourceId, "resourceId must not be null or blank");
        Validate.notBlank(remoteHost, "remoteHost must not be null or blank");
        return repository.findByResourceIdAndRemoteHost(resourceId, remoteHost);
    }

    /* (non-Javadoc)
     * @see org.bremersee.fac.domain.FailedAccessDao#removeById(java.io.Serializable)
     */
    @Override
    public boolean removeById(Serializable id) {

        Validate.notNull(id, "id must not be null");
        FailedAccessMongoDoc e = repository.findOne(id.toString());
        if (e != null) {
            repository.delete(e);
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.bremersee.fac.domain.FailedAccessDao#removeByResourceIdAndRemoteHost(java.lang.String, java.lang.String)
     */
    @Override
    public boolean removeByResourceIdAndRemoteHost(String resourceId, String remoteHost) {

        Validate.notBlank(resourceId, "resourceId must not be null or blank");
        Validate.notBlank(remoteHost, "remoteHost must not be null or blank");
        FailedAccessMongoDoc e = repository.findByResourceIdAndRemoteHost(resourceId, remoteHost);
        if (e != null) {
            repository.delete(e);
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.bremersee.fac.domain.FailedAccessDao#count(java.lang.String)
     */
    @Override
    public long count(String searchValue) {

        if (StringUtils.isBlank(searchValue)) {
            return repository.count();
        }
        return repository.count(searchValue);
    }

    /* (non-Javadoc)
     * @see org.bremersee.fac.domain.FailedAccessDao#find(java.lang.String, java.lang.Integer, java.lang.Integer, org.bremersee.comparator.model.ComparatorItem)
     */
    @Override
    public List<? extends FailedAccess> find(String searchValue, Integer firstResult, Integer maxResults,
            ComparatorItem comparatorItem) {
        
        int size = Integer.MAX_VALUE;
        int page = 0;
        if (firstResult != null && firstResult >= 0 && maxResults != null && maxResults > 0) {
            size = maxResults.intValue();
            page = Double.valueOf(Math.floor(firstResult.doubleValue() / maxResults.doubleValue())).intValue();
            
        }
        PageRequest pageRequest = new PageRequest(page, size);
        if (comparatorItem == null || StringUtils.isBlank(comparatorItem.getField())) {
            pageRequest = new PageRequest(page, size);
        } else {
            ComparatorItem tmp = comparatorItem;
            while (tmp != null) {
                tmp.setIgnoreCase(false);
                tmp = tmp.getNextComparatorItem();
            }
            pageRequest = new PageRequest(page, size, ComparatorSpringUtils.toSort(comparatorItem));
        }
        if (StringUtils.isBlank(searchValue)) {
            return repository.findAll(pageRequest).getContent();
        } else {
            return repository.findByResourceIdLikeOrRemoteHostLike(searchValue, searchValue, pageRequest).getContent();
        }
    }

    /* (non-Javadoc)
     * @see org.bremersee.fac.domain.FailedAccessDao#findObsolete(long)
     */
    @Override
    public List<? extends FailedAccess> findObsolete(long removeFailedAccessEntriesAfterMillis) {

        return repository.findObsolete(removeFailedAccessEntriesAfterMillis);
    }

}
