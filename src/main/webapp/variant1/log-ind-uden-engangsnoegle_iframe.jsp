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
<%@ page import="dk.certifikat.tuexample.*" %>
<%@ page pageEncoding="UTF-8" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="da" lang="da">
    <head>
        <title>TU-interaktionsdesign</title>
        <%@ include file="/WEB-INF/jsp-include/common-head.jsp" %>

        <c:set var="parameterElementName" scope="page" value="nemid_nonotp_parameters"/>
        <script type="text/javascript">
        $().ready(function() {
            // Read cookie
            var preferredLogin = $.cookie('preferredLogin');

            // Set 'remember' checkbox according to preferredLogin cookie
            if( preferredLogin == 'Software') {
                $('#rememberSoftware').attr('checked', true);
            }

            // Set preferredLogin cookie
            $('#rememberSoftware').click(function(){
                if($('#rememberSoftware').is(':checked')) {
                    $.cookie('preferredLogin', 'Software', { expires: 365, path: '/' });
                } else {
                    $.cookie('preferredLogin', '', { expires: 365, path: '/' });
                }
            });
        });
        </script>

        <% CodeFileClientGenerator codeFileClientGenerator = new CodeFileClientGenerator();%>
        <%=codeFileClientGenerator.generateLogonParameters(request, "PID:")%>
        <%=codeFileClientGenerator.generateLogonJs()%>

    </head>
	
    <body>
        <div id="skipToContent" class="accessibility">
            <dl>
                <dt>Genveje:</dt>
                <dd><a href="#tabs" title="Gå direkte til loginvalg" accesskey="1">Loginvalg</a></dd>
                <dd><a href="#content" title="Gå direkte til hovedindhold" accesskey="2">Hovedindhold</a></dd>
            </dl>
        </div>

        <!-- container for header section -->
        <div id="headerWrapper">
            <div id="header">
                <ul id="tabs">
                    <li>
                        <a href="log-ind-med-javascript.jsp" accesskey="4">
                            <span class="nemidLogo"><!-- nemidLogo--></span><span>Log ind med nøglekort</span>
                        </a>
                    </li>
                    <li class="selected">
                        <a href="log-ind-uden-engangsnoegle_iframe.jsp" accesskey="6">
                            <span class="nemidLogo"><!-- nemidLogo--></span><span>Log ind med nøglefil</span>
                        </a>
                    </li>
                </ul>
            </div>
        </div>

        <div id="contentWrapper">
            <div id="content">
                <div class="tools">
                    <ul>
                        <li id="speaker"><a href="http://adgangforalle.dk" rel="external" title="Få teksten læst op - link til Adgangforalle.dk (åbner i nyt vindue)"><span class="accessibility">Oplæsning af tekst</span></a></li>
                    </ul>
                </div>
                <div id="loginWrapper">
                    <div id="appPlaceholder" class="wide">

                        <%=codeFileClientGenerator.generateForm("logon.html")%>

                            <fieldset>
                                <legend>Log ind uden engangsnøgle</legend>
                                <dl>
                                    <dd>

                                        <%=codeFileClientGenerator.generateIframe()%>

                                    </dd>
                                </dl>
                                <p class="remember">
                                    <input type="checkbox" name="rememberSoftware" id="rememberSoftware" />
                                    <label for="rememberSoftware">Husk jeg vil logge ind med nøglefil</label>
                                </p>
                            </fieldset>

                    </div>
                    <div id="right-col">
                        <ul class="thumbs">
                            <li>
                                <span><img style="margin-top: 4px;" src="<c:url value='/resources/images/icons/Noglefil_brudt_lille.png'/>" alt="Log ind med nøglefil" title="Log ind med nøglefil" /></span>
                                <span><img style="margin-left: 40px;" src="<c:url value='/resources/images/icons/Noglefil_hardware_lille.png'/>" alt="Log ind med nøglefil på hardware" title="Log ind med nøglefil på hardware" /></span>
                            </li>
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
    </body>
</html>