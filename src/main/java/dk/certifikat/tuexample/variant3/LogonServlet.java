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

package dk.certifikat.tuexample.variant3;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.openoces.ooapi.certificate.MocesCertificate;
import org.openoces.ooapi.certificate.OcesCertificate;
import org.openoces.ooapi.certificate.PocesCertificate;
import org.openoces.serviceprovider.CertificateAndStatus;

import dk.certifikat.tuexample.AbstractLogonServlet;
import dk.certifikat.tuexample.ErrorHandler;

@SuppressWarnings("serial")
public class LogonServlet extends AbstractLogonServlet {
	
	private static final Logger logger = Logger.getLogger(LogonServlet.class);
    public static final String KEY_LOGGED_IN = "variant3.loggedIn";
    public static final String KEY_PID = "variant3.pid";
    public static final String KEY_RID = "variant3.rid";
    public static final String KEY_CVR = "variant3.cvr";
    public static final String KEY_TYPE = "variant3.type";
    public static final String KEY_NAME = "variant3.name";

    @Override
    protected void logon(HttpServletRequest req, HttpServletResponse resp, CertificateAndStatus certificateAndStatus) throws IOException, ServletException {
        HttpSession httpSession = req.getSession();
        if (isMocesOrPoces(certificateAndStatus)) {
            setAttributesForMocesOrPoces(certificateAndStatus.getCertificate(), httpSession);
            resp.sendRedirect(req.getContextPath()+"/variant3/restricted/kvittering.jsp");
        } else {
            showErrorPage(req, resp, "Det benyttede certifikat er ikke af korrekt type. Forventede medarbejdercertifikat eller personligt certifikat, fik " + ErrorHandler.getCertificateType(certificateAndStatus.getCertificate()));
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

    private boolean isMocesOrPoces(CertificateAndStatus certificateAndStatus) {
        return certificateAndStatus.getCertificate() instanceof PocesCertificate || certificateAndStatus.getCertificate() instanceof MocesCertificate;
    }

    private void setAttributesForMocesOrPoces(OcesCertificate certificate, HttpSession httpSession) {
        httpSession.setAttribute(KEY_LOGGED_IN, Boolean.TRUE);

        if(certificate instanceof PocesCertificate) {
            httpSession.setAttribute(KEY_TYPE, "POCES");
            PocesCertificate pocesCert = ((PocesCertificate)certificate);
            httpSession.setAttribute(KEY_PID, pocesCert.getPid());
            if (pocesCert.hasPseudonym()) {
                httpSession.setAttribute(KEY_NAME, "[intet navn]");
            } else {
                httpSession.setAttribute(KEY_NAME, pocesCert.getSubjectCN());
            }
        } else {
            httpSession.setAttribute(KEY_TYPE, "MOCES");
            httpSession.setAttribute(KEY_RID, ((MocesCertificate)certificate).getRid());
            httpSession.setAttribute(KEY_CVR, ((MocesCertificate)certificate).getCvr());
        }
    }

    @Override
    protected void showErrorPage(HttpServletRequest req, HttpServletResponse resp, String errorText) throws IOException, ServletException {
        req.setAttribute("errorText", errorText);
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/variant3/loginfejl.jsp");
        dispatcher.forward(req, resp);
    }
}