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
import org.bouncycastle.util.encoders.Base64;

@SuppressWarnings("serial")
public class NmasSetupServlet extends HttpServlet {

    public static final String SESS_TRANSACTIONCONTEXT = "transactioncontext";

	public static final String SESS_ENABLE_AWAITING_APP_APPROVAL_EVENT = "enableapprovalevent";

	public static final String SESS_SUPPRESS_PUSH_TO_DEVICE = "suppresspush";

	public static final String CODEAPPSETTING = "codeapp";
	private static final Logger log = Logger.getLogger(NmasSetupServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        
        String transactionContextUnEncoded = req.getParameter(SESS_TRANSACTIONCONTEXT);
        String enableApprovalEvent = req.getParameter(SESS_ENABLE_AWAITING_APP_APPROVAL_EVENT);
        String suppressPush = req.getParameter(SESS_SUPPRESS_PUSH_TO_DEVICE);
        
		session.setAttribute(SESS_TRANSACTIONCONTEXT, Base64.encode(transactionContextUnEncoded.getBytes("UTF-8")));
		session.setAttribute(SESS_ENABLE_AWAITING_APP_APPROVAL_EVENT, convertCheckboxValueToTrueFalse(enableApprovalEvent));
		session.setAttribute(SESS_SUPPRESS_PUSH_TO_DEVICE, convertCheckboxValueToTrueFalse(suppressPush));
		
		String text = "Setting NMAS properties: transactionContextUnEncoded: '" + session.getAttribute(SESS_TRANSACTIONCONTEXT) 
			+ "' enableApprovalEvent: '" + session.getAttribute(SESS_ENABLE_AWAITING_APP_APPROVAL_EVENT) 
			+ "' suppressPush: '" + session.getAttribute(SESS_SUPPRESS_PUSH_TO_DEVICE) + "'" ;
		log.debug(text);
        
		resp. sendRedirect(req.getContextPath() + "/extras/index.jsp?" + CODEAPPSETTING + "=true");
    }
    
    private Boolean convertCheckboxValueToTrueFalse(String onOff) {
    	if ("on".equalsIgnoreCase(onOff)) {
    		return Boolean.TRUE;
    	}
    	return Boolean.FALSE;
    }
}
