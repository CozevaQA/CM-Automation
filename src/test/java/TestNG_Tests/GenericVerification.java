package TestNG_Tests;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import PageObjects.CaseManagement;
import PageObjects.CozevaPage;
import PageObjects.LoginPage;
import PageObjects.Masquerade;
import Resources.InitializeDriver;
import TasksVerification.CarePlan;
import TasksVerification.HospitalVisitNote;
import Resources.Excel;
import Resources.GUIFrame;
import Resources.GenericFunctions;

public class GenericVerification {

	private WebDriver driver;
	private Logger logger;
	private Properties configuration;
	private Properties genericXpaths;
	private String report_folder_directory;
	private WebDriverWait wait;
	private GenericFunctions genFunc;
	private CozevaPage cozPage;
	private Actions action;
	private String cmTaskID;
	private String cmProgramID;
	private List<Object> taskDetails;
	private CaseManagement cm;
	private int testCaseID;
	private Properties cmUserInput;
	private Map<String, Object> guiData;
	private Map<String, Object> programToTask;
	private Map<String, Object> testSuite;
	private List<Object> genericTestcase;

	@BeforeTest
	public void collect_Load_Initialize() throws IOException {
		String configPropertyPath = System.getProperty("user.dir") + File.separator
				+ "src\\main\\java\\Config\\configuration.properties";

		System.setProperty("configFilePath", configPropertyPath);

		String configPath = System.getProperty("configFilePath");
		taskDetails = new ArrayList<Object>();
		System.out.println(configPath);

		genFunc = new GenericFunctions();

		driver = genFunc.initializeChromeDriver();
		configuration = genFunc.getConfigurationProperty();
		genericXpaths = genFunc.getGenericXpathProperty();
		cmUserInput = genFunc.getCMUserInputProperty();

		cozPage = new CozevaPage(driver, configuration, genericXpaths);

		// Getting current date
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMMM-yyyy");
		Date date = new Date();
		String currentTimestamp = formatter.format(date);
		System.out.println(currentTimestamp);

		// Generating report folder directory
		File folderHierarchy = new File(
				configuration.getProperty("report_folder_location") + File.separator + currentTimestamp);
		if (!folderHierarchy.exists()) {
			folderHierarchy.mkdirs();
			System.out.println("folder created...");
		}
		report_folder_directory = configuration.getProperty("report_folder_location") + File.separator
				+ currentTimestamp;

		wait = new WebDriverWait(driver, 40);
		action = new Actions(driver);

		programToTask = new HashMap<>();
		testSuite = new HashMap<>();
		testCaseID = 1;

		String programs[] = { "CareTransitions", "CareManagement", "CareCoordination", "ScreeningTools" };
		for (String program : programs) {
			String taskStr = cmUserInput.getProperty(program);
			String[] tasks = taskStr.split(",");
			List<String> taskList = new ArrayList<>();
			for (String task : tasks) {
				taskList.add(task);
			}
			programToTask.put(program, taskList);
		}

		List<Object> customerDB = new ArrayList<>();
		Excel customerXlsx = new Excel();
		String filePath = System.getProperty("user.dir") + "\\src\\main\\java\\TestData";
		System.out.println("filePath: " + filePath);
		customerXlsx.readExcel(filePath, "customerDB", 0);
		int i = 0;
		while (customerXlsx.ReadCellData(i++, 0) != null) {
			String customerName = null;
			String customerID = null;
			String userName = null;
			String patientID = null;

			try {
				customerName = customerXlsx.ReadCellData(i, 0);
				customerID = customerXlsx.ReadCellData(i, 1);
				userName = customerXlsx.ReadCellData(i, 2);
				patientID = customerXlsx.ReadCellData(i, 3);
			} catch (NullPointerException npE) {
				break;
			}

			List<String> customerData = new ArrayList<>();
			customerData.add(customerName);
			customerData.add(customerID);
			customerData.add(userName);
			customerData.add(patientID);

			customerDB.add(customerData);
		}

		GUIFrame gui = new GUIFrame(programToTask, customerDB);
		guiData = gui.getDataFromUI();

		// Configuration of logger file
		logger = Logger.getLogger(GenericVerification.class.getName());
		File file = new File(report_folder_directory + File.separator +guiData.get("customerID") + " "
				+ guiData.get("userName") + "CM_logfile.log");
		if (file.exists()) {
			file.delete();
			System.out.println("Previously created logfile is deleted.");
		}
		Appender fh = new FileAppender(new SimpleLayout(), report_folder_directory + File.separator +guiData.get("customerID") + " "
				+ guiData.get("userName") + "CM_logfile.log");
		logger.addAppender(fh);
	}

