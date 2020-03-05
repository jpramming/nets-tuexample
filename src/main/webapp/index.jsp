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
<%@ page pageEncoding="UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="da" lang="da">
    <head>
        <title>TU-interaktionsdesign - Indeks Privat</title>
        <%@ include file="/WEB-INF/jsp-include/common-head.jsp" %>
    </head>
    <body id="frontpage">

        <div id="skipToContent" class="accessibility">
            <dl>
                <dt>Genveje:</dt>
                <dd><a href="#content" title="Gå direkte til hovedindhold" accesskey="2">Hovedindhold</a></dd>
            </dl>
        </div>

        <div id="contentWrapper">
            <div id="content">
                <h1>Velkommen til demo af interaktionsdesignet</h1>
                <p>Her på siden har du mulighed for at afprøve hvilken af de tre forskellige tjenesteudbydervarianter af interaktionsdesignet der passer bedst til jeres site, og typen af brugere der logger ind hos jer.</p>
                <p>De tre forløb som du kan prøve, har identiske brugergrænseflader, men hjælpetekster, log-in og signeringsappletter er specifikke for de enkelte scenarier.
                I hvert scenarie har du mulighed for at gennemføre både log-in og signering, og derigennem få en oplevelse der vil svare til den dine brugere vil få når de logger ind eller signerer et dokument på dit site.</p>
                <ul>
                    <li>
                        <h2>TU-variant 01: NemID til privat</h2>
                        <p>TU-variant 01 henvender sig til tjenesteudbydere der ønsker at deres brugere kan logge ind og signere med NemID til privat (personsignatur).</p>
                        <a href="variant1/" class="btn" title="Klik her for at prøve NemID til privat-demo">Prøv demo</a>
                    </li>
                    <li>
                        <h2>TU-variant 02: NemID til erhverv</h2>
                        <p>TU-variant 02 henvender sig til tjenesteudbydere der ønsker at deres brugere kan logge ind og signere med NemID til erhverv (medarbejdersignatur).</p>
                        <a href="variant2/" class="btn" title="Klik her for at prøve NemID til erhverv-demo">Prøv demo</a>
                    </li>
                    <li>
                        <h2>TU-variant 03: NemID</h2>
                        <p>TU-variant 03 henvender sig til tjenesteudbydere der ønsker at deres brugere kan logge ind og signere med både NemID til privat (personsignatur) og NemID til erhverv (medarbejdersignatur).</p>
                        <a href="variant3/" class="btn" title="Klik her for at prøve NemID-demo">Prøv demo</a>
                    </li>
                </ul>
            </div>
        </div>
    </body>
</html>
