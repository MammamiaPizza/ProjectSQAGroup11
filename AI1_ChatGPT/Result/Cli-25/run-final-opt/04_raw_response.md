public void testPrintWrappedChunksLongWordWithinIndentedWidth()
{
    HelpFormatter formatter = new HelpFormatter();
    java.io.StringWriter writer = new java.io.StringWriter();
    java.io.PrintWriter printWriter = new java.io.PrintWriter(writer);

    formatter.printWrapped(printWriter, 10, 3, "abcdefghijklmnopqr");
    printWriter.flush();

    assertEquals("abcdefghij" + System.getProperty("line.separator")
        + "   klmnopq" + System.getProperty("line.separator")
        + "   r" + System.getProperty("line.separator"), writer.toString());
}

public void testPrintWrappedBreaksAtTabCharacter()
{
    HelpFormatter formatter = new HelpFormatter();
    java.io.StringWriter writer = new java.io.StringWriter();
    java.io.PrintWriter printWriter = new java.io.PrintWriter(writer);

    formatter.printWrapped(printWriter, 4, "abc\tdef");
    printWriter.flush();

    assertEquals("abc" + System.getProperty("line.separator")
        + "def" + System.getProperty("line.separator"), writer.toString());
}