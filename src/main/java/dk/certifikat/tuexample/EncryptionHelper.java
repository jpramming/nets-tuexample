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

import org.bouncycastle.util.encoders.Base64Encoder;

import java.io.ByteArrayOutputStream;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * This is a simple helper that makes helps encrypting and decrypting passwords.
 * 
 * This is used so that we do not keep passwords as plain text in property files. 
 * 
 * If needed - use you're own encrypting implementation for passwords in the property file. 
 */
public class EncryptionHelper {

    private String algorithm = "AES";
    private SecretKeySpec keySpec = null;
    private byte[] key = "Nets Solutions 1".getBytes();

    private Cipher cipher = null;

    private static EncryptionHelper encrypter;

    public EncryptionHelper() {
        try {
            cipher = Cipher.getInstance(algorithm);
            keySpec = new SecretKeySpec(key, algorithm);
        } catch (Exception e){
            throw new RuntimeException("Unable to initialize EncryptionHelper", e);
        }
    }

    public String encrypt(String input) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] inputBytes = input.getBytes();
            byte[] outputBytes = cipher.doFinal(inputBytes);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            (new Base64Encoder()).encode(outputBytes, 0, outputBytes.length, baos);
            return baos.toString();
        } catch (Exception e){
            throw new RuntimeException("Unable to encrypt", e);
        }
    }

    public String decrypt(String passphrase) {

        try {
            ByteArrayOutputStream certStream = new ByteArrayOutputStream();
            (new Base64Encoder()).decode(passphrase, certStream);
            byte[] encryptionBytes = certStream.toByteArray();

            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] recoveredBytes = cipher.doFinal(encryptionBytes);
            return new String(recoveredBytes);
        } catch (Exception e) {
            throw new RuntimeException("Unable to decrypt", e);
        }
    }

    public static EncryptionHelper getInstance() {
        if (encrypter == null) {
                encrypter = new EncryptionHelper();
        }
        return encrypter;
    }

    public static void main(String[] args) {
        EncryptionHelper instance = EncryptionHelper.getInstance();
        System.out.println(instance.encrypt("Test1234"));
    }
}
