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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class ClientModeSelectorServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(ClientModeSelectorServlet.class);
	
    public static final String CLIENTMODE = "clientmode";

    public static final String STANDARD_MODE = "standard";
    public static final String LIMITED_MODE = "limited";
    
    public static boolean isStandardMode(HttpSession session) {
    	return STANDARD_MODE.equalsIgnoreCase((String) session.getAttribute(CLIENTMODE));
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String toggleValue;
        if(isStandardMode(session)){
        	toggleValue = LIMITED_MODE;
        } else {
        	toggleValue = STANDARD_MODE;
        }
        
        boolean resetSizes = !toggleValue.equalsIgnoreCase((String)session.getAttribute(CLIENTMODE));
        	
		session.setAttribute(CLIENTMODE, toggleValue);
		
		if (resetSizes) {
			ClientSizeSelectorServlet.setSize(session);
		}
		log.debug("The clientmode session attribute is set to: " + toggleValue);
        resp.sendRedirect(req.getContextPath() + "/extras/index.jsp");
    }
}
