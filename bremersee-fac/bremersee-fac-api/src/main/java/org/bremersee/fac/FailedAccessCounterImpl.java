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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bremersee.comparator.ObjectComparatorFactory;
import org.bremersee.fac.domain.FailedAccessDao;
import org.bremersee.fac.domain.mem.FailedAccessInMemoryDao;
import org.bremersee.fac.model.AccessResultDto;
import org.bremersee.fac.model.BooleanDto;
import org.bremersee.fac.model.FailedAccess;
import org.bremersee.fac.model.FailedAccessDto;
import org.bremersee.pagebuilder.PageBuilder;
import org.bremersee.pagebuilder.PageBuilderImpl;
import org.bremersee.pagebuilder.model.PageDto;
import org.bremersee.pagebuilder.model.PageRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Default failed access counter implementation. It can use any store that
 * implements {@link FailedAccessDao}. It uses an internal thread for removing
 * obsolete failed access entries. So it is important that the failed access
 * counter is started and stopped properly (see
 * {@link FailedAccessCounterImpl#start()} and
 * {@link FailedAccessCounterImpl#stop()}).
 * </p>
 * 
 * @author Christian Bremer
 */
public class FailedAccessCounterImpl implements FailedAccessCounter {
    
    /**
     * The logger
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * The failed access DAO
     */
    protected FailedAccessDao failedAccessDao;

    /**
     * The page builder
     */
    protected PageBuilder pageBuilder = new PageBuilderImpl();

    private int failedAccessCounterThreshold = 10;

    private long removeFailedAccessEntriesAfterMillis = 1000L * 60L * 60L * 23L;

    private long removeFailedEntriesInterval = 1000L * 60L * 60L * 1L;

    private volatile long lastRemovingOfFailedEntries = System.currentTimeMillis();

    private volatile long lastRemovingOfFailedEntriesDuration = 0L;

    private volatile int lastRemovingOfFailedEntriesSize = 0;

    private volatile long removedFailedEntriesTotalSize = 0L;

    private volatile boolean removingRunnning = false;

    /**
     * The thread for removing obsolete failed access entries.
     */
    private final Thread removeFailedEntriesThread = new Thread() {

        @Override
        public void run() {
            while (!isInterrupted()) {
                FailedAccessCounterImpl.this.removeObsoleteFailedAccessEntries();
                try {
                    Thread.sleep(FailedAccessCounterImpl.this.getRemoveFailedEntriesInterval());
                } catch (InterruptedException e) {
                    this.interrupt();
                    return;
                }
            }
        }
    };

    /**
     * Starts the failed access counter.
     */
    @PostConstruct
    public void start() {
        log.info("Starting " + getClass().getSimpleName() + " ...");
        if (failedAccessDao == null) {
            FailedAccessInMemoryDao failedAccessDao = new FailedAccessInMemoryDao();
            failedAccessDao.setObjectComparatorFactory(ObjectComparatorFactory.newInstance());
            failedAccessDao.init();
            this.failedAccessDao = failedAccessDao;
        }
        log.info("failedAccessDao = " + failedAccessDao.getClass().getSimpleName());
        log.info("failedAccessCounterThreshold = " + getFailedAccessCounterThreshold());
        log.info("removeFailedAccessEntriesAfterMillis = " + getRemoveFailedAccessEntriesAfterMillis());
        log.info("removeFailedEntriesInterval = " + getRemoveFailedEntriesInterval());
        lastRemovingOfFailedEntries = System.currentTimeMillis();
        removeFailedEntriesThread.start();
        log.info(getClass().getSimpleName() + " successfully started.");
    }

    /**
     * Stops the failed access counter.
     */
    @PreDestroy
    public void stop() {
        log.info("Stopping " + getClass().getSimpleName() + " ...");
        removeFailedEntriesThread.interrupt();
        log.info(getClass().getSimpleName() + " successfully stopped.");
    }

    /**
     * Sets the failed access DAO.
     * 
     * @param failedAccessDao
     *            the failed access DAO
     */
    public void setFailedAccessDao(FailedAccessDao failedAccessDao) {
        this.failedAccessDao = failedAccessDao;
    }

    /**
     * Sets the page builder.
     * 
     * @param pageBuilder
     *            the page builder
     */
    public void setPageBuilder(PageBuilder pageBuilder) {
        if (pageBuilder != null) {
            this.pageBuilder = pageBuilder;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.FailedAccessCounter#getFailedAccessCounterThreshold()
     */
    @Override
    public int getFailedAccessCounterThreshold() {
        return failedAccessCounterThreshold;
    }

    /**
     * Sets the counter threshold.
     * 
     * @param failedAccessCounterThreshold
     *            the counter threshold
     */
    public void setFailedAccessCounterThreshold(int failedAccessCounterThreshold) {
        this.failedAccessCounterThreshold = failedAccessCounterThreshold;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.FailedAccessCounter#
     * getRemoveFailedAccessEntriesAfterMillis()
     */
    @Override
    public long getRemoveFailedAccessEntriesAfterMillis() {
        return removeFailedAccessEntriesAfterMillis;
    }

    /**
     * Sets the lifetime of a failed access entry.
     * 
     * @param removeFailedAccessEntriesAfterMillis
     *            the lifetime of a failed access entry
     */
    public void setRemoveFailedAccessEntriesAfterMillis(long removeFailedAccessEntriesAfterMillis) {
        this.removeFailedAccessEntriesAfterMillis = removeFailedAccessEntriesAfterMillis;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.FailedAccessCounter#getRemoveFailedEntriesInterval()
     */
    @Override
    public long getRemoveFailedEntriesInterval() {
        return removeFailedEntriesInterval;
    }

    /**
     * Sets the interval of removing obsolete failed access entries.
     * 
     * @param removeFailedEntriesInterval
     *            the interval of removing obsolete failed access entries
     */
    public void setRemoveFailedEntriesInterval(long removeFailedEntriesInterval) {
        this.removeFailedEntriesInterval = removeFailedEntriesInterval;
    }

    /**
     * Returns {@code true} if the access to the resource is not blocked for the
     * remote host, otherwise {@code false}.
     * 
     * @param failedAccess
     *            the failed access entry
     * @return {@code true} if the access to the resource is not blocked for the
     *         remote host, otherwise {@code false}
     */
    protected boolean isAccessGranted(FailedAccess failedAccess) {
        return failedAccess == null || failedAccess.getCounter() <= this.failedAccessCounterThreshold;
    }

    /**
     * Calculates how log the resource is blocked for the remote host.
     * 
     * @param failedAccess
     *            the failed access entry
     * @return the date until the resource is blocked for the remote host or
     *         {@code null}, if the resource is not blocked
     */
    protected Date calculateAccessDeniedUntil(FailedAccess failedAccess) {
        if (isAccessGranted(failedAccess) || failedAccess.getModificationDate() == null) {
            return null;
        }

        long accessDeniedUntilMillis = failedAccess.getModificationDate().getTime()
                + removeFailedAccessEntriesAfterMillis;

        long nextRemove = lastRemovingOfFailedEntries + removeFailedEntriesInterval;
        while (nextRemove < accessDeniedUntilMillis) {
            nextRemove = nextRemove + removeFailedEntriesInterval;
        }

        long plus = nextRemove - accessDeniedUntilMillis;

        accessDeniedUntilMillis = accessDeniedUntilMillis + plus;

        return new Date(accessDeniedUntilMillis);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.FailedAccessCounter#getLastRemovingOfFailedEntries()
     */
    @Override
    public Date getLastRemovingOfFailedEntries() {
        return new Date(lastRemovingOfFailedEntries);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.FailedAccessCounter#
     * getLastRemovingOfFailedEntriesDuration()
     */
    @Override
    public long getLastRemovingOfFailedEntriesDuration() {
        return lastRemovingOfFailedEntriesDuration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.FailedAccessCounter#getLastRemovingOfFailedEntriesSize(
     * )
     */
    @Override
    public int getLastRemovingOfFailedEntriesSize() {
        return lastRemovingOfFailedEntriesSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.FailedAccessCounter#getRemovedFailedEntriesTotalSize()
     */
    @Override
    public long getRemovedFailedEntriesTotalSize() {
        return removedFailedEntriesTotalSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.FailedAccessCounter#getFailedAccessEntries(org.
     * bremersee.pagebuilder.model.PageRequestDto)
     */
    @Override
    public PageDto getFailedAccessEntries(PageRequestDto pageRequest) {

        if (pageRequest == null) {
            pageRequest = new PageRequestDto();
        }

        //@formatter:off
        Long totalSize = failedAccessDao.count(pageRequest.getQuery());
        List<? extends FailedAccess> entities = failedAccessDao
                .find(pageRequest.getQuery(), 
                        pageRequest.getFirstResult(), 
                        pageRequest.getPageSize(), 
                        pageRequest.getComparatorItem());
        //@formatter:on

        List<FailedAccessDto> entries = new ArrayList<>();
        for (FailedAccess entity : entities) {
            entries.add(new FailedAccessDto(entity));
        }

        PageDto page = pageBuilder.buildPage(entries, pageRequest, totalSize);

        return page;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.FailedAccessCounter#getFailedAccessEntry(java.io.
     * Serializable)
     */
    @Override
    public FailedAccessDto getFailedAccessEntry(Serializable id) {

        FailedAccess entity = failedAccessDao.getById(id);
        if (entity == null) {
            return null;
        }
        FailedAccessDto dto = new FailedAccessDto(entity);
        return dto;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.FailedAccessCounter#getFailedAccessEntry(java.lang.
     * String, java.lang.String)
     */
    @Override
    public FailedAccessDto getFailedAccessEntry(String resourceId, String remoteHost) {

        FailedAccess entity = failedAccessDao.getByResourceIdAndRemoteHost(resourceId, remoteHost);
        if (entity == null) {
            return null;
        }
        FailedAccessDto dto = new FailedAccessDto(entity);
        return dto;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.FailedAccessCounter#removeFailedAccessEntry(java.lang.
     * String, java.lang.String)
     */
    @Override
    public BooleanDto removeFailedAccessEntry(String resourceId, String remoteHost) {

        boolean value = failedAccessDao.removeByResourceIdAndRemoteHost(resourceId, remoteHost);
        if (value) {
            removedFailedEntriesTotalSize = removedFailedEntriesTotalSize + 1L;
        }
        return new BooleanDto(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.FailedAccessCounter#removeObsoleteFailedAccessEntries()
     */
    @Override
    public void removeObsoleteFailedAccessEntries() {

        if (removingRunnning) {
            return;
        }
        try {
            removingRunnning = true;
            lastRemovingOfFailedEntries = System.currentTimeMillis();
            int size = 0;
            List<? extends FailedAccess> entities = failedAccessDao.findObsolete(removeFailedAccessEntriesAfterMillis);
            Iterator<? extends FailedAccess> entityIterator = entities.iterator();
            while (entityIterator.hasNext()) {
                FailedAccess entity = entityIterator.next();
                removeFailedAccessEntry(entity.getResourceId(), entity.getRemoteHost());
                size = size + 1;
            }
            lastRemovingOfFailedEntriesSize = size;
            lastRemovingOfFailedEntriesDuration = System.currentTimeMillis() - lastRemovingOfFailedEntries;

        } finally {
            removingRunnning = false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.FailedAccessCounter#accessSucceeded(java.lang.String,
     * java.lang.String, java.lang.Long)
     */
    @Override
    public AccessResultDto accessSucceeded(String resourceId, String remoteHost, Long timeInMillis) {

        Date modificationDate = timeInMillis == null || timeInMillis <= 0 ? new Date() : new Date(timeInMillis);
        FailedAccess entity = failedAccessDao.getByResourceIdAndRemoteHost(resourceId, remoteHost);
        boolean accessGranted = isAccessGranted(entity);
        if (accessGranted && entity != null) {
            failedAccessDao.removeById(entity.getId());
        }
        return new AccessResultDto(accessGranted, modificationDate.getTime(), calculateAccessDeniedUntil(entity));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.FailedAccessCounter#accessFailed(java.lang.String,
     * java.lang.String, java.lang.Long)
     */
    @Override
    public AccessResultDto accessFailed(String resourceId, String remoteHost, Long timeInMillis) {

        Date modificationDate = timeInMillis == null || timeInMillis <= 0 ? new Date() : new Date(timeInMillis);
        FailedAccess entity = failedAccessDao.getByResourceIdAndRemoteHost(resourceId, remoteHost);
        FailedAccessDto dto;
        if (entity == null) {
            dto = new FailedAccessDto();
            dto.setCounter(1);
            dto.setCreationDate(modificationDate);
            dto.setModificationDate(modificationDate);
            dto.setRemoteHost(remoteHost);
            dto.setResourceId(resourceId);
        } else {
            dto = new FailedAccessDto(entity);
            dto.setModificationDate(modificationDate);
            dto.setCounter(dto.getCounter() + 1);
        }
        entity = failedAccessDao.save(dto);
        boolean accessGranted = isAccessGranted(entity);
        AccessResultDto result = new AccessResultDto(accessGranted, modificationDate.getTime(),
                calculateAccessDeniedUntil(dto));
        if (!accessGranted) {
            result.setCounter(entity.getCounter());
            result.setCounterThreshold(getFailedAccessCounterThreshold());
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.FailedAccessCounter#isAccessGranted(java.lang.String,
     * java.lang.String)
     */
    @Override
    public AccessResultDto isAccessGranted(String resourceId, String remoteHost) {

        FailedAccess entity = failedAccessDao.getByResourceIdAndRemoteHost(resourceId, remoteHost);
        long timestamp = entity != null ? entity.getModificationDate().getTime() : System.currentTimeMillis();
        boolean accessGranted = isAccessGranted(entity);
        AccessResultDto dto = new AccessResultDto(accessGranted, timestamp, calculateAccessDeniedUntil(entity));
        return dto;
    }

}
