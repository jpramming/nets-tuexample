/**
 * Copyright (c) 2010, DanID A/S
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *  - Neither the name of the DanID A/S nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package dk.certifikat.tuexample;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openoces.ooapi.environment.Environments;

public class NemIdProperties {
    private static final Logger logger = Logger.getLogger(NemIdProperties.class);
    
    private static String serverUrlPrefix;
    private static String appletParameterSigningKeystore;
    private static String appletParameterSigningKeystorePassword;
    private static String appletParameterSigningKeystoreAlias;
    private static String appletParameterSigningKeystoreKeyPassword;
    private static String serviceProviderName;
    private static String pidServiceTrustKeystore;
    private static String pidServiceTrustKeystorePassword;
    private static String pidServiceSigningKeystore;
    private static String pidServiceSigningKeystorePassword;
    private static String pidServiceSigningKeystoreKeyPassword;
    private static String pidServiceProviderId;
    private static String ridServiceProviderId;
    private static String codeFileIframeSrc;
    private static String codeFileLogonJs;
    private static String codeFileSignJs;
    private static String codeFileIframe;
    private static String codeFileParameters;
    private static String codeFileForm;

    private static Environments.Environment oces2Environment;

    private static Properties properties;

    static {
        readProperties();
    }

    private static void readProperties() {
        properties = new Properties();
        try {
            InputStream propertiesAsStream = NemIdProperties.class.getResourceAsStream("/nemid.properties");
            if (propertiesAsStream == null) {
                throw new IllegalStateException("/nemid.properties not found on classpath");
            }
            properties.load(propertiesAsStream);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read property file nemid.properties from classpath", e);
        }

        serverUrlPrefix = getRequiredProperty(properties, "nemid.applet.server.url.prefix", " to the URL of the applet providing server, eg. https://applet.danid.dk");
        appletParameterSigningKeystore = getRequiredProperty(properties, "nemid.applet.parameter.signing.keystore", " to a classpath path to the keystore, eg. /applet-parameter-signing-keystore-cvr30808460-uid1263281782319.jks");
        appletParameterSigningKeystorePassword = EncryptionHelper.getInstance().decrypt(getRequiredProperty(properties, "nemid.applet.parameter.signing.keystore.password", " to the password to the keystore pointed to by nemid.applet.parameter.signing.keystore"));
        appletParameterSigningKeystoreAlias = getRequiredProperty(properties, "nemid.applet.parameter.signing.keystore.alias", " to the alias of the key to be used in the keystore pointed to by nemid.applet.parameter.signing.keystore");
        appletParameterSigningKeystoreKeyPassword = EncryptionHelper.getInstance().decrypt(getRequiredProperty(properties, "nemid.applet.parameter.signing.keystore.keypassword", " to the password for the key pointed to by nemid.applet.parameter.signing.keystore.alias"));
        pidServiceTrustKeystore = getRequiredProperty(properties, "nemid.pidservice.trust.keystore", " to your PID service trust keystore file");
        pidServiceTrustKeystorePassword = getRequiredProperty(properties, "nemid.pidservice.trust.keystore.password", " to your PID service trust keystore file");
        pidServiceSigningKeystore = getRequiredProperty(properties, "nemid.pidservice.signing.keystore", " to your PID service signing keystore file");
        pidServiceSigningKeystorePassword = getRequiredProperty(properties, "nemid.pidservice.signing.keystore.password", " to your PID service signing keystore file password");
        pidServiceSigningKeystoreKeyPassword = getRequiredProperty(properties, "nemid.pidservice.signing.keystore.keypassword", " to your PID service signing key password");
        pidServiceProviderId = getRequiredProperty(properties, "nemid.pidservice.serviceproviderid", " to your PID service provider id");
        ridServiceProviderId = getOptionalProperty(properties, "nemid.ridservice.serviceproviderid", " to your RID service provider id");
        serviceProviderName = getRequiredProperty(properties, "nemid.serviceprovider.logonto", " to you service provider name");
        oces2Environment = getOces2EnvironmentFromProperty(properties, "oces2.environment");
        codeFileIframeSrc = getRequiredProperty(properties, "nemid.codefile.iframe.src.url", " to the url of the CodeFile iframe");
        codeFileLogonJs = getRequiredProperty(properties, "nemid.codefile.logon.js", "");
        codeFileSignJs = getRequiredProperty(properties, "nemid.codefile.sign.js", "");
        codeFileIframe = getRequiredProperty(properties, "nemid.codefile.iframe", "");
        codeFileParameters = getRequiredProperty(properties, "nemid.codefile.parameters", "");
        codeFileForm = getRequiredProperty(properties, "nemid.codefile.form", "");

        logger.debug("serverUrlPrefix: " +serverUrlPrefix);
    }

    private static Environments.Environment getOces2EnvironmentFromProperty(Properties properties, String s) {
        return Environments.Environment.valueOf(getRequiredProperty(properties, s, " to the environment to check nemid environment against, eg. OCESII_DANID_ENV_PROD, " +
                "OCESII_DANID_ENV_PROD or OCESII_DANID_ENV_EXTERNALTEST").toUpperCase());
    }

    private static String getRequiredProperty(Properties properties, String key, String helpMsg) {
        String value = properties.getProperty(key);
        if (value == null || value.length() == 0) {
            throw new IllegalStateException("You must set property " + key + " in nemid.properties" + helpMsg);
        }
        return value;
    }
    
    private static String getOptionalProperty(Properties properties, String key, String helpMsg) {
    	String value = properties.getProperty(key);
    	if(value==null) {
    		return "";
    	}
    	return value;
    }


    public static String getServerUrlPrefix() {
        return serverUrlPrefix;
    }

    public static String getAppletParameterSigningKeystore() {
        return appletParameterSigningKeystore;
    }

    public static String getAppletParameterSigningKeystorePassword() {
        return appletParameterSigningKeystorePassword;
    }

    public static String getAppletParameterSigningKeystoreAlias() {
        return appletParameterSigningKeystoreAlias;
    }

    public static String getAppletParameterSigningKeyPassword() {
        return appletParameterSigningKeystoreKeyPassword;
    }

    public static String getPidServiceTrustKeystore() { return pidServiceTrustKeystore; }

    public static String getPidServiceTrustKeystorePassword() { return pidServiceTrustKeystorePassword; }

    public static String getPidServiceSigningKeystore() { return pidServiceSigningKeystore; }

    public static String getPidServiceSigningKeystorePassword() { return pidServiceSigningKeystorePassword; }

    public static String getPidServiceSigningKeystoreKeyPassword() { return pidServiceSigningKeystoreKeyPassword; }

    public static String getPidServiceProviderId() {
        return pidServiceProviderId;
    }

    public static String getServiceProviderName() {
        return serviceProviderName;
    }
    
    public static String getRidServiceProviderId() {
    	return ridServiceProviderId;
    }

    public static Environments.Environment getOces2Environment() {
        return oces2Environment;
    }

    public static String getCodeFileIframeSrc() { return codeFileIframeSrc; }

    public static String getCodeFileIframe() { return codeFileIframe; }

    public static String getCodeFileForm() { return codeFileForm; }



    public static String getCodeFileIframeOrigin() {
        int idx= codeFileIframeSrc.substring(8).indexOf('/'); // Skip http(s)://
        return (idx!=-1) ? codeFileIframeSrc.substring(0,8+idx) : codeFileIframeSrc;
    }

    public static String getCodeFileLogonJs() { return codeFileLogonJs; }

    public static String getCodeFileSignJs() { return codeFileSignJs; }

    public static String getCodeFileParameters() { return codeFileParameters; }

    public static String propertiesToString() {
        Set<Object> keys = properties.keySet();
        if(keys == null || keys.isEmpty()) {
            return "Properties er ikke blevet sat!";
        }

        String print = "";
        for (Object key : keys) {
            print += key.toString() + "=" + properties.getProperty((String ) key) + "\n";
        }
        return print;
    }

    public static void setServerUrlPrefix(String serverUrlPrefix) {
        NemIdProperties.serverUrlPrefix = serverUrlPrefix;
    }

    public static void setAppletParameterSigningKeystore(String appletParameterSigningKeystore) {
        NemIdProperties.appletParameterSigningKeystore = appletParameterSigningKeystore;
    }

    public static void setAppletParameterSigningKeystorePassword(String appletParameterSigningKeystorePassword) {
        NemIdProperties.appletParameterSigningKeystorePassword = EncryptionHelper.getInstance().decrypt(appletParameterSigningKeystorePassword);
    }

    public static void setAppletParameterSigningKeystoreAlias(String appletParameterSigningKeystoreAlias) {
        NemIdProperties.appletParameterSigningKeystoreAlias = appletParameterSigningKeystoreAlias;
    }

    public static void setAppletParameterSigningKeyPassword(String appletParameterSigningKeyPassword) {
        NemIdProperties.appletParameterSigningKeystoreKeyPassword = EncryptionHelper.getInstance().decrypt(appletParameterSigningKeyPassword);
    }

    public static void setServiceProviderName(String serviceProviderName) {
        NemIdProperties.serviceProviderName = serviceProviderName;//?
    }

    public static void setCodeFileIframeSrc(String codeFileIframeSrc) {
        NemIdProperties.codeFileIframeSrc = codeFileIframeSrc;
    }

    public static void setPidServiceProviderId(String pidServiceProviderId) {
        NemIdProperties.pidServiceProviderId = pidServiceProviderId;
    }

    public static void setRidServiceProviderId(String ridServiceProviderId) {
        NemIdProperties.ridServiceProviderId = ridServiceProviderId;
    }

    public static void setPidServiceTrustKeystore(String pidServiceTrustKeystore) {
        NemIdProperties.pidServiceTrustKeystore = pidServiceTrustKeystore;
    }

    public static void setPidServiceTrustKeystorePassword(String pidServiceTrustKeystorePassword) {
        NemIdProperties.pidServiceTrustKeystorePassword = pidServiceTrustKeystorePassword;
    }

    public static void setPidServiceSigningKeystore(String pidServiceSigningKeystore) {
        NemIdProperties.pidServiceSigningKeystore = pidServiceSigningKeystore;
    }

    public static void setPidServiceSigningKeystorePassword(String pidServiceSigningKeystorePassword) {
        NemIdProperties.pidServiceSigningKeystorePassword = pidServiceSigningKeystorePassword;
    }

    public static void setPidServiceSigningKeystoreKeyPassword(String pidServiceSigningKeystoreKeyPassword) {
        NemIdProperties.pidServiceSigningKeystoreKeyPassword = pidServiceSigningKeystoreKeyPassword;
    }
}
