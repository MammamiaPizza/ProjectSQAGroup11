package org.mockito.internal.invocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class InvocationMatcherRegressionTest {

    public interface VarArgApi {
        String join(String... values);

        void strings(String prefix, String... values);

        void objects(Object... values);

        boolean booleans(boolean... values);

        void mixed(String name, int number, String... values);
    }

    @Test
    public void shouldVerifyNonEmptyStringVarargs() {
        VarArgApi api = mock(VarArgApi.class);

        api.strings("prefix", "one", "two");

        verify(api).strings("prefix", "one", "two");
    }

    @Test
    public void shouldVerifyEmptyVarargs() {
        VarArgApi api = mock(VarArgApi.class);

        api.strings("prefix");

        verify(api).strings("prefix");
    }

    @Test
    public void shouldVerifyNullVarargArray() {
        VarArgApi api = mock(VarArgApi.class);

        api.strings("prefix", (String[]) null);

        verify(api).strings("prefix", (String[]) null);
    }

    @Test
    public void shouldCaptureEachVarargWhenMatcherCountDiffersFromRawArgumentCount() {
        VarArgApi api = mock(VarArgApi.class);
        ArgumentCaptor<String> captured = ArgumentCaptor.forClass(String.class);

        api.strings("prefix", "first", "second");

        verify(api).strings(eq("prefix"), captured.capture(), captured.capture());

        assertEquals(2, captured.getAllValues().size());
        assertEquals("first", captured.getAllValues().get(0));
        assertEquals("second", captured.getAllValues().get(1));
    }

    @Test
    public void shouldVerifyObjectVarargsUsingAnyObjectMatcher() {
        VarArgApi api = mock(VarArgApi.class);

        api.objects("text", Integer.valueOf(3));

        verify(api).objects((Object[]) anyObject());
    }

    @Test
    public void shouldStubStringVarargsUsingAnyObjectMatcher() {
        VarArgApi api = mock(VarArgApi.class);

        when(api.join((String[]) anyObject())).thenReturn("matched");

        assertEquals("matched", api.join("one", "two"));
        assertEquals("matched", api.join());
    }

    @Test
    public void shouldStubBooleanVarargsUsingAnyObjectMatcher() {
        VarArgApi api = mock(VarArgApi.class);

        when(api.booleans((boolean[]) anyObject())).thenReturn(true);

        assertTrue(api.booleans(true, false));
        assertTrue(api.booleans());
        assertFalse(mock(VarArgApi.class).booleans(true));
    }

    @Test
    public void shouldUseLatestStubbingForSameVarargsInvocation() {
        VarArgApi api = mock(VarArgApi.class);

        when(api.join("value")).thenReturn("first");
        when(api.join("value")).thenReturn("second");

        assertEquals("second", api.join("value"));
    }

    @Test
    public void shouldVerifyMixedFixedAndVarargsArguments() {
        VarArgApi api = mock(VarArgApi.class);

        api.mixed("name", 7, "left", "right");

        verify(api).mixed(eq("name"), eq(7), eq("left"), eq("right"));
    }

    @Test
    public void shouldRemoveVarargsStubbingAfterReset() {
        VarArgApi api = mock(VarArgApi.class);

        when(api.join("value")).thenReturn("stubbed");
        reset(api);

        assertNull(api.join("value"));
    }
}
