package com.github.httpmock.exec;

import org.apache.openejb.loader.Files;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.Zips;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationServerDistribution {
    public static final String PROPERTY_DISTRIBUTION = "distribution";

    private File distributionDirectory;
    private Properties properties;

    public ApplicationServerDistribution(File distributionDirectory, Properties properties) {
        this.distributionDirectory = distributionDirectory;
        this.properties = properties;
    }

    public void updateIfNecessary() throws IOException {
        File timestampFile = getTimestampFile();
        boolean forceDelete = Boolean.getBoolean("tomee.runner.force-delete");
        if (forceDelete || !timestampFile.exists() || isUpdateRequired()) {
            if (forceDelete || timestampFile.exists()) {
                System.out.println("Deleting " + distributionDirectory.getAbsolutePath());
                Files.delete(distributionDirectory);
            }
            extractApplicationServer();
            writeTimestamp();
        }
    }

    void writeTimestamp() throws IOException {
        File timestampFile = getTimestampFile();
        String currentTime = Long.toString(System.currentTimeMillis());
        IO.writeString(timestampFile, properties.getProperty("timestamp", currentTime));
    }

    private boolean isUpdateRequired() throws IOException {
        File timestampFile = getTimestampFile();
        return getTimestampFromFile(timestampFile) < getTimestampFromConfig();
    }

    private long getTimestampFromConfig() {
        return Long.parseLong(properties.getProperty("timestamp"));
    }

    private long getTimestampFromFile(File timestampFile) throws IOException {
        return Long.parseLong(IO.slurp(timestampFile).replace(System.getProperty("line.separator"), ""));
    }

    private File getTimestampFile() {
        return new File(distributionDirectory, "timestamp.txt");
    }

    private void extractApplicationServer() throws IOException {
        String distrib = properties.getProperty(PROPERTY_DISTRIBUTION);
        System.out.println("Extracting tomee to " + distributionDirectory.getAbsolutePath());
        unzip(distributionDirectory, distrib);
    }

    void unzip(File distribOutput, String distrib) throws IOException {
        ClassLoader contextClassLoader = getClassLoader();
        InputStream distribIs = contextClassLoader.getResourceAsStream(distrib);
        Zips.unzip(distribIs, distribOutput, false);
    }

    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
