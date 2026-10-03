@org.junit.Test
public void nonDefaultPrimitiveUsesPrimitiveDefaultWhenBeanHasNoDefaultConstructor() throws Exception {
    class PrimitiveBean {
        public int number;

        PrimitiveBean(int number) {
            this.number = number;
        }
    }

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT);

    org.junit.Assert.assertEquals("{}", mapper.writeValueAsString(new PrimitiveBean(0)));
    org.junit.Assert.assertEquals("{\"number\":3}", mapper.writeValueAsString(new PrimitiveBean(3)));
}

@org.junit.Test
public void nonDefaultUsesValueFromDefaultConstructedBean() throws Exception {
    class BeanWithInitializedDefault {
        public String str = "default";
    }

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT);

    BeanWithInitializedDefault changed = new BeanWithInitializedDefault();
    changed.str = "changed";

    org.junit.Assert.assertEquals("{}", mapper.writeValueAsString(new BeanWithInitializedDefault()));
    org.junit.Assert.assertEquals("{\"str\":\"changed\"}", mapper.writeValueAsString(changed));
}