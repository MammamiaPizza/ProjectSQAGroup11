package com.google.javascript.rhino.jstype;

 import com.google.javascript.rhino.ErrorReporter;
 import com.google.javascript.rhino.jstype.RecordTypeBuilder;

 import junit.framework.TestCase;

 import java.util.Set;

 /**
  *
  * Tests for RecordType#getLeastSupertype, isSubtype, and isEquivalentTo,
  * targeting the union-construction bug in Closure 46.
  */
 public class RecordTypeLeastSupertypeTest extends TestCase {

   private JSTypeRegistry registry;
   private JSType numberType;
   private JSType stringType;

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     registry = new JSTypeRegistry(new ErrorReporter() {
       @Override public void warning(String msg, String source, int line, int offset) {}
       @Override public void error(String msg, String source, int line, int offset) {}
       @Override public void error(String msg) {}
       @Override public void warning(String msg) {}
     });
     numberType = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
     stringType = registry.getNativeType(JSTypeNative.STRING_TYPE);
   }

   private RecordType record(String... keysAndTypes) {
     RecordTypeBuilder builder = new RecordTypeBuilder(registry);
     for (int i = 0; i < keysAndTypes.length; i += 2) {
       JSType t = "number".equals(keysAndTypes[i + 1]) ? numberType : stringType;
       builder.addProperty(keysAndTypes[i], t, null);
     }
     JSType built = builder.build();
     return built.toMaybeRecordType();
   }

   // ----- getLeastSupertype tests -----

   /** Overlapping non-subtype records must produce a union. */
   public void testLeastSupertype_overlapping_not_subtype_1() {
     RecordType r1 = record("a", "number", "b", "string");
     RecordType r2 = record("b", "string", "c", "string", "e", "number");
     JSType sup = r1.getLeastSupertype(r2);

     assertTrue("must be union", sup.isUnionType());
     UnionType union = sup.toMaybeUnionType();
     Set<JSType> alts = union.getAlternates();
     assertTrue(alts.contains(r1));
     assertTrue(alts.contains(r2));
     assertTrue(r1.isSubtype(sup));
     assertTrue(r2.isSubtype(sup));
   }

   /** Another overlapping non-subtype pair. */ public void
   testLeastSupertype_overlapping_not_subtype_2() {
     RecordType r1 = record("a", "number", "b", "string");
     RecordType r2 = record("d", "number", "e", "string", "f", "string");
     JSType sup = r1.getLeastSupertype(r2);

     assertTrue("must be union", sup.isUnionType());
     UnionType union = sup.toMaybeUnionType();
     Set<JSType> alts = union.getAlternates();
     assertTrue(alts.contains(r1));
     assertTrue(alts.contains(r2));
     assertTrue(r1.isSubtype(sup));
     assertTrue(r2.isSubtype(sup));
   }

   /** Disjoint records → union. */
   public void testLeastSupertype_disjoint() {
     RecordType r1 = record("a", "number");
     RecordType r2 = record("b", "string");
     JSType sup = r1.getLeastSupertype(r2);

     assertTrue("must be union", sup.isUnionType());
     UnionType union = sup.toMaybeUnionType();
     Set<JSType> alts = union.getAlternates();
     assertTrue(alts.contains(r1));
     assertTrue(alts.contains(r2));
     assertTrue(r1.isSubtype(sup));
     assertTrue(r2.isSubtype(sup));
   }

   /** Identical records → the record itself (not a union). */
   public void testLeastSupertype_identical() {
     RecordType r1 = record("a", "number", "b", "string");
     RecordType r2 = record("a", "number", "b", "string");
     JSType sup = r1.getLeastSupertype(r2);

     // For equal records the result should be the record, not a union.
     assertFalse("should not be union for identical records", sup.isUnionType());
     assertEquals(r1, sup);
   }

   /** Subset relationship: one is supertype of the other. */
   public void testLeastSupertype_subset() {
     RecordType sub = record("a", "number", "b", "string");
     RecordType sup = record("a", "number");   // fewer properties → supertype
     // sub is a subtype of sup
     JSType result = sub.getLeastSupertype(sup);
     assertFalse("should not be union", result.isUnionType());
     assertEquals(sup, result);
     assertTrue(sub.isSubtype(result));
   }

   /** Two empty records. */
   public void testLeastSupertype_empty_empty() {
     RecordType e1 = record();
     RecordType e2 = record();
     JSType sup = e1.getLeastSupertype(e2);
     // Empty record is the top record type → itself.
     assertFalse("should not be union", sup.isUnionType());
     assertEquals(e1, sup);
   }

   /** Empty record and non-empty. */
   public void testLeastSupertype_empty_nonEmpty() {
     RecordType empty = record();
     RecordType nonEmpty = record("a", "number");
     JSType sup = empty.getLeastSupertype(nonEmpty);
     // The empty record is supertype of all records.
     assertFalse("should not be union", sup.isUnionType());
     assertEquals(empty, sup);
     assertTrue(nonEmpty.isSubtype(sup));
   }

   /** Non-record argument. */
   public void testLeastSupertype_withBoolean() {
     RecordType r1 = record("a", "number");
     JSType boolType = registry.getNativeType(JSTypeNative.BOOLEAN_TYPE);
     JSType sup = r1.getLeastSupertype(boolType);
     // Should fall back to general least-supertype (likely a union with Object).
     // Only assert that both are subtypes of the result.
     assertTrue(r1.isSubtype(sup));
     assertTrue(boolType.isSubtype(sup));
   }

   // ----- isSubtype tests -----

   public void testIsSubtype_property_subset() {
     RecordType sub = record("a", "number", "b", "string");
     RecordType sup = record("a", "number");
     // A record with more properties is a subtype of a record with fewer.
     assertTrue(sub.isSubtype(sup));
   }

   public void testIsSubtype_not_subtype_missing_property() {
     RecordType a = record("a", "number");
     RecordType b = record("b", "string");
     // Neither has the other's required property → not subtype.
     assertFalse(a.isSubtype(b));
     assertFalse(b.isSubtype(a));
   }

   /** Property types must match for declared properties (equivalent). */
   public void testIsSubtype_property_type_mismatch() {
     RecordType rNumber = record("a", "number");
     RecordType rString = record("a", "string");
     assertFalse(rNumber.isSubtype(rString));
     assertFalse(rString.isSubtype(rNumber));
   }

   // ----- isEquivalentTo tests -----

   public void testIsEquivalentTo_equval() {
     RecordType r1 = record("a", "number", "b", "string");
     RecordType r2 = record("a", "number", "b", "string");
     assertTrue(r1.isEquivalentTo(r2));
   }

   public void testIsEquivalentTo_different_propertys() {
     RecordType r1 = record("a", "number");
     RecordType r2 = record("b", "string");
     assertFalse(r1.isEquivalentTo(r2));
   }
 }