package org.mockito.internal.configuration;

import static org.junit.Assert.*;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.util.MockUtil;

public class SpyAnnotationEngineTest {

 static class SingleSpyField {
     @Spy Object f = new Object();
 }

 static class SpecialCharField {
     @Spy Object my$Field = new Object();
 }

 static class PrivateSpyField {
     @Spy private Object secret = new Object();
 }

 static class InnerClassFixture {
     @Spy Object innerField = new Object();
 }

 static class NullSpyField {
     @Spy Object nullField;
 }

 static class MultipleSpyFields {
     @Spy Object a = new Object();
     @Spy Object b = new Object();
 }

 static class NonSpyFieldFixture {
     @Spy Object spied = new Object();
     Object notSpied = new Object();
 }

 static class CombinedAnnotationFixture {
     @Spy @Mock Object bad;
 }

 @Test
 public void shouldUseFieldNameAsSpyName() {
     SingleSpyField tc = new SingleSpyField();
     new SpyAnnotationEngine().process(SingleSpyField.class, tc);
     assertEquals("f", tc.f.toString());
 }

 @Test
 public void shouldHandleSpecialCharsInFieldName() {
     SpecialCharField tc = new SpecialCharField();
     new SpyAnnotationEngine().process(SpecialCharField.class, tc);
     assertEquals("my$Field", tc.my$Field.toString());
 }

 @Test
 public void shouldSpyPrivateField() {
     PrivateSpyField tc = new PrivateSpyField();
     new SpyAnnotationEngine().process(PrivateSpyField.class, tc);
     assertEquals("secret", tc.secret.toString());
 }

 @Test
 public void shouldSpyFieldInInnerClass() {
     InnerClassFixture tc = new InnerClassFixture();
     new SpyAnnotationEngine().process(InnerClassFixture.class, tc);
     assertEquals("innerField", tc.innerField.toString());
 }

 @Test(expected = MockitoException.class)
 public void shouldThrowExceptionWhenInstanceNull() {
     NullSpyField tc = new NullSpyField();
     new SpyAnnotationEngine().process(NullSpyField.class, tc);
 }

 @Test
 public void shouldAssignCorrectNamesToMultipleSpyFields() {
     MultipleSpyFields tc = new MultipleSpyFields();
     new SpyAnnotationEngine().process(MultipleSpyFields.class, tc);
     assertEquals("a", tc.a.toString());
     assertEquals("b", tc.b.toString());
 }

 @Test
 public void shouldIgnoreNonSpyField() {
     NonSpyFieldFixture tc = new NonSpyFieldFixture();
     new SpyAnnotationEngine().process(NonSpyFieldFixture.class, tc);
     assertFalse(new MockUtil().isMock(tc.notSpied));
 }

 @Test
 public void shouldResetAlreadyMockedSpy() {
     SingleSpyField tc = new SingleSpyField();
     tc.f = Mockito.mock(Object.class);
     Object originalMock = tc.f;
     new SpyAnnotationEngine().process(SingleSpyField.class, tc);
     assertSame(originalMock, tc.f);
 }

 @Test(expected = RuntimeException.class)
 public void shouldThrowForCombinedSpyAndMock() {
     CombinedAnnotationFixture tc = new CombinedAnnotationFixture();
     new SpyAnnotationEngine().process(CombinedAnnotationFixture.class, tc);
 }

}