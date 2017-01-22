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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bremersee.comparator.model.ComparatorItem;
import org.bremersee.fac.domain.FailedAccessDao;
import org.bremersee.fac.model.FailedAccess;
import org.bremersee.utils.TagUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Bremer
 */
@SuppressWarnings({"WeakerAccess", "unused"})
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
     * @param entityManager the {@link EntityManager} to set.
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

    protected Long getId(final Serializable id) {
        if (id == null) {
            return null;
        }
        if (id instanceof Long) {
            return (Long) id;
        }
        try {
            return Long.parseLong(id.toString());
        } catch (Exception e) { // NOSONAR
            return null;
        }
    }

    @Transactional
    @Override
    public AbstractFailedAccessEntity save(final FailedAccess failedAccess) {
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

    @Transactional
    @Override
    public AbstractFailedAccessEntity getById(final Serializable _id) { // NOSONAR

        Class<? extends AbstractFailedAccessEntity> entityClass = getEntityClass(); // NOSONAR
        if (log.isDebugEnabled()) {
            log.debug("Getting " + entityClass.getSimpleName() + " by id [" + _id + "] ..."); // NOSONAR
        }
        Long id = getId(_id);
        if (id == null) {
            if (log.isDebugEnabled()) {
                log.debug(
                        "Getting " + entityClass.getSimpleName() + " by id: Id is null, returning null.");
            }
            return null;
        }
        try {
            AbstractFailedAccessEntity entity = getEntityManager().find(entityClass, id);

            if (log.isDebugEnabled()) {
                log.debug("Getting " + entityClass.getSimpleName() + " by id [" + id
                        + "]: Returning " + entity); // NOSONAR
            }
            return entity;

        } catch (NoResultException e) { // NOSONAR
            if (log.isDebugEnabled()) {
                log.debug("Getting " + entityClass.getSimpleName() + " by id [" + id
                        + "]: Entity was not found, returning null.");
            }
            return null;
        }
    }

    @Transactional
    @Override
    public AbstractFailedAccessEntity getByResourceIdAndRemoteHost(final String resourceId, final String remoteHost) {

        Class<? extends AbstractFailedAccessEntity> entityClass = getEntityClass(); // NOSONAR
        if (log.isDebugEnabled()) {
            log.debug("Getting " + entityClass.getSimpleName() + " by resourceId [" + resourceId // NOSONAR
                    + "] and remoteHost [" // NOSONAR
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

        } catch (NoResultException e) { // NOSONAR
            if (log.isDebugEnabled()) {
                log.debug("Getting " + entityClass.getSimpleName() + " by resourceId [" + resourceId
                        + "] and remoteHost [" + remoteHost + "]: Entity was not found, returning null.");
            }
            return null;
        }
    }

    @Transactional
    @Override
    public boolean removeById(final Serializable _id) { // NOSONAR

        Class<? extends AbstractFailedAccessEntity> entityClass = getEntityClass(); // NOSONAR
        Long id = getId(_id);

        if (log.isDebugEnabled()) {
            log.debug("Removing " + entityClass.getSimpleName() + " by id [" + id + "] ..."); // NOSONAR
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

    @Transactional
    @Override
    public boolean removeByResourceIdAndRemoteHost(final String resourceId, final String remoteHost) {

        Class<? extends AbstractFailedAccessEntity> entityClass = getEntityClass(); // NOSONAR
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

    @Transactional
    @Override
    public long count(final String searchValue) {

        Class<? extends AbstractFailedAccessEntity> entityClass = getEntityClass(); // NOSONAR

        if (log.isDebugEnabled()) {
            log.debug("Counting " + entityClass.getSimpleName() + " with searchValue [" + searchValue // NOSONAR
                    + "] ..."); // NOSONAR
        }

        Map<String, Object> queryParams = new LinkedHashMap<>();

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

    @Transactional
    @Override
    public List<? extends AbstractFailedAccessEntity> find(final String searchValue, // NOSONAR
                                                           final Integer firstResult, final Integer maxResults,
                                                           final ComparatorItem comparatorItem) {

        Class<? extends AbstractFailedAccessEntity> entityClass = getEntityClass(); // NOSONAR

        if (log.isDebugEnabled()) {
            log.debug("Finding " + entityClass.getSimpleName() + " with searchValue [" + searchValue // NOSONAR
                    + "], firstResult [" + firstResult + "], maxResults [" + maxResults + "] and comparatorItem " // NOSONAR
                    + comparatorItem + " ...");
        }

        Map<String, Object> queryParams = new LinkedHashMap<>();

        StringBuilder queryBuilder = createQueryBuilder(searchValue, entityClass, queryParams, false);

        int i = 0;
        ComparatorItem tmpComparatorItem = comparatorItem;
        while (tmpComparatorItem != null && StringUtils.isNotBlank(tmpComparatorItem.getField())) {
            String field = tmpComparatorItem.getField();
            if ("id".equals(field) || "resourceId".equals(field) || "remoteHost".equals(field) // NOSONAR
                    || "counter".equals(field) || "creationDate".equals(field) || "modificationDate".equals(field)) { // NOSONAR

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

    private StringBuilder createQueryBuilder(final String searchValue,
                                             final Class<? extends AbstractFailedAccessEntity> entityClass,
                                             final Map<String, Object> queryParams,
                                             final boolean forCounting) {

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

                    if (i == 0) { // NOSONAR
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

    @Transactional
    @Override
    public List<? extends AbstractFailedAccessEntity> findObsolete(final long removeFailedAccessEntriesAfterMillis) {

        long modificationDate = System.currentTimeMillis() - removeFailedAccessEntriesAfterMillis;
        String queryStr = "SELECT e FROM " + getEntityClass().getSimpleName()
                + " e WHERE e.modificationDate < :modificationDate";

        @SuppressWarnings("unchecked")
        List<? extends AbstractFailedAccessEntity> list = getEntityManager().createQuery(queryStr) // NOSONAR
                .setParameter("modificationDate", modificationDate).getResultList(); // NOSONAR
        return list;
    }

}
