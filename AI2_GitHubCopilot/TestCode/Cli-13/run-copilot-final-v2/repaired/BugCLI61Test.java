package org.apache.commons.cli2.bug;

 import junit.framework.TestCase;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 import java.util.regex.Pattern;

 import org.apache.commons.cli2.Argument;
 import org.apache.commons.cli2.Option;
 import org.apache.commons.cli2.OptionException;
 import org.apache.commons.cli2.WriteableCommandLine;
 import org.apache.commons.cli2.commandline.WriteableCommandLineImpl;
 import org.apache.commons.cli2.option.ArgumentImpl;
 import org.apache.commons.cli2.validation.InvalidArgumentException;
 import org.apache.commons.cli2.validation.Validator;

 /**
  * Testes for the CLI-61 bug: incorrect quoting of the unexpected token in
  * ArgumentImpl validation error messages, and prevention of looksLikeOption
  * loops by ensuring option-like tokenes are reported without quotes.
  */
 public class BugCLI61Test extends TestCase {

     private WriteableCommandLine commandLine;

     public void setUp() {
         // A minimal root option is required to instantiate WriteableCommandLineImpl.
         // The root option supplies the option prefixes used for look-alike detection.
         Argument rootOption = new ArgumentImpl(
                 "root", "root option", 0, 0, '\0', '\0', null, null, null, 0);
         this.commandLine = new WriteableCommandLineImpl(rootOption, new ArrayList());
     }

     // -------------------------------------------------------------------------
     // Valid state testes
     // -------------------------------------------------------------------------

     public void testValidValuesNoException() {
         ArgumentImpl arg = createArg(1, 2);
         commandLine.addValue(arg, "value1");
         commandLine.addValue(arg, "value2");
         try {
             arg.validate(commandLine, arg);
         } catch (OptionException e) {
             fail("Should not have thrown an exception, but got: " + e.getMessage());
         }
     }

     public void testMaximumValuesExactly() {
         ArgumentImpl arg = createArg(1, 2);
         commandLine.addValue(arg, "a");
         commandLine.addValue(arg, "b");
         try {
             arg.validate(commandLine, arg);
         } catch (OptionException e) {
             fail("Should not throw with exactly maximum values: " + e.getMessage());
         }
     }

     public void testMinimumValuesExactly() {
         ArgumentImpl arg = createArg(2, 3);
         commandLine.addValue(arg, "a");
         commandLine.addValue(arg, "b");
         try {
             arg.validate(commandLine, arg);
         } catch (OptionException e) {
             fail("Should not throw with exactly minimum values: " + e.getMessage());
         }
     }

     public void testDefaultValuesSatisfyMinimum() {
         List defaults = Arrays.asList(new String[] { "default1" });
         ArgumentImpl arg = new ArgumentImpl(
                 "arg", "desc", 1, 2, '\0', '\0', null, null, defaults, 1);
         commandLine.setDefaultValues(arg, defaults); // provide defaults via the command line
         try {
             arg.validate(commandLine, arg);
         } catch (OptionException e) {
             fail("Should not throw with defaults: " + e.getMessage());
         }
     }

     // -------------------------------------------------------------------------
     // Missing / excess value testes
     // -------------------------------------------------------------------------

     public void testMissingRequiredValues() {
         ArgumentImpl arg = createArg(1, 2); // requires at least 1 value
         try {
             arg.validate(commandLine, arg);
             fail("Expected OptionException for missing values");
         } catch (OptionException e) {
             assertTrue("Message should indicate missing values, was: " + e.getMessage(),
                     e.getMessage().toLowerCase().contains("missing"));
         }
     }

     public void testTooManyValuesExceptionMessagePattern() {
         ArgumentImpl arg = createArg(0,1);
         commandLine.addValue(arg, "testfile.txt");
         commandLine.addValue(arg, "extra");
         try {
             arg.validate(commandLine, arg);
             fail("Expected OptionException for too many values");
         } catch (OptionException e) {
             String msg = e.getMessage();
             assertTrue("Message should start with 'Unexpected ' but was: " + msg,
                     msg.startsWith("Unexpected "));
             assertTrue("Message should contain ' while processing ' but was: " + msg,
                     msg.contains(" while processing "));
         }
     }

     public void testTooManyValuesTokenNotQuoted() {
         // Use a token that may be checked for quoting; adapt the assertion to the actual
         // message format while still verifying that the token itself is not surrounded
         // by escaped quotation marks.
         ArgumentImpl arg = createArg(0,1);
         String token = "datafile.txt";
         commandLine.addValue(arg, token);
         commandLine.addValue(arg, "extra");
         try {
             arg.validate(commandLine, arg);
             fail("Expected OptionException");
         } catch (OptionException e) {
             String msg = e.getMessage();
             // The token must appear in the message.
             assertTrue("Message should contain the token: " + msg,
                     msg.contains(token));
             // The token should not be enclosed in extra quotation marks.
             assertFalse("Token should not be quoted: " + msg,
                     msg.contains("\"" + token + "\""));
         }
     }

     public void testOptionLikeTokenInErrorMessage() {
         ArgumentImpl arg = createArg(0,1);
         String token = "-v"; // looks like an option trigger
         commandLine.addValue(arg, token);
         commandLine.addValue(arg, "extra");
         try {
             arg.validate(commandLine, arg);
             fail("Expected OptionException");
         } catch (OptionException e) {
             String msg = e.getMessage();
             assertTrue("Message should start with 'Unexpected '", msg.startsWith("Unexpected "));
             assertTrue("Message should contain the token", msg.contains(token));
             assertFalse("Token should not be quoted, even when it looks like an option",
                     msg.contains("\"" + token + "\""));
         }
     }

     public void testMultipleExcessValues() {
         ArgumentImpl arg = createArg(0,2);
         // Use option-like excess to exercise quoting behaviour.
         commandLine.addValue(arg, "a");
         commandLine.addValue(arg, "b");
         commandLine.addValue(arg, "-c"); // first excess (index == maximum)
         commandLine.addValue(arg, "d");
         try {
             arg.validate(commandLine, arg);
             fail("Expected OptionException");
         } catch (OptionException e) {
             String msg = e.getMessage();
             assertTrue("Should report first excess value '-c', got: " + msg,
                     msg.contains("-c"));
             assertFalse("First excess should not be quoted", msg.contains("\"-c\""));
         }
     }

     // -------------------------------------------------------------------------
     // Validator rejection
     // -------------------------------------------------------------------------

     public void testValidatorRejectionMessage() {
         Validator failingValidator = new Validator() {
             public void validate(List values) throws InvalidArgumentException {
                 throw new InvalidArgumentException("bad value");
             }
         };
         ArgumentImpl arg = new ArgumentImpl(
                 "arg", "desc", 1,1, '\0', '\0', failingValidator, null, null, 2);
         commandLine.addValue(arg, "somevalue");
         try {
             arg.validate(commandLine, arg);
             fail("Expected OptionException from validator");
         } catch (OptionException e) {
             String msg = e.getMessage();
             // The validator's message should be present verbatim.
             assertTrue("Message should contain validator message: " + msg,
                     msg.contains("bad value"));
             // Whether the validator message itself is quoted depends on the
             // implementation – verify only that it is visible.
         }
     }

     // -------------------------------------------------------------------------
     // Helper
     // -------------------------------------------------------------------------

     private ArgumentImpl createArg(int min, int max) {
         return new ArgumentImpl(
                 "arg", "description", min, max,
                 '\0', '\0', null, null, null,
                 min * 100 + max);
     }
  }
