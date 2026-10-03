@org.junit.Test
public void testParseTypeStringWithNestedGenericAtEndOfInput() {
  assertNotNull(
      JsDocInfoParser.parseTypeString("Array.<Object.<string>>"));
}