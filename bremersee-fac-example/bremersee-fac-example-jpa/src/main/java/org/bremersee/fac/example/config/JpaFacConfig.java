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

import org.bremersee.fac.domain.FailedAccessDao;
import org.bremersee.fac.domain.FailedAccessJpaDao;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Christian Bremer <a href="mailto:christian@bremersee.org">christian@bremersee.org</a>
 *
 */
@Configuration
@EntityScan(basePackages = {"org.bremersee.fac.domain"})
public class JpaFacConfig extends AbstractFacConfig {

    /* (non-Javadoc)
     * @see org.bremersee.fac.example.config.AbstractFacConfig#failedAccessDao()
     */
    @Bean
    @Override
    public FailedAccessDao failedAccessDao() {
        FailedAccessJpaDao dao = new FailedAccessJpaDao();
        return dao;
    }

}
