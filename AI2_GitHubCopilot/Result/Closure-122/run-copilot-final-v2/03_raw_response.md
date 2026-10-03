package com.google.javascript.jscomp.parsing;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;

 import com.google.common.collect.Lists;
 import com.google.javascript.jscomp.parsing.Config.LanguageMode;
 import com.google.javascript.rhino.JSDocInfo;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.SimpleErrorReporter;
 import com.google.javascript.rhino.Token;
 import com.google.javascript.rhino.head.ErrorReporter;
 import com.google.javascript.rhino.head.Token.CommentType;
 import com.google.javascript.rhino.head.ast.AstRoot;
 import com.google.javascript.rhino.head.ast.Comment;
 import com.google.javascript.rhino.head.ast.ExpressionStatement;
 import com.google.javascript.rhino.head.ast.Name;

 import org.junit.Test;

 import java.util.List;

 /**

 - Tests for {@link IRFactory} focused on comment handling, file-overview JsDoc,
 - and suspicious block comment detection around the parsedComments tracking.
   */
  public class IRFactoryCommentBugTest {

   private static final String TEST_SOURCE = "var x = 1;";

   private ErrorReporter createErrorReporter() {
     return new SimpleErrorReporter(); }

   private Config createConfig() {
     return new Config(LanguageMode.ECMASCRIPT3); }

   private AstRoot createAstRoot() {
     AstRoot root = new AstRoot(0);
     root.setLength(TEST_SOURCE.length());
     return root; }

   /**
    * Helper that invokes transformTree and returns the resulting script Node.
    / private Node transform(AstRoot root, String source, List<Comment> comments) {
     if (comments != null && !comments.isEmpty()) {
       root.setComments(comments);
     }
     Config config = createConfig();
     return IRFactory.transformTree(root, / sourceFile */ null, source, config,
createErrorReporter());
    }

   /**
    * When only a BLOCK_COMMENT type comment is present and it is not JSDOC,
    * handleBlockComment should process it. The IR node should still be a SCRIPT
    * and no file-overview JsDoc should be set (since it is not JSDOC).
    / @Test public void testBlockCommentWithoutJsDoc() {
     AstRoot root = createAstRoot();
     List<Comment> comments = Lists.newArrayList();
     Comment blockComment = new Comment(0, 10, CommentType.BLOCK_COMMENT, "/ just a block */");
     comments.add(blockComment);

  Node script = transform(root, "/* just a block */ var a = 1;", comments);
  assertNotNull(script);
  assertEquals(Token.SCRIPT, script.getType());
  // BLOCK_COMMENT without @fileoverview should not produce fileoverview JsDoc.
  assertFalse(script.getBooleanProp(Node.IS_FILE_OVERVIEW_JSDOC));
 }

   /**
    * A JSDOC comment that contains @fileoverview at the start of the file
    * should result in the script node having the fileoverview flag set.
    / @Test public void testFileOverviewJsDocAtScriptStart() {
     AstRoot root = createAstRoot();
     List<Comment> comments = Lists.newArrayList();
     Comment jsdoc = new Comment(0, 30, CommentType.JSDOC,
         "/* @fileoverview describes this file */");
     comments.add(jsdoc);

  Node script = transform(root, "/** @fileoverview describes this file */ var a = 1;", comments);
  assertNotNull(script);
  // After setFileOverviewJsDoc, the script node should carry the flag.
  assertTrue(script.getBooleanProp(Node.IS_FILE_OVERVIEW_JSDOC));
 }

   /**
    * A JSDOC comment that does NOT contain @fileoverview should not
    * set the fileoverview flag on the script node.
    / @Test public void testJsDocWithoutFileOverview() {
     AstRoot root = createAstRoot();
     List<Comment> comments = Lists.newArrayList();
     Comment jsdoc = new Comment(0, 20, CommentType.JSDOC,
         "/* @param {string} x */");
     comments.add(jsdoc);

  Node script = transform(root, "/** @param {string} x */ function f(x){}", comments);
  assertNotNull(script);
  assertFalse(script.getBooleanProp(Node.IS_FILE_OVERVIEW_JSDOC));
 }

   /**
    * Multiple JSDOC comments: only the first one that contains @fileoverview
    * should cause the flag to be set. The second should not override a missing
    * fileoverview (parsedComments must correctly track which were consumed).
    / @Test public void testMultipleJsDocOnlyFirstFileOverview() {
     AstRoot root = createAstRoot();
    List<Comment> comments = Lists.newArrayList();
     Comment first = new Comment(0, 35, CommentType.JSDOC,
         "/* @fileoverview initial overview /");
     Comment second = new Comment(36, 20, CommentType.JSDOC,
         "/* @param {number} n */");
     comments.add(first);
     comments.add(second);

  Node script = transform(root,
      "/** @fileoverview initial overview */ /** @param {number} n */ var n = 1;", comments);
  assertNotNull(script);
  assertTrue("Script should carry fileoverview from first comment",
      script.getBooleanProp(Node.IS_FILE_OVERVIEW_JSDOC));
 }

   /**
    * A suspicious block comment (e.g., a JSDOC-style comment that looks like a
    * fileoverview but appears in a non-top-level position) should still be
    * handled through the parsedComments filtering. This test verifies that
    * parsedComments does not cause such a comment to be silently dropped.
    *
    * Bug 1037: comments of type JSDOC that are already in parsedComments were
    * incorrectly bypassed and never reached handleBlockComment for suspicious
    * pattern detection.
    / @Test public void testSuspiciousJsDocNotDroppedFromProcessing() {
     AstRoot root = createAstRoot();
     List<Comment> comments = Lists.newArrayList();
     // This JSDOC looks like a fileoverview but appears after code –
     // handlePossibleFileOverviewJsDoc should still examine it.
     Comment suspicious = new Comment(20, 40, CommentType.JSDOC,
         "/* @fileoverview this looks suspicious here */");
     comments.add(suspicious);

  // Add an expression so the AST is not empty and the comment position
  // suggests it is not at the very top.
  ExpressionStatement stmt = new ExpressionStatement(
      new Name(10, "x"), 10);
  stmt.setLength(1);
  root.addChildToBack(stmt);

  Node script = transform(root,
      "var x = 1; /** @fileoverview this looks suspicious here */", comments);
  assertNotNull(script);
  // The suspicious comment should still be processed through
  // handlePossibleFileOverviewJsDoc because it is JSDOC type.
  assertTrue("Fileoverview JsDoc in non-leading position should still set flag",
      script.getBooleanProp(Node.IS_FILE_OVERVIEW_JSDOC));
 }

   /**
    * When a JSDOC comment is present but has already been consumed during
    * transform() (i.e., is in parsedComments), it should NOT be
    * double-processed. This test verifies parsedComments deduplication.
    / @Test public void testAlreadyParsedJsDocNotReprocessed() {
     AstRoot root = createAstRoot();
     List<Comment> comments = Lists.newArrayList();
     // A JSDOC comment that looks like it could be attached to a child node.
     Comment attachedJsdoc = new Comment(0, 25, CommentType.JSDOC,
         "/* @type {number} */");
     comments.add(attachedJsdoc);

  // Add a child node so transform() has something to attach the JSDOC to.
  Name nameNode = new Name(26, "x");
  ExpressionStatement stmt = new ExpressionStatement(nameNode, 26);
  stmt.setLength(1);
  root.addChildToBack(stmt);

  Node script = transform(root,
      "/** @type {number} */ var x = 1;", comments);
  assertNotNull(script);
  // This JSDOC was already parsed via the var node; it should not
  // accidentally set the file-overview flag.
  assertFalse("Already-parssed JSDOC should not set fileoverview",
     script.getBooleanProp(Node.IS_FILE_OVERVIEW_JSDOC));
 }

   /**
    * A comment of type LINE_COMMENT (single-line) should be ignored by both
    * handleBlockComment and handlePossibleFileOverviewJsDoc.
    */ @Test public void testLineCommentIgnored() {
     AstRoot root = createAstRoot();
     List<Comment> comments = Lists.newArrayList();
     Comment lineComment = new Comment(0, 10, CommentType.LINE_COMMENT, "// fileoverview");
     comments.add(lineComment);

  Node script = transform(root, "/ / fileoverview\nvar a = 1;", comments);
  assertNotNull(script);
  // LINE_COMMENT should never trigger fileoverview or block-comment logic.
  assertFalse(script.getBooleanProp(Node.IS_FILE_OVERVIEW_JSDOC));
 }

   /**
    * A BLOCK_COMMENT type comment that is not JSDOC but contains
    * suspicious @fileoverview-like content should go through
    * handleBlockComment for warning emission.
    / @Test public void testPlainBlockCommentWithSuspiciousContent() {
     AstRoot root = createAstRoot();
     List<Comment> comments = Lists.newArrayList();
     Comment plainBlock = new Comment(0, 30, CommentType.BLOCK_COMMENT,
         "/ @fileoverview in plain block */");
     comments.add(plainBlock);

  Node script = transform(root,
      "/* @fileoverview in plain block */ var b = 2;", comments);
  assertNotNull(script);
  // Plain block comment should be routed to handleBlockComment,
  // not to handlePossibleFileOverviewJsDoc, so no fileoverview flag.
  assertFalse("Plain block comment should not set fileoverview flag",
      script.getBooleanProp(Node.IS_FILE_OVERVIEW_JSDOC));
 }

   /**
    * An empty comments list should cause no processing and the script node
    * should have no fileoverview flag.
    */ @Test public void testNoComments() {
     AstRoot root = createAstRoot();
     // Add a child to make the AST non-empty.
     Name nameNode = new Name(0, "y");
     ExpressionStatement stmt = new ExpressionStatement(nameNode, 0);
     stmt.setLength(1);
     root.addChildToBack(stmt);

  Node script = transform(root, "var y = 2;", null);
  assertNotNull(script);
  assertFalse("No comments means no fileoverview flag",
      script.getBooleanProp(Node.IS_FILE_OVERVIEW_JSDOC));
 }

   /**
    * Multiple JSDOC comments where the second contains @fileoverview but the
    * first does not. The fileoverview flag should still be set because
    * handlePossibleFileOverviewJsDoc processes unparsed JSDOC comments.
    *
    * This exercises the parsedComments filtering in the comment loop:
    * the first JSDOC (no @fileoverview) may or may not be consumed during
    * transform(); the second should be processed in the post-transform loop.
    / @Test
    public void testSecondJsDocContainsFileOverview() {
     AstRoot root = createAstRoot();
     List<Comment> comments = Lists.newArrayList();
     Comment first = new Comment(0, 20, CommentType.JSDOC,
         "/* @param {string} s /");
     Comment second = new Comment(35, 35, CommentType.JSDOC,
          "/* @fileoverview entry point */");
     comments.add(first);
     comments.add(second);

  Node script = transform(root,
      "/** @param {string} s */ var s; /** @fileoverview entry point */", comments);
  assertNotNull(script);
  assertTrue("Second JSDOC with @fileoverview should set flag",
      script.getBooleanProp(Node.IS_FILE_OVERVIEW_JSDOC));
 }

   /**
    * A script with no comments and no AST children produces a valid SCRIPT
    * node with no fileoverview JsDoc.
    */ @Test public void testEmptyScript() {
     AstRoot root = createAstRoot();
     Node script = transform(root, "", null);
     assertNotNull(script);
     assertEquals(Token.SCRIPT, script.getType());
     assertFalse(script.getBooleanProp(Node.IS_FILE_OVERVIEW_JSDOC));
    }

   /**
    * Mixed comment types: JSDOC with @fileoverview, BLOCK_COMMENT without, and
    * LINE_COMMENT. Only the JSDOC should set the fileoverview flag.
    / @Test public void testMixedCommentTypes() {
     AstRoot root = createAstRoot();
     List<Comment> comments = Lists.newArrayList();
     Comment jsdoc = new Comment(0, 25, CommentType.JSDOC,
         "/* @fileoverview mixed /");
     Comment block = new Comment(26, 15, CommentType.BLOCK_COMMENT,
         "/ plain block */");
     Comment line = new Comment(50, 12, CommentType.LINE_COMMENT,
         "// a note");
     comments.add(jsdoc);
     comments.add(block);
     comments.add(line);

  Node script = transform(root,
      "/** @fileoverview mixed */ /* plain block */\n// a note\nvar z = 3;", comments);
  assertNotNull(script);
  assertTrue("JSDOC @fileoverview must set flag even with other comment types",
      script.getBooleanProp(Node.IS_FILE_OVERVIEW_JSDOC));
 }

 }