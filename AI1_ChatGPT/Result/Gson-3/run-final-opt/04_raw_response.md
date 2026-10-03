public void testExactTypeInstanceCreatorIsUsed() {
  final com.google.gson.reflect.TypeToken<java.util.List<java.lang.String>> typeToken =
      new com.google.gson.reflect.TypeToken<java.util.List<java.lang.String>>() {};
  final java.util.List<java.lang.String> expected = new java.util.ArrayList<java.lang.String>();
  final java.lang.reflect.Type[] receivedType = new java.lang.reflect.Type[1];

  com.google.gson.InstanceCreator<java.util.List<java.lang.String>> creator =
      new com.google.gson.InstanceCreator<java.util.List<java.lang.String>>() {
        public java.util.List<java.lang.String> createInstance(java.lang.reflect.Type type) {
          receivedType[0] = type;
          return expected;
        }
      };

  java.util.Map<java.lang.reflect.Type, com.google.gson.InstanceCreator<?>> creators =
      new java.util.HashMap<java.lang.reflect.Type, com.google.gson.InstanceCreator<?>>();
  creators.put(typeToken.getType(), creator);

  com.google.gson.internal.ConstructorConstructor constructorConstructor =
      new com.google.gson.internal.ConstructorConstructor(creators);
  com.google.gson.internal.ObjectConstructor<java.util.List<java.lang.String>> constructor =
      constructorConstructor.get(typeToken);

  junit.framework.Assert.assertSame(expected, constructor.construct());
  junit.framework.Assert.assertSame(typeToken.getType(), receivedType[0]);
}

public void testRawTypeInstanceCreatorIsUsedForParameterizedType() {
  final com.google.gson.reflect.TypeToken<java.util.List<java.lang.Integer>> typeToken =
      new com.google.gson.reflect.TypeToken<java.util.List<java.lang.Integer>>() {};
  final java.util.List<java.lang.Integer> expected = new java.util.LinkedList<java.lang.Integer>();
  final java.lang.reflect.Type[] receivedType = new java.lang.reflect.Type[1];

  com.google.gson.InstanceCreator<java.util.List<java.lang.Integer>> creator =
      new com.google.gson.InstanceCreator<java.util.List<java.lang.Integer>>() {
        public java.util.List<java.lang.Integer> createInstance(java.lang.reflect.Type type) {
          receivedType[0] = type;
          return expected;
        }
      };

  java.util.Map<java.lang.reflect.Type, com.google.gson.InstanceCreator<?>> creators =
      new java.util.HashMap<java.lang.reflect.Type, com.google.gson.InstanceCreator<?>>();
  creators.put(java.util.List.class, creator);

  com.google.gson.internal.ConstructorConstructor constructorConstructor =
      new com.google.gson.internal.ConstructorConstructor(creators);
  com.google.gson.internal.ObjectConstructor<java.util.List<java.lang.Integer>> constructor =
      constructorConstructor.get(typeToken);

  junit.framework.Assert.assertSame(expected, constructor.construct());
  junit.framework.Assert.assertSame(typeToken.getType(), receivedType[0]);
}

public void testEnumSetDefaultImplementationUsesElementType() {
  com.google.gson.internal.ConstructorConstructor constructorConstructor =
      new com.google.gson.internal.ConstructorConstructor(
          new java.util.HashMap<java.lang.reflect.Type, com.google.gson.InstanceCreator<?>>());

  com.google.gson.internal.ObjectConstructor<java.util.EnumSet<java.lang.Thread.State>> constructor =
      constructorConstructor.get(
          new com.google.gson.reflect.TypeToken<java.util.EnumSet<java.lang.Thread.State>>() {});

  java.util.EnumSet<java.lang.Thread.State> states = constructor.construct();
  junit.framework.Assert.assertTrue(states.isEmpty());
  junit.framework.Assert.assertTrue(states.add(java.lang.Thread.State.NEW));
  junit.framework.Assert.assertTrue(states.contains(java.lang.Thread.State.NEW));
}

public void testMapDefaultImplementationsDependOnKeyType() {
  com.google.gson.internal.ConstructorConstructor constructorConstructor =
      new com.google.gson.internal.ConstructorConstructor(
          new java.util.HashMap<java.lang.reflect.Type, com.google.gson.InstanceCreator<?>>());

  com.google.gson.internal.ObjectConstructor<java.util.Map<java.lang.Integer, java.lang.String>>
      integerKeyConstructor = constructorConstructor.get(
          new com.google.gson.reflect.TypeToken<java.util.Map<java.lang.Integer, java.lang.String>>() {});
  com.google.gson.internal.ObjectConstructor<java.util.Map<java.lang.String, java.lang.Integer>>
      stringKeyConstructor = constructorConstructor.get(
          new com.google.gson.reflect.TypeToken<java.util.Map<java.lang.String, java.lang.Integer>>() {});

  junit.framework.Assert.assertTrue(
      integerKeyConstructor.construct() instanceof java.util.LinkedHashMap);
  junit.framework.Assert.assertTrue(
      stringKeyConstructor.construct() instanceof com.google.gson.internal.LinkedTreeMap);
}