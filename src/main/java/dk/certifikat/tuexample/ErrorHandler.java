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

import org.openoces.ooapi.certificate.CertificateStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.openoces.ooapi.certificate.FocesCertificate;
import org.openoces.ooapi.certificate.MocesCertificate;
import org.openoces.ooapi.certificate.OcesCertificate;
import org.openoces.ooapi.certificate.PocesCertificate;
import org.openoces.ooapi.certificate.VocesCertificate;

/**
 * This class is responsible for translating error codes from the applet to human-readable text.
 */
public class ErrorHandler {
	/** Map error-code to descriptive text. Content is read from <em>error-codes.properties</em>. */
	private static Properties errorCodes;
	static {
		InputStream errorCodesPropertiesStream = null;
		try {
			errorCodesPropertiesStream = ErrorHandler.class.getResourceAsStream("/error-codes.properties");
			errorCodes = new Properties();
			errorCodes.load(errorCodesPropertiesStream);
		} catch (IOException e) {
			throw new RuntimeException("Could not read error code properties file", e);
		} finally {
			if (errorCodesPropertiesStream!=null) {
				try { errorCodesPropertiesStream.close(); } catch (IOException e) {}
			}
		}
	}

	/**
	 * Translates the supplied error code to a descriptive message to be shown to the end-user.
	 * 
	 * @param result Error code from the applet.
	 * @return Descriptive message.
	 */
	public static String getErrorText(String result) {
		final String errorText = errorCodes.getProperty(result.toUpperCase()+".text");
		if (errorText == null) {
			return result;
		}
		return errorText;
	}

    /**
     * Translates the certificate status
     *
     * @param status the <code>CertificateStatus</code>
     * @return Descriptive message.
     */
    public static String getCertificateStatusText(CertificateStatus status) {
		final String certificateStatusText = errorCodes.getProperty("certificate." + status.getName());
		if (certificateStatusText == null) {
			return status.getName();
		}
		return certificateStatusText;
    }


	/**
	 * Translates the supplied error code to a descriptive message to be logged.
	 * 
	 * @param result Error code from the applet.
	 * @return Descriptive message.
	 */
	public static Object getTechnicalDescription(String result) {
		final String technicalDescription = errorCodes.getProperty(result.toUpperCase()+".description");
		if (technicalDescription == null) {
			return result;
		}
		return technicalDescription;
	}

    public static String getCertificateType(OcesCertificate certificate) {
        if (certificate instanceof PocesCertificate) {
            return errorCodes.getProperty("certificateTypes.poces");
        }
        if (certificate instanceof MocesCertificate) {
            return errorCodes.getProperty("certificateTypes.moces");
        }
        if (certificate instanceof FocesCertificate) {
            return errorCodes.getProperty("certificateTypes.foces");
        }
        if (certificate instanceof VocesCertificate) {
            return errorCodes.getProperty("certificateTypes.voces");
        }
        return errorCodes.getProperty("certificateTypes.unknown");
    }
}