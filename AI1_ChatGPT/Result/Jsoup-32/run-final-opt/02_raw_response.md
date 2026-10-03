package org.jsoup.nodes;

import org.jsoup.parser.Tag;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class ElementCloneClassNamesTest {

    private Element element() {
        return new Element(Tag.valueOf("div"), "http://example.com/");
    }

    @Test
    public void clonePreservesAllClassNamesSetThroughClassNamesSetter() {
        Element original = element();
        Set<String> classes = new LinkedHashSet<String>(Arrays.asList("featured", "large", "rounded"));
        original.classNames(classes);

        Element clone = original.clone();

        assertNotSame(original, clone);
        assertEquals(classes, clone.classNames());
        assertEquals(classes, original.classNames());
    }

    @Test
    public void changingCloneClassesDoesNotChangeOriginalClasses() {
        Element original = element();
        original.addClass("alpha").addClass("beta");

        Element clone = original.clone();
        clone.removeClass("alpha").addClass("gamma").toggleClass("beta");

        assertTrue(original.hasClass("alpha"));
        assertTrue(original.hasClass("beta"));
        assertFalse(original.hasClass("gamma"));

        assertFalse(clone.hasClass("alpha"));
        assertFalse(clone.hasClass("beta"));
        assertTrue(clone.hasClass("gamma"));
        assertEquals(Collections.singleton("gamma"), clone.classNames());
    }

    @Test
    public void changingOriginalClassesDoesNotChangeCloneClasses() {
        Element original = element();
        original.addClass("alpha");

        Element clone = original.clone();
        original.toggleClass("alpha").addClass("beta");

        assertFalse(original.hasClass("alpha"));
        assertTrue(original.hasClass("beta"));

        assertTrue(clone.hasClass("alpha"));
        assertFalse(clone.hasClass("beta"));
        assertEquals(Collections.singleton("alpha"), clone.classNames());
    }

    @Test
    public void cloneOfElementWithEmptyClassSetHasIndependentClassNames() {
        Element original = element();
        original.classNames(Collections.<String>emptySet());

        Element clone = original.clone();
        clone.addClass("new-class");

        assertTrue(original.classNames().isEmpty());
        assertFalse(original.hasClass("new-class"));
        assertEquals(Collections.singleton("new-class"), clone.classNames());
    }

    @Test
    public void addRemoveAndToggleClassMaintainClassNameMembership() {
        Element element = element();

        element.addClass("one").addClass("two").addClass("one");
        element.removeClass("one");
        element.toggleClass("two");
        element.toggleClass("three");

        assertFalse(element.hasClass("one"));
        assertFalse(element.hasClass("two"));
        assertTrue(element.hasClass("three"));
        assertEquals(Collections.singleton("three"), element.classNames());
    }
}