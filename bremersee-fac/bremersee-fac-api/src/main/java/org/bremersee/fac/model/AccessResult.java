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

import org.bremersee.fac.FailedAccessCounter;
import org.bremersee.fac.FailedAccessCounterImpl;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * A result of the {@link FailedAccessCounter}.
 * </p>
 * 
 * @author Christian Bremer
 */
public interface AccessResult extends Serializable {

    /**
     * Returns whether access is granted or not.
     * 
     * @return {@code true} if access is granted otherwise {@code false}
     */
    boolean isAccessGranted();

    /**
     * The time stamp (millis) of the decision (optional,
     * {@link FailedAccessCounterImpl} sets the time stamp always).
     * 
     * @return the time stamp of the decision or {@code null}
     */
    Long getTimestamp();
    
    Integer getCounter();
    
    Integer getCounterThreshold();

    /**
     * If {@link AccessResult#isAccessGranted()} returns {@code false}, this
     * method returns the {@link Date} until the access to the resource is
     * denied, otherwise {@code null}. But it may return {@code null}, too, when
     * the {@link FailedAccessCounter} doesn't provide such a date. This depends
     * on the implementation ({@link FailedAccessCounterImpl} supports it).
     * 
     * @return the date until the access to the resource is denied or
     *         {@code null}
     */
    Date getAccessDeniedUntil();

}
