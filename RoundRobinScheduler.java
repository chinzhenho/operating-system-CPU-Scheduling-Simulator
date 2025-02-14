import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;

public class RoundRobinScheduler {
    private final int timeQuantum;
    private final List<String> ganttChart = new ArrayList<>();
    private final List<Integer> timeMarkers = new ArrayList<>();
    private int totalTurnaroundTime = 0;
    private int totalWaitingTime = 0;

    public RoundRobinScheduler(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

    public int getTimeQuantum() {
        return timeQuantum;
    }

    public void schedule(List<Process> processes) {
        Queue<Process> queue = new LinkedList<>();
        int currentTime = 0;

        // Sort processes by arrival time only (ignore priority)
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int index = 0;
        while (index < processes.size() || !queue.isEmpty()) {
            // Add processes to the queue based on arrival time
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                queue.add(processes.get(index));
                index++;
            }

            if (!queue.isEmpty()) {
                Process currentProcess = queue.poll();

                if (currentProcess.remainingTime <= timeQuantum) {
                    currentTime += currentProcess.remainingTime;
                    currentProcess.remainingTime = 0;
                    currentProcess.finishingTime = currentTime;
                    ganttChart.add("P" + currentProcess.processID);
                    timeMarkers.add(currentTime);
                } else {
                    currentTime += timeQuantum;
                    currentProcess.remainingTime -= timeQuantum;
                    ganttChart.add("P" + currentProcess.processID);
                    timeMarkers.add(currentTime);
                    queue.add(currentProcess);
                }
            } else {
                currentTime++;
            }
        }

        // Calculate total turnaround time and waiting time
        for (Process process : processes) {
            int turnaroundTime = process.finishingTime - process.arrivalTime;
            int waitingTime = turnaroundTime - process.burstTime;
            totalTurnaroundTime += turnaroundTime;
            totalWaitingTime += waitingTime;
        }
    }

