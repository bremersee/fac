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

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * Information about failed access to a resource.
 * </p>
 * 
 * @author Christian Bremer
 */
public interface FailedAccess extends Serializable, Comparable<FailedAccess> {

    /**
     * Returns the ID of the failed access entry.
     * 
     * @return the ID of the failed access entry
     */
    Serializable getId();

    /**
     * Returns the date of the creation.
     * 
     * @return the date of the creation
     */
    Date getCreationDate();

    /**
     * Returns the date of the last modification.
     * 
     * @return the date of the last modification
     */
    Date getModificationDate();

    /**
     * Returns the ID of the resource.
     * 
     * @return the ID of the resource
     */
    String getResourceId();

    /**
     * Returns the remote host.
     * 
     * @return the remote host
     */
    String getRemoteHost();

    /**
     * Returns how many accesses to the resource were failed.
     * 
     * @return how many accesses to the resource were failed
     */
    int getCounter();

}
