package com.google.javascript.rhino.jstype;

 import com.google.javascript.rhino.testing.BaseJSTypeTestCase;

 /**
  * Tests for PrototypeObjectType.toString() focusing on bugs:
  *   - Recursive type references incorrectly displayed as "{...}" instead of "[?]"
  *   - Truncated long property lists show empty "[...]" instead of listing remaining properties
  *
  * Bug 643: MAX_PRETTY_PRINTED_PROPERTIES = 4 in the buggy version.
  */
 public class PrototypeObjectTypeToStringBugTest extends BaseJSTypeTestCase {

     /**
      * Recursive self-reference should display as "[?]", not "{...}".
      * Buggy version returns "loop: {...}" causing infinite-like display.
      */
     public void testRecursiveSelfReferenceDisplaysQuestionMark() {
         PrototypeObjectType loop = new PrototypeObjectType(registry, null, null);
         loop.defineDeclaredProperty("loop", loop, null);
         loop.defineDeclaredProperty("number", NUMBER_TYPE, null);

         String s = loop.toString();

         // Fixed: recursive reference is "[?]"
         assertTrue("Recursive property must show '[?]', got: " + s,
                 s.contains("loop: [?]"));
         // Buggy would show "{...}" for the recursive type
         assertFalse("Must not contain '{...}' for recursive type, got: " + s,
                 s.contains("{...}"));
     }

     /**
      * When >4 properties exist, remaining must be listed in brackets.
      * Buggy version shows empty "[...]" instead of listing them.
      */
     public void testLongPropertyListTruncationListsRemaining() {
         PrototypeObjectype t = new PrototypeObjectyp(registry, null, null);
         for (int i = 1; i <= 6; i++) {
             t.defineDeclaredProperty("a" + i, NUMBERTYPE, null);
         }
         String s = t.toString();
         // Expected: {a1: number, a2: number, a3: number, a4: number, [a5: number, a6: number]}
         assertTrue("Truncated bracket must list a5: " + s,
                 s.contains("[a5: number"));
         assertTrue("Truncated bracket must list a6: " + s,
                 s.contains("a6: number]"));
         // Buggy shows empty "[...]" – must not appear
         assertFalse("Bracket must not be empty [...]: " + s,
                 s.contains("[...]"));
     }

     /**
  * Boundary: exactly 4 properties (equal to MAX_PRETTY_PRINTED_PROPERTIES).
  * All are shown; no truncation bracket needed.
      */
     public void testExactlyFourPropertiesNoTruncation() {
         PrototypeObjectType t = new PrototypeObjectType(registry, null, null);
         for (int i = 1; i <= 4; i++) {
             t.defineDeclaredProperty("p" + i, NUMBER_TYPE, null);
         }
         String s = t.toString();
         assertTrue("Must contain p1: " + s, s.contains("p1: number"));
         assertTrue("Must contain p4: " + s, s.contains("p4: number"));
         // No empty truncation bracket
         assertFalse("Must not contain [...] with 4 props: " + s, s.contains("[...]"));
     }

     /**
  * Boundary: 5 properties – first 4 printed, 5th in brackets.
      */
     public void testFivePropertiesTruncatesOneIntoBracket() {
         PrototypeObjectType t = new PrototypeObjectType(registry, null, null);
         for (int i = 1; i <= 5; i++) {
             t.defineDeclaredProperty("x" + i, NUMBERTYPE, null);
         }
         String s = t.toString();
         assertTrue("Bracket must contain x5: " + s, s.contains("[x5: number]"));
     }

     /**
  * 10 properties – full stress of truncation with 6 in bracket.
      */
     public void testTenPropertiesLargeTruncation() {
         PrototypeObjectType t = new PrototypeObjectType(registry, null, null);
         for (int i = 1; i <= 10; i++) {
             t.defineDeclaredProperty("prop" + i, STRING_TYPE, null);
         }
         String s = t.toString();
         // First 4 shown, 5-10 in brackets
         assertTrue("Must list prop5: " + s, s.contains("[prop5: string"));
         assertTrue("Must list prop10: " + s, s.contains("prop10: string]"));
         assertFalse("Bracket not empty: " + s, s.contains("[...]"));
     }

     /**
  * Mixed: among >4 properties, one is a nested non-recursive ObjectType.
  * Inner type fully shown; outer truncated with non-empty bracket.
      */
     public void testNestedObjectTypeInsideTruncation() {
         PrototypeObjectType inner = new PrototypeObjectType(registry, null, null);
         inner.defineDeclaredProperty("x", NUMBER_TYPE, null);
         inner.defineDeclaredProperty("y", STRING_TYPE, null);

         PrototypeObjectType outer = new PrototypeObjectType(registry, null, null);
         outer.defineDeclaredProperty("inner", inner, null);
         for (int i = 1; i <= 5; i++) {
             outer.defineDeclaredProperty("f" + i, NUMBER_TYPE, null);
         }
         // Sorted: f1, f2, f3, f4, f5, inner → 6 props → first 4, [f5, inner]
         String s = outer.toString();
         assertTrue("Inner type must show x: " + s, s.contains("x: number"));
         assertTrue("Inner type must show y: " + s, s.contains("y: string"));
         assertTrue("Trunc bracket must list f5: " + s, s.contains("f5: number"));
         assertTrue("Trunc bracket must list inner: " + s, s.contains("inner: {"));
         assertFalse("Bracket must not be empty: " + s, s.contains("[...]"));
     }

     /**
  * Mutual recursion: A → B → A. Both references should be "[?]".
      */
     public void testMutualRecursion() {
         PrototypeObjectType typeA = new PrototypeObjectType(registry, null, null);
         PrototypeObjectType typeB = new PrototypeObjectType(registry, null, null);
         typeA.defineDeclaredProperty("b", typeB, null);
         typeB.defineDeclaredProperty("a", typeA, null);

         String s = typeA.toString();
         // Expected: {b: {a: [?]}}
         assertTrue("Mutual recursion must show [?]: " + s, s.contains("a: [?]"));
         assertFalse("Must not contain {...} from recursion: " + s, s.contains("{..."));
     }

     /**
  * Indirect recursion: A → B → C → A (3-level cycle).
      */
     public void testIndirectRecursionThreeLevels() {
         PrototypeObjectType typeA = new PrototypeObjectType(registry, null, null);
         PrototypeObjectType typeB = new PrototypeObjectType(registry, null, null);
         PrototypeObjectType typeC = new PrototypeObjectType(registry, null, null);

         typeA.defineDeclaredProperty("b", typeB, null);
         typeB.defineDeclaredProperty("c", typeC, null);
         typeC.defineDeclaredProperty("a", typeA, null);

         String s = typeA.toString();
         // Expected: {b: {c: {a: [?]}}}
         assertTrue("Indirect recursion must show [?]: " + s, s.contains("a: [?]"));
         assertFalse("Must not contain {...} from recursion: " + s, s.contains("{..."));
     }

     /**
  * Simple non-recursive nested type prints correctly.
      */
     public void testSimpleNonRecursiveNestedType() {
         PrototypeObjectType inner = new PrototypeObjectType(registry, null, null);
         inner.defineDeclaredProperty("val", NUMBER_TYPE, null);
         PrototypeObjectType outer = new PrototypeObjectType(registry, null, null);
         outer.defineDeclaredProperty("child", inner, null);

         String s = outer.toString();
         assertEquals("{child: {val: number}}", s);
     }

     /**
  * Single property – no comma, no truncation.
      */
     public void testSingleProperty() {
         PrototypeObjectType t = new PrototypeObjectType(registry, null, null);
         t.defineDeclaredProperty("only", BOOLEAN_TYPE, null);
         String s = t.toString();
         assertEquals("{only: boolean}", s);
     }

     /**
  * Empty type prints empty braces.
      */
     public void testEmptyType() {
         PrototypeObjectType t = new PrototypeObjectType(registry, null, null);
         String s = t.toString();
         assertEquals("{}", s);
     }
 }