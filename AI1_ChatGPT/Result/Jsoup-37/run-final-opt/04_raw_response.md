@org.junit.Test
public void parentsAreReturnedFromNearestAncestorToOutermostAncestor() {
    org.jsoup.nodes.Element outer = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("div"), "");
    org.jsoup.nodes.Element middle = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("section"), "");
    org.jsoup.nodes.Element inner = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");

    outer.appendChild(middle);
    middle.appendChild(inner);

    org.junit.Assert.assertEquals(2, inner.parents().size());
    org.junit.Assert.assertEquals("section", inner.parents().get(0).tagName());
    org.junit.Assert.assertEquals("div", inner.parents().get(1).tagName());
}

@org.junit.Test
public void classMutationMethodsMaintainMembershipAcrossAddRemoveAndToggle() {
    org.jsoup.nodes.Element element = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("div"), "");

    element.addClass("one").addClass("two").addClass("one");
    org.junit.Assert.assertTrue(element.hasClass("one"));
    org.junit.Assert.assertTrue(element.hasClass("two"));

    element.toggleClass("one").toggleClass("three");
    element.removeClass("two");

    org.junit.Assert.assertFalse(element.hasClass("one"));
    org.junit.Assert.assertFalse(element.hasClass("two"));
    org.junit.Assert.assertTrue(element.hasClass("three"));
}

@org.junit.Test
public void appendAndAppendElementAddParsedAndConstructedChildren() {
    org.jsoup.nodes.Element container = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("div"), "");

    container.append("<p>One</p><p>Two</p>");
    org.jsoup.nodes.Element created = container.appendElement("section").appendText("Three");

    org.junit.Assert.assertEquals(3, container.children().size());
    org.junit.Assert.assertEquals("p", container.child(0).tagName());
    org.junit.Assert.assertEquals("One", container.child(0).html());
    org.junit.Assert.assertEquals("p", container.child(1).tagName());
    org.junit.Assert.assertEquals("Two", container.child(1).html());
    org.junit.Assert.assertEquals("section", created.tagName());
    org.junit.Assert.assertEquals("Three", created.html());
}

@org.junit.Test
public void afterHtmlAndNodeInsertImmediatelyAfterTheElement() {
    org.jsoup.nodes.Element root = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("div"), "");
    org.jsoup.nodes.Element first = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
    org.jsoup.nodes.Element inserted = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("strong"), "");

    root.appendChild(first);
    first.after("<em>markup</em>");
    first.after(inserted);

    org.junit.Assert.assertEquals(3, root.children().size());
    org.junit.Assert.assertEquals("p", root.child(0).tagName());
    org.junit.Assert.assertEquals("strong", root.child(1).tagName());
    org.junit.Assert.assertEquals("em", root.child(2).tagName());
    org.junit.Assert.assertEquals("markup", root.child(2).html());
}