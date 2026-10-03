import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonWriter;
import java.io.StringWriter;
import junit.framework.TestCase;

public class JsonWriterBoxedBooleanTest extends TestCase {

  public void testJsonWriterWritesBoxedBooleanValuesInArray() throws Exception {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);

    writer.beginArray();
    writer.value(Boolean.TRUE);
    writer.value(Boolean.FALSE);
    writer.endArray();
    writer.flush();

    assertEquals("[true,false]", output.toString());
  }

  public void testJsonWriterWritesNullBoxedBoolean() throws Exception {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);

    writer.value((Boolean) null);
    writer.flush();

    assertEquals("null", output.toString());
  }

  public void testJsonWriterOmitsNullBoxedBooleanMemberWhenSerializeNullsDisabled()
      throws Exception {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    writer.setSerializeNulls(false);

    writer.beginObject();
    writer.name("present").value(Boolean.TRUE);
    writer.name("missing").value((Boolean) null);
    writer.name("falseValue").value(Boolean.FALSE);
    writer.endObject();
    writer.flush();

    assertEquals("{\"present\":true,\"falseValue\":false}", output.toString());
  }

  public void testJsonTreeWriterBuildsNestedBoxedBooleanValues() throws Exception {
    JsonTreeWriter writer = new JsonTreeWriter();

    writer.beginObject();
    writer.name("values");
    writer.beginArray();
    writer.value(Boolean.TRUE);
    writer.value((Boolean) null);
    writer.value(Boolean.FALSE);
    writer.endArray();
    writer.endObject();

    JsonObject object = writer.get().getAsJsonObject();
    JsonArray values = object.get("values").getAsJsonArray();
    assertEquals(3, values.size());
    assertTrue(values.get(0).getAsBoolean());
    assertTrue(values.get(1).isJsonNull());
    assertFalse(values.get(2).getAsBoolean());
  }

  public void testJsonTreeWriterOmitsNullBoxedBooleanMemberWhenSerializeNullsDisabled()
      throws Exception {
    JsonTreeWriter writer = new JsonTreeWriter();
    writer.setSerializeNulls(false);

    writer.beginObject();
    writer.name("missing").value((Boolean) null);
    writer.name("present").value(Boolean.TRUE);
    writer.endObject();

    JsonObject object = writer.get().getAsJsonObject();
    assertFalse(object.has("missing"));
    assertTrue(object.get("present").getAsBoolean());
  }

  public void testBooleanTypeAdapterWritesBoxedAndNullValuesToJsonWriter() throws Exception {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);

    writer.beginArray();
    TypeAdapters.BOOLEAN.write(writer, Boolean.TRUE);
    TypeAdapters.BOOLEAN.write(writer, null);
    TypeAdapters.BOOLEAN.write(writer, Boolean.FALSE);
    writer.endArray();
    writer.flush();

    assertEquals("[true,null,false]", output.toString());
  }

  public void testBooleanTypeAdapterWritesNullToJsonTreeWriter() throws Exception {
    JsonTreeWriter writer = new JsonTreeWriter();

    TypeAdapters.BOOLEAN.write(writer, null);

    JsonElement result = writer.get();
    assertTrue(result.isJsonNull());
  }
}
