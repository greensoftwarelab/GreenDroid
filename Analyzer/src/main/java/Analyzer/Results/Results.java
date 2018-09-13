package Analyzer.Results;




import Analyzer.Consumption;
import Analyzer.Pair;

import java.util.*;

public class Results {

    public Map<Integer,Double> powerSamples = new TreeMap<>();
    public Map<Integer,Integer> timeSamples = new TreeMap<>(); // time samples (of application state) between start/ stop flag (inclusive)
    public Map<Integer,Integer> memorySamples = new TreeMap<>();
    public Map<Integer,Integer> screenBrigthtnessSamples = new TreeMap<>();
    public Map<Integer,Integer> gpuLoadSamples = new TreeMap<>();
    public Map<Integer,Map <Integer,Integer>>  cpuLoadSamples = new TreeMap<>();
    public Map<Integer,Map <Integer,Integer>>  cpuFrequencySamples = new TreeMap<>();
    public Map<Integer,Integer> wifiStateSamples = new TreeMap<>();
    public Map<Integer,Integer> bluetoothStateSamples = new TreeMap<>();
    public Map<Integer,Integer> mobileDataStateSamples = new TreeMap<>();
    public Map<Integer,Integer> gPSStateSamples = new TreeMap<>();
    public Map<Integer,Integer> rSSILevelSamples = new TreeMap<>();
    public Map<Integer,Integer> screenStateSamples = new TreeMap<>();
    public Map<Integer,Integer> batteryStatusSamples = new TreeMap<>();
    public Map<Integer,Integer> batteryRemainingSamples = new TreeMap<>();



    public int time = 0;
    public double totalConsumption = 0;

    public int startTime = -1;
    public int stopTime= -1;

    public boolean hasStartTime(){
        return this.startTime !=-1;
    }

    public boolean hasEndTime(){
        return this.stopTime !=-1;
    }

    public static Double getClosestPower (Map<Integer,Double> timeConsumption, int time){
        // calcular a medida de bateria mais aproximada
        int closestStart = 1000000, closestStop = 1000000;
        int difStart = 1000000, diffEnd = 1000000;
        int alternativeEnd = 0, alternativeStart=0;
        for (Integer i : timeConsumption.keySet()) {
            if(Math.abs(time-i) < difStart){
                difStart = Math.abs(time-i);
                alternativeStart =closestStart;
                closestStart = i;
            }


        }
        return timeConsumption.get(closestStart);
    }


    public double getTotalConsumption(){

        int timeEnd = stopTime;
        int timeStart = startTime;
        // calcular a medida de bateria mais aproximada
        int closestStart = 1000000, closestStop = 1000000;
        int difStart = 1000000, diffEnd = 1000000;
        int alternativeEnd = 0, alternativeStart = 0;
        for (Integer i : powerSamples.keySet()) {
            if (Math.abs(timeStart - i) <= difStart) {
                difStart = Math.abs(timeStart - i);
                alternativeStart = closestStart;
                closestStart = i;
            }
            if (Math.abs(timeEnd - i) < diffEnd) {
                if (i <= timeEnd) {
                    closestStop = i;
                    diffEnd = Math.abs(timeEnd - i);
                } else {
                    alternativeEnd = i;
                }
            }
        }

        double startConsum = ((TreeMap <Integer,Double> ) powerSamples).firstEntry().getValue();
        double stopConsum = ((TreeMap <Integer,Double> ) powerSamples).lastEntry().getValue();
        double total = stopConsum;
        int totaltime = timeEnd  - timeStart;

        // if is a relevant test (in terms of time)
        if (total <= 0 && alternativeEnd != 0 ) {
            stopConsum = powerSamples.get(alternativeEnd);
//                 total = stopConsum-startConsum;.csv
            total = startConsum;
            totaltime = timeEnd - timeStart;
        }
        if (total <= 0 && alternativeStart != 0) {
            startConsum = powerSamples.get(alternativeStart);
//                total = stopConsum-startConsum;
            total = startConsum;
            totaltime = timeEnd - timeStart;
        }


        if (totaltime==0)
            return 0;

        int totalconsum = 0;
        double delta_seconds = 0, watt = 0;
        int toma = 0, delta = 0, ultimo = ((TreeMap<Integer, Double>) powerSamples).firstKey();
        for (Integer i : timeSamples.keySet()) {
            if (i>timeStart && i <= stopTime){
                delta = i-ultimo;
                delta_seconds = ((double) delta / ((double) 1000));
                double closestPowerSample = getClosestPower(powerSamples, i);
                watt = (closestPowerSample) / ((double) 1000000);
                //totalconsum+= (delta * (closestMemMeasure(timeConsumption,toma)));
                totalconsum += (delta_seconds * watt);

            }
            ultimo = i;

        }

        //double watt = (double) total/((double) 1000000);
        this.time=  totaltime;
        this.totalConsumption =  (((double) totaltime / ((double) 1000))) * (watt);
       return ((double) (((double) totaltime / ((double) 1000))) * (watt));
    }


