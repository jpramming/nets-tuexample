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
<%@ page import="dk.certifikat.tuexample.NemIdProperties"%>
<%@ page import="dk.certifikat.tuexample.OtpClientGenerator"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page import="dk.certifikat.tuexample.ClientFlow"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="da" lang="da">
     <head>
        <title>TU-interaktionsdesign</title>
        <%@ include file="/WEB-INF/jsp-include/common-head.jsp"%>

        <%
            OtpClientGenerator paramGen = new OtpClientGenerator(request, ClientFlow.OCESSIGN2);
            
            try {
                String paramEl = paramGen.getJSElement();
                out.println(paramEl);
            } catch (Exception e){
            }
        %>

        <script>
            function onNemIDMessage(e) {
                var event = e || event;

                var iframeOrigin = "<%=NemIdProperties.getServerUrlPrefix()%>";
                if (iframeOrigin !== event.origin){
                    return;
                }
            
                var win = document.getElementById("nemid_iframe").contentWindow, postMessage = {}, message;
                try { message = JSON.parse(event.data); } catch (e) { return; }
                
                if (message.command === "SendParameters") {
                    var htmlParameters = document.getElementById("nemid_parameters").innerHTML;
            
                    postMessage.command = "parameters";
                    postMessage.content = htmlParameters;
                    
                    win.postMessage(JSON.stringify(postMessage), iframeOrigin);
                }
                
                if (message.command === "changeResponseAndSubmit") {
                    document.postBackForm.response.value = message.content;
                    document.postBackForm.submit();
                }
            }
            
            if (window.addEventListener) {
            window.addEventListener("message", onNemIDMessage);
            } else if (window.attachEvent) {
                window.attachEvent("onmessage", onNemIDMessage);
            }
        </script>
        <script type="text/javascript">
            $().ready(function() {
                // Read cookie
                var preferredLogin = $.cookie('preferredLogin');

                // Set 'remember' checkbox according to preferredLogin cookie
                if( preferredLogin == 'JavaScript') {
                    $('#rememberOtpJS').attr('checked', true);
                }

                // Set preferredLogin cookie
                $('#rememberOtpJS').click(function(){
                    if($('#rememberOtpJS').is(':checked')) {
                        $.cookie('preferredLogin', 'JavaScript', { expires: 365, path: '/' });
                    } else {
                        $.cookie('preferredLogin', '', { expires: 365, path: '/' });
                    }
                });
            });
        </script>
        <script type="text/javascript">                           
            var signatureChunk="";
    		function addChunk(chunk) {
    			signatureChunk = signatureChunk + chunk;
    		}	
    		function allChunk() {
    			onSignOk(signatureChunk);
    		}
    	</script>
    </head>
    <body>
        <div id="skipToContent" class="accessibility">
            <dl>
                <dt>Genveje:</dt>
                <dd>
                    <a href="#tabs" title="Gå direkte til signeringsvalg" accesskey="1">Signeringsvalg</a>
                </dd>
                <dd>
                    <a href="#content" title="Gå direkte til hovedindhold" accesskey="2">Hovedindhold</a>
                </dd>
            </dl>
        </div>
        <!-- container for header section -->
        <div id="headerWrapper">
            <div id="header">
                <ul id="tabs">
                    <li class="selected">
                        <a href="signer-med-javascript.jsp" accesskey="4">
                            <span class="nemidLogo"><!-- nemidLogo--></span><span>Signer med nøglekort</span>
                        </a>
                    </li>
                    <li>
                        <a href="signer-uden-engangsnoegle_iframe.jsp" accesskey="6">
                            <span class="nemidLogo"><!-- nemidLogo--></span><span>Signer med nøglefil</span>
                        </a>
                    </li>
                </ul>
            </div>
        </div>

        <div id="contentWrapper">
            <div id="content">
                <div class="tools">
                    <ul>
                        <li id="speaker">
                            <a href="http://adgangforalle.dk" rel="external" title="Få teksten læst op - link til Adgangforalle.dk (åbner i nyt vindue)">
                                <span class="accessibility">Oplæsning af tekst</span>
                            </a>
                        </li>
                    </ul>
                </div>
                <div id="signerWrapper">
                    <div id="appPlaceholder">
                        <fieldset>
                            <legend>Signer med engangsnøgle</legend>
                            <%@ include file="/extras/common-iframe.jsp"%>
                            <p class="remember">
                                <input type="checkbox" name="rememberOtpJS" id="rememberOtpJS" /> 
                                <label for="rememberOtpJS">Husk jeg vil signere med nøglekort</label>
                            </p>
                        </fieldset>
                    </div>
                    <div id="right-col">
                        <ul class="thumbs">
                            <li><span><img src="<c:url value='/resources/images/icons/Noglekort_lille.png'/>" alt="Nøglekort" title="Nøglekort" /></span></li>
                        </ul>

                        <h2>Genveje</h2>
                        <ul class="linkList">
                            <li><a href="<c:url value='/'/>" title="Klik her for at få hjælp nu">Få hjælp nu</a></li>
                            <li><a href="<c:url value='/'/>" title="Klik her for at bestille NemID">Bestil NemID</a></li>
                            <li><a href="<c:url value='/'/>" title="Klik her hvis du har glemt din adgangskode">Glemt adgangskode</a></li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
        <form name="postBackForm" action="sign.html" method="post"><input type="hidden" name="response" value=""/></form>
        <%@ include file="/WEB-INF/jsp-include/common-tail.jsp"%>
    </body>
</html>