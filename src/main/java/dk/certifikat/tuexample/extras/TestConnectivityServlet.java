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
                if (runTest) {
                    if (!ConnectivityUtil.verifyServerUrlPrefix()) {
                        addMessage(resourceBundle.getString("verify.server.url.prefix.exists"));
                        setTestResult(TestResult.ERROR);
                    } else {
                        setTestResult(TestResult.OK);
                    }
                } else {
                    setTestResult(TestResult.MISSING);
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("appletParameterSigningKeystore");
                setPropertyKey("nemid.applet.parameter.signing.keystore");
                setPropertyValue(NemIdProperties.getAppletParameterSigningKeystore());
                if (runTest) {
                    if (!ConnectivityUtil.verifyExists(NemIdProperties.getAppletParameterSigningKeystore())) {
                        addMessage(resourceBundle.getString("verify.nemid.applet.parameter.signing.keystore.exists"));
                        setTestResult(TestResult.ERROR);
                    } else {
                        setTestResult(TestResult.OK);
                    }
                } else {
                    setTestResult(TestResult.MISSING);
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("appletParameterSigningKeystorePassword");
                setPropertyKey("nemid.applet.parameter.signing.keystore.password");
                setPropertyValue(NemIdProperties.getAppletParameterSigningKeystorePassword());
                setPassword(true);
                if (runTest) {
                    if (!ConnectivityUtil.verifyKeystore(NemIdProperties.getAppletParameterSigningKeystore(),
                            NemIdProperties.getAppletParameterSigningKeystorePassword())) {
                        addMessage(resourceBundle.getString("verify.nemid.applet.parameter.signing.keystore.password.verify"));
                        setTestResult(TestResult.ERROR);
                    } else {
                        setTestResult(TestResult.OK);
                    }
                } else {
                    setTestResult(TestResult.MISSING);
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("appletParameterSigningKeystoreAlias");
                setPropertyKey("nemid.applet.parameter.signing.keystore.alias");
                setPropertyValue(NemIdProperties.getAppletParameterSigningKeystoreAlias());
                if (runTest) {
                    if (!ConnectivityUtil.verifyKeystoreAlias(NemIdProperties.getAppletParameterSigningKeystore(),
                        NemIdProperties.getAppletParameterSigningKeystorePassword(),
                        NemIdProperties.getAppletParameterSigningKeystoreAlias())) {
                        addMessage(resourceBundle.getString("verify.nemid.applet.parameter.signing.keystore.alias.verify"));
                        setTestResult(TestResult.ERROR);
                    } else if (!ConnectivityUtil.verifySigningCertificate(NemIdProperties.getAppletParameterSigningKeystore(),
                        NemIdProperties.getAppletParameterSigningKeystorePassword(),
                        NemIdProperties.getAppletParameterSigningKeystoreAlias())) {
                        addMessage(resourceBundle.getString("verify.nemid.applet.parameter.signing.keystore.verify"));
                        setTestResult(TestResult.ERROR);
                    } else {
                        setTestResult(TestResult.OK);
                    }
                } else {
                    setTestResult(TestResult.MISSING);
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("appletParameterSigningKeyPassword");
                setPropertyKey("nemid.applet.parameter.signing.keystore.keypassword");
                setPropertyValue(NemIdProperties.getAppletParameterSigningKeyPassword());
                setPassword(true);
                if (runTest) {
                    if (!ConnectivityUtil.verifyKeyPassword(NemIdProperties.getAppletParameterSigningKeystore(),
                            NemIdProperties.getAppletParameterSigningKeystorePassword(),
                            NemIdProperties.getAppletParameterSigningKeystoreAlias(),
                            NemIdProperties.getAppletParameterSigningKeyPassword())) {
                        addMessage(resourceBundle.getString("verify.nemid.applet.parameter.signing.keystore.keypassword.verify"));
                        setTestResult(TestResult.ERROR);
                    } else {
                        setTestResult(TestResult.OK);
                    }
                } else {
                    setTestResult(TestResult.MISSING);
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("serviceProviderName");
                setPropertyKey("nemid.serviceprovider.logonto");
                setPropertyValue(NemIdProperties.getServiceProviderName());
                if (runTest) {
                    if (!ConnectivityUtil.verifyServiceProviderName()) {
                        addMessage(resourceBundle.getString("verify.nemid.serviceprovider.logonto.exists"));
                        setTestResult(TestResult.ERROR);
                    } else {
                        setTestResult(TestResult.OK);
                    }
                } else {
                    setTestResult(TestResult.MISSING);
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("codeFileIframeSrc");
                setPropertyKey("nemid.codefile.iframe.src.url");
                setPropertyValue(NemIdProperties.getCodeFileIframeSrc());
                if (runTest) {
                    if (!ConnectivityUtil.verifyCodeFileIframeSrc()) {
                        addMessage(resourceBundle.getString("verify.nemid.codefile.iframe.src.url.exists"));
                        setTestResult(TestResult.ERROR);
                    } else {
                        setTestResult(TestResult.OK);
                    }
                } else {
                    setTestResult(TestResult.MISSING);
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("oces2Environment");
                setPropertyKey("oces2.environment");
                setPropertyValue(NemIdProperties.getOces2Environment().name());
                setDisabled(true);
                if (runTest) {
                    if (!ConnectivityUtil.verifyEnvironment()) {
                        addMessage(resourceBundle.getString("verify.oces2.environment.valid"));
                        setTestResult(TestResult.ERROR);
                    } else {
                        setTestResult(TestResult.OK);
                    }
                } else {
                    setTestResult(TestResult.MISSING);
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("pidServiceTrustKeystore");
                setPropertyKey("nemid.pidservice.trust.keystore");
                setPropertyValue(NemIdProperties.getPidServiceTrustKeystore());
                setDisabled(true);
                if (runTest) {
                    if (!ConnectivityUtil.verifyExists(NemIdProperties.getPidServiceTrustKeystore())) {
                        addMessage(resourceBundle.getString("verify.nemid.pidservice.trust.keystore.exists"));
                        setTestResult(TestResult.ERROR);
                    } else {
                        setTestResult(TestResult.OK);
                    }
                } else {
                    setTestResult(TestResult.MISSING);
                }

                // TODO: Validate certificates in trust store

            }});
            add(new NEMIDProperty(){{
                setFieldName("pidServiceTrustKeystorePassword");
                setPropertyKey("nemid.pidservice.trust.keystore.password");
                setPropertyValue(NemIdProperties.getPidServiceTrustKeystorePassword());
                setDisabled(true);
                if (runTest) {
                    if (!ConnectivityUtil.verifyKeystore(NemIdProperties.getPidServiceTrustKeystore(),
                            NemIdProperties.getPidServiceTrustKeystorePassword())) {
                        addMessage(resourceBundle.getString("verify.nemid.pidservice.trust.keystore.password.valid"));
                        setTestResult(TestResult.ERROR);
                    } else {
                        setTestResult(TestResult.OK);
                    }
                } else {
                    setTestResult(TestResult.MISSING);
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("pidServiceSigningKeystore");
                setPropertyKey("nemid.pidservice.signing.keystore");
                setPropertyValue(NemIdProperties.getPidServiceSigningKeystore());
                setDisabled(true);
                if (runTest) {
                    if (!ConnectivityUtil.verifyExists(NemIdProperties.getPidServiceSigningKeystore())) {
                        addMessage(resourceBundle.getString("verify.nemid.pidservice.signing.keystore.exists"));
                        setTestResult(TestResult.ERROR);
                    } else if (!ConnectivityUtil.verifyKeystoreAlias(NemIdProperties.getPidServiceSigningKeystore(),
                            NemIdProperties.getPidServiceSigningKeystorePassword(), ConnectivityUtil.getFirstAlias(NemIdProperties.getPidServiceSigningKeystore(),
                                    NemIdProperties.getPidServiceSigningKeystorePassword()))) {
                        addMessage(resourceBundle.getString("verify.nemid.pidservice.signing.keystore.alias.valid"));
                        setTestResult(TestResult.ERROR);
                    } else {
                        setTestResult(TestResult.OK);
                    }
                } else {
                    setTestResult(TestResult.MISSING);
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("pidServiceSigningKeystorePassword");
                setPropertyKey("nemid.pidservice.signing.keystore.password");
                setPropertyValue(NemIdProperties.getPidServiceSigningKeystorePassword());
                setDisabled(true);
                if (runTest) {
                    if (!ConnectivityUtil.verifyKeystore(NemIdProperties.getPidServiceSigningKeystore(),
                            NemIdProperties.getPidServiceSigningKeystorePassword())) {
                        addMessage(resourceBundle.getString("verify.nemid.pidservice.signing.keystore.password.valid"));
                        setTestResult(TestResult.ERROR);
                    } else {
                        setTestResult(TestResult.OK);
                    }
                } else {
                    setTestResult(TestResult.MISSING);
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("pidServiceSigningKeystoreKeyPassword");
                setPropertyKey("nemid.pidservice.signing.keystore.keypassword");
                setPropertyValue(NemIdProperties.getPidServiceSigningKeystoreKeyPassword());
                setDisabled(true);
                if (runTest) {
                    if (!ConnectivityUtil.verifyKeyPassword(NemIdProperties.getPidServiceSigningKeystore(),
                            NemIdProperties.getPidServiceSigningKeystorePassword(), ConnectivityUtil.getFirstAlias(NemIdProperties.getPidServiceSigningKeystore(),
                                    NemIdProperties.getPidServiceSigningKeystorePassword()), NemIdProperties.getPidServiceSigningKeystoreKeyPassword())) {
                        addMessage(resourceBundle.getString("verify.nemid.pidservice.signing.keystore.keypassword.valid"));
                        setTestResult(TestResult.ERROR);
                    } else {
                        setTestResult(TestResult.OK);
                    }
                } else {
                    setTestResult(TestResult.MISSING);
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("pidServiceProviderId");
                setPropertyKey("nemid.pidservice.serviceproviderid");
                setPropertyValue(NemIdProperties.getPidServiceProviderId());
                if (runTest) {
                    if (!ConnectivityUtil.verifyPidService()) {
                        addMessage(resourceBundle.getString("verify.nemid.pidservice.serviceproviderid.exists"));
                        setTestResult(TestResult.ERROR);
                    } else if (!ConnectivityUtil.verifyPidServiceProviderId()) {
                        addMessage(resourceBundle.getString("verify.nemid.pidservice.serviceproviderid.access"));
                        setTestResult(TestResult.ERROR);
                    } else {
                        setTestResult(TestResult.OK);
                    }
                } else {
                    setTestResult(TestResult.MISSING);
                }
            }});
            add(new NEMIDProperty(){{
                setFieldName("ridServiceProviderId");
                setPropertyKey("nemid.ridservice.serviceproviderid");
                setPropertyValue(NemIdProperties.getRidServiceProviderId());
                if (runTest) {
                    if (!ConnectivityUtil.verifyRidService()) {
                        addMessage(resourceBundle.getString("verify.nemid.ridservice.serviceproviderid.exists"));
                        setTestResult(TestResult.ERROR);
                    } else if (!ConnectivityUtil.verifyRidServiceProviderId()) {
                        addMessage(resourceBundle.getString("verify.nemid.ridservice.serviceproviderid.access"));
                        setTestResult(TestResult.ERROR);
                    } else {
                        setTestResult(TestResult.OK);
                    }
                } else {
                    setTestResult(TestResult.MISSING);
                }
            }});

        }};
        request.setAttribute("verifyParam", verifyParams);
        request.setAttribute("runTest", runTest);
    }

    /**
     * Enum for test status
     */
    public enum TestResult {
        OK("icon_happy.png"),
        ERROR("icon_sad.png"),
        MISSING("icon_gray.png");

        String icon;

        private TestResult(String icon) {
            this.icon = icon;
        }

        public String getIcon() { return icon; }
    }

    /**
     * Used to generate input fields on connectivity.jsp
     */
    public static class NEMIDProperty {
        private List<String> messsages = new ArrayList<>();
        private String fieldName;
        private String propertyKey;
        private String propertyValue;
        private TestResult testResult = TestResult.MISSING;
        private boolean disabled;
        private boolean password;

        public void setFieldName(String fieldName) { this.fieldName = fieldName; }
        public void setPropertyKey(String propertyKey) { this.propertyKey = propertyKey; }
        public void setPropertyValue(String propertyValue) { this.propertyValue = propertyValue; }
        public void setDisabled(boolean disabled) { this.disabled = disabled; }
        public void setPassword(boolean password) { this.password = password; }
        public void setTestResult(TestResult testResult) { this.testResult = testResult; }
        public void addMessage(String message) { this.messsages.add(message); }

        public String getFieldName() { return fieldName; }
        public String getPropertyKey() { return propertyKey; }
        public String getPropertyValue() { return (password)?EncryptionHelper.getInstance().encrypt(propertyValue):propertyValue; }
        public String getTestImage() { return testResult.getIcon(); }
        public boolean getDisabled() { return disabled; }
        public List<String> getMesssages() { return messsages; }
    }
}