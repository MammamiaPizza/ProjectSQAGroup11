import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ElementClassCaseInsensitiveTest {

    @Test
    public void hasClassMatchesSingleClassIgnoringCase() {
        Element element = new Element("div").attr("class", "MiXeDCaSe");

        assertTrue(element.hasClass("mixedcase"));
        assertTrue(element.hasClass("MIXEDCASE"));
        assertTrue(element.hasClass("MiXeDCaSe"));
    }

    @Test
    public void hasClassMatchesWhitespaceSeparatedTokensIgnoringCase() {
        Element element = new Element("div").attr("class", "\tAlpha\nBETA  gamma ");

        assertTrue(element.hasClass("alpha"));
        assertTrue(element.hasClass("beta"));
        assertTrue(element.hasClass("GAMMA"));
    }

    @Test
    public void hasClassDoesNotMatchOnlyPartOfAToken() {
        Element element = new Element("div").attr("class", "primary-button BUTTONED");

        assertFalse(element.hasClass("primary"));
        assertFalse(element.hasClass("button"));
        assertFalse(element.hasClass("toned"));
    }

    @Test
    public void hasClassReturnsFalseForMissingOrBlankClassAttributes() {
        Element noClass = new Element("div");
        Element blankClass = new Element("div").attr("class", "   \t ");

        assertFalse(noClass.hasClass("anything"));
        assertFalse(blankClass.hasClass("anything"));
    }

    @Test
    public void getElementsByClassFindsAllCaseVariants() {
        Element root = new Element("div");
        root.appendElement("p").attr("class", "Target");
        root.appendElement("span").attr("class", "TARGET other");
        root.appendElement("a").attr("class", "other target");
        root.appendElement("em").attr("class", "not-target");

        Elements matches = root.getElementsByClass("tArGeT");

        assertEquals(3, matches.size());
        assertTrue(matches.get(0).hasClass("target"));
        assertTrue(matches.get(1).hasClass("target"));
        assertTrue(matches.get(2).hasClass("target"));
    }

    @Test
    public void cssClassSelectorFindsAllCaseVariantsWithoutSubstringMatches() {
        Element root = new Element("div");
        root.appendElement("p").attr("class", "CaseClass");
        root.appendElement("p").attr("class", "caseclass extra");
        root.appendElement("p").attr("class", "EXTRa CASECLASS");
        root.appendElement("p").attr("class", "caseclass-suffix");

        Elements matches = root.select(".cAsEcLaSs");

        assertEquals(3, matches.size());
        for (Element match : matches) {
            assertTrue(match.hasClass("caseclass"));
        }
    }
}
