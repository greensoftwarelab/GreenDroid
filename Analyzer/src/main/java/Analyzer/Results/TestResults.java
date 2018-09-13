package Analyzer.Results;

import Analyzer.Analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestResults extends Results {


    public Map <String, Integer >  invokedMethods = new HashMap<>();
    public String testId = "";
    public String testName = "";
    public List<String> metricsName = new ArrayList<>();




    public TestResults(){
        super();
        metricsName.add("Test Number");metricsName.add("Test Name");metricsName.add("Consumption (J)");
        metricsName.add("Time (ms)"); metricsName.add("Method coverage (%)");metricsName.add("Wifi?");
        metricsName.add("Mobile Data?");metricsName.add("Screen state");metricsName.add("Battery Charging?");
        metricsName.add("Avg RSSI Level");metricsName.add("Avg Mem Usage");metricsName.add("Top Mem Usage");
        metricsName.add("Bluetooth?");metricsName.add("Avg gpu Load");metricsName.add("Avg CPU Load");
        metricsName.add("Max CPU Load");metricsName.add("GPS?");

    }




    public double getCoverage(){

        return (double) invokedMethods.keySet().size() / ((double) Analyzer.allmethods.size() );
    }

    public String [] showGlobalData() {
        // conforme metrics name in
        String [] finalReturnList = new String[metricsName.size()];


        finalReturnList[0] = testId;
        finalReturnList[1] = testName;
        finalReturnList[2] =  String.valueOf( totalConsumption);
        finalReturnList[3] =  String.valueOf(time);
        finalReturnList[4] = String.valueOf(getCoverage() * 100);
        finalReturnList[5] =  String.valueOf( wifiUsed());
        finalReturnList[6] =  String.valueOf( mobileDataUsed());
        finalReturnList[7] =  String.valueOf( screenUsed());
        finalReturnList[8] =  String.valueOf( batteryCharging());
        finalReturnList[9] =  String.valueOf( getAvgRSSILevel());
        finalReturnList[10] =  String.valueOf( getAvgAndTopMemory().first);
        finalReturnList[11] =  String.valueOf( getAvgAndTopMemory().second);
        finalReturnList[12] =  String.valueOf( bluetoothUsed());
        finalReturnList[13] =  String.valueOf( getAvgGPULoad());
        finalReturnList[14] =  String.valueOf( getAvgAndTopCPULoad().first);
        finalReturnList[15] =  String.valueOf( getAvgAndTopCPULoad().second);
        finalReturnList[16] =  String.valueOf(gpsUsed());


        return finalReturnList;

    }


}

