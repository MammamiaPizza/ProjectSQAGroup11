@Test
public void setLooseTypesIsRetainedWhenOptionsAreCloned() throws Exception {
  com.google.javascript.jscomp.CompilerOptions options =
      new com.google.javascript.jscomp.CompilerOptions();
  java.lang.reflect.Field looseTypes =
      com.google.javascript.jscomp.CompilerOptions.class.getDeclaredField("looseTypes");
  looseTypes.setAccessible(true);

  options.setLooseTypes(true);
  junit.framework.Assert.assertTrue(looseTypes.getBoolean(options));

  com.google.javascript.jscomp.CompilerOptions cloned =
      (com.google.javascript.jscomp.CompilerOptions) options.clone();
  junit.framework.Assert.assertTrue(looseTypes.getBoolean(cloned));

  options.setLooseTypes(false);
  junit.framework.Assert.assertFalse(looseTypes.getBoolean(options));
  junit.framework.Assert.assertTrue(looseTypes.getBoolean(cloned));
}