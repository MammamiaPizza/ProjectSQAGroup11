package org.apache.commons.cli2.commandline;

import junit.framework.TestCase;
import java.util.*;

import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.WriteableCommandLine;

/**

 - Tests for {@link WriteableCommandLineImpl} focusing on CLI-158:
 - default values should appear in {@code getNormalised()} output.
  */
 public class WriteableCommandLineImplTest extends TestCase { private static final Set PREFIXES =
Collections.singleton("-");

  // Minimal Option stub implementing only the methods used by WriteableCommandLineImpl private
static class StubOption implements Option {
    private final String preferredName;
    private final Set prefixes;
    private final List triggers;

 StubOption(String preferredName, Set prefixes, List triggers) {
   this.preferredName = preferredName;
   this.prefixes = prefixes;
   this.triggers = triggers;
 }

 public String getPreferredName() { return preferredName; }
 public Set getPrefixes() { return prefixes; }
 public List getTriggers() { return triggers; }

 // Remaining Option methods – unused, return safe defaults
 public String getId() { return preferredName; }
 public String getDescription() { return ""; }
 public boolean isRequired() { return false; }
 public Option getParent() { return null; }
 public List getChildren() { return Collections.EMPTY_LIST; }
 public org.apache.commons.cli2.Argument getArgument() { return null; }
 public int getMinimum() { return 0; }
 public int getMaximum() { return Integer.MAX_VALUE; }
 public void validate(WriteableCommandLine commandLine) { }
 public boolean canProcess(WriteableCommandLine commandLine, String arg) { return false; }
 public void process(WriteableCommandLine commandLine, java.util.ListIterator args) { }
 public void addHelp(StringBuffer buffer, Set helpSettings) { }
 public void defaultTo(WriteableCommandLine commandLine) { } }

  private WriteableCommandLineImpl createCmd(List normalised) {
    StubOption root = new StubOption("root", PREFIXES, Collections.EMPTY_LIST);
    return new WriteableCommandLineImpl(root, normalised); }

  // ---------- CLI-158 specific tests ----------

  // Failing – bug CLI-158: default value should be appended to normalised list public void
testSingleOptionSingleArgument() {
    WriteableCommandLineImpl cmd = createCmd(new ArrayList<>(Collections.singletonList("1")));
    Option opt = new StubOption("opt", PREFIXES, Collections.singletonList("-o"));
    cmd.addOption(opt);
    cmd.setDefaultValues(opt, Collections.singletonList("1000"));
    List expected = Arrays.asList("1", "1000");
    assertEquals(expected, cmd.getNormalised()); }

  // Failing – bug CLI-158: all defaults should appear even when maximum args given public void
testSingleOptionMaximumNumberOfArguments() {
    WriteableCommandLineImpl cmd = createCmd(new ArrayList<>(Arrays.asList("1", "2")));
    Option opt = new StubOption("opt", PREFIXES, Collections.singletonList("-o"));
    cmd.addOption(opt);
    cmd.setDefaultValues(opt, Collections.singletonList("10000"));
    List expected = Arrays.asList("1", "2", "10000");
    assertEquals(expected, cmd.getNormalised()); }

  // Failing: default should appear when no explicit args are given public void
testDefaultWhenNoExplicitArgs() {
    WriteableCommandLineImpl cmd = createCmd(new ArrayList<>());
    Option opt = new StubOption("opt", PREFIXES, Collections.singletonList("-o"));
    cmd.addOption(opt);
    cmd.setDefaultValues(opt, Collections.singletonList("defaultVal"));
    assertEquals(Collections.singletonList("defaultVal"), cmd.getNormalised()); }

  // Should pass – no defaults set, so normalised stays empty public void
testNormalisedWithoutDefaults() {
    WriteableCommandLineImpl cmd = createCmd(Collections.EMPTY_LIST);
    assertTrue(cmd.getNormalised().isEmpty()); }

  // ---------- toString (CLI-158 visible symptom) ----------

  // Failing: toString must include defaults as shown in bug report public void
testToStringWithDefaults() {
    WriteableCommandLineImpl cmd = createCmd(new ArrayList<>(Collections.singletonList("1")));
    Option opt = new StubOption("opt", PREFIXES, Collections.singletonList("-o"));
    cmd.addOption(opt);
    cmd.setDefaultValues(opt, Collections.singletonList("1000"));
    assertEquals("1 1000", cmd.toString()); }

  // Should pass – quotes around arguments containing spaces public void testToStringQuoting() {
    WriteableCommandLineImpl cmd = createCmd(new ArrayList<>(Arrays.asList("hello world",
"simple")));
    assertEquals(""hello world" simple", cmd.toString()); }

  // ---------- getValues / defaults behaviour ----------

  public void testGetValuesUsesStoredDefaults() {
    WriteableCommandLineImpl cmd = createCmd(Collections.EMPTY_LIST);
    Option opt = new StubOption("opt", PREFIXES, Collections.singletonList("-o"));
    cmd.addOption(opt);
    cmd.setDefaultValues(opt, Arrays.asList("a", "b"));
    List vals = cmd.getValues(opt, null);
    assertEquals(Arrays.asList("a", "b"), vals); }

  public void testGetValuesPrefersProvidedDefaults() {
    WriteableCommandLineImpl cmd = createCmd(Collections.EMPTY_LIST);
    Option opt = new StubOption("opt", PREFIXES, Collections.singletonList("-o"));
    cmd.addOption(opt);
    List vals = cmd.getValues(opt, Arrays.asList("x", "y"));
    assertEquals(Arrays.asList("x", "y"), vals); }

  public void testGetValuesReturnsEmptyIfNothing() {
    WriteableCommandLineImpl cmd = createCmd(Collections.EMPTY_LIST);
    Option opt = new StubOption("opt", PREFIXES, Collections.singletonList("-o"));
    cmd.addOption(opt);
    assertTrue(cmd.getValues(opt, null).isEmpty()); }

  // ---------- addValue / getUndefaultedValues ----------

  public void testAddValueAndGetUndefaultedValues() {
    WriteableCommandLineImpl cmd = createCmd(Collections.EMPTY_LIST);
    Option opt = new StubOption("opt", PREFIXES, Collections.singletonList("-o"));
    cmd.addOption(opt);
    cmd.addValue(opt, "val1");
    List vals = cmd.getUndefaultedValues(opt);
    assertEquals(Collections.singletonList("val1"), vals); }

  // ---------- addSwitch / getSwitch ----------

  public void testAddSwitchAndGetSwitch() {
    WriteableCommandLineImpl cmd = createCmd(Collections.EMPTY_LIST);
    Option opt = new StubOption("opt", PREFIXES, Collections.singletonList("-s"));
    cmd.addSwitch(opt, true);
    assertEquals(Boolean.TRUE, cmd.getSwitch(opt, null)); }

  // Boundary: getSwitch falls back to defaultSwitch public void testGetSwitchUsesDefaultSwitch() {
    WriteableCommandLineImpl cmd = createCmd(Collections.EMPTY_LIST);
    Option opt = new StubOption("opt", PREFIXES, Collections.singletonList("-s"));
    cmd.addOption(opt);
    cmd.setDefaultSwitch(opt, Boolean.FALSE);
    assertEquals(Boolean.FALSE, cmd.getSwitch(opt, null)); }
}
