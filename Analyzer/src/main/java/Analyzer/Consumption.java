package Analyzer;

/**
 * Created by rrua on 21/03/17.
 */
public class Consumption {

    private int timecpu1Freq;
    private int cpu1Freq;
    private int timecpu1Load;
    private int cpu1Load;
    private int timeMemUsage;
    private int memUsage;
    private int timeMobileDataState;
    private int mobileDataState;
    private int timecpu2Freq;
    private int cpu2Freq;
    private int timecpu2Load;
    private int cpu2Load;
    private int timeWifiState;
    private int wifiState;
    private int timecpu3Freq;
    private int cpu3Freq;
    private int timecpu3Load;
    private int cpu3Load;
    private int timewifiRSSILevel;
    private int wifiRSSILevel;
    private int timecpu4Freq;
    private int cpu4Freq;
    private int timecpu4Load;
    private int cpu4Load;
    private int timeScreenBrightness;
    private int ScreenBrightness;
    private int timeScreenState;
    private int ScreenState;
    private int timeBatteryPowerRaw;
    private int batteryPowerRaw; // no ketchup, just sauce, raw sauce
    private int batteryPowerDelta;
    private int timeBatteryRemaining;
    private int batteryRemaining;
    private int timeBatteryStatus;
    private int batteryStatus;
    private int timeBluetoothState;
    private int bluetoothState;
    private int timeGPUFreq;
    private int gpuFreq;
    private int timeGPULoad;
    private int gpuLoad;
    private int timeGPSState;
    private int gpsState;
    private int timecpuLoad;
    private int cpuLoad;
    private int timeCpuLoadNormalized;
    private int cpuLoadNormalized;
    private int timeState;
    private int state;
    private String runningMethod; //description
    //
    public int index;
    private double consumption;



    public Consumption(int memUsage, int mobileDataState, int wifiState, int wifiRSSILevel, int screenState, int batteryPowerDelta, int batteryRemaining, int batteryStatus, int bluetoothState, int gpuLoad, int gpsState, int cpuLoadNormalized) {
        this.memUsage = memUsage;
        this.mobileDataState = mobileDataState;
        this.wifiState = wifiState;
        this.wifiRSSILevel = wifiRSSILevel;
        ScreenState = screenState;
        this.batteryPowerDelta = batteryPowerDelta;
        this.batteryRemaining = batteryRemaining;
        this.batteryStatus = batteryStatus;
        this.bluetoothState = bluetoothState;
        this.gpuLoad = gpuLoad;
        this.gpsState = gpsState;
        this.cpuLoadNormalized = cpuLoadNormalized;
    }


    public int getTimecpu1Freq() {
        return timecpu1Freq;
    }

    public void setTimecpu1Freq(int timecpu1Freq) {
        this.timecpu1Freq = timecpu1Freq;
    }

    public int getCpu1Freq() {
        return cpu1Freq;
    }

    public void setCpu1Freq(int cpu1Freq) {
        this.cpu1Freq = cpu1Freq;
    }

    public int getTimecpu1Load() {
        return timecpu1Load;
    }

    public void setTimecpu1Load(int timecpu1Load) {
        this.timecpu1Load = timecpu1Load;
    }

    public int getCpu1Load() {
        return cpu1Load;
    }

    public void setCpu1Load(int cpu1Load) {
        this.cpu1Load = cpu1Load;
    }

    public int getTimeMemUsage() {
        return timeMemUsage;
    }

    public void setTimeMemUsage(int timeMemUsage) {
        this.timeMemUsage = timeMemUsage;
    }

    public int getMemUsage() {
        return memUsage;
    }

    public void setMemUsage(int memUsage) {
        this.memUsage = memUsage;
    }

    public int getTimeMobileDataState() {
        return timeMobileDataState;
    }

    public void setTimeMobileDataState(int timeMobileDataState) {
        this.timeMobileDataState = timeMobileDataState;
    }

    public int getMobileDataState() {
        return mobileDataState;
    }

    public void setMobileDataState(int mobileDataState) {
        this.mobileDataState = mobileDataState;
    }

    public int getTimecpu2Freq() {
        return timecpu2Freq;
    }

    public void setTimecpu2Freq(int timecpu2Freq) {
        this.timecpu2Freq = timecpu2Freq;
    }

    public int getCpu2Freq() {
        return cpu2Freq;
    }

    public void setCpu2Freq(int cpu2Freq) {
        this.cpu2Freq = cpu2Freq;
    }

    public int getTimecpu2Load() {
        return timecpu2Load;
    }

    public void setTimecpu2Load(int timecpu2Load) {
        this.timecpu2Load = timecpu2Load;
    }

