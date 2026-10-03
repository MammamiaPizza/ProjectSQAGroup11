package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import org.junit.Test;

public class CodeGeneratorUnicodeTest {

  @Test
  public void testDeleteCharacterIsEscapedWithDefaultOutputCharset() {
    assertEquals("\"\\u007f\"", CodeGenerator.jsString("\u007f", null));
  }

  @Test
  public void testDeleteCharacterIsEscapedWithoutChangingPrintableNeighbors() {
    assertEquals("\"a\\u007fz\"", CodeGenerator.jsString("a\u007fz", null));
  }

  @Test
  public void testDeleteCharacterIsEscapedEvenWhenUtf8CanEncodeIt() {
    CharsetEncoder utf8 = Charset.forName("UTF-8").newEncoder();

    assertEquals("\"\\u007f\"", CodeGenerator.jsString("\u007f", utf8));
  }
}