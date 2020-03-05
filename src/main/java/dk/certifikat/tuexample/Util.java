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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import javax.servlet.ServletContext;

import org.openoces.ooapi.utils.Base64Handler;
import org.openoces.serviceprovider.ServiceProviderException;

public class Util {

    /**
     * Reads document from provided URI, hashes it using provided algorithm and base64 encodes the result
     *
     * @param ctx
     * @param uri
     * @param algorithm
     * @return
     */
    public String getDocumentHash(ServletContext ctx, String uri, String algorithm) {
        InputStream is = ctx.getResourceAsStream(uri);
        return getDocumentHash(is, algorithm);
    }

	public String getDocumentAsBase64String(ServletContext ctx, String path) {
    	InputStream is = ctx.getResourceAsStream(path);
        return  getDocumentAsBase64String(is);
	}

    /**
     * Reads document from input stream, hashes it using provided algorithm and base64 encodes the result
     *
     * @param is
     * @param algorithm
     * @return
     */
    public String getDocumentHash(InputStream is, String algorithm) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm.toUpperCase(), "BC");
            is = new java.security.DigestInputStream(is, md);
            while(is.read()!=-1) {
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            try{
                is.close();
            } catch (Exception e) {
                // never mind
            }

        }
        return Base64Handler.encode(md.digest());
    }

    public String getDocumentAsBase64String(InputStream is) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data = null;
        int i = 0;
        try {
            while((i=is.read())!=-1) {
                bos.write(i);
            }
            data = bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Unable to read document!", e);
        } finally {
            try {
                if(bos!=null)
                    bos.close();
                if(is!=null)
                    is.close();
            } catch (Exception e) {
                // never mind
            }
        }
        return Base64Handler.encode(data);
    }





}
