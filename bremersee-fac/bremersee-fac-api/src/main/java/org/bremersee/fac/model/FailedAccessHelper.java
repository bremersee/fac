/*
 * Copyright 2017 the original author or authors.
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

import java.util.Comparator;

/**
 * @author Christian Bremer
 */
public class FailedAccessHelper implements Comparator<FailedAccess> {

    @Override
    public int compare(FailedAccess o1, FailedAccess o2) { // NOSONAR

        String s1 = o1 == null || o1.getResourceId() == null ? "" : o1.getResourceId();
        String s2 = o2 == null || o2.getResourceId() == null ? "" : o2.getResourceId();
        int c = s1.compareTo(s2);
        if (c != 0) {
            return c;
        }
        s1 = o1 == null || o1.getRemoteHost() == null ? "" : o1.getRemoteHost();
        s2 = o2 == null || o2.getRemoteHost() == null ? "" : o2.getRemoteHost();
        c = s1.compareTo(s2);
        if (c != 0) {
            return c;
        }
        if (o1 != null && o1.getModificationDate() != null && o2 != null && o2.getModificationDate() != null) {
            c = o1.getModificationDate().compareTo(o2.getModificationDate());
            if (c != 0) {
                return c;
            }
        }
        if (o2 != null && o1 != null) {
            c = o1.getCounter() - o2.getCounter();
            if (c != 0) {
                return c;
            }
        }
        s1 = o1 == null || o1.getId() == null ? "" : o1.getId().toString();
        s2 = o2 == null || o2.getId() == null ? "" : o2.getId().toString();
        return s1.compareTo(s2);
    }

}
