package PageObjects.Tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import Resources.Excel;

public class HospitalVisitNote_PageObject {

	private WebDriver driver;
	private Logger logger;
	private WebDriverWait wait;
	
	public HospitalVisitNote_PageObject(WebDriver driver, Logger logger) {
		// TODO Auto-generated constructor stub
		// comment added 2
		this.driver = driver;
		this.logger = logger;
		
		wait = new WebDriverWait(driver, 40);
	}
	
	public Integer getTotalNoOfSectionsAvailable() {
		String xpath = "//div[@id='generic_form_scrollspy_wrapper']//ul[@data-scroll-collapse-id='task_collapse']//ul//li";
		List<WebElement> allSections = driver.findElements(By.xpath(xpath));
		return allSections.size();
	}
	
	public Integer getCurrentNoOfEncounters() {
		String currentNoElementXpath = "//div[@id='generic_form_scrollspy_wrapper']//ul[@data-scroll-collapse-id='task_collapse']"+
									   "//ul//li//span[text()='Encounters']//span[@class='count-indicator']";
		
		WebElement currentNoElement = driver.findElement(By.xpath(currentNoElementXpath));
		String dataRowsCount = currentNoElement.getAttribute("data-row-count").toString();
		return Integer.parseInt(dataRowsCount);
	}
	
	public WebElement encounterPlusIcon() {
		String encounterPlusIcon = "//div[@id='generic_form_scrollspy_wrapper']//ul[@data-scroll-collapse-id='task_collapse']"+
								   "//ul//li//span[text()='Encounters']//preceding-sibling::span";
		
		return driver.findElement(By.xpath(encounterPlusIcon));
	}
	
	public WebElement encounterText() {
		String encounterTextXpath = "//div[@id='generic_form_scrollspy_wrapper']//ul[@data-scroll-collapse-id='task_collapse']"+
									"//ul//li//span[text()='Encounters']";
		
		return driver.findElement(By.xpath(encounterTextXpath));
	}
	
	public void clickOnEncounterText() {
		String encounterTextXpath = "//div[@id='generic_form_scrollspy_wrapper']//ul[@data-scroll-collapse-id='task_collapse']"+
									"//ul//li//span[text()='Encounters']";
		
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		executor.executeScript("arguments[0].click();", driver.findElement(By.xpath(encounterTextXpath)));
	}
	
	public void clickOnInternalReferralsText() {
		String internalReferralsTextXpath = "//div[@id='generic_form_scrollspy_wrapper']//ul[@data-scroll-collapse-id='task_collapse']"+
									"//ul//li//span[text()='Internal Referrals']";
		
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		executor.executeScript("arguments[0].click();", driver.findElement(By.xpath(internalReferralsTextXpath)));
	}
	
	public void clickOnChecklistText() {
		String checklistTextXpath = "//div[@id='generic_form_scrollspy_wrapper']//ul[@data-scroll-collapse-id='task_collapse']"+
									"//ul//li//span[text()='Checklist']";
		
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		executor.executeScript("arguments[0].click();", driver.findElement(By.xpath(checklistTextXpath)));
	}
	
