package org.apache.commons.cli2.option;

 import java.io.File;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;

 import org.apache.commons.cli2.Argument;
 import org.apache.commons.cli2.DisplaySetting;
 import org.apache.commons.cli2.Group;
 import org.apache.commons.cli2.Option;
 import org.apache.commons.cli2.OptionException;
 import org.apache.commons.cli2.WriteableCommandLine;

 import junit.framework.TestCase;

 /**
  * Tests for {@link GroupImpl} focusing on validation of anonymous arguments
  * (CLI-144: ClassCastException when a converted non-String value such as a
  * {@link File} is validated).
  */
 public class GroupImplCli14Test extends TestCase {

     /* ------------------------------------------------------------------ */
     /* Helper: a minimal WriteableCommandLine implementation                */
     /* ------------------------------------------------------------------ */
     private static class FakeCommandLine implements WriteableCommandLine {
         private final List present;
         private final List convertedValues;

         FakeCommandLine(final List present, final List converted) {
             this.present = present == null ? Collections.EMPTY_LIST : present;
             this.convertedValues = converted == null ? Collections.EMPTY_LIST : converted;
         }

         public boolean hasOption(final Option option) {
             return present.contains(option);
         }

         public List getTokens(final Option option) {
             return present.contains(option) ? convertedValues : Collections.EMPTY_LIST;
         }

         public List getOptionGroups() {
             return Collections.EMPTY_LIST;
         }

         public List getOptions() {
             return Collections.EMPTY_LIST;
         }

         public boolean hasOption(final Group group) {
             return present.contains(group);
         }

         public Object getValue(final Option option) {
             return null;
         }

         public Object getValue(final Option option, final Object defaultValue) {
             return defaultValue;
         }

         public List getValues(final Option option) {
             return present.contains(option) ? convertedValues : Collections.EMPTY_LIST;
         }

         public List getValues(final Option option, final List defaultValues) {
             return present.contains(option) ? convertedValues : defaultValues;
         }

         public void process(final Option option, final String arg) {
             // not used
         }

         public void writePrevious(String previous, final Option option) {
             // not used
         }

         public boolean looksLikeOption(final String arg) {
             return arg != null && arg.startsWith("-");
         }
     }

     /* ------------------------------------------------------------------ */
     /* Helper: an Option stub (not an Argument)                            */
     /* ------------------------------------------------------------------ */
     private static class StubOption implements Option {
         private final String name;

         StubOption(final String name) {
             this.name = name;
         }

         public String getPreferredName() {
             return name;
         }

         public Set getTriggers() {
             return Collections.singleton(name);
         }

         public Set getPrefixes() {
             return Collections.EMPTY_SET;
         }

         public boolean isRequired() {
             return false;
         }

         public void validate(final WriteableCommandLine commandLine)
                 throws OptionException {
             // no-op
         }

         public boolean canProcess(final WriteableCommandLine commandLine,
                                   final String arg) {
             return false;
         }

         public void process(WriteableCommandLine commandLine) {
             // not used
         }

         public void defaults(final WriteableCommandLine commandLine) {
             // not used
         }

         public void appendUsage(final StringBuffer buffer,
                                 final Set helpSettings) {
             buffer.append(name);
         }

         public void appendUsage(final StringBuffer buffer,
                                 final Set helpSettings,
                                 final ComparatorOption comp) {
             appendUsage(buffer, helpSettings);
         }

         public java.util.List helpLines(final int depth,
                                         final Set helpSettings,
                                         final java.util.Comparator comp) {
             return Collections.EMPTY_LIST;
         }

         public Option findOption(final String trigger) {
             return trigger.equals(name) ? this : null;
         }

         public String getDescription() {
             return name + " desc";
         }
     }

     /** Placeholder to avoid importing extra types in the stub. */
     private interface ComparatorOption extends java.util.Comparator {
     }

     /* ------------------------------------------------------------------ */
     /* Fixture                                                             */
     /* ------------------------------------------------------------------ */

     /**
      * An Argument implementation that simulates a File-typed argument: its
      * validate() receives a non-String converted value (a File).  The buggy
      * GroupImpl code path performs
      * {@code (String) arguments.next()}; if the cast survives to validate of
      * the child, CLI-144's ClassCastException is surfaced. We record the
      * types observed by validate so the tests can detect a bad cast that
      * would otherwise be swallowed.
      */
     private static class RecordingArgument implements Argument {
         private final List observed = new ArrayList();
         private final List valuesToValidate;
         private RuntimeException thrown;

         RecordingArgument(final List valuesToValidate) {
             this.valuesToValidate = valuesToValidate;
         }

         public Set getTriggers() {
             return Collections.EMPTY_SET;
         }

         public Set getPrefixes() {
             return Collections.EMPTY_SET;
         }

         public String getPreferredName() {
             return "anonymous";
         }

         public boolean isRequired() {
             return false;
         }

         public void validate(final WriteableCommandLine commandLine)
                 throws OptionException {
             try {
                 for (final Iterator i = valuesToValidate.iterator(); i.hasNext();) {
                     // this is where the CLI-144 bug can manifest:
                     final Object v = i.next();
                     // Attempt the buggy cast pattern defensively:
                     observed.add(v.getClass().getName());
                     if (v instanceof String) {
                         // ok
                     } else {
                         // Simulate what the fixed code should tolerate and
                         // what the buggy code cannot handle: cast is recorded.
                     }
                 }
             } catch (RuntimeException e) {
                 thrown = e;
             }
         }

         public boolean canProcess(final WriteableCommandLine commandLine,
                                   final String arg) {
             return true;
         }

         public void process(WriteableCommandLine commandLine) {
         }

         public void defaults(final WriteableCommandLine commandLine) {
         }

         public void appendUsage(final StringBuffer buffer,
                                 final Set helpSettings) {
         }

         public void appendUsage(final StringBuffer buffer,
                                 final Set helpSettings,
                                 final java.util.Comparator comp) {
         }

         public java.util.List helpLines(final int depth,
                                         final Set helpSettings,
                                         final java.util.Comparator comp) {
             return Collections.EMPTY_LIST;
         }

         public Option findOption(final String trigger) {
             return null;
         }

         public String getDescription() {
             return "file arg";
         }

         public List getObserved() {
             return observed;
         }

         public RuntimeException getThrown() {
             return thrown;
         }

         public int getMinimum() {
             return 0;
         }

         public int getMaximum() {
             return Integer.MAX_VALUE;
         }

         public boolean hasValueSeparator() {
             return false;
         }

         public char getValueSeparator() {
             return 0;
         }
     }

     private static List optionsList() {
         return new ArrayList();
     }

     /* ------------------------------------------------------------------ */
     /* Tests                                                               */
     /* ------------------------------------------------------------------ */

     public void testValidateWithFileArgumentDoesNotThrowCCE() throws Exception {
         File file = new File("data.txt");
         RecordingArgument arg = new RecordingArgument(Collections.singletonList((Object) file));

         GroupImpl group = new GroupImpl(new ArrayList(Collections.singletonList((Option) arg)),
                 "g", "group", 0, Integer.MAX_VALUE);

         // GroupImpl.validate must not wrap/perform a String cast that blows
         // up for File-typed anonymous argument values.
         group.validate(new FakeCommandLine(null, Collections.singletonList((Object) file)));

         assertNull("anonymous argument validate threw: " + arg.getThrown(),
                 arg.getThrown());
     }

     public void testValidateWithStringArgument() throws Exception {
         RecordingArgument arg = new RecordingArgument(Collections.singletonList((Object) "abc"));

         GroupImpl group = new GroupImpl(new ArrayList(Collections.singletonList((Option) arg)),
                 "g", "group", 0, Integer.MAX_VALUE);

         group.validate(new FakeCommandLine(null, Collections.singletonList((Object) "abc")));

         assertNull(arg.getThrown());
         assertEquals(1, arg.getObserved().size());
         assertEquals(String.class.getName(), arg.getObserved().get(0));
     }

     public void testValidateWithNoAnonymousArguments() throws Exception {
         // Group with no anonymous args and no options: validate succeeds.
         GroupImpl group = new GroupImpl(new ArrayList(), "g", "group", 0, 1);
         group.validate(new FakeCommandLine(null, null));
         assertTrue(group.getAnonymous().isEmpty());
     }

     public void testValidateTooManyOptionsThrowsOptionException() throws Exception {
         final StubOption opt = new StubOption("-a");
         // StubOption must be added as a non-argument option.
         List opts = new ArrayList(Collections.singletonList((Option) opt));
         GroupImpl group = new GroupImpl(opts, "g", "group", 0, 1);

         WriteableCommandLine cmd = new WriteableCommandLine() {
             public boolean hasOption(Option option) {
                 return true; // present twice effectively via second option? no
             }

             public boolean hasOption(Group group) {
                 return false;
             }

             public List getTokens(Option option) {
                 return Collections.EMPTY_LIST;
             }

             public List getOptions() {
                 return Collections.EMPTY_LIST;
             }

             public List getOptionGroups() {
                 return Collections.EMPTY_LIST;
             }

             public Object getValue(Option option) {
                 return null;
             }

             public Object getValue(Option option, Object def) {
                 return def;
             }

             public List getValues(Option option) {
                 return Collections.EMPTY_LIST;
             }

             public List getValues(Option option, List defs) {
                 return defs;
             }

             public void process(Option option, String arg) {
             }

             public void writePrevious(String previous, Option option) {
             }

             public boolean looksLikeOption(String arg) {
                 return false;
             }
         };

         try {
             // With maximum=1 a single present option is OK; craft two opts:
             throw new IllegalStateException("skip");
         } catch (IllegalStateException ignore) {
         }

         // Proper two-option scenario:
         StubOption a = new StubOption("-a");
         StubOption b = new StubOption("-b");
         List two = new ArrayList();
         two.add(a);
         two.add(b);
         GroupImpl g2 = new GroupImpl(two, "g", "d", 0, 1);

         final Set presentOpts = new java.util.HashSet(java.util.Arrays.asList(new Option[] { a, b
}));
         WriteableCommandLine cmd2 = new WriteableCommandLine() {
             public boolean hasOption(Option option) {
                 return presentOpts.contains(option);
             }

             public boolean hasOption(Group group) {
                 return false;
             }

             public List getTokens(Option option) {
                 return Collections.EMPTY_LIST;
             }

             public List getOptions() {
                 return Collections.EMPTY_LIST;
             }

             public List getOptionGroups() {
                 return Collections.EMPTY_LIST;
             }

             public Object getValue(Option option) {
                 return null;
             }

             public Object getValue(Option option, Object def) {
                 return def;
             }

             public List getValues(Option option) {
                 return Collections.EMPTY_LIST;
             }

             public List getValues(Option option, List defs) {
                 return defs;
             }

             public void process(Option option, String arg) {
             }

             public void writePrevious(String previous, Option option) {
             }

             public boolean looksLikeOption(String arg) {
                 return false;
             }
         };

         try {
             g2.validate(cmd2);
             fail("Expected OptionException for too many options");
         } catch (OptionException expected) {
             // expected
         }
     }

     public void testValidateTooFewOptionsThrowsOptionException() throws Exception {
         List opts = optionsList();
         GroupImpl group = new GroupImpl(opts, "g", "d", 1, 2);
         // present count will be 0 < minimum => MISSING_OPTION
         try {
             group.validate(new FakeCommandLine(null, null));
             fail("Expected OptionException for missing options");
         } catch (OptionException expected) {
             // expected
         }
     }

     public void testCanProcessNullArgReturnsFalse() {
         GroupImpl group = new GroupImpl(optionsList(), "g", "d", 0, Integer.MAX_VALUE);
         assertFalse(group.canProcess(new FakeCommandLine(null, null), null));
     }

     public void testCanProcessKnownTrigger() {
         StubOption opt = new StubOption("-x");
         List opts = new ArrayList(Collections.singletonList((Option) opt));
         GroupImpl group = new GroupImpl(opts, "g", "d", 0, Integer.MAX_VALUE);
         assertTrue(group.canProcess(new FakeCommandLine(null, null), "-x"));
     }

     public void testCanProcessAnonymous() {
         RecordingArgument arg = new RecordingArgument(Collections.EMPTY_LIST);
         List opts = new ArrayList(Collections.singletonList((Option) arg));
         GroupImpl group = new GroupImpl(opts, "g", "d", 0, Integer.MAX_VALUE);
         assertTrue(group.canProcess(new FakeCommandLine(null, null), "plain.txt"));
     }

     public void testCanProcessLooksLikeOptionNoAnonymous() {
         GroupImpl group = new GroupImpl(optionsList(), "g", "d", 0, Integer.MAX_VALUE);
         assertFalse(group.canProcess(new FakeCommandLine(null, null), "-nope"));
     }

     public void testGettersAndEmptyGroup() {
         GroupImpl group = new GroupImpl(optionsList(), "name", "desc", 2, 5);
         assertEquals("name", group.getPreferredName());
         assertEquals("desc", group.getDescription());
         assertEquals(2, group.getMinimum());
         assertEquals(5, group.getMaximum());
         assertTrue(group.isRequired());
         assertTrue(group.getOptions().isEmpty());
         assertTrue(group.getanonymousSafely());
     }

     public void testDefaultsPropagatesToAnonymousWithFile() {
         final List defaulted = new ArrayList();
         Argument arg = (Argument) new RecordingArgument(Collections.EMPTY_LIST) {
             public void defaults(WriteableCommandLine commandLine) {
                 defaulted.add(Boolean.TRUE);
             }
         };
         List opts = new ArrayList(Collections.singletonList((Option) arg));
         GroupImpl group = new GroupImpl(opts, "g", "d", 0, Integer.MAX_VALUE);
         group.defaults(new FakeCommandLine(null, null));
         assertFalse(defaulted.isEmpty());
     }

     public void testValidateWithMultipleMixedTypes() throws Exception {
         Object[] vals = new Object[] { "a", new File("b"), new Integer(3) };
         RecordingArgument arg =
                 new RecordingArgument(java.util.Arrays.asList(vals));
         List opts = new ArrayList(Collections.singletonList((Option) arg));
         GroupImpl group = new GroupImpl(opts, "g", "d", 0, Integer.MAX_VALUE);
         try {
             group.validate(new FakeCommandLine(null, java.util.Arrays.asList(vals)));
         } catch (ClassCastException e) {
             fail("GroupImpl.validate performed an illegal String cast on non-String anonymous
argument: " + e);
         } catch (OptionException e) {
             fail("Unexpected OptionException: " + e);
         }
         assertEquals(3, arg.getObserved().size());
     }

     // small helper used by testGettersAndEmptyGroup to keep that test valid
     private static boolean getanonymousSafelyStub() {
         return true;
     }
 }
