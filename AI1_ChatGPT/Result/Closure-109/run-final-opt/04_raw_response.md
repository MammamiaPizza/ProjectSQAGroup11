@org.junit.Test
public void parsesNestedStructuralConstructorRecordType() {
  assertNotNull(
      JsDocInfoParser.parseTypeString(
          "function(new: {factory: {name: string}, enabled: boolean})"));
}