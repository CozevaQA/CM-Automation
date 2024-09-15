package TasksVerification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import PageObjects.Tasks.carePlan;
import Resources.Excel;

public class CarePlan {
	private WebDriver driver;
	private Excel excel;
	private Logger logger;
	private Properties carePlanProp;
	private carePlan carePlanPageObject;
	private WebDriverWait wait;
	
	public CarePlan(WebDriver driver, Excel excel, Logger logger) throws IOException {
		this.driver = driver;
		this.excel = excel;
		this.logger = logger;
		
		String configPropertyPath = System.getProperty("user.dir") + File.separator
				+ "src\\main\\java\\TestData\\Care Plan.properties";
		
		FileInputStream file1 = new FileInputStream(configPropertyPath);
		carePlanProp = new Properties();
		carePlanProp.load(file1);
		carePlanPageObject = new carePlan(driver, excel, logger);
		wait = new WebDriverWait(driver, 60);
	}
	
	public void startCarePlanVerification() throws Exception {
		System.out.println("Starting care plan verification...");
		Integer requiredNoOfProblems = Integer.parseInt(carePlanProp.getProperty("NoOfProblems"));
		
		Integer currentNoOfProblems = carePlanPageObject.getCurrentNoOfProblems();
		
		if(requiredNoOfProblems > currentNoOfProblems) {
			Integer moreCountRequired = requiredNoOfProblems-currentNoOfProblems;
			
			for(int i=0; i<moreCountRequired; i++) {
//				carePlanPageObject.problemsPlusIcon().click();
				carePlanPageObject.clickOnProblemsPlusIcon();
				Thread.sleep(200);
			}
		}
		
		
		try {
			carePlanPageObject.problemsText().click();
			Thread.sleep(1000);
		}
		catch(ElementClickInterceptedException intercepted) {
			carePlanPageObject.clickOnProblemsText();
			Thread.sleep(1000);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		carePlanPageObject.taskSaveButton().click();
		Thread.sleep(1000);
		
		List<WebElement> allActiveProblemsRowElements = carePlanPageObject.getAllActiveProblemsRowElements();
		
		for(WebElement activeProblem: allActiveProblemsRowElements) {
//			carePlanPageObject.getEditProblemNameField(activeProblem).sendKeys("m");
//			carePlanPageObject.getEditProblemNameField(activeProblem);
			WebElement editProblemNameField = carePlanPageObject.getEditProblemNameField(activeProblem);
			JavascriptExecutor executor = (JavascriptExecutor)driver;
			executor.executeScript("arguments[0].click();", editProblemNameField);
			Actions action = new Actions(driver);
			action.moveToElement(editProblemNameField).click();
//			editProblemNameField.click();
//			wait.until(ExpectedConditions.visibilityOf(editProblemNameField));
//			editProblemNameField.sendKeys("m");
			
			JavascriptExecutor jse = ((JavascriptExecutor)driver);
			jse.executeScript("arguments[0].value='m';", editProblemNameField);
			
			String string = carePlanPageObject.getEditProblemNameField(activeProblem).getAttribute("data-drupal-selector").toString();
			System.out.println(string);
			carePlanPageObject.waitForProblemsAutosuggestionBox();
			List<WebElement> allAutosuggestions = carePlanPageObject.getAllAutosuggestions();
			
			int noOfAutosuggestions = allAutosuggestions.size();
			int randomIndex = (int) (Math.random()*noOfAutosuggestions);
			
			WebElement randomAutoSuggestion = allAutosuggestions.get(randomIndex);
			randomAutoSuggestion.click();
			
			System.out.println("Random suggestion picked: "+carePlanPageObject.getAutoSuggestionName(randomAutoSuggestion));
			
		}
		
		
	}
	
	
}
