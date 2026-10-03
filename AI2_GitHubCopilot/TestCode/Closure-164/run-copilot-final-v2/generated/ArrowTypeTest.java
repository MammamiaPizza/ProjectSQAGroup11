package com.google.javascript.rhino.jstype;

 import static org.junit.Assert.*;

 import com.google.javascript.rhino.ErrorReporter;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;

 import org.junit.Test;

 /**
  * Tests for {@link ArrowType} behavioral contracts, including the subtyping rules,
  * least-supertype / greatest-subtype calculations, and toString representation.
  * Designed to expose the known bug in the supplied version where
  * {@code getLeastSupertype} and {@code getGreatestSubtype} throw
  * {@link UnsupportedOperationException}, {@code isSubtype} incorrectly allows a
  * function with more required parameters to be a subtype of one with fewer required
  * parameters, and {@code toString} returns a generic "Function" label instead of
  * the precise arrow-type representation.
  */
 public class ArrowTypeTest {

     private final JSTypeRegistry registry;
     private final JSType numberType;
     private final JSType booleanType;
     private final JSType stringType;
     private final JSType unknownType;

     public ArrowTypeTest() {
         ErrorReporter reporter = new ErrorReporter() {
             public void error(String message, String sourceName, int line, int lineOffset) {
             }
             public void warning(String message, String sourceName, int line, int lineOffset) {
             }
         };
         registry = new JSTypeRegistry(reporter);
         numberType = registry.createNumberType();
         booleanType = registry.createBooleanType();
         stringType = registry.createStringType();
         unknownType = registry.getNativeType(JSTypeNative.UNKNOWN_TYPE);
     }

     // --- helpers ------------------------------------------------------------

     private Node paramNode(JSType type) {
         Node n = new Node(Token.NAME);
         n.setJSType(type);
         return n;
     }

     private Node paramList(JSType... types) {
         Node params = new Node(Token.LP);
         for (JSType t : types) {
             params.addChildToBack(paramNode(t));
         }
         return params;
     }

     private ArrowType arrow(JSType returnType, JSType... paramTypes) {
         return new ArrowType(registry, paramList(paramTypes), returnType);
     }

     // --- isSubtype ----------------------------------------------------------

     @Test
     public void testIsSubtypeSameType() {
         ArrowType a1 = arrow(booleanType, numberType);
         ArrowType a2 = arrow(booleanType, numberType);
         assertTrue("identical arrow types must be subtypes of each other", a1.isSubtype(a2));
     }

     @Test
     public void testIsSubtypeContravariantParam() {
         ArrowType strict = arrow(booleanType, numberType);            // requires number
         ArrowType loose  = arrow(booleanType, stringType);         // requires string - not subtype
of number
         assertFalse("contravariance: that-param (string) is not subtype of this-param (number)",
                 strict.isSubtype(loose));
     }

     @Test
     public void testIsSubtypeCovariantReturn() {
         ArrowType a1 = arrow(numberType, stringType);   // returns number
         ArrowType a2 = arrow(stringType, stringType);   // returns string (not subtype of number)
         assertFalse("covariance: string is not subtype of number, so a1 cannot be subtype of a2",
                 a1.isSubtype(a2));
     }

     /**
      * Regression test for the bug where {@code isSubtype} blindly returned
      * {@code true} when {@code this} has more required parameters than
      * {@code that}. The correct behaviour is to return {@code false} because
      * the supertype is missing a required argument.
      */
     @Test
     public void testIsSubtypeThisHasMoreRequiredParamsMustBeFalse() {
         ArrowType requiresTwo = arrow(booleanType, numberType, numberType);
         ArrowType requiresOne  = arrow(booleanType, numberType);
         assertFalse(
             "a subtype must not have more required parameters than the supertype",
             requiresTwo.isSubtype(requiresOne));
     }

     /** The inverse case – fewer required parameters must still be allowed. */
     @Test
     public void testIsSubtypeThisHasFewerRequiredParamsIsTrue() {
         ArrowType requiresOne  = arrow(booleanType, numberType);
         ArrowType requiresTwo = arrow(booleanType, numberType, numberType);
         assertTrue(
             "a function with fewer required parameters must be a subtype of one with more",
             requiresOne.isSubtype(requiresTwo));
     }

     @Test
     public void testIsSubtypeWithNullParameters() {
         // null parameters are replaced with var_args unknown in the constructor
         ArrowType a1 = new ArrowType(registry, null, booleanType);  // var_args unknown -> boolean
         ArrowType a2 = arrow(booleanType);                           // 0 params -> boolean (empty
LP)
         assertTrue("arrow with null params (var-args unknown) is supertype of 0-param arrow",
                 a2.isSubtype(a1));
     }

     // --- least supertype / greatest subtype (currently unimplemented) -------

     @Test
     public void testGetLeastSupertypeShouldReturnArrowType() {
         ArrowType a1 = arrow(booleanType, numberType, numberType);
         ArrowType a2 = arrow(booleanType, numberType, numberType);
         JSType sup = a1.getLeastSupertype(a2);
         assertNotNull("least supertype must not be null", sup);
         assertEquals("least supertype must produce the correct arrow-type string",
                 "function (number, number): boolean", sup.toString());
     }

     @Test
     public void testGetGreatestSubtypeShouldReturnArrowType() {
         ArrowType a1 = arrow(booleanType, numberType, numberType);
         ArrowType a2 = arrow(booleanType, numberType, numberType);
         JSType sub = a1.getGreatestSubtype(a2);
         assertNotNull("greatest subtype must not be null", sub);
         assertEquals("greatest subtype must produce the correct arrow-type string",
                 "function (number, number): boolean", sub.toString());
     }

     // --- toString / isEquivalentTo ------------------------------------------

     @Test
     public void testToStringCorrectRepresentation() {
         ArrowType a = arrow(booleanType, numberType, stringType);
         assertEquals("function (number, string): boolean", a.toString());
     }

     @Test
     public void testIsEquivalentToSameParamsAndReturn() {
         ArrowType a1 = arrow(booleanType, numberType, stringType);
         ArrowType a2 = arrow(booleanType, numberType, stringType);
         assertTrue("equivalent when return types and parameters match", a1.isEquivalentTo(a2));
     }

     @Test
     public void testIsEquivalentToDifferentParams() {
         ArrowType a1 = arrow(booleanType, numberType);
         ArrowType a2 = arrow(booleanType, stringType);
         assertFalse("not equivalent when parameters differ", a1.isEquivalentTo(a2));
     }

     @Test
     public void testIsEquivalentToDifferentReturnType() {
         ArrowType a1 = arrow(numberType, stringType);
         ArrowType a2 = arrow(booleanType, stringType);
         assertFalse("not equivalent when return types differ", a1.isEquivalentTo(a2));
     }
 }
