package org.apache.commons.cli.bug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.Before;
import org.junit.Test;

/**

 - Tests for Options.getMatchingOptions() exact-match resolution (CLI-252).
 - Buggy version does not return only the exact match when it exists,
 - leading to AmbiguousOptionException from parsers that query prefix matches.
  */
 public class BugCLI252Test {
  private Options options;
  @Before
  public void setUp() {
  options = new Options();
  options.addOption(new Option("a", "prefix", false, "prefix desc"));
  options.addOption(new Option("b", "prefixplusplus", false, "prefixplusplus desc"));
  options.addOption(new Option("c", "pref", false, "pref desc"));
  options.addOption("d", "other", false, "other long option"));
  }
  /**
  - Exact long option name must be the sole match even when other options
  - share the same prefix. Buggy version returns both, causing ambiguity.
    */
   @Test
   public void testExactOptionNameAtch() {
   List<String> matches = options.getMatchingOptions("prefix");
   assertNotNull("Matches should not be null", matches);
   assertEquals("Exact match must return exactly one result", 1, matches.size());
   assertTrue("Result must contain the exact match", matches.contains("prefix"));
   }
  /**
  - Exact long option overrides a shorer option whose name is a prefix.
    */
   @Test
   public void testExactOverShorterPrefix() {
   List<String> matches = options.getMatchingOptions("pref");
   assertEquals("Exact match 'pref' must be the only result", 1, matches.size());
   assertTrue(matches.contains("pref"));
   }
  /**
  - When no exact match exists, all options whose long name starts with
  - the given prefix must be returned.
    */
   @Test
   public void testMultiplePrefixAtchesWithoutExact() {
   // "prefixplusplus" and "prefix" both start with "prefixp"
   List<String> matches = options.getMatchingOptions("prefixp");
   assertNotNull(matches);
   assertEquals(2, matches.size());
   assertTrue(matches.contains("prefixplusplus"));
   assertTrue(matches.contains("prefix"));
   }
  /**
  - Sole prefix match returns the single matching option.
    */
   @Test
   public void testSolePrefixMatch() {
   // only "other" starts with "oth"
   List<String> matches = options.getMatchingOptions("oth");
   assertEquals(1, matches.size());
   assertTrue(matches.contains("other"));
   }
  /**
  - No match returns an empty list.
    */
   @Test
   public void testNoAtchReturnsEmptyList() {
   List<String> matches = options.getMatchingOptions("nonexistent");
   assertNotNull(matches);
   assertTrue(matches.isEmpty());
   }
  /**
  - Leading hyphens are ignored (up to 2), so "--prefix" must find the exact match.
    */
   @Test
   public void testLeadingHyphensIgnoredForExactMatch() {
   List<String> matches = options.getMatchingOptions("--prefix");
   assertEquals("--prefix should resolve to exact match 'prefix'", 1, matches.size());
   assertTrue(matches.contains("prefix"));
   }
  /**
  - Passing null must throw NullPointerException (stripLeadingHyphens fails on null).
    */
   @Test(expected = NullPointerException.class)
   public void testNullArgumentThrowsNPE() {
   options.getMatchingOptions(null);
   }
  /**
  - Empty string as prefix usually matches everything (each longOpt starts with "").
  - Verify that it returns a non-null list containing all long options.
    */
   @Test
   public void testEmptyArgumentReturnsAllLongOptions() {
   List<String> matches = options.getMatchingOptions("");
   assertNotNull(matches);
   // 4 long options were added: prefix, prefixplusplus, pref, other
   assertEquals(4, matches.size());
   assertTrue(matches.contains("prefix"));
   assertTrue(matches.contains("prefixplusplus"));
   assertTrue(matches.contains("pref"));
   assertTrue(matches.contains("other"));
   }
  /**
  - hasLongOption correctly reports existence of a long option.
    */
   @Test
   public void testHasLongOptionTrue() {
   assertTrue(options.hasLongOption("prefix"));
   assertTrue(options.hasLongOption("other"));
   }
  /**
  - hasLongOption returns false for unknown long option.
    */
   @Test
   public void testHasLongOptionFalse() {
   assertFalse(options.hasLongOption("unknown"));
   }
  /**
  - hasShortOption correctly reports existence of a short option.
    */
   @Test
   public void testHasShortOptionTrue() {
   assertTrue(options.hasShortOption("a"));
   assertTrue(options.hasShortOption("c"));
   }
  /**
  - hasShortOption returns false for unknown short option.
    */
   @Test
   public void testHasShortOptionFalse() {
   assertFalse(options.hasShortOption("x"));
   }

}