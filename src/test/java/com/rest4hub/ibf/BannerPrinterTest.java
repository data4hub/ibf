/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rest4hub.ibf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TestRule;

/**
 *
 * @author francisco
 */
public class BannerPrinterTest {

    private final StringBuffer printBuffer = new StringBuffer();

    private static final String BANNER_PATH = "target/classes/banner.txt";
    private static final String BANNER_TEXT = "im here";
    private static final String CUSTOM_BANNER = "custom-banner.txt";
    private static final String CUSTOM_BANNER_PATH = "target/classes/" + CUSTOM_BANNER;
    private static final String CUSTOM_BANNER_TEXT = "im custom";

    private static final String TMP_BANNER_PATH = "/tmp/banner.txt";
    private static final String TMP_BANNER_TEXT = "im tmp";

    private static final String VAR = "VAR";
    private static final String BANNER_TEXT_WITH_VAR = BANNER_TEXT + " ${" + VAR + "!dev}";
    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
    @Rule
    public final TestRule restoreSystemProperties = new RestoreSystemProperties();

    public BannerPrinterTest() {
    }

    @Before
    public void setUp() throws Exception {
        redirectSystemOutStream();
        reloadInitEnvAndProperties();
    }

    private void reloadInitEnvAndProperties() throws Exception {
        Method initEnvAndProperties = BannerPrinter.class.getDeclaredMethod("initEnvAndProperties");
        initEnvAndProperties.setAccessible(true);
        initEnvAndProperties.invoke(null);
    }

    private void redirectSystemOutStream() {
        System.setOut(new PrintStream(System.out) {
            @Override
            public void println(String s) {
                printBuffer.append(s);
            }
        });
    }

    @After
    public void tearDown() {

    }

    private static void printBanner(String path, String text) throws IOException {
        deleteBanner(path);
        try (FileOutputStream fs = new FileOutputStream(path)) {
            fs.write(text.getBytes());
            fs.flush();
        }
        BannerPrinter.print();
    }

    private static void deleteBanner(String path) {
        new File(path).delete();
    }

    @Test
    public void whenIDontHaveABanner_shouldNotPrint() throws IOException {
        deleteBanner(BANNER_PATH);
        BannerPrinter.print();
        assertEquals("", printBuffer.toString());
    }

    @Test
    public void whenIHaveABanner_shouldPrintIt() throws IOException {
        printBanner(BANNER_PATH, BANNER_TEXT);
        assertEquals(BANNER_TEXT, printBuffer.toString());
    }

    @Test
    public void whenIHaveABannerWithVarNotAssigned_shouldPrintWithDefaultValue() throws IOException {
        printBanner(BANNER_PATH, BANNER_TEXT_WITH_VAR);
        assertEquals(BANNER_TEXT + " dev", printBuffer.toString());
    }

    @Test
    public void whenIHaveABannerWithEnvVarAssigned_shouldPrintWithVarValue() throws Exception {
        environmentVariables.set(VAR, "ENV");
        reloadInitEnvAndProperties();
        printBanner(BANNER_PATH, BANNER_TEXT_WITH_VAR);
        assertEquals(BANNER_TEXT + " ENV", printBuffer.toString());
    }

    @Test
    public void whenIHaveABannerWithPropertyAssigned_shouldPrintWithVarValue() throws Exception {
        setProperty(VAR, "SYSTEM_PROP");
        printBanner(BANNER_PATH, BANNER_TEXT_WITH_VAR);
        assertEquals(BANNER_TEXT + " SYSTEM_PROP", printBuffer.toString());
    }

    @Test
    public void whenIHaveABannerWithCustomPath_shouldPrintIt() throws Exception {
        setProperty(BannerPrinter.BANNER_LOCATION_PROPERTY, CUSTOM_BANNER);
        printBanner(CUSTOM_BANNER_PATH, CUSTOM_BANNER_TEXT);
        assertEquals(CUSTOM_BANNER_TEXT, printBuffer.toString());
    }

    @Test
    public void whenIHaveABannerThatIsNotAResource_shouldPrintIt() throws Exception {
        setProperty(BannerPrinter.BANNER_LOCATION_PROPERTY, TMP_BANNER_PATH);
        printBanner(TMP_BANNER_PATH, TMP_BANNER_TEXT);
        assertEquals(TMP_BANNER_TEXT, printBuffer.toString());
    }

    private void setProperty(String key, String value) throws Exception {
        System.setProperty(key, value);
        reloadInitEnvAndProperties();
    }

}
