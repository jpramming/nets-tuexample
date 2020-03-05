<%--

    Copyright (c) 2010, DanID A/S
    All rights reserved.

    Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

     - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
     - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
     - Neither the name of the DanID A/S nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
    INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
    DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
    SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
    SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
    WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
    USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

--%>
<!DOCTYPE html> 
<%@page import="dk.certifikat.tuexample.extras.attachment.*"%>
<%@page import="dk.certifikat.tuexample.Util"%>
<%@page import="org.bouncycastle.util.encoders.Base64"%>
<%@page import="org.openoces.ooapi.utils.Base64Handler"%>
<%@page import="dk.certifikat.tuexample.extras.ClientSizeSelectorServlet"%>
<%@page import="org.bouncycastle.util.encoders.Base64"%>
<%@page import="java.net.URL"%>
<%@page import="dk.certifikat.tuexample.extras.NmasSetupServlet"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="da" lang="da">
    <%
		if ( (session.getAttribute("signTextFormat") == null) || (session.getAttribute("signTextFormat").equals("")) ) {
			session.setAttribute("signTextFormat", "html");
		}
		
		if(session.getAttribute("revocationCheckType") == null) {
			session.setAttribute("revocationCheckType", "partitioned");
		}

		String signTextFormat = (String) session.getAttribute("signTextFormat");
		if(signTextFormat.equalsIgnoreCase("pdf")) {
        	session.setAttribute("signTextUri", "/extras/attachment/sample.pdf");
       	} else if(signTextFormat.equals("pdf_w_attachment")) {
       		AttachmentFactory factory = new AttachmentFactory("SHA256");
    		URL attachmentFile = application.getResource("/extras/attachment/sample.pdf");
    		Attachment attachment = factory.create("attachment", request.getContextPath() + "/extras/attachment/sample.pdf", attachmentFile, true);
       		session.setAttribute("signTextUri", "/extras/attachment/sample.pdf");
            session.setAttribute("attachment", attachment);
       	}
     %>

    <head>
        <title>TU-interaktionsdesign - Standardindstillinger</title>
        <%@ include file="/WEB-INF/jsp-include/common-head.jsp" %>
         <script>
            function showXMLFields() {
                var myTextField = document.getElementById('signTextType');
                if (myTextField.value == "xml") {
                    document.getElementById('signtransformation-div').style.display = 'block';
                }
                else {
                    document.getElementById('signtransformation-div').style.display = 'none';
                }
            }
            function findFirstParentOfType(elm, type) {
            	type = type.toUpperCase();
                while (elm) {
                	if (elm.tagName == type) {
                		return elm;
                	}
                	elm = elm.parentElement;
                }
            }
            function toggle(event, showElm, hideElm) {
            	var showMe = document.getElementById(showElm);
            	showMe.style='display:block';
            	var li = findFirstParentOfType(event.target, 'li');
            	var lis = li.parentElement.children;
            	var arrayLength = lis.length;
            	for (var i = 0; i < arrayLength; i++) {
            		lis[i].className=undefined;
            	}
            	li.className='selected';
            	
            	var hideMe = document.getElementById(hideElm);
            	hideMe.style='display:none';
            	
            }
        </script>   
    </head>
    <body id="demosite">
        <div id="skipToContent" class="accessibility">
            <dl>
                <dt>Genveje:</dt>
                <dd><a href="#content" title="Gå direkte til hovedindhold" accesskey="2">Hovedindhold</a></dd>
            </dl>
        </div>

        <!-- container for header section -->
        <div id="headerWrapper">
            <div id="header">
                <div id="logo">
                    <img src="../resources/images/logo.png" alt="Demologo" title="Kun til demobrug" />
                </div>
                <ul id="menu">
                    <li><a href="#.#" title="Simuleret menu kun til demobrug">Menu 01</a></li>
                    <li><a href="#.#" title="Simuleret menu kun til demobrug">Menu 02</a></li>
                    <li><a href="#.#" title="Simuleret menu kun til demobrug">Menu 03</a></li>
                    <li><a href="#.#" title="Simuleret menu kun til demobrug">Menu 04</a></li>
                    <li><a href="#.#" title="Simuleret menu kun til demobrug">Menu 05</a></li>
                </ul>
                <div class="button-wrapper">
                    <a href="../index.jsp" title="Klik her for at komme tilbage til forsiden">Til forsiden</a>				
                    <a href="../variant1/index.jsp" title="Klik her for at prøve et variant1 forløb">Variant1</a>
                    <a href="../variant2/index.jsp" title="Klik her for at prøve et variant2 forløb">Variant2</a>
                    <a href="../variant3/index.jsp" title="Klik her for at prøve et variant3 forløb">Variant3</a>					
                </div>
            </div>
        </div>

        <div id="contentWrapper">
            <div id="content">
                <div class="content-block">
                        <!-- container for header section -->
       <div id="headerSelectors">
                <ul id="tabsSetting">
                <% if(null == request.getParameter("codeapp")) { %>
                    <li class="selected">
                <% } else { %>
                    <li>
                <% } %>
                        <a id="std" accesskey="4" onclick="toggle(event, 'Standardindstillinger','nmas-indstillinger');">
                            <span>Standardindstillinger</span>
                        </a>
                    </li>
                  <% if (null != request.getParameter("codeapp")) { %>
                    <li class="selected">
	              <% } else { %>
	                <li>
	              <% } %>
                        <a id="nmas" accesskey="6" onclick="toggle(event, 'nmas-indstillinger', 'Standardindstillinger');">
                            <span>N&oslash;gleapp indstillinger</span>
                        </a>
                    </li>
                </ul>
            </div>
                <% if(null == request.getParameter("codeapp")) { %>
                    <div id="Standardindstillinger">
                <% } else { %>
                    <div id="Standardindstillinger" style="display: none;">
                <% } %>
        
                    <h1>Standardindstillinger</h1>
                    <p>Her kan du se og ændre standardindstillingerne til login og signering.</p>

                    <form method="post" action="skift-spaerrelistecheck.html">
                        <h2>Valg af spærrelistecheck</h2>
                        <p>Du kan vælge hvilken form for spærreliste du ønsker at benytte.</p>
                        <p>
                            <label for="revocationCheckType">Spærrelistetype:</label>
                            <select id="revocationCheckType" name="revocationCheckType" style="width: 150px;" onchange="this.form.submit();">
                                <option value="full" <%= "full".equals(session.getAttribute("revocationCheckType")) ? "selected=selected" : ""%>>Full CRL</option>
                                <option value="partitioned" <%= "partitioned".equals(session.getAttribute("revocationCheckType")) ? "selected=selected" : ""%>>Partitioned CRL</option>
                                <option value="ocsp" <%= "ocsp".equals(session.getAttribute("revocationCheckType")) ? "selected=selected" : ""%>>OCSP</option>
                            </select>
                        </p>
                    </form>

                    <form method="post" action="skift-signeringsteksttype.html">
                        <h2>Valg af signeringstekst-type</h2>
                        <p>Du kan vælge hvilken form for signeringstekst du ønsker at benytte.</p>
                        <div>
                            <p>
                                <label for="signTextType">Signeringstekst-type:</label>
                                <!--<select id="signTextType" name="signTextType" style="width: 150px;" onchange="showXMLFields()">-->
                                <select id="signTextType" name="signTextType" style="width: 150px;" onchange="this.form.submit();">
                                    <option value="html" <%= "html".equals(session.getAttribute("signTextFormat")) ? "selected=selected" : "" %>>HTML</option>
                                    <option value="txt" <%= "txt".equals(session.getAttribute("signTextFormat")) ? "selected=selected" : "" %>>Tekst</option>
                                    <option value="xml" <%= "xml".equals(session.getAttribute("signTextFormat")) ? "selected=selected" : "" %>>XML</option>
                                    <option value="pdf" <%= "pdf".equals(session.getAttribute("signTextFormat")) ? "selected=selected" : "" %>>PDF</option>
                                    <option value="pdf_w_attachment" <%= "pdf_w_attachment".equals(session.getAttribute("signTextFormat")) ? "selected=selected" : "" %>>PDF W. Attachment</option>
                                </select>
                            </p>
                         </div>
                        <div id="signtransformation-div" style="display: none;">
                            <div>
                                <label for="signTransformationId">Singerings transformations-id:</label>
                                <span style="padding-left: 5px">
                                    <input id="signTransformationId" name="signTransformationId" size="20" value="<%= session.getAttribute("signTransformationId") != null ? session.getAttribute("signTransformationId") : "" %>" />
                                </span>
                            </div>
                            <div>
                                <label for="signTransformationStyle">Singerings xml-stylesheet:</label>
                                <span style="padding-left: 22px">
                                    <textarea id="signTransformationStyle" name="signTransformationStyle" rows="2" cols="30" style="resize: none;"></textarea>
                                </span>
                            </div>
                        </div>                         
                    </form>
                    <form method="post" action="skift-rememberUserID.html">
                        <h2>Valg af huskBrugerId (kun JS)</h2>
                        <p>
                        <input type="hidden" id="rememberUserId" name="rememberUserId" value="<%= ("false".equals(session.getAttribute("rememberUserId")) ? "false" : "true") %>"/>
                            Husk Bruger-id er <span style="font-style:italic; font-weight:bolder"><%= ("false".equals(session.getAttribute("rememberUserId"))) ? "IKKE " : "" %> AKTIVERET.</span>
                            <input type="button" onclick="document.getElementById('rememberUserId').value='<%="false".equals(session.getAttribute("rememberUserId")) ? "true" : "false"%>';this.form.submit();" value="<%=("false".equals(session.getAttribute("rememberUserId"))) ? "AKTIVER" : "DEAKTIVER" %>"/>
                            <% if (!"false".equals(session.getAttribute("rememberUserId"))) { %>
                             <br/><label for="rememberUserIdChecked">Husk Bruger-id sat initielt</label><input type="checkbox" id="rememberUserIdChecked" name="rememberUserIdChecked" <%=(Boolean.FALSE == session.getAttribute("rememberUserIdChecked")) ? "" : "checked" %>  onclick="this.form.submit();"/>
                            <% } %>
                        </p>
                    </form>
                    <form method="post" action="skift-jsclient-type.html">
                        <h2>Valg af klient type (kun JS)</h2>
                        <p>
                            JS Klient type er <span style="font-style:italic; font-weight:bolder"><%= ("standard".equals(session.getAttribute("clientmode"))) ? "Standard" : "Limited" %>.</span>
                            <input type="submit" value="<%=("standard".equals(session.getAttribute("clientmode"))) ? "Skift til Limited" : "Skift til Standard" %>"/>
                        </p>
                    </form>
                    <form method="post" action="skift-jsclient-size.html">
                        <h2>Valg af klient st&oslash;rrelse (kun JS)</h2>
                          
						<table>
							<tr>
                            	<td><label for="dimensions">St&oslash;rrelse</label></td>
                            	<td><select id="dimensions" name="dimensions" onchange="this.form.submit();"></select></td>
                            	<% if (!"standard".equalsIgnoreCase((String) session.getAttribute("clientmode"))) { %>
                            	<td><label for="dynamicDim">Tillad dynamisk st&oslash;rrelse</label></td>
                            	<td><input type="checkbox" id="dynamicDim" name="dynamicDim" <%=(Boolean.TRUE == session.getAttribute("clientdynamicsize")) ? "checked" : "" %>  onclick="this.form.submit();"/></td>
                            	<% } %>                            	
                            </tr>
                           </table>
                    </form>
            </div>
            <% if (null != request.getParameter("codeapp")) { %>
              <div id="nmas-indstillinger">
            <% } else { %>
              <div id="nmas-indstillinger" style="display: none;">
            <% } %>
                    <h1>N&oslash;gleapp indstillinger</h1>
                    <p>Her kan du se og ændre n&oslash;gleapp indstillingerne til login og signering. Disse indstillinger er kun med i nyere versioner af NemID</p>
                    <form method="post" action="skift-nmas-setup.html">
                        <h2>Valg af klient parametre til n&oslash;gleapp (kun JS / )</h2>
                          
                        <table>
                            <tr>
                                <td><label for="transactioncontext">Transactions kontekst</label></td>
                                <%
                                        byte[] transactioncontext = (byte[]) session.getAttribute(NmasSetupServlet.SESS_TRANSACTIONCONTEXT);
                                		String transactioncontextStr;
                                		if (null != transactioncontext && 0 < transactioncontext.length) {
                                			transactioncontextStr = new String(Base64.decode(transactioncontext), "UTF-8");
                                		} else {
                                			transactioncontextStr = "";
                                		}
                                
                                %>
                                <td><input id="transactioncontext" name="<%=NmasSetupServlet.SESS_TRANSACTIONCONTEXT %>" type="text" onchange="this.form.submit();" value="<%=transactioncontextStr%>"></input></td>
                            </tr>
                            <tr>
                                <td><label for="enableapprovalevent">Modtag javascript event ved godkendelse</label></td>
                                <td><input id="enableapprovalevent" name="<%=NmasSetupServlet.SESS_ENABLE_AWAITING_APP_APPROVAL_EVENT %>" type="checkbox" <%=(Boolean.TRUE == session.getAttribute(NmasSetupServlet.SESS_ENABLE_AWAITING_APP_APPROVAL_EVENT)) ? "checked" : "" %>  onclick="this.form.submit();"/></td>
                            </tr>
                            <tr>
                                <td><label for="suppresspush">Ingen push besked til n&oslash;gleapp (kun pull fra app)</label></td>
                                <td><input id="suppresspush" name="<%=NmasSetupServlet.SESS_SUPPRESS_PUSH_TO_DEVICE %>" type="checkbox" <%=(Boolean.TRUE == session.getAttribute(NmasSetupServlet.SESS_SUPPRESS_PUSH_TO_DEVICE)) ? "checked" : "" %>  onclick="this.form.submit();"/></td>
                            </tr>
                           </table>
                    </form>
                    </div>
                    <div class="button-wrapper">
						<a href="pid.jsp" title="Klik her for at teste PID">Test PID</a>
						<a href="rid.jsp" title="Klik her for at teste RID">Test RID</a>
                    </div>
                </div>
                <div class="image-block">
                    <img src="../resources/images/01.jpg" alt="Demobillede" />
                    <img src="../resources/images/02.jpg" alt="Demobillede" />
                </div>
            </div>
        </div>
	    <script type="text/javascript">
            var currentWidthSetting = <%=(null == session.getAttribute("clientwidth")) ? "-1" : session.getAttribute("clientwidth") %>;
            var currentHeightSetting = <%=(null == session.getAttribute("clientheight")) ? "-1" : session.getAttribute("clientheight") %>;

            <%
		String[][] sizes = ClientSizeSelectorServlet.getSizesJS(session);
        int defaultSize = 0;
		String jsVar = "var sizes = [";
		for (int i = 0; sizes.length > i; i++) {
			String[] size = sizes[i];
			jsVar += "[" + size[0] + "," + size[1]  + "],";
			if (null != size[2]) {
				defaultSize = i;
			}
		}
		jsVar += "];";
		
		 out.print(jsVar);
		 out.print("var defaultSize = " + defaultSize + ";");
			%>
              
		  	function getPixelOption(size) {
		  	  var newOption = document.createElement("option");
		  	  newOption.textContent = "" + size[0] + " X " + size[1];
		  	  newOption.value = "" + size[0] + " X " + size[1];
		  	  return newOption;
		  	}
		  	if (-1 < currentWidthSetting) {
		  		var currentSizeOption = getPixelOption([currentWidthSetting, currentHeightSetting]);
		  	} else {
		  	    var currentSizeOption = getPixelOption(sizes[defaultSize]);
		  	}
		  	var selectDim = document.getElementById("dimensions");
		  	for (var i = 0; sizes.length > i; i++) {
		  	  var newOption = getPixelOption(sizes[i]);
 			  if (currentSizeOption.value == newOption.value) {
	  	        newOption.selected= true;
 			  }    
		  	  selectDim.appendChild(newOption);
		  	 
		  	}
    	</script>
	</body>
</html>