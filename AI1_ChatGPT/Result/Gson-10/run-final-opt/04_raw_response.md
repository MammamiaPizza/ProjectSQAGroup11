public void testSerializedNameAlternatesDeserializeButPrimaryNameSerializes() {
  AlternateNameField value = new AlternateNameField();
  value.number = 7;

  assertEquals("{\"current\":7}", new com.google.gson.Gson().toJson(value));
  assertEquals(8, new com.google.gson.Gson().fromJson("{\"legacy\":8}", AlternateNameField.class).number);
  assertEquals(9, new com.google.gson.Gson().fromJson("{\"older\":9}", AlternateNameField.class).number);
}

public void testTransientFieldIsExcludedFromSerializationAndDeserialization() {
  ExcludedTransientField value = new ExcludedTransientField();
  value.visible = 3;
  value.hidden = 4;

  assertEquals("{\"visible\":3}", new com.google.gson.Gson().toJson(value));

  ExcludedTransientField parsed = new com.google.gson.Gson().fromJson(
      "{\"visible\":5,\"hidden\":6}", ExcludedTransientField.class);
  assertEquals(5, parsed.visible);
  assertEquals(0, parsed.hidden);
}

public void testDuplicateSerializedNamesAreRejected() {
  try {
    new com.google.gson.Gson().toJson(new DuplicateSerializedNames());
    fail();
  } catch (IllegalArgumentException expected) {
  }
}

public void testInterfaceDeclaredTypeSerializesWithoutImplementationFields() {
  assertEquals("{}", new com.google.gson.Gson().toJson(
      new EmptyDeclaredInterface() { }, EmptyDeclaredInterface.class));
}

private static final class AlternateNameField {
  @com.google.gson.annotations.SerializedName(value = "current", alternate = {"legacy", "older"})
  int number;
}

private static final class ExcludedTransientField {
  int visible;
  transient int hidden;
}

private static final class DuplicateSerializedNames {
  @com.google.gson.annotations.SerializedName("same")
  int first;
  @com.google.gson.annotations.SerializedName("same")
  int second;
}

private interface EmptyDeclaredInterface {
}