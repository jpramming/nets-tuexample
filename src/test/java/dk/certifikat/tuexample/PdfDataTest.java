package dk.certifikat.tuexample;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class PdfDataTest {

    @Test
    public void canFindExistingPdf() throws Exception {
        String pdf = PdfData.getDemoPdf("nemid_termer.pdf");
        assertNotSame(PdfData.basicPdf, pdf);
    }

    @Test
    public void failsToFindNotExistingPdfAndReturnsEmptyPdfInstead() throws Exception {
        String pdf = PdfData.getDemoPdf("not_existing.pdf");
        assertEquals(PdfData.basicPdf, pdf);
    }

}
