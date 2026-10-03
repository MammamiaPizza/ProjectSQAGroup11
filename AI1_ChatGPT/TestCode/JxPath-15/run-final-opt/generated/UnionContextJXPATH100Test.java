package org.apache.commons.jxpath.ri.axes;

import java.io.StringReader;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class UnionContextJXPATH100Test extends TestCase {

    private JXPathContext createContext() throws Exception {
        String xml =
            "<vendor>"
                + "<contact>John</contact>"
                + "<contact>Jane</contact>"
                + "<contact>Bob</contact>"
                + "<contact>Jack Black</contact>"
                + "<contact>Alice</contact>"
            + "</vendor>";

        Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(xml)));
        return JXPathContext.newContext(document);
    }

    public void testUnionReturnsFirstDocumentNodeRatherThanFirstOperand()
            throws Exception {
        JXPathContext context = createContext();

        assertEquals("John",
                context.getValue("/vendor[1]/contact[4] | /vendor[1]/contact[1]"));
    }

    public void testUnionIteratorUsesDocumentOrderForMultipleReverseOperands()
            throws Exception {
        JXPathContext context = createContext();

        Iterator values = context.iterate(
                "/vendor[1]/contact[5] | /vendor[1]/contact[2] | /vendor[1]/contact[4]");

        assertTrue(values.hasNext());
        assertEquals("Jane", values.next());
        assertTrue(values.hasNext());
        assertEquals("Jack Black", values.next());
        assertTrue(values.hasNext());
        assertEquals("Alice", values.next());
        assertFalse(values.hasNext());
    }

    public void testPositionPredicatesOnUnionUseDocumentOrder() throws Exception {
        JXPathContext context = createContext();

        assertEquals("John",
                context.getValue("(/vendor[1]/contact[4] | /vendor[1]/contact[1])[1]"));
        assertEquals("Jack Black",
                context.getValue("(/vendor[1]/contact[4] | /vendor[1]/contact[1])[2]"));
    }

    public void testUnionEliminatesDuplicateNodes() throws Exception {
        JXPathContext context = createContext();

        Iterator values = context.iterate(
                "/vendor[1]/contact[1] | /vendor[1]/contact[1]");

        assertTrue(values.hasNext());
        assertEquals("John", values.next());
        assertFalse(values.hasNext());
    }

    public void testUnionOfOutOfRangeSelectionsIsEmpty() throws Exception {
        JXPathContext context = createContext();

        Iterator values = context.iterate(
                "/vendor[1]/contact[99] | /vendor[1]/contact[100]");

        assertFalse(values.hasNext());
    }
}
