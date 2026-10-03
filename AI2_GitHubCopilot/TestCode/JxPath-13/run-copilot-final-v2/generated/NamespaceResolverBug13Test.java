package org.apache.commons.jxpath.ri.model;

 import java.util.Locale;

 import javax.xml.parsers.DocumentBuilderFactory;

 import org.apache.commons.jxpath.JXPathContext;
 import org.apache.commons.jxpath.JXPathException;
 import org.apache.commons.jxpath.ri.NamespaceResolver;
 import org.apache.commons.jxpath.ri.QName;
 import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;

 import junit.framework.TestCase;

 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 import org.w3c.dom.Node;

 /**
  * Tests for bug JXPATH-97: NamespaceResolver should traverse the parent chain
  * when resolving a prefix for attribute creation via DOMNodePointer.createAttribute.
  */
 public class NamespaceResolverBug13Test extends TestCase {

     private static final String NS_URI = "http://example.com/ns";
     private static final String PREFIX = "A";

     private Document doc;
     private Element rootElement;
     private Element childElement;
     private NamespaceResolver parentResolver;
     private NamespaceResolver childResolver;
     private JXPathContext context;

     protected void setUp() throws Exception {
         super.setUp();
         // Build a simple XML document with a namespace declaration on the root
         doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
         rootElement = doc.createElementNS(NS_URI, PREFIX + ":root");
         rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + PREFIX, NS_URI);
         doc.appendChild(rootElement);

         childElement = doc.createElementNS(NS_URI, PREFIX + ":child");
         rootElement.appendChild(childElement);

         context = JXPathContext.newContext(doc);
     }

     /**
      * Register prefix in parent resolver; child resolver inherits it.
      * Creating an attribute with that prefix on a child element must succeed.
      */
     public void testCreateAttributeWithPrefixFromParentResolver() {
         parentResolver = new NamespaceResolver();
         parentResolver.registerNamespace(PREFIX, NS_URI);

         childResolver = new NamespaceResolver(parentResolver);

         DOMNodePointer childPointer = new DOMNodePointer(null, childElement);
         childPointer.setNamespaceResolver(childResolver);

         QName attrName = new QName(PREFIX, "attr");
         // This must not throw
         NodePointer result = childPointer.createAttribute(context, attrName);
         assertNotNull(result);
         Node attrNode = (Node) result.getImmediateNode();
         assertEquals("attr", attrNode.getLocalName());
         assertEquals(NS_URI, attrNode.getNamespaceURI());
     }

     /**
      * Prefix registered in the same (non-parented) resolver works normally.
      */
     public void testCreateAttributeWithPrefixInSameResolver() {
         childResolver = new NamespaceResolver();
         childResolver.registerNamespace(PREFIX, NS_URI);

         DOMNodePointer childPointer = new DOMNodePointer(null, childElement);
         childPointer.setNamespaceResolver(childResolver);

         QName attrName = new QName(PREFIX, "attr");
         NodePointer result = childPointer.createAttribute(context, attrName);
         assertNotNull(result);
         Node attrNode = (Node) result.getImmediateNode();
         assertEquals(NS_URI, attrNode.getNamespaceURI());
     }

     /**
      * Prefix not registered anywhere must throw JXPathException.
      */
     public void testCreateAttributeWithUnregisteredPrefixThrows() {
         childResolver = new NamespaceResolver();
         DOMNodePointer childPointer = new DOMNodePointer(null, childElement);
         childPointer.setNamespaceResolver(childResolver);

         QName attrName = new QName("unregistered", "attr");
         try {
             childPointer.createAttribute(context, attrName);
             fail("Expected JXPathException for unknown namespace prefix");
         } catch (JXPathException expected) {
             assertTrue(expected.getMessage().contains("Unknown namespace prefix"));
         }
     }

     /**
      * Null prefix (no namespace) creates an attribute without namespace.
      */
     public void testCreateAttributeWithNullPrefix() {
         childResolver = new NamespaceResolver();
         DOMNodePointer childPointer = new DOMNodePointer(null, childElement);
         childPointer.setNamespaceResolver(childResolver);

         QName attrName = new QName((String) null, "attr");
         NodePointer result = childPointer.createAttribute(context, attrName);
         assertNotNull(result);
         Node attrNode = (Node) result.getImmediateNode();
         assertNull(attrNode.getNamespaceURI());
         assertEquals("attr", attrNode.getNodeName());
     }

     /**
      * Empty prefix creates an attribute without namespace.
      */
     public void testCreateAttributeWithEmptyPrefix() {
         childResolver = new NamespaceResolver();
         DOMNodePointer childPointer = new DOMNodePointer(null, childElement);
         childPointer.setNamespaceResolver(childResolver);

         QName attrName = new QName("", "attr");
         NodePointer result = childPointer.createAttribute(context, attrName);
         assertNotNull(result);
         Node attrNode = (Node) result.getImmediateNode();
         assertNull(attrNode.getNamespaceURI());
     }

     /**
      * getNamespaceURI on child resolver delegates to parent when prefix is not
      * registered locally.
      */
     public void testGetNamespaceURIDelegatesToParent() {
         parentResolver = new NamespaceResolver();
         parentResolver.registerNamespace(PREFIX, NS_URI);

         childResolver = new NamespaceResolver(parentResolver);
         assertEquals(NS_URI, childResolver.getNamespaceURI(PREFIX));
     }

     /**
      * getNamespaceURI returns null for unregistered prefix even with parent.
      */
     public void testGetNamespaceURIReturnsNullForUnknownPrefix() {
         parentResolver = new NamespaceResolver();
         parentResolver.registerNamespace(PREFIX, NS_URI);

         childResolver = new NamespaceResolver(parentResolver);
         assertNull(childResolver.getNamespaceURI("unknown"));
     }

     /**
      * Sealed parent resolver still returns its registered namespace URI.
      */
     public void testSealedParentReturnsNamespaceURI() {
         parentResolver = new NamespaceResolver();
         parentResolver.registerNamespace(PREFIX, NS_URI);
         parentResolver.seal();

         childResolver = new NamespaceResolver(parentResolver);
         assertEquals(NS_URI, childResolver.getNamespaceURI(PREFIX));
     }

     /**
      * Registering namespace on a sealed resolver throws IllegalStateException.
      */
     public void testRegisterOnSealedResolverThrows() {
         childResolver = new NamespaceResolver();
         childResolver.seal();
         try {
             childResolver.registerNamespace(PREFIX, NS_URI);
             fail("Expected IllegalStateException");
         } catch (IllegalStateException expected) {
             // expected
         }
     }

     /**
      * Parent-sealed resolver is also sealed.
      */
     public void testChildOfSealedParentIsNotSealed() {
         parentResolver = new NamespaceResolver();
         parentResolver.seal();
         childResolver = new NamespaceResolver(parentResolver);
         assertFalse(childResolver.isSealed());
     }

     /**
      * Deep hierarchy: prefix registered in grandparent, child can create attribute.
      */
     public void testCreateAttributeWithPrefixFromGrandparentResolver() {
         NamespaceResolver grandparent = new NamespaceResolver();
         grandparent.registerNamespace(PREFIX, NS_URI);

         parentResolver = new NamespaceResolver(grandparent);
         childResolver = new NamespaceResolver(parentResolver);

         DOMNodePointer childPointer = new DOMNodePointer(null, childElement);
         childPointer.setNamespaceResolver(childResolver);

         QName attrName = new QName(PREFIX, "attr");
         NodePointer result = childPointer.createAttribute(context, attrName);
         assertNotNull(result);
         assertEquals(NS_URI, ((Node) result.getImmediateNode()).getNamespaceURI());
     }

     /**
      * Locally registered prefix takes precedence over parent.
      */
     public void testLocalPrefixOverridesParent() {
         String parentNS = "http://parent.example.com/ns";
         parentResolver = new NamespaceResolver();
         parentResolver.registerNamespace(PREFIX, parentNS);

         childResolver = new NamespaceResolver(parentResolver);
         childResolver.registerNamespace(PREFIX, NS_URI);

         assertEquals(NS_URI, childResolver.getNamespaceURI(PREFIX));
     }
 }
