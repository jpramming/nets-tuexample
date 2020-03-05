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

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.openoces.ooapi.environment.Environments.Environment;
import org.openoces.ooapi.service.RidOIOServiceProviderClient;
import org.openoces.serviceprovider.ServiceProviderSetup;

import dk.certifikat.tuexample.NemIdProperties;

@SuppressWarnings("serial")
public class RidCprLookupServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(RidCprLookupServlet.class);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String rid = request.getParameter("rid");
        long spid =  Long.valueOf(NemIdProperties.getRidServiceProviderId());

        HttpSession session = request.getSession();
        session.removeAttribute("text");
        session.removeAttribute("errorText");
        
        if(!checkParametersValid(rid, session)) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/extras/rid.jsp");
            dispatcher.forward(request, response);
            return;
        }

        try {
            Environment environment = NemIdProperties.getOces2Environment();

            logger.debug("spid parameter " + spid);
            logger.debug("rid parameter " + rid);
            logger.debug("environment parameter " + environment);

            RidOIOServiceProviderClient ridService = ServiceProviderSetup.getRidServiceProviderClient(environment);

            String cpr = ridService.getCpr(rid.trim(), spid, null);
            if (cpr == null || "".equals(cpr)) {
                session.setAttribute("errorText", "Fandt ikke tilknyttet CPR");
            } else { 
                request.setAttribute("cpr", cpr);
            }
        } catch (Exception e) {
            session.setAttribute("errorText", e);
			logger.error(e, e);
        }
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/extras/rid.jsp");
        dispatcher.forward(request, response);
    }
    
    private boolean checkParametersValid(String rid, HttpSession session) {
        if (rid == null || "".equals(rid.trim())) {
            session.setAttribute("errorText", "Tom, eller manglende Rid");
            return false;
        }
        return true;
    } 
}
