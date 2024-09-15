package Resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.KeyEvent;

import PageObjects.LoginPage;

public class GenericFunctions {
	private Properties config;
	private Properties xpaths;
	private Properties cmUserInput;
	private WebDriver driver;
	private WebDriverWait wait;

	public GenericFunctions() throws IOException {
		super();
		// TODO Auto-generated constructor stub

		String configPath = System.getProperty("configFilePath");
		// System.out.println(configPath);
		FileInputStream configFile = new FileInputStream(configPath);
		config = new Properties();
		config.load(configFile);

		FileInputStream xpathFile = new FileInputStream(
				System.getProperty("user.dir") + File.separator + "src\\main\\java\\Xpaths\\genericXpaths.properties");
		xpaths = new Properties();
		xpaths.load(xpathFile);

		String cmUserInputFilePath = System.getProperty("user.dir") + File.separator
				+ "src\\main\\java\\TestData\\CM userInput.properties";
		FileInputStream cmUserInputFile = new FileInputStream(cmUserInputFilePath);
		cmUserInput = new Properties();
		cmUserInput.load(cmUserInputFile);
	}

	public WebDriver initializeChromeDriver() {
		String chromeDriverPath = config.getProperty("chromeDriverPath");
		String chromeProfilePath = config.getProperty("chromeProfilePath");

		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		ChromeOptions option = new ChromeOptions();
		option.addArguments("user-data-dir=" + chromeProfilePath);
		driver = new ChromeDriver(option);

		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		// driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(20));
		// driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

		this.driver = driver;
		return driver;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public Properties getConfigurationProperty() {
		return config;
	}

	public Properties getGenericXpathProperty() {
		return xpaths;
	}

	public Properties getCMUserInputProperty() {
		return cmUserInput;
	}

	public void loginToCozeva() {

		driver.get(config.getProperty("login_URL").trim());
//		driver.findElement(By.xpath(xpaths.getProperty("login"))).click();
		driver.findElement(By.xpath(xpaths.getProperty("edit_name")))
				.sendKeys(new String(Base64.getDecoder().decode(config.getProperty("username"))));
		driver.findElement(By.xpath(xpaths.getProperty("edit_pass")))
				.sendKeys(new String(Base64.getDecoder().decode(config.getProperty("password"))));
		driver.findElement(By.xpath(xpaths.getProperty("edit_submit"))).click();

		wait = new WebDriverWait(driver, 60);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpaths.getProperty("login_reason"))));

		driver.findElement(By.xpath(xpaths.getProperty("login_reason"))).sendKeys(config.getProperty("RM_no"));
		driver.findElement(By.xpath(xpaths.getProperty("edit_submit"))).click();
		ajaxPreloaderWait();
	}

	public void ajaxPreloaderWait() {
		try {

			wait = new WebDriverWait(driver, 120);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpaths.getProperty("ajax_preloader"))));

//			WebElement loader_elem = driver.findElement(By.xpath(xpaths.getProperty("ajax_preloader")));
//			wait = new WebDriverWait(driver,60);
//			wait.until(ExpectedConditions.invisibilityOf(loader_elem));
		} catch (Exception noElem) {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void ajaxPreloaderWaitNotForMobile() {
		try {

			wait = new WebDriverWait(driver, 300);
			wait.until(ExpectedConditions
					.invisibilityOfElementLocated(By.xpath("//div[@class='ajax_preloader not_for_mobile']")));
		} catch (Exception noElem) {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void jsClickOnElement(WebElement elem) {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", elem);
	}

	public void programPageLoaderWait() throws InterruptedException {
		Thread.sleep(500);
		try {
			WebDriverWait wait = new WebDriverWait(driver, 60);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@id='tr_ajax_loader']")));
			Thread.sleep(500);
		} catch (NoSuchElementException noElem) {
			Thread.sleep(1500);
		}
	}
	
	public void programPage2ndLoaderWait() throws InterruptedException {
		Thread.sleep(100);
		try {
			WebElement loader_elem = driver.findElement(By.xpath("//div[@id='cm_task_registry_processing']//div[@class='ajax_preloader']"));
			WebDriverWait wait = new WebDriverWait(driver,60);
			wait.until(ExpectedConditions.invisibilityOf(loader_elem));
			Thread.sleep(500);
		}
		catch(NoSuchElementException noElem) {
			Thread.sleep(1500);
		}
	}
	
	
	
	public String getCustomer(String contextID) throws IOException, InterruptedException {
		String baseURL = config.getProperty("login_URL").toString().trim().split("com")[0]+"com";
		String custContext = String.format("//li[contains(@id,'context_id')][contains(@id,'%s')][contains(@class,'context_cust_no_parent_grandparent')]", contextID+"_"+contextID+"_"+contextID);
		//WebElement custElem=drv.findElement(By.id(contextID));
		String custName;
		try {
			WebElement custElem=driver.findElement(By.xpath(custContext));
			String getLink = baseURL + custElem.getAttribute("redirect_link");
			custName=custElem.findElement(By.xpath(".//a")).getAttribute("title");
			driver.get(getLink);
		}
		catch(Exception e) {
			Thread.sleep(1500);
			WebElement custElem=driver.findElement(By.xpath(custContext));
			String getLink = baseURL + custElem.getAttribute("redirect_link");
			custName=custElem.findElement(By.xpath(".//a")).getAttribute("title");
			driver.get(getLink);
			
		}
		
		return custName;
	}

	public boolean removeAnnouncement() {
		wait = new WebDriverWait(driver, 3);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='announcement_list']")));
			driver.findElement(By.xpath("//div[@class='banner_footer']//a[text()='Hide']")).click();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private boolean skipEULAPage() {
		wait = new WebDriverWait(driver, 3);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='cf-user-agreement-form']")));
			driver.findElement(By.xpath("//form[@id='cf-user-agreement-form']//div[@id='agreement-buttons']//button[@id='edit-later']")).click();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean openUserlistAndMasqueradeToUsername(String userName) throws InterruptedException {
		
		if(userName.trim().equals("")) {
			return false;
		}
		
		driver.findElement(By.xpath(xpaths.getProperty("account_section"))).click();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			driver.findElement(By.xpath(xpaths.getProperty("users_option"))).click();
		} catch (ElementClickInterceptedException e) {
			Thread.sleep(2000);
			driver.findElement(By.xpath(xpaths.getProperty("users_option"))).click();
		}
		
		ajaxPreloaderWait();
		driver.findElement(By.xpath(xpaths.getProperty("user_list_filter"))).click();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		driver.findElement(By.xpath(xpaths.getProperty("search_field"))).sendKeys(userName);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		driver.findElement(By.xpath(xpaths.getProperty("apply_button_of_filter"))).click();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ajaxPreloaderWait();

		List<WebElement> userList = driver.findElements(By.xpath(xpaths.getProperty("user_checkboxes")));
		if (userList.size() == 0) {
			return false;
		}

		jsClickOnElement(userList.get(0));
		driver.findElement(By.xpath(xpaths.getProperty("kebab_icon"))).click();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		driver.findElement(By.xpath(xpaths.getProperty("masquerade"))).click();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		driver.findElement(By.xpath(xpaths.getProperty("masquerade_reason_field")))
				.sendKeys(config.getProperty("RM_no"));
		driver.findElement(By.xpath(xpaths.getProperty("masquerade_signature_field")))
				.sendKeys(config.getProperty("signature"));
		driver.findElement(By.xpath(xpaths.getProperty("masquerade_go_button"))).click();
		skipEULAPage();
		wait = new WebDriverWait(driver, 20);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[text()='Switch Back']")));
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public boolean masqueradeToUsername(String userName) {
		driver.findElement(By.xpath(xpaths.getProperty("user_list_filter"))).click();
		driver.findElement(By.xpath(xpaths.getProperty("search_field"))).sendKeys(userName);
		driver.findElement(By.xpath(xpaths.getProperty("apply_button_of_filter"))).click();
		ajaxPreloaderWait();

		List<WebElement> userList = driver.findElements(By.xpath(xpaths.getProperty("user_checkboxes")));
		if (userList.size() == 0) {
			return false;
		}

		jsClickOnElement(userList.get(0));
		driver.findElement(By.xpath(xpaths.getProperty("kebab_icon"))).click();
		driver.findElement(By.xpath(xpaths.getProperty("masquerade"))).click();
		driver.findElement(By.xpath(xpaths.getProperty("masquerade_reason_field")))
				.sendKeys(config.getProperty("RM_no"));
		driver.findElement(By.xpath(xpaths.getProperty("masquerade_signature_field")))
				.sendKeys(config.getProperty("signature"));
		driver.findElement(By.xpath(xpaths.getProperty("masquerade_go_button"))).click();

		wait = new WebDriverWait(driver, 10);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[text()='Switch Back']")));
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public WebDriver prepareDriver() throws IOException {
		String chromeDriver = config.getProperty("chromeDriverPath");
		String chromeProfile = config.getProperty("chromeProfilePath");

		System.setProperty("webdriver.chrome.driver", chromeDriver);
		ChromeOptions option = new ChromeOptions();
		option.addArguments("user-data-dir=" + chromeProfile);
		WebDriver driver = new ChromeDriver(option);
		// WebDriver driver = new ChromeDriver();

		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
//		driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(20));
		driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);

		return driver;
	}

	public void switchTab(int tab_no) {
		try {
			ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());

			driver.switchTo().window(tabs2.get(tab_no - 1));
			// driver.close();
			// driver.switchTo().window(tabs2.get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void duplicateCurrentTab() {
		try {
			((JavascriptExecutor) driver).executeScript("window.open(window.location.href, '_blank');");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean waitForElementToAppear(String elementXpath, Integer waitForSeconds) {
		wait = new WebDriverWait(driver, waitForSeconds);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(elementXpath)));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean waitForElementToDisappear(String elementXpath, Integer waitForSeconds) {
		wait = new WebDriverWait(driver, waitForSeconds);
		try {
			wait.until(ExpectedConditions.invisibilityOf(driver.findElement(By.xpath(elementXpath))));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void invisibilityWait(WebDriver driver) throws InterruptedException {
		Thread.sleep(500);
		try {
			WebElement loader_elem = driver.findElement(By.xpath(xpaths.getProperty("ajax_loader")));
			WebDriverWait wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.invisibilityOf(loader_elem));
			Thread.sleep(500);
		} catch (NoSuchElementException noElem) {
			Thread.sleep(1500);
		}
	}

	public void ajaxPreloaderWait(WebDriver driver) throws InterruptedException {
		Thread.sleep(500);
		try {
			WebElement loader_elem = driver.findElement(By.xpath(xpaths.getProperty("ajax_preloader")));
			WebDriverWait wait = new WebDriverWait(driver, 60);
			wait.until(ExpectedConditions.invisibilityOf(loader_elem));
			Thread.sleep(500);
		} catch (NoSuchElementException noElem) {
			Thread.sleep(1500);
		}
	}

	public void ajaxPreloaderModalWait() throws InterruptedException {
		Thread.sleep(100);
		try {
			WebElement loader_elem = driver.findElement(By.xpath("//div[@id='myAjaxModal']//div[@class='ajaxloader']"));
			WebDriverWait wait = new WebDriverWait(driver, 60);
			wait.until(ExpectedConditions.invisibilityOf(loader_elem));
			Thread.sleep(500);
		} catch (NoSuchElementException noElem) {
			Thread.sleep(1500);
		}
	}

	public void ajaxPreloaderModalWait2() throws InterruptedException {
		Thread.sleep(100);
		try {
			WebElement loader_elem = driver
					.findElement(By.xpath("//div[@class='modal cozeva-prompt open in-progress']"));
			WebDriverWait wait = new WebDriverWait(driver, 300);
			wait.until(ExpectedConditions.invisibilityOf(loader_elem));
			Thread.sleep(100);
		} catch (NoSuchElementException noElem) {
			Thread.sleep(1000);
		}
	}

	public String navigateToCustomerByCustomerID(String contextID) throws IOException, InterruptedException {
		// String baseURL = "https://www.cozeva.com/";
		String baseURL = config.getProperty("login_URL").split("user")[0];
		String custContext = String.format("//li[@id='context_id_%s']", contextID + "_" + contextID + "_" + contextID);
		// WebElement custElem=drv.findElement(By.id(contextID));
		String custName;
		try {
			WebElement custElem = driver.findElement(By.xpath(custContext));
			String getLink = baseURL + custElem.getAttribute("redirect_link");
			custName = custElem.findElement(By.xpath(".//a")).getAttribute("title");
			driver.get(getLink);
		} catch (Exception e) {
			Thread.sleep(1500);
			WebElement custElem = driver.findElement(By.xpath(custContext));
			String getLink = baseURL + custElem.getAttribute("redirect_link");
			custName = custElem.findElement(By.xpath(".//a")).getAttribute("title");
			driver.get(getLink);

		}

		return custName;
	}

	public void wait_to_load(WebDriver driver) {
		String loader_element_classname = "sm_download_cssload_loader_wrap";
		@SuppressWarnings("deprecation")
		WebDriverWait w = new WebDriverWait(driver, 100);
		w.until(ExpectedConditions.invisibilityOfElementLocated(By.className(loader_element_classname)));
	}
}
