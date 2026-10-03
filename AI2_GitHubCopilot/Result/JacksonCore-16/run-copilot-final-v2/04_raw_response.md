@Test
 public void testAddFlattenedActiveParsersWithPlainNonSequenceParser() {
     com.fasterxml.jackson.core.JsonFactory f = new com.fasterxml.jackson.core.JsonFactory();
     com.fasterxml.jackson.core.JsonParser p1 = f.createParser("1");
     com.fasterxml.jackson.core.JsonParser p2 = f.createParser("2");
     JsonParserSequence seq = JsonParserSequence.createFlattened(p1, p2);
     assertEquals(2, seq.containedParsersCount());
     assertEquals(JsonToken.VALUE_NUMBER_INT, seq.nextToken());
     assertEquals(JsonToken.VALUE_NUMBER_INT, seq.nextToken());
     assertNull(seq.nextToken());
 }

 @Test
 public void testNextTokenWhileLoopSwitchesToNextDelegate() {
     com.fasterxml.jackson.core.JsonFactory f = new com.fasterxml.jackson.core.JsonFactory();
     com.fasterxml.jackson.core.JsonParser p1 = f.createParser("1");
     com.fasterxml.jackson.core.JsonParser p2 = f.createParser("true");
     com.fasterxml.jackson.core.JsonParser p3 = f.createParser("3");
     JsonParserSequence seq = JsonParserSequence.createFlattened(
             JsonParserSequence.createFlattened(p1, p2), p3);
     assertEquals(3, seq.containedParsersCount());
     assertEquals(JsonToken.VALUE_NUMBER_INT, seq.nextToken());
     assertEquals(JsonToken.VALUE_TRUE, seq.nextToken());
     assertEquals(JsonToken.VALUE_NUMBER_INT, seq.nextToken());
     assertNull(seq.nextToken());
 }

 @Test
 public void testNextTokenReturnsNullAfterSwitchToNextFails() {
     com.fasterxml.jackson.core.JsonFactory f = new com.fasterxml.jackson.core.JsonFactory();
     com.fasterxml.jackson.core.JsonParser p1 = f.createParser("1");
     com.fasterxml.jackson.core.JsonParser p2 = f.createParser("2");
     JsonParserSequence seq = JsonParserSequence.createFlattened(p1, p2);
     assertEquals(JsonToken.VALUE_NUMBER_INT, seq.nextToken());
     assertEquals(JsonToken.VALUE_NUMBER_INT, seq.nextToken());
     assertNull(seq.nextToken());
     assertNull(seq.nextToken());
 }

 @Test
 public void testContainedParsersCountMatchesFlattenedInput() {
     com.fasterxml.jackson.core.JsonFactory f = new com.fasterxml.jackson.core.JsonFactory();
     com.fasterxml.jackson.core.JsonParser plain = f.createParser("1");
     JsonParserSequence inner = JsonParserSequence.createFlattened(
             f.createParser("2"), f.createParser("3"));
     JsonParserSequence seq = JsonParserSequence.createFlattened(plain, inner);
     assertEquals(3, seq.containedParsersCount());
 }