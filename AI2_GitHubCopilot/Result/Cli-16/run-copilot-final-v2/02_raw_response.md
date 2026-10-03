package org.apache.commons.cli2.bug;

import junit.framework.TestCase;
import org.apache.commons.cli2.;
import org.apache.commons.cli2.builder.;
import org.apache.commons.cli2.commandline.WriteableCommandLineImpl;
import java.util.*;

public class BugCLI16Test extends TestCase {

 private DefaultOptionBuilder dob;
 private GroupBuilder gb;
 private ArgumentBuilder ab;

 public void setUp() {
     dob = new DefaultOptionBuilder();
     gb = new GroupBuilder();
     ab = new ArgumentBuilder();
 }

 private Option createHelpOption() {
     return dob.withShortName("h").withLongName("help").withDescription("Print help").create();
 }

 private Group createLoginGroup(int min, int max) {
     Option username = ab.withName("username").withMinimum(1).create();
     return gb.withName("login").withDescription("Login").withMinimum(min).withMaximum(max)
             .withOption(username).create();
 }

 private Group createParentGroup(String name, int min, int max, Option child) {
     return gb.withName(name).withDescription("parent").withMinimum(min).withMaximum(max)
             .withOption(child).create();
 }

 public void testGroupAddedOnChildAddOption() {
     Option childArg = ab.withName("childArg").withMinimum(1).create();
     Group parent = createParentGroup("parent", 0, Integer.MAX_VALUE, childArg);
     Group rootGroup = gb.withName("root").withMinimum(0).withOption(parent).create();
     WriteableCommandLineImpl cmd = new WriteableCommandLineImpl(rootGroup, new ArrayList());
     cmd.addOption(childArg);

     List options = cmd.getOptions();
     boolean foundParent = false;
     for (Iterator it = options.iterator(); it.hasNext();) {
         Option opt = (Option) it.next();
         if ("parent".equals(opt.getPreferredName())) {
             foundParent = true;
             break;
         }
     }
     assertTrue("Parent group should be present in getOptions after adding a child", foundParent);
 }

 public void testGetOptionsOrderAfterDefaults() {
     Group loginGroup = createLoginGroup(0, Integer.MAX_VALUE);
     Option help = createHelpOption();
     Group rootGroup =
gb.withName("root").withMinimum(0).withOption(help).withOption(loginGroup).create();
     WriteableCommandLineImpl cmd = new WriteableCommandLineImpl(rootGroup, new ArrayList());
     rootGroup.defaults(cmd);

     List options = cmd.getOptions();
     boolean hasLogin = false;
     boolean hasHelp = false;
     for (Iterator it = options.iterator(); it.hasNext();) {
         Option opt = (Option) it.next();
         if ("login".equals(opt.getPreferredName())) hasLogin = true;
         if ("help".equals(opt.getPreferredName())) hasHelp = true;
     }
     assertTrue("Defaults should include login group", hasLogin);
     assertTrue("Defaults should include help option", hasHelp);
 }

 public void testSingleChildOptionWithRequiredGroup() {
     Option child = ab.withName("child").withMinimum(1).create();
     Group parent = createParentGroup("parentOptions", 1, Integer.MAX_VALUE, child);
     Group rootGroup = gb.withName("root").withMinimum(0).withOption(parent).create();

     Parser parser = new Parser();
     try {
         WriteableCommandLine cmd = parser.parse(rootGroup, new String[]{"child"});
     } catch (OptionException e) {
         fail("Single child option should satisfy required parent group: " + e.getMessage());
     }
 }

 public void testMultipleChildOptionsWithRequiredGroup() {
     Option child1 = ab.withName("child1").withMinimum(0).create();
     Option child2 = ab.withName("child2").withMinimum(0).create();
     Group parent = gb.withName("parentOptions").withMinimum(1).withMaximum(10)
             .withOption(child1).withOption(child2).create();
     Group rootGroup = gb.withName("root").withMinimum(0).withOption(parent).create();

     Parser parser = new Parser();
     try {
         WriteableCommandLine cmd = parser.parse(rootGroup, new String[]{"child1", "child2"});
     } catch (OptionException e) {
         fail("Multiple children should satisfy required parent: " + e.getMessage());
     }
 }

