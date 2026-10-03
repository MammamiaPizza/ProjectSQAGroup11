package org.apache.commons.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class OptionsCli252Test {

    @Test
    public void exactLongOptionMatchTakesPrecedenceOverLongerPrefixMatch() {
        Options options = new Options();
        options.addOption("p", "prefix", false, "prefix option");
        options.addOption("x", "prefixplusplus", false, "longer prefix option");

        assertEquals(Collections.singletonList("prefix"),
                options.getMatchingOptions("--prefix"));
    }

    @Test
    public void uniquePartialLongOptionMatchReturnsOnlyMatchingOption() {
        Options options = new Options();
        options.addOption("p", "prefix", false, "prefix option");
        options.addOption("x", "prefixplusplus", false, "longer prefix option");

        assertEquals(Collections.singletonList("prefixplusplus"),
                options.getMatchingOptions("prefixp"));
    }

    @Test
    public void sharedNonExactPrefixReturnsAllMatchingLongOptions() {
        Options options = new Options();
        options.addOption("p", "prefix", false, "prefix option");
        options.addOption("x", "prefixplusplus", false, "longer prefix option");

        List<String> matches = options.getMatchingOptions("pref");

        assertEquals(2, matches.size());
        assertTrue(matches.containsAll(Arrays.asList("prefix", "prefixplusplus")));
    }

    @Test
    public void absentPrefixReturnsNoMatches() {
        Options options = new Options();
        options.addOption("p", "prefix", false, "prefix option");

        assertTrue(options.getMatchingOptions("unknown").isEmpty());
    }

    @Test
    public void exactLongAndShortLookupsRemainDistinctAndAcceptLeadingHyphens() {
        Options options = new Options();
        options.addOption("p", "prefix", false, "prefix option");

        assertNotNull(options.getOption("--prefix"));
        assertEquals("prefix", options.getOption("--prefix").getLongOpt());
        assertTrue(options.hasOption("-p"));
        assertTrue(options.hasOption("--prefix"));
        assertTrue(options.hasLongOption("--prefix"));
        assertFalse(options.hasLongOption("-p"));
    }

@org.junit.Test
public void convenienceAddOptionOverloadsRegisterShortOptions() {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();

    options.addOption("q", "quiet");
    options.addOption("f", true, "file");

    org.junit.Assert.assertTrue(options.hasShortOption("q"));
    org.junit.Assert.assertTrue(options.hasShortOption("f"));
    org.junit.Assert.assertNotNull(options.getOption("f"));
    org.junit.Assert.assertFalse(options.hasLongOption("q"));
}

@org.junit.Test
public void getOptionResolvesShortOptionWithLeadingHyphen() {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("x", "execute", false, "execute");

    org.junit.Assert.assertSame(options.getOption("x"), options.getOption("-x"));
}

@org.junit.Test(expected = java.lang.UnsupportedOperationException.class)
public void getOptionsReturnsAnUnmodifiableCollection() {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("q", "quiet");

    options.getOptions().clear();
}

@org.junit.Test(expected = java.lang.UnsupportedOperationException.class)
public void getRequiredOptionsReturnsAnUnmodifiableList() {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();

    options.getRequiredOptions().add("required");
}
}
