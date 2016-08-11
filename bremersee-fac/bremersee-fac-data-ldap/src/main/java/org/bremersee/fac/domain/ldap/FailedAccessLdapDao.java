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

package org.bremersee.fac.domain.ldap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bremersee.comparator.ObjectComparator;
import org.bremersee.comparator.ObjectComparatorFactory;
import org.bremersee.comparator.model.ComparatorItem;
import org.bremersee.fac.domain.FailedAccessDao;
import org.bremersee.fac.model.FailedAccess;
import org.bremersee.fac.model.FailedAccessDto;
import org.bremersee.utils.CodingUtils;
import org.bremersee.utils.TagUtils;
import org.ldaptive.AddOperation;
import org.ldaptive.AddRequest;
import org.ldaptive.AttributeModification;
import org.ldaptive.AttributeModificationType;
import org.ldaptive.Connection;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.DeleteOperation;
import org.ldaptive.DeleteRequest;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.ModifyOperation;
import org.ldaptive.ModifyRequest;
import org.ldaptive.Response;
import org.ldaptive.ResultCode;
import org.ldaptive.ReturnAttributes;
import org.ldaptive.SearchFilter;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Bremer
 */
@Named("failedAccessLdapDao")
public class FailedAccessLdapDao implements FailedAccessDao {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected ConnectionFactory connectionFactory;

    protected SearchRequest searchRequest;

    protected FailedAccessLdapMapper failedAccessLdapMapper;

    protected ObjectComparatorFactory objectComparatorFactory;

    private String searchFilter;

    private String loadFilter;

    private String obsoleteFilter;

