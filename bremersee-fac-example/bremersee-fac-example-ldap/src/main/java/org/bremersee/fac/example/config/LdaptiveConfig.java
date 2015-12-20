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

import org.apache.commons.lang3.StringUtils;
import org.ldaptive.BindConnectionInitializer;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.ConnectionInitializer;
import org.ldaptive.Credential;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.pool.BlockingConnectionPool;
import org.ldaptive.pool.ConnectionPool;
import org.ldaptive.pool.IdlePruneStrategy;
import org.ldaptive.pool.PoolConfig;
import org.ldaptive.pool.PooledConnectionFactory;
import org.ldaptive.pool.PruneStrategy;
import org.ldaptive.pool.SearchValidator;
import org.ldaptive.provider.Provider;
import org.ldaptive.provider.unboundid.UnboundIDProvider;
import org.ldaptive.ssl.CredentialConfig;
import org.ldaptive.ssl.SslConfig;
import org.ldaptive.ssl.X509CredentialConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Christian Bremer <a href="mailto:christian@bremersee.org">christian@bremersee.org</a>
 *
 */
@Configuration
@EnableConfigurationProperties(LdaptiveProperties.class)
public class LdaptiveConfig {
    
    @Autowired
    private LdaptiveProperties properties;
    
    @Autowired(required = false)
    @Qualifier("inMemoryDirectoryServerBean")
    private Object inMemoryDirectoryServerBean;

    @Bean
    public ConnectionFactory connectionFactory() {
        
        if (inMemoryDirectoryServerBean != null) {
            return defaultConnectionFactory(new UnboundIDProvider());
        }
        
        if (properties.isPooled()) {
            return pooledConnectionFactory(null);
        }
        return defaultConnectionFactory(null);
    }

    private DefaultConnectionFactory defaultConnectionFactory(Provider<?> provider) {
        DefaultConnectionFactory factory = new DefaultConnectionFactory();
        factory.setConnectionConfig(connectionConfig());
        if (provider != null) {
            factory.setProvider(provider);
        }
        return factory;
    }
    
    private ConnectionConfig connectionConfig() {
        ConnectionConfig cc = new ConnectionConfig();
        cc.setLdapUrl(properties.getLdapUrl());
        cc.setConnectTimeout(properties.getConnectTimeout());
        cc.setResponseTimeout(properties.getResponseTimeout());
        cc.setUseSSL(properties.isUseSSL());
        cc.setUseStartTLS(properties.isUseStartTLS());
        
        if (properties.isUseSSL() || properties.isUseStartTLS()) {
            cc.setSslConfig(sslConfig());
        }
        
        if (StringUtils.isNotBlank(properties.getBindDn())) {
            cc.setConnectionInitializer(connectionInitializer());
        }
        
        return cc;
    }
    
    private SslConfig sslConfig() {
        SslConfig sc = new SslConfig();
        sc.setCredentialConfig(sslCredentialConfig());
        // there may be other ways
        //        sc.setEnabledCipherSuites(suites);
        //        sc.setEnabledProtocols(protocols);
        //        sc.setHandshakeCompletedListeners(listeners);
        //        sc.setTrustManagers(managers);
        return sc;
    }
    
    private CredentialConfig sslCredentialConfig() {
        // there may be other ways
        X509CredentialConfig x509 = new X509CredentialConfig();
        x509.setAuthenticationCertificate(properties.getAuthenticationCertificate());
        x509.setAuthenticationKey(properties.getAuthenticationKey());
        x509.setTrustCertificates(properties.getTrustCertificates());
        return x509;
    }
    
    private ConnectionInitializer connectionInitializer() {
        // sasl is not supported at the moment
        BindConnectionInitializer bci = new BindConnectionInitializer();
        bci.setBindDn(properties.getBindDn());
        bci.setBindCredential(new Credential(properties.getBindCredential()));
        return bci;
    }
    

    private PooledConnectionFactory pooledConnectionFactory(Provider<?> provider) {
        PooledConnectionFactory factory = new PooledConnectionFactory();
        factory.setConnectionPool(connectionPool(provider));
        return factory;
    }
    
    private ConnectionPool connectionPool(Provider<?> provider) {
        BlockingConnectionPool pool = new BlockingConnectionPool();
        pool.setConnectionFactory(defaultConnectionFactory(provider));
        pool.setPoolConfig(poolConfig());
        pool.setPruneStrategy(pruneStrategy());
        pool.setValidator(searchValidator());
        pool.setBlockWaitTime(properties.getBlockWaitTime());
        pool.initialize();
        return pool;
    }
    
    private PoolConfig poolConfig() {
        PoolConfig pc = new PoolConfig();
        pc.setMaxPoolSize(properties.getMaxPoolSize());
        pc.setMinPoolSize(properties.getMinPoolSize());
        pc.setValidateOnCheckIn(properties.isValidateOnCheckIn());
        pc.setValidateOnCheckOut(properties.isValidateOnCheckOut());
        pc.setValidatePeriod(properties.getValidatePeriod());
        pc.setValidatePeriodically(properties.isValidatePeriodically());
        return pc;
    }
    
    private PruneStrategy pruneStrategy() {
        // there may be other ways
        IdlePruneStrategy ips = new IdlePruneStrategy();
        ips.setIdleTime(properties.getIdleTime());
        ips.setPrunePeriod(properties.getPrunePeriod());
        return ips;
    }
    
    private SearchValidator searchValidator() {
        SearchValidator sv = new SearchValidator();
        return sv;
    }
    
}
