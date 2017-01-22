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

package org.bremersee.fac.example.config;

import org.bremersee.comparator.ComparatorItemTransformer;
import org.bremersee.comparator.ComparatorItemTransformerImpl;
import org.bremersee.comparator.ObjectComparatorFactory;
import org.bremersee.fac.FailedAccessCounter;
import org.bremersee.fac.FailedAccessCounterImpl;
import org.bremersee.fac.domain.FailedAccessDao;
import org.bremersee.pagebuilder.PageBuilder;
import org.bremersee.pagebuilder.PageBuilderImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author Christian Bremer
 */
public abstract class AbstractFacConfig { // NOSONAR

    public abstract FailedAccessDao failedAccessDao();

    @Bean
    @ConfigurationProperties(prefix = "failedAccessCounter")
    public FailedAccessCounter failedAccessCounter() {
        FailedAccessCounterImpl fac = new FailedAccessCounterImpl();
        fac.setFailedAccessDao(failedAccessDao());
        return fac;
    }

    @Bean
    public ObjectComparatorFactory objectComparatorFactory() {
        return ObjectComparatorFactory.newInstance();
    }

    @Bean
    public PageBuilder pageBuilder() {
        return new PageBuilderImpl();
    }

    @Bean
    public ComparatorItemTransformer comparatorItemTransformer() {
        return new ComparatorItemTransformerImpl();
    }

}
