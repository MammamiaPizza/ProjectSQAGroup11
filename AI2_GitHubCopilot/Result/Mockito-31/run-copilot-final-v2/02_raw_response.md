import static org.hamcrest.CoreMatchers.containsString;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertThat;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import org.junit.Test;
 import org.mockito.Mockito;
 import org.mockito.internal.stubbing.defaultanswers.ReturnsSmartNulls;
 import org.mockito.invocation.InvocationOnMock;
 import org.mockito.stubbing.Answer;

 /**
  * Tests for {@link ReturnsSmartNulls} focusing on the format of the smart null
  * toString message, which should include the method name and its arguments.
  */
 public class ReturnsSmartNullsTest {

     // ---------- Interfaces for mock creation ----------

     interface NoArgMethods {
         Object returnObject();
         String returnString();
     }

     interface SingleArgMethods {
         Object withString(String s);
         Object withDouble(double d);
         Object withInt(int i);
     }

     interface MultiArgMethods {
         Object withMixed(String s, int i, Object o);
         Object withSeveral(String a, String b, String c, String d, String e);
     }

     interface PrimitiveArgMethods {
         Object withDoubleArg(double d);
     }

     // ---------- Helper ----------

     /**
      * Creates a mock using {@link ReturnsSmartNulls}, calls the supplied
      * method, and returns the smart null object (which can be used to call
      * toString or other methods).
      */
     private <T> Object smartNullFor(Class<T> type, Invoker<T> invoker) {
         T mock = Mockito.mock(type, new ReturnsSmartNulls());
         return invoker.invoke(mock);
     }

     private interface Invoker<T> {
         Object invoke(T mock);
     }

     // ---------- Tests ----------

     @Test
     public void shouldPrintTheParametersWhenCallingAMethodWithNoArgs() {
         Object smartNull = smartNullFor(NoArgMethods.class,
                 new Invoker<NoArgMethods>() {
                     public Object invoke(NoArgMethods mock) {
                         return mock.returnObject();
                     }
                 });
         String msg = smartNull.toString();
         assertThat(msg, containsString("returnObject()"));
         // No arguments → "withArgs([])"
         assertThat(msg, containsString("withArgs([])"));
     }

     @Test
     public void shouldPrintTheParametersWhenCallingAMethodWithSingleStringArg() {
         Object smartNull = smartNullFor(SingleArgMethods.class,
                 new Invoker<SingleArgMethods>() {
                     public Object invoke(SingleArgMethods mock) {
                         return mock.withString("foo");
                     }
                 });
         String msg = smartNull.toString();
         assertThat(msg, containsString("withString"));
         assertThat(msg, containsString("withArgs([foo])"));
     }

     @Test
     public void shouldPrintTheParametersWhenCallingAMethodWithSingleIntArg() {
         Object smartNull = smartNullFor(SingleArgMethods.class,
                 new Invoker<SingleArgMethods>() {
                     public Object invoke(SingleArgMethods mock) {
                         return mock.withInt(42);
                     }
                 });
         String msg = smartNull.toString();
         assertThat(msg, containsString("withInt"));
         assertThat(msg, containsString("withArgs([42])"));
     }

     @Test
     public void shouldPrintTheParametersWhenCallingAMethodWithSingleDoubleArg() {
         // 3.14 may be represented as 3.14 in the message
         Object smartNull = smartNullFor(SingleArgMethods.class,
                 new Invoker<SingleArgMethods>() {
                     public Object invoke(SingleArgMethods mock) {
                         return mock.withDouble(3.14);
                     }
                 });
         String msg = smartNull.toString();
         assertThat(msg, containsString("withDouble"));
         assertThat(msg, containsString("withArgs([3.14])"));
     }

     @Test
     public void shouldPrintTheParametersWhenCallingAMethodWithMultipleArgs() {
         Object smartNull = smartNullFor(MultiArgMethods.class,
                 new Invoker<MultiArgMethods>() {
                     public Object invoke(MultiArgMethods mock) {
                         return mock.withMixed("a", 2, null);
                     }
                 });
         String msg = smartNull.toString();
         assertThat(msg, containsString("withMixed"));
         // Argument order matters; null should be printed as "null"
         assertThat(msg, containsString("withArgs([a, 2, null])"));
     }

     @Test
     public void shouldPrintTheParametersWhenCallingAMethodWithSeveralArgs() {
         Object smartNull = smartNullFor(MultiArgMethods.class,
                 new Invoker<MultiArgMethods>() {
                     public Object invoke(MultiArgMethods mock) {
                         return mock.withSeveral("x", "y", "z", "w", "v");
                     }
                 });
         String msg = smartNull.toString();
         assertThat(msg, containsString("withSeveral"));
         assertThat(msg, containsString("withArgs([x, y, z, w, v])"));
     }

     @Test
     public void shouldIncludeFullPrefixAndSuffixInToString() {
         Object smartNull = smartNullFor(NoArgMethods.class,
                 new Invoker<NoArgMethods>() {
                     public Object invoke(NoArgMethods mock) {
                         return mock.returnObject();
                     }
                 });
         String msg = smartNull.toString();
         assertTrue(msg.startsWith("SmartNull returned by unstubbed "));
         assertTrue(msg.endsWith(" method on mock"));
     }

     @Test
     public void shouldThrowWhenCallingNonToStringMethodOnSmartNull() {
         Object smartNull = smartNullFor(NoArgMethods.class,
                 new Invoker<NoArgMethods>() {
                     public Object invoke(NoArgMethods mock) {
                         return mock.returnObject();
                     }
                 });
         try {
             // invoke hashCode (or any non-toString method) on the proxy
             smartNull.hashCode();
             fail("Expected a RuntimeException/NullPointerException");
         } catch (RuntimeException e) {
             // expected – the exact exception type is not the focus here
         }
     }

     @Test
     public void shouldNotThrowWhenCallingToStringOnSmartNull() {
         Object smartNull = smartNullFor(NoArgMethods.class,
                 new Invoker<NoArgMethods>() {
                     public Object invoke(NoArgMethods mock) {
                         return mock.returnObject();
                     }
                 });
         // must not throw
         String msg = smartNull.toString();
         assertTrue(msg.length() > 0);
     }

     @Test
     public void shouldWorkWithDifferentReturnTypes() {
         Object smartNull = smartNullFor(NoArgMethods.class,
                 new Invoker<NoArgMethods>() {
                     public Object invoke(NoArgMethods mock) {
                         return mock.returnString(); // String is mockable
                     }
                 });
         String msg = smartNull.toString();
         assertThat(msg, containsString("returnString"));
         assertThat(msg, containsString("withArgs([])"));
     }

     @Test
     public void shouldIncludePrimitiveDoubleArgument() {
         Object smartNull = smartNullFor(PrimitiveArgMethods.class,
                 new Invoker<PrimitiveArgMethods>() {
                     public Object invoke(PrimitiveArgMethods mock) {
                         return mock.withDoubleArg(3.14);
                     }
                 });
         String msg = smartNull.toString();
         assertThat(msg, containsString("withDoubleArg"));
         assertThat(msg, containsString("withArgs([3.14])"));
     }

     @Test
     public void shouldPrintWithArgsOnNullArgument() {
         Object smartNull = smartNullFor(SingleArgMethods.class,
                 new Invoker<SingleArgMethods>() {
                     public Object invoke(SingleArgMethods mock) {
                         return mock.withString(null);
                     }
                 });
         String msg = smartNull.toString();
         assertThat(msg, containsString("withString"));
         assertThat(msg, containsString("withArgs([null])"));
     }
 }