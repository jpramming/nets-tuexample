package dk.certifikat.tuexample;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EncryptionHelperTest {

    @Before
    public void setup() {
    }

    @Test
    public void encryptAndDecryptPassword() throws Exception {
        EncryptionHelper instance = EncryptionHelper.getInstance();
        assertNotNull(instance);

        String password = "mypassword";

        assertEquals(password, instance.decrypt(instance.encrypt(password)));
    }
}
