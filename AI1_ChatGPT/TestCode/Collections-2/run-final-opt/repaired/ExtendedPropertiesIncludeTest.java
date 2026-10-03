package org.apache.commons.collections;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ExtendedPropertiesIncludeTest {

    @Test
    public void defaultIncludeDirectiveIsInclude() {
        ExtendedProperties properties = new ExtendedProperties();

        assertEquals("include", properties.getInclude());
    }

    @Test
    public void setIncludeChangesDirectiveNameReportedByGetter() {
        ExtendedProperties properties = new ExtendedProperties();
        String previous = properties.getInclude();

        try {
            properties.setInclude("loadFrom");

            assertEquals("loadFrom", properties.getInclude());
        } finally {
            properties.setInclude(previous);
        }
    }

    @Test
    public void loadRecognizesDefaultIncludeDirectiveAndLoadsRelativeFile() throws Exception {
        File directory = createTemporaryDirectory();
        File included = new File(directory, "included.properties");
        File root = new File(directory, "root.properties");

        try {
            write(included, "child.value=loaded-from-child\n");
            write(root, "root.value=loaded-from-root\ninclude=included.properties\n");

            ExtendedProperties properties = new ExtendedProperties(root.getAbsolutePath());

            assertEquals("loaded-from-root", properties.getString("root.value"));
            assertEquals("loaded-from-child", properties.getString("child.value"));
            assertNull(properties.getProperty("include"));
        } finally {
            deleteRecursively(directory);
        }
    }

    @Test
    public void loadUsesCustomIncludeDirectiveAfterItIsConfigured() throws Exception {
        ExtendedProperties loaded = new ExtendedProperties();
        String previous = loaded.getInclude();
        File directory = createTemporaryDirectory();
        File included = new File(directory, "custom.properties");
        File root = new File(directory, "root.properties");

        try {
            write(included, "custom.child=yes\n");
            write(root, "loadFrom=custom.properties\n");

            loaded.setInclude("loadFrom");
            loaded.basePath = directory.getAbsolutePath();

            FileInputStream input = new FileInputStream(root);
            try {
                loaded.load(input);
            } finally {
                input.close();
            }

            assertEquals("yes", loaded.getString("custom.child"));
            assertNull(loaded.getProperty("loadFrom"));
        } finally {
            loaded.setInclude(previous);
            deleteRecursively(directory);
        }
    }

    private static File createTemporaryDirectory() throws Exception {
        File directory = File.createTempFile("extended-properties-", "");
        if (!directory.delete() || !directory.mkdir()) {
            throw new IllegalStateException("Unable to create temporary directory");
        }
        return directory;
    }

    private static void write(File file, String content) throws Exception {
        FileOutputStream output = new FileOutputStream(file);
        try {
            output.write(content.getBytes("ISO-8859-1"));
        } finally {
            output.close();
        }
    }

    private static void deleteRecursively(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    deleteRecursively(children[i]);
                }
            }
        }
        file.delete();
    }
}
