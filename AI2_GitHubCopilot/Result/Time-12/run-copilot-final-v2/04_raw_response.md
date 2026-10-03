@Test
 public void testLocalDate_constructor_year0() {
     LocalDate ld = new LocalDate(0, 2, 3);
     assertEquals("0000-02-03", ld.toString());
 }

 @Test
 public void testLocalDate_constructor_yearMinus2() {
     LocalDate ld = new LocalDate(-2, 2, 3);
     assertEquals("-0002-02-03", ld.toString());
 }

 @Test
 public void testLocalDateTime_constructor_year0() {
     LocalDateTime ldt = new LocalDateTime(0, 2, 3, 4, 5, 6, 7);
     assertEquals("0000-02-03T04:05:06.007", ldt.toString());
 }

 @Test
 public void testLocalDateTime_constructor_yearMinus2() {
     LocalDateTime ldt = new LocalDateTime(-2, 2, 3, 4, 5, 6, 7);
     assertEquals("-0002-02-03T04:05:06.007", ldt.toString());
 }