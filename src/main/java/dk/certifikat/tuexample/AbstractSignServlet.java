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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.openoces.ooapi.certificate.CertificateStatus;
import org.openoces.ooapi.exceptions.InternalException;
import org.openoces.ooapi.utils.Base64Handler;
import org.openoces.securitypackage.SignHandler;
import org.openoces.securitypackage.SignatureValidationStatus;

/**
 * This servlet is activated when data is sent from the applet to sign.html.
 * 
 * This servlet's tasks are:
 * <ul>
 * <li>
 * To present to the user a page with the Open Sign applet including the text to be signed. This is handled by
 * {@link #doGet(HttpServletRequest, HttpServletResponse)}.</li>
 * <li>
 * After signing, to decide whether the data sent by the Open Sign applet is valid, and that the signed text matches the text
 * shown to the user. This is handled by {@link #doPost(HttpServletRequest, HttpServletResponse)}.</li>
 * </ul>
 */
@SuppressWarnings("serial")
public abstract class AbstractSignServlet extends AbstractCommonServlet {
	
    private static final Logger logger = Logger.getLogger(AbstractLogonServlet.class);

    // session attribute names
    public static final String SIGN_TEXT_FORMAT = "signTextFormat";
    public static final String SIGN_TEXT = "signText";
    public static final String SIGN_TEXT_TRANSFORMATION = "signTransformation";
    public static final String SIGN_TEXT_URI = "signTextUri";

    /**
     * Uses <em>sikkerhedspakken</em> to validate that the data sent from the applet contains a valid certificated, a valid
     * signature, and that the signature contains the text to be signed (the attribute textToBeSigned).
     * 
     * This method is being invoked after "Underskriv" has been activated in the applet.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OcesEnvironment.setOcesEnvironment();
        
        String signature = null;
        String result = req.getParameter("result");

        if (result != null && !result.isEmpty()) {
            result = Base64Handler.decode(result);

            signature = req.getParameter("signature");
        } else {

            // JSClient
            String responseB64 = req.getParameter("response");

            if (responseB64 != null && responseB64.length() > 0) {
                // As this is a demo a rudimentary validation will do: Client error codes e.g. APP003, OCES001, AUTH010 etc. basecode encoded
                // are less than 20 characters where as a valid response is an xml document encompassing more characters
                if (responseB64.length() > 20) {
                    result = "ok";
                    signature = responseB64;
                } else {
                    result =  Base64Handler.decode(responseB64);;
                }
            }
        }

        HttpSession httpSession = req.getSession();
        if ("ok".equalsIgnoreCase(result)) {
            try {
                String textToBeSigned = (String) httpSession.getAttribute(SIGN_TEXT);
                String challenge = getChallenge(httpSession);
                String serviceProviderName = NemIdProperties.getServiceProviderName();

                SignatureValidationStatus status = null;

                String signTextFormat = (String) httpSession.getAttribute(SIGN_TEXT_FORMAT);

                if (signTextFormat.equalsIgnoreCase("pdf") || signTextFormat.equalsIgnoreCase("pdf_w_attachment")) {                       
                    status = SignHandler.validateSignatureAgainstAgreementPDF(signature, textToBeSigned, challenge, serviceProviderName);

                } else if (signTextFormat.equalsIgnoreCase("xml")) {
                    String signTextTransformation = (String) httpSession.getAttribute(SIGN_TEXT_TRANSFORMATION);
                    status = SignHandler.validateSignatureAgainstAgreement(signature, textToBeSigned, signTextTransformation, challenge, serviceProviderName);
                } else {
                    status = SignHandler.validateSignatureAgainstAgreement(signature, textToBeSigned, null, challenge, serviceProviderName);
                }
                if (status.getCertificateStatus() != CertificateStatus.VALID) {
                    invalidCertificate(req, resp, status);
                } else if (!status.signatureMatches()) {
                    signatureDoesNotMatch(req, resp, textToBeSigned);
                } else {
                    signed(status, req, resp, textToBeSigned);
                }
                //Extract and store rememberUseridToken, if it has been received.
                String rememberUseridToken = status.getRememberMyUserIdToken();//LogonHandler.extractRememberUserIdToken(signature);
                manageRememberUserIdCookie(req, resp, rememberUseridToken);                 
            } catch (Exception e) {
                unknownError(req, resp, e);
            }
        } else {
            notSigned(result == null || result.isEmpty() ? "CAN002" : result, req, resp);
        }
    }

    protected abstract void signed(SignatureValidationStatus status, HttpServletRequest req, HttpServletResponse resp, String textToBeSigned) throws IOException, InternalException, ServletException;

    protected abstract void notSigned(String result, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

    protected abstract void invalidCertificate(HttpServletRequest req, HttpServletResponse resp, SignatureValidationStatus status) throws ServletException, IOException;

    protected abstract void signatureDoesNotMatch(HttpServletRequest req, HttpServletResponse resp, String textToBeSigned) throws ServletException, IOException;

    protected abstract void unknownError(HttpServletRequest req, HttpServletResponse resp, Exception e) throws ServletException, IOException;

    private String getChallenge(HttpSession httpSession) {
        String challenge = (String) httpSession.getAttribute(ChallengeGenerator.CHALLENGE_SESSION_KEY);
        if (challenge == null || challenge.equals("")) {
            throw new RuntimeException("Session has no challenge");
        }
        httpSession.removeAttribute(ChallengeGenerator.CHALLENGE_SESSION_KEY);
        return challenge;
    }
}