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

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;

/**
 * @author Christian Bremer
 */
@SuppressWarnings({"SpringAutowiredFieldsWarningInspection", "unused", "WeakerAccess"})
public class FailedAccessMongoRepositoryImpl implements FailedAccessMongoRepositoryCustom {

    @Autowired
    protected MongoOperations mongoOperations;

    public void setMongoOperations(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public long count(String searchValue) {

        Validate.notBlank(searchValue, "searchValue must not be null or blank");
        Query query = new Query();
        // TODO adjust when 1.9.x is available
        // MongoRegexCreator is available at 1.9.x
//        query.addCriteria(
//                Criteria.where("resourceId").regex(MongoRegexCreator.INSTANCE.toRegularExpression(searchValue, Part.Type.LIKE))
//                .orOperator(Criteria.where("remoteHost").regex(MongoRegexCreator.INSTANCE.toRegularExpression(searchValue, Part.Type.LIKE)))); // NOSONAR
        query.addCriteria(
                Criteria.where("resourceId").regex(toLikeRegex(searchValue))
                        .orOperator(Criteria.where("remoteHost").regex(toLikeRegex(searchValue))));
        return mongoOperations.count(query, FailedAccessMongoDoc.class);
    }

    @Override
    public List<FailedAccessMongoDoc> findObsolete(long removeFailedAccessEntriesAfterMillis) {

        long modificationDate = System.currentTimeMillis() - removeFailedAccessEntriesAfterMillis;
        Query query = new Query();
        query.addCriteria(Criteria.where("modificationDate").lt(new Date(modificationDate)));
        return mongoOperations.find(query, FailedAccessMongoDoc.class);
    }

    private String toLikeRegex(String source) {
        return source.replaceAll("\\*", ".*");
    }

}
