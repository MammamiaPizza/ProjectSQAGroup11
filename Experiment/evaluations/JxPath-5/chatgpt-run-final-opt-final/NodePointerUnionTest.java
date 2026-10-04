package org.apache.commons.jxpath.ri.model;

import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;

public class NodePointerUnionTest extends TestCase {

    public void testUnionOfVariableAndContextNodeReturnsBothNodes() {
        Object root = new Object();
        Object variableValue = new Object();

        JXPathContext context = JXPathContext.newContext(root);
        context.getVariables().declareVariable("var", variableValue);

        List nodes = context.selectNodes("$var | .");

        assertEquals(2, nodes.size());
        assertTrue(nodes.contains(root));
        assertTrue(nodes.contains(variableValue));
    }
}
