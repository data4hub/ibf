/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rest4hub.ibf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author Francisco Guimarães
 * @since 12/05/2016
 */
public class BannerPrinter {

    public static final String BANNER_LOCATION_PROPERTY = "banner.location";
    private static final String DEFAULT_BANNER_LOCATION = "banner.txt";
    private static Map<String, String> envAndProperties;

    static {
        initEnvAndProperties();
    }

    @SuppressWarnings("unchecked")
    private static void initEnvAndProperties() {
        envAndProperties = new HashMap(System.getProperties());
        envAndProperties.putAll(System.getenv());
    }

    public static void print() {
        String location = envAndProperties.getOrDefault(
                BANNER_LOCATION_PROPERTY,
                DEFAULT_BANNER_LOCATION);
        try (InputStream resourceStream = getResourceStream(location)) {
            if (resourceStream != null) {
                String banner = new BufferedReader(new InputStreamReader(resourceStream))
                        .lines()
                        .collect(Collectors.joining("\n"));
                if (!banner.trim().isEmpty()) {
                    System.out.println(replaceEnvAndProperties(banner));
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static InputStream getResourceStream(String location) {
        InputStream resourceStream = BannerPrinter.class.getClassLoader().getResourceAsStream(location);
        if (resourceStream == null) {
            final File file = new File(location);
            if (file.exists()) {
                try {
                    resourceStream = new FileInputStream(file);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return resourceStream;
    }

    private static String replaceEnvAndProperties(String banner) {
        String pattern = "\\$\\{([A-Za-z0-9!]+)\\}";
        Pattern expr = Pattern.compile(pattern);
        Matcher matcher = expr.matcher(banner);
        while (matcher.find()) {
            String varReplace = matcher.group();
            String var = matcher.group(1);
            String defaulValue = "";
            String[] varOrDefault = var.split("!");
            var = varOrDefault[0];
            if (varOrDefault.length == 2) {
                defaulValue = varOrDefault[1];
            }
            String envValue = envAndProperties.get(var);
            if (envValue == null) {
                envValue = defaulValue;
            } else {
                envValue = envValue.replace("\\", "\\\\");
            }
            Pattern subexpr = Pattern.compile(Pattern.quote(varReplace));
            banner = subexpr.matcher(banner).replaceAll(envValue);
        }
        return banner;
    }

}