    public int getCpu2Load() {
        return cpu2Load;
    }

    public void setCpu2Load(int cpu2Load) {
        this.cpu2Load = cpu2Load;
    }

    public int getTimeWifiState() {
        return timeWifiState;
    }

    public void setTimeWifiState(int timeWifiState) {
        this.timeWifiState = timeWifiState;
    }

    public int getWifiState() {
        return wifiState;
    }

    public void setWifiState(int wifiState) {
        this.wifiState = wifiState;
    }

    public int getTimecpu3Freq() {
        return timecpu3Freq;
    }

    public void setTimecpu3Freq(int timecpu3Freq) {
        this.timecpu3Freq = timecpu3Freq;
    }

    public int getCpu3Freq() {
        return cpu3Freq;
    }

    public void setCpu3Freq(int cpu3Freq) {
        this.cpu3Freq = cpu3Freq;
    }

    public int getTimecpu3Load() {
        return timecpu3Load;
    }

    public void setTimecpu3Load(int timecpu3Load) {
        this.timecpu3Load = timecpu3Load;
    }

    public int getCpu3Load() {
        return cpu3Load;
    }

    public void setCpu3Load(int cpu3Load) {
        this.cpu3Load = cpu3Load;
    }

    public int getTimewifiRSSILevel() {
        return timewifiRSSILevel;
    }

    public void setTimewifiRSSILevel(int timewifiRSSILevel) {
        this.timewifiRSSILevel = timewifiRSSILevel;
    }

    public int getWifiRSSILevel() {
        return wifiRSSILevel;
    }

    public void setWifiRSSILevel(int wifiRSSILevel) {
        this.wifiRSSILevel = wifiRSSILevel;
    }

    public int getTimecpu4Freq() {
        return timecpu4Freq;
    }

    public void setTimecpu4Freq(int timecpu4Freq) {
        this.timecpu4Freq = timecpu4Freq;
    }

    public int getCpu4Freq() {
        return cpu4Freq;
    }

    public void setCpu4Freq(int cpu4Freq) {
        this.cpu4Freq = cpu4Freq;
    }

    public int getTimecpu4Load() {
        return timecpu4Load;
    }

    public void setTimecpu4Load(int timecpu4Load) {
        this.timecpu4Load = timecpu4Load;
    }

    public int getCpu4Load() {
        return cpu4Load;
    }

    public void setCpu4Load(int cpu4Load) {
        this.cpu4Load = cpu4Load;
    }

    public int getTimeScreenBrightness() {
        return timeScreenBrightness;
    }

    public void setTimeScreenBrightness(int timeScreenBrightness) {
        this.timeScreenBrightness = timeScreenBrightness;
    }

    public int getScreenBrightness() {
        return ScreenBrightness;
    }

    public void setScreenBrightness(int screenBrightness) {
        ScreenBrightness = screenBrightness;
    }

    public int getTimeScreenState() {
        return timeScreenState;
    }

    public void setTimeScreenState(int timeScreenState) {
        this.timeScreenState = timeScreenState;
    }

    public int getScreenState() {
        return ScreenState;
    }

    public void setScreenState(int screenState) {
        ScreenState = screenState;
    }

    public int getTimeBatteryPowerRaw() {
        return timeBatteryPowerRaw;
    }

    public void setTimeBatteryPowerRaw(int timeBatteryPowerRaw) {
        this.timeBatteryPowerRaw = timeBatteryPowerRaw;
    }

    public int getBatteryPowerRaw() {
        return batteryPowerRaw;
    }

    public void setBatteryPowerRaw(int batteryPowerRaw) {
        this.batteryPowerRaw = batteryPowerRaw;
    }

    public int getBatteryPowerDelta() {
        return batteryPowerDelta;
    }

    public void setBatteryPowerDelta(int batteryPowerDelta) {
        this.batteryPowerDelta = batteryPowerDelta;
    }

    public int getTimeBatteryRemaining() {
        return timeBatteryRemaining;
    }

    public void setTimeBatteryRemaining(int timeBatteryRemaining) {
        this.timeBatteryRemaining = timeBatteryRemaining;
    }

    public int getBatteryRemaining() {
        return batteryRemaining;
    }

    public void setBatteryRemaining(int batteryRemaining) {
        this.batteryRemaining = batteryRemaining;
    }

    public int getTimeBatteryStatus() {
        return timeBatteryStatus;
    }

    public void setTimeBatteryStatus(int timeBatteryStatus) {
        this.timeBatteryStatus = timeBatteryStatus;
    }

