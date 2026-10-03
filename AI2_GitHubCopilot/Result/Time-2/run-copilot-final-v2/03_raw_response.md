package org.joda.time;

 import junit.framework.TestCase;

 public class TestPartialBug93 extends TestCase {

     // Era and year are not duplicates; the buggy version may throw a false duplicate.
     public void testConstructorEraYearNoDuplicate() {
         Partial p = new Partial(
             new DateTimeFieldType[]{DateTimeFieldType.era(), DateTimeFieldType.year()},
             new int[]{1, 2023}
         );
         assertEquals(2, p.size());
         assertEquals(DateTimeFieldType.era(), p.getFieldType(0));
         assertEquals(DateTimeFieldType.year(), p.getFieldType(1));
     }

     // Adding year to an era partial should succeed.
     public void testWithAddYearToEraNoDuplicate() {
         Partial p = new Partial(DateTimeFieldType.era(), 1);
         Partial p2 = p.with(DateTimeFieldType.year(), 2023);
         assertEquals(2, p2.size());
         assertEquals(DateTimeFieldType.era(), p2.getFieldType(0));
         assertEquals(DateTimeFieldType.year(), p2.getFieldType(1));
     }

     // Adding era to a year partial should succeed as well.
     public void testWithAddEraToYearNoDuplicate() {
         Partial p = new Partial(DateTimeFieldType.year(), 2023);
         Partial p2 = p.with(DateTimeFieldType.era(), 1);
         assertEquals(2, p2.size());
         assertEquals(DateTimeFieldType.era(), p2.getFieldType(0));
         assertEquals(DateTimeFieldType.year(), p2.getFieldType(1));
     }

     // Updating a field without changing the type must work.
     public void testWithUpdateSameType() {
         Partial p = new Partial(DateTimeFieldType.year(), 2023);
         Partial p2 = p.with(DateTimeFieldType.year(), 2025);
         assertEquals(1, p2.size());
         assertEquals(2025, p2.getValue(0));
     }

     // Adding a smaller field (month) to a larger (year) keeps correct order.
     public void testWithAddMonthToYearSuccess() {
         Partial p = new Partial(DateTimeFieldType.year(), 2023);
         Partial p2 = p.with(DateTimeFieldType.monthOfYear(), 6);
         assertEquals(2, p2.size());
         assertEquals(DateTimeFieldType.year(), p2.getFieldType(0));
         assertEquals(DateTimeFieldType.monthOfYear(), p2.getFieldType(1));
     }

     // Adding a larger field after a smaller one violates the order constraint.
     public void testWithAddYearToMonthOrderError() {
         Partial p = new Partial(DateTimeFieldType.monthOfYear(), 6);
         try {
             p.with(DateTimeFieldType.era(), 2);
             fail("Should have thrown order error");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("order largest-smallest"));
         }
     }

     // Genuine duplicate of the same DateTimeFieldType must be rejected.
     public void testConstructorDuplicateSameTypeThrows() {
         try {
             new Partial(
                 new DateTimeFieldType[]{DateTimeFieldType.era(), DateTimeFieldType.era()},
                 new int[]{1, 2}
             );
             fail("Should have thrown duplicate error");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("duplicate"));
         }
     }

     // Single-field construction is valid.
     public void testConstructorSingleFieldValid() {
         Partial p = new Partial(DateTimeFieldType.era(), 1);
         assertEquals(1, p.size());
     }

     // with() should accept the same field type and value.
     public void testWithExistingTypeNoOp() {
         Partial p = new Partial(DateTimeFieldType.era(), 1);
         Partial p2 = p.with(DateTimeFieldType.era(), 1);
         assertEquals(1, p2.size());
         assertEquals(1, p2.getValue(0));
     }

     // Null types array must throw.
     public void testConstructorNullTypesThrows() {
         try {
             new Partial((DateTimeFieldType[]) null, new int[]{1});
             fail("Should have thrown");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Types array must not be null"));
         }
     }

     // Mismatched lengths must throw.
     public void testConstructorValuesLengthMismatchThrows() {
         try {
             new Partial(new DateTimeFieldType[]{DateTimeFieldType.era()}, new int[]{1, 2});
             fail("Should have thrown");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("same length"));
         }
     }

     // Empty arrays are allowed.
     public void testConstructorEmptyTypes() {
         Partial p = new Partial(new DateTimeFieldType[]{}, new int[]{});
         assertEquals(0, p.size());
     }

 }