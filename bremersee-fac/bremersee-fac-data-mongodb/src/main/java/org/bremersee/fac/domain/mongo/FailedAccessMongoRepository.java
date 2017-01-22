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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Christian Bremer
 */
@Repository("failedAccessMongoRepository")
public interface FailedAccessMongoRepository extends MongoRepository<FailedAccessMongoDoc, String>, FailedAccessMongoRepositoryCustom {

    FailedAccessMongoDoc findByResourceIdAndRemoteHost(String resourceId, String remoteHost);

    Page<FailedAccessMongoDoc> findByResourceIdLikeOrRemoteHostLike(String resourceId, String remoteHost, Pageable pageable);

}
