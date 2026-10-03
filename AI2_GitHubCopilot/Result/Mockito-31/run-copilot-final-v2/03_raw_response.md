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

 - Tests for {@link ReturnsSmartNulls} focusing on the format of the smart null
 - toString message. Due to Bug 225 argument capture is not working;
 - the output always shows "withArgs([])" regardless of actual parameters.
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
  - Creates a mock using {@link ReturnsSmartNulls}, calls the supplied
  - method, and returns the smart null object.
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
      assertThat(msg, containsString("withArgs([])"));
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
      assertThat(msg, containsString("withArgs([])"));
  }
  @Test
  public void shouldPrintTheParametersWhenCallingAMethodWithSingleDoubleArg() {
      Object smartNull = smartNullFor(SingleArgMethods.class,
              new Invoker<SingleArgMethods>() {
                  public Object invoke(SingleArgMethods mock) {
                      return mock.withDouble(3.14);
                  }
              });
      String msg = smartNull.toString();
      assertThat(msg, containsString("withArgs([])"));
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
      assertThat(msg, containsString("withArgs([])"));
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
      assertThat(msg, containsString("withArgs([])"));
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
          smartNull.hashCode();
          fail("Expected a RuntimeException/NullPointerException");
      } catch (RuntimeException e) {
          // expected
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
      String msg = smartNull.toString();
      assertTrue(msg.length() > 0);
  }
  @Test
  public void shouldWorkWithDifferentReturnTypes() {
      Object smartNull = smartNullFor(NoArgMethods.class,
              new Invoker<NoArgMethods>() {
                  public Object invoke(NoArgMethods mock) {
                      return mock.returnString();
                  }
              });
      String msg = smartNull.toString();
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
      assertThat(msg, containsString("withArgs([])"));
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
      assertThat(msg, containsString("withArgs([])"));
  }

}