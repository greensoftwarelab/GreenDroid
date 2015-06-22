/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid.tools;

/**
 *
 * @author User
 */
public class Config {

    private String javaPath;
    private String antPath;
    private String androidPath;
    private String localResDir;
    private String deviceResDir;

    public Config(){
        this.javaPath = "";
        this.antPath = "";
        this.androidPath = "";
        this.localResDir = "";
        this.deviceResDir = "";
    }
    
    public Config(String javaPath, String antPath, String androidPath, String localResDir, String deviceResDir) {
        this.javaPath = javaPath;
        this.antPath = antPath;
        this.androidPath = androidPath;
        this.localResDir = localResDir;
        this.deviceResDir = deviceResDir;
    }
    
    public String getJavaPath() {
        return javaPath;
    }

    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }

    public String getAntPath() {
        return antPath;
    }

    public void setAntPath(String antPath) {
        this.antPath = antPath;
    }

    public String getAndroidPath() {
        return androidPath;
    }

    public void setAndroidPath(String androidPath) {
        this.androidPath = androidPath;
    }

    public String getLocalResDir() {
        return localResDir;
    }

    public void setLocalResDir(String localResDir) {
        this.localResDir = localResDir;
    }

    public String getDeviceResDir() {
        return deviceResDir;
    }

    public void setDeviceResDir(String deviceResDir) {
        this.deviceResDir = deviceResDir;
    }
    
    
}
