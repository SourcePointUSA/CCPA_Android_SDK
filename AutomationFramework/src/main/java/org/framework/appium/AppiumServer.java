package org.framework.appium;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

import static org.framework.logger.LoggingManager.logMessage;

import java.io.File;
import java.io.IOException;

public class AppiumServer {

    public static AppiumDriverLocalService appium;

    public static void start() throws IOException {
        AppiumServiceBuilder builder = new AppiumServiceBuilder()
                .withAppiumJS(new File("/usr/local/lib/node_modules/appium/build/lib/main.js"))
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE);
        appium = builder.build();
        appium.start();
        logMessage("Appium server has been started");
    }

    public static void stop() throws IOException {
        appium.stop();
        logMessage("Appium server has been stopped");
    }
}