    public boolean bluetoothUsed (){

        for (Integer i :bluetoothStateSamples.values()){
            if (i>0) {
                return true;
            }
        }
        return false;
    }


    public void addCpuFreqSample ( Integer cpuNr, Integer time, Integer freq ) {

        if (this.cpuFrequencySamples.containsKey(cpuNr)){
            this.cpuFrequencySamples.get(cpuNr).put(time,freq );
        }
        else {
            HashMap<Integer,Integer> h = new HashMap<>();
            h.put(time,freq);
            this.cpuFrequencySamples.put(cpuNr,h);
        }
    }

    public void addCpuLoadSample ( Integer cpuNr, Integer time, Integer load ) {

        if (this.cpuLoadSamples.containsKey(cpuNr)){
            this.cpuLoadSamples.get(cpuNr).put(time,load );
        }
        else {
            HashMap<Integer,Integer> h = new HashMap<>();
            h.put(time,load);
            this.cpuLoadSamples.put(cpuNr,h);
        }
    }

    public Pair<Double,Double> getAvgAndTopMemory(){
        int total = 0, top = 0;
        for (Integer mem: this.memorySamples.values()) {
            total += mem;
            top = top > mem ? top : mem;
        }
        return new Pair<>((double) total / (double) memorySamples.size(),(double) top);
    }

    public  Pair<Integer,Integer> getDrainedBattery(){
        return  new Pair<>(((TreeMap<Integer, Integer>) this.batteryRemainingSamples).firstEntry().getValue(),
                ((TreeMap<Integer, Integer>) this.batteryRemainingSamples).firstEntry().getValue()
                    - ((TreeMap<Integer, Integer>) this.batteryRemainingSamples).lastEntry().getValue());
    }

    public double getAvgRSSILevel(){
        int total = 0;
        for (Integer mem: this.rSSILevelSamples.values()) {
            total += mem;
        }
        return ((double) total / (double) memorySamples.size());
    }

    public double getAvgGPULoad(){
        int total = 0;
        for (Integer mem: this.gpuLoadSamples.values()) {
            total += mem;
        }
        return ((double) total / (double) gpuLoadSamples.size());
    }

    public Pair<Double,Double> getAvgAndTopPower(){
        double total = 0, top = 0;
        for (Double pow: this.powerSamples.values()) {
            total += pow;
            top = top > pow ? top : pow;
        }
        return new Pair<>((double) total / (double) powerSamples.size(),(double) top);
    }

    public Pair<Integer,Integer> getBottomAndTopScreenBrigthness(){
        int bottom = 0, top = 0;
        for (Integer mem: this.screenBrigthtnessSamples.values()) {
            bottom  = mem < bottom ? mem : bottom;
            top = top > mem ? top : mem;
        }
        return new Pair<>(bottom, top);
    }

    public Pair<Double,Double> getAvgAndTopCPULoad(){
        double total = 0, top = 0;
        int samples = 0;
        for (Map <Integer,Integer> m : this.cpuLoadSamples.values()) {
            samples += m.size();
            for (Integer load: m.values()) {
                total += load;
                top = top > load ? top : load;
            }
        }
        return new Pair<>((double) total / (double)samples,(double) top);
    }

    public Pair<Double,Double> getAvgAndTopCPUFreq(){
        double total = 0, top = 0;
        int samples = 0;
        for (Map <Integer,Integer> m : this.cpuFrequencySamples.values()) {
            samples += m.size();
            for (Integer load: m.values()) {
                total += load;
                top = top > load ? top : load;
            }
        }


        return new Pair<>((double) total / (double)samples,(double) top);
    }

    public boolean wifiUsed (){

        for (Integer i :wifiStateSamples.values()){
            if (i>0) {
                return true;
            }
        }
        return false;
    }

    public boolean mobileDataUsed (){

        for (Integer i :mobileDataStateSamples.values()){
            if (i>0) {
                return true;
            }
        }
        return false;
    }

    public boolean batteryCharging (){

        for (Integer i :batteryStatusSamples.values()){
            if (i>0) {
                return true;
            }
        }
        return false;
    }


    public boolean gpsUsed (){

        for (Integer i :gPSStateSamples.values()){
            if (i>0) {
                return true;
            }
        }
        return false;
    }

    public boolean screenUsed (){

        for (Integer i :this.screenStateSamples.values()){
            if (i>0) {
                return true;
            }
        }
        return false;
    }





    public Consumption getResults() {
        Consumption c = new Consumption();

        //TODO

        return c;

    }
}