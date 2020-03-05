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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * This abstract servlet contains common functionality for both log on and signing scenarios.
 */
public class AbstractCommonServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(AbstractCommonServlet.class);
	protected void manageRememberUserIdCookie(HttpServletRequest req,
			HttpServletResponse resp, String rememberUseridToken) {
		Cookie tokenCookie = null; 
		for(Cookie cookie : req.getCookies()){
			if ("rememberUserId".equals(cookie.getName())){
				tokenCookie = cookie;
			}
		}
		if(rememberUseridToken == null){
			logger.debug("No token returned");
			if (tokenCookie != null){
				//TODO: Optional: Instead of always deleting the cookie, consider also checking here whether the REMEMBER_USERID parameter was sent in the request at all.
				logger.debug("Deleting tokenCookie");
				//delete the obsolete cookie and set value to "dead" so that we can ignore it until it is removed.
				tokenCookie.setMaxAge(0);
				tokenCookie.setValue("dead");
				resp.addCookie(tokenCookie);
			}
		} else {
			// We have a token, set or update cookie.
			logger.debug("Token returned: " + rememberUseridToken);
			if (tokenCookie == null) {
				logger.debug("Creating new tokenCookie.");
				tokenCookie = new Cookie("rememberUserId", null);
			}
			tokenCookie.setValue(rememberUseridToken);
			tokenCookie.setMaxAge(31104000); // 360*60*60*24 //Integer.MAX_VALUE can give problems with WebSphere7.
			tokenCookie.setPath("/");
			resp.addCookie(tokenCookie);
			logger.debug("Storing tokenCookie: " + tokenCookie.getValue() + ":" + tokenCookie.getMaxAge() + ":" + tokenCookie.getPath());
		}
	}

}
