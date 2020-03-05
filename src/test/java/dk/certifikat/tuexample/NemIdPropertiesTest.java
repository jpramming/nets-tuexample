package dk.certifikat.tuexample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openoces.ooapi.environment.Environments.Environment;

public class NemIdPropertiesTest {

    @Test
    public void checkAppletParameterSigningKeyPassword() {
        assertEquals("Test1234", NemIdProperties.getAppletParameterSigningKeyPassword());
    }

    @Test
    public void checkAppletParameterSigningKeystorePassword() {
        assertEquals("Test1234", NemIdProperties.getAppletParameterSigningKeystorePassword());
    }

    @Test
    public void checkEnvironmentProperties() {
        assertEquals(Environment.OCESII_DANID_ENV_DEVELOPMENTTEST, NemIdProperties.getOces2Environment());
    }
}
