<%@ page import="dk.certifikat.tuexample.NemIdProperties" %><%--

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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="content-script-type" content="text/javascript" />
<meta http-equiv="X-UA-Compatible" content="IE=edge"> 
<meta name="description" content="" />
<meta name="keywords" content="" />
<link rel="stylesheet" type="text/css" href="<c:url value='/resources/css/global.css'/>" media="all" />
<script type="text/javascript" src="<c:url value='/resources/js/jquery-3.5.1.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/jquery.cookie.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/common.js'/>"></script>
<script type="text/javascript">
    $( document ).ready(function() {
        $('<div/>').addClass('tu_version').text('<%=NemIdProperties.getTuVersion()%>').appendTo($('body'));
    });
</script>