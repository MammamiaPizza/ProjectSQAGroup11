package org.jsoup.nodes;

import org.jsoup.select.Elements;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class ElementCloneSiblingNavigationTest {

    @Test
    public void cloneOfElementCanFindFollowingElementSibling() {
        Element parent = new Element("div");
        Element first = parent.appendElement("p").attr("id", "first");
        Element second = parent.appendElement("p").attr("id", "second");

        Element clone = first.clone();

        assertSame(second, first.nextElementSibling());
    }

    @Test
    public void cloneOfLastElementHasNoNextElementSibling() {
        Element parent = new Element("div");
        parent.appendElement("p").attr("id", "first");
        Element last = parent.appendElement("p").attr("id", "last");

        Element clone = last.clone();

        assertNull(clone.nextElementSibling());
    }

    @Test
    public void cloneFindsNextElementSiblingWhenTextNodesArePresent() {
        Element parent = new Element("div");
        parent.appendText("before");
        Element first = parent.appendElement("p").attr("id", "first");
        parent.appendText("between");
        Element second = parent.appendElement("p").attr("id", "second");
        parent.appendText("after");

        Element clone = first.clone();

        assertSame(second, first.nextElementSibling());
    }

    @Test
    public void clonedChildrenNavigateAmongTheirClonedSiblings() {
        Element parent = new Element("div");
        Element container = parent.appendElement("section");
        container.appendElement("p").attr("id", "first");
        container.appendText("separator");
        container.appendElement("p").attr("id", "second");

        Element clone = container.clone();
        Element clonedFirst = clone.child(0);
        Element clonedSecond = clone.child(1);

        assertSame(clonedSecond, clonedFirst.nextElementSibling());

        Elements siblings = clonedFirst.siblingElements();
        assertEquals(1, siblings.size());
        assertSame(clonedSecond, siblings.get(0));
    }

@Test
public void parentsReturnsAncestorsFromClosestToFarthest() {
    Element grandparent = new Element("section");
    Element parent = new Element("article");
    Element child = new Element("p");

    grandparent.appendChild(parent);
    parent.appendChild(child);

    assertEquals(2, child.parents().size());
    assertSame(parent, child.parents().get(0));
    assertSame(grandparent, child.parents().get(1));
}

@Test
public void addClassAddsOnlyNewClassNames() {
    Element element = new Element("div").attr("class", "first");

    assertSame(element, element.addClass("second"));
    element.addClass("first");

    assertEquals("first second", element.attr("class"));
}

@Test
public void appendToAndAfterInsertElementsInExpectedOrder() {
    Element parent = new Element("div");
    Element first = new Element("p");

    first.append("<b>one</b>");
    assertSame(first, first.appendTo(parent));
    assertSame(first, first.after("<em>two</em>"));
    assertSame(first, first.after(new Element("span")));

    assertEquals(3, parent.children().size());
    assertSame(first, parent.child(0));
    assertEquals("span", parent.child(1).tagName());
    assertEquals("em", parent.child(2).tagName());
    assertEquals("<b>one</b>", first.html());
}

@Test
public void textNormalizesWhitespaceExceptInPreformattedElements() {
    Element normal = new Element("div").append("one \n two");
    Element preformatted = new Element("pre").append("one \n two");

    assertEquals("one two", normal.text());
    assertEquals("one \n two", preformatted.text());
}
}
