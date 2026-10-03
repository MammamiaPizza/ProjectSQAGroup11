package org.jsoup.nodes;

 import static org.junit.Assert.*;

 import org.junit.Test;

 /**
  * Tests for {@link Comment} focusing on XML declaration parsing and boundary conditions
  * to prevent IndexOutOfBoundsException (bug #1139).
  */
 public class CommentTest {

     @Test
     public void testEmptyComment() {
         Comment comment = new Comment("");
         assertFalse("Empty string should not be XML declaration", comment.isXmlDeclaration());
         assertNull("Empty string should not parse as XML declaration", comment.asXmlDeclaration());
         assertEquals("", comment.getData());
     }

     @Test
     public void testExclamationOnly() {
         Comment comment = new Comment("!");
         assertFalse("Single exclamation mark should not be XML declaration",
comment.isXmlDeclaration());
         // asXmlDeclaration() must not throw -- length > 1 guard in isXmlDeclaration doesn't
protect this path
         assertNull(comment.asXmlDeclaration());
     }

     @Test
     public void testQuestionOnly() {
         Comment comment = new Comment("?");
         assertFalse("Single question mark should not be XML declaration",
comment.isXmlDeclaration());
         assertNull(comment.asXmlDeclaration());
     }

     @Test
     public void testShortContentLengthOne() {
         Comment comment = new Comment("a");
         assertFalse("Length 1 content should not be XML declaration", comment.isXmlDeclaration());
         assertNull(comment.asXmlDeclaration());
     }

     @Test
     public void testXmlDeclarationWithQuestion() {
         Comment comment = new Comment("?xml version=\"1.0\" encoding=\"UTF-8\"?");
         assertTrue("Question-prefixed XML declaration should be recognized",
comment.isXmlDeclaration());
         XmlDeclaration decl = comment.asXmlDeclaration();
         assertNotNull("Should parse as XML declaration", decl);
     }

     @Test
     public void testXmlDeclarationWithQuestionAndAttributes() {
         Comment comment = new Comment("?xml version=\"1.0\"?>");
         assertTrue(comment.isXmlDeclaration());
         XmlDeclaration decl = comment.asXmlDeclaration();
         assertNotNull("Should produce a declaration, not null", decl);
     }

     @Test
     public void testXmlDeclarationWithExclamation() {
         Comment comment = new Comment("!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"");
         assertTrue("Exclamation-prefixed should be recognized as XML declaration",
comment.isXmlDeclaration());
         XmlDeclaration decl = comment.asXmlDeclaration();
         assertNotNull(decl);
     }

     @Test
     public void testHtmlCommentMarkerShouldNotThrow() {
         // "<!--" prefix -- the substring call in asXmlDeclaration must not cause
IndexOutOfBoundsException
         Comment comment = new Comment("<!-- some comment");
         assertTrue("starts with !", comment.isXmlDeclaration());
         // The critical test: must not throw IndexOutOfBoundsException
         XmlDeclaration decl = comment.asXmlDeclaration();
         // Declaration may or may not be produced, but no exception is the key
         // We just assert no exception occurred
     }

     @Test
     public void testExclamationWithNoContentShouldNotThrow() {
         // data starts with "!" but substring(1, data.length()-1) gives empty range when length is
small
         Comment comment = new Comment("!!");
         assertTrue(comment.isXmlDeclaration());
         // Must not throw IndexOutOfBoundsException
         XmlDeclaration decl = comment.asXmlDeclaration();
         assertNotNull(decl);
     }

     @Test
     public void testInvalidXmlContentShouldNotThrow() {
         Comment comment = new Comment("?invalid");
         assertTrue(comment.isXmlDeclaration());
         XmlDeclaration decl = comment.asXmlDeclaration();
         // Should not throw; result may be null or valid, but no exception is the contract
     }

     @Test
     public void testStringConstructor() {
         Comment comment = new Comment("test data");
         assertEquals("#comment", comment.nodeName());
         assertEquals("test data", comment.getData());
         assertFalse(comment.isXmlDeclaration());
         assertNull(comment.asXmlDeclaration());
     }

     @Test
     public void testStringBaseUriConstructor() {
         Comment comment = new Comment("test", "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         assertEquals("#comment", comment.nodeName());
         assertEquals("test", comment.getData());
         assertFalse(comment.isXmlDeclaration());
     }
 }