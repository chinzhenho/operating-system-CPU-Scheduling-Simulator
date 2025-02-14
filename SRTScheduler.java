import java.util.*;

// Main class for the Shortest Remaining Time (SRT) Scheduler.
public class SRTScheduler {

    // Variables to store total turnaround and waiting times for all processes
    private double totalTurnaroundTime = 0;
    private double totalWaitingTime = 0;

    // List to maintain the Gantt chart representation of the schedule
    private final List<String> ganttChartProcesses = new ArrayList<>();

    // List to store time markers for the Gantt chart timeline
    private final List<Integer> timeMarkers = new ArrayList<>();

    // Method to perform scheduling Shortest Remaining Time (SRT) algorithm
    public void schedule(List<Process> processes) {
        int currentTime = 0; // Initialize the current time to zero

        // Priority queue to select the process with the shortest remaining burst time
        // first
        Queue<Process> processReadyQueue = new PriorityQueue<>(Comparator.comparingInt((Process p) -> p.remainingTime)
                // Compare by remainning burst time
                .thenComparingInt(p -> p.arrivalTime)); // If remaining times are equal, compare by arrival time

        boolean allProcessesHandled = false; // Flag to indicate if all processes are scheduled
        timeMarkers.add(currentTime); // Add the initial start time to the timeline

        // Loop until all processes are handled
        while (!allProcessesHandled) {
            // Add processes to the ready queue if they have arrived and are not yet
            // completed
            for (Process process : processes) {
                if (!processReadyQueue.contains(process) && process.remainingTime > 0
                        && process.arrivalTime <= currentTime) {
                    processReadyQueue.add(process);
                }
            }

            if (!processReadyQueue.isEmpty()) { // If the ready queue has processes

                // Select the process with the shortest remaining burst time
                Process currentProcess = processReadyQueue.poll();

                // Add the process to the Gantt chart
                ganttChartProcesses.add("P" + currentProcess.processID);

                // Update the current time by incrementing it by 1
                currentTime++;

                // Add the current time to the time markers after executing the process
                timeMarkers.add(currentTime);

                // Update the remaining time of the selected process
                currentProcess.remainingTime--;

                // If the process is finished, calculate finishing time, turnaround time, and
                // waiting time
                if (currentProcess.remainingTime == 0) {
                    currentProcess.finishingTime = currentTime;
                    int turnaroundTime = currentProcess.finishingTime - currentProcess.arrivalTime; // Total time from
                                                                                                    // arrival to
                                                                                                    // completion
                    int waitingTime = turnaroundTime - currentProcess.burstTime; // Time spent waiting in the ready
                                                                                 // queue

                    // calculate the turnaround and waiting times
                    totalTurnaroundTime += turnaroundTime;
                    totalWaitingTime += waitingTime;
                }

            } else {
                // If no process is ready, increment the current time
                currentTime++;

                // Add an idle time marker to the timeline
                timeMarkers.add(currentTime);
            }

            // Check if all processes are completed
            allProcessesHandled = processes.stream().allMatch(p -> p.remainingTime == 0);
        }
    }

    // Method to calculate and return the average turnaround time
    public double getAverageTurnaroundTime(int numProcesses) {
        return totalTurnaroundTime / numProcesses; // Divide total turnaround time by the number of processes
    }

    // Method to calculate and return the average waiting time
    public double getAverageWaitingTime(int numProcesses) {
        return totalWaitingTime / numProcesses; // Divide total waiting time by the number of processes
    }

    // Method to format and return the Gantt chart representation
    public String getFormattedGanttChart() {
        StringBuilder chartLine = new StringBuilder(); // Line for process names
        StringBuilder timeLine = new StringBuilder(); // Line for time markers

        // Initialize Gantt chart formatting for process names (fixed-width for
        // alignment)
        chartLine.append("|");
        for (String process : ganttChartProcesses) {
            chartLine.append(String.format(" %-7s|", process)); // Format process name to take 7 characters
        }

        // Append aligned time markers below the Gantt chart
        timeLine.append(" "); // Add one space before the first number
        for (int i = 0; i < timeMarkers.size(); i++) {
            int marker = timeMarkers.get(i);

            // For one-digit and two-digit markers, use 10 spaces for consistency
            if (marker < 10) {
                timeLine.append(String.format("%-10d", marker)); // 10 spaces for one-digit
            } else {
                timeLine.append(String.format("%-9d", marker)); // 10 spaces for two-digit markers
            }
        }

        // Return the formatted Gantt chart with aligned process names and time markers
        return chartLine.toString() + "\n" + timeLine.toString();
    }

}
