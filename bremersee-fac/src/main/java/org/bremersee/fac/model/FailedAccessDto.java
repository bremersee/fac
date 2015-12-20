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

package org.bremersee.fac.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * <p>
 * Data transfer object of a {@link FailedAccess}.
 * </p>
 * 
 * @author Christian Bremer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "failedAccess")
@XmlType(name = "failedAccessType", propOrder = {
        "id",
        "resourceId",
        "remoteHost",
        "counter",
        "creationDate",
        "modificationDate"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.ALWAYS)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, 
    getterVisibility = Visibility.NONE, 
    creatorVisibility = Visibility.NONE, 
    isGetterVisibility = Visibility.NONE, 
    setterVisibility = Visibility.NONE)
@JsonPropertyOrder(value = {
        "id",
        "resourceId",
        "remoteHost",
        "counter",
        "creationDate",
        "modificationDate"
})
public class FailedAccessDto implements FailedAccess, Cloneable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute(name = "id", required = false)
    @JsonProperty(value = "id", required = false)
    private String id;

    @XmlAttribute(name = "resourceId", required = true)
    @JsonProperty(value = "resourceId", required = true)
    private String resourceId = ResourceDescription.UNKNOWN_RESOURCE;

    @XmlAttribute(name = "remoteHost", required = true)
    @JsonProperty(value = "remoteHost", required = true)
    private String remoteHost;

    @XmlAttribute(name = "counter", required = true)
    @JsonProperty(value = "counter", required = true)
    private int counter;

    @XmlAttribute(name = "creationDate", required = false)
    @JsonProperty(value = "creationDate", required = false)
    private Date creationDate;

    @XmlAttribute(name = "modificationDate", required = false)
    @JsonProperty(value = "modificationDate", required = false)
    private Date modificationDate;

    /**
     * Default constructor.
     */
    public FailedAccessDto() {
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
    public FailedAccessDto(String resourceId, String remoteHost) {
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
    public FailedAccessDto(FailedAccess failedAccess) {
        this();
        update(failedAccess);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FailedAccessDto [id=" + id + ", resourceId=" + resourceId + ", remoteHost=" + remoteHost + ", counter="
                + counter + ", creationDate=" + creationDate + ", modificationDate=" + modificationDate + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((remoteHost == null) ? 0 : remoteHost.hashCode());
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public FailedAccessDto clone() {
        return new FailedAccessDto(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(FailedAccess o) {
        String s1 = getResourceId() == null ? "" : getResourceId();
        String s2 = o == null ? "" : (o.getResourceId() == null ? "" : o.getResourceId());
        int c = s1.compareTo(s2);
        if (c != 0) {
            return c;
        }
        s1 = remoteHost == null ? "" : remoteHost;
        s2 = o == null ? "" : (o.getRemoteHost() == null ? "" : o.getRemoteHost());
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
        s2 = o == null ? "" : (o.getId() == null ? "" : o.getId().toString());
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
