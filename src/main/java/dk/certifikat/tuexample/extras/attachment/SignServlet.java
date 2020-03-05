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

package dk.certifikat.tuexample.extras.attachment;

import dk.certifikat.tuexample.AbstractSignServlet;
import dk.certifikat.tuexample.ErrorHandler;
import org.apache.log4j.Logger;
import org.openoces.ooapi.certificate.MocesCertificate;
import org.openoces.ooapi.certificate.PocesCertificate;
import org.openoces.ooapi.exceptions.InternalException;
import org.openoces.securitypackage.SignatureValidationStatus;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("serial")
public class SignServlet extends AbstractSignServlet {

    private static final Logger logger = Logger.getLogger(SignServlet.class);

    @Override
    protected void signed(SignatureValidationStatus status, HttpServletRequest req, HttpServletResponse resp, String textToBeSigned) throws IOException, InternalException, ServletException {
        if (isMocesOrPoces(status)) {
            showSuccessPage(req, resp);
        } else {
            showErrorPage(req, resp, "Det benyttede certifikat er ikke af korrekt type. Forventede medarbejdercertifikat eller personligt certifikat, fik " + ErrorHandler.getCertificateType(status.getCertificate()));
        }
    }

    @Override
    protected void invalidCertificate(HttpServletRequest req, HttpServletResponse resp, SignatureValidationStatus status) throws ServletException, IOException {
        showErrorPage(req, resp, "Certifikatet er " + ErrorHandler.getCertificateStatusText(status.getCertificateStatus()));
    }

    @Override
    protected void signatureDoesNotMatch(HttpServletRequest req, HttpServletResponse resp, String textToBeSigned) throws ServletException, IOException {
        showErrorPage(req, resp, "Signaturen matcher ikke teksten '" + textToBeSigned + "'.");
    }

    @Override
    protected void notSigned(String result, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info(ErrorHandler.getTechnicalDescription(result));
        showErrorPage(req, resp, ErrorHandler.getErrorText(result));
    }

    @Override
    protected void unknownError(HttpServletRequest req, HttpServletResponse resp, Exception e) throws ServletException, IOException {
		logger.error(e.getMessage(), e);
        showErrorPage(req, resp, e.getMessage());
    }

    private void showSuccessPage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/extras/bilag-signeringskvittering.jsp");
        dispatcher.forward(req, resp);
    }

    private void showErrorPage(HttpServletRequest req, HttpServletResponse resp, String errorText) throws IOException, ServletException {
        req.setAttribute("errorText", errorText);
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/extras/bilag-signeringsfejl.jsp");
        dispatcher.forward(req, resp);
    }

    private boolean isMocesOrPoces(SignatureValidationStatus status) throws InternalException {
        return status.getCertificate() instanceof MocesCertificate ||
                status.getCertificate() instanceof PocesCertificate;
    }
    
    
}
