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

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openoces.ooapi.utils.Base64Handler;
import org.openoces.ooapi.web.Signer;

import dk.certifikat.tuexample.extras.ClientSizeSelectorServlet;
import dk.certifikat.tuexample.extras.NmasSetupServlet;
import dk.certifikat.tuexample.extras.RememberUserIdSelectorServlet;

public class OtpClientGenerator {
    private static final Logger log = Logger.getLogger(OtpClientGenerator.class);
    private Signer signer;
    private List<Parameter> params = new ArrayList<Parameter>();

    public OtpClientGenerator(HttpServletRequest request, ClientFlow clientFlow) {

        String url = NemIdProperties.getAppletParameterSigningKeystore();
        String keyStorePw = NemIdProperties.getAppletParameterSigningKeystorePassword();
        String alias = NemIdProperties.getAppletParameterSigningKeystoreAlias();
        String keyPw = NemIdProperties.getAppletParameterSigningKeyPassword();

        signer = new Signer(url, keyStorePw, alias, keyPw);

        try {
            setClientFlow(clientFlow, request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to set client flow: " + clientFlow);
        }
    }

    private void setClientFlow(ClientFlow clientFlow, HttpServletRequest request) throws Exception {

        HttpSession session = request.getSession();

        params.add(new ParameterImpl("TIMESTAMP", getTimestamp(), true));
        params.add(new ParameterImpl("SP_CERT", signer.getCertificate()));
        params.add(new ParameterImpl("SIGN_PROPERTIES", "challenge=" + ChallengeGenerator.generateChallenge(request.getSession())));
        params.add(new ParameterImpl("ORIGIN", AuthClientUtils.generateOrigin(request), false));

        //CLIENT_MODE is deprecated and ignored by NemID

        ClientSizeSelectorServlet.setSize(session);

        //request.getSession().setAttribute(ChallengeGenerator.CHALLENGE_SESSION_KEY, millis + "");

        //Add REMEMBER_USERID parameter if rememberUserId has been enabled by the site configuration settings
        String rememberUserIdActiveSetting = (String) session.getAttribute(RememberUserIdSelectorServlet.SESS_REMEMBER_USER_ID);
        if (rememberUserIdActiveSetting == null || rememberUserIdActiveSetting.equals("true")){
            String token = "";
            for (Cookie cookie : request.getCookies()) {
                if ("rememberUserId".equals(cookie.getName()) && !"dead".equals(cookie.getValue())) {
                    token = cookie.getValue();
                    log.debug("token found:"+cookie.getValue());
                    break;
                }
            }
            params.add(new ParameterImpl("REMEMBER_USERID", token));

            if (Boolean.FALSE == session.getAttribute(RememberUserIdSelectorServlet.SESS_REMEMBER_USER_ID_CHECKED)) {
                params.add(new ParameterImpl("REMEMBER_USERID_INITIAL_STATUS", "false"));
            } else {

                // IGNORE
                // default for IBSS 5.4.0+ and earlier version does not have this, so not setting is better
                // params.add(new ParameterImpl("REMEMBER_USERID_INITIAL_STATUS", "true"));

            }
        }
        if (null != session.getAttribute(NmasSetupServlet.SESS_TRANSACTIONCONTEXT)) {
	        String transactionContext = new String( (byte[]) session.getAttribute(NmasSetupServlet.SESS_TRANSACTIONCONTEXT), "UTF-8");
	        if (StringUtils.isNotEmpty(transactionContext)) {
	        	params.add(new ParameterImpl("TRANSACTION_CONTEXT", transactionContext));
	        }
        }
        Boolean suppressPush = (Boolean) session.getAttribute(NmasSetupServlet.SESS_SUPPRESS_PUSH_TO_DEVICE);
        if (null != suppressPush) {
        	params.add(new ParameterImpl("SUPPRESS_PUSH_TO_DEVICE", suppressPush ? "true" : "false"));
        }
        Boolean enableApprovalEvent = (Boolean) session.getAttribute(NmasSetupServlet.SESS_ENABLE_AWAITING_APP_APPROVAL_EVENT);
        if (null != enableApprovalEvent) {
        	params.add(new ParameterImpl("ENABLE_AWAITING_APP_APPROVAL_EVENT", enableApprovalEvent ? "true" : "false"));
        }

        switch (clientFlow) {
            case OCESLOGIN2:
                params.add(new ParameterImpl("CLIENTFLOW", ClientFlow.OCESLOGIN2.toString()));
                break;

            case OCESSIGN2:
                params.add(new ParameterImpl("CLIENTFLOW", ClientFlow.OCESSIGN2.toString()));
                String signTextFormat = (String) session.getAttribute(AbstractSignServlet.SIGN_TEXT_FORMAT);

                if (signTextFormat == null || signTextFormat.equals("")) {
                    signTextFormat = "html";

                    // to be used later on by AbstractSignServlet for transaction validation
                    session.setAttribute(AbstractSignServlet.SIGN_TEXT_FORMAT, "html");
                }

                if (signTextFormat.equals("html")) {
                    params.add(new ParameterImpl("signtext_format", "HTML"));
                    params.add(new ParameterImpl("signtext", SignConstants.SIGN_HTML, true));

                    // to be used by AbstractSignServlet for transaction validation
                    session.setAttribute(AbstractSignServlet.SIGN_TEXT, SignConstants.SIGN_HTML);

                } else if (signTextFormat.equals("txt")) {
                    params.add(new ParameterImpl("signtext_format", "TEXT"));
                    params.add(new ParameterImpl("signtext", SignConstants.SIGN_TEXT, true));

                    // to be used by AbstractSignServlet for transaction validation
                    session.setAttribute(AbstractSignServlet.SIGN_TEXT, SignConstants.SIGN_TEXT);

                } else if (signTextFormat.equals("xml")) {
                    params.add(new ParameterImpl("signtext_format", "XML"));
                    params.add(new ParameterImpl("signtext", SignConstants.SIGN2_XML, true));
                    params.add(new ParameterImpl("SIGNTEXT_TRANSFORMATION", SignConstants.SIGN2_XSLT, true));

                    // to be used by AbstractSignServlet for transaction validation
                    session.setAttribute(AbstractSignServlet.SIGN_TEXT, SignConstants.SIGN2_XML);
                    session.setAttribute(AbstractSignServlet.SIGN_TEXT_TRANSFORMATION, Base64Handler.encode(SignConstants.SIGN2_XSLT));

                } else if (signTextFormat.equals("pdf")) {
                    String demoPdf = new Util().getDocumentAsBase64String(request.getSession().getServletContext(), SignConstants.PDF_URI_NEMID_TERMER);
                    params.add(new ParameterImpl("signtext", demoPdf));
                    params.add(new ParameterImpl("signtext_format", "PDF"));

                    // to be used by AbstractSignServlet for transaction validation
                    session.setAttribute(AbstractSignServlet.SIGN_TEXT, demoPdf);

                } else if (signTextFormat.equalsIgnoreCase("pdf_w_attachment")) {
                    params.add(new ParameterImpl("signtext_format", "PDF"));
                    String signtextUri = NemIdProperties.getServerUrlPrefix() + "/developers/resources/sign/nemid_termer.pdf";

                    params.add(new ParameterImpl("Signtext_Uri", signtextUri));
                    params.add(new ParameterImpl("Signtext_Remote_Hash", "WSfjk6uizle1F3SDIQvmyqOlHBolXbk58fYLQTbgzzk="));

                    // to be used by AbstractSignServlet for transaction validation
                    session.setAttribute(AbstractSignServlet.SIGN_TEXT_URI, signtextUri);
                }

                break;
            default:
                throw new IllegalStateException(clientFlow + " is not supported yet");
        }

        String normalizedParameters = ParameterImpl.normalize(params);
        params.add(new ParameterImpl("PARAMS_DIGEST", ParameterImpl.computeDigest(normalizedParameters)));
        params.add(new ParameterImpl("DIGEST_SIGNATURE", computeSignature(normalizedParameters)));
    }

    public String getJSElement() {
        StringBuffer sb = new StringBuffer();
        sb.append("<script type=\"text/x-nemid\" id=\"nemid_parameters\">\r\n");
        sb.append(ParameterImpl.toJson(params).toString()).append("\r\n");
        sb.append("</script>\r\n");
        return sb.toString();
    }

    private String computeSignature(String normalizedParameters) {
        byte[] signedBytes = signer.calculateSignature(normalizedParameters.getBytes());
        return new String(Base64.encodeBase64(signedBytes));
    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        long millis = System.currentTimeMillis();
        return sdf.format(new Date(millis));
    }

    private String getRemoteResourceUrl(HttpServletRequest request, String pathToFile){
        int port = request.getServerPort();

        if (request.getScheme().equals("http") && port == 80) {
            port = -1;
        } else if (request.getScheme().equals("https") && port == 443) {
            port = -1;
        }
        try {
            URL url = new URL(request.getScheme(), request.getServerName(), port, request.getContextPath() + pathToFile);
            return url.toExternalForm();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
