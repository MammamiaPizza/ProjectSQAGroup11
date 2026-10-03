    @org.junit.Test
    public void classLevelIgnoralsShouldSkipNamedAndUnknownProperties() throws Exception {
        IgnorablePropertiesBean bean = new com.fasterxml.jackson.databind.ObjectMapper().readValue(
                "{\"visible\":\"kept\",\"hidden\":\"ignored\",\"unknown\":3}",
                IgnorablePropertiesBean.class);

        org.junit.Assert.assertEquals("kept", bean.visible);
        org.junit.Assert.assertNull(bean.hidden);
    }

    @org.junit.Test
    public void anySetterMethodShouldReceiveOtherwiseUnknownProperties() throws Exception {
        AnySetterMethodBean bean = new com.fasterxml.jackson.databind.ObjectMapper().readValue(
                "{\"known\":\"value\",\"first\":1,\"second\":\"two\"}",
                AnySetterMethodBean.class);

        org.junit.Assert.assertEquals("value", bean.known);
        org.junit.Assert.assertEquals(java.lang.Integer.valueOf(1), bean.other.get("first"));
        org.junit.Assert.assertEquals("two", bean.other.get("second"));
    }

    @org.junit.Test
    public void anySetterFieldShouldReceiveOtherwiseUnknownProperties() throws Exception {
        AnySetterFieldBean bean = new com.fasterxml.jackson.databind.ObjectMapper().readValue(
                "{\"first\":true,\"second\":2}",
                AnySetterFieldBean.class);

        org.junit.Assert.assertEquals(java.lang.Boolean.TRUE, bean.other.get("first"));
        org.junit.Assert.assertEquals(java.lang.Integer.valueOf(2), bean.other.get("second"));
    }

    @org.junit.Test
    public void creatorPropertyShouldWorkWithRegularSetterProperties() throws Exception {
        CreatorAndSetterBean bean = new com.fasterxml.jackson.databind.ObjectMapper().readValue(
                "{\"name\":\"jack\",\"age\":12}",
                CreatorAndSetterBean.class);

        org.junit.Assert.assertEquals("jack", bean.getName());
        org.junit.Assert.assertEquals(12, bean.getAge());
    }

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(value = { "hidden" }, ignoreUnknown = true)
    private static class IgnorablePropertiesBean {
        public java.lang.String visible;
        public java.lang.String hidden;
    }

    private static class AnySetterMethodBean {
        public java.lang.String known;
        public final java.util.Map<java.lang.String, java.lang.Object> other =
                new java.util.LinkedHashMap<java.lang.String, java.lang.Object>();

        @com.fasterxml.jackson.annotation.JsonAnySetter
        public void add(java.lang.String name, java.lang.Object value) {
            other.put(name, value);
        }
    }

    private static class AnySetterFieldBean {
        @com.fasterxml.jackson.annotation.JsonAnySetter
        public final java.util.Map<java.lang.String, java.lang.Object> other =
                new java.util.LinkedHashMap<java.lang.String, java.lang.Object>();
    }

    private static class CreatorAndSetterBean {
        private final java.lang.String name;
        private int age;

        @com.fasterxml.jackson.annotation.JsonCreator
        public CreatorAndSetterBean(@com.fasterxml.jackson.annotation.JsonProperty("name") java.lang.String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public java.lang.String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }