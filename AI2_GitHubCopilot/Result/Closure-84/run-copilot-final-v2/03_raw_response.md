package com.google.javascript.jscomp.parsing;

 import com.google.javascript.jscomp.mozilla.rhino.ErrorReporter;
 import com.google.javascript.jscomp.mozilla.rhino.EvaluatorException;
 import com.google.javascript.jscomp.mozilla.rhino.ast.*;
 import com.google.javascript.rhino.Node;
 import junit.framework.TestCase;

 import java.util.ArrayList;
 import java.util.List;

 /**
  * Tests for {@link IRFactory} focused on destructuring assignment validation.
  * The buggy version fails to report errors for forbidden destructuring patterns
  * such as ({a}) = 1, ([a]) = 1, etc.
  */
 public class IRFactoryDestructuringTest extends TestCase {

     // ==================== HELPER CLASSES ====================

     private static final class CapturingErrorReporter implements ErrorReporter {
         private final List<String> errors = new ArrayList<String>();

         @Override
         public void error(String msg, String src, int line, String lineSrc, int off) {
             errors.add(msg);
         }

         @Override
         public void warning(String msg, String src, int line, String lineSrc, int off) {
             // not used in these tests
         }

         @Override
         public EvaluatorException runtimeError(String msg, String src, int line,
                                                String lineSrc, int off) {
             return new EvaluatorException(msg, src, line, lineSrc, off);
         }

         boolean hasErrors() {
             return !errors.isEmpty();
         }

         int errorCount() {
             return errors.size();
         }
     }

     // ==================== HELPER METHODS ====================

     private static final int ASSIGN =
         com.google.javascript.jscomp.mozilla.rhino.Token.ASSIGN;

     private static AstRoot root() {
         AstRoot r = new AstRoot(0);
         r.setSourceName("test.js");
         return r;
     }

     private static CapturingErrorReporter reporter() {
         return new CapturingErrorReporter();
     }

     private static Node transformAst(AstRoot root, CapturingErrorReporter rep) {
         return IRFactory.transformTree(root, "", null, rep);
     }

     private static Name name(String id, int line) {
         Name n = new Name();
         n.setIdentifier(id);
         n.setLineno(line);
         return n;
     }

     private static NumberLiteral num(double v, int line) {
         NumberLiteral n = new NumberLiteral();
         n.setValue(v);
         n.setLineno(line);
         return n;
     }

     private static ObjectLiteral objectLit(int line, String... names) {
         ObjectLiteral o = new ObjectLiteral();
         o.setLineno(line);
         for (String id : names) {
             o.addChildToBack(name(id, line));
         }
         return o;
     }

     private static ArrayLiteral arrayLit(int line, String... names) {
         ArrayLiteral a = new ArrayLiteral();
         a.setLineno(line);
         for (String id : names) {
             a.addChildToBack(name(id, line));
         }
         return a;
     }

     private static ParenthesizedExpression paren(AstNode expr, int line) {
         ParenthesizedExpression p = new ParenthesizedExpression();
         p.setLineno(line);
         p.setExpression(expr);
         return p;
     }

     private static Assignment assignment(AstNode left, AstNode right, int op, int line) {
         Assignment a = new Assignment();
         a.setLineno(line);
         a.setOperator(op);
         a.addChildToBack(left);
         a.addChildToBack(right);
         return a;
     }

     private static AstRoot singleExprStmt(AstNode expr) {
         AstRoot r = root();
         ExpressionStatement stmt = new ExpressionStatement(expr);
         stmt.setLineno(1);
         r.addChildToBack(stmt);
         return r;
     }

     private static VariableDeclaration varDecl(AstNode target, AstNode init, int line) {
         VariableDeclaration vd = new VariableDeclaration();
         vd.setLineno(line);
         VariableInitializer vi = new VariableInitializer();
         vi.setLineno(line);
         vi.setTarget(target);
         if (init != null) {
             vi.setInitializer(init);
         }
         vd.addChildToBack(vi);
         return vd;
     }

     private static ForInLoop forIn(AstNode iterator, AstNode iterObj, int line) {
         ForInLoop f = new ForInLoop();
         f.setLineno(line);
         f.setIterator(iterator);
         f.setIteratedObject(iterObj);
         Block body = new Block();
         body.setLineno(line);
         f.setBody(body);
         return f;
     }

     // ==================== TESTS ====================

     /**
      * ({a}) = 1  -- parenthesized object literal LHS is forbidden destructuring.
      * The buggy version does NOT report an error; this test reveals the defect.
      */
     public void testObjectLiteralInAssignmentForbidden() {
         ObjectLiteral objLit = objectLit(1, "a");
         ParenthesizedExpression p = paren(objLit, 1);
         Assignment a = assignment(p, num(1.0, 1), ASSIGN, 1);
         AstRoot root = singleExprStmt(a);
         CapturingErrorReporter rep = reporter();
         transformAst(root, rep);
         assertTrue("Must report error for ({a}) = 1", rep.hasErrors());
     }

     /**
      * ([a]) = 1  -- parenthesized array literal LHS is forbidden destructuring.
      */
     public void testArrayLiteralInAssignmentForbidden() {
         ArrayLiteral arrLit = arrayLit(1, "a");
         ParenthesizedExpression p = paren(arrLit, 1);
         Assignment a = assignment(p, num(1.0, 1), ASSIGN, 1);
         AstRoot root = singleExprStmt(a);
         CapturingErrorReporter rep = reporter();
         transformAst(root, rep);
         assertTrue("Must report error for ([a]) = 1", rep.hasErrors());
     }

     /**
      * ({a, b}) = 1 -- multi-property object destructuring forbidden.
      */
     public void testMultiPropertyObjectLiteralForbidden() {
         ObjectLiteral objLit = objectLit(1, "a", "b");
         ParenthesizedExpression p = paren(objLit, 1);
         Assignment a = assignment(p, num(1.0, 1), ASSIGN, 1);
         AstRoot root = singleExprStmt(a);
         CapturingErrorReporter rep = reporter();
         transformAst(root, rep);
         assertTrue("Must report error for multi-property destructuring", rep.hasErrors());
     }

     /**
      * ({a: [b]}) = 1 -- nested destructuring (object containing array) forbidden.
      */
     public void testNestedDestructuringForbidden() {
         ObjectLiteral inner = new ObjectLiteral();
         inner.setLineno(1);
         ArrayLiteral arr = arrayLit(1, "b");
         ObjectProperty prop = new ObjectProperty();
         prop.addChildToBack(name("a", 1));
         prop.addChildToBack(arr);
         inner.addChildToBack(prop);
         ParenthesizedExpression p = paren(inner, 1);
         Assignment a = assignment(p, num(1.0, 1), ASSIGN, 1);
         AstRoot root = singleExprStmt(a);
         CapturingErrorReporter rep = reporter();
         transformAst(root, rep);
         assertTrue("Must report error for nested destructuring", rep.hasErrors());
     }

     /**
      * ({a}) = ({b}) = 1 -- chained destructuring forbidden.
      */
     public void testChainedDestructuringForbidden() {
         ObjectLiteral objLitB = objectLit(1, "b");
         ParenthesizedExpression pB = paren(objLitB, 1);
         Assignment inner = assignment(pB, num(1.0, 1), ASSIGN, 1);
         ObjectLiteral objLitA = objectLit(1, "a");
         ParenthesizedExpression pA = paren(objLitA, 1);
         Assignment outer = assignment(pA, inner, ASSIGN, 1);
         AstRoot root = singleExprStmt(outer);
         CapturingErrorReporter rep = reporter();
         transformAst(root, rep);
         assertTrue("Must report error for chained destructuring", rep.hasErrors());
     }

     /**
      * a = 1 -- simple name assignment is allowed (no destructuring).
      */
     public void testSimpleAssignmentAllowed() {
         Assignment a = assignment(name("a", 1), num(1.0, 1), ASSIGN, 1);
         AstRoot root = singleExprStmt(a);
         CapturingErrorReporter rep = reporter();
         transformAst(root, rep);
         assertFalse("No error expected for a = 1", rep.hasErrors());
     }

     /**
      * (a) = 1 -- parenthesized name is legal, not destructuring.
      */
     public void testParenthesizedNameAssignmentAllowed() {
         ParenthesizedExpression p = paren(name("a", 1), 1);
         Assignment a = assignment(p, num(1.0, 1), ASSIGN, 1);
         AstRoot root = singleExprStmt(a);
         CapturingErrorReporter rep = reporter();
         transformAst(root, rep);
         assertFalse("No error expected for (a) = 1", rep.hasErrors());
     }

     /**
      * var {a} = obj -- destructuring in variable declaration is allowed.
      */
     public void testDestructuringVarDeclAllowed() {
         AstRoot root = root();
         ObjectLiteral objLit = objectLit(1, "a");
         VariableDeclaration vd = varDecl(objLit, name("obj", 1), 1);
         root.addChildToBack(vd);
         CapturingErrorReporter rep = reporter();
         transformAst(root, rep);
         assertFalse("No error expected for var {a} = obj", rep.hasErrors());
     }

     /**
      * var [a] = obj -- array destructuring in variable declaration allowed.
      */
     public void testArrayDestructuringVarDeclAllowed() {
         AstRoot root = root();
         ArrayLiteral arrLit = arrayLit(1, "a");
         VariableDeclaration vd = varDecl(arrLit, name("obj", 1), 1);
         root.addChildToBack(vd);
         CapturingErrorReporter rep = reporter();
         transformAst(root, rep);
         assertFalse("No error expected for var [a] = obj", rep.hasErrors());
     }

     /**
      * for (var {a} in obj) {} -- destructuring in for-in header allowed.
      */
     public void testDestructuringForInAllowed() {
         AstRoot root = root();
         VariableDeclaration vd = varDecl(objectLit(1, "a"), null, 1);
         ForInLoop loop = forIn(vd, name("obj", 1), 1);
         root.addChildToBack(loop);
         CapturingErrorReporter rep = reporter();
         transformAst(root, rep);
         assertFalse("No error expected for for-in destructuring", rep.hasErrors());
     }

     /**
      * for (var [a] in obj) {} -- array destructuring in for-in header allowed.
      */
     public void testArrayDestructuringForInAllowed() {
         AstRoot root = root();
         VariableDeclaration vd = varDecl(arrayLit(1, "a"), null, 1);
         ForInLoop loop = forIn(vd, name("obj", 1), 1);
         root.addChildToBack(loop);
         CapturingErrorReporter rep = reporter();
         transformAst(root, rep);
         assertFalse("No error expected for for-in array destructuring", rep.hasErrors());
     }

     /**
      * Sanity: an expression statement (non-assignment) produces no error.
      */
     public void testFunctionCallNoError() {
         AstRoot root = root();
         FunctionCall call = new FunctionCall();
         call.setLineno(1);
         call.addChildToBack(name("foo", 1));
         call.addChildToBack(name("bar", 1));
         ExpressionStatement stmt = new ExpressionStatement(call);
         stmt.setLineno(1);
         root.addChildToBack(stmt);
         CapturingErrorReporter rep = reporter();
         transformAst(root, rep);
         assertFalse("No error expected for function call", rep.hasErrors());
     }
 }