    @PostConstruct
    public void init() {
        log.info("Initialzing " + getClass().getSimpleName() + " ...");
        Validate.notNull(connectionFactory, "connectionFactory must not be null");
        Validate.notNull(searchRequest, "searchRequest must not be null");
        log.info("baseDn = " + searchRequest.getBaseDn());
        if (failedAccessLdapMapper == null) {
            FailedAccessDefaultLdapMapper defaultLdapFailedAccessMapper = new FailedAccessDefaultLdapMapper();
            defaultLdapFailedAccessMapper.init();
            failedAccessLdapMapper = defaultLdapFailedAccessMapper;
        }
        if (objectComparatorFactory == null) {
            objectComparatorFactory = ObjectComparatorFactory.newInstance();
        }
        if (StringUtils.isBlank(searchFilter)) {
            searchFilter = '(' + this.failedAccessLdapMapper.getIdAttribute() + "={0})";
        }
        log.info("searchFilter = " + searchFilter);
        if (StringUtils.isBlank(loadFilter)) {
            loadFilter = "(objectClass=" + this.failedAccessLdapMapper.getObjectClass() + ')';
        }
        log.info("loadFilter = " + loadFilter);
        if (StringUtils.isBlank(obsoleteFilter)) {
            String loadFilter = this.loadFilter;
            if (!loadFilter.startsWith("(")) {
                loadFilter = "(" + loadFilter;
            }
            if (!loadFilter.endsWith("")) {
                loadFilter = loadFilter + ")";
            }
            obsoleteFilter = "(&" + loadFilter + "(" + failedAccessLdapMapper.getModificationDateAttribute()
                    + "<={0}))";
        }
        log.info("obsoleteFilter = " + obsoleteFilter);
        log.info(getClass().getSimpleName() + " successfully initialized.");
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setSearchRequest(SearchRequest searchRequest) {
        this.searchRequest = searchRequest;
    }

    public void setFailedAccessLdapMapper(FailedAccessLdapMapper failedAccessLdapMapper) {
        this.failedAccessLdapMapper = failedAccessLdapMapper;
    }

    public void setObjectComparatorFactory(ObjectComparatorFactory objectComparatorFactory) {
        this.objectComparatorFactory = objectComparatorFactory;
    }

    public String getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(String searchFilter) {
        this.searchFilter = searchFilter;
    }

    public String getLoadFilter() {
        return loadFilter;
    }

    public void setLoadFilter(String loadFilter) {
        this.loadFilter = loadFilter;
    }

    public String getObsoleteFilter() {
        return obsoleteFilter;
    }

    public void setObsoleteFilter(String obsoleteFilter) {
        this.obsoleteFilter = obsoleteFilter;
    }

    private String createUid(FailedAccess failedAccess) {
        if (failedAccess == null) {
            failedAccess = new FailedAccessDto();
        }
        return createUid(failedAccess.getResourceId(), failedAccess.getRemoteHost());
    }

    private String createUid(String resourceId, String remoteHost) {
        String uid = resourceId + "@" + remoteHost;
        if (CodingUtils.toBytesSilently(uid, "UTF-8").length < 256) {
            return uid;
        }
        return CodingUtils.toHex(CodingUtils.digestSilenty(CodingUtils.getInstanceSilently("MD5"),
                CodingUtils.toBytesSilently(uid, "UTF-8")));
    }

    /**
     * Gets connection from the factory. Opens the connection if needed.
     *
     * @return the connection
     * @throws LdapException
     *             the ldap exception
     */
    private Connection getConnection() throws LdapException {
        final Connection c = this.connectionFactory.getConnection();
        if (!c.isOpen()) {
            c.open();
        }
        return c;
    }

    /**
     * Close the given context and ignore any thrown exception. This is useful
     * for typical finally blocks in manual Ldap statements.
     *
     * @param context
     *            the Ldap connection to close
     */
    private void closeConnection(final Connection context) {
        if (context != null && context.isOpen()) {
            try {
                context.close();
            } catch (final Exception ex) {
                log.warn("Closing ldap connection failed.", ex);
            }
        }
    }

    /**
     * Checks to see if response has a result.
     *
     * @param response
     *            the response
     * @return true, if successful
     */
    private boolean hasResults(final Response<SearchResult> response) {
        final SearchResult result = response.getResult();
        if (result != null && result.getEntry() != null) {
            return true;
        }
        if (log.isDebugEnabled()) {
            log.debug("Requested ldap operation did not return a result or an ldap entry [code = "
                    + response.getResultCode() + ", message = " + response.getMessage() + "]");
        }
        return false;
    }

    /**
     * Search for access failed entry by id.
     *
     * @param connection
     *            the connection
     * @param id
     *            the id
     * @return the response
     * @throws LdapException
     *             the ldap exception
     */
    private Response<SearchResult> searchForFailedAccessById(final Connection connection, final String id)
            throws LdapException {

        final SearchFilter filter = new SearchFilter(this.searchFilter);
        filter.setParameter(0, id);
        return executeSearchOperation(connection, filter);
    }

    /**
     * Execute search operation.
     *
     * @param connection
     *            the connection
     * @param filter
     *            the filter
     * @return the response
     * @throws LdapException
     *             the ldap exception
     */
    private Response<SearchResult> executeSearchOperation(final Connection connection, final SearchFilter filter)
            throws LdapException {

        final SearchOperation searchOperation = new SearchOperation(connection);
        final SearchRequest request = newRequest(filter);
        if (log.isDebugEnabled()) {
            log.debug("Using search request " + request.toString());
        }
        return searchOperation.execute(request);
    }

    /**
     * Builds a new request.
     *
     * @param filter
     *            the filter
     * @return the search request
     */
    private SearchRequest newRequest(final SearchFilter filter) {
        
        int a = 0;

        final SearchRequest sr = new SearchRequest(this.searchRequest.getBaseDn(), filter);
        sr.setBinaryAttributes(ReturnAttributes.ALL_USER.value());
        sr.setDerefAliases(this.searchRequest.getDerefAliases());
        sr.setSearchEntryHandlers(this.searchRequest.getSearchEntryHandlers());
        sr.setSearchReferenceHandlers(this.searchRequest.getSearchReferenceHandlers());
        // TODO new in 1.2
        //sr.setFollowReferrals(this.searchRequest.getFollowReferrals());
        sr.setReturnAttributes(ReturnAttributes.ALL_USER.value());
        sr.setSearchScope(this.searchRequest.getSearchScope());
        sr.setSizeLimit(this.searchRequest.getSizeLimit());
        sr.setSortBehavior(this.searchRequest.getSortBehavior());
        // TODO new in 1.2
        //sr.setTimeLimit(this.searchRequest.getTimeLimit());
        sr.setTypesOnly(this.searchRequest.getTypesOnly());
        sr.setControls(this.searchRequest.getControls());
        
        // TODO new in 1.2
        sr.setControls(this.searchRequest.getControls());
        sr.setIntermediateResponseHandlers(this.searchRequest.getIntermediateResponseHandlers());
        sr.setReferralHandler(this.searchRequest.getReferralHandler());
        sr.setTimeLimit(this.searchRequest.getTimeLimit());
        
        return sr;
    }

    /**
     * Update the ldap entry with the given failed access.
     *
     * @param failedAccess
     *            the rs
     * @return the registered service
     */
    private FailedAccessDto update(final FailedAccess failedAccess) {

        Connection searchConnection = null;
        try {
            searchConnection = getConnection();
            final Response<SearchResult> response = searchForFailedAccessById(searchConnection,
                    failedAccess.getId().toString());
            if (hasResults(response)) {
                final String currentDn = response.getResult().getEntry().getDn();

                Connection modifyConnection = null;
                try {
                    modifyConnection = getConnection();
                    final ModifyOperation operation = new ModifyOperation(searchConnection);

                    final List<AttributeModification> mods = new ArrayList<>();

                    final LdapEntry entry = failedAccessLdapMapper
                            .mapFromFailedAccessLdapEntity(this.searchRequest.getBaseDn(), failedAccess);
                    for (final LdapAttribute attr : entry.getAttributes()) {
                        if (!attr.getName().equals(failedAccessLdapMapper.getIdAttribute())) {
                            mods.add(new AttributeModification(AttributeModificationType.REPLACE, attr));
                        }
                    }
                    final ModifyRequest request = new ModifyRequest(currentDn,
                            mods.toArray(new AttributeModification[] {}));
                    operation.execute(request);

                } catch (final LdapException e) {
                    String message = "Updating " + failedAccess + " failed.";
                    log.error(message, e);
                    throw new RuntimeException(message, e);

                } finally {
                    closeConnection(modifyConnection);
                }

                return new FailedAccessDto(failedAccess);

            } else {

                FailedAccessDto dto = new FailedAccessDto(failedAccess);
                dto.setId(null);
                return save(dto);
            }

        } catch (final LdapException e) {
            String message = "Updating " + failedAccess + " failed.";
            log.error(message, e);
            throw new RuntimeException(message, e);

        } finally {
            closeConnection(searchConnection);
        }
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

        if (failedAccess.getId() != null) {
            return update(failedAccess);
        }

        FailedAccessDto entity = new FailedAccessDto(failedAccess);
        entity.setId(createUid(failedAccess));
        Connection connection = null;
        try {
            connection = getConnection();
            final AddOperation operation = new AddOperation(connection);

            final LdapEntry entry = this.failedAccessLdapMapper
                    .mapFromFailedAccessLdapEntity(this.searchRequest.getBaseDn(), entity);
            operation.execute(new AddRequest(entry.getDn(), entry.getAttributes()));

            return entity;

        } catch (final LdapException e) {

            String message = "Saving " + failedAccess + " failed.";
            log.error(message, e);
            throw new RuntimeException(message, e);

        } finally {
            closeConnection(connection);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.domain.FailedAccessDao#getById(java.io.Serializable)
     */
    @Override
    public FailedAccessDto getById(Serializable _id) {

        if (_id == null) {
            return null;
        }
        String id = _id.toString();
        Connection connection = null;
        try {
            connection = getConnection();

            final Response<SearchResult> response = searchForFailedAccessById(connection, id);
            if (hasResults(response)) {
                return failedAccessLdapMapper.mapToFailedAccessLdapEntity(response.getResult().getEntry());
            }
            return null;

        } catch (final LdapException e) {
            String message = "Getting failed access by ID [" + _id + "] failed.";
            log.error(message, e);
            throw new RuntimeException(message, e);

        } finally {
            closeConnection(connection);
        }
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
        return getById(createUid(resourceId, remoteHost));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.domain.FailedAccessDao#removeById(java.io.Serializable)
     */
    @Override
    public boolean removeById(Serializable _id) {

        if (_id == null) {
            return false;
        }
        String id = _id.toString();
        Connection connection = null;
        try {
            connection = getConnection();

            final Response<SearchResult> response = searchForFailedAccessById(connection, id);
            if (hasResults(response)) {
                final LdapEntry entry = response.getResult().getEntry();
                final DeleteOperation delete = new DeleteOperation(connection);
                final DeleteRequest request = new DeleteRequest(entry.getDn());
                final Response<Void> res = delete.execute(request);
                return res.getResultCode() == ResultCode.SUCCESS;
            }
            return false;

        } catch (final LdapException e) {
            String message = "Removing failed access by ID [" + _id + "] failed.";
            log.error(message, e);
            throw new RuntimeException(message, e);

        } finally {
            closeConnection(connection);
        }
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
        return removeById(createUid(resourceId, remoteHost));
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
        maxResults = Math.min(maxResults, entities.size());
        for (int i = firstResult; i - firstResult < maxResults; i++) {
            resultList.add(entities.get(i));
        }

        return resultList;
    }

    private List<FailedAccessDto> find(String searchValue) {

        Connection connection = null;
        final List<FailedAccessDto> list = new LinkedList<>();
        try {
            String loadFilter;
            if (StringUtils.isBlank(searchValue)) {
                loadFilter = this.loadFilter;
            } else {
                String[] tags = TagUtils.buildTags(searchValue);
                if (tags == null || tags.length == 0) {
                    loadFilter = this.loadFilter;
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("(&");
                    if (!this.loadFilter.startsWith("(")) {
                        sb.append("(");
                    }
                    sb.append(this.loadFilter);
                    if (!this.loadFilter.endsWith(")")) {
                        sb.append(")");
                    }
                    sb.append("(|");
                    for (String tag : tags) {
                        sb.append("(").append(failedAccessLdapMapper.getResourceIdAttribute()).append("=*").append(tag)
                                .append("*)").append("(").append(failedAccessLdapMapper.getRemoteHostAttribute())
                                .append("=*").append(tag).append("*)");
                    }
                    sb.append("))");
                    loadFilter = sb.toString();
                }
            }
            connection = getConnection();
            final Response<SearchResult> response = executeSearchOperation(connection, new SearchFilter(loadFilter));
            if (hasResults(response)) {
                for (final LdapEntry entry : response.getResult().getEntries()) {
                    final FailedAccessDto failedAccessEntity = this.failedAccessLdapMapper
                            .mapToFailedAccessLdapEntity(entry);
                    list.add(failedAccessEntity);
                }
            }

        } catch (final LdapException e) {
            String message = "Finding failed access entries failed.";
            log.error(message, e);
            throw new RuntimeException(message, e);

        } finally {
            closeConnection(connection);
        }

        return list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.domain.FailedAccessDao#findObsolete(long)
     */
    @Override
    public List<? extends FailedAccessDto> findObsolete(long removeFailedAccessEntriesAfterMillis) {

        Connection connection = null;
        final List<FailedAccessDto> list = new LinkedList<FailedAccessDto>();
        try {
            connection = getConnection();
            final Object[] params = new Object[1];
            params[0] = failedAccessLdapMapper.getSimpleDateFormat()
                    .format(new Date(System.currentTimeMillis() - removeFailedAccessEntriesAfterMillis));
            final Response<SearchResult> response = executeSearchOperation(connection,
                    new SearchFilter(obsoleteFilter, params));
            if (hasResults(response)) {
                for (final LdapEntry entry : response.getResult().getEntries()) {
                    final FailedAccessDto failedAccessEntity = this.failedAccessLdapMapper
                            .mapToFailedAccessLdapEntity(entry);
                    list.add(failedAccessEntity);
                }
            }

        } catch (final LdapException e) {
            String message = "Finding obsolete failed access entries failed.";
            log.error(message, e);
            throw new RuntimeException(message, e);

        } finally {
            closeConnection(connection);
        }

        return list;
    }

}
