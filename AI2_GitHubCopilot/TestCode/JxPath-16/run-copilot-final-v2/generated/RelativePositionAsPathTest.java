package org.apache.commons.jxpath.ri.model;

 import java.util.Locale;
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;

 import junit.framework.TestCase;

 import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;
 import org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer;
 import org.jdom.Element;
 import org.jdom.Text;
 import org.w3c.dom.Document;
 import org.w3c.dom.Node;

 /**
  * Tests for relative position and asPath() methods in
  * DOMNodePointer and JDOMNodePointer, targeting JXPATH-114.
  * The bug caused attribute/namespace children to be incorrectly
  * counted or sibling ordering to be wrong, leading to wrong
  * path suffixes for following and preceding axis results.
  */
 public class RelativePositionAsPathTest extends TestCase {

     /**
      * Verify that an empty DOM element's asPath ends with "[]"
      * (no children), even if the element has no child nodes.
      */
     public void testDOMEmptyElementPath() throws Exception {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document doc = db.newDocument();
         org.w3c.dom.Element root = doc.createElement("root");
         doc.appendChild(root);
         org.w3c.dom.Element child = doc.createElement("empty");
         root.appendChild(child);

         DOMNodePointer ptr = new DOMNodePointer(child, (Locale) null);
         String path = ptr.asPath();
         assertTrue("Path should end with empty[1][] but was: " + path,
                 path.endsWith("empty[1][]"));
     }

     /**
      * DOM element with attribute but no child should still
      * produce a leaf path with "[]", not include the attribute
      * as a child.
      */
     public void testDOMElementWithAttributePath() throws Exception {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document doc = db.newDocument();
         org.w3c.dom.Element root = doc.createElement("root");
         doc.appendChild(root);
         org.w3c.dom.Element child = doc.createElement("child");
         child.setAttribute("attr", "val");
         root.appendChild(child);

         DOMNodePointer ptr = new DOMNodePointer(child, (Locale) null);
         String path = ptr.asPath();
         // path should be something like /root[1]/child[1][@attr='val'][]
         // at least it must not contain another element child.
         assertTrue("Path should end with child index and brackets, "
                 + "but was: " + path,
                 path.contains("child[1]") && path.endsWith("[]"));
     }

     /**
      * Text node inside an element: asPath must be text()[index]
      * where index counts only text siblings, ignoring elements.
      */
     public void testDOMTextNodePath() throws Exception {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document doc = db.newDocument();
         org.w3c.dom.Element root = doc.createElement("root");
         doc.appendChild(root);
         org.w3c.dom.Text text1 = doc.createTextNode("first");
         root.appendChild(text1);
         org.w3c.dom.Element child = doc.createElement("child");
         root.appendChild(child);
         org.w3c.dom.Text text2 = doc.createTextNode("second");
         root.appendChild(text2);

         // pointer to second text node
         DOMNodePointer ptr = new DOMNodePointer(text2, (Locale) null);
         String path = ptr.asPath();
         assertEquals("Second text node path", "/root[1]/text()[2]", path);
     }

     /**
      * Elements with same name must receive a correct index
      * considering only previous siblings with the same element name.
      */
     public void testDOMElementIndexAmongSameName() throws Exception {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document doc = db.newDocument();
         org.w3c.dom.Element root = doc.createElement("root");
         doc.appendChild(root);
         root.appendChild(doc.createElement("a"));
         root.appendChild(doc.createElement("b"));
         org.w3c.dom.Element a2 = doc.createElement("a");
         root.appendChild(a2);

         DOMNodePointer ptr = new DOMNodePointer(a2, (Locale) null);
         String path = ptr.asPath();
         assertEquals("Second 'a' element path", "/root[1]/a[2][]", path);
     }

     // ---- JDOM counterparts ----

     /**
      * JDOM empty element must also end with "[]".
      */
     public void testJDOMEmptyElementPath() {
         Element root = new Element("root");
         org.jdom.Document doc = new org.jdom.Document(root);
         Element child = new Element("empty");
         root.addContent(child);

         JDOMNodePointer ptr = new JDOMNodePointer(child, (Locale) null);
         String path = ptr.asPath();
         assertTrue("Path should end with empty[1][] but was: " + path,
                 path.endsWith("empty[1][]"));
     }

     /**
      * JDOM element with attribute but no children should be leaf ([]).
      */
     public void testJDOMElementWithAttributePath() {
         Element root = new Element("root");
         new org.jdom.Document(root);
         Element child = new Element("child");
         child.setAttribute("attr", "val");
         root.addContent(child);

         JDOMNodePointer ptr = new JDOMNodePointer(child, (Locale) null);
         String path = ptr.asPath();
         assertTrue("Path should end with "] and not include child nodes but was: "
                         + path,
                 path.contains("child[1]") && path.endsWith("]"));
         // exact "[@attr='val']" suffix may be present; at minimum it must not
         // contain extra child path after element index.
     }

     /**
      * JDOM text node must have asPath text()[index] where
      * index ignores preceding elements.
      */
     public void testJDOMTextNodePath() {
         Element root = new Element("root");
         new org.jdom.Document(root);
         root.addContent(new Text("first"));
         root.addContent(new Element("child"));
         Text second = new Text("second");
         root.addContent(second);

         JDOMNodePointer ptr = new JDOMNodePointer(second, (Locale) null);
         String path = ptr.asPath();
         assertEquals("Second text node path", "/root[1]/text()[2]", path);
     }

     /**
      * JDOM same-name elements must get monotonically increasing index.
      */
     public void testJDOMElementIndexAmongSameName() {
         Element root = new Element("root");
         new org.jdom.Document(root);
         root.addContent(new Element("a"));
         root.addContent(new Element("b"));
         Element a2 = new Element("a");
         root.addContent(a2);

         JDOMNodePointer ptr = new JDOMNodePointer(a2, (Locale) null);
         String path = ptr.asPath();
         assertEquals("Second 'a' element path", "/root[1]/a[2][]", path);
     }

 }