    public RoundRobinResult RoundRobin(int ProcessNum, int quantumTime, int burstTime[], int arrivalTime[]) {
        int[] tat = new int[ProcessNum];
        int[] wt = new int[ProcessNum];
        Process[] processes = new Process[ProcessNum];
        String ganttChart = "| ";

        for (int i = 0; i < ProcessNum; i++) {
            processes[i] = new Process(i, arrivalTime[i], burstTime[i], 0);
        }

        // Sort processes based on arrival time
        Arrays.sort(processes, Comparator.comparingInt(p -> p.arrivalTime));

        // Calculate the TAT and WT
        int totalTAT = 0;
        int totalWT = 0;

        int[] remainingTime = new int[ProcessNum];
        for (int i = 0; i < ProcessNum; i++) {
            remainingTime[i] = processes[i].burstTime;
        }
        int totalburstTime = 0;
        for (int i = 0; i < ProcessNum; i++) {
            totalburstTime += processes[i].burstTime;
        }
        ArrayList<Integer> processqueue = new ArrayList<>();
        ArrayList<Integer> timebefore = new ArrayList<>();
        timebefore.add(0);
        ArrayList<Integer> timeafter = new ArrayList<>();
        if (processes[0].burstTime < quantumTime) {
            timeafter.add(processes[0].burstTime);
        } else {
            timeafter.add(quantumTime);
        }
        ArrayList<Integer> finishTime = new ArrayList<>();
        for (int j = 0; j < ProcessNum; j++) {
            finishTime.add(0);
        }
        for (int j = 0; j < ProcessNum; j++) {
            wt[j] = 0;
        }
        for (int j = 0; j < ProcessNum; j++) {
            tat[j] = 0;
        }
        ArrayList<Integer> ganttChartTime = new ArrayList<>();
        ganttChartTime.add(0);

        int currentTime = 0;
        int k = 0;
        int t = 0;
        while (true) {
            boolean allProcessesComplete = true;

            for (int o = 0; o < ProcessNum; o++) {
                if (processes[o].arrivalTime <= timeafter.get(t) && processes[o].arrivalTime >= timebefore.get(t)) {
                    if (remainingTime[o] > 0) {
                        allProcessesComplete = false;
                        if (remainingTime[o] > quantumTime) {
                            currentTime += quantumTime;
                            ganttChartTime.add(currentTime);
                            remainingTime[o] -= quantumTime;
                            ganttChart += "P" + processes[o].processID + " | ";
                            timebefore.add(timeafter.get(timeafter.size() - 1) + 1);
                            timeafter.add(timeafter.get(timeafter.size() - 1) + quantumTime);
                            processqueue.add(o);
                            continue;
                        } else {
                            currentTime += remainingTime[o];
                            ganttChartTime.add(currentTime);
                            tat[processes[o].processID] = currentTime - processes[o].arrivalTime;
                            remainingTime[o] = 0;
                            wt[processes[o].processID] = tat[processes[o].processID] - processes[o].burstTime;
                            processes[o].finishingTime = currentTime; // Update finishing time
                            ganttChart += "P" + processes[o].processID + " | ";
                            timebefore.add(timeafter.get(timeafter.size() - 1) + 1);
                            timeafter.add(timeafter.get(timeafter.size() - 1) + processes[o].burstTime);
                            finishTime.set(o, currentTime); // Update finish time in the list
                            continue;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (k > (processqueue.size() - 1)) {
                k = 0;
            }

            if ((remainingTime[processqueue.get(k)] > 0)) {
                allProcessesComplete = false;
                if (remainingTime[processqueue.get(k)] > quantumTime) {
                    currentTime += quantumTime;
                    ganttChartTime.add(currentTime);
                    remainingTime[processqueue.get(k)] -= quantumTime;
                    ganttChart += "P" + processes[processqueue.get(k)].processID + " | ";
                    timebefore.add(timeafter.get(timeafter.size() - 1) + 1);
                    timeafter.add(timeafter.get(timeafter.size() - 1) + quantumTime);
                    processqueue.add(processqueue.get(k));
                    k = k + 1;
                    t = t + 1;
                    continue;
                } else {
                    currentTime += remainingTime[processqueue.get(k)];
                    ganttChartTime.add(currentTime);
                    tat[processes[processqueue.get(k)].processID] = currentTime - processes[processqueue.get(k)].arrivalTime;
                    remainingTime[processqueue.get(k)] = 0;
                    wt[processes[processqueue.get(k)].processID] = tat[processes[processqueue.get(k)].processID] - processes[processqueue.get(k)].burstTime;
                    processes[processqueue.get(k)].finishingTime = currentTime; // Update finishing time
                    ganttChart += "P" + processes[processqueue.get(k)].processID + " | ";
                    timebefore.add(timeafter.get(timeafter.size() - 1) + 1);
                    timeafter.add(timeafter.get(timeafter.size() - 1) + processes[processqueue.get(k)].burstTime);
                    finishTime.set(processqueue.get(k), currentTime); // Update finish time in the list
                    k = k + 1;
                    t = t + 1;
                    continue;
                }
            }

            if (currentTime == totalburstTime) {
                allProcessesComplete = true;
            }

            if (allProcessesComplete) {
                break;
            }
        }
        ganttChart += " |";

        ganttChart = ganttChart.substring(0, ganttChart.length() - 2);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ganttChartTime.size(); i++) {
            sb.append(ganttChartTime.get(i));
            if (ganttChartTime.get(i) > 9 && i < (ganttChartTime.size() - 1)) {
                sb.append("   ");
            } else {
                sb.append("    ");
            }
        }
        Arrays.sort(processes, Comparator.comparingInt(p -> p.processID));

        String finalgctime = sb.toString();

        // Check if both ArrayLists have the same size
        if (burstTime.length != arrivalTime.length) {
            System.out.println("ArrayLists have different sizes. Cannot create table.");
            return null;
        }

        for (int i = 0; i < ProcessNum; i++) {
            totalTAT += tat[i];
        }

        for (int i = 0; i < ProcessNum; i++) {
            totalWT += wt[i];
        }
        double avtotalTAT = (double) totalTAT / ProcessNum;
        double avtotalWT = (double) totalWT / ProcessNum;

        int[] finishTimeArray = finishTime.stream().mapToInt(Integer::intValue).toArray();

        HashMap<String, int[]> timeValues = new HashMap<>();
        timeValues.put("burstTime", burstTime);
        timeValues.put("arrivalTime", arrivalTime);
        timeValues.put("finishTime", finishTimeArray);
        timeValues.put("wt", wt);
        timeValues.put("tat", tat);

        return new RoundRobinResult(timeValues, avtotalTAT, avtotalWT, totalTAT, totalWT, ganttChart, finalgctime);
    }

    public void printHashMapValues(HashMap<String, int[]> map) {
        for (Map.Entry<String, int[]> entry : map.entrySet()) {
            String key = entry.getKey();
            int[] values = entry.getValue();

            System.out.println("Key: " + key);
            System.out.print("Values: ");
            for (int value : values) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    public void showResults(RoundRobinResult result) {
        JFrame resultsFrame = new JFrame("Round Robin Results - Averages and Gantt Chart");
        resultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultsFrame.setSize(600, 400);
        resultsFrame.setLayout(new BorderLayout());

        // Averages
        double avgTurnaroundTime = result.getavTotalTAT();
        double avgWaitingTime = result.getavTotalWT();

        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setText("Average Turnaround Time: " + avgTurnaroundTime + "\n");
        resultArea.append("Average Waiting Time: " + avgWaitingTime + "\n\n");

        // Gantt Chart representation
        resultArea.append("Gantt Chart:\n" + result.getganttChart() + "\n");
        resultArea.append("Time Markers: " + result.getfinalgctime() + "\n\n");

        // Simple Gantt Chart representation
        resultArea.append("Simple Gantt Chart:\n");
        for (String process : result.getganttChart().split(" \\| ")) {
            resultArea.append(process + " | ");
        }
        resultArea.append("\n");
        
        String[] timeMarkers = result.getfinalgctime().split("\\s+");
for (int i = 0; i < timeMarkers.length; i++) {
    resultArea.append(timeMarkers[i]);
    if (i < timeMarkers.length - 1) {
        resultArea.append("    ");
    }
}
resultArea.append("\n");

        resultsFrame.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        resultsFrame.setVisible(true);
    }
}