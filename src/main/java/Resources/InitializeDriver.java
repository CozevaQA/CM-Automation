package Resources;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class InitializeDriver {
	private WebDriver driver;
	private Properties configurationProp;
	private Properties genericProp;
	private Properties cmUserInput;
	
	public InitializeDriver(String configPropertyPath) throws IOException {
		
		FileInputStream file1 = new FileInputStream(configPropertyPath);
		configurationProp = new Properties();
		configurationProp.load(file1);
		
		String xpathPropertyPath = configurationProp.getProperty("genericXpath");
		FileInputStream file2 = new FileInputStream(xpathPropertyPath);
		genericProp = new Properties();
		genericProp.load(file2);
		
		String cmUserInputPropertyPath = configurationProp.getProperty("CMuserInputPropertiesFilePath");
		FileInputStream file3 = new FileInputStream(cmUserInputPropertyPath);
		cmUserInput = new Properties();
		cmUserInput.load(file3);
		
		String chromeDriverPath = configurationProp.getProperty("chromeDriverPath");
		String chromeProfilePath = configurationProp.getProperty("chromeProfilePath");
		
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		ChromeOptions option = new ChromeOptions();
		option.addArguments("user-data-dir=" + chromeProfilePath);
		driver = new ChromeDriver(option);
		
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(20,TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(60,TimeUnit.SECONDS);
//		driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(20));
//		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

	}
	
	public WebDriver getDriver() {
		return driver;
	}

	public Properties getConfigurationProperty() {
		return configurationProp;
	}

	public Properties getGenericProperty() {
		return genericProp;
	}
	
	public Properties getCMUserInputProperty() {
		return cmUserInput;
	}
}
