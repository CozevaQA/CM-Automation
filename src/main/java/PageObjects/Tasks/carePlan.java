package PageObjects.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import Resources.Excel;

public class carePlan {
	
	private WebDriver driver;
	private Excel excel;
	private Logger logger;
	private WebDriverWait wait;
	
	public carePlan(WebDriver driver, Excel excel, Logger logger) {
		// TODO Auto-generated constructor stub
		// comment added 2
		this.driver = driver;
		this.excel = excel;
		this.logger = logger;
		wait = new WebDriverWait(driver, 40);
	}
	
	public Integer getCurrentNoOfProblems() {
		String problemCountXpath ="//div[@id='generic_form_scrollspy_wrapper']//span[text()='Assessment']//parent::li//span[text()='Problems']//parent::span[@section_id='assessment_wrapper']//span[@class='count-indicator']";
		WebElement problemsCountElem = driver.findElement(By.xpath(problemCountXpath));
		Integer problemsCount  = Integer.parseInt(problemsCountElem.getAttribute("data-row-count").toString());
		return problemsCount;
	}
	
	public WebElement problemsPlusIcon() {
		String problemsXpath = "//div[@id='generic_form_scrollspy_wrapper']//span[text()='Assessment']//parent::li//span[text()='Problems']//parent::span[@section_id='assessment_wrapper']//span";
		WebElement problemsPlusIcon = driver.findElements(By.xpath(problemsXpath)).get(0);
		return problemsPlusIcon;
	}
	
	public void clickOnProblemsPlusIcon() {
		String problemsXpath = "//div[@id='generic_form_scrollspy_wrapper']//span[text()='Assessment']//parent::li//span[text()='Problems']//parent::span[@section_id='assessment_wrapper']//span";
		WebElement problemsPlusIcon = driver.findElements(By.xpath(problemsXpath)).get(0);
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		executor.executeScript("arguments[0].click();", problemsPlusIcon);
		//driver.findElement(By.xpath(problemsTextXpath)).click();
	}
	
	public WebElement problemsText() {
		String problemsXpath = "//div[@id='generic_form_scrollspy_wrapper']//span[text()='Assessment']//parent::li//span[text()='Problems']//parent::span[@section_id='assessment_wrapper']//span";
		WebElement problemsText = driver.findElements(By.xpath(problemsXpath)).get(1);
		return problemsText;
	}
	
	public void clickOnProblemsText() {
		String problemsXpath = "//div[@id='generic_form_scrollspy_wrapper']//span[text()='Assessment']//parent::li//span[text()='Problems']//parent::span[@section_id='assessment_wrapper']//span";
		WebElement problemsText = driver.findElements(By.xpath(problemsXpath)).get(1);
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		executor.executeScript("arguments[0].click();", problemsText);
	}
	
	public void clickOnProblems() {
		String problemsTextXpath = "//div[@id='generic_form_scrollspy_wrapper']//span[text()='Assessment']//parent::li//span[text()='Problems']";
		//driver.findElements(By.xpath(problemsXpath)).get(1).click();
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		executor.executeScript("arguments[0].click();", driver.findElement(By.xpath(problemsTextXpath)));
		//driver.findElement(By.xpath(problemsTextXpath)).click();
	}
	
	public WebElement taskSaveButton() {
		String saveButtonXpath = "//span[@data-badge-caption='Save']//i";
		return driver.findElement(By.xpath(saveButtonXpath));
	}
	
	
	
	public List<WebElement> getAllActiveProblemsRowElements() {
		String activeProblemRowXpath = "//table[@id='patient_a_p_table']//tbody//tr[not(@class='notShown exclude_option')][not(@class='add_row hide')]";
		List<WebElement> activeProblemRowElements = driver.findElements(By.xpath(activeProblemRowXpath));
		
		return activeProblemRowElements;
	}
	
	public WebElement getEditProblemNameField(WebElement parentProblemRow) {
		String editProblemFieldXpath = "//div[@id='problem_name_div']//input[contains(@id, 'edit-problem_name')]";
		WebElement editProblemField = parentProblemRow.findElement(By.xpath(editProblemFieldXpath));
		return editProblemField;
	}
	
	public boolean waitForProblemsAutosuggestionBox() {
		String autoSuggestionBoxXpath = "//ul[contains(@id, 'ac-dropdown-edit-problem_name')]";
		
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(autoSuggestionBoxXpath)));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public List<WebElement> getAllAutosuggestions(){
		String autoSuggestionsXpath = "//ul[contains(@id, 'ac-dropdown-edit-problem_name')]//li//div";
		List<WebElement> autoSuggestions = driver.findElements(By.xpath(autoSuggestionsXpath));
		return autoSuggestions;
	}
	
	public String getAutoSuggestionName(WebElement autoSuggestion){
		String autoSuggestionName = autoSuggestion.getAttribute("name").toString();
		return autoSuggestionName;
	}
	
	
	public String setProblemData(WebElement problemRow, String autoSearch) throws InterruptedException {
		String problemInputFieldXpath = "//input[contains(@id, 'edit-problem_name')]";
		String autoSugesstionBoxXpath = "//ul[contains(@id,'ac-dropdown-edit-problem_name')]";
		WebElement problemInputField = problemRow.findElement(By.xpath(problemInputFieldXpath));
		Thread.sleep(1000);
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		executor.executeScript("arguments[0].click();", problemInputField);
		Thread.sleep(200);
		problemInputField.sendKeys(autoSearch);
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(autoSugesstionBoxXpath)));
		Thread.sleep(200);
		List<WebElement> autosuggestions = problemRow.findElements(By.xpath(autoSugesstionBoxXpath+"//li//div"));
		int noOfSuggestions = autosuggestions.size();
		
		Integer randomIndex = (int) (Math.random() * noOfSuggestions);
		
		WebElement randomSuggestion = autosuggestions.get(randomIndex);
		randomSuggestion.click();
		
		return randomSuggestion.getAttribute("id").toString();
	}
}
