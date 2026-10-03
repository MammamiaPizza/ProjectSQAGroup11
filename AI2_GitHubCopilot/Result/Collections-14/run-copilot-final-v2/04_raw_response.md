@Test
 public void testConstructorWithInitialCapacity() {
     CaseInsensitiveMap map = new CaseInsensitiveMap(10);
     assertTrue(map.isEmpty());
 }

 @Test
 public void testConstructorWithInitialCapacityAndLoadFactor() {
     CaseInsensitiveMap map = new CaseInsensitiveMap(10, 0.75f);
     assertTrue(map.isEmpty());
 }

 @Test
 public void testSerializationRoundtrip() throws Exception {
     CaseInsensitiveMap map = new CaseInsensitiveMap();
     map.put("key", "value");
     map.put("Key", "value2");

     java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
     java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
     oos.writeObject(map);
     oos.close();

     java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
     java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
     CaseInsensitiveMap deserialized = (CaseInsensitiveMap) ois.readObject();
     ois.close();

     assertEquals(1, deserialized.size());
     assertEquals("value2", deserialized.get("KEY"));
     assertEquals("value2", deserialized.get("key"));
 }

 @Test
 public void testSerializationPreservesLocaleIndependence() throws Exception {
     java.util.Locale turkish = new java.util.Locale("tr");
     java.util.Locale defaultLocale = java.util.Locale.getDefault();
     try {
         java.util.Locale.setDefault(turkish);

         CaseInsensitiveMap map = new CaseInsensitiveMap();
         map.put("i", "lowercaseI");
         map.put("I", "uppercaseI");

         java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
         java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
         oos.writeObject(map);
         oos.close();

         java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
         java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
         CaseInsensitiveMap deserialized = (CaseInsensitiveMap) ois.readObject();
         ois.close();

         assertEquals(1, deserialized.size());
         assertEquals("uppercaseI", deserialized.get("i"));
         assertEquals("uppercaseI", deserialized.get("I"));
     } finally {
         java.util.Locale.setDefault(defaultLocale);
     }
 }