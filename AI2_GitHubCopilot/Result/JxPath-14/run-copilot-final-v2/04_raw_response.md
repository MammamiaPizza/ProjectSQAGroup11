public void testComputeContextDependentPositionFunctions() {
    CoreFunction last = new
CoreFunction(org.apache.commons.jxpath.ri.compiler.Compiler.FUNCTION_LAST, null);
    assertTrue(last.computeContextDependent());
    CoreFunction position = new
CoreFunction(org.apache.commons.jxpath.ri.compiler.Compiler.FUNCTION_POSITION, null);
    assertTrue(position.computeContextDependent());
}

public void testComputeContextDependentNumberFunctions() {
    CoreFunction numberNull = new
CoreFunction(org.apache.commons.jxpath.ri.compiler.Compiler.FUNCTION_NUMBER, null);
    assertTrue(numberNull.computeContextDependent());
    CoreFunction numberEmpty = new
CoreFunction(org.apache.commons.jxpath.ri.compiler.Compiler.FUNCTION_NUMBER, new
org.apache.commons.jxpath.ri.compiler.Expression[0]);
    assertTrue(numberEmpty.computeContextDependent());
    CoreFunction numberArg = new
CoreFunction(org.apache.commons.jxpath.ri.compiler.Compiler.FUNCTION_NUMBER, new
org.apache.commons.jxpath.ri.compiler.Expression[1]);
    assertFalse(numberArg.computeContextDependent());
}

public void testComputeContextDependentCountAndFormatNumber() {
    CoreFunction count = new
CoreFunction(org.apache.commons.jxpath.ri.compiler.Compiler.FUNCTION_COUNT, null);
    assertFalse(count.computeContextDependent());
    CoreFunction formatNull = new
CoreFunction(org.apache.commons.jxpath.ri.compiler.Compiler.FUNCTION_FORMAT_NUMBER, null);
    assertFalse(formatNull.computeContextDependent());
    CoreFunction formatTwo = new
CoreFunction(org.apache.commons.jxpath.ri.compiler.Compiler.FUNCTION_FORMAT_NUMBER, new
org.apache.commons.jxpath.ri.compiler.Expression[2]);
    assertTrue(formatTwo.computeContextDependent());
}

public void testComputeContextDependentReturnsFalse() {
    CoreFunction round = new
CoreFunction(org.apache.commons.jxpath.ri.compiler.Compiler.FUNCTION_ROUND, null);
    assertFalse(round.computeContextDependent());
    CoreFunction ceiling = new
CoreFunction(org.apache.commons.jxpath.ri.compiler.Compiler.FUNCTION_CEILING, null);
    assertFalse(ceiling.computeContextDependent());
    CoreFunction string = new
CoreFunction(org.apache.commons.jxpath.ri.compiler.Compiler.FUNCTION_STRING, null);
    assertFalse(string.computeContextDependent());
}