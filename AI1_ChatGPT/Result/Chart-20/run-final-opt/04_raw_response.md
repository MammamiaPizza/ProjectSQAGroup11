@org.junit.Test
public void testEqualsReturnsTrueForSameInstance() {
    ValueMarker marker = new ValueMarker(1.0);
    org.junit.Assert.assertTrue(marker.equals(marker));
}