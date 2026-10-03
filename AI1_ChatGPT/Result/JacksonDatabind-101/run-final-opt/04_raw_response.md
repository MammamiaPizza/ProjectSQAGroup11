@Test
public void testDefaultBeanDeserializationForPoint() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    java.awt.Point result = mapper.readValue("{\"x\":3,\"y\":4}", java.awt.Point.class);

    assertEquals(3, result.x);
    assertEquals(4, result.y);
}

@Test
public void testUpdateExistingPointBean() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.awt.Point point = new java.awt.Point(1, 2);

    mapper.readerForUpdating(point).readValue("{\"x\":7}");

    assertEquals(7, point.x);
    assertEquals(2, point.y);
}