@org.junit.Test
public void testParentMixInClassAnnotationsAreApplied() throws Exception {
    class ValueHolder {
        public String included = "yes";
        public String ignored = "no";
    }
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({ "ignored" })
    abstract class ParentMixIn { }
    abstract class ChildMixIn extends ParentMixIn { }

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.addMixInAnnotations(ValueHolder.class, ChildMixIn.class);

    org.junit.Assert.assertEquals("{\"included\":\"yes\"}",
            mapper.writeValueAsString(new ValueHolder()));
}

@org.junit.Test
public void testParentMixInMethodAnnotationIsApplied() throws Exception {
    class ValueHolder {
        public String value() {
            return "parent";
        }
    }
    abstract class ParentMixIn {
        @com.fasterxml.jackson.annotation.JsonProperty("name")
        public abstract String value();
    }
    abstract class ChildMixIn extends ParentMixIn { }

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.addMixInAnnotations(ValueHolder.class, ChildMixIn.class);

    org.junit.Assert.assertEquals("{\"name\":\"parent\"}",
            mapper.writeValueAsString(new ValueHolder()));
}