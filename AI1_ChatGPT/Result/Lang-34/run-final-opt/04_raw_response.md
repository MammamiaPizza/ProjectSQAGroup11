@Test
public void testDirectPrimitiveAppendsRenderAllScalarValuesAndCleanRegistry() {
    ToStringStyle style = ToStringStyle.DEFAULT_STYLE;
    StringBuffer buffer = new StringBuffer();
    Object root = new Object();

    style.appendStart(buffer, root);
    style.append(buffer, "byteValue", (byte) 1);
    style.append(buffer, "charValue", 'z');
    style.append(buffer, "doubleValue", 2.5d);
    style.append(buffer, "floatValue", 3.5f);
    style.append(buffer, "intValue", 4);
    style.append(buffer, "longValue", 5L);
    style.appendEnd(buffer, root);

    assertTrue(buffer.toString().indexOf("byteValue") >= 0);
    assertTrue(buffer.toString().indexOf("charValue") >= 0);
    assertTrue(buffer.toString().indexOf("doubleValue") >= 0);
    assertTrue(buffer.toString().indexOf("floatValue") >= 0);
    assertTrue(buffer.toString().indexOf("intValue") >= 0);
    assertTrue(buffer.toString().indexOf("longValue") >= 0);
    assertTrue(buffer.toString().indexOf("z") >= 0);
    assertNull(ToStringStyle.getRegistry());
}

@Test
public void testDirectArrayAppendsRenderFieldsAndCleanRegistry() {
    ToStringStyle style = ToStringStyle.DEFAULT_STYLE;
    StringBuffer buffer = new StringBuffer();
    Object root = new Object();

    style.appendStart(buffer, root);
    style.append(buffer, "bytes", new byte[] { 1, 2 }, Boolean.TRUE);
    style.append(buffer, "chars", new char[] { 'x' }, Boolean.TRUE);
    style.append(buffer, "doubles", new double[] { 1.5d }, Boolean.TRUE);
    style.append(buffer, "floats", new float[] { 2.5f }, Boolean.TRUE);
    style.append(buffer, "ints", new int[] { 3 }, Boolean.TRUE);
    style.append(buffer, "longs", new long[] { 4L }, Boolean.TRUE);
    style.append(buffer, "objects", new Object[] { "value", null }, Boolean.TRUE);
    style.appendEnd(buffer, root);

    assertTrue(buffer.toString().indexOf("bytes") >= 0);
    assertTrue(buffer.toString().indexOf("chars") >= 0);
    assertTrue(buffer.toString().indexOf("doubles") >= 0);
    assertTrue(buffer.toString().indexOf("floats") >= 0);
    assertTrue(buffer.toString().indexOf("ints") >= 0);
    assertTrue(buffer.toString().indexOf("longs") >= 0);
    assertTrue(buffer.toString().indexOf("objects") >= 0);
    assertTrue(buffer.toString().indexOf("value") >= 0);
    assertNull(ToStringStyle.getRegistry());
}