/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst.visitors.utils;

/**
 *
 * @author User
 */
public class ClassDefs {
    private String pack ="";
    private String name = "";
    private String appName = "";
    private boolean isLauncher;
    private boolean isActivity;
    private boolean tests;
    private boolean instrumented;
    private boolean hasStop;
    private boolean suite;
    private boolean setUp;
    private boolean tearDown;
    private boolean after;
    private boolean before;
    private boolean junit4;
    private boolean junit4suite;
    private boolean afterClass;
    private boolean beforeClass;
    private boolean isOther;


    public ClassDefs() {
        this.hasStop = false;
        this.isActivity = false;
        this.isLauncher = false;
        this.after = false;
        this.before = false;
        this.junit4 = false;
        this.junit4suite = false;
    }

    public ClassDefs(String pack, String name, String app) {
        this.pack = pack;
        this.name = name;
        this.appName = app;
        this.hasStop = false;
        this.isActivity = false;
        this.isLauncher = false;
        this.instrumented = false;
        this.setUp = false;
        this.tearDown = false;
        this.suite = false;
        this.tests = false;
        this.after = false;
        this.before = false;
        this.junit4 = false;
        this.junit4suite= false;
    }

    public ClassDefs(String pack, String name) {
        this.pack = pack;
        this.name = name;
//        this.appName = app;
        this.hasStop = false;
        this.isActivity = false;
        this.isLauncher = false;
        this.instrumented = false;
        this.setUp = false;
        this.tearDown = false;
        this.suite = false;
        this.tests = false;
        this.after = false;
        this.before = false;
        this.junit4 = false;
        this.junit4suite= false;
    }


    public boolean hasTests() {
        return tests;
    }

    public void setTests(boolean tests) {
        this.tests = tests;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isLauncher() {
        return isLauncher;
    }

    public void setLauncher(boolean isLauncher) {
        this.isLauncher = isLauncher;
    }

    public boolean isActivity() {
        return isActivity;
    }

    public void setActivity(boolean isActivity) {
        this.isActivity = isActivity;
    }

    public boolean isInstrumented() {
        return instrumented;
    }

    public void setInstrumented(boolean instrumented) {
        this.instrumented = instrumented;
    }

    public boolean hasStop() {
        return hasStop;
    }

    public void setStop(boolean hasStop) {
        this.hasStop = hasStop;
    }
    
    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSuite() {
        return suite;
    }

    public void setSuite(boolean suite) {
        this.suite = suite;
    }

    public boolean hasSetUp() {
        return setUp;
    }

    public void setSetUp(boolean setUp) {
        this.setUp = setUp;
    }

    public boolean hasTearDown() {
        return tearDown;
    }

    public void setTearDown(boolean tearDown) {
        this.tearDown = tearDown;
    }
    
    
    public String getDescriptor(){
        return "<"+pack+">"+name;
    }

    public boolean hasBefore() {
        return before;
    }

    public void setBefore(boolean before) {
        this.before = before;
    }

    public boolean hasAfter() {
        return after;
    }

    public void setAfter(boolean after) {
        this.after = after;
    }

    public boolean isJunit4() {
        return junit4;
    }

    public void setJunit4(boolean junit4) {
        this.junit4 = junit4;
    }

    public boolean isJunit4suite() {
        return junit4suite;
    }

    public void setJunit4suite(boolean junit4suite) {
        this.junit4suite = junit4suite;
    }

    public boolean isAfterClass() {
        return afterClass;
    }

    public boolean isBeforeClass() {
        return beforeClass;
    }

    public void setBeforeClass(boolean beforeClass) {
        this.beforeClass = beforeClass;
    }

    public void setAfterClass(boolean afterClass) {
        this.afterClass = afterClass;
    }

    public boolean isOther() {
        return isOther;
    }

    public void setOther(boolean other) {
        isOther = other;
    }


    @Override
    public boolean equals(Object o) {
        if (o==null) return false;
        if(o==this) return true;
        else {
            ClassDefs c = (ClassDefs) o;
            return this.getName().equals(c.name) && this.pack.equals(c.getPack());
        }
    }
}
