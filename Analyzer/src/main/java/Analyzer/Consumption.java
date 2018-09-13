package Analyzer;
import Analyzer.Pair;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by rrua on 21/03/17.
 */
public class Consumption {



    public Map<Integer, Pair<Integer,Integer>> cpuFrequencies = new HashMap<Integer, Pair<Integer, Integer>>(); // CPU<nr> -> <time,freq> | 0 -> normalized
    public Map<Integer, Pair<Integer,Integer>> cpuLoads = new HashMap<Integer, Pair<Integer, Integer>>(); // CPU<nr> -> <time,load>
    public Pair<Integer, Integer> memUsage = new Pair<Integer, Integer>(-1,-1); // < absolut time , respective value >
    public Pair<Integer, Integer> mobileDataState = new Pair<Integer, Integer>(-1,-1); // < absolut time , respective value >
    public Pair<Integer, Integer> wifiState = new Pair<Integer, Integer>(-1,-1); // < absolut time , respective value >
    public Pair<Integer, Integer> rssiLevel = new Pair<Integer, Integer>(-1,-1); // < absolut time , respective value >
    public Pair<Integer, Integer> screenBrightness = new Pair<Integer, Integer>(-1,-1); // < absolut time , respective value >

    public Pair<Integer, Integer> screenState = new Pair<Integer, Integer>(-1,-1); // < absolut time , respective value >
    public Pair<Integer, Double> batteryPowerRaw = new Pair<Integer, Double>(-1,-1.0); // < absolut time , respective value >
    public Pair<Integer, Integer> batteryPowerDelta = new Pair<Integer, Integer>(-1,-1); // < absolut time , respective value >
    public Pair<Integer, Integer> batteryStatus = new Pair<Integer, Integer>(-1,-1); // < absolut time , respective value >
    public Pair<Integer, Integer> batteryRemaining = new Pair<Integer, Integer>(-1,-1); // < absolut time , respective value >
    public Pair<Integer, Integer> bluetoothState = new Pair<Integer, Integer>(-1,-1); // < absolut time , respective value >
    public Pair<Integer, Integer> gpuFreq = new Pair<Integer, Integer>(-1,-1); // < absolut time , respective value >
    public Pair<Integer, Integer> gpuLoad = new Pair<Integer, Integer>(-1,-1); // < absolut time , respective value >
    public Pair<Integer, Integer> gpsState = new Pair<Integer, Integer>(-1,-1); // < absolut time , respective value >
    public Pair<Integer, Integer> applicationState = new Pair<Integer, Integer>(-1,0); // < absolut time , respective value >
    private String runningMethod; //description

    public int index;
    private double consumption;




    public Consumption(){

    }



    @Override
    public String toString() {
        return "Battery time:" + batteryPowerRaw.second +"ms|" + "Method: " + runningMethod + " |" + "Consumption: " +consumption + " uW|" + "State: " + ( applicationState)+ " | index = " +this.index;
    }


    @Override
    public boolean equals(Object o) {
        if(o==null) return false;
        if(this==o) return true;
        Consumption c = (Consumption) o;
        if(this.runningMethod.equals(c.runningMethod) && this.applicationState.first == c.applicationState.first && this.consumption==c.consumption)
            return true;
        else return false;
    }
}
