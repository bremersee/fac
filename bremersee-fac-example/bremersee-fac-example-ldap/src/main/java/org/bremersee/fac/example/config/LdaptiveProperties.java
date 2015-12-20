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

import org.ldaptive.pool.PoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Christian Bremer <a href="mailto:christian@bremersee.org">christian@bremersee.org</a>
 *
 */
@ConfigurationProperties(prefix = "bremersee.ldaptive")
public class LdaptiveProperties {
    
    /** URL to the LDAP(s). */
    private String ldapUrl = "ldap://localhost:1389";

    /** Amount of time in milliseconds that connects will block. */
    private long connectTimeout = -1;

    /** Amount of time in milliseconds to wait for responses. */
    private long responseTimeout = -1;

    /** Connect to LDAP using SSL protocol. */
    private boolean useSSL;

    /** Connect to LDAP using startTLS. */
    private boolean useStartTLS;

    
    // X509CredentialConfig, but there may be more to create a org.ldaptive.ssl.CredentialConfig
    /** Name of the trust certificates to use for the SSL connection. */
    private String trustCertificates;
    /** Name of the authentication certificate to use for the SSL connection. */
    private String authenticationCertificate;
    /** Name of the key to use for the SSL connection. */
    private String authenticationKey;


    // BindConnectionInitializer, sasl is not supported at the moment
    /** DN to bind as before performing operations. */
    private String bindDn;
    /** Credential for the bind DN. */
    private String bindCredential;
    //    /** Configuration for bind SASL authentication. */
    //    private SaslConfig bindSaslConfig;

    
    private boolean pooled = false;
            
    /** Minimum pool size. */
    private int minPoolSize = PoolConfig.DEFAULT_MIN_POOL_SIZE;

    /** Maximum pool size. */
    private int maxPoolSize = PoolConfig.DEFAULT_MAX_POOL_SIZE;

    /** Whether the ldap object should be validated when returned to the pool. */
    private boolean validateOnCheckIn = PoolConfig.DEFAULT_VALIDATE_ON_CHECKIN;

    /** Whether the ldap object should be validated when given from the pool. */
    private boolean validateOnCheckOut = PoolConfig.DEFAULT_VALIDATE_ON_CHECKOUT;

    /** Whether the pool should be validated periodically. */
    private boolean validatePeriodically = PoolConfig.DEFAULT_VALIDATE_PERIODICALLY;

    /** Time in seconds that the validate pool should repeat. */
    private long validatePeriod = PoolConfig.DEFAULT_VALIDATE_PERIOD;

    
    /** Prune period in seconds. */
    private long prunePeriod = 300L;

    /** Idle time in seconds. */
    private long idleTime = 600L;


    /** Time in milliseconds to wait for an available connection. */
    private long blockWaitTime = 10000L;
    
    
    public String getLdapUrl() {
        return ldapUrl;
    }

    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public long getResponseTimeout() {
        return responseTimeout;
    }

    public void setResponseTimeout(long responseTimeout) {
        this.responseTimeout = responseTimeout;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public boolean isUseStartTLS() {
        return useStartTLS;
    }

    public void setUseStartTLS(boolean useStartTLS) {
        this.useStartTLS = useStartTLS;
    }

    public String getTrustCertificates() {
        return trustCertificates;
    }

    public void setTrustCertificates(String trustCertificates) {
        this.trustCertificates = trustCertificates;
    }

    public String getAuthenticationCertificate() {
        return authenticationCertificate;
    }

    public void setAuthenticationCertificate(String authenticationCertificate) {
        this.authenticationCertificate = authenticationCertificate;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public void setAuthenticationKey(String authenticationKey) {
        this.authenticationKey = authenticationKey;
    }

    public String getBindDn() {
        return bindDn;
    }

    public void setBindDn(String bindDn) {
        this.bindDn = bindDn;
    }

    public String getBindCredential() {
        return bindCredential;
    }

    public void setBindCredential(String bindCredential) {
        this.bindCredential = bindCredential;
    }

    public boolean isPooled() {
        return pooled;
    }

    public void setPooled(boolean pooled) {
        this.pooled = pooled;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public boolean isValidateOnCheckIn() {
        return validateOnCheckIn;
    }

    public void setValidateOnCheckIn(boolean validateOnCheckIn) {
        this.validateOnCheckIn = validateOnCheckIn;
    }

    public boolean isValidateOnCheckOut() {
        return validateOnCheckOut;
    }

    public void setValidateOnCheckOut(boolean validateOnCheckOut) {
        this.validateOnCheckOut = validateOnCheckOut;
    }

    public boolean isValidatePeriodically() {
        return validatePeriodically;
    }

    public void setValidatePeriodically(boolean validatePeriodically) {
        this.validatePeriodically = validatePeriodically;
    }

    public long getValidatePeriod() {
        return validatePeriod;
    }

    public void setValidatePeriod(long validatePeriod) {
        this.validatePeriod = validatePeriod;
    }

    public long getPrunePeriod() {
        return prunePeriod;
    }

    public void setPrunePeriod(long prunePeriod) {
        this.prunePeriod = prunePeriod;
    }

    public long getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(long idleTime) {
        this.idleTime = idleTime;
    }

    public long getBlockWaitTime() {
        return blockWaitTime;
    }

    public void setBlockWaitTime(long blockWaitTime) {
        this.blockWaitTime = blockWaitTime;
    }

}
