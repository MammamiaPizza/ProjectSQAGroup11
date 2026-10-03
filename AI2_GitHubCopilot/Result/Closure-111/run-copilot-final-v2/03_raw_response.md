package com.google.javascript.jscomp;

 import static com.google.javascript.rhino.jstype.JSTypeNative.ARRAY_TYPE;

 import com.google.common.base.Function;
 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CodingConvention;
 import com.google.javascript.jscomp.type.ClosureReverseAbstractInterpreter;
 import com.google.javascript.rhino.jstype.JSType;
 import com.google.javascript.rhino.jstype.JSTypeNative;
 import com.google.javascript.rhino.jstype.JSTypeRegistry;
 import com.google.javascript.rhino.jstype.ObjectType;

 import java.lang.reflect.Constructor;
 import java.lang.reflect.Field;
 import java.util.Map;

 import junit.framework.TestCase;

 public class ClosureReverseAbstractInterpreterTest extends TestCase {

   private Compiler compiler;
   private JSTypeRegistry typeRegistry;
   private ClosureReverseAbstractInterpreter interpreter;
   private Function<?, JSType> isArrayRestricter;
   private Constructor<?> typeRestrictionCtor;

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     compiler = new Compiler();
     typeRegistry = compiler.getTypeRegistry();
     CodingConvention convention = compiler.getCodingConvention();
     interpreter = new ClosureReverseAbstractInterpreter(convention, typeRegistry);

     Field restrictersField =
ClosureReverseAbstractInterpreter.class.getDeclaredField("restricters");
     restrictersField.setAccessible(true);
     @SuppressWarnings({"rawtypes", "unchecked"})
     Map restricters = (Map) restrictersField.get(interpreter);
     isArrayRestricter = (Function<?, JSType>) restricters.get("isArray");

     Class<?>[] innerClasses = ClosureReverseAbstractInterpreter.class.getDeclaredClasses();
     for (Class<?> inner : innerClasses) {
       if ("TypeRestriction".equals(inner.getSimpleName())) {
         typeRestrictionCtor = inner.getDeclaredConstructor(JSType.class, boolean.class);
         typeRestrictionCtor.setAccessible(true);
         break;
       }
     }
   }

   private Object createTypeRestriction(JSType type, boolean outcome) throws Exception {
     return typeRestrictionCtor.newInstance(type, outcome);
   }

   private JSType applyIsArray(JSType type, boolean outcome) throws Exception {
     @SuppressWarnings("unchecked")
     Function<Object, JSType> restricter = (Function<Object, JSType>) (Object) isArrayRestricter;
     return restricter.apply(createTypeRestriction(type, outcome));
   }

   public void testIsArrayTrueNullType() throws Exception {
     JSType result = applyIsArray(null, true);
     assertEquals(typeRegistry.getNativeType(ARRAY_TYPE), result);
   }

   public void testIsArrayFalseNullType() throws Exception {
     JSType result = applyIsArray(null, false);
     assertNull(result);
   }

   public void testIsArrayTrueArrayType() throws Exception {
     JSType arrayType = typeRegistry.getNativeType(ARRAY_TYPE);
     JSType result = applyIsArray(arrayType, true);
     assertEquals(arrayType, result);
   }

   public void testIsArrayFalseArrayType() throws Exception {
     JSType arrayType = typeRegistry.getNativeType(ARRAY_TYPE);
     JSType result = applyIsArray(arrayType, false);
     assertNull(result);
   }

   public void testIsArrayTrueNonArrayObjectType() throws Exception {
     ObjectType objectType = typeRegistry.getNativeObjectType(JSTypeNative.OBJECT_TYPE);
     JSType result = applyIsArray(objectType, true);
     assertNull(result);
   }

   public void testIsArrayFalseNonArrayObjectType() throws Exception {
     ObjectType objectType = typeRegistry.getNativeObjectType(JSTypeNative.OBJECT_TYPE);
     JSType result = applyIsArray(objectType, false);
     assertEquals(objectType, result);
   }

   public void testIsArrayTrueArraySubtype() throws Exception {
     ObjectType arrayType = typeRegistry.getNativeType(ARRAY_TYPE);
     ObjectType arraySubtype = typeRegistry.createObjectType("SubArray", arrayType);
     JSType result = applyIsArray(arraySubtype, true);
     assertEquals(arrayType, result);
   }

   public void testIsArrayFalseArraySubtype() throws Exception {
     ObjectType arrayType = typeRegistry.getNativeType(ARRAY_TYPE);
     ObjectType arraySubtype = typeRegistry.createObjectType("SubArray", arrayType);
     JSType result = applyIsArray(arraySubtype, false);
     assertNull(result);
   }

   public void testIsArrayTrueUnionWithArray() throws Exception {
     JSType arrayType = typeRegistry.getNativeType(ARRAY_TYPE);
     JSType stringType = typeRegistry.getNativeType(JSTypeNative.STRING_TYPE);
     JSType unionType = typeRegistry.createUnionType(arrayType, stringType);
     JSType result = applyIsArray(unionType, true);
     assertEquals(arrayType, result);
   }

   public void testIsArrayFalseUnionWithArray() throws Exception {
     JSType arrayType = typeRegistry.getNativeType(ARRAY_TYPE);
     JSType stringType = typeRegistry.getNativeType(JSTypeNative.STRING_TYPE);
     JSType unionType = typeRegistry.createUnionType(arrayType, stringType);
     JSType result = applyIsArray(unionType, false);
     assertEquals(stringType, result);
   }
 }