	public void taskSaveButton() {
		String saveButtonXpath = "//span[@data-badge-caption='Save']//i";
		driver.findElement(By.xpath(saveButtonXpath)).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='auto_save_display'][@class='rfloat']")));
	}
	
	public List<WebElement> getActiveEncounterRows(){
		String activeRowsXpath = "//div[@id='generic_form_inner_wrapper']//div[@data-label='wpc_table_encounter_type_new']"+
								 "//table//tr[not(@class='notShown')][not(@class='add_row hide')]";
		
		return driver.findElements(By.xpath(activeRowsXpath));
	}
	
	public void setEncounterType(int index, String encounterType) {
		String encounterTypeXpath = "//div[@id='generic_form_inner_wrapper']//div[@data-label='wpc_table_encounter_type_new']"+
									"//table//tr[not(@class='notShown')][not(@class='add_row hide')]"+
									"//select[@element_name='log_type']";
		
		Select selectEncounterType = new Select(driver.findElements(By.xpath(encounterTypeXpath)).get(index));
		selectEncounterType.selectByVisibleText(encounterType);
	}
	
	public void setEncounterDuration(int index, String encounterDuration) {
		String encounterDurationXpath = "//div[@id='generic_form_inner_wrapper']//div[@data-label='wpc_table_encounter_type_new']"+
										"//table//tr[not(@class='notShown')][not(@class='add_row hide')]"+
										"//select[@element_name='time_spent']";
		
		Select selectEncounterDuration = new Select(driver.findElements(By.xpath(encounterDurationXpath)).get(index));
		selectEncounterDuration.selectByVisibleText(encounterDuration);
	}
	
	public void setEncounterWithWhom(int index, String encounterWithWhom) {
		String encounterWithWhomXpath = "//div[@id='generic_form_inner_wrapper']//div[@data-label='wpc_table_encounter_type_new']"+
										"//table//tr[not(@class='notShown')][not(@class='add_row hide')]"+
										"//select[@element_name='contact_with']";
		
		Select selectEncounterWithWhom = new Select(driver.findElements(By.xpath(encounterWithWhomXpath)).get(index));
		selectEncounterWithWhom.selectByVisibleText(encounterWithWhom);
	}
	
	public void setEncounterNote(int index, String encounterNote) {
		String encounterNotesXpath = "//div[@id='generic_form_inner_wrapper']//div[@data-label='wpc_table_encounter_type_new']"+
										"//table//tr[not(@class='notShown')][not(@class='add_row hide')]"+
										"//textarea[@element_name='log_note']";
		
		driver.findElements(By.xpath(encounterNotesXpath)).get(index).sendKeys(encounterNote);
	}
	
	public WebElement getActiveChecklist() {
		String activeChecklistXpath = "//div[@id='generic_form_inner_wrapper']//div[@data-label='WPC_table_checklist']";
		return driver.findElement(By.xpath(activeChecklistXpath));
	}
	
	public List<WebElement> getActiveChecklistLabels(WebElement activeChecklist) {
		String activeChecklistLabelsXpath = "//div[@id='generic_form_inner_wrapper']//div[@data-label='WPC_table_checklist']"+
											"//table[@id='WPC_table_checklist']//tbody//div[@class='fieldset-wrapper']//div//div//label";
		
		return driver.findElements(By.xpath(activeChecklistLabelsXpath));
	}
	
	public WebElement getInternalReferrals() {
		String activeChecklistXpath = "//div[@id='generic_form_inner_wrapper']//div[@data-label='WPC_table_internal_referral_need']";
		return driver.findElement(By.xpath(activeChecklistXpath));
	}
	
	public List<WebElement> getInternalReferralsRadioButtons(WebElement internalReferrals) {
		String activeRadioButtonsXpath = "//div[@id='generic_form_inner_wrapper']//div[@data-label='WPC_table_internal_referral_need']"+
										 "//div[@class='fieldset-wrapper']//div//div//label";
		return driver.findElements(By.xpath(activeRadioButtonsXpath));
	}
	
	public String getCheckedChecklistData() {
		String checkedChecklistDataXpath = "//div[@id='generic_form_inner_wrapper']//div[@data-label='WPC_table_checklist']"+
										   "//table[@id='WPC_table_checklist']//tbody//div[@class='fieldset-wrapper']"+
										   "//div//div//input[@intmd_val]";
		
//		List<String> checkedData = new ArrayList<>();
//		driver.findElements(By.xpath(checkedChecklistDataXpath)).forEach(e -> {checkedData.add(e.getAttribute("element_desc").toString());});
		
		return driver.findElement(By.xpath(checkedChecklistDataXpath)).getAttribute("element_desc").toString();
	}
	
	public String getInternalReferralsSelectedOption() {
		String xpath1 = "//div[@id='generic_form_inner_wrapper']//div[@data-label='WPC_table_internal_referral_need']"+
						"//div[@class='fieldset-wrapper']//div[@intmd_val]";
		
		String selectedOptionValue = driver.findElement(By.xpath(xpath1)).getAttribute("intmd_val").toString();
		String xpath2 = String.format(xpath1+"//div//input[@value=%s]//..//label", selectedOptionValue);
		WebElement elem = driver.findElement(By.xpath(xpath2));
		
		return elem.getText().toString();
	}
	
	public WebElement threeDot() {
		String threeDotXpath = "//div[contains(@class,'no_export_elem')]//a[contains(@data-target,'action_list')]";
		return driver.findElement(By.xpath(threeDotXpath));
	}
	
	public WebElement threeDotCompleteOption() {
		String threeDotCompleteOptionXpath = "//div[contains(@class,'no_export_elem')]//ul[contains(@id,'action_list')]//a[text()='Complete']";
		return driver.findElement(By.xpath(threeDotCompleteOptionXpath));
	}
	
	public WebElement threeDotCancelOption() {
		String threeDotCancelOptionXpath = "//div[contains(@class,'no_export_elem')]//ul[contains(@id,'action_list')]//a[text()='Cancel']";
		return driver.findElement(By.xpath(threeDotCancelOptionXpath));
	}
	
	public WebElement yesOptionOnPrompt() {
		String yesOptioXpath = "//div[@class='modal cozeva-prompt open']//div[@class='modal-footer']//a[@data-index='confirm']";
		return driver.findElement(By.xpath(yesOptioXpath));
	}
	
	public void waitForTaskPreloader() {
		String promptXpath = "//div[@class='modal cozeva-prompt open']";
		String preloaderXpath = "//div[@id='csm_generic_auto_save_forms']//div[@class='ajax_preloader not_for_mobile']";
		try {
//			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(promptXpath)));
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(preloaderXpath)));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public WebElement threeDotReopenOption() {
		String reopenOptioXpath = "//div[contains(@class,'no_export_elem')]//ul[contains(@id,'action_list')]//a[text()='Reopen']";
		return driver.findElement(By.xpath(reopenOptioXpath));
	}
	
	public WebElement reasonForReopen() {
		String reopenReasonXpath = "//div[@class='modal cozeva-prompt open']//div[contains(@class,'input-field')]//textarea[@id='edit_status_reason']";
		return driver.findElement(By.xpath(reopenReasonXpath));
	}
	
	public List<String> getSelectedEncounterTypes() {
		String encounterTypeXpath = "//div[@id='generic_form_inner_wrapper']//div[@data-label='wpc_table_encounter_type_new']"+
								   "//table//tr[not(@class='notShown')][not(@class='add_row hide')]"+
								   "//select[@element_name='log_type']";
		
		List<String> encounterTypes = new ArrayList<>();
		List<WebElement> activeFields = driver.findElements(By.xpath(encounterTypeXpath));
		
		activeFields.forEach(e -> {
			String oldValue = e.getAttribute("old_val").toString();
			System.out.println("oldValue: "+oldValue);
			String optionXpath = String.format("//option[@value='%s']", oldValue);
			String selectedOption = e.findElement(By.xpath(optionXpath)).getAttribute("innerHTML").toString();
			encounterTypes.add(selectedOption);
		});
		
		return encounterTypes;
	}
	
	public List<String> getSelectedEncounterDurations() {
		String encounterDurationXpath = "//div[@id='generic_form_inner_wrapper']//div[@data-label='wpc_table_encounter_type_new']"+
								   "//table//tr[not(@class='notShown')][not(@class='add_row hide')]"+
								   "//select[@element_name='time_spent']";
		
		List<String> encounterDurations = new ArrayList<>();
		List<WebElement> activeFields = driver.findElements(By.xpath(encounterDurationXpath));
		
		activeFields.forEach(e -> {
			String oldValue = e.getAttribute("old_val");
			String optionXpath = String.format("//option[@value='%s']", oldValue);
			String selectedOption = e.findElement(By.xpath(optionXpath)).getAttribute("innerHTML").toString();
			encounterDurations.add(selectedOption);
		});
		
		return encounterDurations;
	}
	
	public List<String> getSelectedEncounterWithWhoms() {
		String encounterWithWhomXpath = "//div[@id='generic_form_inner_wrapper']//div[@data-label='wpc_table_encounter_type_new']"+
								   "//table//tr[not(@class='notShown')][not(@class='add_row hide')]"+
								   "//select[@element_name='contact_with']";
		
		List<String> encounterWithWhoms = new ArrayList<>();
		List<WebElement> activeFields = driver.findElements(By.xpath(encounterWithWhomXpath));
		
		activeFields.forEach(e -> {
			String oldValue = e.getAttribute("old_val");
			String optionXpath = String.format("//option[@value='%s']", oldValue);
			String selectedOption = e.findElement(By.xpath(optionXpath)).getAttribute("innerHTML").toString();
			encounterWithWhoms.add(selectedOption);
		});
		
		return encounterWithWhoms;
	}
	
	public List<String> getSelectedEncounterNotes() {
		String encounterNotesXpath = "//div[@id='generic_form_inner_wrapper']//div[@data-label='wpc_table_encounter_type_new']"+
									 "//table//tr[not(@class='notShown')][not(@class='add_row hide')]"+
									 "//textarea[@element_name='log_note']";
		
		List<String> encounterNotes = driver.findElements(By.xpath(encounterNotesXpath)).stream().map(e->e.getText().toString()).collect(Collectors.toList());
		
		return encounterNotes;
	}
	
	public Map<String, Object> getAllSavedData() {
		Map<String, Object> savedData = new HashMap<>();
		
		List<String> selectedEncounterTypes = getSelectedEncounterTypes();
		List<String> selectedEncounterDurations = getSelectedEncounterDurations();
		List<String> selectedEncounterWithWhoms = getSelectedEncounterWithWhoms();
		List<String> selectedEncounterNotes = getSelectedEncounterNotes();
		
		List<Object> allEncounterData = new ArrayList<>();
		for(int i=0; i<selectedEncounterTypes.size(); i++) {
			HashMap<String, String> encounterData = new HashMap<>();
			encounterData.put("Encounter Type", selectedEncounterTypes.get(i));
			encounterData.put("Duration", selectedEncounterDurations.get(i));
			encounterData.put("With Whom", selectedEncounterWithWhoms.get(i));
			encounterData.put("Notes", selectedEncounterNotes.get(i));
			allEncounterData.add(encounterData);
		}
		savedData.put("Encounter", allEncounterData);
		
		HashMap<String, String> checklistData = new HashMap<>();
		String checkedChecklistData = getCheckedChecklistData();
		checklistData.put("ChecklistSelected", checkedChecklistData);
		savedData.put("Checklist", checklistData);
		
		HashMap<String, String> internalReferralsData = new HashMap<>();
		internalReferralsData.put("selectedRadioButton", getInternalReferralsSelectedOption());
		savedData.put("internalReferrals", internalReferralsData);
		
		return savedData;
	}
	
	public boolean matchData(Map<String, Object> storedData, Map<String, Object> retrievedData) {
		
		//Matching encounter data
		List<Object> encounters1 = (List<Object>) storedData.get("Encounter");
		List<Object> encounters2 = (List<Object>) retrievedData.get("Encounter");
		
		Map<String, String> checklist1 =  (Map<String, String>) storedData.get("Checklist");
		Map<String, String> checklist2 =  (Map<String, String>) retrievedData.get("Checklist");
		
		Map<String, String> internalReferrals1 =  (Map<String, String>) storedData.get("internalReferrals");
		Map<String, String> internalReferrals2 =  (Map<String, String>) retrievedData.get("internalReferrals");
		
		
		try {
			for(int i=0; i<encounters1.size(); i++) {
				Map<String, String> encounter1 = (Map<String, String>) encounters1.get(i);
				Map<String, String> encounter2 = (Map<String, String>) encounters2.get(i);
				
				Set<String> keySet = encounter1.keySet();
				for(String encounterKey: keySet) {
					if(!encounter1.get(encounterKey).equals(encounter2.get(encounterKey))) {
						throw new Exception("Encounter data is not matching");
					}
				}
			}
			
			Set<String> keySet = checklist1.keySet();
			for(String Key: keySet) {
				if(!checklist1.get(Key).equals(checklist2.get(Key))) {
					throw new Exception("Checklist data is not matching");
				}
			}
			
			keySet = internalReferrals1.keySet();
			for(String Key: keySet) {
				if(!internalReferrals1.get(Key).equals(internalReferrals2.get(Key))) {
					throw new Exception("Internal Referrals data is not matching");
				}
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		
		return true;
	}
	
	
}
