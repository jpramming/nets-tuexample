package dk.certifikat.tuexample.extras;

import dk.certifikat.tuexample.EncryptionHelper;
import dk.certifikat.tuexample.NemIdProperties;
import dk.certifikat.tuexample.OcesEnvironment;
import dk.certifikat.tuexample.extras.util.ConnectivityUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class TestConnectivityServlet extends HttpServlet {
    ResourceBundle resourceBundle = ResourceBundle.getBundle("connectivity");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        updateConfiguration(request, response);
        buildConnectivityTest(request, response, true);

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/extras/connectivity.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        buildConnectivityTest(request, response,false);

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/extras/connectivity.jsp");
        dispatcher.forward(request, response);
    }

    private void updateConfiguration(HttpServletRequest request, HttpServletResponse response) {
        OcesEnvironment.setOcesEnvironment();

        NemIdProperties.setServerUrlPrefix(request.getParameter("serverUrlPrefix"));
        NemIdProperties.setAppletParameterSigningKeystore(request.getParameter("appletParameterSigningKeystore"));
        NemIdProperties.setAppletParameterSigningKeystorePassword(request.getParameter("appletParameterSigningKeystorePassword"));
        NemIdProperties.setAppletParameterSigningKeystoreAlias(request.getParameter("appletParameterSigningKeystoreAlias"));
        NemIdProperties.setAppletParameterSigningKeyPassword(request.getParameter("appletParameterSigningKeyPassword"));
        NemIdProperties.setServiceProviderName(request.getParameter("serviceProviderName"));
        NemIdProperties.setPidServiceTrustKeystore(request.getParameter("pidServiceTrustKeystore"));
        NemIdProperties.setPidServiceTrustKeystorePassword(request.getParameter("pidServiceTrustKeystorePassword"));
        NemIdProperties.setPidServiceSigningKeystore(request.getParameter("pidServiceSigningKeystore"));
        NemIdProperties.setPidServiceSigningKeystorePassword(request.getParameter("pidServiceSigningKeystorePassword"));
        NemIdProperties.setPidServiceSigningKeystoreKeyPassword(request.getParameter("pidServiceSigningKeystoreKeyPassword"));
        NemIdProperties.setPidServiceProviderId(request.getParameter("pidServiceProviderId"));
        NemIdProperties.setRidServiceProviderId(request.getParameter("ridServiceProviderId"));
        NemIdProperties.setCodeFileIframeSrc(request.getParameter("codeFileIframeSrc"));
    }

    private void buildConnectivityTest(HttpServletRequest request, HttpServletResponse response, final boolean runTest) {
        List<NEMIDProperty> verifyParams = new ArrayList<NEMIDProperty>(){{
            add(new NEMIDProperty(){{
                setFieldName("serverUrlPrefix");
                setPropertyKey("nemid.applet.server.url.prefix");
                setPropertyValue(NemIdProperties.getServerUrlPrefix());
                if (runTest && !ConnectivityUtil.verifyServerUrlPrefix()) {
                    addMessage(resourceBundle.getString("verify.server.url.prefix.exists"));
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("appletParameterSigningKeystore");
                setPropertyKey("nemid.applet.parameter.signing.keystore");
                setPropertyValue(NemIdProperties.getAppletParameterSigningKeystore());
                if (runTest && !ConnectivityUtil.verifyExists(NemIdProperties.getAppletParameterSigningKeystore())) {
                    addMessage(resourceBundle.getString("verify.nemid.applet.parameter.signing.keystore.exists"));
                }
                else if (runTest && !ConnectivityUtil.verifySigningCertificate(NemIdProperties.getAppletParameterSigningKeystore(),
                        NemIdProperties.getAppletParameterSigningKeystorePassword(),
                        NemIdProperties.getAppletParameterSigningKeystoreAlias())) {
                    addMessage(resourceBundle.getString("verify.nemid.applet.parameter.signing.keystore.verify"));
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("appletParameterSigningKeystorePassword");
                setPropertyKey("nemid.applet.parameter.signing.keystore.password");
                setPropertyValue(NemIdProperties.getAppletParameterSigningKeystorePassword());
                setPassword(true);
                if (runTest && !ConnectivityUtil.verifyKeystore(NemIdProperties.getAppletParameterSigningKeystore(),
                        NemIdProperties.getAppletParameterSigningKeystorePassword())) {
                    addMessage(resourceBundle.getString("verify.nemid.applet.parameter.signing.keystore.password.verify"));
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("appletParameterSigningKeystoreAlias");
                setPropertyKey("nemid.applet.parameter.signing.keystore.alias");
                setPropertyValue(NemIdProperties.getAppletParameterSigningKeystoreAlias());
                if (runTest && !ConnectivityUtil.verifyKeystoreAlias(NemIdProperties.getAppletParameterSigningKeystore(),
                        NemIdProperties.getAppletParameterSigningKeystorePassword(),
                        NemIdProperties.getAppletParameterSigningKeystoreAlias())) {
                    addMessage(resourceBundle.getString("verify.nemid.applet.parameter.signing.keystore.alias.verify"));
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("appletParameterSigningKeyPassword");
                setPropertyKey("nemid.applet.parameter.signing.keystore.keypassword");
                setPropertyValue(NemIdProperties.getAppletParameterSigningKeyPassword());
                setPassword(true);
                if (runTest && !ConnectivityUtil.verifyKeyPassword(NemIdProperties.getAppletParameterSigningKeystore(),
                        NemIdProperties.getAppletParameterSigningKeystorePassword(),
                        NemIdProperties.getAppletParameterSigningKeystoreAlias(),
                        NemIdProperties.getAppletParameterSigningKeyPassword())) {
                    addMessage(resourceBundle.getString("verify.nemid.applet.parameter.signing.keystore.keypassword.verify"));
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("serviceProviderName");
                setPropertyKey("nemid.serviceprovider.logonto");
                setPropertyValue(NemIdProperties.getServiceProviderName());
                if (runTest && !ConnectivityUtil.verifyServiceProviderName()) {
                    addMessage(resourceBundle.getString("verify.nemid.serviceprovider.logonto.exists"));
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("codeFileIframeSrc");
                setPropertyKey("nemid.codefile.iframe.src.url");
                setPropertyValue(NemIdProperties.getCodeFileIframeSrc());
                if (runTest && !ConnectivityUtil.verifyCodeFileIframeSrc()) {
                    addMessage(resourceBundle.getString("verify.nemid.codefile.iframe.src.url.exists"));
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("oces2Environment");
                setPropertyKey("oces2.environment");
                setPropertyValue(NemIdProperties.getOces2Environment().name());
                setDisabled(true);
                if (runTest && !ConnectivityUtil.verifyEnvironment()) {
                    addMessage(resourceBundle.getString("verify.oces2.environment.valid"));
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("pidServiceTrustKeystore");
                setPropertyKey("nemid.pidservice.trust.keystore");
                setPropertyValue(NemIdProperties.getPidServiceTrustKeystore());
                if (runTest && !ConnectivityUtil.verifyExists(NemIdProperties.getPidServiceTrustKeystore())) {
                    addMessage(resourceBundle.getString("verify.nemid.pidservice.trust.keystore.exists"));
                }

                // TODO: Validate certificates in trust store

            }});
            add(new NEMIDProperty(){{
                setFieldName("pidServiceTrustKeystorePassword");
                setPropertyKey("nemid.pidservice.trust.keystore.password");
                setPropertyValue(NemIdProperties.getPidServiceTrustKeystorePassword());
                if (runTest && !ConnectivityUtil.verifyKeystore(NemIdProperties.getPidServiceTrustKeystore(),
                        NemIdProperties.getPidServiceTrustKeystorePassword())) {
                    addMessage(resourceBundle.getString("verify.nemid.pidservice.trust.keystore.password.valid"));
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("pidServiceSigningKeystore");
                setPropertyKey("nemid.pidservice.signing.keystore");
                setPropertyValue(NemIdProperties.getPidServiceSigningKeystore());
                if (runTest && !ConnectivityUtil.verifyExists(NemIdProperties.getPidServiceSigningKeystore())) {
                    addMessage(resourceBundle.getString("verify.nemid.pidservice.signing.keystore.exists"));
                }
                else if (runTest && !ConnectivityUtil.verifyKeystoreAlias(NemIdProperties.getPidServiceSigningKeystore(),
                        NemIdProperties.getPidServiceSigningKeystorePassword(), ConnectivityUtil.getFirstAlias(NemIdProperties.getPidServiceSigningKeystore(),
                                NemIdProperties.getPidServiceSigningKeystorePassword()))) {
                    addMessage(resourceBundle.getString("verify.nemid.pidservice.signing.keystore.alias.valid"));
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("pidServiceSigningKeystorePassword");
                setPropertyKey("nemid.pidservice.signing.keystore.password");
                setPropertyValue(NemIdProperties.getPidServiceSigningKeystorePassword());
                if (runTest && !ConnectivityUtil.verifyKeystore(NemIdProperties.getPidServiceSigningKeystore(),
                        NemIdProperties.getPidServiceSigningKeystorePassword())) {
                    addMessage(resourceBundle.getString("verify.nemid.pidservice.signing.keystore.password.valid"));
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("pidServiceSigningKeystoreKeyPassword");
                setPropertyKey("nemid.pidservice.signing.keystore.keypassword");
                setPropertyValue(NemIdProperties.getPidServiceSigningKeystoreKeyPassword());
                if (runTest && !ConnectivityUtil.verifyKeyPassword(NemIdProperties.getPidServiceSigningKeystore(),
                        NemIdProperties.getPidServiceSigningKeystorePassword(), ConnectivityUtil.getFirstAlias(NemIdProperties.getPidServiceSigningKeystore(),
                                NemIdProperties.getPidServiceSigningKeystorePassword()), NemIdProperties.getPidServiceSigningKeystoreKeyPassword())) {
                    addMessage(resourceBundle.getString("verify.nemid.pidservice.signing.keystore.keypassword.valid"));
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("pidServiceProviderId");
                setPropertyKey("nemid.pidservice.serviceproviderid");
                setPropertyValue(NemIdProperties.getPidServiceProviderId());
                if (runTest && !ConnectivityUtil.verifyPidService()) {
                    addMessage(resourceBundle.getString("verify.nemid.pidservice.serviceproviderid.exists"));
                }
                else if (runTest && !ConnectivityUtil.verifyPidServiceProviderId()) {
                    addMessage(resourceBundle.getString("verify.nemid.pidservice.serviceproviderid.access"));
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("ridServiceProviderId");
                setPropertyKey("nemid.ridservice.serviceproviderid");
                setPropertyValue(NemIdProperties.getRidServiceProviderId());
                if (runTest && !ConnectivityUtil.verifyRidService()) {
                    addMessage(resourceBundle.getString("verify.nemid.ridservice.serviceproviderid.exists"));
                }
                else if (runTest && !ConnectivityUtil.verifyRidServiceProviderId()) {
                    addMessage(resourceBundle.getString("verify.nemid.ridservice.serviceproviderid.access"));
                }
            }});

        }};
        request.setAttribute("verifyParam", verifyParams);
    }

    /**
     * Used to generate input fields on connectivity.jsp
     */
    public static class NEMIDProperty {
        private List<String> messsages = new ArrayList<>();
        private String fieldName;
        private String propertyKey;
        private String propertyValue;
        private boolean disabled;
        private boolean password;

        public void setFieldName(String fieldName) { this.fieldName = fieldName; }
        public void setPropertyKey(String propertyKey) { this.propertyKey = propertyKey; }
        public void setPropertyValue(String propertyValue) { this.propertyValue = propertyValue; }
        public void setDisabled(boolean disabled) { this.disabled = disabled; }
        public void setPassword(boolean password) { this.password = password; }
        public void addMessage(String message) { this.messsages.add(message); }

        public String getFieldName() { return fieldName; }
        public String getPropertyKey() { return propertyKey; }
        public String getPropertyValue() { return (password)?EncryptionHelper.getInstance().encrypt(propertyValue):propertyValue; }
        public boolean getDisabled() { return disabled; }
        public List<String> getMesssages() { return messsages; }
    }
}