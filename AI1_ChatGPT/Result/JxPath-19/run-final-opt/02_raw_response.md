import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.jdom.Element;
import org.jdom.Namespace;
import org.w3c.dom.Document;

public class AliasedNamespacePathRegressionTest extends TestCase {

    private static final String NAMESPACE_URI = "urn:jxpath:aliased-namespace";
    private static final String SOURCE_PREFIX = "source";
    private static final String ALIAS_PREFIX = "a";

    public void testDOMAliasedNamespaceSiblingPointersHaveDistinctPositions()
            throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document = factory.newDocumentBuilder().newDocument();

        org.w3c.dom.Element root = document.createElementNS(
                NAMESPACE_URI, SOURCE_PREFIX + ":doc");
        root.setAttributeNS("http://www.w3.org/2000/xmlns/",
                "xmlns:" + SOURCE_PREFIX, NAMESPACE_URI);
        root.appendChild(document.createElementNS(
                NAMESPACE_URI, SOURCE_PREFIX + ":elem"));
        root.appendChild(document.createElementNS(
                NAMESPACE_URI, SOURCE_PREFIX + ":elem"));
        document.appendChild(root);

        JXPathContext context = JXPathContext.newContext(document);
        context.registerNamespace(ALIAS_PREFIX, NAMESPACE_URI);

        Iterator pointers = context.iteratePointers("/a:doc/a:elem");

        assertTrue(pointers.hasNext());
        assertEquals("/a:doc[1]/a:elem[1]",
                ((Pointer) pointers.next()).asPath());

        assertTrue(pointers.hasNext());
        assertEquals("/a:doc[1]/a:elem[2]",
                ((Pointer) pointers.next()).asPath());

        assertFalse(pointers.hasNext());
    }

    public void testJDOMAliasedNamespaceSiblingPointersHaveDistinctPositions() {
        Namespace sourceNamespace = Namespace.getNamespace(
                SOURCE_PREFIX, NAMESPACE_URI);
        Element root = new Element("doc", sourceNamespace);
        root.addContent(new Element("elem", sourceNamespace));
        root.addContent(new Element("elem", sourceNamespace));
        org.jdom.Document document = new org.jdom.Document(root);

        JXPathContext context = JXPathContext.newContext(document);
        context.registerNamespace(ALIAS_PREFIX, NAMESPACE_URI);

        Iterator pointers = context.iteratePointers("/a:doc/a:elem");

        assertTrue(pointers.hasNext());
        assertEquals("/a:doc[1]/a:elem[1]",
                ((Pointer) pointers.next()).asPath());

        assertTrue(pointers.hasNext());
        assertEquals("/a:doc[1]/a:elem[2]",
                ((Pointer) pointers.next()).asPath());

        assertFalse(pointers.hasNext());
    }
}