package PageObjects;

import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Masquerade {
	private WebDriver driver;
	private Properties configurationProp;
	private Properties genericProp;

	public Masquerade(WebDriver driver, Properties configurationProp, Properties genericProp) {
		// TODO Auto-generated constructor stub
		// comment added 2
		this.driver = driver;
		this.configurationProp = configurationProp;
		this.genericProp = genericProp;
	}

	public WebElement accountIcon() {
		return driver.findElement(By.xpath(genericProp.getProperty("account_section")));
	}

	public WebElement usersOption() {
		return driver.findElement(By.xpath(genericProp.getProperty("users_option")));
	}

	public WebElement userListFilterButton() {
		return driver.findElement(By.xpath(genericProp.getProperty("user_list_filter")));
	}

	public WebElement userListFilterSearchfield() {
		return driver.findElement(By.xpath(genericProp.getProperty("search_field")));
	}

	public WebElement userListFilterApplyButtton() {
		return driver.findElement(By.xpath(genericProp.getProperty("apply_button_of_filter")));
	}

	public List<WebElement> userListUserCheckboxes() {
		return driver.findElements(By.xpath(genericProp.getProperty("user_checkboxes")));
	}

	public WebElement kebabIcon() {
		return driver.findElement(By.xpath(genericProp.getProperty("kebab_icon")));
	}

	public WebElement masqueradeOption() {
		return driver.findElement(By.xpath(genericProp.getProperty("masquerade")));
	}

	public WebElement masqueradeFormReasonField() {
		return driver.findElement(By.xpath(genericProp.getProperty("masquerade_reason_field")));
	}

	public WebElement masqueradeFormSignatureField() {
		return driver.findElement(By.xpath(genericProp.getProperty("masquerade_signature_field")));
	}

	public WebElement masqueradeFormGoButton() {
		return driver.findElement(By.xpath(genericProp.getProperty("masquerade_go_button")));
	}

	public WebElement getUser(int userIndex) {
		List<WebElement> userList = driver.findElements(By.xpath(genericProp.getProperty("user_checkboxes")));
		if (userList.size() == 0) {
			return null;
		}

		try {
			return driver.findElements(By.xpath(genericProp.getProperty("user_checkboxes"))).get(userIndex);
		} catch (Exception e) {
			return null;
		}
	}
	
	public WebElement masquerade() {
		return driver.findElement(By.xpath(genericProp.getProperty("masquerade")));
	}

	public WebElement masqueradeReasonField() {
		return driver.findElement(By.xpath(genericProp.getProperty("masquerade_reason_field")));
	}

	public WebElement masqueradeSignatureField() {
		return driver.findElement(By.xpath(genericProp.getProperty("masquerade_signature_field")));
	}

	public WebElement goButton() {
		return driver.findElement(By.xpath(genericProp.getProperty("masquerade_go_button")));
	}

	public WebElement switchBack() {
		return driver.findElement(By.xpath(genericProp.getProperty("switch_back")));
	}

	public WebElement switchBackAgain() {
		return driver.findElement(By.xpath(genericProp.getProperty("switch_back_again")));
	}
	
	

}
