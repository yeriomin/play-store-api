package com.github.yeriomin.copydevice;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class CopyDevice {

    private File destination;

    public CopyDevice(File destination) {
        this.destination = destination;
    }

    public void walk(File directory) {
        File[] list = directory.listFiles();
        if (list == null) {
            return;
        }
        for (File file: list) {
            if (file.isDirectory()) {
                walk(file);
            } else if (isDeviceDefinition(file) && shouldCopy(file)) {
                try {
                    Files.copy(file.toPath(), Paths.get(destination.getAbsolutePath(), file.getName()), REPLACE_EXISTING);
                    System.out.println("Successfully copied " + file.getName());
                } catch (IOException e) {
                    System.out.println("Could not copy " + file.getName() + " : " + e.getMessage().trim());
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isDeviceDefinition(File file) {
        return file.isFile() && file.length() > 0 && file.getName().startsWith("device-") && file.getName().endsWith(".properties");
    }

    private boolean shouldCopy(File file) {
        File existingFile = new File(destination, file.getName());
        if (!existingFile.exists()) {
            return true;
        }
        Properties currentProperties = new Properties();
        try (FileReader reader = new FileReader(file)) {
            currentProperties.load(reader);
        } catch (IOException e) {
            System.out.println("Cannot read " + file.getAbsolutePath() + " : " + e.getMessage());
            return false;
        }
        int currentVcVending = getIntProperty(currentProperties, "Vending.version");
        int currentVcGsf = getIntProperty(currentProperties, "GSF.version");
        int currentSdk = getIntProperty(currentProperties, "Build.VERSION.SDK_INT");
        Properties existingProperties = new Properties();
        try (FileReader reader = new FileReader(existingFile)) {
            existingProperties.load(reader);
        } catch (IOException e) {
            System.out.println("Cannot read " + existingFile.getAbsolutePath() + " : " + e.getMessage());
            return false;
        }
        int existingVcVending = getIntProperty(existingProperties, "Vending.version");
        int existingVcGsf = getIntProperty(existingProperties, "GSF.version");
        int existingSdk = getIntProperty(existingProperties, "Build.VERSION.SDK_INT");
        return (currentVcGsf > 1000000 && currentVcVending > 1000000 && existingVcGsf == 0 && existingVcVending == 0)
            || (currentSdk > existingSdk && currentVcGsf > 1000000 && currentVcVending > 1000000)
            || (currentSdk == existingSdk && currentVcGsf >= existingVcGsf && currentVcVending >= existingVcVending)
        ;
    }

    private int getIntProperty(Properties properties, String key) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static void main(String[] arguments) {
        if (arguments.length < 2) {
            System.out.println("Source directory and destination directory expected");
            System.exit(128);
        }
        File source = new File(arguments[0]);
        File destination = new File(arguments[1]);
        if (!source.exists() || !source.isDirectory()) {
            System.out.println("Source directory does not exist or is not a directory");
            System.exit(128);
        }
        if (!destination.exists() || !destination.isDirectory() || !destination.canWrite()) {
            System.out.println("Destination directory does not exist or is not a directory or is not writable");
            System.exit(128);
        }
        try {
            System.out.println("Walking " + source.getAbsolutePath() + " and putting device definitions into " + destination.getAbsolutePath());
            new CopyDevice(destination).walk(source);
            System.out.println("Done");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
