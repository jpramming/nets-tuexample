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
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="da" lang="da">
    <head>
        <title>TU-interaktionsdesign - RID-opslag</title>
        <%@ include file="/WEB-INF/jsp-include/common-head.jsp" %>
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
                    <a href="index.jsp" title="Klik her for at komme til konfiguration">Konfiguration</a>
                 </div>
            </div>
        </div>

        <div id="contentWrapper">
            <div id="content">
                <div class="content-block">
                    <h2>RID Cpr-opslag</h2>
                    <p>Indtast et RID-nummer og check hvilket CPR der er knyttet til.
                    <form method="post" action="ridcprlookup.html">
                        <p><label for="rid">RID-nummer:</label> 
						<input type="text" name="rid" id="rid" size="30" value="<%= request.getParameter("rid") != null ? request.getParameter("rid") : ""%>"/><small>(Format: CVR:12345678-RID:12345678)</small></p>
                        <input type="submit" value="Check RID"/>
                    </form>
                    <% if (request.getAttribute("cpr") != null) { %>
                        <h1 style="color: green;">RID er tilknyttet CPR: <%= request.getAttribute("cpr") %></h1>
                    <% } else if(request.getParameter("rid") != null) { %>
                        <h1 style="color: red;">Der blev ikke fundet et CPR til det indtastede RID</h1>
                        <p>Årsag: <%=  session.getAttribute("errorText") %></p>
                    <% }%>
                </div>
                <div class="image-block">
                    <img src="../resources/images/01.jpg" alt="Demobillede" />
                    <img src="../resources/images/02.jpg" alt="Demobillede" />
                </div>
            </div>
        </div>
    </body>
</html>