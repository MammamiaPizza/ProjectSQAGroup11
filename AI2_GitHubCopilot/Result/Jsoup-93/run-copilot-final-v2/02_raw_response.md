package org.jsoup.nodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.Test;

public class FormElementTest {

 private FormElement parseForm(String html) {
     return (FormElement) Jsoup.parse(html).select("form").first();
 }

 private List<Connection.KeyVal> formData(String html) {
     return parseForm(html).formData();
 }

 private void assertKeyVal(Connection.KeyVal kv, String expectedKey, String expectedValue) {
     assertEquals(expectedKey, kv.key());
     assertEquals(expectedValue, kv.value());
 }

 @Test
 public void createsFormData() {
     String html = "<form>"
             + "<input name='one' value='two'>"
             + "<select name='three'>"
             + "<option value='not selected'>"
             + "<option value='four' selected>"
             + "<option value='five' selected>"
             + "</select>"
             + "<input name='six' value='seven'>"
             + "<input name='eight' value='nine' type='checkbox' checked>"
             + "<input name='ten' value='eleven' type='radio' checked>"
             + "<input name='ten' value='twelve' type='radio'>"
             + "</form>";

     List<Connection.KeyVal> data = formData(html);

     assertEquals(6, data.size());
     assertKeyVal(data.get(0), "one", "two");
     assertKeyVal(data.get(1), "three", "four");
     assertKeyVal(data.get(2), "three", "five");
     assertKeyVal(data.get(3), "six", "seven");
     assertKeyVal(data.get(4), "eight", "nine");
     assertKeyVal(data.get(5), "ten", "eleven");
 }

 @Test
 public void checkedCheckboxWithoutValueDefaultsToOn() {
     String html = "<form><input type='checkbox' name='opt-in' checked></form>";

     List<Connection.KeyVal> data = formData(html);

     assertEquals(1, data.size());
     assertKeyVal(data.get(0), "opt-in", "on");
 }

 @Test
 public void selectSubmitsOnlySelectedOption() {
     String html = "<form><select name='choice'>"
             + "<option value='a'>A</option>"
             + "<option value='b' selected>B</option>"
             + "<option value='c'>C</option>"
             + "</select></form>";

     List<Connection.KeyVal> data = formData(html);

     assertEquals(1, data.size());
     assertKeyVal(data.get(0), "choice", "b");
 }

 @Test
 public void selectWithoutSelectedOptionSubmitsFirstOption() {
     String html = "<form><select name='country'>"
             + "<option value='ca'>Canada</option>"
             + "<option value='us'>United States</option>"
             + "</select></form>";

     List<Connection.KeyVal> data = formData(html);

     assertEquals(1, data.size());
     assertKeyVal(data.get(0), "country", "ca");
 }

 @Test
 public void disabledControlsAreSkipped() {
     String html = "<form>"
             + "<input name='keep' value='yes'>"
             + "<input name='skip' value='no' disabled>"
             + "<select name='skip-select' disabled><option value='x' selected></select>"
             + "<textarea name='skip-textarea' disabled>text</textarea>"
             + "</form>";

     List<Connection.KeyVal> data = formData(html);

     assertEquals(1, data.size());
     assertKeyVal(data.get(0), "keep", "yes");
 }

 @Test
 public void unnamedControlsAreSkipped() {
     String html = "<form>"
             + "<input value='no-name'>"
             + "<input name='named' value='yes'>"
             + "<textarea></textarea>"
             + "<select><option value='x' selected></select>"
             + "</form>";

     List<Connection.KeyVal> data = formData(html);

     assertEquals(1, data.size());
     assertKeyVal(data.get(0), "named", "yes");
 }

 @Test
 public void emptyFormReturnsNoData() {
     List<Connection.KeyVal> data = formData("<form></form>");

     assertTrue(data.isEmpty());
 }

 @Test
 public void textareaContentIsSubmitted() {
     String html = "<form><textarea name='message'>Hello world</textarea></form>";

     List<Connection.KeyVal> data = formData(html);

     assertEquals(1, data.size());
     assertKeyVal(data.get(0), "message", "Hello world");
 }

}