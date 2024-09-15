package PageObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import Resources.GenericFunctions;

public class CaseManagement {
	private WebDriver driver;
	private Properties configurationProp;
	private Properties genericProp;
	private WebDriverWait wait;
	private Actions action;
	private GenericFunctions genFunc;
	private Integer noOfAttachment = 0;
	
	public CaseManagement(WebDriver driver, Properties configurationProp, Properties genericProp, GenericFunctions genFunc) {
		// TODO Auto-generated constructor stub
		//comment added 2
		this.driver = driver;
		this.configurationProp = configurationProp;
		this.genericProp = genericProp;
		this.genFunc = genFunc;
		wait = new WebDriverWait(driver, 1);
		action = new Actions(driver);
		
	}
	
	// The methods mentioned below are providing useful utilities
	
	public boolean globalSearchAndNavigateToPatientDashboardByPatientID(String paatientCozID) {
		try {
			globalSearchBar().sendKeys(paatientCozID);
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			wait = new WebDriverWait(driver, 20);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[@id='search_results']")));
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			globalSearchBar().sendKeys(Keys.ENTER);
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			globalSearchPatientTab().click();
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			getTotalPatients().get(0).click();
			genFunc.switchTab(3);
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			genFunc.ajaxPreloaderWait();
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='patient_detail']")));
			
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean createNewCMTask(String programType, String taskType, String taskName, String assignee, String taskReason) {
		String[] assigneeList = assignee.split(",");
		int assigneeNo = assigneeList.length;
		try {
			patientDropdown().click();
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			action.moveToElement(patientDropdownCaseManagement());
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			action.moveToElement(patientDropdownAddTask()).click().build().perform();
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath(genericProp.getProperty("create_task_modal_category_dropdown"))));
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			Select selectCMcategory = new Select(createTaskModalCategoryDropdown());
			selectCMcategory.selectByVisibleText(programType.trim());
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			Select selectCMtask = new Select(createTaskModalTaskDropdownForCategory(programType.trim()));
			selectCMtask.selectByVisibleText(taskType.trim());
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			try {
				genFunc.ajaxPreloaderModalWait();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			createTaskModalTaskName().sendKeys(taskName);
			
			wait = new WebDriverWait(driver, 10);
			for(int i=0; i<assigneeNo; i++) {
				try {
					createTaskModalAssignee().clear();
					createTaskModalAssignee().sendKeys(assigneeList[i].trim());
					wait.until(ExpectedConditions.visibilityOfElementLocated(
							By.xpath(genericProp.getProperty("create_task_modal_assignee_names_autosuggestion_box"))));
					createTaskModalAssigneeAutosuggestionNames().get(0).click();
					break;
				} catch (Exception e) {
					continue;
				}
			}
			
			createTaskModalReasonField().sendKeys(taskReason);
			createTaskModalCreateNewTaskButton().click();
			
			try {
				genFunc.ajaxPreloaderWaitNotForMobile();
				wait = new WebDriverWait(driver, 10);
				wait.until(ExpectedConditions.visibilityOfElementLocated(
						By.xpath("//div[@id='generic_form_outer_wrapper']")));
			} catch (Exception e) {
				wait = new WebDriverWait(driver, 10);
				wait.until(ExpectedConditions.visibilityOfElementLocated(
						By.xpath("//div[@id='generic_form_outer_wrapper']")));
			}
			
			
			return true;
		}
		catch (Exception e) {
			driver.findElement(By.xpath("//div[contains(@class, \"custom-modal-dialog\")]//a[@title='Close']")).click();
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean taskReflectionOnProgramHistoryPage(String programID, String taskID, List<Object> taskDetails) {
		try {
			patientDropdown().click();
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			action.moveToElement(patientDropdownCaseManagement());
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			action.moveToElement(patientDropdownProgramHistory()).click().build().perform();
			genFunc.waitForElementToDisappear("//div[@class='ajax_preloader not_for_mobile']", 30);
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			
			for(Object taskDetail: taskDetails) {
				List<String> details = (List<String>)taskDetail;
				
				findProgramAtProgramHistoryByProgramID(details.get(2));
				try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			}
			
			programHistoryPageTaskTab().click();
			genFunc.waitForElementToDisappear("//div[@class='ajaxloader enc-loader']", 300);
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			
			for(Object taskDetail: taskDetails) {
				List<String> details = (List<String>)taskDetail;
				
				findTaskAtProgramHistoryByTaskID(details.get(1));
				try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			}
			
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	// The methods mentioned below are returning different webelements
	public WebElement appTray() {
		return driver.findElement(By.xpath(genericProp.getProperty("app_tray")));
	}
	
	public WebElement appTrayCaseManagementOption() {
		return driver.findElement(By.xpath(genericProp.getProperty("case_management_icon")));
	}
	
	public WebElement globalSearchBar() {
		return driver.findElement(By.xpath(genericProp.getProperty("global_search_field")));
	}
	
	public WebElement globalSearchPatientTab() {
		return driver.findElement(By.xpath(genericProp.getProperty("global_search_patients_tab")));
	}
	
	public List<WebElement> getTotalPatients() {
		return driver.findElements(By.xpath(genericProp.getProperty("total_patients")));
	}
	
	public WebElement patientDropdown() {
		return driver.findElement(By.xpath(genericProp.getProperty("patient_dropdown")));
	}
	
	public WebElement patientDropdownCaseManagement() {
		return driver.findElement(By.xpath(genericProp.getProperty("patient_dropdown_case_management")));
	}
	
	public WebElement patientDropdownHistory() {
		return driver.findElement(By.xpath(genericProp.getProperty("patient_dropdown_history")));
	}
	
	public WebElement patientDropdownHistoryDocuments() {
		return driver.findElement(By.xpath(genericProp.getProperty("patient_dropdown_history_documents")));
	}
	
	public List<String> getFileNamesAtDocumentPage(Integer num_files) {
		List<String> fileNames = new ArrayList<>();
		String fileNamesXpath = "//div[@class='patient_document_name']";
		List<WebElement> fileNamesElem = driver.findElements(By.xpath(fileNamesXpath));
		int num_fileName = fileNamesElem.size();
		for(int i=0; i<num_fileName && i<num_files ; i++) {
			fileNames.add(fileNamesElem.get(i).getText());
		}
		return fileNames;
	}
	
	public WebElement patientDropdownAddTask() {
		return driver.findElement(By.xpath(genericProp.getProperty("patient_dropdown_case_management_add_task")));
	}
	
	public WebElement patientDropdownProgramHistory() {
		String programHistoryXpath = "//ul[@id='patient_header_dropdown_compact']//li[@id='care-coordination']//ul[@class='patient_submenu']//a[text()='Program History']";
		return driver.findElement(By.xpath(programHistoryXpath));
	}
	
	public WebElement createTaskModalCategoryDropdown() {
		return driver.findElement(By.xpath(genericProp.getProperty("create_task_modal_category_dropdown")));
	}
	
	public WebElement createTaskModalTaskDropdownForCategory(String category) {
		switch(category) {
			case "Care Transitions":
				return driver.findElements(By.xpath(genericProp.getProperty("create_task_modal_task_dropdown"))).get(0);
				
			case "Care Management":
				return driver.findElements(By.xpath(genericProp.getProperty("create_task_modal_task_dropdown"))).get(1);
				
			case "Care Coordination":
				return driver.findElements(By.xpath(genericProp.getProperty("create_task_modal_task_dropdown"))).get(2);
				
			case "Screening Tools":
				return driver.findElements(By.xpath(genericProp.getProperty("create_task_modal_task_dropdown"))).get(3);
				
		}
		return null;
	}
	
	public WebElement createTaskModalTaskName() {
		return driver.findElement(By.xpath(genericProp.getProperty("create_task_modal_task_name")));
	}
	
	public WebElement createTaskModalAssignee() {
		return driver.findElement(By.xpath(genericProp.getProperty("create_task_modal_assignee")));
	}
	
	public List<WebElement> createTaskModalAssigneeAutosuggestionNames() {
		return driver.findElements(By.xpath(genericProp.getProperty("create_task_modal_assignee_names_autosuggestion_names")));
	}
	
	public WebElement createTaskModalReasonField() {
		return driver.findElement(By.xpath(genericProp.getProperty("create_task_modal_reason")));
	}
	
	public WebElement createTaskModalCreateNewTaskButton() {
		return driver.findElement(By.xpath(genericProp.getProperty("create_task_modal_create_new_task_button")));
	}
	
	public WebElement taskDetailsOfCMtaskPage() {
		return driver.findElement(By.xpath(genericProp.getProperty("task_details_of_CM_task")));
	}
	
	public WebElement burgerIcon() {
		return driver.findElement(By.xpath(genericProp.getProperty("burger_icon")));
	}
	
	public WebElement sidebarTaskOption() {
		return driver.findElement(By.xpath(genericProp.getProperty("sidebar_task_option")));
	}
	
	public WebElement sidebarProgramsOption() {
		return driver.findElement(By.xpath(genericProp.getProperty("sidebar_programs_option")));
	}
	
	public WebElement sidebarReportsOption() {
		return driver.findElement(By.xpath(genericProp.getProperty("sidebar_reports_option")));
	}
	
	public WebElement findProgramByProgramName(String programName) {
		String programXpath = String.format(genericProp.getProperty("programs_at_programs_page"), programName);
		return driver.findElement(By.xpath(programXpath));
	}
	
	public WebElement findChildTaskOfProgram(WebElement program, String taskName) {
//		WebElement parentElementOfProgram = program.findElement(By.xpath("./.."));
		String taskXpath = String.format("//following-sibling::*//a[text()='%s']", taskName);
		return program.findElement(By.xpath(taskXpath));
	}
	
	public WebElement findTaskAtProgramsPage(String programName, String taskName) {
//		WebElement parentElementOfProgram = program.findElement(By.xpath("./.."));
		String taskXpath = String.format("//ul[@id='task_cat_wrapper']//li[@class='active']//div[contains(@class,'collapsible-header')][text()='%s']//following-sibling::*//a[text()='%s']", programName, taskName);
		return driver.findElement(By.xpath(taskXpath));
	}
	
	public String findProgramIDByTaskID(String taskID) {
		String programXpath = String.format("//a[@id='%s']//ancestor::li//div//a", taskID);
		WebElement program = driver.findElement(By.xpath(programXpath));
		return program.getAttribute("form_id");
	}
	
	public String findProgramAtProgramHistoryByProgramID(String programID) {
		String programXpath = String.format("//table[@id='patient_specific_case_list']//a[text()='#%s']", programID);
		WebElement program = driver.findElement(By.xpath(programXpath));
		return program.getText();
	}
	
	public WebElement programHistoryPageTaskTab() {
		String taskTabXpath = "//div[@id='generic_patient_specific_tab_list']//a[text()='Tasks']";
		return driver.findElement(By.xpath(taskTabXpath));
	}
	
	public String findTaskAtProgramHistoryByTaskID(String taskID) {
		String taskXpath = String.format("//table[@id='patient_specific_task_list']//a[text()='#%s']", taskID);
		WebElement program = driver.findElement(By.xpath(taskXpath));
		return program.getText();
	}
	
	public WebElement programsPageFilterIcon() {
		return driver.findElement(By.xpath(genericProp.getProperty("programs_page_filter_icon")));
	}
	
	public WebElement taskIdDeletedSuccessfully() {
		String taskIdDeletionXpath = "//div[@id='toast-container']//div[text()='Deleted Successfully']";
		return driver.findElement(By.xpath(taskIdDeletionXpath));
	}
	
	public WebElement filterTaskField() {
		return driver.findElement(By.xpath(genericProp.getProperty("filter_task_field")));
	}
	
	public WebElement programsPageFilterApplyButton() {
		return driver.findElement(By.xpath(genericProp.getProperty("programs_page_filter_apply_button")));
	}
	
	public WebElement reportsPageFilterIcon() {
		return driver.findElement(By.xpath(genericProp.getProperty("reports_page_filter_icon")));
	}
	
	public WebElement reportsPageFilterProgramField() {
		return driver.findElement(By.xpath(genericProp.getProperty("reports_page_filter_programID_field")));
	}
	
	public WebElement reportsPageFilterApplyButton() {
		return driver.findElement(By.xpath(genericProp.getProperty("reports_page_filter_apply_button")));
	}
	
	public List<WebElement> reportsPageAllPrograms() {
		return driver.findElements(By.xpath(genericProp.getProperty("reports_page_programs")));
	}
	
	public List<WebElement> getProgramsPageTaskIDs() {
		return driver.findElements(By.xpath(genericProp.getProperty("programs_page_cm_taskRow_id")));
	}
	
	public List<WebElement> activeFilterOptions() {
		return driver.findElements(By.xpath(genericProp.getProperty("active_filter_options")));
	}
	
	public WebElement agendaViewFilter() {
		return driver.findElement(By.xpath(genericProp.getProperty("agenda_view_filter")));
	}
	
	public WebElement agendaViewFilterTaskIDfield() {
		return driver.findElement(By.xpath(genericProp.getProperty("agenda_view_filter_task_field")));
	}
	
	public WebElement agendaViewFilterApply() {
		return driver.findElement(By.xpath(genericProp.getProperty("agenda_view_filter_apply")));
	}
	
	public WebElement agendaViewTaskDetails() {
		return driver.findElements(By.xpath(genericProp.getProperty("agenda_view_task_details"))).get(0);
	}
	
	public WebElement agendaViewTPencilIcon() {
		String pencilIconXpath = "//div[@class='card horizontal']//div[contains(@class,'card-stacked')]//a[@title='Edit Task']";
		return driver.findElements(By.xpath(pencilIconXpath)).get(0);
	}
	
	public WebElement closeIcon() {
		String closeIconXpath = "//a[@title='Close']";
		return driver.findElement(By.xpath(closeIconXpath));
	}
	
	public WebElement threeDot() {
		String threeDotXpath = "//div[contains(@class,'no_export_elem')]//a[contains(@data-target,'action_list')]";
		return driver.findElement(By.xpath(threeDotXpath));
	}
	
	public WebElement threeDotAddAttachmentOption() {
		String threeDotCompleteOptionXpath = "//div[contains(@class,'no_export_elem')]//ul[contains(@id,'action_list')]//a[text()='Add Attachment']";
		return driver.findElement(By.xpath(threeDotCompleteOptionXpath));
	}
	
	public void deleteAttachedFile(Integer noOfFile) {
		String deleteIconXpath = "//table[@id='patient_uploaded_doc_table']//tbody//tr[not(@class='add_row hide')]//span[contains(@class, 'generic_delete_button')]";
		List<WebElement> deleteIcon = driver.findElements(By.xpath(deleteIconXpath));
		for(int i=0; i<noOfFile; i++) {
			deleteIcon.get(i).click();
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	
	public void attachFile(String filePath) {
		String attachmentField = "//input[@title='Drag and Drop / Select from your computer']";
		driver.findElement(By.xpath(attachmentField)).sendKeys(filePath);
		wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[contains(@class, 'uploading')]")));
		noOfAttachment++;
	}
	
	public Integer getNoOfUploadedFiles() {
		return noOfAttachment;
	}
	
	public Integer getNoOfAttachedFileVisibleInWebpage() {
		try {
			driver.findElement(By.xpath("//div[contains(@class, 'attach_content')]//span[2]")).getText();
		} catch (NoSuchElementException e) {
			return 0;
		}
		String text = driver.findElement(By.xpath("//div[contains(@class, 'attach_content')]//span[2]")).getText();
		return Integer.parseInt(text.substring(1, 2));
	}
	
	public void deleteAllAttachedFiles() {
		String deleteIconsXpath = "//table[@id='patient_uploaded_doc_table']//tbody//tr[not(@class='add_row hide')]//span[contains(@class, 'generic_delete_button')]";
		List<WebElement> deleteIcons = driver.findElements(By.xpath(deleteIconsXpath));
		int num_icons = deleteIcons.size();
		
		for(int i=0; i<num_icons; i++) {
			WebElement deleteIcon = deleteIcons.get(i);
			deleteIcon.click();
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	
	public void saveAttachment() {
		String saveIconXpath = "//button[text()='Save']";
		driver.findElement(By.xpath(saveIconXpath)).click();
	}
	
	
}
