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

import org.openoces.ooapi.environment.Environments.Environment;
import org.openoces.ooapi.service.PidServiceProviderClient;
import org.openoces.ooapi.service.ServiceException;
import org.openoces.ooapi.service.ServiceProviderClient;
import org.openoces.serviceprovider.ServiceProviderSetup;

import dk.certifikat.tuexample.NemIdProperties;
import dk.certifikat.tuexample.OcesEnvironment;

@SuppressWarnings("serial")
public class PidCprMatchServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OcesEnvironment.setOcesEnvironment();

        request.removeAttribute("PIDCprmatch");
        request.removeAttribute("PIDCprlookup");
        request.removeAttribute("text");
        request.removeAttribute("errorText");

        String pid = request.getParameter("pidmatch");
        String cpr = request.getParameter("cpr");

        if (!checkParametersValid(pid, cpr, request)) {
            request.setAttribute("PIDCprmatch", false);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/extras/pid.jsp");
            dispatcher.forward(request, response);
            return;
        }

        try {
            PidServiceProviderClient pidCprService = ServiceProviderSetup.getPidServiceProviderClient(NemIdProperties.getOces2Environment());
            boolean cprMatched = pidCprService.match(cpr, pid, NemIdProperties.getPidServiceProviderId(), null);
            request.setAttribute("PIDCprmatch", cprMatched);
            if (cprMatched) {
                request.setAttribute("text", "Det lykkedes at matche CPR og PID.");
            } else {
                request.setAttribute("errorText", "pid " + pid + " matcher ikke cpr " + cpr);
            }
        } catch (ServiceException e) {
            request.setAttribute("PIDCprmatch", false);
            request.setAttribute("errorText", e.getStatusTextDK());
        } catch (Exception generalException) {
            request.setAttribute("PIDCprmatch", false);
            request.setAttribute("errorText", generalException.getCause().getMessage());
        }

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/extras/pid.jsp");
        dispatcher.forward(request, response);
    }

    private boolean checkParametersValid(String pid, String cpr, HttpServletRequest request) {
        if (pid == null || "".equals(pid.trim())) {
            request.setAttribute("errorText", "Tom, eller manglende PID");
            return false;
        }

        if (cpr == null || "".equals(cpr.trim())) {
            request.setAttribute("errorText", "Tom, eller manglende CPR");
            return false;
        }
        return true;
    }

}
