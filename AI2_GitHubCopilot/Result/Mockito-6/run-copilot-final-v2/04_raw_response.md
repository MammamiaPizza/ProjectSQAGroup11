@Test
    public void shouldExerciseIsNullAndIsNotNullMatchers() {
        when(mock.byString(isNull())).thenReturn("wasNull");
        when(mock.byString(isNotNull())).thenReturn("wasNotNull");
        assertEquals("wasNull", mock.byString(null));
        assertEquals("wasNotNull", mock.byString("hello"));
    }

 @Test
 public void shouldExerciseStringMatchers() {
     when(mock.byString(contains("abc"))).thenReturn("contained");
     when(mock.byString(matches("\\d+"))).thenReturn("digitMatch");
     when(mock.byString(endsWith("xyz"))).thenReturn("endMatch");
     when(mock.byString(startsWith("abc"))).thenReturn("startMatch");
     assertEquals("contained", mock.byString("xabcy"));
     assertEquals("digitMatch", mock.byString("123"));
     assertEquals("endMatch", mock.byString("endingxyz"));
     assertEquals("startMatch", mock.byString("abcstart"));
     assertNull("string matchers should reject null", mock.byString(null));
 }

 @Test
 public void shouldExerciseArgThatMatcher() {
     when(mock.byString(argThat(new org.hamcrest.BaseMatcher<String>() {
         public boolean matches(Object item) {
             return item != null && ((String) item).length() > 3;
         }
         public void describeTo(org.hamcrest.Description description) {
             description.appendText("length > 3");
         }
     }))).thenReturn("longString");
     assertEquals("longString", mock.byString("hello"));
     assertNull("argThat should reject null", mock.byString(null));
     assertNull("argThat should reject short strings", mock.byString("hi"));
 }

 @Test
 public void shouldExerciseNotNullAndSameMatchers() {
     when(mock.byString(notNull())).thenReturn("notNullResult");
     assertEquals("notNullResult", mock.byString("hello"));
     assertNull("notNull should reject null", mock.byString(null));

     Object ref = new Object();
     when(mock.byObject(same(ref))).thenReturn("sameResult");
     assertEquals("sameResult", mock.byObject(ref));
     assertNull("same should reject different object", mock.byObject(new Object()));
 }