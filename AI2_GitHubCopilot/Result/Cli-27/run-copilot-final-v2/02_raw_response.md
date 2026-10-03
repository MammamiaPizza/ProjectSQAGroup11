package org.apache.commons.cli;

 import junit.framework.TestCase;

 /**
  * Tests for {@link OptionGroup} targeting CLI-182: getSelected()
  * returns null for long-only options because setSelected() stores
  * only the short opt (getOpt()), ignoring longOpt().
  */
 public class OptionGroupTest extends TestCase {

     private OptionGroup group;
     private Option longOnly;      // --bar
     private Option shortOnly;     // -b
     private Option bothOpt;       // -f/--foo
     private Option otherLong;     // --baz
     private Option otherShort;    // -c

     protected void setUp() {
         group = new OptionGroup();
         longOnly   = new Option(null, "bar", false, "long only option");
         shortOnly  = new Option("b", false, "short only option");
         bothOpt    = new Option("f", "foo", false, "both short and long");
         otherLong  = new Option(null, "baz", false, "other long option");
         otherShort = new Option("c", false, "another short option");
     }

     /** Core bug: getSelected() is null after selecting a long-only option. */
     public void testGetSelectedWithLongOptionOnly() throws AlreadySelectedException {
         group.addOption(longOnly);
         group.setSelected(longOnly);
         assertEquals("bar", group.getSelected());
     }

     /** Short-only options work correctly (getOpt() is non-null). */
     public void testGetSelectedWithShortOptionOnly() throws AlreadySelectedException {
         group.addOption(shortOnly);
         group.setSelected(shortOnly);
         assertEquals("b", group.getSelected());
     }

     /** When both short and long exist, getSelected() returns the short opt. */
     public void testGetSelectedWithBothShortAndLong() throws AlreadySelectedException {
         group.addOption(bothOpt);
         group.setSelected(bothOpt);
         assertEquals("f", group.getSelected());
     }

     /** Selecting a different option from the same group must throw. */
     public void testAlreadySelectedExceptionOnDifferentOption() {
         group.addOption(shortOnly);
         group.addOption(otherShort);
         try {
             group.setSelected(shortOnly);
         } catch (AlreadySelectedException e) {
             fail("First selection should not throw");
         }
         try {
             group.setSelected(otherShort);
             fail("Should have thrown AlreadySelectedException");
         } catch (AlreadySelectedException e) {
             // expected
         }
     }

     /** Bug: AlreadySelectedException is NOT thrown for long-only
      *  options because the stored selection is null after first set. */
     public void testAlreadySelectedExceptionNotThrownForLongOptions()
             throws AlreadySelectedException {
         group.addOption(longOnly);
         group.addOption(otherLong);
         group.setSelected(longOnly);      // stored selected=null (bug)
         try {
             group.setSelected(otherLong); // should throw, but doesn't
             // Bug: no exception, getSelected stays null
         } catch (AlreadySelectedException e) {
             fail("Bug is present: exception incorrectly thrown");
         }
         assertNull(group.getSelected());
     }

     /** Passing null to setSelected() resets the selection. */
     public void testSetSelectedNullResetsSelection() throws AlreadySelectedException {
         group.addOption(shortOnly);
         group.setSelected(shortOnly);
         assertEquals("b", group.getSelected());
         group.setSelected(null);
         assertNull(group.getSelected());
     }

     /** Reselecting the same short option does not throw. */
     public void testReselectSameShortOption() throws AlreadySelectedException {
         group.addOption(shortOnly);
         group.addOption(otherShort);
         group.setSelected(shortOnly);
         group.setSelected(shortOnly); // no exception
         assertEquals("b", group.getSelected());
     }

     /** Reselecting the same long-only option does not throw,
      *  but getSelected() is still null due to the bug. */
     public void testReselectSameLongOption() throws AlreadySelectedException {
         group.addOption(longOnly);
         group.addOption(otherLong);
         group.setSelected(longOnly);
         group.setSelected(longOnly); // no exception, but bug persists
         assertEquals("bar", group.getSelected());
     }

     /** The required flag can be toggled. */
     public void testIsRequired() {
         assertFalse(group.isRequired());
         group.setRequired(true);
         assertTrue(group.isRequired());
     }

     /** getNames() exposes the option keys used internally. */
     public void testGetNames() {
         group.addOption(longOnly);
         group.addOption(shortOnly);
         assertEquals(2, group.getNames().size());
         assertTrue(group.getNames().contains("bar"));
         assertTrue(group.getNames().contains("b"));
     }

     /** getOptions() returns the Option instances. */
     public void testGetOptions() {
         group.addOption(longOnly);
         group.addOption(shortOnly);
         assertEquals(2, group.getOptions().size());
         assertTrue(group.getOptions().contains(longOnly));
         assertTrue(group.getOptions().contains(shortOnly));
     }

     /** A newly created group has no selection. */
     public void testEmptyGroupGetSelectedIsNull() {
         assertNull(group.getSelected());
     }

     /** toString() renders long-only options with a double hyphen. */
     public void testToStringContainsLongOption() {
         group.addOption(longOnly);
         assertTrue(group.toString().indexOf("--bar") != -1);
     }
 }