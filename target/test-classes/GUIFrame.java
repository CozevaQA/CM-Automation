package Resources;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GUIFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFrame uiFrame;
	private JTextField custID;
	private JTextField user;
	private JTextField patient;
	private JComboBox selectProgram;
	private JComboBox customerNames;
	private JComboBox selectTask;
	private JPanel panel;
	private boolean submitted = false;
	private Map<String, Object> programToTask;

	public GUIFrame(Map<String, Object> programToTask, List<Object> customerDB) {
		this.programToTask=programToTask;
		uiFrame = new JFrame("CM AUTOMATION");
		
		List<String> customerList = new ArrayList<>();
		for(Object customer: customerDB) {
			List<String> customerdetail = (ArrayList<String>)customer;
			customerList.add(customerdetail.get(0));
		}
		customerList.add(0, "Select");
		String[] customerNameList = new String[customerList.size()];
		customerList.toArray(customerNameList);
		
		JLabel customerID = new JLabel("Customer ID: ");
		//customerID.setLocation(50, 100);
		JLabel userName = new JLabel("Username: ");
		//userName.setLocation(50, 200);
		JLabel patientID = new JLabel("Patient ID: ");
		//patientID.setLocation(50, 300);

		custID = new JTextField(20);
		//custID.setLocation(55, 100);
		user = new JTextField(20);
		//user.setLocation(55, 200);
		patient = new JTextField(20);
		//patient.setLocation(55, 300);

		JButton submit = new JButton("SUBMIT");
		
		String programs[] = { "Select", "Care Transitions", "Care Management", "Care Coordination", "Screening Tools" };
		selectProgram = new JComboBox<>(programs);
		customerNames = new JComboBox<>(customerNameList);
		selectTask = new JComboBox<>();
		Dimension d = new Dimension(100, 20);
		selectTask.setSize(200, 20);
		
		uiFrame.add(new JLabel("Customer: "));
		customerNames.setPreferredSize(new Dimension(180, 20));
		uiFrame.add(customerNames);
		
		uiFrame.add(customerID);
		uiFrame.add(custID);
		uiFrame.add(userName);
		uiFrame.add(user);
		uiFrame.add(patientID);
		uiFrame.add(patient);
		
		uiFrame.add(new JLabel("Select program: "));
		uiFrame.add(selectProgram);
		
		uiFrame.add(new JLabel("Select task: "));
//		uiFrame.add(selectTask);
		
		panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        uiFrame.getContentPane().add(panel);
        
        uiFrame.add(submit);
		
		uiFrame.setVisible(true);
		uiFrame.setSize(320, 600);
		uiFrame.setResizable(false);
		uiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		uiFrame.setLayout(new FlowLayout());

		submit.addActionListener(ae -> {
			submitted = true;
		});
		
		selectProgram.addItemListener(ie -> {
			String selectedProgram = (String) selectProgram.getSelectedItem();
			
			if(selectedProgram.equals("Care Transitions")) {
				removeCheckboxes(panel);
				selectTask.removeAllItems();
				List<String> taskList = (List<String>) programToTask.get(removeSpaceFromString(selectedProgram));
				for(String task: taskList) {
					selectTask.addItem(task);
					
					JCheckBox checkBox = new JCheckBox(task);
			        panel.add(checkBox);
				}
			}
			else if(selectedProgram.equals("Care Management")) {
				removeCheckboxes(panel);
				selectTask.removeAllItems();
				List<String> taskList = (List<String>) programToTask.get(removeSpaceFromString(selectedProgram));
				for(String task: taskList) {
					selectTask.addItem(task);
					
					JCheckBox checkBox = new JCheckBox(task);
			        panel.add(checkBox);
				}
			}
			else if(selectedProgram.equals("Care Coordination")) {
				removeCheckboxes(panel);
				selectTask.removeAllItems();
				List<String> taskList = (List<String>) programToTask.get(removeSpaceFromString(selectedProgram));
				for(String task: taskList) {
					selectTask.addItem(task);
					
					JCheckBox checkBox = new JCheckBox(task);
			        panel.add(checkBox);
				}
			}
			else if(selectedProgram.equals("Screening Tools")) {
				removeCheckboxes(panel);
				selectTask.removeAllItems();
				List<String> taskList = (List<String>) programToTask.get(removeSpaceFromString(selectedProgram));
				for(String task: taskList) {
					selectTask.addItem(task);
					
					JCheckBox checkBox = new JCheckBox(task);
			        panel.add(checkBox);
				}
			}
			else {
				removeCheckboxes(panel);
				selectTask.removeAllItems();
			}
		});
		
		
		customerNames.addItemListener(ie -> {
			String selectedCustomer = (String) customerNames.getSelectedItem();
			
			if(selectedCustomer.equals("Select")) {
				custID.setText("");
				user.setText("");
				patient.setText("");
				return;
			}
			
			List<String> customerData = new ArrayList<>();
			
			for(Object customer: customerDB) {
				List<String> data = (ArrayList<String>)customer;
				if(data.get(0).equals(selectedCustomer)) {
					customerData = data;
					break;
				}
			}
			
			
			custID.setText(customerData.get(1).substring(0, 4));
			user.setText(customerData.get(2));
			patient.setText(customerData.get(3));
		});

		
	}
	
	private static void removeCheckboxes(Container container) {
        // Iterate through components and remove checkboxes
        Component[] components = container.getComponents();
        for (Component component : components) {
            if (component instanceof JCheckBox) {
                container.remove(component);
            }
        }

        // Repaint the container
        container.revalidate();
        container.repaint();
    }
	
	private static List<String> getSelectedCheckboxes(Container container) {
		List<String> selectedCheckboxes = new ArrayList<String>();
        // Iterate through components and check selected checkboxes
        Component[] components = container.getComponents();
        for (Component component : components) {
            if (component instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) component;
                if (checkBox.isSelected()) {
                	selectedCheckboxes.add(checkBox.getText());
                }
            }
        }
        return selectedCheckboxes;
    }
	
	
	public Map<String, Object> getDataFromUI() {
		while (!submitted) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Map<String, Object> submittedUIData = new HashMap<>();
		submittedUIData.put("customerID", custID.getText());
		submittedUIData.put("userName", user.getText());
		submittedUIData.put("patientID", patient.getText());
		submittedUIData.put("program", (String)selectProgram.getSelectedItem());
//		submittedUIData.put("task", (String)selectTask.getSelectedItem());
		List<String> selectedCheckboxes = getSelectedCheckboxes(panel);
		submittedUIData.put("task", selectedCheckboxes);
		
		uiFrame.setVisible(false);
		return submittedUIData;
	}
	
	public String removeSpaceFromString(String str) {
		String[] splitStr = str.trim().split(" ");
		String clearStr = "";
		for(String spStr: splitStr) {
			clearStr+=spStr;
		}
		return clearStr;
	}

}
