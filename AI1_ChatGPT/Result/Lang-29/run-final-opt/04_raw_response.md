@Test
public void testJavaVersionMatchHandlesNullAndPrefixes() {
    org.junit.Assert.assertFalse(SystemUtils.isJavaVersionMatch(null, "1.6"));
    org.junit.Assert.assertTrue(SystemUtils.isJavaVersionMatch("1.6.0_23", "1.6"));
    org.junit.Assert.assertFalse(SystemUtils.isJavaVersionMatch("1.5.0_22", "1.6"));
}

@Test
public void testOSNameMatchHandlesNullAndPrefixes() {
    org.junit.Assert.assertFalse(SystemUtils.isOSNameMatch(null, "Windows"));
    org.junit.Assert.assertTrue(SystemUtils.isOSNameMatch("Windows 7", "Windows"));
    org.junit.Assert.assertFalse(SystemUtils.isOSNameMatch("Linux", "Windows"));
}

@Test
public void testJavaVersionFloatHandlesNullAndSingleComponentVersions() {
    org.junit.Assert.assertEquals(0.0f, SystemUtils.toJavaVersionFloat(null), 0.0f);
    org.junit.Assert.assertEquals(9.0f, SystemUtils.toJavaVersionFloat("9"), 0.0f);
}

@Test
public void testDirectoryAccessorsReflectSystemProperties() {
    org.junit.Assert.assertEquals(new java.io.File(System.getProperty("java.home")), SystemUtils.getJavaHome());
    org.junit.Assert.assertEquals(new java.io.File(System.getProperty("java.io.tmpdir")), SystemUtils.getJavaIoTmpDir());
    org.junit.Assert.assertEquals(new java.io.File(System.getProperty("user.dir")), SystemUtils.getUserDir());
    org.junit.Assert.assertEquals(new java.io.File(System.getProperty("user.home")), SystemUtils.getUserHome());
}