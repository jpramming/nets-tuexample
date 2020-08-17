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

import dk.certifikat.tuexample.extras.attachment.Attachment;
import dk.certifikat.tuexample.extras.attachment.AttachmentFactory;
import dk.certifikat.tuexample.extras.attachment.AttachmentXmlRender;
import org.apache.commons.codec.binary.Base64;
import org.openoces.ooapi.utils.Base64Handler;
import org.openoces.ooapi.web.Signer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Goal of this class is to prepare a HTML/JavaScript code needed to embed a CodeFile client within a JSP page.
 */
public class CodeFileClientGenerator {

    private Signer signer;
    private static final String CODEFILE_PARAMETER_TAG_ID = "codefile_parameters";
    private static final String PARAM_SIGNTEXT="SIGNTEXT";
    private static final String PARAM_SIGN_TEXT_FORMAT="SIGNTEXT_FORMAT";
    private static final String PARAM_SIGNTEXT_TRANSFORMATION="SIGNTEXT_TRANSFORMATION";
    private static final String PARAM_ADDITIONAL_PARAMS="ADDITIONAL_PARAMS";

    public CodeFileClientGenerator() {
        this.signer = getSigner();
    }

    /**
     * Generates parameters to be used by CodeFile client for logon flow.
     *
     * @param request
     * @param subjectdnfilter end user certificate filter.
     *                        Only certificates matching this filter are made available to the end user.
     *                        "PID:" - private person certificates only.
     *                        "RID:" - corporate certificates only.
     *                        empty - all certificates are acceptable.
     *
     * @return html script tag
     */
    public String generateLogonParameters(HttpServletRequest request, String subjectdnfilter){
        StringBuffer additionalParameters=new StringBuffer();
        List<Parameter> params = getCommonParameters(request, additionalParameters, subjectdnfilter);
        params.add(new ParameterImpl("CLIENTFLOW", ClientFlow.CODEFILE_LOGIN.toString()));
        return generateParametersTag(finaliseParameters(params, additionalParameters));
    }

