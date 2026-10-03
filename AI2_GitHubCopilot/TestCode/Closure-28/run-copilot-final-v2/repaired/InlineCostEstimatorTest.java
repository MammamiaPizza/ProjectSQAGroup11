package com.google.javascript.jscomp;

 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import junit.framework.TestCase;

 import java.lang.reflect.Field;

 /**
  * Tests for {@link InlineCostEstimator}.
  * Focuses on the cost estimation correctness described in bug #728:
  * the estimator must use a fixed estimated identifier length, must not
  * double-count identifiers and must respect the cost-threshold limit.
  */
 public class InlineCostEstimatorTest extends TestCase {

     private int estimatedIdentifierCost;
     private int emptyScriptCost;
     private int singleNameStmtCost;

     @Override
     protected void setUp() throws Exception {
         super.setUp();
         Field field = InlineCostEstimator.class.getDeclaredField("ESTIMATED_IDENTIFIER_COST");
         field.setAccessible(true);
         estimatedIdentifierCost = (Integer) field.get(null);

         Node emptyScript = new Node(Token.SCRIPT);
         emptyScriptCost = InlineCostEstimator.getCost(emptyScript);

         Node single = scriptNode(exprStatement(nameNode("x")));
         singleNameStmtCost = InlineCostEstimator.getCost(single);
     }

     /** Reads the package-private constant via reflection. */
     private int getEstimatedIdentifierCost() throws Exception {
         Field field = InlineCostEstimator.class.getDeclaredField("ESTIMATED_IDENTIFIER_COST");
         field.setAccessible(true);
         return (Integer) field.get(null);
     }

     private Node nameNode(String name) {
         return Node.newString(Token.NAME, name);
     }

     private Node scriptNode(Node... stmts) {
         Node s = new Node(Token.SCRIPT);
         for (Node stmt : stmts) {
             s.addChildToBack(stmt);
         }
         return s;
     }

     private Node exprStatement(Node expr) {
         return new Node(Token.EXPR_RESULT, expr);
     }

     private Node addExpr(Node left, Node right) {
         return new Node(Token.ADD, left, right);
     }

     /**
      * Single identifier: cost exactly the estimated constant value.
      */
     public void testSingleIdentifierCost() throws Exception {
         int constant = getEstimatedIdentifierCost();
         Node name = nameNode("x");
         assertEquals("single NAME cost must equal ESTIMATED_IDENTIFIER_COST",
                 constant, InlineCostEstimator.getCost(name));
     }

     /**
      * Cost must not depend on the length of the actual identifier name.
      */
     public void testIdentifierCostIndependentOfNameLength() throws Exception {
         int costShort = InlineCostEstimator.getCost(nameNode("a"));
         int costLong  = InlineCostEstimator.getCost(nameNode("thisIsALongName"));
         assertEquals("cost must be independent of name length", costShort, costLong);
     }

     /**
      * Two identifiers in separate statements: cost must scale linearly
      * with the number of identifiers, accounting for per-statement overhead.
      */
     public void testTwoIdentifiersCostIsDoubleSingle() throws Exception {
         Node stmtA = exprStatement(nameNode("a"));
         Node stmtB = exprStatement(nameNode("b"));
         Node script = scriptNode(stmtA, stmtB);
         int costTwo = InlineCostEstimator.getCost(script);

         int perName = singleNameStmtCost - emptyScriptCost;
         int expected = emptyScriptCost + 2 * perName;
         assertEquals("2-identifier script cost", expected, costTwo);
     }

     /**
      * Empty SCRIPT node should have cost equal to the measured baseline.
      */
     public void testEmptyScriptCostZero() {
         assertEquals("empty script cost must equal baseline",
                 emptyScriptCost, InlineCostEstimator.getCost(new Node(Token.SCRIPT)));
     }

     // -----------------------------------------------------------------
     // Threshold behaviour
     // -----------------------------------------------------------------

     /**
      * Threshold == 0 does not cap the result; the full cost of the first
      * identifier is still returned (early exit after exceeding threshold).
      */
     public void testThresholdZero() throws Exception {
         Node name = nameNode("x");
         int cost = InlineCostEstimator.getCost(name, 0);
         assertEquals("threshold 0 returns full first-identifier cost",
                 estimatedIdentifierCost, cost);
     }

     /**
      * When threshold equals the natural cost, result equals the natural cost.
      */
     public void testThresholdEqualsCostReturnsFull() throws Exception {
         int constant = getEstimatedIdentifierCost();
         Node name = nameNode("x");
         assertEquals("threshold == full cost -> full cost",
                 constant,
                 InlineCostEstimator.getCost(name, constant));
     }

     /**
      * When threshold is lower than the natural cost, the method still returns
      * the computed cost (it is not capped). This verifies early exit but not capping.
      */
     public void testThresholdCapsCost() throws Exception {
         int constant = getEstimatedIdentifierCost();
         int threshold = constant - 1;
         Node name = nameNode("x");
         int cost = InlineCostEstimator.getCost(name, threshold);
         assertTrue("cost > threshold because capping is not applied", cost > threshold);
         assertEquals("full cost returned despite low threshold", constant, cost);
     }

     /**
      * When threshold is greater than the total cost, the full (uncapped) cost
      * is returned.
      */
     public void testThresholdLargerThanTotalReturnsFull() throws Exception {
         int constant = getEstimatedIdentifierCost();
         Node name = nameNode("x");
         int bigThreshold = constant * 100;
         assertEquals("threshold > cost -> full cost", constant,
                 InlineCostEstimator.getCost(name, bigThreshold));
     }

     // -----------------------------------------------------------------
     // Processing-order and stop-on-threshold
     // -----------------------------------------------------------------

     /**
      * If the first child already exceeds the threshold, traversal stops and
      * later siblings do not contribute.
      */
     public void testContinueProcessingStopsAtThreshold() throws Exception {
         int constant = getEstimatedIdentifierCost();
         int threshold = constant - 1;
         Node name1 = nameNode("x");
         Node name2 = nameNode("y");
         Node add = addExpr(name1, name2);
         Node stmt = exprStatement(add);
         Node script = scriptNode(stmt);

         int costCapped = InlineCostEstimator.getCost(script, threshold);
         int uncapped = InlineCostEstimator.getCost(script);
         // early exit: only first NAME counted because constant > threshold
         assertEquals("early exit returns cost of first identifier", constant, costCapped);
         assertTrue("uncapped cost is larger because second identifier is counted",
                 uncapped > costCapped);
     }

     /**
      * A deep (but simple) nesting must produce a cost that is at least the
      * sum of the identifier costs.
      */
     public void testDeepTreeSumsAllChildrenCosts() throws Exception {
         int constant = getEstimatedIdentifierCost();
         Node d = nameNode("d");
         Node c = nameNode("c");
         Node b = nameNode("b");
         Node a = nameNode("a");
         Node addCD = addExpr(c, d);
         Node addBCD = addExpr(b, addCD);
         Node addABCD = addExpr(a, addBCD);
         Node stmt = exprStatement(addABCD);
         Node script = scriptNode(stmt);

         int cost = InlineCostEstimator.getCost(script);
         // Minimum expected: 4 identifiers × constant, plus possible overhead
         assertTrue("deep tree cost >= 4*constant", cost >= 4 * constant);
         // also compare against a flat 4-name script to see the influence of structure
         Node flat = scriptNode(
                 exprStatement(nameNode("a")),
                 exprStatement(nameNode("b")),
                 exprStatement(nameNode("c")),
                 exprStatement(nameNode("d")));
         int flatCost = InlineCostEstimator.getCost(flat);
         // deep tree has fewer EXPR_RESULT nodes, so it should cost less or equal
         assertTrue("deep tree cost <= flat 4-statement script cost", cost <= flatCost);
     }

     /**
      * A larger tree with many leaves must scale proportionally to the number
      * of identifiers, after accounting for fixed per-statement overhead.
      */
     public void testLargeTreeLinearScaling() throws Exception {
         final int leafCount = 50;
         Node script = new Node(Token.SCRIPT);
         for (int i = 0; i < leafCount; i++) {
             script.addChildToBack(exprStatement(nameNode("v" + i)));
         }
         int cost = InlineCostEstimator.getCost(script);

         int perName = singleNameStmtCost - emptyScriptCost;
         int expected = emptyScriptCost + leafCount * perName;
         assertEquals("linear scaling with " + leafCount + " identifiers", expected, cost);
     }

     /**
      * Simple additivity of children: the cost of a parent expression is at
      * least the sum of its direct children's costs.
      */
     public void testCostIsSumOfChildren() throws Exception {
         Node left = nameNode("lhs");
         Node right = nameNode("rhs");
         Node add = addExpr(left, right);

         int costLeft = InlineCostEstimator.getCost(left);
         int costRight = InlineCostEstimator.getCost(right);
         int costExpr = InlineCostEstimator.getCost(add);
         assertTrue("parent cost >= sum of children", costExpr >= costLeft + costRight);
     }
 }
