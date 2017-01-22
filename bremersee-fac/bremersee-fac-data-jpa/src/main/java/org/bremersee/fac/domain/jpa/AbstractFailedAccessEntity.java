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

import org.bremersee.fac.model.FailedAccess;
import org.bremersee.fac.model.FailedAccessHelper;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * @author Christian Bremer
 */
@SuppressWarnings("WeakerAccess")
@MappedSuperclass
public abstract class AbstractFailedAccessEntity implements FailedAccess {

    private static final long serialVersionUID = 1L;

    @Column(name = "resource_id", nullable = false, length = 75)
    private String resourceId; // this is something like username, registration hash etc.

    @Column(name = "remote_host", nullable = false)
    private String remoteHost;

    @Column(name = "counter", nullable = false)
    private int counter;

    @Column(name = "creation_millis", nullable = false)
    private long creationDate;

    @Column(name = "modification_millis", nullable = false)
    private long modificationDate;

    /**
     * Default constructor.
     */
    public AbstractFailedAccessEntity() {
        long ct = System.currentTimeMillis();
        this.creationDate = ct;
        this.modificationDate = ct;
    }

    /**
     * Creates a failed access entry with the values of the given one.
     *
     * @param failedAccess the failed access entry source
     */
    public AbstractFailedAccessEntity(FailedAccess failedAccess) {
        this();
        update(failedAccess);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [id=" + getId() + ", resourceId=" + resourceId + ", remoteHost="
                + remoteHost + ", failedAccessCounter=" + counter + ", creationDate=" + getCreationDate()
                + ", modificationDate=" + getModificationDate() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((remoteHost == null) ? 0 : remoteHost.hashCode());
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof FailedAccess))
            return false;
        FailedAccess other = (FailedAccess) obj;
        if (remoteHost == null) {
            if (other.getRemoteHost() != null)
                return false;
        } else if (!remoteHost.equals(other.getRemoteHost()))
            return false;
        if (resourceId == null) {
            if (other.getResourceId() != null)
                return false;
        } else if (!resourceId.equals(other.getResourceId()))
            return false;
        return true;
    }

    @Override
    public int compareTo(FailedAccess o) {
        return new FailedAccessHelper().compare(this, o);
    }

    /**
     * Updates this failed access entry with the given one.
     *
     * @param failedAccess the update source
     */
    protected abstract void update(FailedAccess failedAccess);

    @Override
    public Date getCreationDate() {
        return new Date(creationDate);
    }

    /**
     * Sets the the creation date.
     */
    public void setCreationDate(Date date) {
        if (date != null) {
            this.creationDate = date.getTime();
        }
    }

    @Override
    public Date getModificationDate() {
        return new Date(modificationDate);
    }

    /**
     * Sets the the modification date.
     */
    public void setModificationDate(Date date) {
        if (date != null) {
            this.modificationDate = date.getTime();
        }
    }

    @Override
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Sets the the resource identifier.
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String getRemoteHost() {
        return remoteHost;
    }

    /**
     * Sets the remote host.
     */
    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    @Override
    public int getCounter() {
        return counter;
    }

    /**
     * Sets the counter.
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }

}
