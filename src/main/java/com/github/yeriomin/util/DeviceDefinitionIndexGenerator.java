package com.github.yeriomin.util;

import java.io.*;
import java.util.*;

public class DeviceDefinitionIndexGenerator {

    private static final String usageMessage = "Usage: java " + DeviceDefinitionIndexGenerator.class.getSimpleName() + " <device defs dir> <index target file>";
    private static final String filePrefix = "device-";
    private static final String fileSuffix = ".properties";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Where are the device definitions? Where should the index be created?");
            System.out.println(usageMessage);
            System.exit(128);
        }
        File deviceDefinitionsDir = new File(args[0]);
        if (!deviceDefinitionsDir.exists()) {
            System.out.println("Device definition storage does not exist");
            System.exit(128);
        }
        if (!deviceDefinitionsDir.isDirectory()) {
            System.out.println("Device definition storage is not a directory");
            System.exit(128);
        }
        if (args.length < 2) {
            System.out.println("Where should the index be created?");
            System.out.println(usageMessage);
            System.exit(128);
        }
        File indexTargetFile = new File(args[1]);
        new DeviceDefinitionIndexGenerator().createIndex(deviceDefinitionsDir, indexTargetFile);
    }

    public void createIndex(File deviceDefinitionsDir, File target) {
        List<String> deviceList = buildDeviceList(deviceDefinitionsDir);
        writeDeviceList(deviceList, target);
        System.out.println("Done");
    }

    private List<String> buildDeviceList(File deviceDefinitionsDir) {
        File[] files = deviceDefinitionsDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(filePrefix) && name.endsWith(fileSuffix);
            }
        });
        if (null == files || files.length == 0) {
            return new ArrayList<>();
        }
        List<String> deviceList = new ArrayList<>();

        System.out.println("Processing " + files.length + " device definitions");
        for (File deviceDefinitionFile: files) {
            String deviceName = deviceDefinitionFile.getName().substring(filePrefix.length(), deviceDefinitionFile.getName().length() - fileSuffix.length());
            Properties properties = getProperties(deviceDefinitionFile);
            if (null == properties) {
                continue;
            }
            String searchableString = getSearchableString(properties);
            String userReadableName = getUserReadableName(properties);
            if (deviceName.length() == 0 || searchableString.trim().length() == 0 || userReadableName.trim().length() == 0) {
                System.out.println(deviceName + " is fishy");
                continue;
            }
            deviceList.add(String.join(",", new String[] {deviceName, searchableString, userReadableName}));
        }
        return deviceList;
    }

    private Properties getProperties(File deviceDefinitionFile) {
        try (InputStream inputStream = new FileInputStream(deviceDefinitionFile)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            System.out.println("Could not read " + deviceDefinitionFile + ": " + e.getMessage());
        }
        return null;
    }

    private String getSearchableString(Properties properties) {
        Set<String> components = new HashSet<>();
        components.add(properties.getProperty("Build.MANUFACTURER"));
        components.add(properties.getProperty("Build.MODEL"));
        components.add(properties.getProperty("Build.PRODUCT"));
        components.add(properties.getProperty("Build.DEVICE"));
        components.add(properties.getProperty("Build.BRAND"));
        components.add(properties.getProperty("Build.HARDWARE"));
        return String.join(" ", components)
            .replace("\n", " ")
            .replace("\r", " ")
            .replace(",", " ")
            .trim()
        ;
    }

    private String getUserReadableName(Properties properties) {
        String fingerprint = properties.getProperty("Build.FINGERPRINT", "");
        String manufacturer = properties.getProperty("Build.MANUFACTURER", "");
        String product = properties.getProperty("Build.PRODUCT", "").replace("aokp_", "").replace("aosp_", "").replace("cm_", "").replace("lineage_", "");
        String model = properties.getProperty("Build.MODEL", "");
        String device = properties.getProperty("Build.DEVICE", "");
        String result = (fingerprint.toLowerCase().contains(product.toLowerCase()) || product.toLowerCase().contains(device.toLowerCase()) || device.toLowerCase().contains(product.toLowerCase())) ? model : product;
        if (!result.toLowerCase().contains(manufacturer.toLowerCase())) {
            result = manufacturer + " " + result;
        }
        if (result.length() == 0) {
            return result;
        }
        return (result.substring(0, 1).toUpperCase() + result.substring(1))
            .replace("\n", " ")
            .replace("\r", " ")
            .replace(",", " ")
            .trim()
        ;
    }

    private void writeDeviceList(List<String> deviceList, File target) {
        System.out.println("Writing " + deviceList.size() + " device searchable strings to " + target);
        try (FileWriter fileWriter = new FileWriter(target)) {
            for (String device: deviceList) {
                fileWriter.append(device).append("\r\n");
            }
        } catch (IOException e) {
            System.out.println("Could not write: " + e.getMessage());
        }
    }
}
