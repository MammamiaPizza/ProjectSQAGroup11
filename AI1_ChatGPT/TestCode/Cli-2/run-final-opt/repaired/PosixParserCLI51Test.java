import junit.framework.TestCase;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class PosixParserCLI51Test extends TestCase {

    public void testSingleHyphenStopsOptionRecognitionWhenStopAtNonOptionIsTrue()
            throws Exception {
        Options options = new Options();
        options.addOption("a", false, "flag a");
        options.addOption("o", false, "flag o");

        CommandLine line = new PosixParser().parse(
                options, new String[] { "-a", "-", "-o" }, true);

        assertTrue(line.hasOption("a"));
        assertFalse(line.hasOption("o"));
        assertEquals(2, line.getArgs().length);
        assertEquals("-", line.getArgs()[0]);
        assertEquals("-o", line.getArgs()[1]);
    }

    public void testSingleHyphenAndFollowingOptionArePreservedAsArgumentsInFlattenedTokens() {
        Options options = new Options();
        options.addOption("o", false, "flag o");

        ExposedPosixParser parser = new ExposedPosixParser();
        String[] tokens = parser.flattenTokens(
                options, new String[] { "-", "-o", "tail" }, true);

        assertTokens(tokens, new String[] { "--", "-", "-o", "tail" });
    }

    public void testSingleHyphenStopsBurstingOfFollowingOptionLikeToken()
            throws Exception {
        Options options = new Options();
        options.addOption("o", true, "output");

        CommandLine line = new PosixParser().parse(
                options, new String[] { "-", "-ovalue" }, true);

        assertFalse(line.hasOption("o"));
        assertEquals(2, line.getArgs().length);
        assertEquals("-", line.getArgs()[0]);
        assertEquals("-ovalue", line.getArgs()[1]);
    }

    public void testSingleHyphenCanStillBeArgumentOfPrecedingArgumentOption()
            throws Exception {
        Options options = new Options();
        options.addOption("f", true, "file");
        options.addOption("o", false, "output");

        CommandLine line = new PosixParser().parse(
                options, new String[] { "-f", "-", "-o" }, true);

        assertEquals("-", line.getOptionValue("f"));
        assertTrue(line.hasOption("o"));
        assertEquals(0, line.getArgs().length);
    }

    public void testSingleHyphenDoesNotStopOptionRecognitionWhenStopAtNonOptionIsFalse()
            throws Exception {
        Options options = new Options();
        options.addOption("o", false, "output");

        CommandLine line = new PosixParser().parse(
                options, new String[] { "-", "-o" }, false);

        assertTrue(line.hasOption("o"));
        assertEquals(1, line.getArgs().length);
        assertEquals("-", line.getArgs()[0]);
    }

    private void assertTokens(String[] actual, String[] expected) {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    private static class ExposedPosixParser extends PosixParser {
        public String[] flattenTokens(Options options, String[] arguments,
                                      boolean stopAtNonOption) {
            return flatten(options, arguments, stopAtNonOption);
        }
    }
}
