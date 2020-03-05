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

import javax.servlet.*;
import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * This servlet filter checks whether the user is logged in.
 * 
 * If the user is not logged in he will be redirected to the logon page.
 */
public abstract class AbstractLogonFilter implements Filter {
	private static final Logger logger = Logger.getLogger(AbstractLogonFilter.class);
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
	public void destroy() {}

	/**
	 * Decide whether the user is logged in by checking whether a session attribute named "pid" exists.
	 * 
	 * If the user is logged in the requested page will be shown.
	 * 
	 * If the user is not logged in he will be redirected to the logon page.
	 * 
	 * This sample application does not remember which page the user requested before he was logged in.
	 */
    @Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
		throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
	    if (isLoggedIn(request)) {
	    	chain.doFilter(request, response);	    	
	    } else {
	    	redirectToLogonPage(httpRequest, httpResponse, getPreferredLogin(httpRequest));
	    }
	}

	/**
	 * Checks whether the user is logged in.
	 */
	protected abstract boolean isLoggedIn(ServletRequest request);

     /**
	 * Redirects the user to the logon page.
	 */
    protected abstract void redirectToLogonPage(HttpServletRequest request, HttpServletResponse response, String preferredLogin) throws IOException;

    private String getPreferredLogin(HttpServletRequest request) {
        for (Cookie cookie : request.getCookies()) {
        	logger.debug("cookie: " + cookie.getName() +":"+ cookie.getValue());
            if ("preferredLogin".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
