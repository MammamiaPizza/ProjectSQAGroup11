@Test
public void freshMapHasNoLookupOrIterableAnnotations() {
    AnnotationMap map = new AnnotationMap();

    assertNull(map.get(Flag.class));
    assertFalse(map.annotations().iterator().hasNext());
    assertEquals("[null]", map.toString());
}

@Test
public void mergeWithNullSecondaryReturnsPrimaryMap() {
    AnnotationMap primary = new AnnotationMap();
    primary.add(flag());

    assertTrue(AnnotationMap.merge(primary, null) == primary);
}

@Test
public void toStringReflectsInitializedAnnotationContents() {
    AnnotationMap map = new AnnotationMap();
    java.lang.annotation.Annotation annotation = flag();
    map.add(annotation);

    assertEquals(java.util.Collections.singletonMap(Flag.class, annotation).toString(),
            map.toString());
}