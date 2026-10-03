package com.google.javascript.rhino.jstype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import org.junit.Test;

public class PrototypeObjectTypeTest {

  private JSTypeRegistry newRegistry() {
    return new JSTypeRegistry(null);
  }

  private JSType numberType(JSTypeRegistry registry) {
    return registry.getNativeType(JSTypeNative.NUMBER_TYPE);
  }

  @Test
  public void testRecursiveRecordPrintsUnknownForSelfReference() {
    JSTypeRegistry registry = newRegistry();
    RecordType record = new RecordTypeBuilder(registry).build();

    record.defineDeclaredProperty("loop", record, null);
    record.defineDeclaredProperty("number", numberType(registry), null);
    record.defineDeclaredProperty(
        "string", registry.getNativeType(JSTypeNative.STRING_TYPE), null);

    assertEquals(
        "{loop: ?, number: number, string: string}",
        record.toString());
  }

  @Test
  public void testLongRecordPrintsAllPropertiesWithoutEllipsis() {
    JSTypeRegistry registry = newRegistry();
    RecordTypeBuilder builder = new RecordTypeBuilder(registry);
    JSType number = numberType(registry);

    builder.addProperty("a1", number, null);
    builder.addProperty("a2", number, null);
    builder.addProperty("a3", number, null);
    builder.addProperty("a4", number, null);
    builder.addProperty("a5", number, null);
    builder.addProperty("a6", number, null);

    assertEquals(
        "{a1: number, a2: number, a3: number, a4: number, a5: number, a6: number}",
        builder.build().toString());
  }

  @Test
  public void testRecordStringRepresentationSortsProperties() {
    JSTypeRegistry registry = newRegistry();
    RecordTypeBuilder builder = new RecordTypeBuilder(registry);

    builder.addProperty("zeta", numberType(registry), null);
    builder.addProperty("alpha", registry.getNativeType(JSTypeNative.STRING_TYPE), null);

    assertEquals("{alpha: string, zeta: number}", builder.build().toString());
  }

  @Test
  public void testOwnPropertyLookupAndMissingPropertyType() {
    JSTypeRegistry registry = newRegistry();
    RecordTypeBuilder builder = new RecordTypeBuilder(registry);
    JSType number = numberType(registry);
    builder.addProperty("present", number, null);
    RecordType record = builder.build();

    Set<String> names = record.getOwnPropertyNames();

    assertTrue(record.hasProperty("present"));
    assertTrue(record.hasOwnProperty("present"));
    assertEquals(number, record.getPropertyType("present"));
    assertTrue(names.contains("present"));
    assertFalse(record.hasOwnProperty("definitelyAbsent"));
    assertSame(
        registry.getNativeType(JSTypeNative.UNKNOWN_TYPE),
        record.getPropertyType("definitelyAbsent"));
  }
}