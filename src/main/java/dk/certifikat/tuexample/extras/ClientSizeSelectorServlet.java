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
public class ClientSizeSelectorServlet extends HttpServlet {

    private static final String SESS_CLIENTDYNAMICSIZE = "clientdynamicsize";

	private static final String SESS_CLIENTHEIGHT = "clientheight";

	private static final String SESS_CLIENTWIDTH = "clientwidth";

	private static final String PARAMETER_DYNAMIC_DIM = "dynamicDim";

	private static final String PARAMETER_DIMENSIONS = "dimensions";

	private static final Logger log = Logger.getLogger(ClientModeSelectorServlet.class);

	private static String[][] STD_SIZES = new String[][] {
		new String[] {"450", "500", "*"}, new String[] {"500", "450", null}
	}; 
	private static String[][] LMT_SIZES = new String[][] {
		new String[] {"200", "250", null}, new String[] {"250", "300", null},
		new String[] {"299", "324", null}, new String[] {"300", "325", null},
		new String[] {"300", "375", null}, new String[] {"320", "460", "*"},
		new String[] {"330", "460", null}, new String[] {"500", "450", null},
		new String[] {"640", "960", null}, new String[] {"640", "1136", null},
		new String[] {"1024", "768", null}
	};
	
	public static String[][] getSizesJS(HttpSession session) {
		return ClientModeSelectorServlet.isStandardMode(session) ? STD_SIZES : LMT_SIZES;
	}
	public static void setSize(HttpSession session) {
		String[] defaultSize = null;
		String[] selectSize = new String[] { (String) session.getAttribute(SESS_CLIENTWIDTH), (String) session.getAttribute(SESS_CLIENTHEIGHT) };
		boolean isSelectSize = null != selectSize[0] && null != selectSize[1];
		for (String[] size : getSizesJS(session)) {
			if (null == defaultSize) {
				defaultSize = size;
			} 
			if (null != size[2]) {
				 defaultSize = size;
			}
			if (isSelectSize && selectSize[0].equals(size[0]) && selectSize[1].equals(size[1]) ) {
				//faund previously selected size
				setSize(session, selectSize);
				return;
			}
		}
		setSize(session, defaultSize);
	}
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        
        String dimensions = req.getParameter(PARAMETER_DIMENSIONS);
        
        String[] lengths = dimensions.split("[xX]");
        
        for (int i = 0; lengths.length > i ; i++) {
        	lengths[i] = lengths[i].trim();
        }
        
		setSize(session, lengths);
		
		log.debug("The clientsize session attribute is set to: " + session.getAttribute(SESS_CLIENTWIDTH) + " x " + session.getAttribute(SESS_CLIENTHEIGHT));
        
		// Dynamic Resizing enabled
		String dynamicDimensions = req.getParameter(PARAMETER_DYNAMIC_DIM);
		
		session.setAttribute(SESS_CLIENTDYNAMICSIZE, null != dynamicDimensions);
			
		log.debug("The clientdynamicsize session attribute is set to:  " + session.getAttribute(SESS_CLIENTDYNAMICSIZE));
		
		resp.sendRedirect(req.getContextPath() + "/extras/index.jsp");
    }
    
	private static void setSize(HttpSession session, String[] lengths) {
		session.setAttribute(SESS_CLIENTWIDTH, lengths[0]);
		session.setAttribute(SESS_CLIENTHEIGHT, lengths[1]);
		if (ClientModeSelectorServlet.isStandardMode(session)) {
			session.setAttribute(SESS_CLIENTDYNAMICSIZE, false);
		}
	}
}
