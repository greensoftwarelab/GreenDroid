/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid.project;

/**
 *
 * @author marco
 */
public class TestResult {
    private long totalConsumption;
    private double time;

    public TestResult() {
        this.totalConsumption=0;
        this.time=0.0;
    }

    public TestResult(long totalConsumption, double time) {
        this.totalConsumption = totalConsumption;
        this.time = time;
    }

    public long getTotalConsumption() {
        return totalConsumption;
    }

    public void setTotalConsumption(long totalConsumption) {
        this.totalConsumption = totalConsumption;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    @Override
    protected TestResult clone() {
        return new TestResult(totalConsumption, time);
    }
    
}