    public int getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(int batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public int getTimeBluetoothState() {
        return timeBluetoothState;
    }

    public void setTimeBluetoothState(int timeBluetoothState) {
        this.timeBluetoothState = timeBluetoothState;
    }

    public int getBluetoothState() {
        return bluetoothState;
    }

    public void setBluetoothState(int bluetoothState) {
        this.bluetoothState = bluetoothState;
    }

    public int getTimeGPUFreq() {
        return timeGPUFreq;
    }

    public void setTimeGPUFreq(int timeGPUFreq) {
        this.timeGPUFreq = timeGPUFreq;
    }

    public int getGpuFreq() {
        return gpuFreq;
    }

    public void setGpuFreq(int gpuFreq) {
        this.gpuFreq = gpuFreq;
    }

    public int getTimeGPULoad() {
        return timeGPULoad;
    }

    public void setTimeGPULoad(int timeGPULoad) {
        this.timeGPULoad = timeGPULoad;
    }

    public int getGpuLoad() {
        return gpuLoad;
    }

    public void setGpuLoad(int gpuLoad) {
        this.gpuLoad = gpuLoad;
    }

    public int getTimeGPSState() {
        return timeGPSState;
    }

    public void setTimeGPSState(int timeGPSState) {
        this.timeGPSState = timeGPSState;
    }

    public int getGpsState() {
        return gpsState;
    }

    public void setGpsState(int gpsState) {
        this.gpsState = gpsState;
    }

    public int getTimecpuLoad() {
        return timecpuLoad;
    }

    public void setTimecpuLoad(int timecpuLoad) {
        this.timecpuLoad = timecpuLoad;
    }

    public int getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(int cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public int getTimeCpuLoadNormalized() {
        return timeCpuLoadNormalized;
    }

    public void setTimeCpuLoadNormalized(int timeCpuLoadNormalized) {
        this.timeCpuLoadNormalized = timeCpuLoadNormalized;
    }

    public int getCpuLoadNormalized() {
        return cpuLoadNormalized;
    }

    public void setCpuLoadNormalized(int cpuLoadNormalized) {
        this.cpuLoadNormalized = cpuLoadNormalized;
    }

    public int getState() {
        return state;
    }

    public Consumption(double consumption, int state, String runningMethod, int timeTrepn, int timeBatttery, int deltaBattery, int timeState, int index){
        this.consumption=consumption;
        this.state = state;
        this.runningMethod = new String(runningMethod);
        this.timeBatteryPowerRaw = timeBatttery;
        this.batteryPowerDelta = deltaBattery;
        this.timeState = timeState;
        this.timecpu1Freq = timeTrepn;
        this.index = index;
    }

    public Consumption(){
       this.state=0;

    }


    public Consumption(double consumption, int state, String runningMethod, int timeTrepn, int timeBatttery, int deltaBattery, int timeState){
        this.consumption=consumption;
        this.state = state;
        this.runningMethod = new String(runningMethod);
        this.timeBatteryPowerRaw = timeBatttery;
        this.batteryPowerDelta = deltaBattery;
        this.timeState = timeState;
        this.timecpu1Freq = timeTrepn;
    }


    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    public int isState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getRunningMethod() {
        return runningMethod;
    }

    public void setRunningMethod(String runningMethod) {
        this.runningMethod = runningMethod;
    }

    public int getTime() {
        return timecpu1Freq;
    }

    public void setTime(int time) {
        this.timecpu1Freq = time;
    }


    @Override
    public String toString() {
        return "Battery time:" + timeBatteryPowerRaw +"ms|" + "Method: " + runningMethod + " |" + "Consumption: " +consumption + " uW|" + "State: " + ( state)+ " | index = " +this.index;
    }

    public int getTimeTrepn() {
        return timecpu1Freq;
    }

    public void setTimeTrepn(int timeTrepn) {
        this.timecpu1Freq = timeTrepn;
    }

    public int getTimeBatttery() {
        return timeBatteryPowerRaw;
    }

    public void setTimeBatttery(int timeBatttery) {
        this.timeBatteryPowerRaw = timeBatttery;
    }

    public int getDeltaBattery() {
        return batteryPowerDelta;
    }

    public void setDeltaBattery(int deltaBattery) {
        this.batteryPowerDelta = deltaBattery;
    }

    public int getTimeState() {
        return timeState;
    }

    public void setTimeState(int timeState) {
        this.timeState = timeState;
    }

    @Override
    public boolean equals(Object o) {
        if(o==null) return false;
        if(this==o) return true;
        Consumption c = (Consumption) o;
        if(this.runningMethod.equals(c.getRunningMethod()) && this.timeState == c.getTimeState() && this.consumption==c.consumption)
            return true;
        else return false;
    }
}
