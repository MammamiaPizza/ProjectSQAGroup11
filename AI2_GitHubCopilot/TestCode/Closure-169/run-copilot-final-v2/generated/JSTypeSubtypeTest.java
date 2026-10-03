package com.google.javascript.rhino.jstype;

 import static com.google.javascript.rhino.jstype.JSTypeNative.NUMBER_TYPE;
 import static com.google.javascript.rhino.jstype.JSTypeNative.OBJECT_TYPE;
 import static com.google.javascript.rhino.jstype.JSTypeNative.STRING_TYPE;
 import static com.google.javascript.rhino.jstype.JSTypeNative.UNKNOWN_TYPE;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;

 import com.google.javascript.rhino.ErrorReporter;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import com.google.javascript.rhino.jstype.RecordTypeBuilder.RecordProperty;

 import java.util.HashMap;
 import java.util.Map;

 import org.junit.Before;
 import org.junit.Test;

 /**
  * Tests for the subtype relationship fixes related to Bug 791.
  * Covers RecordType, ArrowType, FunctionType, and UnionType isSubtype logic,
  * especially with UNKNOWN_TYPE properties / parameters.
  */
 public class JSTypeSubtypeTest {

     private JSTypeRegistry registry;

     private JSType unknownType;
     private JSType numberType;
     private JSType stringType;
     private JSType objectType;

     @Before
     public void setUp() throws Exception {
         registry = new JSTypeRegistry(new DummyErrorReporter());
         unknownType = registry.getNativeType(UNKNOWN_TYPE);
         numberType = registry.getNativeType(NUMBER_TYPE);
         stringType = registry.getNativeType(STRING_TYPE);
         objectType = registry.getNativeType(OBJECT_TYPE);
     }

     // ---------- RecordType isSubtype tests ----------

     @Test
     public void testRecordWithUnknownPropertyIsSubtypeOfRecordWithKnownProperty() {
         // { x : ? } should be subtype of { x : number } because UNKNOWN is bottom.
         RecordType subtype = createRecord("x", unknownType);
         RecordType supertype = createRecord("x", numberType);
         assertTrue("Record(unknown) <: Record(number) should be true",
subtype.isSubtype(supertype));
     }

     @Test
     public void testRecordWithKnownPropertyIsNotSubtypeOfRecordWithUnknownProperty() {
         // { x : number } is not a subtype of { x : ? } .
         RecordType subtype = createRecord("x", numberType);
         RecordType supertype = createRecord("x", unknownType);
         assertFalse("Record(number) <: Record(unknown) should be false",
subtype.isSubtype(supertype));
     }

     @Test
     public void testRecordWithExtraPropertyIsSubtype() {
         // { x : number, y : string }  <:  { x : number }  because extra properties are ignored.
         Map<String, RecordProperty> propsA = new HashMap<String, RecordProperty>();
         propsA.put("x", new RecordProperty(numberType, newNode("x")));
         propsA.put("y", new RecordProperty(stringType, newNode("y")));
         RecordType typeA = new RecordType(registry, propsA);

         RecordType typeB = createRecord("x", numberType);

         assertTrue("Record with extra property should be subtype", typeA.isSubtype(typeB));
     }

     @Test
     public void testRecordWithMissingPropertyIsNotSubtype() {
         // { x : number }  <:  { x : number, y : string }  is false because 'y' is missing.
         RecordType typeA = createRecord("x", numberType);

         Map<String, RecordProperty> propsB = new HashMap<String, RecordProperty>();
         propsB.put("x", new RecordProperty(numberType, newNode("x")));
         propsB.put("y", new RecordProperty(stringType, newNode("y")));
         RecordType typeB = new RecordType(registry, propsB);

         assertFalse("Record missing property should not be subtype", typeA.isSubtype(typeB));
     }

     @Test
     public void testRecordPropertySubtypeCompatibility() {
         // { x : number }  <:  { x : (number|string) }  since number <: (number|string)
         RecordType typeA = createRecord("x", numberType);

         JSType unionNumStr = registry.createUnionType(numberType, stringType);
         RecordType typeB = createRecord("x", unionNumStr);

         assertTrue("Record(number) should be subtype of Record(number|string)",
typeA.isSubtype(typeB));
     }

     @Test
     public void testEmptyRecordIsSubtypeOfAnyRecord() {
         // {}  <:  { x : ? }  because empty record satisfies any property requirement.
         RecordType empty = new RecordType(registry, new HashMap<String, RecordProperty>());
         RecordType nonEmpty = createRecord("x", unknownType);
         assertTrue("Empty record should be subtype", empty.isSubtype(nonEmpty));
     }

     @Test
     public void testRecordIsNotSubtypeOfArrowType() {
         RecordType record = createRecord("x", numberType);
         ArrowType arrow = registry.createArrowType(null, numberType);
         // Record is not an arrow, so isSubtype must return false
         assertFalse("Record should not be subtype of arrow", record.isSubtype(arrow));
     }

     // ---------- ArrowType isSubtype tests ----------

     @Test
     public void testArrowWithUnknownReturnIsSubtypeOfArrowWithKnownReturn() {
         // function():?  <:  function():number  because unknown <: number
         ArrowType subArrow = new ArrowType(registry, null, unknownType);
         ArrowType superArrow = new ArrowType(registry, null, numberType);
         assertTrue("Arrow(unknown return) <: Arrow(number return)",
subArrow.isSubtype(superArrow));
     }

     @Test
     public void testArrowWithKnownReturnIsNotSubtypeOfArrowWithUnknownReturn() {
         // function():number  <:  function():?  is false
         ArrowType subArrow = new ArrowType(registry, null, numberType);
         ArrowType superArrow = new ArrowType(registry, null, unknownType);
         assertFalse("Arrow(number return) should not be subtype of Arrow(unknown return)",
subArrow.isSubtype(superArrow));
     }

     // ---------- UnionType isSubtype tests ----------

     @Test
     public void testUnionWithUnknownIsSubtypeOfUnionOfKnownTypes() {
         // (?|number)  <:  (number|string)  because ? is bottom and subtype of every alternate
         JSType unionSub = registry.createUnionType(unknownType, numberType);
         JSType unionSuper = registry.createUnionType(numberType, stringType);
         assertTrue("Union(?|number) <: Union(number|string)", unionSub.isSubtype(unionSuper));
     }

     @Test
     public void testUnionWithoutUnknownIsSubtype() {
         // (number|string)  <:  (number|string|object)  because each element is in the target
         JSType unionSub = registry.createUnionType(numberType, stringType);
         JSType unionSuper = registry.createUnionType(numberType, stringType, objectType);
         assertTrue("Union(number|string) <: Union(number|string|object)",
unionSub.isSubtype(unionSuper));
     }

     // ---------- Helper methods ----------

     private RecordType createRecord(String propName, JSType propType) {
         Map<String, RecordProperty> props = new HashMap<String, RecordProperty>();
         props.put(propName, new RecordProperty(propType, newNode(propName)));
         return new RecordType(registry, props);
     }

     private Node newNode(String name) {
         return Node.newString(Token.NAME, name);
     }

     private static final class DummyErrorReporter implements ErrorReporter {
         @Override
         public void warning(String message, String sourceName, int line,
                             String lineSource, int lineOffset) {}
         @Override
         public void error(String message, String sourceName, int line,
                           String lineSource, int lineOffset) {}
         @Override
         public void error(String message) {}
     }
 }
