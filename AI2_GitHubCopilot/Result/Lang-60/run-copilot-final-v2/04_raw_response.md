@Test
     public void testAppendCharArrayWithStartAndLength() {
         StrBuilder sb = new StrBuilder();
         char[] chars = new char[] {'x', 'y', 'z'};
         sb.append(chars, 0, 2);
         assertEquals(2, sb.size());
         assertTrue(sb.contains('x'));
         assertTrue(sb.contains('y'));
         assertFalse(sb.contains('z'));
     }

     @Test
     public void testAppendStringBufferWithStartAndLength() {
         StrBuilder sb = new StrBuilder("ab");
         StringBuffer buf = new StringBuffer("cdefg");
         sb.append(buf, 2, 2);
         assertEquals(4, sb.size());
         assertTrue(sb.contains('e'));
         assertTrue(sb.contains('f'));
     }

     @Test
     public void testAppendStrBuilderWithStartAndLength() {
         StrBuilder sb = new StrBuilder("hello");
         StrBuilder other = new StrBuilder("_world_");
         sb.append(other, 1, 5);
         assertEquals(10, sb.size());
         assertTrue(sb.contains('w'));
         assertTrue(sb.contains('d'));
     }

     @Test
     public void testContainsCharAfterDeleteCharAtRechecksSize() {
         StrBuilder sb = new StrBuilder();
         sb.append("test");
         sb.deleteCharAt(3);
         assertFalse(sb.contains('t'));
         assertTrue(sb.contains('e'));
         assertTrue(sb.contains('s'));
     }