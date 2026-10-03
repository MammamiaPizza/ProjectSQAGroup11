package org.mockito.internal.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.exceptions.base.MockitoException;

public class SpyAnnotationEngineTest {

    @Test
    public void shouldCreateNamedSpyThatDelegatesToAnnotatedFieldInstance() {
        SpyHolder holder = new SpyHolder();
        List<String> original = holder.items;

        new SpyAnnotationEngine().process(SpyHolder.class, holder);

        assertNotSame(original, holder.items);

        holder.items.add("value");

        assertEquals(1, holder.items.size());
        assertEquals("value", holder.items.get(0));
        assertVerificationFailureUsesFieldName(holder.items, "items.clear()");
    }

    @Test
    public void shouldResetAnAlreadySpiedAnnotatedField() {
        ExistingSpyHolder holder = new ExistingSpyHolder();
        holder.items.add("interaction before processing");

        new SpyAnnotationEngine().process(ExistingSpyHolder.class, holder);

        Mockito.verifyNoMoreInteractions(holder.items);
    }

    @Test
    public void shouldRejectSpyFieldWithoutAnInstance() {
        MissingInstanceHolder holder = new MissingInstanceHolder();

        try {
            new SpyAnnotationEngine().process(MissingInstanceHolder.class, holder);
            fail("Expected processing an uninitialized @Spy field to fail");
        } catch (MockitoException expected) {
            assertTrue(expected.getMessage().contains("Cannot create a @Spy for 'items' field"));
            assertTrue(expected.getMessage().contains("instance must be created"));
        }
    }

    @Test
    public void shouldRejectSpyCombinedWithMockAnnotation() {
        ConflictingAnnotationsHolder holder = new ConflictingAnnotationsHolder();

        try {
            new SpyAnnotationEngine().process(ConflictingAnnotationsHolder.class, holder);
            fail("Expected conflicting @Spy and @Mock annotations to be rejected");
        } catch (MockitoException expected) {
            assertFalse(expected.getMessage().isEmpty());
        }
    }

    @Test
    public void shouldRestorePrivateFieldAccessibilityAfterProcessing() throws Exception {
        PrivateSpyHolder holder = new PrivateSpyHolder();
        Field field = PrivateSpyHolder.class.getDeclaredField("items");

        assertFalse(field.isAccessible());

        new SpyAnnotationEngine().process(PrivateSpyHolder.class, holder);

        assertFalse(field.isAccessible());
        assertTrue(holder.items.contains("initial"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void assertVerificationFailureUsesFieldName(List<?> spy, String expectedInvocation) {
        try {
            Mockito.verify((List) spy).clear();
            fail("Expected verification of an uninvoked method to fail");
        } catch (AssertionError expected) {
            assertTrue(expected.getMessage().contains(expectedInvocation));
        }
    }

    private static class SpyHolder {
        @Spy
        List<String> items = new ArrayList<String>();
    }

    private static class ExistingSpyHolder {
        @Spy
        List<String> items = Mockito.spy(new ArrayList<String>());
    }

    private static class MissingInstanceHolder {
        @Spy
        List<String> items;
    }

    private static class ConflictingAnnotationsHolder {
        @Spy
        @Mock
        List<String> items = new ArrayList<String>();
    }

    private static class PrivateSpyHolder {
        @Spy
        private List<String> items = new ArrayList<String>();

        private PrivateSpyHolder() {
            items.add("initial");
        }
    }
}