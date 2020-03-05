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
<%
	if ( (session.getAttribute("signTextFormat") == null) || (session.getAttribute("signTextFormat").equals("")) ) {
		session.setAttribute("signTextFormat", "html");
    }
	
    if(session.getAttribute("revocationCheckType") == null) {
		session.setAttribute("revocationCheckType", "partitioned");
    }	
%>
<%@ page pageEncoding="UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="da" lang="da">
    <head>
        <title>TU-interaktionsdesign - Velkommen Privat</title>
        <%@ include file="/WEB-INF/jsp-include/common-head.jsp" %>
        <script type="text/javascript">
			$().ready(function() {
				var rememberedLogin = 'signer-med-javascript.jsp'
				var preferredLogin = $.cookie('preferredLogin');
				
                if (preferredLogin == 'Software') {
					rememberedLogin = "signer-uden-engangsnoegle_iframe.jsp";
				}
				$('#sign').attr('href', rememberedLogin);
			});
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
                    <img src="<c:url value='/resources/images/logo.png'/>" alt="Demologo" title="Kun til demobrug" />
                </div>
                <ul id="menu">
                    <li><a href="#.#" title="Simuleret menu kun til demobrug">Menu 01</a></li>
                    <li><a href="#.#" title="Simuleret menu kun til demobrug">Menu 02</a></li>
                    <li><a href="#.#" title="Simuleret menu kun til demobrug">Menu 03</a></li>
                    <li><a href="#.#" title="Simuleret menu kun til demobrug">Menu 04</a></li>
                    <li><a href="#.#" title="Simuleret menu kun til demobrug">Menu 05</a></li>
                </ul>
                <div class="button-wrapper">
                    <a href="../extras/index.jsp" title="Klik her for at rette konfiguration før et forløb">Konfiguration</a>				
                    <a href="restricted/kvittering.jsp" title="Klik her for at prøve log ind-forløbet">Log ind</a>
                    <a href="signer-med-javascript.jsp" id="sign" title="Klik her for at prøve signeringsforløbet">Signér</a>
                </div>
            </div>
        </div>

        <div id="contentWrapper">
            <div id="content">
                <div class="content-block">
                    <h1>Velkommen til TU-variant 01: NemID til privat</h1>
                    <p>Her kan du prøve to forskellige flows - Log ind og signering med NemID til privat. Denne variant af interaktionsdesignet er relevant for jer, der kun har private kunder som brugere.</p>
                    <p>Når du klikker på knappen "Log ind", har du mulighed for at afprøve det flow som jeres brugere vil opleve når de logger ind med NemID til privat (personsignatur) på jeres site.</p>
                    <p>Når du klikker på knappen "Signér", har du mulighed for at afprøve det flow som jeres brugere vil opleve når de skal signere et dokument på jeres site.</p>
					<br /><br />
                    <p>Indstillinger:</p>
					<p>Spærrelistetype: <%= session.getAttribute("revocationCheckType") %> </p>
					<p>Signeringstekst-type: <%= session.getAttribute("signTextFormat") %> </p>
                </div>
                <div class="image-block">
                    <img src="<c:url value='/resources/images/01.jpg'/>" alt="Demobillede" />
                    <img src="<c:url value='/resources/images/02.jpg'/>" alt="Demobillede" />
                </div>
            </div>
        </div>

    </body>
</html>