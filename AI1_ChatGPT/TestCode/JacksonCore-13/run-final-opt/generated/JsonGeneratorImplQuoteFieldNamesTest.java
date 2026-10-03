import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class JsonGeneratorImplQuoteFieldNamesTest
{
    @Test
    public void defaultConfigurationQuotesFieldNames() throws Exception
    {
        StringWriter output = new StringWriter();
        JsonGenerator generator = new JsonFactory().createGenerator(output);

        generator.writeStartObject();
        generator.writeNumberField("foo", 1);
        generator.writeEndObject();
        generator.close();

        assertEquals("{\"foo\":1}", output.toString());
    }

    @Test
    public void disablingQuoteFieldNamesOnGeneratorWritesUnquotedName() throws Exception
    {
        StringWriter output = new StringWriter();
        JsonGenerator generator = new JsonFactory().createGenerator(output);

        generator.disable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);
        generator.writeStartObject();
        generator.writeNumberField("foo", 1);
        generator.writeEndObject();
        generator.close();

        assertEquals("{foo:1}", output.toString());
    }

    @Test
    public void quoteFieldNamesSettingCanBeToggledDuringGeneration() throws Exception
    {
        StringWriter output = new StringWriter();
        JsonGenerator generator = new JsonFactory().createGenerator(output);

        generator.writeStartObject();
        generator.disable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);
        generator.writeNumberField("unquoted", 1);
        generator.enable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);
        generator.writeNumberField("quoted", 2);
        generator.writeEndObject();
        generator.close();

        assertEquals("{unquoted:1,\"quoted\":2}", output.toString());
    }

    @Test
    public void factoryDisabledQuoteFieldNamesIsAppliedToCreatedGenerator() throws Exception
    {
        StringWriter output = new StringWriter();
        JsonFactory factory = new JsonFactory();
        factory.disable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);
        JsonGenerator generator = factory.createGenerator(output);

        generator.writeStartObject();
        generator.writeNumberField("foo", 1);
        generator.writeEndObject();
        generator.close();

        assertEquals("{foo:1}", output.toString());
    }
}
