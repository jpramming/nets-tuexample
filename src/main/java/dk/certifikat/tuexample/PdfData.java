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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

public class PdfData {

    public static final String basicPdf = "JVBERi0xLjIKJeLjz9MKMSAwIG9iaiAKPDwKL1BhZ2VzIDIgMCBSCi9UeXBlIC9DYXRhbG9nCj4+CmVuZG9iaiAKMiAwIG9iaiAKPDwKL0tpZHMgWzMgMCBSXQovQ291bnQgMQovVHlwZSAvUGFnZXMKPj4KZW5kb2JqIAozIDAgb2JqIAo8PAovUGFyZW50IDIgMCBSCi9SZXNvdXJjZXMgCjw8Ci9Gb250IAo8PAovRjEgNCAwIFIKPj4KPj4KL01lZGlhQm94IFswIDAgNjEyIDc5Ml0KL0NvbnRlbnRzIDUgMCBSCi9UeXBlIC9QYWdlCj4+CmVuZG9iaiAKNCAwIG9iaiAKPDwKL0Jhc2VGb250IC9FdmlsCi9TdWJ0eXBlIC9UeXBlMQovRm9udERlc2NyaXB0b3IgCjw8Ci9Gb250TmFtZSAvRXZpbAovU3RlbVYgMAovRm9udEZpbGUgNSAwIFIKL0FzY2VudCAwCi9GbGFncyAyNjIxNzYKL0Rlc2NlbnQgMAovSXRhbGljQW5nbGUgMAovRm9udEJCb3ggWzAgMCAwIDBdCi9UeXBlIC9Gb250RGVzY3JpcHRvcgo+PgovVHlwZSAvRm9udAo+PgplbmRvYmogCjUgMCBvYmogCjw8Ci9MZW5ndGggNDgKPj4Kc3RyZWFtCkJUDQovRjEgOCBUZg0KMTAwIDcwMCBUZA0KKEhlbGxvIHdvcmxkLilUag0KRVQNCgplbmRzdHJlYW0gCmVuZG9iaiB4cmVmCjAgNgowMDAwMDAwMDAwIDY1NTM1IGYgCjAwMDAwMDAwMTUgMDAwMDAgbiAKMDAwMDAwMDA2NiAwMDAwMCBuIAowMDAwMDAwMTI1IDAwMDAwIG4gCjAwMDAwMDAyNTUgMDAwMDAgbiAKMDAwMDAwMDQ3OCAwMDAwMCBuIAp0cmFpbGVyCgo8PAovUm9vdCAxIDAgUgovU2l6ZSA2Cj4+CnN0YXJ0eHJlZgo1NzgKJSVFT0YK";


	public static String getDemoPdf(String filename) throws IOException{
		String path = "/demo/" + filename;
        
        InputStream is = PdfData.class.getResourceAsStream(path);
        if (is == null) {
            return basicPdf;
        }

        try {
            byte[] byteArray = IOUtils.toByteArray(is);
            byte[] encodedBytes = Base64.encodeBase64(byteArray);
            return new String(encodedBytes);
        } finally {
            IOUtils.closeQuietly(is);
        }

	}
}
