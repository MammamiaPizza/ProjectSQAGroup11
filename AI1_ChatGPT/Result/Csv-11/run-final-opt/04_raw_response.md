@Test
public void explicitHeaderCanSkipTheFirstInputRecord() throws Exception {
    final org.apache.commons.csv.CSVParser parser = org.apache.commons.csv.CSVParser.parse(
            "name\nvalue\n",
            org.apache.commons.csv.CSVFormat.DEFAULT.withHeader("name").withSkipHeaderRecord());
    try {
        final java.util.List<org.apache.commons.csv.CSVRecord> records = parser.getRecords();
        org.junit.Assert.assertEquals(1, records.size());
        org.junit.Assert.assertEquals("value", records.get(0).get(0));
        org.junit.Assert.assertEquals(Integer.valueOf(0), parser.getHeaderMap().get("name"));
    } finally {
        parser.close();
    }
}

@Test
public void unterminatedFinalRecordIsReturnedAndUpdatesRecordNumber() throws Exception {
    final org.apache.commons.csv.CSVParser parser = org.apache.commons.csv.CSVParser.parse(
            "value", org.apache.commons.csv.CSVFormat.DEFAULT);
    try {
        final java.util.List<org.apache.commons.csv.CSVRecord> records = parser.getRecords();
        org.junit.Assert.assertEquals(1, records.size());
        org.junit.Assert.assertEquals("value", records.get(0).get(0));
        org.junit.Assert.assertEquals(1L, parser.getRecordNumber());
        org.junit.Assert.assertTrue(parser.getCurrentLineNumber() > 0);
    } finally {
        parser.close();
    }
}

@Test
public void consecutiveCommentsDoNotCreateDataRecords() throws Exception {
    final org.apache.commons.csv.CSVParser parser = org.apache.commons.csv.CSVParser.parse(
            "# first comment\n# second comment\nvalue\n",
            org.apache.commons.csv.CSVFormat.DEFAULT.withCommentMarker('#'));
    try {
        final java.util.List<org.apache.commons.csv.CSVRecord> records = parser.getRecords();
        org.junit.Assert.assertEquals(1, records.size());
        org.junit.Assert.assertEquals("value", records.get(0).get(0));
    } finally {
        parser.close();
    }
}

@Test
public void fileAndUrlFactoriesReadTheSameCsvContent() throws Exception {
    final java.io.File file = java.io.File.createTempFile("csv-parser", ".csv");
    try {
        final java.io.FileOutputStream output = new java.io.FileOutputStream(file);
        try {
            output.write("value\n".getBytes("UTF-8"));
        } finally {
            output.close();
        }

        final java.nio.charset.Charset charset = java.nio.charset.Charset.forName("UTF-8");
        org.apache.commons.csv.CSVParser parser = org.apache.commons.csv.CSVParser.parse(
                file, charset, org.apache.commons.csv.CSVFormat.DEFAULT);
        try {
            org.junit.Assert.assertEquals("value", parser.getRecords().get(0).get(0));
        } finally {
            parser.close();
        }

        parser = org.apache.commons.csv.CSVParser.parse(
                file.toURI().toURL(), charset, org.apache.commons.csv.CSVFormat.DEFAULT);
        try {
            org.junit.Assert.assertEquals("value", parser.getRecords().get(0).get(0));
        } finally {
            parser.close();
        }
    } finally {
        file.delete();
    }
}