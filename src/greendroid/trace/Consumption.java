/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid.trace;

/**
 *
 * @author User
 */
public class Consumption {
    private long lcd;
    private long cpu;
    private long wifi;
    private long g3;
    private long gps;
    private long audio;
    private double time;
    
    public Consumption(){
        this.lcd = 0;
        this.cpu = 0;
        this.wifi = 0;
        this.g3 = 0;
        this.gps = 0;
        this.audio = 0;
    }
    
    public Consumption(long lcd, long cpu, long wifi, long g3, long gps, long audio, double time) {
        this.lcd = lcd;
        this.cpu = cpu;
        this.wifi = wifi;
        this.g3 = g3;
        this.gps = gps;
        this.audio = audio;
        this.time = time;
    }

    public long getLcd() {
        return lcd;
    }

    public void setLcd(long lcd) {
        this.lcd = lcd;
    }

    public long getCpu() {
        return cpu;
    }

    public void setCpu(long cpu) {
        this.cpu = cpu;
    }

    public long getWifi() {
        return wifi;
    }

    public void setWifi(long wifi) {
        this.wifi = wifi;
    }

    public long getG3() {
        return g3;
    }

    public void setG3(long g3) {
        this.g3 = g3;
    }

    public long getGps() {
        return gps;
    }

    public void setGps(long gps) {
        this.gps = gps;
    }

    public long getAudio() {
        return audio;
    }

    public void setAudio(long audio) {
        this.audio = audio;
    }
    
    public long sum(){
        long ret = lcd + cpu + wifi + g3 + gps + audio;
        return ret;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
    
    public Consumption clone(){
        return new Consumption(lcd, cpu, wifi, g3, gps, audio, time);
    }
}
