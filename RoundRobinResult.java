import java.util.HashMap;

public class RoundRobinResult {
    private final HashMap<String, int[]> timeValues;
    private final double avTotalTAT;
    private final double avTotalWT;
    private final int totalTAT;
    private final int totalWT;
    private final String ganttChart;
    private final String finalgctime;

    public RoundRobinResult(HashMap<String, int[]> timeValues, double avTotalTAT, double avTotalWT, int totalTAT, int totalWT, String ganttChart, String finalgctime) {
        this.timeValues = timeValues;
        this.avTotalTAT = avTotalTAT;
        this.avTotalWT = avTotalWT;
        this.totalTAT = totalTAT;
        this.totalWT = totalWT;
        this.ganttChart = ganttChart;
        this.finalgctime = finalgctime;
    }

    public HashMap<String, int[]> getTimeValues() {
        return timeValues;
    }

    public double getavTotalTAT() {
        return avTotalTAT;
    }

    public double getavTotalWT() {
        return avTotalWT;
    }

    public int getTotalTAT() {
        return totalTAT;
    }

    public int getTotalWT() {
        return totalWT;
    }

    public String getganttChart() {
        return ganttChart;
    }

    public String getfinalgctime() {
        return finalgctime;
    }
}