    /**
     * Generates parameters to be used by CodeFile client for signing flow.
     *
     * It is expected, that signTextFormat session attribute value is set to one of the known document types (else defaulted to html).
     *
     * Content of non binary documents (plain text, html and xml) is retrieved from a file system and becomes part of
     * signing transaction parameters (stored as value of signtext parameter).
     *
     * Content of binary documents (pdfs) and any type of attachments is not delivered to CodeFile client via parameters.
     * Their hash values and a URIs are delivered instead. CodeFile retrieves such documents via provided URI
     * (protocol, hostname and port are delivered to CodeFile by the CodeFile browser extension).
     *
     * @param request
     * @param subjectdnfilter end user certificate filter.
     *                        Only certificates matching this filter are made available to the end user.
     *                        "PID:" - private person certificates only.
     *                        "RID:" - corporate certificates only.
     *                        empty - all certificates are acceptable.
     *
     * @return html script tag
     */
    public String generateSigningParameters(HttpServletRequest request, String subjectdnfilter){
        HttpSession session = request.getSession();
        StringBuffer additionalParameters=new StringBuffer();
        List<Parameter> params = getCommonParameters(request, additionalParameters, subjectdnfilter);

        params.add(new ParameterImpl("CLIENTFLOW", ClientFlow.CODEFILE_SIGN.toString()));

        String signTextFormat = (String) session.getAttribute(AbstractSignServlet.SIGN_TEXT_FORMAT);

        if (signTextFormat == null || signTextFormat.length() == 0){
            signTextFormat = "html";

            // to be used later on by AbstractSignServlet for transaction validation
            session.setAttribute(AbstractSignServlet.SIGN_TEXT_FORMAT, "html");
        }

        switch (signTextFormat){
            case "txt" : {
                params.add(new ParameterImpl(PARAM_SIGN_TEXT_FORMAT, "text"));
                params.add(new ParameterImpl(PARAM_SIGNTEXT, SignConstants.SIGN_TEXT, true));

                // to be used by AbstractSignServlet for transaction validation
                session.setAttribute(AbstractSignServlet.SIGN_TEXT, SignConstants.SIGN_TEXT);
                break;
            }
            case "html" : {
                params.add(new ParameterImpl(PARAM_SIGN_TEXT_FORMAT, "html"));
                params.add(new ParameterImpl(PARAM_SIGNTEXT, SignConstants.SIGN_HTML, true));

                // to be used by AbstractSignServlet for transaction validation
                session.setAttribute(AbstractSignServlet.SIGN_TEXT, SignConstants.SIGN_HTML);
                break;
            }
            case "xml" : {
                params.add(new ParameterImpl(PARAM_SIGN_TEXT_FORMAT, "xml"));
                params.add(new ParameterImpl(PARAM_SIGNTEXT, SignConstants.SIGN2_XML, true));
                params.add(new ParameterImpl(PARAM_SIGNTEXT_TRANSFORMATION, SignConstants.SIGN2_XSLT, true));

                // to be used by AbstractSignServlet for transaction validation
                session.setAttribute(AbstractSignServlet.SIGN_TEXT, SignConstants.SIGN2_XML);
                session.setAttribute(AbstractSignServlet.SIGN_TEXT_TRANSFORMATION, Base64Handler.encode(SignConstants.SIGN2_XSLT));
                break;
            }
            case "pdf_w_attachment": {
                // reference to an attachment file on file system. Will use this to calculate hash
                URL urlToFileOnFileSystem = null;
                try {
                    urlToFileOnFileSystem = request.getSession().getServletContext().getResource(SignConstants.PLAIN_FILE_URI);
                } catch (MalformedURLException e){
                    throw new RuntimeException("Invalid provided URI: " + SignConstants.PLAIN_FILE_URI);
                }

                // reference to same attachment file via HTTP. CodeFile client will use this URI to load an attachment
                String uriToFileViaHttp = request.getContextPath() + SignConstants.PLAIN_FILE_URI;

                Attachment attachment = new AttachmentFactory("SHA256").create("Almindelig tekst bilag", uriToFileViaHttp, urlToFileOnFileSystem, Boolean.FALSE);
                addToAdditionalParam(additionalParameters, "attachments", Base64Handler.encode(new AttachmentXmlRender().toXml(attachment)));

                /*
                Note! Execution does not stop here.  It continues to "pdf" case and break then.
                In this case we are attaching an plain text document to a PDF as an illustration, but attachments can be added other types of documents as well.
                 */
            }
            case "pdf": {
                Util util=new Util();
                params.add(new ParameterImpl(PARAM_SIGN_TEXT_FORMAT, "pdf"));
                String documentHash = util.getDocumentHash(request.getSession().getServletContext().getResourceAsStream(SignConstants.PDF_URI_NEMID_TERMER), "sha-256");
                params.add(new ParameterImpl(PARAM_SIGNTEXT, util.getDocumentAsBase64String(request.getSession().getServletContext().getResourceAsStream(SignConstants.PDF_URI_NEMID_TERMER))));

                // The following is to support legacy OpenSign applet, it will be removed in future release, but only when we are sure that noone uses the java fallback anymore
                addToAdditionalParam(additionalParameters, "signtext.uri", request.getContextPath() + SignConstants.PDF_URI_NEMID_TERMER);
                addToAdditionalParam(additionalParameters, "signtext.uri.hash.value", documentHash);
                addToAdditionalParam(additionalParameters, "signtext.uri.hash.algorithm", "sha-256");

                // to be used by AbstractSignServlet for transaction validation
                session.setAttribute(AbstractSignServlet.SIGN_TEXT, new Util().getDocumentAsBase64String(request.getSession().getServletContext(), SignConstants.PDF_URI_NEMID_TERMER));

                break;
            }
            case "default": {
                throw new IllegalArgumentException("Unsupported document type: " + signTextFormat);
            }
        }
        return generateParametersTag(finaliseParameters(params, additionalParameters));
    }