	@Test(priority = 1)
	public void Test1_initialization() throws IOException, InterruptedException {
		genericTestcase = new ArrayList<>();
		cm = new CaseManagement(driver, configuration, genericXpaths, genFunc);
		testSuite.put("Generic test", genericTestcase);
	}

	@Test(priority = 2)
	public void Test2_login_to_registry() throws InterruptedException, IOException {
		try {
			genFunc.loginToCozeva();
			logger.info("Logged in to cozeva!");
		} catch (Exception e) {
			logger.error("Failed to login Cozeva");
		}

		genFunc.getCustomer((String) guiData.get("customerID"));
		logger.info("Navigated to customer: "+guiData.get("customerID").toString());

		// Removing existing announcement
		if (genFunc.removeAnnouncement()) {
			logger.info("Announcement found after masquerading the user and removed.");
		} else {
			logger.info("No announcement found after masquerading the user.");
		}
	}

	@Test(priority = 3)
	public void Test3_masquerade_cm_user() throws InterruptedException {

		try {

			// Masquerading user
			boolean masqFlag = genFunc.openUserlistAndMasqueradeToUsername((String) guiData.get("userName"));
			if (masqFlag) {
				logger.info("Masqueraded to user: " + guiData.get("userName"));
			} else {
				if (guiData.get("userName").toString().trim().equals("")) {
					cozPage.appTray().click();
					Thread.sleep(200);
					cozPage.caseManagement().click();
					Thread.sleep(200);
					genFunc.switchTab(2);
					logger.info("Username not found. Probably it's an onshore client.");
					throw new Exception("Username not found. Probably it's an onshore client.");
				} else {
					logger.error("Unable to masquerade user: " + guiData.get("userName"));
					throw new Exception("Failed to masquerade");
				}
			}

			// Removing existing announcement
			if (genFunc.removeAnnouncement()) {
				logger.info("Announcement found after masquerading the user and removed.");
			} else {
				logger.info("No announcement found after masquerading the user.");
			}

			cozPage.appTray().click();
			logger.info("Clicked on apptray.");
			Thread.sleep(200);
			cozPage.caseManagement().click();
			logger.info("Clicked on case management app.");
			Thread.sleep(200);
			genFunc.switchTab(2);
			logger.info("Tab switched to case management app window.");

			List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
					"To verify if the user has been masqueraded properly or not",
					"User should be masqueraded properly.", "PASSED"));
			genericTestcase.add(testList);
		} catch (Exception e) {
			List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
					"To verify if the user has been masqueraded properly or not",
					"User should be masqueraded properly.", "FAILED", e.getMessage()));
			genericTestcase.add(testList);
			e.printStackTrace();
		}

	}

	@Test(priority = 4)
	public void Test4_creating_a_task() throws InterruptedException {
		try {
			// Navigation to patient dashboard
			boolean successFlag = cm
					.globalSearchAndNavigateToPatientDashboardByPatientID((String) guiData.get("patientID"));
			if (successFlag) {
				logger.info("Successfully navigated to patient dashboard for patient: "
						+ (String) guiData.get("patientID"));
			} else {
				logger.error("Failed to navigate to patient dashboard");
				throw new Exception("Failed to navigate to patient dashboard");
			}

			genFunc.duplicateCurrentTab();
			logger.info("Patient dashboard window duplicated.");

			// New task creation
			genFunc.switchTab(4);
			logger.info("Swiched to new duplicated patient dashboard window.");
			List<String> tasks = (List<String>) guiData.get("task");
			for (String task : tasks) {
				List<String> taskDetail = new ArrayList<>();
				successFlag = cm.createNewCMTask((String) guiData.get("program"), task,
						cmUserInput.getProperty("testTaskName"), cmUserInput.getProperty("assigneeName"),
						cmUserInput.getProperty("taskReason"));

				if (successFlag) {
					String cmTaskDetails = cm.taskDetailsOfCMtaskPage().getText();
					String cmTaskID = cmTaskDetails.split(":")[0].split(" ")[1];
					String cmProgramID = cm.findProgramIDByTaskID(cmTaskID);
					logger.info(String.format("Created task %s with taskID %s and programID %s", guiData.get("task"),
							cmTaskID, cmProgramID));
					taskDetail.add(task);
					taskDetail.add(cmTaskID);
					taskDetail.add(cmProgramID);

					List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
							"To verify if CM task is created properly or not",
							"CM task(" + task + ")should be created properly.", "PASSED",
							"Task ID: " + cmTaskID + ", " + "Program ID: " + cmProgramID));
					genericTestcase.add(testList);
				} else {
					logger.error("Failed to create task: " + task + ", Probably this tasktype is not available here.");
					List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
							"To verify if CM task is created properly or not",
							"CM task(" + task + ")should be created properly.", "FAILED",
							"Probably this tasktype is not available here"));
					genericTestcase.add(testList);
					continue;
				}
				taskDetails.add(taskDetail);
			}
			/*
			 * if (guiData.get("task").equals("Hospital Visit Note")) {
			 * System.out.println("Entered Hospital Visit Note verification"); List<Object>
			 * HVN_Testcase = new ArrayList<>(); HospitalVisitNote hvnVerification = new
			 * HospitalVisitNote(driver, HVN_Testcase, logger);
			 * hvnVerification.startHospitalVisitNoteVerification(); List<Object>
			 * hvnVerification_report = hvnVerification.getReport();
			 * testSuite.put("Hospital Visit Note", hvnVerification_report); }
			 * 
			 * List<String> testList = new
			 * ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
			 * "To verify if CM task is created properly or not",
			 * "CM task should be created properly.", "PASSED", "Task ID: " + cmTaskID +
			 * ", " + "Program ID: " + cmProgramID)); genericTestcase.add(testList);
			 */
		} catch (Exception e) {
			logger.error("Failed to create task.");
			e.printStackTrace();
			List<String> testList = new ArrayList<String>(
					Arrays.asList(Integer.toString(testCaseID++), "To verify if CM task is created properly or not",
							"CM task should be created properly.", "FAILED"));
			genericTestcase.add(testList);
		}

	}

	@Test(priority = 5)
	public void Test5_Verify_attachment() throws Exception {
		logger.info("Started verifying attachment.");
		try {
			cm.threeDot().click();
			logger.info("Three dot clicked.");
			cm.threeDotAddAttachmentOption().click();
			logger.info("Attachment option clicked.");
			wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//div[contains(@class, 'custom-modal-dialog')]//h3[text()='Add Attachment']")));
			try {
				int attachedFileNo = 1;
				while (true) {
					cm.attachFile(configuration.getProperty("attachFilePath" + attachedFileNo));
					logger.info(String.format("File %s attached.", attachedFileNo));
					attachedFileNo++;
				}
			} catch (IllegalArgumentException e) {
				logger.info("Attachment completed.");
				logger.info("No. of files attached: " + cm.getNoOfUploadedFiles());
			}

			cm.saveAttachment();
			logger.info("Attachments are saved.");
			Thread.sleep(500);
			Integer noOfUploadedFiles = cm.getNoOfUploadedFiles();
			Integer noOfVisibleFiles = cm.getNoOfAttachedFileVisibleInWebpage();

			if (noOfUploadedFiles.equals(noOfVisibleFiles)) {
				logger.info("No. of attached files and no. of visible attcahed files matched");
				List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
						"To verify if files got uploaded properly or not.", "Files were uploaded properly", "PASSED",
						"No. of attached files reflects on webpage."));
				genericTestcase.add(testList);

			} else {
				logger.error("No. of attached files and no. of visible attcahed files did not matched");
				List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
						"To verify if files got uploaded properly or not.", "Files were not uploaded properly",
						"FAILED", "No. of attached files doesn't reflect on webpage."));
				genericTestcase.add(testList);
			}

			cm.threeDot().click();
			logger.info("Three dot clicked");
			cm.threeDotAddAttachmentOption().click();
			logger.info("Attachment option clicked.");
			wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//div[contains(@class, 'custom-modal-dialog')]//h3[text()='Add Attachment']")));

			cm.deleteAllAttachedFiles();
			logger.info("All attached files are removed from the attachment modal.");
			Thread.sleep(500);
			cm.saveAttachment();
			logger.info("Attachment modal is saved.");
			Thread.sleep(500);
		} catch (Exception e) {
			logger.error("Something went wrong while attaching file!");
			List<String> testList = new ArrayList<String>(
					Arrays.asList(Integer.toString(testCaseID++), "To verify if files got uploaded properly or not.",
							"Something went wrong while attaching file!", "FAILED"));
			genericTestcase.add(testList);
			e.printStackTrace();
		}

	}

	@Test(priority = 6)
	public void Test6_task_details_appearance_on_program_history_page() {
		genFunc.switchTab(3);
		logger.info("Tab switched to 3 for program history verification.");
		try {
			// Checking reflection of task details at program history page
			boolean successFlag = cm.taskReflectionOnProgramHistoryPage(cmProgramID, cmTaskID, taskDetails);
			if (successFlag) {
				logger.info("Task deails found at program history page.");
			} else {
				logger.error("Task details were not found at program history page.");
				throw new Exception("Task details didn't match at progran history page.");
			}

			List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
					"To verify if CM task details is appearing in the programs history or not",
					"CM task details should appear in the programs history properly.", "PASSED"));
			genericTestcase.add(testList);
		} catch (Exception e) {
			e.printStackTrace();
			List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
					"To verify if CM task details is appearing in the programs history or not",
					"CM task details should appear in the programs history properly.", "FAILED"));
			genericTestcase.add(testList);
		}
	}

	@Test(priority = 7)
	public void Test7_navigate_again_to_agenda_view() throws InterruptedException {
		genFunc.switchTab(2);
		logger.info("Tab switched to 2 for agenda view verification.");
		try {
			Thread.sleep(100);
			cm.burgerIcon().click();
			logger.info("Burger icon clicked.");
			Thread.sleep(100);
			cm.sidebarTaskOption().click();
			logger.info("Side bar task option clicked.");
			genFunc.ajaxPreloaderWaitNotForMobile();
			Thread.sleep(100);

			try {
				List<WebElement> activeFilterOptions = cm.activeFilterOptions();
				int len = activeFilterOptions.size();
				for (int i = 0; i < len; i++) {
					WebElement closeIcon = cm.activeFilterOptions().get(0)
							.findElement(By.xpath(".//span[@class='dt_tag_close material-icons']"));
					genFunc.jsClickOnElement(closeIcon);
					logger.info("Filter "+(i+1)+ "removed from agenda view.");
					genFunc.ajaxPreloaderWaitNotForMobile();
					Thread.sleep(200);
				}
			} catch (Exception e) {
				System.out.println("There is no filter option available on agenga view!");
			}

			List<String> testList = new ArrayList<String>(
					Arrays.asList(Integer.toString(testCaseID++), "To verify if CM agenda view page is opening or not",
							"CM agenda view page should open properly.", "PASSED"));
			genericTestcase.add(testList);
		} catch (Exception e) {
			List<String> testList = new ArrayList<String>(
					Arrays.asList(Integer.toString(testCaseID++), "To verify if CM agenda view page is opening or not",
							"CM agenda view page should open properly.", "FAILED", "Something went wrong!"));
			genericTestcase.add(testList);
			e.printStackTrace();
		}

	}

	@Test(priority = 8)
	public void Test8_check_taskID_populating_at_agenda_view() throws InterruptedException {
		Thread.sleep(100);
		for (Object taskDetail : taskDetails) {
			List<String> details = (List<String>) taskDetail;

			try {
				cm.agendaViewFilter().click();
				logger.info("agenga view filter clicked.");
			} catch (ElementClickInterceptedException e) {
				genFunc.ajaxPreloaderWait();
				cm.agendaViewFilter().click();
				logger.info("agenga view filter clicked.");
			}

			try {
				Thread.sleep(100);
				cm.agendaViewFilterTaskIDfield().clear();
				logger.info("agenda view filter task field cleared.");
				cm.agendaViewFilterTaskIDfield().sendKeys(details.get(1));
				logger.info("Task ID "+details.get(1)+ " pasted on agenda view filter task field");
				Thread.sleep(100);
				cm.agendaViewFilterApply().click();
				logger.info("agenda view filter applied");
				Thread.sleep(100);
				genFunc.ajaxPreloaderWait();
				String agendaViewTaskDetails = cm.agendaViewTaskDetails().getText();
				String agendaViewTaskID = agendaViewTaskDetails.split(":")[0].split(" ")[1];

				System.out.println("agendaViewTaskDetails: " + agendaViewTaskDetails);
				System.out.println(agendaViewTaskID);

				try {
					Assert.assertEquals(details.get(1), agendaViewTaskID);
					if (agendaViewTaskID.equals(details.get(1))) {
						logger.info("Task ID found at agenda view: " + details.get(1));
						List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
								"To verify if CM task id is found at agenda view or not",
								"CM task id should be found at agenda view", "PASSED",
								"Task ID found at agenda view: " + details.get(1)));
						genericTestcase.add(testList);
					} else {
						logger.error("Task ID not found at agenda view: " + details.get(1));
						throw new Exception("TaskID not matched at agenda view.");
					}
					System.out.println("TaskID matched successfully at agenda view");
				} catch (Exception e) {
					List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
							"To verify if CM task id is found at agenda view or not",
							"CM task id should be found at agenda view", "FAILED",
							"Task ID was not found at agenda view: " + details.get(1)));
					genericTestcase.add(testList);
				}

			} catch (Exception e) {
				List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
						"To verify if agenda view functionalities are working fine or not",
						"Agenda view functionalities should be working fine", "FAILED", "Something went wrong!"));
				genericTestcase.add(testList);
				e.printStackTrace();
			}
		}

		try {
			cm.agendaViewTPencilIcon().click();
			logger.info("Agenda view task template pencil icon clicked.");
			Thread.sleep(100);
			wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//div[@id='myAjaxModal']//div[@class='popupform clearfix']")));
			List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
					"To verify if task editing modal is opening at agenda view or not",
					"Task editing modal should be opened at agenda view.", "PASSED"));
			genericTestcase.add(testList);
			Thread.sleep(100);
			cm.closeIcon().click();
			logger.info("Agenda view task template closed.");
		} catch (Exception e) {
			List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
					"To verify if task editing modal is opening at agenda view or not",
					"Task editting modal should be opened at agenda view.", "FAILED"));
			genericTestcase.add(testList);
			e.printStackTrace();
		}

	}

	@Test(priority = 9)
	public void Test9_check_taskID_populating_at_programs_page() throws InterruptedException {
		Thread.sleep(100);
		try {
			cm.burgerIcon().click();
			logger.info("Burger icon clicked.");
			Thread.sleep(100);
			cm.sidebarProgramsOption().click();
			logger.info("Side bar program option clicked.");
		} catch (ElementNotInteractableException e) {
			Thread.sleep(5000);
			cm.burgerIcon().click();
			logger.info("Burger icon clicked.");
			Thread.sleep(100);
			cm.sidebarProgramsOption().click();
			logger.info("Side bar program option clicked.");
		}
		genFunc.programPageLoaderWait();

		try {
//			List<WebElement> activeFilterOptions = cm.activeFilterOptions();
//			Thread.sleep(100);
//			int len = activeFilterOptions.size();
//			for (int i = 0; i < len; i++) {
//				WebElement closeIcon = cm.activeFilterOptions().get(0)
//						.findElement(By.xpath(".//span[@class='dt_tag_close material-icons']"));
////				action.moveToElement(closeIcon).click().build().perform();
//				genFunc.jsClickOnElement(closeIcon);
//				genFunc.programPageLoaderWait();
//				Thread.sleep(200);
//			}

			Thread.sleep(100);
//			WebElement programElement = cm.findProgramByProgramName("Care Transitions");
//			System.out.println(programElement.getText());
//			WebElement childTaskElement = cm.findChildTaskOfProgram(programElement, "Hospital Visit Note");
			List<String> tasks = (List<String>) guiData.get("task");
			for (Object taskDetail : taskDetails) {
				List<String> details = (List<String>) taskDetail;

				WebElement taskElement = cm.findTaskAtProgramsPage((String) guiData.get("program"),
						details.get(0).trim());
				logger.info("Task element located at program page.");
				System.out.println(taskElement.getText());
				taskElement.click();
				logger.info("Task element clicked.");

				genFunc.programPageLoaderWait();

//				help.ajaxPreloaderWait();
				Thread.sleep(100);

				try {
					cm.programsPageFilterIcon().click();
					logger.info("Programs page filter icon clicked.");
				} catch (ElementClickInterceptedException clickException) {
					wait.until(ExpectedConditions.elementToBeClickable(cm.programsPageFilterIcon()));
					cm.programsPageFilterIcon().click();
					logger.info("Programs page filter icon clicked.");
				} catch (Exception e) {
					e.printStackTrace();
				}

				Thread.sleep(100);
				cm.filterTaskField().clear();
				logger.info("Programs page filter task field cleared.");
				cm.filterTaskField().sendKeys(details.get(1));
				logger.info("Task ID "+details.get(1)+" pasted on programs page filter task field");
				Thread.sleep(100);
//				help.jsClickOnElement(cm.programsPageFilterApplyButton());
				cm.programsPageFilterApplyButton().click();
				logger.info("Programs page filter applied.");
				genFunc.programPage2ndLoaderWait();
				Thread.sleep(100);

				try {
					String taskIdFoundAtProgramsPage = cm.getProgramsPageTaskIDs().get(0).getText();
					logger.info("Task ID found at programs page: "+taskIdFoundAtProgramsPage);
					System.out.println("taskIdFoundAtProgramsPage: " + taskIdFoundAtProgramsPage);

					if (taskIdFoundAtProgramsPage.equals(details.get(1))) {
						List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
								"To verify if CM task id is found at programs page or not",
								"CM task id should be found at programs page", "PASSED",
								"Task ID found: " + taskIdFoundAtProgramsPage + ", task name: " + details.get(0)));
						genericTestcase.add(testList);
						System.out.println("TaskID matched successfully at programs page");
					} else {
						List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
								"To verify if CM task id is found at programs page or not",
								"CM task id should be found at programs page", "FAILED",
								"Task ID found: " + taskIdFoundAtProgramsPage + ", task name: " + details.get(0)));
						genericTestcase.add(testList);
						throw new Exception("TaskID not matched at programs page.");
					}

				} catch (Exception e) {
					List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
							"To verify if CM task id is found at programs page or not",
							"CM task id should be found at programs page", "FAILED", "task name: " + details.get(0)));
					genericTestcase.add(testList);
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
					"To verify if programs page functionalities are working fine or not",
					"Programs page functionalities should be working fine", "FAILED", "Something went wrong!"));
			genericTestcase.add(testList);
			e.printStackTrace();
		}

