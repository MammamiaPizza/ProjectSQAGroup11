package org.mockito.internal.invocation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class InvocationMatcherVarargsRegressionTest {

    private interface VarargService {
        void strings(String... values);

        void prefixed(String prefix, String... values);

        void objects(Object... values);

        void bytes(byte... values);
    }

    @Test
    public void capturesAllValuesWhenSingleCaptorIsUsedForPureStringVarargs() {
        VarargService service = mock(VarargService.class);
        service.strings("a", "b");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(service).strings(captor.capture());

        assertEquals(Arrays.asList("a", "b"), captor.getAllValues());
    }

    @Test
    public void capturesAllValuesWhenSingleCaptorIsUsedAfterFixedArgument() {
        VarargService service = mock(VarargService.class);
        service.prefixed("prefix", "a", "b", "c", "again ?!");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(service).prefixed(org.mockito.Matchers.eq("prefix"), captor.capture());

        assertEquals(Arrays.asList("a", "b", "c", "again ?!"), captor.getAllValues());
    }

    @Test
    public void capturesCorrespondingValuesWhenSameCaptorIsUsedMultipleTimes() {
        VarargService service = mock(VarargService.class);
        service.strings("first", "second");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(service).strings(captor.capture(), captor.capture());

        assertEquals(Arrays.asList("first", "second"), captor.getAllValues());
    }

    @Test
    public void matchesOnlyInvocationWithCorrectNumberOfAnyObjectVarargs() {
        VarargService service = mock(VarargService.class);
        service.objects("single");
        service.objects("first", "second");

        verify(service).objects("single");
        verify(service, times(1)).objects(anyObject(), anyObject());
        verifyNoMoreInteractions(service);
    }

    @Test
    public void capturesPrimitiveByteVarargsWithWrapperCaptor() {
        VarargService service = mock(VarargService.class);
        service.bytes((byte) 1, (byte) 2, (byte) 3);

        ArgumentCaptor<Byte> captor = ArgumentCaptor.forClass(Byte.class);
        verify(service).bytes(captor.capture());

        assertEquals(Arrays.asList(Byte.valueOf((byte) 1), Byte.valueOf((byte) 2), Byte.valueOf((byte) 3)),
                captor.getAllValues());
    }

    @Test
    public void capturesPrimitiveByteVarargsWithPrimitiveCaptorClass() {
        VarargService service = mock(VarargService.class);
        service.bytes((byte) 4, (byte) 5);

        ArgumentCaptor<Byte> captor = ArgumentCaptor.forClass(byte.class);
        verify(service).bytes(captor.capture());

        assertEquals(Arrays.asList(Byte.valueOf((byte) 4), Byte.valueOf((byte) 5)), captor.getAllValues());
    }
}