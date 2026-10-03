package com.google.javascript.rhino.jstype;

  import com.google.javascript.rhino.ErrorReporter;
  import com.google.javascript.rhino.jstype.JSType;
  import com.google.javascript.rhino.jstype.JSTypeNative;
  import com.google.javascript.rhino.jstype.JSTypeRegistry;
  import com.google.javascript.rhino.jstype.ObjectType;
  import com.google.javascript.rhino.jstype.RecordTypeBuilder;

  import junit.framework.TestCase;

  public class RecordTypeBug725Test extends TestCase {

    private JSTypeRegistry registry;
    private JSType numberType;
    private JSType stringType;
    private JSType nullType;

    @Override
    protected void setUp() throws Exception {
      ErrorReporter reporter = new ErrorReporter() {
        @Override
        public void error(String message, String sourceName, int line, int lineOffset) {
        }
        @Override
        public void warning(String message, String sourceName, int line, int lineOffset) {
        }
      };
      registry = new JSTypeRegistry(reporter);
      numberType = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
      stringType = registry.getNativeType(JSTypeNative.STRING_TYPE);
      nullType = registry.getNativeType(JSTypeNative.NULL_TYPE);
    }

    public void testEmptyRecord() {
      RecordTypeBuilder builder = new RecordTypeBuilder(registry);
      JSType type = builder.build();
      assertTrue("empty record must be an object", type instanceof ObjectType);
      assertEquals("empty record is OBJECT_TYPE",
          registry.getNativeObjectType(JSTypeNative.OBJECT_TYPE), type);
    }

    public void testDuplicateProperty() {
      RecordTypeBuilder builder = new RecordTypeBuilder(registry);
      builder.addProperty("x", numberType, null);
      RecordTypeBuilder result = builder.addProperty("x", stringType, null);
      assertNull("duplicate property addition returns null", result);
      JSType built = builder.build();
      assertTrue("duplicate property still yields a record", built.isRecordType());
      ObjectType obj = (ObjectType) built;
      JSType propType = obj.getOwnSlot("x").getType();
      assertTrue("duplicate field keeps first type",
          propType.isEquivalentTo(numberType));
    }

    public void testRecordSubtypeWidth() {
      RecordTypeBuilder b1 = new RecordTypeBuilder(registry);
      b1.addProperty("a", numberType, null);
      JSType r1 = b1.build();
      RecordTypeBuilder b2 = new RecordTypeBuilder(registry);
      b2.addProperty("a", numberType, null);
      b2.addProperty("b", stringType, null);
      JSType r2 = b2.build();
      assertTrue("wider record is subtype of narrower", r2.isSubtype(r1));
      assertFalse("narrower is not subtype of wider", r1.isSubtype(r2));
    }

    public void testRecordSubtypeObject() {
      RecordTypeBuilder builder = new RecordTypeBuilder(registry);
      builder.addProperty("a", numberType, null);
      JSType record = builder.build();
      JSType objType = registry.getNativeObjectType(JSTypeNative.OBJECT_TYPE);
      assertTrue("every record is a subtype of OBJECT_TYPE", record.isSubtype(objType));
    }

    public void testAnonymousObjectMatchesRecord() {
      ObjectType anon = (ObjectType) registry.createAnonymousObjectType();
      anon.defineDeclaredProperty("a", numberType, null);
      RecordTypeBuilder builder = new RecordTypeBuilder(registry);
      builder.addProperty("a", numberType, null);
      JSType record = builder.build();
      assertTrue("anonymous with same properties is subtype of record",
          anon.isSubtype(record));
    }

    public void testAnonymousObjectMissingProperty() {
      ObjectType anon = (ObjectType) registry.createAnonymousObjectType();
      // intentionally missing required property
      RecordTypeBuilder builder = new RecordTypeBuilder(registry);
      builder.addProperty("a", numberType, null);
      JSType record = builder.build();
      assertFalse("anonymous missing required property is NOT subtype of record",
          anon.isSubtype(record));
    }

    public void testAnonymousObjectPropertyTypeMismatch() {
      ObjectType anon = (ObjectType) registry.createAnonymousObjectType();
      anon.defineDeclaredProperty("a", stringType, null);
      RecordTypeBuilder builder = new RecordTypeBuilder(registry);
      builder.addProperty("a", numberType, null);
      JSType record = builder.build();
      assertFalse("anonymous with wrong property type is NOT subtype of record",
          anon.isSubtype(record));
    }

    public void testRecordNotSubtypeNull() {
      RecordTypeBuilder builder = new RecordTypeBuilder(registry);
      builder.addProperty("a", numberType, null);
      JSType record = builder.build();
      assertFalse("record is not a subtype of NULL", record.isSubtype(nullType));
    }

    public void testEmptyRecordNotSubtypeNonEmpty() {
      JSType empty = new RecordTypeBuilder(registry).build();
      RecordTypeBuilder builder = new RecordTypeBuilder(registry);
      builder.addProperty("a", numberType, null);
      JSType record = builder.build();
      assertFalse("empty record is not subtype of non-empty record",
          empty.isSubtype(record));
    }

    public void testRecordIsSubtypeSelf() {
      RecordTypeBuilder builder = new RecordTypeBuilder(registry);
      builder.addProperty("a", numberType, null);
      JSType record = builder.build();
      assertTrue("record is subtype of itself", record.isSubtype(record));
    }

    public void testRecordTypeRecognition() {
      RecordTypeBuilder builder = new RecordTypeBuilder(registry);
      builder.addProperty("a", numberType, null);
      JSType record = builder.build();
      assertTrue("must be recognized as record type", record.isRecordType());
      assertTrue("must be recognized as object type", record.isObjectType());
    }

    public void testGetOwnSlotOnAnonymousObject() {
      ObjectType anon = (ObjectType) registry.createAnonymousObjectType();
      anon.defineDeclaredProperty("prop", stringType, null);
      ObjectType.Property slot = anon.getOwnSlot("prop");
      assertNotNull("slot must exist", slot);
      assertEquals("slot type must match", stringType, slot.getType());
    }
  }