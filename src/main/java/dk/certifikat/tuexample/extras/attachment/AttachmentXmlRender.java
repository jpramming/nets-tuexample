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

public class AttachmentXmlRender {

    public String toXml(Attachment... attachments) {
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        builder.append("<attachments>\n");
        for(Attachment attachment : attachments) {
            builder.append(toAttachmentXml(attachment));
        }
        builder.append("</attachments>\n");
        return builder.toString();
    }

    private String toAttachmentXml(Attachment attachment) {
        StringBuilder builder = new StringBuilder();
        builder.append("<attachment>\n");
        builder.append("<title>").append(attachment.getTitle()).append("</title>\n");
        builder.append("<path>").append(attachment.getPath()).append("</path>\n");
        builder.append("<mimeType>").append(attachment.getMimeType()).append("</mimeType>\n");
        builder.append("<size>").append(attachment.getSize()).append("</size>\n");
        builder.append("<hashValue>").append(attachment.getHashValue()).append("</hashValue>\n");
        if(Boolean.TRUE.equals(attachment.getOptional())) {
            builder.append("<optional/>\n");
        }
        builder.append("</attachment>\n");
        return builder.toString();
    }
}
