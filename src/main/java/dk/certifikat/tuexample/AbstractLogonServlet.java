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

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.openoces.ooapi.certificate.CertificateStatus;
import org.openoces.ooapi.exceptions.AppletException;
import org.openoces.ooapi.exceptions.InvalidSignatureException;
import org.openoces.ooapi.utils.Base64Handler;
import org.openoces.securitypackage.LogonHandler;
import org.openoces.serviceprovider.CertificateAndStatus;
import org.openoces.serviceprovider.ServiceProviderException;

/**
 * This servlet is invoked when the applet sends data to logon.html. The servlet is responsible for logging the user in if the supplied data is
 * valida.
 * 
 * Whether the user is logged on or not he will be redirected to <em>base-url</em>/restricted. In conjunction with {@link LogonFilter} the result is
 * that the user will only be shown the content of the restricted url when he is logged in.
 */
@SuppressWarnings("serial")
public abstract class AbstractLogonServlet extends AbstractCommonServlet {

    private static final Logger logger = Logger.getLogger(AbstractLogonServlet.class);

    /**
     * Uses <em>sikkerhedspakken</em> to check that the supplied data from the applet contains a valid certificate.
     * 
     * If a valid certificate is received the user's pid is stored in a session attribute named "pid".
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OcesEnvironment.setOcesEnvironment();
        
        String signature = null;
        String result = req.getParameter("result");

        if (result != null) {
            result = Base64Handler.decode(result);

            String encodedSignature = req.getParameter("signature");
            if (encodedSignature != null && !encodedSignature.isEmpty()) {
                signature = Base64Handler.decode(encodedSignature);
            }
        } else {

            // JSClient
            String responseB64 = req.getParameter("response");

            if (responseB64 != null && responseB64.length() > 0) {
                String response = Base64Handler.decode(responseB64);

                // As this is a demo a rudimentary validation will do: Client error codes e.g. APP003, OCES001, AUTH010 etc.
                // are less than 20 characters even when b64 encoded, whereas a valid response is an xml document encompassing more characters
                if (response.length() > 20) {
                    result = "ok";
                    signature = response;
                } else {
                    result = response;
                }
            }
        }

        HttpSession httpSession = req.getSession();
        if ("ok".equalsIgnoreCase(result)) {
            try {
                String challenge = getChallenge(httpSession);
                String serviceProviderName = NemIdProperties.getServiceProviderName();

                CertificateAndStatus certificateAndStatus = LogonHandler.validateAndExtractCertificateAndStatus(signature, challenge,
                        serviceProviderName);
                //Extract and store rememberUseridToken, if it has been received.
                String rememberUseridToken = certificateAndStatus.getRememberMyUserIdToken();//LogonHandler.extractRememberUserIdToken(signature);
                manageRememberUserIdCookie(req, resp, rememberUseridToken);             
                if (logger.isDebugEnabled()) {
                	logger.debug("Token extracted: " + rememberUseridToken);
                    logger.debug("Cerfiticate being validated: " + certificateAndStatus.getCertificate().getSubjectDistinguishedNameOces2());
                    logger.debug("Certificate validation status: " + certificateAndStatus.getCertificateStatus().getName());
                }

                if (certificateAndStatus.getCertificateStatus() != CertificateStatus.VALID) {
                    invalidCertificate(req, resp, certificateAndStatus);
                } else {
                    logon(req, resp, certificateAndStatus);
                }

            } catch (InvalidSignatureException e) {
                unknownError(req, resp, e);
            } catch (ServiceProviderException e) {
                unknownError(req, resp, e);
            } catch (AppletException e) {
                unknownError(req, resp, e);
            } catch (Exception e) {
                unknownError(req, resp, e);
            }
        } else {
            logger.error("Failure. Error code from Nets: " + result + ", description: " + ErrorHandler.getTechnicalDescription(result));
            showErrorPage(req, resp, ErrorHandler.getErrorText(result) + " desc: " + ErrorHandler.getTechnicalDescription(result));
        }
    }

    protected abstract void logon(HttpServletRequest req, HttpServletResponse resp, CertificateAndStatus certificateAndStatus) throws IOException,
            ServletException;

    protected abstract void invalidCertificate(HttpServletRequest req, HttpServletResponse resp, CertificateAndStatus certificateAndStatus)
            throws IOException, ServletException;

    protected abstract void unknownError(HttpServletRequest req, HttpServletResponse resp, Exception e) throws IOException, ServletException;

    protected abstract void showErrorPage(HttpServletRequest req, HttpServletResponse resp, String errorText) throws IOException, ServletException;

    private String getChallenge(HttpSession httpSession) {
        String challenge = (String) httpSession.getAttribute(ChallengeGenerator.CHALLENGE_SESSION_KEY);
        if (challenge == null || challenge.equals("")) {
            throw new RuntimeException("Session has no challenge");
        }
        httpSession.removeAttribute(ChallengeGenerator.CHALLENGE_SESSION_KEY);
        return challenge;
    }
}