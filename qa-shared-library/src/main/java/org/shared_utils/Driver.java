package org.shared_utils;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class Driver {

    private Driver() {
    }

    public static WebDriver driver;

    public static WebDriver getDriver() {

        if (driver == null) {

            String browserType = ConfigurationReader.getProperty("browser");
            Map<String, String> prefs = new HashMap<>();
            String downloadLocation = System.getProperty("user.dir") + "\\Downloads";
            prefs.put("download.default_directory", downloadLocation);
            ChromeOptions options;

            switch (browserType) {
                case "chrome":
                    System.out.println("----------Chrome driver started----------");
                    //WebDriverManager.chromedriver().clearDriverCache().forceDownload().setup();
                    options = new ChromeOptions();
                    options.setExperimentalOption("prefs", prefs);
                    options.addArguments("--remote-allow-origins=*");
                    driver = new ChromeDriver(options);
                    break;

                case "headless":
                    System.out.println("----------Headless driver started----------");
                    //WebDriverManager.chromedriver().setup();
                    options = new ChromeOptions();
                    options.setExperimentalOption("prefs", prefs);
                    options.addArguments("--window-size=1280,800");
                    options.addArguments("--headless", "--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage"); //Overcome limited resource problem
                    options.addArguments("--disable-gpu");
                    options.addArguments("--ignore-certificate-errors");
                    options.addArguments("--allow-insecure-localhost");
                    driver = new ChromeDriver(options);
                    break;
            }
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            //How to get Session ID for debugging
            //System.out.println("sessionID = " + ((ChromeDriver) driver).getSessionId().toString());
        }
        return driver;
    }

    public static void closeDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
            System.out.println("----------Driver closed----------");
        }
    }
}
