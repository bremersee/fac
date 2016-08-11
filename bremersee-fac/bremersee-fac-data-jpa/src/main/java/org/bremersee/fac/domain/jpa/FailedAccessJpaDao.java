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

package org.bremersee.fac.domain.jpa;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bremersee.comparator.model.ComparatorItem;
import org.bremersee.fac.domain.FailedAccessDao;
import org.bremersee.fac.model.FailedAccess;
import org.bremersee.utils.TagUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Bremer
 */
@Named("failedAccessJpaDao")
public class FailedAccessJpaDao implements FailedAccessDao {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    protected EntityManager entityManager;

    private Class<? extends AbstractFailedAccessEntity> entityClass;

    @PostConstruct
    public void init() {
        log.info("Initializing " + getClass().getSimpleName() + " ...");
        Validate.notNull(entityManager, "Entity manager must not be null.");
        if (entityClass == null) {
            entityClass = createEntity(new FailedAccessEntity()).getClass();
        }
        log.info(getClass().getSimpleName() + " successfully initialized.");
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Configure the entity manager to be used.
     * 
     * @param entityManager
     *            the {@link EntityManager} to set.
     */
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected AbstractFailedAccessEntity createEntity(FailedAccess failedAccess) {
        Validate.notNull(failedAccess, "failedAccess must not be null");
        if (failedAccess instanceof AbstractFailedAccessEntity) {
            return (AbstractFailedAccessEntity) failedAccess;
        } else {
            return new FailedAccessEntity(failedAccess);
        }
    }

    protected Class<? extends AbstractFailedAccessEntity> getEntityClass() {
        if (entityClass == null) {
            entityClass = createEntity(new FailedAccessEntity()).getClass();
        }
        return entityClass;
    }

    protected Long getId(Serializable id) {
        if (id == null) {
            return null;
        }
        if (id instanceof Long) {
            return (Long) id;
        }
        try {
            return Long.parseLong(id.toString());
        } catch (Exception e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.domain.FailedAccessDao#save(org.bremersee.fac.model.
     * FailedAccess)
     */
    @Transactional
    @Override
    public AbstractFailedAccessEntity save(FailedAccess failedAccess) {
        if (log.isDebugEnabled()) {
            log.debug("Persisting " + failedAccess + " ...");
        }
        Validate.notNull(failedAccess, "failedAccess must not be null");
        AbstractFailedAccessEntity entity = createEntity(failedAccess);
        if (entity.getId() == null) {
            getEntityManager().persist(entity);
        } else {
            entity = getEntityManager().merge(entity);
        }
        return entity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.domain.FailedAccessDao#getById(java.io.Serializable)
     */
    @Transactional
    @Override
    public AbstractFailedAccessEntity getById(Serializable _id) {

        Class<? extends AbstractFailedAccessEntity> entityClass = getEntityClass();
        if (log.isDebugEnabled()) {
            log.debug("Getting " + entityClass.getSimpleName() + " by id [" + _id + "] ...");
        }
        Long id = getId(_id);
        if (id == null) {
            if (log.isDebugEnabled()) {
                log.debug(
                        "Getting " + entityClass.getSimpleName() + " by id [" + id + "]: Id is null, returning null.");
            }
            return null;
        }
        try {
            AbstractFailedAccessEntity entity = (AbstractFailedAccessEntity) getEntityManager().find(entityClass, id);

            if (log.isDebugEnabled()) {
                log.debug("Getting " + entityClass.getSimpleName() + " by id [" + id + "]: Returning " + entity);
            }
            return entity;

        } catch (NoResultException e) {
            if (log.isDebugEnabled()) {
                log.debug("Getting " + entityClass.getSimpleName() + " by id [" + id
                        + "]: Entity was not found, returning null.");
            }
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.domain.FailedAccessDao#getByResourceIdAndRemoteHost(
     * java.lang.String, java.lang.String)
     */
    @Transactional
    @Override
    public AbstractFailedAccessEntity getByResourceIdAndRemoteHost(String resourceId, String remoteHost) {

        Class<? extends AbstractFailedAccessEntity> entityClass = getEntityClass();
        if (log.isDebugEnabled()) {
            log.debug("Getting " + entityClass.getSimpleName() + " by resourceId [" + resourceId + "] and remoteHost ["
                    + remoteHost + "] ...");
        }
        if (resourceId == null || remoteHost == null) {
            if (log.isDebugEnabled()) {
                log.debug("Getting " + entityClass.getSimpleName() + " by resourceId [" + resourceId
                        + "] and remoteHost [" + remoteHost + "]: resourceId or remoteHost is null, returning null.");
            }
            return null;
        }
        String queryStr = "SELECT e FROM " + entityClass.getSimpleName()
                + " e WHERE e.resourceId = :resourceId AND e.remoteHost = :remoteHost";
        try {
            AbstractFailedAccessEntity entity = (AbstractFailedAccessEntity) getEntityManager().createQuery(queryStr)
                    .setParameter("resourceId", resourceId).setParameter("remoteHost", remoteHost).getSingleResult();

            if (log.isDebugEnabled()) {
                log.debug("Getting " + entityClass.getSimpleName() + " by resourceId [" + resourceId
                        + "] and remoteHost [" + remoteHost + "]: Returning " + entity);
            }
            return entity;

        } catch (NoResultException e) {
            if (log.isDebugEnabled()) {
                log.debug("Getting " + entityClass.getSimpleName() + " by resourceId [" + resourceId
                        + "] and remoteHost [" + remoteHost + "]: Entity was not found, returning null.");
            }
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.domain.FailedAccessDao#removeById(java.io.Serializable)
     */
    @Transactional
    @Override
    public boolean removeById(Serializable _id) {

        Class<? extends AbstractFailedAccessEntity> entityClass = getEntityClass();
        Long id = getId(_id);

        if (log.isDebugEnabled()) {
            log.debug("Removing " + entityClass.getSimpleName() + " by id [" + id + "] ...");
        }
        AbstractFailedAccessEntity entity = getById(id);
        if (entity != null) {
            getEntityManager().remove(entity);
            if (log.isDebugEnabled()) {
                log.debug("Removing " + entityClass.getSimpleName() + " by id [" + id + "]: Returning true");
            }
            return true;
        }
        if (log.isDebugEnabled()) {
            log.debug("Removing " + entityClass.getSimpleName() + " by id [" + id + "]: Returning false");
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.domain.FailedAccessDao#removeByResourceIdAndRemoteHost(
     * java.lang.String, java.lang.String)
     */
    @Transactional
    @Override
    public boolean removeByResourceIdAndRemoteHost(String resourceId, String remoteHost) {

        Class<? extends AbstractFailedAccessEntity> entityClass = getEntityClass();
        if (log.isDebugEnabled()) {
            log.debug("Removing " + entityClass.getSimpleName() + " by resourceId [" + resourceId + "] and remoteHost ["
                    + remoteHost + "] ...");
        }
        AbstractFailedAccessEntity entity = getByResourceIdAndRemoteHost(resourceId, remoteHost);
        if (entity != null) {
            getEntityManager().remove(entity);
            if (log.isDebugEnabled()) {
                log.debug("Removing " + entityClass.getSimpleName() + " by resourceId [" + resourceId
                        + "] and remoteHost [" + remoteHost + "]: Returning true");
            }
            return true;
        }
        if (log.isDebugEnabled()) {
            log.debug("Removing " + entityClass.getSimpleName() + " by resourceId [" + resourceId + "] and remoteHost ["
                    + remoteHost + "]: Returning false");
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.domain.FailedAccessDao#count(java.lang.String)
     */
    @Transactional
    @Override
    public long count(String searchValue) {

        Class<? extends AbstractFailedAccessEntity> entityClass = getEntityClass();

        if (log.isDebugEnabled()) {
            log.debug("Counting " + entityClass.getSimpleName() + " with searchValue [" + searchValue + "] ...");
        }

        Map<String, Object> queryParams = new LinkedHashMap<String, Object>();

        StringBuilder queryBuilder = createQueryBuilder(searchValue, entityClass, queryParams, true);

        String queryStr = queryBuilder.toString();
        if (log.isDebugEnabled()) {
            log.debug("Counting " + entityClass.getSimpleName() + " with searchValue [" + searchValue
                    + "]: Using\nqueury [" + queryStr + "]");
        }
        Query query = getEntityManager().createQuery(queryStr);
        for (Map.Entry<String, Object> queryParamEntry : queryParams.entrySet()) {
            query.setParameter(queryParamEntry.getKey(), queryParamEntry.getValue());
        }

        long size = ((Number) query.getSingleResult()).longValue();
        if (log.isDebugEnabled()) {
            log.debug("Counting " + entityClass.getSimpleName() + " with searchValue [" + searchValue + "]: Returning "
                    + size);
        }
        return size;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.domain.FailedAccessDao#find(java.lang.String,
     * java.lang.Integer, java.lang.Integer,
     * org.bremersee.comparator.model.ComparatorItem)
     */
    @Transactional
    @Override
    public List<? extends AbstractFailedAccessEntity> find(String searchValue, Integer firstResult, Integer maxResults,
            ComparatorItem comparatorItem) {

        Class<? extends AbstractFailedAccessEntity> entityClass = getEntityClass();

        if (log.isDebugEnabled()) {
            log.debug("Finding " + entityClass.getSimpleName() + " with searchValue [" + searchValue
                    + "], firstResult [" + firstResult + "], maxResults [" + maxResults + "] and comparatorItem "
                    + comparatorItem + " ...");
        }

        Map<String, Object> queryParams = new LinkedHashMap<String, Object>();

        StringBuilder queryBuilder = createQueryBuilder(searchValue, entityClass, queryParams, false);

        int i = 0;
        ComparatorItem tmpComparatorItem = comparatorItem;
        while (tmpComparatorItem != null && StringUtils.isNotBlank(tmpComparatorItem.getField())) {
            String field = tmpComparatorItem.getField();
            if ("id".equals(field) || "resourceId".equals(field) || "remoteHost".equals(field)
                    || "counter".equals(field) || "creationDate".equals(field) || "modificationDate".equals(field)) {

                String direction = tmpComparatorItem.isAsc() ? "ASC" : "DESC";
                if (i == 0) {
                    queryBuilder.append(" ORDER BY e.");
                } else {
                    queryBuilder.append(", e.");
                }
                queryBuilder.append(field).append(" ").append(direction);

                i = i + 1;
            }
            tmpComparatorItem = tmpComparatorItem.getNextComparatorItem();
        }

        String queryStr = queryBuilder.toString();
        if (log.isDebugEnabled()) {
            log.debug("Finding " + entityClass.getSimpleName() + " with searchValue [" + searchValue
                    + "], firstResult [" + firstResult + "], maxResults [" + maxResults + "] and comparatorItem "
                    + comparatorItem + ":\nUsing queury [" + queryStr + "] ...");
        }
        Query query = getEntityManager().createQuery(queryStr);
        for (Map.Entry<String, Object> queryParamEntry : queryParams.entrySet()) {
            query.setParameter(queryParamEntry.getKey(), queryParamEntry.getValue());
        }

        if (firstResult != null && firstResult >= 0) {
            query.setFirstResult(firstResult);
        }
        if (maxResults != null && maxResults > 0) {
            query.setMaxResults(maxResults);
        }

        @SuppressWarnings("unchecked")
        List<? extends AbstractFailedAccessEntity> list = query.getResultList();
        if (log.isDebugEnabled()) {
            log.debug("Finding " + entityClass.getSimpleName() + " with searchValue [" + searchValue
                    + "], firstResult [" + firstResult + "], maxResults [" + maxResults + "] and comparatorItem "
                    + comparatorItem + ":\nReturning " + list.size() + " " + (list.size() == 1 ? "entry" : "entries"));
        }
        return list;
    }

    private StringBuilder createQueryBuilder(String searchValue,
            Class<? extends AbstractFailedAccessEntity> entityClass, Map<String, Object> queryParams,
            boolean forCounting) {

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT ");
        if (forCounting) {
            queryBuilder.append("COUNT(e)");
        } else {
            queryBuilder.append("e");
        }
        queryBuilder.append(" FROM ").append(entityClass.getSimpleName()).append(" e");

        if (StringUtils.isNotBlank(searchValue)) {
            String[] tags = TagUtils.buildTags(searchValue);
            if (tags != null && tags.length > 0) {
                for (int i = 0; i < tags.length; i++) {

                    if (i == 0) {
                        queryBuilder.append(" WHERE ");
                    } else {
                        queryBuilder.append(" OR ");
                    }

                    queryBuilder.append("LOWER(e.resourceId) LIKE :likeTag").append(i)
                            .append(" OR LOWER(e.remoteHost) LIKE :likeTag").append(i);

                    queryParams.put("likeTag" + i, "%" + tags[i].toLowerCase() + "%");
                }
            }
        }

        return queryBuilder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.domain.FailedAccessDao#findObsolete(long)
     */
    @Transactional
    @Override
    public List<? extends AbstractFailedAccessEntity> findObsolete(long removeFailedAccessEntriesAfterMillis) {

        long modificationDate = System.currentTimeMillis() - removeFailedAccessEntriesAfterMillis;
        String queryStr = "SELECT e FROM " + getEntityClass().getSimpleName()
                + " e WHERE e.modificationDate < :modificationDate";

        @SuppressWarnings("unchecked")
        List<? extends AbstractFailedAccessEntity> list = getEntityManager().createQuery(queryStr)
                .setParameter("modificationDate", modificationDate).getResultList();
        return list;
    }

}
