public void testComputeContextDependentFunctionClassifications() {
    assertTrue(new org.apache.commons.jxpath.ri.compiler.CoreFunction(
            org.apache.commons.jxpath.ri.compiler.Compiler.FUNCTION_LAST,
            null).computeContextDependent());

    assertTrue(new org.apache.commons.jxpath.ri.compiler.CoreFunction(
            org.apache.commons.jxpath.ri.compiler.Compiler.FUNCTION_NUMBER,
            null).computeContextDependent());

    assertFalse(new org.apache.commons.jxpath.ri.compiler.CoreFunction(
            org.apache.commons.jxpath.ri.compiler.Compiler.FUNCTION_NUMBER,
            new org.apache.commons.jxpath.ri.compiler.Expression[1])
            .computeContextDependent());

    assertFalse(new org.apache.commons.jxpath.ri.compiler.CoreFunction(
            org.apache.commons.jxpath.ri.compiler.Compiler.FUNCTION_ROUND,
            null).computeContextDependent());

    assertFalse(new org.apache.commons.jxpath.ri.compiler.CoreFunction(
            org.apache.commons.jxpath.ri.compiler.Compiler.FUNCTION_FORMAT_NUMBER,
            null).computeContextDependent());

    assertTrue(new org.apache.commons.jxpath.ri.compiler.CoreFunction(
            org.apache.commons.jxpath.ri.compiler.Compiler.FUNCTION_FORMAT_NUMBER,
            new org.apache.commons.jxpath.ri.compiler.Expression[2])
            .computeContextDependent());
}