package PageObjects;

import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CozevaPage{
	private WebDriver driver;
	private Properties configuration;
	private Properties genericXpaths;
	
	public CozevaPage(WebDriver driver, Properties configuration, Properties genericXpaths) {
		// TODO Auto-generated constructor stub
		//comment add
		this.driver = driver;
		this.configuration = configuration;
		this.genericXpaths = genericXpaths;
	}
	
	public WebElement appTray() {
		String appTrayXpath = "//a[@data-target='app_dropdown']";
		return driver.findElement(By.xpath(appTrayXpath));
	}
	
	public WebElement supportTools() {
		String supportToolsXpath = "//a[@title='Support Tools']";
		return driver.findElement(By.xpath(supportToolsXpath));
	}
	
	public WebElement caseManagement() {
		String CMXpath = "//div[@id='app_dropdown']//a[@title='Case Management']";
		return driver.findElement(By.xpath(CMXpath));
	}
	
	
	
	public WebElement burgerIcon() {
		String burgerIconXpath = "//a[@data-target='sidenav_slide_out']";
		return driver.findElement(By.xpath(burgerIconXpath));
	}
	
	public WebElement deleteTestingData() {
		String deleteTestingDataXpath = "//a//span[text()='Delete Testing Data']";
		return driver.findElement(By.xpath(deleteTestingDataXpath));
	}
	
	public WebElement masqueradedToCheckbox() {
		String masqueradedToCheckboxXpath = "//input[@id='masquaraded_to']";
		return driver.findElement(By.xpath(masqueradedToCheckboxXpath));
	}
	
	public WebElement autoSuggestionList() {
		String autoSuggestionListXpath = "//ul[@id='ac-dropdown-logged_or_masquaraded_user_name']";
		return driver.findElement(By.xpath(autoSuggestionListXpath));
	}
	
	public WebElement findOptionAtautoSuggestionList(String username) {
		String findOptionXpath = String.format("//ul[@id='ac-dropdown-logged_or_masquaraded_user_name']//div[@name='%s']", username);
		return driver.findElement(By.xpath(findOptionXpath));
	}
	
	public WebElement selectToCleanDropdown() {
		String findOptionXpath = "//input[@class='select-dropdown dropdown-trigger']";
		return driver.findElement(By.xpath(findOptionXpath));
	}
	
	public WebElement CMtaskIDoption() {
		String CMtaskIDoptionXpath = "//input[@class='select-dropdown dropdown-trigger']//following-sibling::ul//li//span[text()='CM Task ID']";
		return driver.findElement(By.xpath(CMtaskIDoptionXpath));
	}
	
	public WebElement enterTaskID() {
		String enterIDXpath = "//input[@id='delete_type_id']";
		return driver.findElement(By.xpath(enterIDXpath));
	}
	
	public WebElement enterCozevaID() {
		String enterCozevaIDXpath = "//input[@id='cozeva_id']";
		return driver.findElement(By.xpath(enterCozevaIDXpath));
	}
	
	public WebElement deleteTestDataButton() {
		String deleteTestDataButtonXpath = "//div[@class='modal-footer']//a[text()='Delete']";
		return driver.findElement(By.xpath(deleteTestDataButtonXpath));
	}
	
	public WebElement submitUsernameField() {
		String submitUsernameFieldXpath = "//input[@id='logged_or_masquaraded_user_name']";
		return driver.findElement(By.xpath(submitUsernameFieldXpath));
	}
	
}
