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

import java.util.Date;

import org.bremersee.fac.model.FailedAccess;
import org.bremersee.fac.model.ResourceDescription;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * <p>
 * Data transfer object of a {@link FailedAccess}.
 * </p>
 * 
 * @author Christian Bremer
 */
@Document(collection = "failedAccess")
public class FailedAccessMongoDoc implements FailedAccess, Cloneable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Indexed(name = "idx_resource_id")
    private String resourceId = ResourceDescription.UNKNOWN_RESOURCE;

    @Indexed(name = "idx_remote_host")
    private String remoteHost;

    private int counter;

    private Date creationDate;

    @Indexed(name = "idx_modification_date")
    private Date modificationDate;

    /**
     * Default constructor.
     */
    public FailedAccessMongoDoc() {
        creationDate = new Date();
        modificationDate = (Date) creationDate.clone();
    }

    /**
     * Constructs an instance with the specified parameters.
     * 
     * @param resourceId
     *            the ID of the resource
     * @param remoteHost
     *            the remote host
     */
    public FailedAccessMongoDoc(String resourceId, String remoteHost) {
        this();
        this.resourceId = resourceId;
        this.remoteHost = remoteHost;
    }

    /**
     * Constructs a clone of the specified {@link FailedAccess}.
     * 
     * @param failedAccess
     *            the failed access
     */
    public FailedAccessMongoDoc(FailedAccess failedAccess) {
        this();
        update(failedAccess);
    }

    @Override
    public String toString() {
        return "FailedAccessMongoDoc [id=" + id + ", resourceId=" + resourceId + ", remoteHost=" + remoteHost + ", counter="
                + counter + ", creationDate=" + creationDate + ", modificationDate=" + modificationDate + "]";
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
    public FailedAccessMongoDoc clone() {
        return new FailedAccessMongoDoc(this);
    }

    @Override
    public int compareTo(FailedAccess o) {
        String s1 = getResourceId() == null ? "" : getResourceId();
        String s2 = o == null ? "" : o.getResourceId() == null ? "" : o.getResourceId();
        int c = s1.compareTo(s2);
        if (c != 0) {
            return c;
        }
        s1 = remoteHost == null ? "" : remoteHost;
        s2 = o == null ? "" : o.getRemoteHost() == null ? "" : o.getRemoteHost();
        c = s1.compareTo(s2);
        if (c != 0) {
            return c;
        }
        if (modificationDate != null && o != null && o.getModificationDate() != null) {
            c = modificationDate.compareTo(o.getModificationDate());
            if (c != 0) {
                return c;
            }
        }
        if (o != null) {
            c = counter - o.getCounter();
            if (c != 0) {
                return c;
            }
        }
        s1 = id == null ? "" : id;
        s2 = o == null ? "" : o.getId() == null ? "" : o.getId().toString();
        return s1.compareTo(s2);
    }

    /**
     * Updates this failed access by another one.
     * 
     * @param failedAccess
     *            another failed access
     */
    public void update(FailedAccess failedAccess) {
        if (failedAccess != null) {
            this.counter = failedAccess.getCounter();
            this.creationDate = failedAccess.getCreationDate();
            this.id = failedAccess.getId() != null ? failedAccess.getId().toString() : null;
            this.modificationDate = failedAccess.getModificationDate();
            this.remoteHost = failedAccess.getRemoteHost();
            this.resourceId = failedAccess.getResourceId();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.model.FailedAccess#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Sets the ID.
     * 
     * @param id
     *            the ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.model.FailedAccess#getResourceId()
     */
    @Override
    public String getResourceId() {
        if (resourceId == null || resourceId.trim().length() == 0) {
            resourceId = ResourceDescription.UNKNOWN_RESOURCE;
        }
        return resourceId;
    }

    /**
     * Sets the ID of the resource.
     * 
     * @param resourceId
     *            the ID of the resource
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.model.FailedAccess#getRemoteHost()
     */
    @Override
    public String getRemoteHost() {
        return remoteHost;
    }

    /**
     * Sets the remote host.
     * 
     * @param remoteHost
     *            the remote host
     */
    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.model.FailedAccess#getCounter()
     */
    @Override
    public int getCounter() {
        return counter;
    }

    /**
     * Sets the counter.
     * 
     * @param failedAccessCounter
     *            the counter
     */
    public void setCounter(int failedAccessCounter) {
        this.counter = failedAccessCounter;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date.
     * 
     * @param creationDate
     *            the creation date
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public Date getModificationDate() {
        return modificationDate;
    }

    /**
     * Sets the last modification date.
     * 
     * @param modificationDate
     *            the last modification date
     */
    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

}
