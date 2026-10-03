package org.apache.commons.lang;

import java.io.StringWriter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EntitiesSupplementaryUnicodeTest {

    @Test
    public void escapeStringUsesSeparateSurrogatesForSupplementaryCharacter() {
        String character = "\uD834\uDF06";

        assertEquals("&#55348;&#57186;", Entities.HTML40.escape(character));
    }

    @Test
    public void escapeWriterUsesSeparateSurrogatesForSupplementaryCharacter() throws Exception {
        StringWriter writer = new StringWriter();

        Entities.HTML40.escape(writer, "\uD834\uDF06");

        assertEquals("&#55348;&#57186;", writer.toString());
    }

    @Test
    public void escapeWriterAndStringOverloadsAgreeForSupplementaryCharacterInText() throws Exception {
        String input = "before \uD834\uDF06 after";
        StringWriter writer = new StringWriter();

        Entities.HTML40.escape(writer, input);

        assertEquals("before &#55348;&#57186; after", Entities.HTML40.escape(input));
        assertEquals(Entities.HTML40.escape(input), writer.toString());
    }

    @Test
    public void escapePreservesNamedEntitiesAndEscapesSupplementaryCharacter() {
        String input = "A&<>\"\u00E9\uD834\uDF06Z";

        assertEquals("A&amp;&lt;&gt;&quot;&eacute;&#55348;&#57186;Z",
                Entities.HTML40.escape(input));
    }
}
