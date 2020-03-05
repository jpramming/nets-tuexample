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

package dk.certifikat.tuexample.variant2;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.openoces.ooapi.certificate.MocesCertificate;
import org.openoces.serviceprovider.CertificateAndStatus;

import dk.certifikat.tuexample.AbstractLogonServlet;
import dk.certifikat.tuexample.ErrorHandler;

@SuppressWarnings("serial")
public class LogonServlet extends AbstractLogonServlet {
	
	private static final Logger logger = Logger.getLogger(LogonServlet.class);
    public static final String KEY_LOGGED_IN = "variant2.loggedIn";
    public static final String KEY_RID = "variant2.rid";
    public static final String KEY_CVR = "variant2.cvr";

    @Override
    protected void logon(HttpServletRequest req, HttpServletResponse resp, CertificateAndStatus certificateAndStatus) throws IOException, ServletException {
        HttpSession httpSession = req.getSession();
        if (isMoces(certificateAndStatus)) {
            MocesCertificate mocesCertificate = (MocesCertificate) certificateAndStatus.getCertificate();
            httpSession.setAttribute(KEY_LOGGED_IN, Boolean.TRUE);
            httpSession.setAttribute(KEY_RID, mocesCertificate.getRid());
            httpSession.setAttribute(KEY_CVR, mocesCertificate.getCvr());
            resp.sendRedirect(req.getContextPath()+"/variant2/restricted/kvittering.jsp");
        } else {
            showErrorPage(req, resp, "Det benyttede certifikat er ikke af korrekt type. Forventede medarbejdercertifikat, fik " + ErrorHandler.getCertificateType(certificateAndStatus.getCertificate()));
        }
    }

    @Override
    protected void invalidCertificate(HttpServletRequest req, HttpServletResponse resp, CertificateAndStatus certificateAndStatus) throws IOException, ServletException {
        showErrorPage(req, resp, "Certifikatet er " + ErrorHandler.getCertificateStatusText(certificateAndStatus.getCertificateStatus()));
    }

    @Override
	protected void unknownError(HttpServletRequest req, HttpServletResponse resp, Exception e) throws ServletException, IOException {
		logger.error(e.getMessage(), e);
        showErrorPage(req, resp, e.getMessage());
	}

    private boolean isMoces(CertificateAndStatus certificateAndStatus) {
        return certificateAndStatus.getCertificate() instanceof MocesCertificate;
    }

    @Override
    protected void showErrorPage(HttpServletRequest req, HttpServletResponse resp, String errorText) throws IOException, ServletException {
        req.setAttribute("errorText", errorText);
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/variant2/loginfejl.jsp");
        dispatcher.forward(req, resp);
    }
}