    private Signer getSigner(){

        String url = NemIdProperties.getAppletParameterSigningKeystore();
        String keyStorePw = NemIdProperties.getAppletParameterSigningKeystorePassword();
        String alias = NemIdProperties.getAppletParameterSigningKeystoreAlias();
        String keyPw = NemIdProperties.getAppletParameterSigningKeyPassword();

        return new Signer(url, keyStorePw, alias, keyPw);
    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        long millis = System.currentTimeMillis();
        return sdf.format(new Date(millis));
    }


    private List<Parameter> getCommonParameters(HttpServletRequest request, StringBuffer additionalParameters, String subjectdnfilter){
        List<Parameter> params = new ArrayList<Parameter>();

        params.add(new ParameterImpl("SP_CERT", getSigner().getCertificate()));
        params.add(new ParameterImpl("TIMESTAMP", getTimestamp(), true));
        params.add(new ParameterImpl("SIGN_PROPERTIES", "challenge=" + ChallengeGenerator.generateChallenge(request.getSession())));
        params.add(new ParameterImpl("REQUESTISSUER", NemIdProperties.getServiceProviderName(), true));
        params.add(new ParameterImpl("LANGUAGE","da"));
        params.add(new ParameterImpl("ORIGIN", AuthClientUtils.generateOrigin(request), true));
        params.add(new ParameterImpl("TU_VERSION", NemIdProperties.getTuVersion()));
        addToAdditionalParam(additionalParameters, "subjectdnfilter", Base64Handler.encode(subjectdnfilter));
        return params;
    }

    private void addToAdditionalParam(StringBuffer additionalParams, String key, String value) {
        additionalParams.append(((additionalParams.length()>0) ? ";" : "") + key + "=" + value);
    }

    private List<Parameter> finaliseParameters(List<Parameter> params, StringBuffer additionalParams){
        if (null!=additionalParams && additionalParams.length()>0) {
            params.add(new ParameterImpl(PARAM_ADDITIONAL_PARAMS, additionalParams.toString(), true));
        }
        String normalisedParameters = ParameterImpl.normalize(params);
        params.add(new ParameterImpl("PARAMS_DIGEST", ParameterImpl.computeDigest (normalisedParameters)));
        params.add(new ParameterImpl("DIGEST_SIGNATURE", computeSignature(normalisedParameters)));
        return params;
    }

    private String computeSignature(String normalizedParameters) {
        byte[] signedBytes = signer.calculateSignature(normalizedParameters.getBytes());
        return new String(Base64.encodeBase64(signedBytes));
    }

    private String generateParametersTag(List<Parameter> params){
        String parameters = ParameterImpl.toJson(params).toString();
        return MessageFormat.format(NemIdProperties.getCodeFileParameters(), CODEFILE_PARAMETER_TAG_ID, parameters);
    }

    /**
     * Generates java script code for handling communication with CodeFile client during logon flow
     *
     * @return html script tag
     */
    public String generateLogonJs(){
        return MessageFormat.format(NemIdProperties.getCodeFileLogonJs(), NemIdProperties.getCodeFileIframeOrigin(), Base64Handler.encode("ok"));
    }

    /**
     * Generates java script code for handling communication with CodeFile client during signing flow
     *
     * @return html script tag
     */
    public String generateSigningJs(){
        return MessageFormat.format(NemIdProperties.getCodeFileSignJs(), NemIdProperties.getCodeFileIframeOrigin(), Base64Handler.encode("ok"));
    }

    /**
     * Generates an an iframe CodeFile client to be loaded in
     *
     * @return html iframe tag
     */
    public String generateIframe(){
        return MessageFormat.format(NemIdProperties.getCodeFileIframe(), NemIdProperties.getCodeFileIframeSrc() + "?t=" + System.currentTimeMillis());
    }

    /**
     * Generates a form which gets submitted upon a callback from CodeFile iframe
     *
     * @param action    uri of the servlet to take care of the CodeFile client response data for logon or sign flows
     * @return html form tag
     */
    public String generateForm(String action){
        return MessageFormat.format(NemIdProperties.getCodeFileForm(), action);
    }


}
