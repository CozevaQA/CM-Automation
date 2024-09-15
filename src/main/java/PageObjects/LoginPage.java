package PageObjects;

import java.util.Properties;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage {
	private WebDriver driver;
	private Properties configurationProp;
	private Properties genericProp;
	
	public LoginPage(WebDriver driver, Properties configurationProp, Properties genericProp) {
		// TODO Auto-generated constructor stub
		//comment added 2
		this.driver = driver;
		this.configurationProp = configurationProp;
		this.genericProp = genericProp;
	}
	
	
	public WebElement username() {
		return driver.findElement(By.xpath(genericProp.getProperty("edit_name")));
	}
	
	public WebElement password() {
		return driver.findElement(By.xpath(genericProp.getProperty("edit_pass")));
	}
	
	public WebElement corpLoginbutton() {
		return driver.findElement(By.xpath(genericProp.getProperty("login_button")));
	}
	
	public WebElement loginbutton() {
		return driver.findElement(By.xpath(genericProp.getProperty("edit_submit")));
	}
	
	public WebElement submitButton() {
		return driver.findElement(By.xpath(genericProp.getProperty("edit_submit")));
	}
	
	public WebElement reason() {
		return driver.findElement(By.xpath(genericProp.getProperty("login_reason")));
	}
	
	public WebElement ajaxPreloader() {
		return driver.findElement(By.xpath(genericProp.getProperty("ajax_preloader")));
	}
}
