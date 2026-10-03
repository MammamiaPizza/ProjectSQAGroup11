package org.apache.commons.cli.bug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.AlreadySelectedException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BugCLI266Test {

 @Test
 public void testOptionComparatorInsertedOrder() {
     OptionGroup group = new OptionGroup();
     Option p = new Option("p", "p option");
     Option x = new Option("x", "x option");
     group.addOption(p);
     group.addOption(x);

     assertEquals(Arrays.asList("p", "x"), optStrings(group));
 }

 @Test
 public void testGetOptionsSingleOption() {
     OptionGroup group = new OptionGroup();
     Option p = new Option("p", "p option");
     group.addOption(p);

     assertEquals(Arrays.asList("p"), optStrings(group));
     assertSame(p, group.getOptions().iterator().next());
 }

 @Test
 public void testEmptyOptionGroupReturnsEmptyCollection() {
     OptionGroup group = new OptionGroup();

     assertTrue(group.getOptions().isEmpty());
     assertTrue(group.getNames().isEmpty());
 }

 @Test
 public void testDuplicateAddKeepsSingleOptionAndOriginalOrder() {
     OptionGroup group = new OptionGroup();
     Option first = new Option("p", "p option");
     group.addOption(first);
     group.addOption(new Option("x", "x option"));
     group.addOption(new Option("p", "replacement"));

     assertEquals(Arrays.asList("p", "x"), optStrings(group));
 }

 @Test
 public void testGetNamesPreservesInsertionOrder() {
     OptionGroup group = new OptionGroup();
     group.addOption(new Option("p", "p option"));
     group.addOption(new Option("x", "x option"));
     group.addOption(new Option("z", "z option"));

     assertEquals(Arrays.asList("p", "x", "z"), new ArrayList<String>(group.getNames()));
 }

 @Test
 public void testSetSelectedValidOption() throws AlreadySelectedException {
     OptionGroup group = new OptionGroup();
     Option p = new Option("p", "p option");
     group.addOption(p);
     group.addOption(new Option("x", "x option"));

     group.setSelected(p);
     assertEquals("p", group.getSelected());
 }

 @Test
 public void testSetSelectedDifferentOptionThrowsAlreadySelectedException() throws
AlreadySelectedException {
     OptionGroup group = new OptionGroup();
     Option p = new Option("p", "p option");
     Option x = new Option("x", "x option");
     group.addOption(p);
     group.addOption(x);
     group.setSelected(p);

     boolean thrown = false;
     try {
         group.setSelected(x);
     } catch (AlreadySelectedException expected) {
         thrown = true;
     }

     assertTrue(thrown);
     assertEquals("p", group.getSelected());
 }

 @Test
 public void testSetSelectedNullResetsSelection() throws AlreadySelectedException {
     OptionGroup group = new OptionGroup();
     Option p = new Option("p", "p option");
     Option x = new Option("x", "x option");
     group.addOption(p);
     group.addOption(x);
     group.setSelected(p);

     group.setSelected(null);
     assertNull(group.getSelected());

     group.setSelected(x);
     assertEquals("x", group.getSelected());
 }

 @Test
 public void testRequiredFlagDefaultsFalseAndCanBeSet() {
     OptionGroup group = new OptionGroup();

     assertFalse(group.isRequired());
     group.setRequired(true);
     assertTrue(group.isRequired());
     group.setRequired(false);
     assertFalse(group.isRequired());
 }

 @Test
 public void testOptionsAddOptionGroupPreservesInsertionOrder() {
     Options options = new Options();
     OptionGroup group = new OptionGroup();
     group.addOption(new Option("p", "p option"));
     group.addOption(new Option("x", "x option"));
     options.addOptionGroup(group);

     List<String> keys = new ArrayList<String>();
     for (Option option : options.getOptions()) {
         keys.add(option.getOpt());
     }

     assertEquals(Arrays.asList("p", "x"), keys);
 }

 @Test
 public void testOptionsAddOptionGroupRegistersGroupAndRequiredStatus() {
     Options options = new Options();
     OptionGroup group = new OptionGroup();
     Option p = new Option("p", "p option");
     p.setRequired(true);
     group.addOption(p);
     group.addOption(new Option("x", "x option"));
     group.setRequired(true);

     options.addOptionGroup(group);

     assertSame(group, options.getOptionGroup(p));
     assertFalse(p.isRequired());
     assertEquals(Arrays.asList(group), options.getRequiredOptions());
 }

 @Test
 public void testOptionsAddRequiredOptionWithoutGroup() {
     Options options = new Options();
     Option r = new Option("r", "required option");
     r.setRequired(true);
     options.addOption(r);

     assertEquals(Arrays.asList("r"), options.getRequiredOptions());
     assertTrue(options.hasShortOption("r"));
 }

 private static List<String> optStrings(OptionGroup group) {
     List<String> result = new ArrayList<String>();
     for (Option option : group.getOptions()) {
         result.add(option.getOpt());
     }
     return result;
 }

}