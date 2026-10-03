package org.apache.commons.cli;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;

 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;

 import org.junit.Assume;
 import org.junit.Rule;
 import org.junit.Test;
 import org.junit.rules.TemporaryFolder;

 public class TypeHandlerFileValueTest
 {
     @Rule
     public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void testCreateFileReturnsFile()
  {
      String path = "subdir" + File.separator + "file.txt";

      File result = TypeHandler.createFile(path);

      assertNotNull(result);
      assertEquals(path, result.getPath());
  }

  @Test
  public void testCreateValueFileValueReturnsFile() throws Exception
  {
      Object result = TypeHandler.createValue("data.txt", PatternOptionBuilder.FILE_VALUE);

      assertTrue("expected File instance but got: " + result, result instanceof File);
      assertEquals("data.txt", ((File) result).getPath());
  }

  @Test
  public void testCreateValueExistingFileValueReturnsFileInputStream() throws Exception
  {
      File existing = folder.newFile("existing.txt");

      Object result = TypeHandler.createValue(existing.getAbsolutePath(),
              PatternOptionBuilder.EXISTING_FILE_VALUE);

      assertNotNull("expected FileInputStream but got null", result);
      assertTrue("expected FileInputStream but got: " + result.getClass().getName(),
              result instanceof FileInputStream);

      FileInputStream in = (FileInputStream) result;
      try
      {
          assertEquals(-1, in.read());
      }
      finally
      {
          in.close();
      }
  }

  @Test
  public void testCreateValueExistingFileValueReadsContents() throws Exception
  {
      File existing = folder.newFile("existing.txt");
      write(existing, "expected-data");

      Object result = TypeHandler.createValue(existing.getAbsolutePath(),
              PatternOptionBuilder.EXISTING_FILE_VALUE);

      assertNotNull("expected FileInputStream but got null", result);
      assertTrue("expected FileInputStream but got: " + result.getClass().getName(),
              result instanceof FileInputStream);

      FileInputStream in = (FileInputStream) result;
      try
      {
          assertEquals("expected-data", new String(readAll(in), "UTF-8"));
      }
      finally
      {
          in.close();
      }
  }

  @Test
  public void testCreateValueNonExistingFileValueReturnsNull() throws Exception
  {
      File missing = new File(folder.getRoot(), "missing-" + System.nanoTime() + ".txt");

      assertFalse(missing.exists());

      Object result = TypeHandler.createValue(missing.getAbsolutePath(),
              PatternOptionBuilder.EXISTING_FILE_VALUE);

      assertNull("expected null for non-existing file but got: " + result, result);
  }

  @Test
  public void testCreateValueExistingFileValueReturnsNullForDirectory() throws Exception
  {
      File directory = folder.newFolder("directory");

      assertTrue(directory.isDirectory());

      Object result = TypeHandler.createValue(directory.getAbsolutePath(),
              PatternOptionBuilder.EXISTING_FILE_VALUE);

      assertNull("expected null for directory but got: " + result, result);
  }

  @Test
  public void testCreateValueExistingFileValueEmptyStringReturnsNull() throws Exception
  {
      Object result = TypeHandler.createValue("", PatternOptionBuilder.EXISTING_FILE_VALUE);

      assertNull("expected null for empty file name but got: " + result, result);
  }

  @Test
  public void testCreateValueExistingFileValueUnreadableFileReturnsNull() throws Exception
  {
      File noRead = folder.newFile("no-read.txt");
      write(noRead, "data");

      noRead.setReadable(false, false);
      Assume.assumeFalse("unable to create an unreadable file in this environment",
              noRead.canRead());

      Object result = TypeHandler.createValue(noRead.getAbsolutePath(),
              PatternOptionBuilder.EXISTING_FILE_VALUE);

      assertNull("expected null for unreadable file but got: " + result, result);
  }

  private static void write(File file, String content) throws IOException
  {
      FileOutputStream out = null;
      try
      {
          out = new FileOutputStream(file);
          out.write(content.getBytes("UTF-8"));
      }
      finally
      {
          if (out != null)
          {
              out.close();
          }
      }
  }

  private static byte[] readAll(InputStream in) throws IOException
  {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[128];
      int read;
      while ((read = in.read(buffer)) != -1)
      {
          out.write(buffer, 0, read);
      }
      return out.toByteArray();
  }

 }