//		
	}

	@Test(priority = 10)
	public void Test10_check_programID_populating_at_reports_page() throws InterruptedException {
		try {
			Thread.sleep(100);
			cm.burgerIcon().click();
			logger.info("Burger icon clicked.");
			Thread.sleep(100);
			cm.sidebarReportsOption().click();
			logger.info("Side bar report option clicked.");
			genFunc.ajaxPreloaderWaitNotForMobile();
			Thread.sleep(100);

			for (Object taskDetail : taskDetails) {
				List<String> details = (List<String>) taskDetail;

				cm.reportsPageFilterIcon().click();
				logger.info("Reports page filter option clicked.");
				Thread.sleep(300);
				cm.reportsPageFilterProgramField().clear();
				logger.info("Reports page filter program field cleared.");
				Thread.sleep(200);
				cm.reportsPageFilterProgramField().sendKeys(details.get(2));
				logger.info("Program ID: "+details.get(2)+" pasted on reports page filter program field");
				Thread.sleep(100);
				cm.reportsPageFilterApplyButton().click();
				logger.info("Reports page filter applied.");
				wait = new WebDriverWait(driver, 10);
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(
						"//div[@id='cm_case_reports_processing'][@style='display: block;']//div[@class='ajax_preloader']")));
				Thread.sleep(300);
				String programDetails = cm.reportsPageAllPrograms().get(0).getText();
				System.out.println("Program details at reports page: " + programDetails);
				String programIDCaughtAtReportsPage = programDetails.split(":")[0].substring(1);
				logger.info("Program ID caught at reports page: "+programIDCaughtAtReportsPage);

				try {
					if (programIDCaughtAtReportsPage.equals(details.get(2))) {
						System.out.println("Program ID successfully matched at reports page.");
						List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
								"To verify if CM program id is found at reports page or not",
								"CM program id should be found at reports page", "PASSED",
								"Program ID found at reports page: " + details.get(2)));
						genericTestcase.add(testList);
						break;
					} else {
						List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
								"To verify if CM program id is found at reports page or not",
								"CM program id should be found at reports page", "FAILED",
								"Program ID was not found at reports page: " + details.get(2)));
						genericTestcase.add(testList);
						throw new Exception("Program ID doesn't match at reports page.");
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
					List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
							"To verify if CM program id is found at reports page or not",
							"CM program id should be found at reports page", "FAILED", e.getMessage()));
					genericTestcase.add(testList);
				}
			}

		} catch (Exception e) {
			List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
					"To verify if reports page functionalities are working fine or not",
					"Reports page functionalities should be working fine", "FAILED", "Something went wrong!"));
			genericTestcase.add(testList);
			e.printStackTrace();
		}

	}

	@Test(priority = 11)
	public void Test11_masquerade_switch_back() throws InterruptedException {
		genFunc.switchTab(1);
		logger.info("Tab/window switched to 1.");
		if (guiData.get("userName").toString().trim().equals("")) {
			logger.info("This is an onshore customer. No need to switch back.");
			return;
		}
		Masquerade masq = new Masquerade(driver, configuration, genericXpaths);
		masq.switchBack().click();
		Thread.sleep(100);
		masq.switchBackAgain().click();
		logger.info("Successfully swiched back.");
		genFunc.ajaxPreloaderWait();
		Thread.sleep(100);
	}

	@Test(priority = 12)
	public void Test12_delete_CM_task_id() throws InterruptedException, IOException {
		try {
			genFunc.ajaxPreloaderWait();
			// Removing existing announcement
			if (genFunc.removeAnnouncement()) {
				logger.info("Announcement found and removed.");
			} else {
				logger.info("No announcement found.");
			}

			Thread.sleep(100);
			cozPage.appTray().click();
			logger.info("Clicked on apptray");
			Thread.sleep(100);
			cozPage.supportTools().click();
			logger.info("Clicked on support tools.");
			Thread.sleep(3500);
			genFunc.switchTab(5);
			logger.info("Switched to support tools page.");

			for (Object taskDetail : taskDetails) {
				List<String> details = (List<String>) taskDetail;
				Thread.sleep(1000);

				cozPage.burgerIcon().click();
				logger.info("Burger icon clicked.");
				Thread.sleep(100);
				cozPage.deleteTestingData().click();
				logger.info("Delete testing data option clicked.");
				Thread.sleep(1000);

				if (!guiData.get("userName").toString().trim().equals("")) {
					genFunc.jsClickOnElement(cozPage.masqueradedToCheckbox());
					logger.info("Masqueraded to checkbox clicked.");
					Thread.sleep(100);
					cozPage.submitUsernameField().sendKeys((String) guiData.get("userName"));
					logger.info("Keyword entered on username field: "+guiData.get("userName").toString());
					Thread.sleep(100);
					wait.until(ExpectedConditions.visibilityOfElementLocated(
							By.xpath("//ul[@id='ac-dropdown-logged_or_masquaraded_user_name']")));
					Thread.sleep(100);
					cozPage.findOptionAtautoSuggestionList((String) guiData.get("userName")).click();
					logger.info("Clicked and selected username: "+guiData.get("userName").toString());
					Thread.sleep(100);
				}

				cozPage.selectToCleanDropdown().click();
				logger.info("Select to clean dropdown clicked.");
				Thread.sleep(100);
				cozPage.CMtaskIDoption().click();
				logger.info("CM task option clicked.");
				Thread.sleep(100);
				cozPage.enterCozevaID().sendKeys((String) guiData.get("patientID"));
				logger.info("Patient cozeva ID entered: "+ guiData.get("patientID").toString());
				cozPage.enterTaskID().sendKeys(details.get(1));
				logger.info("Task ID entered: "+ details.get(1));
				cozPage.deleteTestDataButton().click();
				logger.info("Delete test data button clicked.");
				Thread.sleep(100);
				genFunc.ajaxPreloaderModalWait2();

				try {
					cm.taskIdDeletedSuccessfully();
					logger.info("'Deleted Successfully' toast message appears.");
					List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
							"To verify if created task ID is deleted successfully or not.",
							"Created task ID should be deleted.", "PASSED",
							"Task ID: " + details.get(1) + " deleted successfully."));
					genericTestcase.add(testList);
				} catch (NoSuchElementException e) {
					List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
							"To verify if created task ID is deleted successfully or not.",
							"Created task ID should be deleted.", "FAILED", "Something went wrong!"));
					genericTestcase.add(testList);
				}

			}

		} catch (Exception e) {
			List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
					"To verify if created task ID is deleted successfully or not.",
					"Created task ID should be deleted.", "FAILED", "Something went wrong!"));
			genericTestcase.add(testList);
			e.printStackTrace();
		}

	}

	@Test(priority = 13)
	public void Test12_logout_and_close() {
//		try {
//			String baseURL = configuration.getProperty("login_URL").toString().trim().split("com")[0]+"com";
//			String logoutURL = baseURL+"/user/logout";
//			driver.get(logoutURL);
//			logger.info("Logout URL hit: "+logoutURL);
//			driver.quit();
//			logger.info("Driver quit successfully.");
//			
//			List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
//					"To verify if successfully logged out and closed or not.",
//					"Should be logged out and closed successfully.", "PASSED"));
//			genericTestcase.add(testList);
//		} catch (Exception e) {
//			List<String> testList = new ArrayList<String>(Arrays.asList(Integer.toString(testCaseID++),
//					"To verify if successfully logged out and closed or not.",
//					"Should be logged out and closed successfully.", "FAILED", "Something went wrong!"));
//			genericTestcase.add(testList);
//			e.printStackTrace();
//		}
	}
		
	@AfterTest
	public void generate_report_file() throws InterruptedException {
		Excel excelData = new Excel();
		excelData.createNewExcel(report_folder_directory,
				guiData.get("customerID") + " " + guiData.get("userName") + " " + "Generic_CM_verification_report");

		Set<String> testCases = testSuite.keySet();
		logger.info("Excel report generation started...");
		for (String testCase : testCases) {
			excelData.createNewSheet(testCase);
			List<String> headerRow = new ArrayList<String>(
					Arrays.asList("Test ID", "Test Scenario", "Expected Result", "Status", "Comment"));
			excelData.setHeader(headerRow);
			List<Object> testRows = (List<Object>) testSuite.get(testCase);
			for (Object testRow : testRows) {
				List<String> testRowData = (List<String>) testRow;
				excelData.insertRow(testRowData);
			}
		}

		excelData.generateFile();
		logger.info("Excel report generated successfully");
	}

}
