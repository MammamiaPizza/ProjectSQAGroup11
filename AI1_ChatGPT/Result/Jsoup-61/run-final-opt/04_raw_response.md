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