 public void testParentOptionAndChildOptionMaximumExceeded() {
     Option child = ab.withName("child").withMinimum(0).create();
     Group parent = createParentGroup("parentOptions", 0, 1, child);
     Group rootGroup = gb.withName("root").withMinimum(0).withOption(parent).create();

     Parser parser = new Parser();
     try {
         WriteableCommandLine cmd = parser.parse(rootGroup, new String[]{"parentOptions", "child"});
         fail("Maximum restriction for parent not verified!");
     } catch (OptionException e) {
         assertTrue("Exception should indicate maximum violation",
                    e.getMessage().toLowerCase().contains("max"));
     }
 }

 public void testSingleChildWithinMaximum() {
     Option child = ab.withName("child").withMinimum(0).create();
     Group parent = createParentGroup("parentOptions", 0, 1, child);
     Group rootGroup = gb.withName("root").withMinimum(0).withOption(parent).create();

     Parser parser = new Parser();
     try {
         WriteableCommandLine cmd = parser.parse(rootGroup, new String[]{"child"});
     } catch (OptionException e) {
         fail("Single child within maximum should be allowed: " + e.getMessage());
     }
 }

 public void testMissingRequiredChildOption() {
     Option child = ab.withName("child").withMinimum(1).create();
     Group parent = createParentGroup("parentOptions", 0, Integer.MAX_VALUE, child);
     Group rootGroup = gb.withName("root").withMinimum(0).withOption(parent).create();

     Parser parser = new Parser();
     try {
         WriteableCommandLine cmd = parser.parse(rootGroup, new String[0]);
         fail("Missing required child option should throw OptionException");
     } catch (OptionException e) {
     }
 }

 public void testAllOptionalGroupSuccess() {
     Option child = ab.withName("child").withMinimum(0).create();
     Group parent = createParentGroup("parentOptions", 0, Integer.MAX_VALUE, child);
     Group rootGroup = gb.withName("root").withMinimum(0).withOption(parent).create();

     Parser parser = new Parser();
     try {
         WriteableCommandLine cmd = parser.parse(rootGroup, new String[0]);
     } catch (OptionException e) {
         fail("All optional group should parse successfully: " + e.getMessage());
     }
 }

 public void testGroupGetTriggersIncludesChildren() {
     Option child = ab.withName("child").create();
     Group parent = createParentGroup("parentOptions", 0, 1, child);

     Set triggers = parent.getTriggers();
     assertTrue("Triggers should contain parent's own trigger", triggers.contains("parentOptions"));
     assertTrue("Triggers should contain child's trigger", triggers.contains("child"));
 }

 public void testFindOptionForChildTrigger() {
     Option child = ab.withName("child").create();
     Group parent = createParentGroup("parentOptions", 0, 1, child);

     Option found = parent.findOption("child");
     assertNotNull("Should find child option by trigger", found);
     assertEquals("Found option should be the child", child.getPreferredName(),
found.getPreferredName());
 }

 public void testDefaultsPropagatesAndAddsParent() {
     Option child = ab.withName("child").withMinimum(1).create();
     Group parent = createParentGroup("parentOptions", 0, 5, child);
     Group rootGroup = gb.withName("root").withMinimum(0).withOption(parent).create();
     WriteableCommandLineImpl cmd = new WriteableCommandLineImpl(rootGroup, new ArrayList());
     rootGroup.defaults(cmd);

     List options = cmd.getOptions();
     assertFalse("Defaults should have added options to the command line", options.isEmpty());
 }

 public void testGetOptionsPreservesOrder() {
     Option opt1 = dob.withShortName("a").create();
     Option opt2 = dob.withShortName("b").create();
     Group rootGroup =
gb.withName("root").withMinimum(0).withOption(opt1).withOption(opt2).create();
     WriteableCommandLineImpl cmd = new WriteableCommandLineImpl(rootGroup, new ArrayList());
     cmd.addOption(opt1);
     cmd.addOption(opt2);

     List options = cmd.getOptions();
     assertEquals("First option should be the first added", opt1, options.get(0));
     assertEquals("Second option should be the second added", opt2, options.get(1));
 }

}