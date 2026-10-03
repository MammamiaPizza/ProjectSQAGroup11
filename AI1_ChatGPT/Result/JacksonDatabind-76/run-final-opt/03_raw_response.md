package com.fasterxml.jackson.databind.deser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

public class BuilderBasedDeserializerBug76Test
{
    @Test
    public void singleCreatorAtBeginningRetainsFollowingUnwrappedValues() throws Exception {
        SingleCreatorValue value = new ObjectMapper().readValue(
                "{\"id\":\"single\",\"name\":\"John\",\"age\":30}",
                SingleCreatorValue.class);

        assertEquals("single", value.id);
        assertNotNull(value.details);
        assertEquals("John", value.details.name);
        assertEquals(30, value.details.age);
    }

    @Test
    public void multipleCreatorsAtBeginningRetainFollowingUnwrappedValues() throws Exception {
        MultipleCreatorValue value = new ObjectMapper().readValue(
                "{\"id\":\"multiple\",\"kind\":\"person\",\"name\":\"John\",\"age\":30}",
                MultipleCreatorValue.class);

        assertEquals("multiple", value.id);
        assertEquals("person", value.kind);
        assertNotNull(value.details);
        assertEquals("John", value.details.name);
        assertEquals(30, value.details.age);
    }

    @Test
    public void singleCreatorInMiddleRetainsUnwrappedValuesOnBothSides() throws Exception {
        SingleCreatorValue value = new ObjectMapper().readValue(
                "{\"name\":\"John\",\"id\":\"single\",\"age\":30}",
                SingleCreatorValue.class);

        assertEquals("single", value.id);
        assertNotNull(value.details);
        assertEquals("John", value.details.name);
        assertEquals(30, value.details.age);
    }

    @Test
    public void multipleCreatorsInMiddleRetainUnwrappedValuesOnBothSides() throws Exception {
        MultipleCreatorValue value = new ObjectMapper().readValue(
                "{\"name\":\"John\",\"id\":\"multiple\",\"kind\":\"person\",\"age\":30}",
                MultipleCreatorValue.class);

        assertEquals("multiple", value.id);
        assertEquals("person", value.kind);
        assertNotNull(value.details);
        assertEquals("John", value.details.name);
        assertEquals(30, value.details.age);
    }

    @Test
    public void defaultBuilderAlsoAcceptsUnwrappedValues() throws Exception {
        DefaultCreatorValue value = new ObjectMapper().readValue(
                "{\"id\":\"default\",\"name\":\"Jane\",\"age\":31}",
                DefaultCreatorValue.class);

        assertEquals("default", value.id);
        assertNotNull(value.details);
        assertEquals("Jane", value.details.name);
        assertEquals(31, value.details.age);
    }

    @Test
    public void creatorOnlyInputLeavesOptionalUnwrappedPropertyUnset() throws Exception {
        SingleCreatorValue value = new ObjectMapper().readValue(
                "{\"id\":\"single\"}", SingleCreatorValue.class);

        assertEquals("single", value.id);
        assertNotNull(value.details);
    }

    public static class Details {
        public String name;
        public int age;

        public Details() {
        }
    }

    @JsonDeserialize(builder = SingleCreatorValue.Builder.class)
    public static class SingleCreatorValue {
        public final String id;
        public final Details details;

        private SingleCreatorValue(Builder builder) {
            id = builder.id;
            details = builder.details;
        }

        @JsonPOJOBuilder(withPrefix = "with")
        public static class Builder {
            private final String id;
            private Details details;

            @JsonCreator
            public Builder(@JsonProperty("id") String id) {
                this.id = id;
            }

            @JsonUnwrapped
            public Builder withDetails(Details details) {
                this.details = details;
                return this;
            }

            public SingleCreatorValue build() {
                return new SingleCreatorValue(this);
            }
        }
    }

    @JsonDeserialize(builder = MultipleCreatorValue.Builder.class)
    public static class MultipleCreatorValue {
        public final String id;
        public final String kind;
        public final Details details;

        private MultipleCreatorValue(Builder builder) {
            id = builder.id;
            kind = builder.kind;
            details = builder.details;
        }

        @JsonPOJOBuilder(withPrefix = "with")
        public static class Builder {
            private final String id;
            private final String kind;
            private Details details;

            @JsonCreator
            public Builder(@JsonProperty("id") String id,
                    @JsonProperty("kind") String kind) {
                this.id = id;
                this.kind = kind;
            }

            @JsonUnwrapped
            public Builder withDetails(Details details) {
                this.details = details;
                return this;
            }

            public MultipleCreatorValue build() {
                return new MultipleCreatorValue(this);
            }
        }
    }

    @JsonDeserialize(builder = DefaultCreatorValue.Builder.class)
    public static class DefaultCreatorValue {
        public final String id;
        public final Details details;

        private DefaultCreatorValue(Builder builder) {
            id = builder.id;
            details = builder.details;
        }

        @JsonPOJOBuilder(withPrefix = "with")
        public static class Builder {
            private String id;
            private Details details;

            public Builder() {
            }

            public Builder withId(String id) {
                this.id = id;
                return this;
            }

            @JsonUnwrapped
            public Builder withDetails(Details details) {
                this.details = details;
                return this;
            }

            public DefaultCreatorValue build() {
                return new DefaultCreatorValue(this);
            }
        }
    }
}