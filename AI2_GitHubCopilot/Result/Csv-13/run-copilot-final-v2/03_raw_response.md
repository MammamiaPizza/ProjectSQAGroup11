package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import org.junit.Test;

public class CSVPrinterTest {

 @Test
 public void testMySqlNullStringDefault() {
     assertEquals("\\N", CSVFormat.MYSQL.getNullString());
 }

 @Test
 public void testMySqlNullOutput() throws IOException {
     final StringWriter sw = new StringWriter();
     final CSVFormat format = CSVFormat.MYSQL.withNullString("NULL");
     try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
         printer.print((Object) null);
     }
     assertEquals("NULL", sw.toString());
 }

 @Test
 public void testMySqlDefaultNullOutput() throws IOException {
     final StringWriter sw = new StringWriter();
     try (final CSVPrinter printer = new CSVPrinter(sw, CSVFormat.MYSQL)) {
         printer.print((Object) null);
     }
     assertEquals("\\N", sw.toString());
 }

 @Test
 public void testNullStringNullPrintsEmpty() throws IOException {
     final StringWriter sw = new StringWriter();
     final CSVFormat format = CSVFormat.MYSQL.withNullString(null);
     try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
         printer.print((Object) null);
     }
     assertEquals("", sw.toString());
 }

 @Test
 public void testWithNullStringNullConfiguration() {
     assertNull(CSVFormat.MYSQL.withNullString(null).getNullString());
 }

 @Test
 public void testPrintNonNullValueUnaffected() throws IOException {
     final StringWriter sw = new StringWriter();
     final CSVFormat format = CSVFormat.MYSQL.withNullString("NULL");
     try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
         printer.print("value");
     }
     assertEquals("value", sw.toString());
 }

 @Test
 public void testMultipleNullsInRecord() throws IOException {
     final StringWriter sw = new StringWriter();
     try (final CSVPrinter printer = new CSVPrinter(sw, CSVFormat.MYSQL)) {
         printer.printRecord((Object) null, "x", (Object) null);
     }
     assertEquals("\\N\tx\t\\N", sw.toString());
 }

 @Test
 public void testPrintRecordWithNullIterable() throws IOException {
     final StringWriter sw = new StringWriter();
     try (final CSVPrinter printer = new CSVPrinter(sw, CSVFormat.MYSQL)) {
         printer.printRecord(Arrays.asList((Object) null, "a"));
     }
     assertEquals("\\N\ta", sw.toString());
 }

 @Test
 public void testFormatMethodTrimsWithNullString() {
     assertEquals("\\N", CSVFormat.MYSQL.format((Object) null));
 }

 @Test
 public void testFormatMethodCustomNullString() {
     assertEquals("NULL", CSVFormat.MYSQL.withNullString("NULL").format((Object) null));
 }

}