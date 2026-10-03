package org.apache.commons.jxpath.ri;

import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**

 - Tests targeting bug JXPATH-97:
 - NamespaceResolver and DOMNodePointer fail to cooperate when
 - external namespace registrations are used during attribute creation.
  */
 public class NamespaceResolverBugTest extends TestCase {

 // ---------- NamespaceResolver tests ----------

 public void testRegisterAndGetNamespaceURI() {
     NamespaceResolver resolver = new NamespaceResolver();
     resolver.registerNamespace("A", "http://example.com");
     assertEquals("http://example.com", resolver.getNamespaceURI("A"));
     assertNull(resolver.getNamespaceURI("B"));
 }

 public void testRegisterOverwrite() {
     NamespaceResolver resolver = new NamespaceResolver();
     resolver.registerNamespace("A", "uri1");
     resolver.registerNamespace("A", "uri2");
     assertEquals("uri2", resolver.getNamespaceURI("A"));
 }

 public void testParentResolverFallback() {
     NamespaceResolver parent = new NamespaceResolver();
     parent.registerNamespace("A", "parentUri");
     NamespaceResolver child = new NamespaceResolver(parent);
     assertEquals("parentUri", child.getNamespaceURI("A"));
     // child's own registration shadows parent
     child.registerNamespace("A", "childUri");
     assertEquals("childUri", child.getNamespaceURI("A"));
 }

 public void testClonePreservesAndIsolates() {
     NamespaceResolver resolver = new NamespaceResolver();
     resolver.registerNamespace("A", "uri");
     NamespaceResolver clone = (NamespaceResolver) resolver.clone();

     assertEquals("uri", clone.getNamespaceURI("A"));

     clone.registerNamespace("A", "newUri");
     assertEquals("newUri", clone.getNamespaceURI("A"));
     assertEquals("uri", resolver.getNamespaceURI("A")); // original unchanged
 }

 public void testSealPreventsRegister() {
     NamespaceResolver resolver = new NamespaceResolver();
     resolver.seal();
     try {
         resolver.registerNamespace("A", "uri");
         fail("Should have thrown IllegalStateException");
     } catch (IllegalStateException e) {
         // expected
     }
 }

 public void testGetPrefix() {
     NamespaceResolver resolver = new NamespaceResolver();
     resolver.registerNamespace("A", "http://example.com");
     assertEquals("A", resolver.getPrefix("http://example.com"));
     assertNull(resolver.getPrefix("urn:unknown"));
 }

 public void testGetPrefixViaParent() {
     NamespaceResolver parent = new NamespaceResolver();
     parent.registerNamespace("A", "http://example.com");
     NamespaceResolver child = new NamespaceResolver(parent);
     assertEquals("A", child.getPrefix("http://example.com"));
 }

 public void testNamespaceResolverUsesPointer() throws Exception {
     // When a prefix is not in the local map, the resolver should fall back
     // to the associated pointer (set via setNamespaceContextPointer).
     Document doc = createDocument();
     Element root = doc.createElement("root");
     root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:foo", "http://foo.com");
     doc.appendChild(root);

     DOMNodePointer pointer = new DOMNodePointer(root, Locale.getDefault());
     NamespaceResolver resolver = new NamespaceResolver();
     resolver.setNamespaceContextPointer(pointer);

     // 'foo' not registered locally; must be resolved by the pointer
     assertEquals("http://foo.com", resolver.getNamespaceURI("foo"));
 }

 // ---------- createAttribute / integration tests ----------

 private Document createDocument() throws Exception {
     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
     factory.setNamespaceAware(true);
     return factory.newDocumentBuilder().newDocument();
 }

 /** External namespace registered on context must be usable for attribute creation. */
 public void testCreateAttributeWithRegisteredPrefix() throws Exception {
     Document doc = createDocument();
     Element root = doc.createElement("root");
     doc.appendChild(root);

     JXPathContext context = JXPathContext.newContext(doc);
     context.registerNamespace("A", "http://example.com");

     // Should succeed – the buggy version throws "Unknown namespace prefix: A"
     try {
         context.setValue("/root/@A:attr", "value");
     } catch (JXPathException e) {
         fail("Registered prefix should not cause JXPathException: " + e.getMessage());
     }

     assertEquals("value", root.getAttributeNS("http://example.com", "attr"));
 }

 /** Unregistered prefix must throw JXPathException. */
 public void testCreateAttributeWithUnregisteredPrefix() throws Exception {
     Document doc = createDocument();
     Element root = doc.createElement("root");
     doc.appendChild(root);

     JXPathContext context = JXPathContext.newContext(doc);
     // "B" is not registered anywhere
     try {
         context.setValue("/root/@B:attr", "value");
         fail("Expected JXPathException for unknown namespace prefix");
     } catch (JXPathException e) {
         assertTrue(e.getMessage().indexOf("Unknown namespace prefix: B") >= 0);
     }
 }

 /** Attribute creation without a namespace prefix must work. */
 public void testCreateAttributeWithoutPrefix() throws Exception {
     Document doc = createDocument();
     Element root = doc.createElement("root");
     doc.appendChild(root);

     JXPathContext context = JXPathContext.newContext(doc);
     context.setValue("/root/@attr", "value");
     assertEquals("value", root.getAttribute("attr"));
 }

}