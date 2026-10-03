package com.fasterxml.jackson.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter;
import com.fasterxml.jackson.core.util.Separators;

public class TestDefaultPrettyPrinterBug23 {

 private JsonGenerator createGenerator(StringWriter sw) throws IOException {
     JsonFactory f = new JsonFactory();
     return f.createGenerator(sw);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testInvalidSubClassThrows() {
     // Subclass that returns wrong type

 DefaultPrettyPrinter bad = new DefaultPrettyPrinter() {
            @Override
            public DefaultPrettyPrinter createInstance() {
                return new DefaultPrettyPrinter(); // not same class
            }
        };
        bad.createInstance();
    }

 @Test
 public void testValidSubClassDoesNotThrow() {
     class GoodSub extends DefaultPrettyPrinter {
         @Override
         public DefaultPrettyPrinter createInstance() {
             return new GoodSub(); // same class
         }
     }
     GoodSub p = new GoodSub();
     DefaultPrettyPrinter result = p.createInstance();
     assertNotNull(result);
     assertTrue(result instanceof GoodSub);
 }

 @Test
 public void testCreateInstanceReturnsPlainType() {
     DefaultPrettyPrinter p = new DefaultPrettyPrinter();
     DefaultPrettyPrinter p2 = p.createInstance();
     assertNotNull(p2);
     assertTrue(p2.getClass() == DefaultPrettyPrinter.class);
 }

 @Test
 public void testRootSeparatorNullWritesNothing() throws IOException {
     DefaultPrettyPrinter pp = new DefaultPrettyPrinter((String) null);
     StringWriter sw = new StringWriter();
     JsonGenerator gen = createGenerator(sw);
     pp.writeRootValueSeparator(gen);
     gen.close();
     assertTrue(sw.toString(). isEmpty() );
 }

 @Test
 public void testRootSeparatorWritesContent() throws IOException {
     DefaultPrettyPrinter pp = new DefaultPrettyPrinter("~");
     StringWriter sw = new StringWriter();
     JsonGenerator gen = createGenerator(sw);
     pp.writeRootValueSeparator(gen);
     gen.close();
     assertEquals("~", sw.toString());
 }

 @Test
 public void testArrayIndenterPropagatedToNewInstance() throws IOException {
     DefaultPrettyPrinter base = new DefaultPrettyPrinter();
     Indenter mark = new DefaultPrettyPrinter.NopIndenter() {
         @Override
         public void writeIndentation(JsonGenerator g, int level) throws IOException {
             g.writeRaw("#");
         }
         @Override
         public boolean isInline() { return false; }
     };
     DefaultPrettyPrinter configured = base.withArrayIndenter(mark);
     DefaultPrettyPrinter copy = configured.createInstance();
     StringWriter sw = new StringWriter();
     JsonGenerator gen = createGenerator(sw);
     gen.setPrettyPrinter(copy);
     gen.writeStartArray();
     gen.writeNumber(1);
     gen.writeEndArray();
     gen.close();
     assertTrue(sw.toString(). contains("#"));
 }

 @Test
 public void testObjectIndenterPropagatedToNewInstance() throws IOException {
     DefaultPrettyPrinter base = new DefaultPrettyPrinter();
     Indenter star = new DefaultPrettyPrinter.NopIndenter() {
         @Override
         public void writeIndentation(JsonGenerator g, int level) throws IOException {
             g.writeRaw("*");
         }
         @Override
         public boolean isInline() { return false; }
     };
     DefaultPrettyPrinter configured = base.withObjectIndenter(star);
     DefaultPrettyPrinter copy = configured.createInstance();
     StringWriter sw = new StringWriter();
     JsonGenerator gen = createGenerator(sw);
     gen.setPrettyPrinter(copy);
     gen.writeStartObject();
     gen.writeNumberField("a", 1);
     gen.writeEndObject();
     gen.close();
     assertTrue(sw.toString().contains("*"));
 }

 @Test
 public void testIndentArraysMutatesOriginal() throws IOException {
     DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
     // use the system linefeed indenter (mult-line)
     pp.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
     StringWriter sw = new StringWriter();
     JsonGenerator gen = createGenerator(sw);
     gen.setPrettyPrinter(pp);
     gen.writeStartArray();
     gen.writeNumber(1);
     gen.writeEndArray();
     gen.close();
     assertTrue(sw.toString().contains("\n"));
 }

 @Test
 public void testIndentObjectsMutatesOriginal() throws IOException {
     DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
     pp.indentObjectsWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
     StringWriter sw = new StringWriter();
     JsonGenerator gen = createGenerator(sw);
     gen.setPrettyPrinter(pp);
     gen.writeStartObject();
     gen.writeNumberField("a", 1);
     gen.writeEndObject();
     gen.close();
     assertTrue(sw.toString().contains("\n"));
 }

}