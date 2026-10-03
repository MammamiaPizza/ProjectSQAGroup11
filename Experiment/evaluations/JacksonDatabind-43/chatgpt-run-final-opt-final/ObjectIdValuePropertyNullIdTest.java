package com.fasterxml.jackson.databind.objectid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class ObjectIdValuePropertyNullIdTest
{
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public static class StringIdBean {
        public String id;
        public String name;
    }

    @Test
    public void deserializesNullStringPropertyObjectId() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        StringIdBean bean = mapper.readValue(
                "{\"id\":null,\"name\":\"later-generated\"}",
                StringIdBean.class);

        assertNull(bean.id);
        assertEquals("later-generated", bean.name);
    }

    @Test
    public void deserializesNonNullStringPropertyObjectId() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        StringIdBean bean = mapper.readValue(
                "{\"id\":\"bean-1\",\"name\":\"present\"}",
                StringIdBean.class);

        assertEquals("bean-1", bean.id);
        assertEquals("present", bean.name);
    }
}
