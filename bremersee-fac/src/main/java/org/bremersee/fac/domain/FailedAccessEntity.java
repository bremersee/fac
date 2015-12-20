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

package org.bremersee.fac.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.bremersee.fac.model.FailedAccess;

/**
 * <p>
 * Entity to persist failed access entries.
 * </p>
 * 
 * @author Christian Bremer
 */
@Entity
@Table(name = "failed_access", uniqueConstraints = {
        @UniqueConstraint(name = "resource_remote_host", columnNames = { "resource", "remote_host" }) })
public class FailedAccessEntity extends AbstractFailedAccessEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Default constructor.
     */
    public FailedAccessEntity() {
        super();
    }

    /**
     * Creates a {@link FailedAccessEntity} from the given {@link FailedAccess}
     * instance.
     * 
     * @param failedAccess
     *            the {@link FailedAccess} instance
     */
    public FailedAccessEntity(FailedAccess failedAccess) {
        super(failedAccess);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bremersee.fac.domain.AbstractFailedAccessEntity#update(org.bremersee.
     * fac.model.FailedAccess)
     */
    @Override
    protected void update(FailedAccess failedAccess) {
        if (failedAccess != null) {
            if (isNumber(failedAccess.getId())) {
                setId(Long.parseLong(failedAccess.getId().toString()));
            }
            setCounter(failedAccess.getCounter());
            setCreationDate(failedAccess.getCreationDate());
            setModificationDate(failedAccess.getModificationDate());
            setRemoteHost(failedAccess.getRemoteHost());
            setResourceId(failedAccess.getResourceId());
        }
    }

    private boolean isNumber(Serializable id) {
        if (id == null) {
            return false;
        }
        try {
            Long.parseLong(id.toString());
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bremersee.fac.domain.AbstractFailedAccessEntity#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     */
    protected void setId(Long id) {
        this.id = id;
    }

}
