package com.fasterxml.jackson.databind.type;

 import static org.junit.Assert.*;

 import org.junit.Test;

 import com.fasterxml.jackson.databind.JavaType;

 public class ResolvedRecursiveTypeTest {

     // Helper classes for testing type hierarchy and self-references
     public static class Base {
         public int base;
     }

     public static class Sub extends Base {
         public int sub;
     }

     public static class SelfRef {
         public SelfRef self;
     }

     private final TypeFactory tf = TypeFactory.defaultInstance();

     @Test
     public void testConstructor() {
         TypeBindings bindings = tf.constructType(Object.class).getBindings();
         ResolvedRecursiveType rrt = new ResolvedRecursiveType(Object.class, bindings);
         assertNotNull(rrt);
         assertNull("_referencedType should initially be null", rrt.getSelfReferencedType());
     }

     @Test
     public void testSetReferenceAndGet() {
         TypeBindings bindings = tf.constructType(String.class).getBindings();
         ResolvedRecursiveType rrt = new ResolvedRecursiveType(String.class, bindings);
         JavaType ref = tf.constructType(String.class);
         rrt.setReference(ref);
         assertSame(ref, rrt.getSelfReferencedType());
     }

     @Test(expected = IllegalStateException.class)
     public void testSetReferenceTwice() {
         TypeBindings bindings = tf.constructType(String.class).getBindings();
         ResolvedRecursiveType rrt = new ResolvedRecursiveType(String.class, bindings);
         rrt.setReference(tf.constructType(String.class));
         rrt.setReference(tf.constructType(Integer.class));
     }

     @Test
     public void testToStringUnresolved() {
         TypeBindings bindings = tf.constructType(Object.class).getBindings();
         ResolvedRecursiveType rrt = new ResolvedRecursiveType(Object.class, bindings);
         String str = rrt.toString();
         assertTrue("ToString should indicate unresolved", str.contains("UNRESOLVED"));
     }

     @Test
     public void testToStringResolved() {
         TypeBindings bindings = tf.constructType(String.class).getBindings();
         ResolvedRecursiveType rrt = new ResolvedRecursiveType(String.class, bindings);
         rrt.setReference(tf.constructType(String.class));
         String str = rrt.toString();
         assertTrue("ToString should contain referenced class name",
str.contains("java.lang.String"));
         assertFalse("ToString should not be UNRESOLVED", str.contains("UNRESOLVED"));
     }

     @Test
     public void testGetGenericSignatureResolved() {
         TypeBindings bindings = tf.constructType(Sub.class).getBindings();
         ResolvedRecursiveType rrt = new ResolvedRecursiveType(Sub.class, bindings);
         JavaType ref = tf.constructType(Sub.class);
         rrt.setReference(ref);

         StringBuilder sb = new StringBuilder();
         rrt.getGenericSignature(sb);
         String actual = sb.toString();

         // Expect the same generic signature as the referenced JavaType
         StringBuilder expectedSb = new StringBuilder();
         ref.getGenericSignature(expectedSb);
         assertEquals("Resolved generic signature should match referenced type",
                 expectedSb.toString(), actual);
     }

     @Test
     public void testGetGenericSignatureUnresolved() {
         TypeBindings bindings = tf.constructType(Sub.class).getBindings();
         ResolvedRecursiveType rrt = new ResolvedRecursiveType(Sub.class, bindings);
         // unresolved => _referencedType is null; should not throw NullPointerException
         // Bug #1647: missing null check leads to NPE, causing base properties to be omitted
         try {
             StringBuilder sb = new StringBuilder();
             rrt.getGenericSignature(sb);
             assertNotNull("Signature should not be null", sb.toString());
         } catch (NullPointerException e) {
             fail("getGenericSignature on unresolved type should not throw NullPointerException (bug
#1647)");
         }
     }

     @Test
     public void testGetErasedSignatureResolved() {
         TypeBindings bindings = tf.constructType(Sub.class).getBindings();
         ResolvedRecursiveType rrt = new ResolvedRecursiveType(Sub.class, bindings);
         JavaType ref = tf.constructType(Sub.class);
         rrt.setReference(ref);

         StringBuilder sb = new StringBuilder();
         rrt.getErasedSignature(sb);
         String actual = sb.toString();

         StringBuilder expectedSb = new StringBuilder();
         ref.getErasedSignature(expectedSb);
         assertEquals("Resolved erased signature should match referenced type",
                 expectedSb.toString(), actual);
     }

     @Test
     public void testGetErasedSignatureUnresolved() {
         TypeBindings bindings = tf.constructType(Sub.class).getBindings();
         ResolvedRecursiveType rrt = new ResolvedRecursiveType(Sub.class, bindings);
         try {
             StringBuilder sb = new StringBuilder();
             rrt.getErasedSignature(sb);
             assertNotNull("Erased signature should not be null", sb.toString());
         } catch (NullPointerException e) {
             fail("getErasedSignature on unresolved type should not throw NullPointerException (bug
#1647)");
         }
     }

     @Test
     public void testEqualsUnresolved() {
         TypeBindings bindings = tf.constructType(String.class).getBindings();
         ResolvedRecursiveType rrt1 = new ResolvedRecursiveType(String.class, bindings);
         ResolvedRecursiveType rrt2 = new ResolvedRecursiveType(String.class, bindings);
         // Both unresolved: equals must return false (preserves "Do NOT ever match unresolved
references")
         assertFalse("Two unresolved types should not be equal", rrt1.equals(rrt2));
         assertTrue("Same instance should be equal", rrt1.equals(rrt1));
         assertFalse("Should not be equal to null", rrt1.equals(null));
     }

     @Test
     public void testEqualsResolved() {
         TypeBindings bindings = tf.constructType(String.class).getBindings();
         ResolvedRecursiveType rrt1 = new ResolvedRecursiveType(String.class, bindings);
         ResolvedRecursiveType rrt2 = new ResolvedRecursiveType(String.class, bindings);
         JavaType ref = tf.constructType(String.class);
         rrt1.setReference(ref);
         rrt2.setReference(ref);
         assertEquals("Two resolved types with same reference should be equal", rrt1, rrt2);
         assertEquals("Hash codes should match for equal objects", rrt1.hashCode(),
rrt2.hashCode());
     }

     @Test
     public void testWithMethodsReturnThis() {
         TypeBindings bindings = tf.constructType(String.class).getBindings();
         ResolvedRecursiveType rrt = new ResolvedRecursiveType(String.class, bindings);
         assertSame(rrt, rrt.withContentType(tf.constructType(Integer.class)));
         assertSame(rrt, rrt.withTypeHandler(new Object()));
         assertSame(rrt, rrt.withContentTypeHandler(new Object()));
         assertSame(rrt, rrt.withValueHandler(new Object()));
         assertSame(rrt, rrt.withContentValueHandler(new Object()));
         assertSame(rrt, rrt.withStaticTyping());
     }

     @Test
     public void testIsContainerTypeFalse() {
         TypeBindings bindings = tf.constructType(String.class).getBindings();
         ResolvedRecursiveType rrt = new ResolvedRecursiveType(String.class, bindings);
         assertFalse("ResolvedRecursiveType is not a container type", rrt.isContainerType());
     }
 }
