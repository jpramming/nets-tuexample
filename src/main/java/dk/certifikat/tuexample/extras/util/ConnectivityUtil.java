package dk.certifikat.tuexample.extras.util;

import dk.certifikat.tuexample.NemIdProperties;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.openoces.ooapi.certificate.OcesCertificate;
import org.openoces.ooapi.certificate.OcesCertificateFactory;
import org.openoces.ooapi.config.OOAPIConfiguration;
import org.openoces.ooapi.environment.RootCertificates;
import org.openoces.ooapi.pidservice.generated.ArrayOfPIDRequest;
import org.openoces.ooapi.pidservice.generated.PIDReply;
import org.openoces.ooapi.service.PidServiceProviderClient;
import org.openoces.ooapi.service.RidOIOServiceProviderClient;
import org.openoces.ooapi.service.ServiceProviderClient;
import org.openoces.ooapi.service.impl.PidServiceProviderClientImpl;
import org.openoces.serviceprovider.ConfigurationChecker;
import org.openoces.serviceprovider.ServiceProviderSetup;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

public class ConnectivityUtil {

    /**
     * Verificer service provider name
     * @return true if test succeeds
     */
    public static boolean verifyServiceProviderName() {
        // TODO: How to check if this provider is correct

        return true;
    }

    /**
     * Call ping service on CodeFileIframeSrc server
     * @return true if test succeeds
     */
    public static boolean verifyCodeFileIframeSrc() {
        try {
            // TODO: How to check if this host is correct

            String url = NemIdProperties.getCodeFileIframeSrc();
            HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
            return 200 == httpClient.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Call ping service on ibss server
     * @return true if test succeeds
     */
    public static boolean verifyServerUrlPrefix() {
        try {
            // TODO: How to check if this host is correct

            String url = NemIdProperties.getServerUrlPrefix() + "/ping";
            HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
            return 200 == httpClient.getResponseCode() && "OK".equals(httpClient.getResponseMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * verify that keystore contain a key having alias, and that it can be accessed
     * @return true if test succeeds
     */
    public static boolean verifyKeyPassword(String keystorePath, String keystorePwd, String keystoreKeyAlias, String keystoreKeyPwd) {
        try {
            return null != ConnectivityUtil.loadSigningKey(keystorePath, keystorePwd, keystoreKeyAlias, keystoreKeyPwd);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verify that keystore contain a certificate having alias
     * @return true if test succeeds
     */
    public static boolean verifyKeystoreAlias(String keystorePath, String keystorePwd, String keystoreKeyAlias) {
        try {
            return null != ConnectivityUtil.loadSigningCertificate(keystorePath, keystorePwd, keystoreKeyAlias);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verify that keystore could be opened / accessed
     * @return true if test succeeds
     */
    public static boolean verifyKeystore(String keystorePath, String keystorePwd) {
        try {
            return null != ConnectivityUtil.loadSigningKeystore(keystorePath, keystorePwd);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verify that resource exists
     * @param resourceInput
     * @return true if resource exists
     */
    public static boolean verifyExists(String resourceInput) {
        try (InputStream resource = ConnectivityUtil.class.getClassLoader().getResourceAsStream(resourceInput)) {
            return null != resource;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verify that environment from nemid configuration exists in the signing certificate chain
     * @return true if test succeeds
     */
    public static boolean verifyEnvironment() {
        try {
            X509Certificate rootCert = RootCertificates.lookupCertificate(NemIdProperties.getOces2Environment());

            List<X509Certificate> chain = ConnectivityUtil.loadCertificateChain(
                    NemIdProperties.getAppletParameterSigningKeystore(),
                    NemIdProperties.getAppletParameterSigningKeystorePassword(),
                    NemIdProperties.getAppletParameterSigningKeystoreAlias());
            if (null != chain && null != rootCert) {
                for (X509Certificate certificate : chain) {
                    if (rootCert.getSubjectDN().equals(certificate.getSubjectDN())) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * verify PID service exists
     * @return true if test succeeds
     */
    public static boolean verifyPidService() {
        try {
            ServiceProviderClient serviceProviderClient = (ServiceProviderClient) ServiceProviderSetup.getPidServiceProviderClient(NemIdProperties.getOces2Environment());
            if (null != serviceProviderClient) {
                serviceProviderClient.test();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verify RID service exists
     * @return true if test succeeds
     */
    public static boolean verifyRidService() {
        try {
            ServiceProviderClient serviceProviderClient = (ServiceProviderClient) ServiceProviderSetup.getRidServiceProviderClient(NemIdProperties.getOces2Environment());
            if (null != serviceProviderClient) {
                serviceProviderClient.test();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verify access to PID service with SID
     * @return true if test succeeds
     */
    public static boolean verifyPidServiceProviderId() {
        try {
            PidServiceProviderClient serviceProviderClient = ServiceProviderSetup.getPidServiceProviderClient(NemIdProperties.getOces2Environment());
            if (null != serviceProviderClient) {
                serviceProviderClient.match("1111111118","9208-2002-2-143168190719", NemIdProperties.getPidServiceProviderId(), null);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verify access to RID service with SID
     * @return true if test succeeds
     */
    public static boolean verifyRidServiceProviderId() {
        try {
            RidOIOServiceProviderClient serviceProviderClient = ServiceProviderSetup.getRidServiceProviderClient(NemIdProperties.getOces2Environment());
            if (null != serviceProviderClient) {
                serviceProviderClient.matchRidAndCpr("1111111118","9208-2002-2-143168190719", NumberUtils.toLong(NemIdProperties.getRidServiceProviderId()), null);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verify that signing certificate is a valid certificate
     * @return true if test succeeds
     */
    public static boolean verifySigningCertificate(String keystorePath, String keystorePwd, String keystoreKeyAlias) {
        try {
            Certificate certificate = ConnectivityUtil.loadSigningCertificate(keystorePath, keystorePwd, keystoreKeyAlias);
            if (certificate instanceof X509Certificate) {
                X509Certificate x509Certificate = (X509Certificate) certificate;
                Calendar start = DateUtils.toCalendar(x509Certificate.getNotBefore());
                Calendar stop = DateUtils.toCalendar(x509Certificate.getNotAfter());
                Calendar now = Calendar.getInstance();
                return now.after(start) && now.before(stop);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Open SSL connection to host using keystore
     * @return return true if connection succeeded
     */
    public static boolean verifyPokeCertificateHost(String keystorePath, String keystorePwd, String urlString) {
        Properties systemProps = System.getProperties();
        systemProps.put( "javax.net.ssl.trustStore", keystorePath);
        systemProps.put( "javax.net.ssl.trustStorePassword", keystorePwd);
        System.setProperties(systemProps);
        return verifyPokeCertificateHost(urlString, systemProps);
    }

    /**** Helper functions below ****/

    /**
     * Get alias of first entry in keystore
     * @param keystorePath
     * @param keystorePwd
     * @return
     * @throws Exception IOException, NoSuchAlgorithmException, CertificateException or KeyStoreException
     */
    public static String getFirstAlias(String keystorePath, String keystorePwd) {
        try {
            KeyStore keyStore = loadSigningKeystore(keystorePath, keystorePwd);
            return keyStore.aliases().nextElement();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Open SSL connection to host using keystore
     * @return return true if connection succeeded
     */
    private static boolean verifyPokeCertificateHost(String urlString, Properties systemProps) {
        try {
            URL url = new URL(urlString);
            String host=url.getHost();
            int port=(url.getPort()>0)?url.getPort():"http".equalsIgnoreCase(url.getProtocol())?80:443;

            SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try(Socket s = ssf.createSocket(host, port);
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));) {
                String x = in.readLine();
                System.out.println(x);
                return true;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Load signing keystore from nemid configuration
     * @param keystorePath
     * @param keystorePwd
     * @return
     * @throws Exception IOException, NoSuchAlgorithmException, CertificateException or KeyStoreException
     */
    private static KeyStore loadSigningKeystore(String keystorePath, String keystorePwd) throws Exception {
        try (InputStream resource = ConnectivityUtil.class.getClassLoader().getResourceAsStream(keystorePath)) {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(resource, keystorePwd.toCharArray());
            return keyStore;
        }
    }

    /**
     * Load signing key from nemid configuration
     * @param keystorePath
     * @param keystorePwd
     * @param keystoreKeyAlias
     * @param keystoreKeyPwd
     * @return
     * @throws Exception IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException or UnrecoverableKeyException
     */
    private static Key loadSigningKey(String keystorePath, String keystorePwd, String keystoreKeyAlias, String keystoreKeyPwd) throws Exception {
        KeyStore keyStore = loadSigningKeystore(keystorePath, keystorePwd);
        return keyStore.getKey(keystoreKeyAlias, keystoreKeyPwd.toCharArray());
    }

    /**
     * Load signing certificate from nemid configuration
     * @param keystorePath
     * @param keystorePwd
     * @param keystoreKeyAlias
     * @return
     * @throws Exception IOException, NoSuchAlgorithmException, CertificateException or KeyStoreException
     */
    private static Certificate loadSigningCertificate(String keystorePath, String keystorePwd, String keystoreKeyAlias) throws Exception {
        KeyStore keyStore = loadSigningKeystore(keystorePath, keystorePwd);
        return keyStore.getCertificate(keystoreKeyAlias);
    }

    /**
     * Load signing certificate chain from nemid configuration
     * @param keystorePath
     * @param keystorePwd
     * @param keystoreKeyAlias
     * @return
     * @throws Exception IOException, NoSuchAlgorithmException, CertificateException or KeyStoreException
     */
    private static List<X509Certificate> loadCertificateChain(String keystorePath, String keystorePwd, String keystoreKeyAlias) throws Exception {
        KeyStore keyStore = loadSigningKeystore(keystorePath, keystorePwd);
        List<X509Certificate> chain = new ArrayList<>();
        if (keyStore.containsAlias(keystoreKeyAlias)) {
            for (Certificate certificate : keyStore.getCertificateChain(keystoreKeyAlias)) {
                if (certificate instanceof X509Certificate) {
                    chain.add((X509Certificate) certificate);
                }
            }
        }
        return chain;
    }

    /**
     * Create OcesCertificate from nemid configuration
     * @param keystorePath
     * @param keystorePwd
     * @param keystoreKeyAlias
     * @return
     * @throws Exception IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException or TrustCouldNotBeVerifiedException
     */
    private static OcesCertificate loadOcesCertificate(String keystorePath, String keystorePwd, String keystoreKeyAlias) throws Exception {
        List<X509Certificate> chain = loadCertificateChain(keystorePath, keystorePwd, keystoreKeyAlias);
        return OcesCertificateFactory.getInstance().generate(chain);
    }

}
