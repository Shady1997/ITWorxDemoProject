package test_cases;

import java.awt.AWTException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import pages.HomePage;
import pages.PageBase;

public class TestBase {
	// npm install -g newman - newman run <collection name> --data <file name> -n
	// <no of iterations> -d <delay time> -e <environment name>

	// define main properties
	public static WebDriver driver;
	FileInputStream readProperty;
	public static Properties prop;
	public static ChromeOptions options;
	DesiredCapabilities handlSSLErr = DesiredCapabilities.chrome();
//	handlSSLErr.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
	JavascriptExecutor js;
	HomePage loginPage;

	@Parameters("browser")
	@BeforeTest
	public void prepareClassProperties(String browser) throws IOException, AWTException {
		readProperty = new FileInputStream(
				System.getProperty("user.dir") + "\\src\\test\\resources\\properties\\generalProperties.properties");
		prop = new Properties();
		prop.load(readProperty);
		options = new ChromeOptions();
		options.addArguments("--start-maximized");
		options.addArguments("--disable-web-security");
		options.addArguments("--no-proxy-server");
//		options.setAcceptInsecureCerts(true);
//		options.addArguments("--user-data-dir=C:\\Users\\G525585\\AppData\\Local\\Google\\Chrome\\User Data");
//		options.addArguments("--enable-automation");
//		options.addArguments("--no-sandbox");
//		options.addArguments("--disable-gpu");
		options.addArguments("--disable-blink-features=AutomationControlled");

		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("credentials_enable_service", false);
		prefs.put("profile.password_manager_enabled", false);

		options.setExperimentalOption("prefs", prefs);
		options.setExperimentalOption("excludeSwitches", new String[] { "enable-automation" });

		if (browser.equalsIgnoreCase("Firefox")) {
			System.setProperty("webdriver.gecko.driver",
					System.getProperty("user.dir") + prop.getProperty("firefoxdriver"));
			driver = new FirefoxDriver();
		} else if (browser.equalsIgnoreCase("Chrome")) {
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + prop.getProperty("chromedriver"));
			driver = new ChromeDriver(options);
		} else {
			throw new IllegalArgumentException("Invalid browser value!!");
			// Change thread count 1 for sequential , 2 for parallel 3 ..browser..
		}

		js = (JavascriptExecutor) driver;
		loginPage = new HomePage(driver);
	}

	@Test(priority = 1, groups = "smoke", description = "Start ITWorx Web Application")
	private void startApplication() throws InterruptedException {
		// Mazimize current window
		driver.manage().window().maximize();
		// navigate to website
		driver.get("https://www.itworx.com./");
		Thread.sleep(20000);
		// take screenshot to Home page
		PageBase.captureScreenshot(driver, "HomePage");
	}

	@AfterTest
	public void tearDown() {
		driver.quit();
//		String report = System.getProperty("user.dir") + "\\test-output\\index.html";
//		driver.get(report);
	}

	public static void getScreenshotOnFailure() {
		PageBase.captureScreenshot(driver, "fail" + java.time.LocalTime.now().toString());
	}

}
