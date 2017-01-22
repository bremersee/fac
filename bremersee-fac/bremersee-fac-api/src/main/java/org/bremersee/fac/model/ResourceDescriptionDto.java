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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import javax.xml.bind.annotation.*;
import java.util.Date;

/**
 * @author Christian Bremer
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "resourceDescription")
@XmlType(name = "resourceDescriptionType", propOrder = {"resourceId", "remoteHost", "accessTime"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.ALWAYS)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, creatorVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonPropertyOrder(value = {"resourceId", "remoteHost", "accessTime"})
public class ResourceDescriptionDto implements ResourceDescription {

    private static final long serialVersionUID = 1L;

    @XmlAttribute(name = "resourceId", required = true)
    @JsonProperty(value = "resourceId", required = true)
    private String resourceId = UNKNOWN_RESOURCE;

    @XmlAttribute(name = "remoteHost", required = true)
    @JsonProperty(value = "remoteHost", required = true)
    private String remoteHost;

    @XmlAttribute(name = "accessTimeInMillis")
    @JsonProperty(value = "accessTimeInMillis")
    private Date accessTime;

    /**
     * Default constructor.
     */
    public ResourceDescriptionDto() {
        super();
    }

    public ResourceDescriptionDto(ResourceDescription resourceDescription) {
        if (resourceDescription != null) {
            this.resourceId = resourceDescription.getResourceId();
            this.remoteHost = resourceDescription.getRemoteHost();
            this.accessTime = resourceDescription.getAccessTime();
        }
    }

    public ResourceDescriptionDto(String resourceId, String remoteHost, Date accessTime) {
        this.resourceId = resourceId;
        this.remoteHost = remoteHost;
        this.accessTime = accessTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ResourceDescriptionDto [resourceId=" + resourceId + ", remoteHost=" + remoteHost + ", accessTime="
                + accessTime + "]";
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
        result = prime * result + ((accessTime == null) ? 0 : accessTime.hashCode());
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
        if (getClass() != obj.getClass())
            return false;
        ResourceDescriptionDto other = (ResourceDescriptionDto) obj;
        if (accessTime == null) {
            if (other.accessTime != null)
                return false;
        } else if (!accessTime.equals(other.accessTime))
            return false;
        if (remoteHost == null) {
            if (other.remoteHost != null)
                return false;
        } else if (!remoteHost.equals(other.remoteHost))
            return false;
        if (resourceId == null) {
            if (other.resourceId != null)
                return false;
        } else if (!resourceId.equals(other.resourceId))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public ResourceDescriptionDto clone() { // NOSONAR
        return new ResourceDescriptionDto(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ResourceDescription o) { // NOSONAR
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
        if (accessTime != null && o != null && o.getAccessTime() != null) {
            c = accessTime.compareTo(o.getAccessTime());
        }
        return c;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.model.ResourceDescription#getResourceId()
     */
    @Override
    public String getResourceId() {
        if (resourceId == null || resourceId.trim().length() == 0) {
            resourceId = ResourceDescription.UNKNOWN_RESOURCE;
        }
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.model.ResourceDescription#getRemoteHost()
     */
    @Override
    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.model.ResourceDescription#getAccessTime()
     */
    @Override
    public Date getAccessTime() {
        return accessTime;
    }

    public void setAccessTimeInMillis(Date accessTime) {
        this.accessTime = accessTime;
    }

}
