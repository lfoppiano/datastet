package org.grobid.core.utilities;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class XMLUtilitiesTest {

    private Element parseRoot(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));
        return doc.getDocumentElement();
    }

    @Test
    public void testHasTEISentenceSegmentation_inTextBody() throws Exception {
        String xml = """
            <TEI>
              <text>
                <body>
                  <div>
                    <p><s>This is a sentence.</s></p>
                  </div>
                </body>
              </text>
            </TEI>
        """;
        Element root = parseRoot(xml);
        assertTrue(XMLUtilities.hasTEISentenceSegmentation(root));
    }

    @Test
    public void testHasTEISentenceSegmentation_inTeiHeaderAbstract() throws Exception {
        String xml = """
            <TEI>
              <teiHeader>
                <abstract>
                  <p><s>Abstract sentence.</s></p>
                </abstract>
              </teiHeader>
            </TEI>
        """;
        Element root = parseRoot(xml);
        assertTrue(XMLUtilities.hasTEISentenceSegmentation(root));
    }

    @Test
    public void testHasTEISentenceSegmentation_noSegmentation() throws Exception {
        String xml = """
            <TEI>
              <text>
                <body>
                  <div>
                    <p>This is not segmented.</p>
                  </div>
                </body>
              </text>
            </TEI>
        """;
        Element root = parseRoot(xml);
        assertFalse(XMLUtilities.hasTEISentenceSegmentation(root));
    }

    @Test
    public void testHasTEISentenceSegmentation_sOutsideP() throws Exception {
        String xml = """
            <TEI>
              <text>
                <body>
                  <div>
                    <s>This is outside p.</s>
                  </div>
                </body>
              </text>
            </TEI>
        """;
        Element root = parseRoot(xml);
        assertFalse(XMLUtilities.hasTEISentenceSegmentation(root));
    }
}

