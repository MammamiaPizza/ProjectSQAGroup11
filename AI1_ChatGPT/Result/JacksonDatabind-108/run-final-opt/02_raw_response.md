import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class ObjectReaderEofTreeReadTest
{
    @Test
    public void readTreeWithDefaultReaderReturnsNullForEmptyParser() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonParser parser = mapper.getFactory().createParser("");

        try {
            assertNull("EOF parser should produce null, not a MissingNode",
                    mapper.reader().readTree(parser));
        } finally {
            parser.close();
        }
    }

    @Test
    public void readTreeWithJsonNodeReaderReturnsNullForEmptyParser() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonParser parser = mapper.getFactory().createParser("");

        try {
            assertNull("EOF parser should produce null even when reader has a value type",
                    mapper.readerFor(JsonNode.class).readTree(parser));
        } finally {
            parser.close();
        }
    }

    @Test
    public void readTreeWithParserStillReadsNonEmptyContent() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonParser parser = mapper.getFactory().createParser("{\"answer\":42}");

        try {
            JsonNode result = mapper.reader().readTree(parser);

            assertNotNull(result);
            assertTrue(result.isObject());
            assertEquals(42, result.get("answer").asInt());
        } finally {
            parser.close();
        }
    }
}