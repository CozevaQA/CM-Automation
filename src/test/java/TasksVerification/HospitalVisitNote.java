package TasksVerification;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import PageObjects.Tasks.HospitalVisitNote_PageObject;
import PageObjects.Tasks.carePlan;
import Resources.Excel;

public class HospitalVisitNote {
	private WebDriver driver;
	private Logger logger;
	private Properties carePlanProp;
	private HospitalVisitNote_PageObject hospitalVisitNotePageObject;
	private WebDriverWait wait;
	private HashMap<String, Object> storedInfo;
	private List<Object> HVN_Testcase;
	private Integer testCaseID;

	public HospitalVisitNote(WebDriver driver, List<Object> HVN_Testcase, Logger logger) throws IOException {
		this.driver = driver;
		this.logger = logger;
		this.HVN_Testcase = HVN_Testcase;
		testCaseID = 1;

		String configPropertyPath = System.getProperty("user.dir") + File.separator
				+ "src\\main\\java\\TestData\\Care Plan.properties";

		FileInputStream file1 = new FileInputStream(configPropertyPath);
		carePlanProp = new Properties();
		carePlanProp.load(file1);
		storedInfo = new HashMap<>();
		hospitalVisitNotePageObject = new HospitalVisitNote_PageObject(driver, logger);
		wait = new WebDriverWait(driver, 60);
	}

