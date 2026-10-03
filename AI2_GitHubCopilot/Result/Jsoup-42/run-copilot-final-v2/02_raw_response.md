package org.jsoup.nodes;

import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.junit.Test;

import static org.junit.Assert.*;

public class FormElementTest {

 // Helper to get value for a given key from form data
 private String valueByKey(List<Connection.KeyVal> data, String key) {
     for (Connection.KeyVal kv : data) {
         if (key.equals(kv.key())) {
             return kv.value();
         }
     }
     return null;
 }

 @Test
 public void createsFormData() {
     String html = "<html><body><form action='' method='get'>"
             + "<input type='text' name='text' value='A' />"
             + "<input type='password' name='password' value='B' />"
             + "<input type='hidden' name='hidden' value='C' />"
             + "<input type='checkbox' name='checkbox' value='test' checked />"
             + "<input type='checkbox' name='checkbox2' checked />"
             + "<input type='radio' name='radio' value='r1' checked />"
             + "<input type='submit' name='submit' value='Submit' />"
             + "</form></body></html>";
     Document doc = Jsoup.parse(html);
     FormElement form = (FormElement) doc.selectFirst("form");
     List<Connection.KeyVal> data = form.formData();

     assertEquals("form data should contain exactly 6 entries (submit button excluded)", 6,
data.size());

     assertEquals("text", data.get(0).key());
     assertEquals("A", data.get(0).value());

     assertEquals("password", data.get(1).key());
     assertEquals("B", data.get(1).value());

     assertEquals("hidden", data.get(2).key());
     assertEquals("C", data.get(2).value());

     assertEquals("checkbox", data.get(3).key());
     assertEquals("test", data.get(3).value());

     assertEquals("checkbox2", data.get(4).key());
     assertEquals("on", data.get(4).value());  // default value for checked checkbox without value

     assertEquals("radio", data.get(5).key());
     assertEquals("r1", data.get(5).value());
 }

 @Test
 public void usesOnForCheckboxValueIfNoValueSet() {
     String html = "<html><body><form>"
             + "<input type='checkbox' name='cb' checked />"
             + "</form></body></html>";
     Document doc = Jsoup.parse(html);
     FormElement form = (FormElement) doc.selectFirst("form");
     List<Connection.KeyVal> data = form.formData();

     assertEquals(1, data.size());
     assertEquals("cb", data.get(0).key());
     assertEquals("on", data.get(0).value());
 }

 @Test
 public void testCheckedCheckboxWithExplicitValue() {
     String html = "<html><body><form>"
             + "<input type='checkbox' name='cb' value='foo' checked />"
             + "</form></body></html>";
     Document doc = Jsoup.parse(html);
     FormElement form = (FormElement) doc.selectFirst("form");
     List<Connection.KeyVal> data = form.formData();

     assertEquals(1, data.size());
     assertEquals("foo", data.get(0).value());
 }

 @Test
 public void testUncheckedCheckboxOmitted() {
     String html = "<html><body><form>"
             + "<input type='checkbox' name='cb' />"
             + "</form></body></html>";
     Document doc = Jsoup.parse(html);
     FormElement form = (FormElement) doc.selectFirst("form"));
     List<Connection.KeyVal> data = form.formData();

     assertTrue("unchecked checkbox should not be included", data.isEmpty());
 }

 @Test
 public void testUncheckedRadioOmitted() {
     String html = "<html><body><form>"
             + "<input type='radio' name='radio' value='1' />"
             + "</form></body></html>";
     Document doc = Jsoup.parse(html);
     FormElement form = (FormElement) doc.selectFirst("form");
     List<Connection.KeyVal> data = form.formData();

     assertTrue("unchecked radio should not be included", data.isEmpty());
 }

 @Test
 public void testRadioCheckedWithNoValueYieldsOn() {
     String html = "<html><body><form>"
             + "<input type='radio' name='radio' checked />"
             + "</form></body></html>";
     Document doc = Jsoup.parse(html);
     FormElement form = (FormElement) doc.selectFirst("form");
     List<Connection.KeyVal> data = form.formData();

     assertEquals(1, data.size(););
     assertEquals("on", data.get(0).value());
 }

 @Test
 public void testSelectWithSelectedOption() {
     String html = "<html><body><form>"
             + "<select name='sel><option value='1'>One</option><option value='2'
selected>Two</option></select>"
             + "</form></body></html>;
     Document doc = Jsoup.parse(html);
     FormElement form = (FormElement) doc.selectFirst("form");
     List<Connection.KeyVal> data = form.formData();

     assertEquals(1, data.size());
     assertEquals("sel", data.get(0).key());
     assertEquals("2", data.get(0).value());
 }

 @Test
 public void testSelectWithNoSelectedOption() {
     String html = "<html><body><form>"
             + "<select name='sel'><option value='1'>One</option><option
value='2'>Two</option></select>"
             + "</form></body></html>"
             + "</form></body></html>";
     Document doc = Jsoup.parse(html);
     FormElement form = (FormElement) doc.selectFirst("form");
     List<Connection.KeyVal> data = form.formData();

     assertEquals(1, data.size());
     assertEquals("1", data.get(0).value()); // first option's value
 }

 @Test
 public void testInputWithEmptyNameSkipped() {
     String html = "<html><body><form>"
             + "<input type='text' name='' value='something' />"
             + "<input type='text' name='ok' value='included' />"
             + "</form></body></html>";
     Document doc = Jsoup.parse(html);
     FormElement form = (FormElement) doc.selectFirst("form");
     List<Connection.KeyVal> data = form.formData();

     assertEquals("only the named input should appear", 1, data.size());
     assertEquals("ok", data.get(0).key());
 }

 @Test
 public void testSubmitButtonNotIncluded() {
     String html = "<html><body><form>"
             + "<input type='text' name='t' value='v' />"
             + "<input type='submit' name='go' value='Go!' />"
             + "</form></body></html>";
     Document doc = Jsoup.parse(html);
     FormElement form = (FormElement) doc.selectFirst("form");
     List<Connection.KeyVal> data = form.formData();

     assertEquals("only the text input should be present, submit button excluded", 1, data.size());
     assertEquals("t", data.get(0).key());
     assertEquals("v", data.get(0).value());
 }

 @Test
 public void testNormalInputsTextPasswordHidden() {
     String html = "<html><body><form>"
             + "<input type='text' name='a' value='1' />"
             + "<input type='password' name='b' value='2' />"
             + "<input type='hidden' name='c' value='3' />"
             + "</form></body></html>";
     Document doc = Jsoup.parse(html);
     FormElement form = (FormElement) doc.selectFirst("form");
     List<Connection.KeyVal> data = form.formData();

     assertEquals(3, data.size());
     assertEquals("1", valueByKey(data, "a"));
     assertEquals("2", valueByKey(data, "b"));
     assertEquals("3", valueByKey(data, "c"));
 }

}