package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.CompilerTestCase;

 public class PeepholeFoldConstantsBugTest extends CompilerTestCase {

   private boolean late;

   @Override protected CompilerPass getProcessor(Compiler compiler) {
     return new PeepholeOptimizationsPass(compiler, new PeepholeFoldConstants(late)); }

   @Override protected int getNumRepetitions() {
     return 2; }

   @Override protected void setUp() throws Exception {
     super.setUp();
     late = false;
     disableNormalize();
     compareJsDoc = false; }

   public void testFoldGetElemInBoundsIndexZero() {
     test("var x = [1,2,3][0]", "var x = 1"); }

   public void testFoldGetElemInBoundsLastIndex() {
     test("var x = [1,2,3][2]", "var x = 3"); }

   public void testFoldGetElemEmptyArrayIndexZero() {
     test("var x = [][0]", "var x = void 0"); }

   public void testFoldGetElemOutOfBoundsEqualToLength() {
     test("var x = [1,2,3][3]", "var x = void 0"); }

   public void testFoldGetElemOutOfBoundsBeyondLength() {
     test("var x = [1,2,3][10]", "var x = void 0"); }

   public void testFoldGetElemNegativeIndexErrors() {
     testError("var x = [1,2,3][-1]",
         PeepholeFoldConstants.INDEX_OUT_OF_BOUNDS_ERROR); }

   public void testFoldGetElemFractionalIndexErrors() {
     testError("var x = [1,2,3][1.5]",
         PeepholeFoldConstants.INVALID_GETELEM_INDEX_ERROR); }

   public void testFoldGetElemNonConstantIndexIsUnchanged() {
     testSame("var i = 0; var x = [1,2,3][i]"); }

   public void testFoldGetElemNonArrayTargetIsUnchanged() {
     testSame("var x = a[0]"); }

   public void testFoldGetElemAssignmentTargetIsUnchanged() {
     testSame("[1,2,3][0] += 1"); }
 }
