package com.fasterxml.jackson.databind.deser.std;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class JdkDeserializersVoidTest
{
    @Test
    public void testVoidDeserializationFromNumericScalarReturnsNull() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        Void result = mapper.readValue("123", Void.class);

        assertNull(result);
    }

    @Test
    public void testVoidDeserializerIsAvailableFromJdkDeserializerLookup()
    {
        JsonDeserializer<?> deserializer = JdkDeserializers.find(Void.class, Void.class.getName());

        assertNotNull("Void should have a JDK deserializer", deserializer);
    }

    @Test
    public void testLookupDoesNotMatchVoidForUnrelatedClassName()
    {
        JsonDeserializer<?> deserializer = JdkDeserializers.find(Void.class, "not.a.real.Void");

        assertNull(deserializer);
    }

    @Test
    public void testExistingJdkDeserializerLookupPathsRemainAvailable()
    {
        assertNotNull(JdkDeserializers.find(UUID.class, UUID.class.getName()));
        assertNotNull(JdkDeserializers.find(StackTraceElement.class, StackTraceElement.class.getName()));
        assertNotNull(JdkDeserializers.find(AtomicBoolean.class, AtomicBoolean.class.getName()));
        assertNotNull(JdkDeserializers.find(ByteBuffer.class, ByteBuffer.class.getName()));
    }
}
