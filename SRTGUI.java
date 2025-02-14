import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// Main class extending JFrame to create the SRT GUI application
public class SRTGUI extends JFrame {

    // Constructor for SRT GUI application
    public SRTGUI() {
        setTitle("Shortest Remaining Time Scheduler");// set the title of application window

        setSize(600, 400);// set window size

        // make sure the app is close when the window is closed
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create a panel for the title with a gradient background
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Overriding paintComponent to add a gradient background
                super.paintComponent(g);
                Graphics2D gTod = (Graphics2D) g;
                // Defining the gradient colors.
                Color startcolor = new Color(70, 130, 180); // Start color
                Color endcolor = new Color(240, 248, 255); // End color
                GradientPaint gradient = new GradientPaint(0, 0, startcolor, getWidth(), getHeight(), endcolor);
                gTod.setPaint(gradient);
                gTod.fillRect(0, 0, getWidth(), getHeight()); // Fill the panel with the gradient
            }
        };

        // Set layout and preferred size for the title panel
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        // Add title label to the panel.
        JLabel headerLabel = new JLabel("Shortest Remaining Time Scheduler", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22)); // Set the font style and size
        headerLabel.setForeground(Color.WHITE); // Set the text color
        headerPanel.add(headerLabel, BorderLayout.CENTER); // Center the label in the panel

        // Add the title panel to the top of the frame
        add(headerPanel, BorderLayout.NORTH);

        // Create the input field panel for user to input
        JPanel inputFieldPanel = new JPanel(new GridLayout(0, 1, 5, 5)); // GridLayout for vertical alignment
        inputFieldPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); // Add padding around the panel.

        // text field for process input.
        JTextField processInputField = new JTextField();

        // Add label and text field to the input panel
        inputFieldPanel.add(new JLabel("Enter Number of Processes (3-10):"));
        inputFieldPanel.add(processInputField);

        // styled button for proceed to the next step
        JButton nextButton = createStyledButton("Next");
        inputFieldPanel.add(nextButton);

        // Add the input field panel to the center of the frame
        add(inputFieldPanel, BorderLayout.CENTER);

        // Add an action listener to the "Next" button
        nextButton.addActionListener(e -> {
            try {
                // Parsing the number of processes from the input field
                int numProcesses = Integer.parseInt(processInputField.getText());

                // Validate the range of the input is 3 to 10 processes or not
                if (numProcesses < 3 || numProcesses > 10) {
                    JOptionPane.showMessageDialog(this, "Please enter a number of processes between 3 and 10.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return; // if the input is invalid stop further execution
                }

                // list to store process details
                List<Process> processes = new ArrayList<>();

                // Looping through the number of processes to take the input for each process
                for (int i = 0; i < numProcesses; i++) {
                    int burstTime = Integer.parseInt(JOptionPane.showInputDialog("Enter Burst Time for P" + i + ":"));
                    int arrivalTime = Integer
                            .parseInt(JOptionPane.showInputDialog("Enter Arrival Time for P" + i + ":"));
                    int priority = Integer.parseInt(JOptionPane.showInputDialog("Enter Priority for P" + i + ":"));

                    // Add the process details to the list
                    processes.add(new Process(i, arrivalTime, burstTime, priority));
                }

                // Create an instance of the Shortest Remaining Time Scheduler and scheduling
                // the processes
                SRTScheduler srtScheduler = new SRTScheduler();
                srtScheduler.schedule(processes);

                // Display the results in a table format
                showTableGUI(processes, srtScheduler);

                // Close the current input screen when click "Next"
                SwingUtilities.getWindowAncestor(nextButton).dispose();
            } catch (NumberFormatException ex) {
                // Handle invalid input and display error message
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter numeric values.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Make the frame visible
        setVisible(true);
    }

    // Method to display the results in a table format
    private void showTableGUI(List<Process> processes, SRTScheduler srtScheduler) {
        JFrame tableFrame = new JFrame("Shortest Remaining Time Results"); // Create new frame for the results
        tableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Set the close operation
        tableFrame.setSize(800, 500); // Set the frame size

        // Define column names for the table
        String[] columns = { "Process", "Arrival Time", "Burst Time", "Priority", "Finishing Time", "Turnaround Time",
                "Waiting Time" };
        DefaultTableModel model = new DefaultTableModel(columns, 0); // Create table model with the columns
        JTable processResultTable = new JTable(model); // Create a table with the model

        // Populating the table with process data
        for (Process process : processes) {
            int turnaroundTime = process.finishingTime - process.arrivalTime; // Calculation of turnaround time
            int waitingTime = turnaroundTime - process.burstTime; // Calculation of waiting time
            model.addRow(new Object[] {
                    "P" + process.processID,
                    process.arrivalTime,
                    process.burstTime,
                    process.priority,
                    process.finishingTime,
                    turnaroundTime,
                    waitingTime
            });
        }

        // Add a scroll pane to the table
        JScrollPane scrollPane = new JScrollPane(processResultTable);
        tableFrame.add(scrollPane, BorderLayout.CENTER);

        // Create text area for additional results
        JTextArea resultTextArea = new JTextArea();
        resultTextArea.setEditable(false); // Make the text area non-editable

        // Get the number of processes
        int numProcesses = processes.size(); // Pass the number of processes

        // Calculate and display average turnaround and waiting times
        String averageTurnaroundTimeStr = String.format("%.2f", srtScheduler.getAverageTurnaroundTime(numProcesses));
        String averageWaitingTimeStr = String.format("%.2f", srtScheduler.getAverageWaitingTime(numProcesses));
        resultTextArea.setText("Average Turnaround Time: " + averageTurnaroundTimeStr + "\n");
        resultTextArea.append("Average Waiting Time: " + averageWaitingTimeStr + "\n\n");
        resultTextArea.append("Gantt Chart:\n" + srtScheduler.getFormattedGanttChart()); // Display the Gantt chart

        // Add the text area to the bottom of the frame
        tableFrame.add(resultTextArea, BorderLayout.SOUTH);

        // Make the results frame visible
        tableFrame.setVisible(true);
    }

    // Method to create a styled button
    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text); // Create a button with the given text
        button.setFocusPainted(false); // Disabling focus painting
        button.setFont(new Font("Arial", Font.PLAIN, 18)); // Set the font style and size
        button.setBackground(new Color(70, 130, 180)); // Set the background color
        button.setForeground(Color.WHITE); // Set the text color
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 70, 120), 2), // Add line border
                BorderFactory.createEmptyBorder(10, 15, 10, 15) // Add padding inside the border
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Changing the cursor to a hand icon when hovered
        return button; // Returning the styled button.
    }

    // Main method to run the app
    public static void main(String[] args) {
        new SRTGUI(); // Create an instance of the SRT GUI
    }
}
