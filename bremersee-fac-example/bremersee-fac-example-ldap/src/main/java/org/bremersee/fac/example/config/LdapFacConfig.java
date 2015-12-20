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
import org.bremersee.fac.domain.FailedAccessDefaultLdapMapper;
import org.bremersee.fac.domain.FailedAccessLdapDao;
import org.bremersee.fac.domain.FailedAccessLdapMapper;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Christian Bremer <a href="mailto:christian@bremersee.org">christian@bremersee.org</a>
 *
 */
@Configuration
public class LdapFacConfig extends AbstractFacConfig {
    
    @Autowired
    private ConnectionFactory connectionFactory;

    /* (non-Javadoc)
     * @see org.bremersee.fac.example.config.AbstractFacConfig#failedAccessDao()
     */
    @Bean
    @Override
    public FailedAccessDao failedAccessDao() {
        FailedAccessLdapDao dao = new FailedAccessLdapDao();
        dao.setFailedAccessLdapMapper(failedAccessLdapMapper());
        dao.setSearchRequest(searchRequest());
        dao.setConnectionFactory(connectionFactory);
        return dao;
    }
    
    @Bean
    public FailedAccessLdapMapper failedAccessLdapMapper() {
        FailedAccessDefaultLdapMapper mapper = new FailedAccessDefaultLdapMapper();
        return mapper;
    }

    @Bean
    public SearchRequest searchRequest() {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setBaseDn("ou=Access Failed Entries,dc=example,dc=org");
        return searchRequest;
    }
    
}
