@Test
public void shouldReturnNullFromCreateMockFor() throws Exception {
    class Probe {
        @org.mockito.Spy
        private java.util.List<String> items = new java.util.ArrayList<String>();
    }

    java.lang.reflect.Field field = Probe.class.getDeclaredField("items");
    java.lang.annotation.Annotation annotation = field.getAnnotation(org.mockito.Spy.class);

    org.junit.Assert.assertNull(new SpyAnnotationEngine().createMockFor(annotation, field));
}

@Test
public void shouldIgnoreFieldsWithoutSpyAnnotation() {
    class Probe {
        @org.mockito.Mock
        private java.util.List<String> items;
    }

    Probe probe = new Probe();

    new SpyAnnotationEngine().process(Probe.class, probe);

    org.junit.Assert.assertNull(probe.items);
}

@Test
public void shouldRejectSpyCombinedWithCaptorAnnotation() {
    class Probe {
        @org.mockito.Spy
        @org.mockito.Captor
        private java.util.List<String> items = new java.util.ArrayList<String>();
    }

    try {
        new SpyAnnotationEngine().process(Probe.class, new Probe());
        org.junit.Assert.fail("Expected conflicting @Spy and @Captor annotations to be rejected");
    } catch (org.mockito.exceptions.base.MockitoException expected) {
        org.junit.Assert.assertFalse(expected.getMessage().isEmpty());
    }
}

@Test
public void shouldRestorePrivateFieldAccessibilityWhenSpyCreationFails() throws Exception {
    class Probe {
        @org.mockito.Spy
        private java.util.List<String> items;
    }

    java.lang.reflect.Field field = Probe.class.getDeclaredField("items");
    org.junit.Assert.assertFalse(field.isAccessible());

    try {
        new SpyAnnotationEngine().process(Probe.class, new Probe());
        org.junit.Assert.fail("Expected processing an uninitialized @Spy field to fail");
    } catch (org.mockito.exceptions.base.MockitoException expected) {
        org.junit.Assert.assertFalse(field.isAccessible());
    }
}