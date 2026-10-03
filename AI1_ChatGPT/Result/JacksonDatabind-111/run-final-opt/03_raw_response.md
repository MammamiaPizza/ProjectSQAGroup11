import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class AtomicReferenceNullHandlingTest
{
    private final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testNullWithinNestedAtomicReferenceCreatesOuterReference() throws Exception
    {
        AtomicReference<AtomicReference<String>> result = MAPPER.readValue("null",
                new TypeReference<AtomicReference<AtomicReference<String>>>() { });

        assertNotNull(result);
        assertNull(result.get());
    }

    @Test
    public void testNestedAtomicReferenceRetainsBothReferenceLevelsForValue() throws Exception
    {
        AtomicReference<AtomicReference<String>> result = MAPPER.readValue("\"value\"",
                new TypeReference<AtomicReference<AtomicReference<String>>>() { });

        assertNotNull(result);
        assertNotNull(result.get());
        assertEquals("value", result.get().get());
    }

    @Test
    public void testCreatorPropertyReceivesEmptyAtomicReferenceForJsonNull() throws Exception
    {
        CreatorBean result = MAPPER.readValue("{\"value\":null}", CreatorBean.class);

        assertNotNull(result.value);
        assertNull(result.value.get());
    }

    @Test
    public void testFieldAndMethodPropertiesRespectNullsSkip() throws Exception
    {
        FieldSkipBean fieldBean = MAPPER.readValue("{\"value\":null}", FieldSkipBean.class);
        MethodSkipBean methodBean = MAPPER.readValue("{\"value\":null}", MethodSkipBean.class);

        assertEquals("field-original", fieldBean.value.get());
        assertEquals("method-original", methodBean.getValue().get());
        assertEquals(0, methodBean.setCalls);
    }

    @Test
    public void testAtomicReferenceDeserializerPublicReferenceOperations() throws Exception
    {
        JavaType type = MAPPER.getTypeFactory().constructReferenceType(
                AtomicReference.class, MAPPER.getTypeFactory().constructType(Object.class));
        AtomicReferenceDeserializer deser = new AtomicReferenceDeserializer(type, null,
                new JsonDeserializer<Object>() {
                    @Override
                    public Object deserialize(JsonParser p, DeserializationContext ctxt) {
                        return null;
                    }
                }, null);

        AtomicReference<Object> nullValue = deser.getNullValue(null);
        AtomicReference<Object> emptyValue = (AtomicReference<Object>) deser.getEmptyValue(null);
        AtomicReference<Object> reference = deser.referenceValue("before");

        assertNotNull(nullValue);
        assertNull(nullValue.get());
        assertNotNull(emptyValue);
        assertNull(emptyValue.get());
        assertEquals("before", deser.getReferenced(reference));

        AtomicReference<Object> updated = deser.updateReference(reference, "after");
        assertSame(reference, updated);
        assertEquals("after", reference.get());
        assertEquals(Boolean.TRUE, deser.supportsUpdate(null));
    }

    public static class CreatorBean {
        public final AtomicReference<String> value;

        @JsonCreator
        public CreatorBean(@JsonProperty("value") AtomicReference<String> value) {
            this.value = value;
        }
    }

    public static class FieldSkipBean {
        @JsonSetter(nulls = Nulls.SKIP)
        public AtomicReference<String> value = new AtomicReference<String>("field-original");
    }

    public static class MethodSkipBean {
        private AtomicReference<String> value = new AtomicReference<String>("method-original");
        public int setCalls;

        @JsonSetter(nulls = Nulls.SKIP)
        public void setValue(AtomicReference<String> value) {
            ++setCalls;
            this.value = value;
        }

        public AtomicReference<String> getValue() {
            return value;
        }
    }
}