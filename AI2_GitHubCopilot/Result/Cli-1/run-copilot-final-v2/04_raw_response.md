public void testGetOptionObjectChar() {
     assertNotNull(cmd.getOptionObject('a'));
     assertEquals(cmd.getOptionObject("a"), cmd.getOptionObject('a'));
 }

 public void testGetOptionValueWithDefault() {
     assertEquals("fallback", cmd.getOptionValue("b", "fallback"));
     assertEquals("default", cmd.getOptionValue("noSuchOption", "default"));
 }

 public void testGetArgs() {
     assertNotNull(cmd.getArgs());
     assertTrue(cmd.getArgs().length >= 0);
 }

 public void testGetOptionObjectBranch() {
     assertNotNull(cmd.getOptionObject("a"));
     assertNull(cmd.getOptionObject("b"));
 }