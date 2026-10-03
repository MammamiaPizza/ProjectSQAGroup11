@Test
public void testAddIdentifierSeparatesAdjacentWordTokens() {
  assertEquals("return value", outputForIdentifiers("return", "value"));
}

@Test
public void testAddIdentifierIgnoresEmptyToken() {
  assertEquals("value", outputForIdentifiers("value", ""));
}

@Test
public void testAppendBlockDelimiters() {
  final java.lang.StringBuilder output = new java.lang.StringBuilder();
  CodeConsumer consumer = new CodeConsumer() {
    @Override
    char getLastChar() {
      return output.length() == 0 ? '\0' : output.charAt(output.length() - 1);
    }

    @Override
    void append(String str) {
      output.append(str);
    }
  };

  consumer.appendBlockStart();
  consumer.appendBlockEnd();

  assertEquals("{}", output.toString());
}

private static String outputForIdentifiers(String... identifiers) {
  final java.lang.StringBuilder output = new java.lang.StringBuilder();
  CodeConsumer consumer = new CodeConsumer() {
    @Override
    char getLastChar() {
      return output.length() == 0 ? '\0' : output.charAt(output.length() - 1);
    }

    @Override
    void append(String str) {
      output.append(str);
    }
  };

  for (String identifier : identifiers) {
    consumer.addIdentifier(identifier);
  }
  return output.toString();
}