package org.joda.time;

import junit.framework.TestCase;

public class TestPartialDuplicateTypes extends TestCase {

 public void testConstructor2Arg_SingleType_Success() {
     Partial partial = new Partial(
         new DateTimeFieldType[] { DateTimeFieldType.year() },
         new int[] { 2000 }
     );
     assertNotNull(partial);
     assertEquals(1, partial.size());
     assertEquals(DateTimeFieldType.year(), partial.getFieldType(0));
     assertEquals(2000, partial.getValue(0));
 }

 public void testConstructor2Arg_NonDuplicate_Success() {
     Partial partial = new Partial(
         new DateTimeFieldType[] { DateTimeFieldType.year(), DateTimeFieldType.monthOfYear() },
         new int[] { 2000, 6 }
     );
     assertNotNull(partial);
     assertEquals(2, partial.size());
 }

 public void testConstructor2Arg_AdjacentDuplicate_ThrowsIAE() {
     try {
         new Partial(
             new DateTimeFieldType[] { DateTimeFieldType.year(), DateTimeFieldType.year() },
             new int[] { 2000, 2001 }
         );
         fail("IllegalArgumentException expected for adjacent duplicate types");
     } catch (IllegalArgumentException e) {
         // expected
     }
 }

 public void testConstructor2Arg_NonAdjacentDuplicate_ThrowsIAE() {
     try {
         new Partial(
             new DateTimeFieldType[] {
                 DateTimeFieldType.year(),
                 DateTimeFieldType.monthOfYear(),
                 DateTimeFieldType.year()
             },
             new int[] { 2000, 6, 2001 }
         );
         fail("IllegalArgumentException expected for non-adjacent duplicate types");
     } catch (IllegalArgumentException e) {
         // expected
     }
 }

 public void testConstructor2Arg_AllSame_ThrowsIAE() {
     try {
         new Partial(
             new DateTimeFieldType[] {
                 DateTimeFieldType.year(),
                 DateTimeFieldType.year(),
                 DateTimeFieldType.year()
             },
             new int[] { 2000, 2001, 2002 }
         );
         fail("IllegalArgumentException expected for all same types");
     } catch (IllegalArgumentException e) {
         // expected
     }
 }

 public void testConstructor3Arg_AdjacentDuplicate_ThrowsIAE() {
     try {
         new Partial(
             new DateTimeFieldType[] { DateTimeFieldType.year(), DateTimeFieldType.year() },
             new int[] { 2000, 2001 },
             (Chronology) null
         );
         fail("IllegalArgumentException expected for adjacent duplicate types with chronology");
     } catch (IllegalArgumentException e) {
         // expected
     }
 }

 public void testConstructor3Arg_NonAdjacentDuplicate_ThrowsIAE() {
     try {
         new Partial(
             new DateTimeFieldType[] {
                 DateTimeFieldType.year(),
                 DateTimeFieldType.monthOfYear(),
                 DateTimeFieldType.year()
             },
             new int[] { 2000, 6, 2001 },
             (Chronology) null
         );
         fail("IllegalArgumentException expected for non-adjacent duplicate with chronology");
     } catch (IllegalArgumentException e) {
         // expected
     }
 }

 public void testConstructor2Arg_NullTypes_ThrowsIAE() {
     try {
         new Partial(null, new int[] { 2000 });
         fail("IllegalArgumentException expected for null types array");
     } catch (IllegalArgumentException e) {
         // expected
     }
 }

 public void testConstructor2Arg_NullValues_ThrowsIAE() {
     try {
         new Partial(new DateTimeFieldType[] { DateTimeFieldType.year() }, null);
         fail("IllegalArgumentException expected for null values array");
     } catch (IllegalArgumentException e) {
         // expected
     }
 }

 public void testConstructor2Arg_MismatchedLengths_ThrowsIAE() {
     try {
         new Partial(
             new DateTimeFieldType[] { DateTimeFieldType.year(), DateTimeFieldType.monthOfYear() },
             new int[] { 2000 }
         );
         fail("IllegalArgumentException expected for mismatched array lengths");
     } catch (IllegalArgumentException e) {
         // expected
     }
 }

 public void testConstructor2Arg_NullElementInTypes_ThrowsIAE() {
     try {
         new Partial(
             new DateTimeFieldType[] { DateTimeFieldType.year(), null },
             new int[] { 2000, 6 }
         );
         fail("IllegalArgumentException expected for null element in types array");
     } catch (IllegalArgumentException e) {
         // expected
     }
 }

 public void testConstructor2Arg_EmptyArrays_Success() {
     Partial partial = new Partial(
         new DateTimeFieldType[] {},
         new int[] {}
     );
     assertNotNull(partial);
     assertEquals(0, partial.size());
 }

}