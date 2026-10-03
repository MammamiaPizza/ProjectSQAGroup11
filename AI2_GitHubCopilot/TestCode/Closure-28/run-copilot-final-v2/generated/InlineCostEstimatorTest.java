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

   // -----------------------------------------------------------------
   // Helper methods
   // -----------------------------------------------------------------

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

   // -----------------------------------------------------------------
   // Cases derived from the test plan: fault-related, boundary, normal
   // -----------------------------------------------------------------

   /**
    * Single identifier: cost exactly the estimated constant value.
    * If the buggy version double-counts, this test catches it.
    */
   public void testSingleIdentifierCost() throws Exception {
     int constant = getEstimatedIdentifierCost();
     Node name = nameNode("x");
     // Direct cost on a single NAME node
     assertEquals("single NAME cost must equal ESTIMATED_IDENTIFIER_COST",
                  constant, InlineCostEstimator.getCost(name));
   }

   /**
    * Cost must not depend on the length of the actual identifier name.
    * Bug #728 suspicion: original name length leaked into the cost.
    */
   public void testIdentifierCostIndependentOfNameLength() throws Exception {
     int costShort = InlineCostEstimator.getCost(nameNode("a"));
     int costLong  = InlineCostEstimator.getCost(nameNode("thisIsALongName"));
     assertEquals("cost must be independent of name length", costShort, costLong);
   }

   /**
    * Two identifiers → twice the single-identifier cost.
    * Verifies no double counting and linear scaling.
    */
   public void testTwoIdentifiersCostIsDoubleSingle() throws Exception {
     Node nameA = nameNode("a");
     Node nameB = nameNode("b");
     Node add = addExpr(nameA, nameB);
     Node stmt = exprStatement(add);
     Node script = scriptNode(stmt);

     int costOne = InlineCostEstimator.getCost(nameNode("x"));
     int costTwo = InlineCostEstimator.getCost(script);
     assertEquals("2 identifiers = 2 × single", 2 * costOne, costTwo);
   }

   /**
    * Empty SCRIPT node should have cost 0.
    */
   public void testEmptyScriptCostZero() {
     Node emptyScript = new Node(Token.SCRIPT);
     assertEquals("empty script cost", 0, InlineCostEstimator.getCost(emptyScript));
   }

   // -----------------------------------------------------------------
   // Threshold behaviour
   // -----------------------------------------------------------------

   /**
    * Threshold == 0 means immediate return with cost 0.
    */
   public void testThresholdZero() {
     Node name = nameNode("x");
     assertEquals("threshold 0 → cost 0", 0,
                  InlineCostEstimator.getCost(name, 0));
   }

   /**
    * When threshold equals the natural cost, result equals the natural cost.
    */
   public void testThresholdEqualsCostReturnsFull() throws Exception {
     int constant = getEstimatedIdentifierCost();
     Node name = nameNode("x");
     assertEquals("threshold == full cost → full cost",
                  constant,
                  InlineCostEstimator.getCost(name, constant));
   }

   /**
    * When threshold is lower than the natural cost, the result must not exceed
    * the threshold.
    */
   public void testThresholdCapsCost() throws Exception {
     int constant = getEstimatedIdentifierCost();
     int threshold = constant - 1;
     Node name = nameNode("x");
     int cost = InlineCostEstimator.getCost(name, threshold);
     assertTrue("capped cost <= threshold", cost <= threshold);
   }

   /**
    * When threshold is greater than the total cost, the full (uncapped) cost
    * is returned.
    */
   public void testThresholdLargerThanTotalReturnsFull() throws Exception {
     int constant = getEstimatedIdentifierCost();
     Node name = nameNode("x");
     int bigThreshold = constant * 100;
     assertEquals("threshold > cost → full cost", constant,
                  InlineCostEstimator.getCost(name, bigThreshold));
   }

   // -----------------------------------------------------------------
   // Processing-order and stop-on-threshold
   // -----------------------------------------------------------------

   /**
    * If the first child already exceeds the threshold, the traversal should
    * stop and later siblings must not contribute to the cost.
    */
   public void testContinueProcessingStopsAtThreshold() throws Exception {
     int constant = getEstimatedIdentifierCost();
     // Use two names so that even the first one costs constant.
     // We pick threshold = constant – 1 so the first name caps at threshold.
     int threshold = constant - 1;
     Node name1 = nameNode("x");
     Node name2 = nameNode("y");
     Node add = addExpr(name1, name2);
     Node stmt = exprStatement(add);
     Node script = scriptNode(stmt);

     int cost = InlineCostEstimator.getCost(script, threshold);
     assertTrue("stopped early → cost <= threshold", cost <= threshold);
     // Also double-check that without cap the cost would be larger
     int unCapped = InlineCostEstimator.getCost(script);
     assertTrue("uncapped cost > threshold", unCapped > threshold);
   }

   /**
    * A deep (but simple) nesting sums exactly the costs of all leaves.
    */
   public void testDeepTreeSumsAllChildrenCosts() throws Exception {
     int constant = getEstimatedIdentifierCost();
     // Build deep script: (a + (b + (c + d)))
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
     // Expected: 4 identifiers × constant
     assertEquals("deep sum", 4 * constant, cost);
   }

   /**
    * A larger tree with many leaves must produce a proportional cost
    * and must not overflow or saturate.
    */
   public void testLargeTreeLinearScaling() throws Exception {
     int constant = getEstimatedIdentifierCost();
     final int leafCount = 50;
     // Build: expr(a1), expr(a2), …
     Node script = new Node(Token.SCRIPT);
     for (int i = 0; i < leafCount; i++) {
       script.addChildToBack(exprStatement(nameNode("v" + i)));
     }
     int cost = InlineCostEstimator.getCost(script);
     assertEquals(leafCount * constant, cost);
   }

   /**
    * Simple additivity: cost of a parent expression containing children
    * must equal the sum of the childrens' individual costs.
    */
   public void testCostIsSumOfChildren() throws Exception {
     Node left = nameNode("lhs");
     Node right = nameNode("rhs");
     Node add = addExpr(left, right);

     int costLeft = InlineCostEstimator.getCost(left);
     int costRight = InlineCostEstimator.getCost(right);
     int costExpr = InlineCostEstimator.getCost(add);
     assertEquals("additivity of direct children",
                  costLeft + costRight, costExpr);
   }
 }
