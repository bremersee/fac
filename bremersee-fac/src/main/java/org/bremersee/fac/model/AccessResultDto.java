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
 * Data transfer object of an {@link AccessResult}.
 * </p>
 * 
 * @author Christian Bremer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "accessResult")
@XmlType(name = "accessResultType", propOrder = {
        "accessGranted",
        "timestamp", 
        "accessDeniedUntil" 
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.ALWAYS)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, 
    getterVisibility = Visibility.NONE, 
    creatorVisibility = Visibility.NONE, 
    isGetterVisibility = Visibility.NONE, 
    setterVisibility = Visibility.NONE)
@JsonPropertyOrder(value = {
        "accessGranted",
        "timestamp", 
        "accessDeniedUntil" 
})
public class AccessResultDto implements AccessResult, Cloneable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute(name = "accessGranted", required = true)
    @JsonProperty(value = "accessGranted", required = true)
    private boolean accessGranted;

    @XmlAttribute(name = "timestamp", required = false)
    @JsonProperty(value = "timestamp", required = false)
    private Long timestamp = System.currentTimeMillis();

    @XmlAttribute(name = "accessDeniedUntil", required = false)
    @JsonProperty(value = "accessDeniedUntil", required = false)
    private Date accessDeniedUntil;

    /**
     * Default constructor.
     */
    public AccessResultDto() {
    }

    /**
     * Constructs an instance with the specified parameter.
     * 
     * @param accessGranted
     *            is access to the resource granted?
     */
    public AccessResultDto(boolean accessGranted) {
        this.accessGranted = accessGranted;
    }

    /**
     * Constructs an instance with the specified parameters.
     * 
     * @param accessGranted
     *            is access to the resource granted?
     * @param timestamp
     *            the time stamp of the decision
     */
    public AccessResultDto(boolean accessGranted, long timestamp) {
        this.accessGranted = accessGranted;
        this.timestamp = timestamp;
    }

    /**
     * Constructs an instance with the specified parameters.
     * 
     * @param accessGranted
     *            is access to the resource granted?
     * @param timestamp
     *            the time stamp of the decision
     * @param accessDeniedUntil
     *            until is the access to the resource is denied
     */
    public AccessResultDto(boolean accessGranted, long timestamp, Date accessDeniedUntil) {
        this.accessGranted = accessGranted;
        this.timestamp = timestamp;
        this.accessDeniedUntil = accessDeniedUntil;
    }

    /**
     * Constructs a clone of another {@link AccessResult}.
     * 
     * @param accessResult
     *            another access result
     */
    public AccessResultDto(AccessResult accessResult) {
        if (accessResult != null) {
            this.accessGranted = accessResult.isAccessGranted();
            this.timestamp = accessResult.getTimestamp();
            this.accessDeniedUntil = accessResult.getAccessDeniedUntil();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AccessResultDto [accessGranted=" + accessGranted + ", timestamp=" + timestamp + ", accessDeniedUntil="
                + accessDeniedUntil + "]";
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
        result = prime * result + ((accessDeniedUntil == null) ? 0 : accessDeniedUntil.hashCode());
        result = prime * result + (accessGranted ? 1231 : 1237);
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
        AccessResultDto other = (AccessResultDto) obj;
        if (accessDeniedUntil == null) {
            if (other.accessDeniedUntil != null)
                return false;
        } else if (!accessDeniedUntil.equals(other.accessDeniedUntil))
            return false;
        if (accessGranted != other.accessGranted)
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public AccessResultDto clone() {
        return new AccessResultDto(this);
    }

    @Override
    public boolean isAccessGranted() {
        return accessGranted;
    }

    /**
     * Sets whether the access to the resource is granted or not.
     * 
     * @param accessGranted
     *            is access to the resource granted?
     */
    public void setAccessGranted(boolean accessGranted) {
        this.accessGranted = accessGranted;
    }

    @Override
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the time stamp.
     * 
     * @param timestamp
     *            the time stamp
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public Date getAccessDeniedUntil() {
        return accessDeniedUntil;
    }

    /**
     * Sets until the access to a resource is denied.
     * 
     * @param accessDeniedUntil
     *            a date
     */
    public void setAccessDeniedUntil(Date accessDeniedUntil) {
        this.accessDeniedUntil = accessDeniedUntil;
    }

}
