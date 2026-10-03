package com.fasterxml.jackson.databind.ser;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;

 import java.util.List;

 import org.junit.Test;

 import com.fasterxml.jackson.annotation.JsonProperty;
 import com.fasterxml.jackson.databind.MapperFeature;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.SerializationConfig;
 import com.fasterxml.jackson.databind.DeserializationConfig;
 import com.fasterxml.jackson.databind.JavaType;
 import com.fasterxml.jackson.databind.introspect.BeanDescription;
 import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

 public class TestFeatures48 {

     // Bean that exposes a setter-only property and a field-only property (with explicit name)
     public static class TCls {
         private String name;
         @JsonProperty("groupname")
         private String groupname;

         public void setName(String n) {
             this.name = n;
         }
     }

     // Bean with only a setter (no field)
     public static class SetterOnly {
         private String value;
         public void setValue(String v) {
             this.value = v;
         }
     }

     // Bean with only a field (annotated to be discovered even if auto-detect-fields is off)
     public static class FieldOnly {
         @JsonProperty
         private int count;
     }

     private ObjectMapper mapper = new ObjectMapper();

     @Test
     public void serializationWithoutAutoDetectSettersShouldFindOnlyFieldProperty() {
         SerializationConfig cfg = mapper.getSerializationConfig();
         cfg = cfg.without(MapperFeature.AUTO_DETECT_SETTERS);
         JavaType type = mapper.getTypeFactory().constructType(TCls.class);
         BeanDescription desc = (BeanDescription) cfg.introspect(type);
         List<BeanPropertyDefinition> props = desc.findProperties();
         assertEquals("Should find 1 property (field only)", 1, props.size());
         assertEquals("groupname", props.get(0).getName());
     }

     @Test
     public void deserializationWithoutAutoDetectSettersShouldFindOnlyFieldProperty() {
         DeserializationConfig cfg = mapper.getDeserializationConfig();
         cfg = cfg.without(MapperFeature.AUTO_DETECT_SETTERS);
         JavaType type = mapper.getTypeFactory().constructType(TCls.class);
         BeanDescription desc = (BeanDescription) cfg.introspect(type);
         List<BeanPropertyDefinition> props = desc.findProperties();
         assertEquals("Should find 1 property (field only)", 1, props.size());
         assertEquals("groupname", props.get(0).getName());
     }

     @Test
     public void serializationWithoutAutoDetectFieldsShouldFindOnlySetterProperty() {
         SerializationConfig cfg = mapper.getSerializationConfig();
         cfg = cfg.without(MapperFeature.AUTO_DETECT_FIELDS);
         JavaType type = mapper.getTypeFactory().constructType(TCls.class);
         BeanDescription desc = (BeanDescription) cfg.introspect(type);
         List<BeanPropertyDefinition> props = desc.findProperties();
         assertEquals("Should find 1 property (setter only)", 1, props.size());
         assertEquals("name", props.get(0).getName());
     }

     @Test
     public void deserializationWithoutAutoDetectFieldsShouldFindOnlySetterProperty() {
         DeserializationConfig cfg = mapper.getDeserializationConfig();
         cfg = cfg.without(MapperFeature.AUTO_DETECT_FIELDS);
         JavaType type = mapper.getTypeFactory().constructType(TCls.class);
         BeanDescription desc = (BeanDescription) cfg.introspect(type);
         List<BeanPropertyDefinition> props = desc.findProperties();
         assertEquals("Should find 1 property (setter only)", 1, props.size());
         assertEquals("name", props.get(0).getName());
     }

     @Test
     public void serializationWithoutAllAutoDetectShouldFindNoProperties() {
         SerializationConfig cfg = mapper.getSerializationConfig();
         cfg = cfg.without(MapperFeature.AUTO_DETECT_SETTERS,
                           MapperFeature.AUTO_DETECT_FIELDS);
         JavaType type = mapper.getTypeFactory().constructType(TCls.class);
         BeanDescription desc = (BeanDescription) cfg.introspect(type);
         List<BeanPropertyDefinition> props = desc.findProperties();
         assertEquals("Should find 0 properties", 0, props.size());
     }

     @Test
     public void defaultAllAutoDetectDetectsBoth() {
         SerializationConfig cfg = mapper.getSerializationConfig();
         JavaType type = mapper.getTypeFactory().constructType(TCls.class);
         BeanDescription desc = (BeanDescription) cfg.introspect(type);
         List<BeanPropertyDefinition> props = desc.findProperties();
         assertEquals("Default should detect both setter and field", 2, props.size());
     }

     @Test
     public void chainedWithRootNamePreservesVisibilitySettings() {
         SerializationConfig cfg = mapper.getSerializationConfig();
         cfg = cfg.without(MapperFeature.AUTO_DETECT_SETTERS)
                  .withRootName("root");
         JavaType type = mapper.getTypeFactory().constructType(TCls.class);
         BeanDescription desc = (BeanDescription) cfg.introspect(type);
         List<BeanPropertyDefinition> props = desc.findProperties();
         assertEquals("Should still find only field after chaining", 1, props.size());
         assertEquals("groupname", props.get(0).getName());
     }

     @Test
     public void toggleSettersOffAndOn() {
         SerializationConfig cfg = mapper.getSerializationConfig();
         // off
         cfg = cfg.without(MapperFeature.AUTO_DETECT_SETTERS);
         JavaType type = mapper.getTypeFactory().constructType(TCls.class);
         BeanDescription desc = (BeanDescription) cfg.introspect(type);
         List<BeanPropertyDefinition> props = desc.findProperties();
         assertEquals("With setters disabled, field only", 1, props.size());

         // on
         cfg = cfg.with(MapperFeature.AUTO_DETECT_SETTERS);
         desc = (BeanDescription) cfg.introspect(type);
         props = desc.findProperties();
         assertEquals("Re-enabling setters should bring back setter property", 2, props.size());
     }

     @Test
     public void boundarySetterOnlyBeanHonorsVisibility() {
         SerializationConfig cfg = mapper.getSerializationConfig();
         cfg = cfg.without(MapperFeature.AUTO_DETECT_SETTERS);
         JavaType type = mapper.getTypeFactory().constructType(SetterOnly.class);
         BeanDescription desc = (BeanDescription) cfg.introspect(type);
         List<BeanPropertyDefinition> props = desc.findProperties();
         assertEquals("Setter-only bean should yield 0 properties when setters off", 0,
props.size());
     }

     @Test
     public void boundaryFieldOnlyBeanHonorsVisibility() {
         SerializationConfig cfg = mapper.getSerializationConfig();
         cfg = cfg.without(MapperFeature.AUTO_DETECT_FIELDS);
         JavaType type = mapper.getTypeFactory().constructType(FieldOnly.class);
         BeanDescription desc = (BeanDescription) cfg.introspect(type);
         List<BeanPropertyDefinition> props = desc.findProperties();
         // The field is annotated with @JsonProperty so it will be recognised even when
         // AUTO_DETECT_FIELDS is disabled; the annotation provides explicit visibility.
         assertEquals("Explicitly annotated field should survive fields-off config", 1,
props.size());
     }

     @Test
     public void deserializationToggleSettersOffAndOnPreservesVisibility() {
         DeserializationConfig cfg = mapper.getDeserializationConfig();
         cfg = cfg.without(MapperFeature.AUTO_DETECT_SETTERS);
         JavaType type = mapper.getTypeFactory().constructType(TCls.class);
         BeanDescription desc = (BeanDescription) cfg.introspect(type);
         List<BeanPropertyDefinition> props = desc.findProperties();
         assertEquals("Deser: with setters off, only field", 1, props.size());

         cfg = cfg.with(MapperFeature.AUTO_DETECT_SETTERS);
         desc = (BeanDescription) cfg.introspect(type);
         props = desc.findProperties();
         assertEquals("Deser: re-enabling setters restores setter property", 2, props.size());
     }
 }