	public void startHospitalVisitNoteVerification() throws Exception {
		System.out.println("Starting Hospital Visit Note verification...");

		Integer requiredNoOfEncounters = 3;
		Integer currentNoOfEncounters = hospitalVisitNotePageObject.getCurrentNoOfEncounters();

		if (requiredNoOfEncounters > currentNoOfEncounters) {
			int moreEncounterFieldsNeeded = requiredNoOfEncounters - currentNoOfEncounters;
			for (int i = 0; i < moreEncounterFieldsNeeded; i++) {
				hospitalVisitNotePageObject.encounterPlusIcon().click();
				System.out.println("encounterPlusIcon clicked");
				Thread.sleep(200);
			}
			hospitalVisitNotePageObject.clickOnEncounterText();
			Thread.sleep(1000);
			hospitalVisitNotePageObject.taskSaveButton();
			Thread.sleep(200);
		}

		// Setting data at encounter fields
		List<WebElement> activeEncounterRows = hospitalVisitNotePageObject.getActiveEncounterRows();
		if (activeEncounterRows.size() == requiredNoOfEncounters) {
			List<Object> allEncounterData = new ArrayList<>();
			for (int i = 0; i < requiredNoOfEncounters; i++) {
				HashMap<String, String> encounterData = new HashMap<>();

				hospitalVisitNotePageObject.setEncounterType(i, "Referral Note");
				encounterData.put("Encounter Type", "Referral Note");
				hospitalVisitNotePageObject.setEncounterDuration(i, "25 min");
				encounterData.put("Duration", "25 min");
				hospitalVisitNotePageObject.setEncounterWithWhom(i, "Colleague");
				encounterData.put("With Whom", "Colleague");
				hospitalVisitNotePageObject.setEncounterNote(i,
						"This encounter note is for testing purpose, please ignore it.");
				encounterData.put("Notes", "This encounter note is for testing purpose, please ignore it.");
				System.out.println("Encounter data filled");
				allEncounterData.add(encounterData);
				Thread.sleep(200);
			}
			storedInfo.put("Encounter", allEncounterData);
		}

		// Setting data at checklist items
		HashMap<String, String> checklistData = new HashMap<>();
		hospitalVisitNotePageObject.clickOnChecklistText();
		Thread.sleep(1000);
		WebElement activeChecklist = hospitalVisitNotePageObject.getActiveChecklist();
		List<WebElement> activeChecklistLabels = hospitalVisitNotePageObject.getActiveChecklistLabels(activeChecklist);
		int randomCheckListIndex = (int) (Math.random() * activeChecklistLabels.size());
		WebElement randomChecklistPicked = activeChecklistLabels.get(randomCheckListIndex);
		randomChecklistPicked.click();
		checklistData.put("ChecklistSelected", randomChecklistPicked.getText().toString());
		Thread.sleep(200);
		storedInfo.put("Checklist", checklistData);

		System.out.println("Selected checklist option: " + randomChecklistPicked.getText().toString());

		// Setting data at Internal Referrals items
		HashMap<String, String> internalReferralsData = new HashMap<>();
		hospitalVisitNotePageObject.clickOnInternalReferralsText();
		Thread.sleep(1000);
		WebElement internalReferrals = hospitalVisitNotePageObject.getInternalReferrals();
		List<WebElement> internalReferralsRadioButtons = hospitalVisitNotePageObject
				.getInternalReferralsRadioButtons(internalReferrals);
		int randomRadioButtonIndex = (int) (Math.random() * internalReferralsRadioButtons.size());
		WebElement randomRadioButtonPicked = internalReferralsRadioButtons.get(randomRadioButtonIndex);
		randomRadioButtonPicked.click();
		internalReferralsData.put("selectedRadioButton", randomRadioButtonPicked.getText().toString());
		Thread.sleep(200);
		storedInfo.put("internalReferrals", internalReferralsData);

		System.out.println("Selected radio button option: " + randomRadioButtonPicked.getText().toString());
		hospitalVisitNotePageObject.taskSaveButton();
		Thread.sleep(1000);

		// Completing and reopening the task
		hospitalVisitNotePageObject.threeDot().click();
		Thread.sleep(200);
		hospitalVisitNotePageObject.threeDotCompleteOption().click();
		Thread.sleep(200);
		hospitalVisitNotePageObject.yesOptionOnPrompt().click();
		Thread.sleep(200);
		hospitalVisitNotePageObject.waitForTaskPreloader();
		hospitalVisitNotePageObject.threeDot().click();
		Thread.sleep(200);
		hospitalVisitNotePageObject.threeDotReopenOption().click();
		Thread.sleep(200);
		hospitalVisitNotePageObject.reasonForReopen().sendKeys("testing purpose...");
		Thread.sleep(200);
		hospitalVisitNotePageObject.yesOptionOnPrompt().click();
		hospitalVisitNotePageObject.waitForTaskPreloader();
		Thread.sleep(200);
		hospitalVisitNotePageObject.taskSaveButton();

		Map<String, Object> allSavedData = hospitalVisitNotePageObject.getAllSavedData();

		if (hospitalVisitNotePageObject.matchData(storedInfo, allSavedData)) {
			System.out.println("All data matched successfully");
			List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
					"To verify if submitted task data entact after completing and reopening task.", "Task data should be same after reopening", "PASSED"));
			HVN_Testcase.add(testList);
		} else {
			System.out.println("Data does not matched");
			List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
					"To verify if submitted task data entact after completing and reopening task.", "Task data should be same after reopening", "FAILED"));
			HVN_Testcase.add(testList);
		}

		// Cancelling and reopening the task
		hospitalVisitNotePageObject.threeDot().click();
		Thread.sleep(200);
		hospitalVisitNotePageObject.threeDotCancelOption().click();
		Thread.sleep(200);
		hospitalVisitNotePageObject.yesOptionOnPrompt().click();
		Thread.sleep(200);
		hospitalVisitNotePageObject.waitForTaskPreloader();
		hospitalVisitNotePageObject.threeDot().click();
		Thread.sleep(200);
		hospitalVisitNotePageObject.threeDotReopenOption().click();
		Thread.sleep(200);
		hospitalVisitNotePageObject.reasonForReopen().sendKeys("testing purpose...");
		Thread.sleep(200);
		hospitalVisitNotePageObject.yesOptionOnPrompt().click();
		hospitalVisitNotePageObject.waitForTaskPreloader();
		Thread.sleep(200);
		hospitalVisitNotePageObject.taskSaveButton();

		allSavedData = hospitalVisitNotePageObject.getAllSavedData();

		if (hospitalVisitNotePageObject.matchData(storedInfo, allSavedData)) {
			List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
					"To verify if submitted task data entact after Cancelling and reopening task.", "Task data should be same after reopening", "PASSED"));
			HVN_Testcase.add(testList);
			System.out.println("All data matched successfully");
		} else {
			System.out.println("Data does not matched");
			List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
					"To verify if submitted task data entact after Cancelling and reopening task.", "Task data should be same after reopening", "FAILED"));
			HVN_Testcase.add(testList);
		}

		System.out.println(storedInfo);
		System.out.println(allSavedData);
	}
	
	public List<Object> getReport(){
		return this.HVN_Testcase;
	}

}
