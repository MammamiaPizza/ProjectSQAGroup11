package org.apache.commons.lang;

import java.io.StringWriter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EntitiesSupplementaryUnicodeTest {

    @Test
    public void escapeStringUsesSingleCodePointForSupplementaryCharacter() {
        String character = "\uD834\uDF06";

        assertEquals("&#119650;", Entities.HTML40.escape(character));
    }

    @Test
    public void escapeWriterUsesSingleCodePointForSupplementaryCharacter() throws Exception {
        StringWriter writer = new StringWriter();

        Entities.HTML40.escape(writer, "\uD834\uDF06");

        assertEquals("&#119650;", writer.toString());
    }

    @Test
    public void escapeWriterAndStringOverloadsAgreeForSupplementaryCharacterInText() throws Exception {
        String input = "before \uD834\uDF06 after";
        StringWriter writer = new StringWriter();

        Entities.HTML40.escape(writer, input);

        assertEquals("before &#119650; after", Entities.HTML40.escape(input));
        assertEquals(Entities.HTML40.escape(input), writer.toString());
    }

    @Test
    public void escapePreservesNamedEntitiesAndEscapesSupplementaryCharacter() {
        String input = "A&<>\"\u00E9\uD834\uDF06Z";

        assertEquals("A&amp;&lt;&gt;&quot;&eacute;&#119650;Z",
                Entities.HTML40.escape(input));
    }
}