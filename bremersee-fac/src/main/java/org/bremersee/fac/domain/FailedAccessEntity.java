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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.bremersee.fac.model.FailedAccess;

/**
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

    public FailedAccessEntity(FailedAccess failedAccess) {
        super(failedAccess);
    }

    @Override
    protected void update(FailedAccess failedAccess) {
        if (failedAccess != null) {
            if (failedAccess.getId() != null) {
                try {
                    setId(Long.parseLong(failedAccess.getId().toString()));
                } catch (Exception ignored) {
                    // ignored
                }
            }
            setCounter(failedAccess.getCounter());
            setCreationDate(failedAccess.getCreationDate());
            setModificationDate(failedAccess.getModificationDate());
            setRemoteHost(failedAccess.getRemoteHost());
            setResourceId(failedAccess.getResourceId());
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

}
