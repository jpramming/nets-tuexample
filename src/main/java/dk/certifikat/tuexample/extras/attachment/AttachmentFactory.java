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

package dk.certifikat.tuexample.extras.attachment;

import eu.medsea.util.MimeUtil;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Base64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;

public class AttachmentFactory {
    private String hashAlorithm;

    public AttachmentFactory(String hashAlorithm) {
        this.hashAlorithm = hashAlorithm;
    }

    public Attachment create(String title, String attachmentPath, URL attachmentFile, Boolean optional) {
        byte[] content = readAttachment(attachmentFile);
        int size = content.length;
        String hashValue = createHashValue(content);
        String mimeType = getMimeType(attachmentFile);

        return new Attachment(title, attachmentPath, mimeType, size, hashValue, optional);
    }


    private byte[] readAttachment(URL attachment) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            inputStream = attachment.openStream();
            outputStream = new ByteArrayOutputStream();
            int b;
            while ((b = inputStream.read()) != -1) {
                outputStream.write(b);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    private String createHashValue(byte[] data) {
        try {
            return base64Encode(MessageDigest.getInstance(hashAlorithm, "BC").digest(data));
        } catch (Exception e) {
            throw new RuntimeException("Could not calculate digest", e);
        }
    }

    private String base64Encode(byte[] bytes) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new Base64Encoder().encode(bytes, 0, bytes.length, baos);
            return baos.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getMimeType(URL attachment) {
        String file = attachment.getFile();
        return MimeUtil.getMimeType(file);
    }
}
