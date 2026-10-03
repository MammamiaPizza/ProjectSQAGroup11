public void testCompileCreatesNestedZoneFileAndAlias() throws Exception {
    java.io.File root = java.io.File.createTempFile("ZoneInfoCompiler", "test");
    try {
        assertTrue(root.delete());
        assertTrue(root.mkdirs());

        java.io.File source = new java.io.File(root, "zones");
        java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(source));
        try {
            writer.write("Zone Test/Nested 0 - TST\n");
            writer.write("Link Test/Nested Test/Alias\n");
        } finally {
            writer.close();
        }

        java.io.File output = new java.io.File(root, "output");
        java.util.Map zones = new ZoneInfoCompiler().compile(output,
            new java.io.File[] {source});

        assertTrue(output.isDirectory());
        assertTrue(new java.io.File(output, "Test/Nested").isFile());
        assertNotNull(zones.get("Test/Nested"));
        assertEquals(zones.get("Test/Nested"), zones.get("Test/Alias"));
    } finally {
        deleteRecursively(root);
    }
}

public void testCompileHonorsStandardTimeRuleTransitions() throws Exception {
    java.io.File source = java.io.File.createTempFile("ZoneInfoCompiler", "rules");
    try {
        java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(source));
        try {
            writer.write("Rule Shift 2000 only - Mar 1 0:00s 1:00 D\n");
            writer.write("Rule Shift 2000 only - Oct 1 1:00s 0 S\n");
            writer.write("Zone Test/Standard 2:00 Shift T%sT\n");
        } finally {
            writer.close();
        }

        java.util.Map zones = new ZoneInfoCompiler().compile(null,
            new java.io.File[] {source});
        org.joda.time.DateTimeZone zone =
            (org.joda.time.DateTimeZone) zones.get("Test/Standard");

        java.util.Calendar calendar =
            new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.set(2000, java.util.Calendar.FEBRUARY, 29, 22, 0, 0);
        long springTransition = calendar.getTimeInMillis();
        calendar.clear();
        calendar.set(2000, java.util.Calendar.SEPTEMBER, 30, 23, 0, 0);
        long autumnTransition = calendar.getTimeInMillis();

        assertEquals(2 * 60 * 60 * 1000, zone.getOffset(springTransition - 1));
        assertEquals("TST", zone.getNameKey(springTransition - 1));
        assertEquals(3 * 60 * 60 * 1000, zone.getOffset(springTransition));
        assertEquals("TDT", zone.getNameKey(springTransition));
        assertEquals(springTransition, zone.nextTransition(springTransition - 1));

        assertEquals(3 * 60 * 60 * 1000, zone.getOffset(autumnTransition - 1));
        assertEquals("TDT", zone.getNameKey(autumnTransition - 1));
        assertEquals(2 * 60 * 60 * 1000, zone.getOffset(autumnTransition));
        assertEquals("TST", zone.getNameKey(autumnTransition));
        assertEquals(autumnTransition, zone.nextTransition(autumnTransition - 1));
    } finally {
        source.delete();
    }
}

public void testCompileRejectsFileAsOutputDirectory() throws Exception {
    java.io.File output = java.io.File.createTempFile("ZoneInfoCompiler", "output");
    try {
        try {
            new ZoneInfoCompiler().compile(output, null);
            fail();
        } catch (java.io.IOException e) {
            assertTrue(e.getMessage().indexOf("not a directory") >= 0);
        }
    } finally {
        output.delete();
    }
}

private static void deleteRecursively(java.io.File file) {
    if (file != null && file.isDirectory()) {
        java.io.File[] children = file.listFiles();
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                deleteRecursively(children[i]);
            }
        }
    }
    if (file != null) {
        file.delete();
    }
}