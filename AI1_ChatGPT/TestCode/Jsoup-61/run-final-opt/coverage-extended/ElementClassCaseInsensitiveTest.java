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

@org.junit.Test
public void parentsReturnsAncestorsFromNearestToFarthest() {
    org.jsoup.nodes.Element grandparent = new org.jsoup.nodes.Element("section");
    org.jsoup.nodes.Element parent = new org.jsoup.nodes.Element("article");
    org.jsoup.nodes.Element child = new org.jsoup.nodes.Element("span");

    grandparent.appendChild(parent);
    parent.appendChild(child);

    org.junit.Assert.assertEquals(2, child.parents().size());
    org.junit.Assert.assertSame(parent, child.parents().get(0));
    org.junit.Assert.assertSame(grandparent, child.parents().get(1));
    org.junit.Assert.assertTrue(grandparent.parents().isEmpty());
}

@org.junit.Test
public void classMutationMethodsAddRemoveAndToggleTokens() {
    org.jsoup.nodes.Element element = new org.jsoup.nodes.Element("div");

    org.junit.Assert.assertSame(element, element.addClass("primary"));
    element.addClass("featured");
    org.junit.Assert.assertTrue(element.hasClass("primary"));
    org.junit.Assert.assertTrue(element.hasClass("featured"));

    element.removeClass("primary");
    org.junit.Assert.assertFalse(element.hasClass("primary"));
    org.junit.Assert.assertTrue(element.hasClass("featured"));

    element.toggleClass("featured");
    org.junit.Assert.assertFalse(element.hasClass("featured"));
    element.toggleClass("featured");
    org.junit.Assert.assertTrue(element.hasClass("featured"));
}

@org.junit.Test
public void appendAndAfterInsertParsedAndNodeSiblingsInOrder() {
    org.jsoup.nodes.Element host = new org.jsoup.nodes.Element("div");
    host.append("<i>one</i>");

    org.jsoup.nodes.Element first = host.child(0);
    first.after("<b>two</b>");

    org.jsoup.nodes.Element third = new org.jsoup.nodes.Element("em").appendText("three");
    host.child(1).after(third);

    org.junit.Assert.assertEquals(3, host.children().size());
    org.junit.Assert.assertEquals("i", host.child(0).tagName());
    org.junit.Assert.assertEquals("b", host.child(1).tagName());
    org.junit.Assert.assertSame(third, host.child(2));
    org.junit.Assert.assertEquals("one", host.child(0).text());
    org.junit.Assert.assertEquals("two", host.child(1).text());
    org.junit.Assert.assertEquals("three", host.child(2).text());
}

@org.junit.Test
public void appendTextNormalizesRegularTextButPreservesPreformattedWhitespace() {
    org.jsoup.nodes.Element regular = new org.jsoup.nodes.Element("div");
    regular.appendText("alpha \n\t beta");

    org.jsoup.nodes.Element preformatted = new org.jsoup.nodes.Element("pre");
    preformatted.appendText("alpha\n  beta");

    org.junit.Assert.assertEquals("alpha beta", regular.text());
    org.junit.Assert.assertEquals("alpha\n  beta", preformatted.text());
}
}
