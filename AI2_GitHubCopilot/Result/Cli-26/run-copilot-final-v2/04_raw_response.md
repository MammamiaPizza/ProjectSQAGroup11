public void testHasArgFalseIsResetted() {
         OptionBuilder.hasArg(true).withDescription("desc");
         Option first = OptionBuilder.create('f');
         assertEquals(1, first.getArgs());
         OptionBuilder.hasArg(false);
         Option second = OptionBuilder.create('s');
         assertEquals(Option.UNINITIALIZED, second.getArgs());
         assertNull("description leaked", second.getDescription());
     }

     public void testWithValueSeparatorIsResetted() {
         OptionBuilder.withValueSeparator('@').withDescription("desc");
         Option first = OptionBuilder.create('f');
         assertEquals('@', first.getValueSeparator());
         Option second = OptionBuilder.create('s');
         assertNull("description leaked", second.getDescription());
     }

     public void testHasOptionalArgsWithNumIsResetted() {
         OptionBuilder.hasOptionalArgs(3).withDescription("desc");
         Option first = OptionBuilder.create('f');
         assertEquals(3, first.getArgs());
         assertTrue(first.hasOptionalArg());
         Option second = OptionBuilder.create('s');
         assertEquals("numberOfArgs was not reset", Option.UNINITIALIZED, second.getArgs());
         assertFalse("optionalArg was not reset", second.hasOptionalArg());
         assertNull("description leaked", second.getDescription());
     }

     public void testHasArgsWithNumIsResetted() {
         OptionBuilder.hasArgs(4).withDescription("desc");
         Option first = OptionBuilder.create('f');
         assertEquals(4, first.getArgs());
         Option second = OptionBuilder.create('s');
         assertEquals("numberOfArgs was not reset", Option.UNINITIALIZED, second.getArgs());
         assertNull("description leaked", second.getDescription());
     }