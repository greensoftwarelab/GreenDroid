/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid;

import instrumentation.util.XMLParser;


/**
 *
 * @author User
 */
public class Project {
    private String pack;
    private String testPackage;
    private String name;
    private String pathProject;
    private String pathTests;
    private String transPath;
    private String transTestsPath;
    

    public Project() {
        this.pack = "";
        this.testPackage = "";
        this.pathProject = "";
        this.pathTests = "";
        this.name = "";
    }

    public Project(String name, String pack, String pathP, String pathT, String tName) {
        this.pack = pack;
        this.testPackage = XMLParser.getXmlPropertyValue("manifest", "package", pathT+"AndroidManifest.xml");
        this.name = name;
        this.pathProject = pathP;
        this.pathTests = pathT;
        this.transPath = pathProject+tName; //+"/";
        this.transTestsPath = transPath+"/tests"; //+"/";
    }

    public String getPackage() {
        return pack;
    }

    public void setPackage(String pack) {
        this.pack = pack;
    }

    public String getTestPackage() {
        return testPackage;
    }

    public void setTestPackage(String testPack) {
        this.testPackage = testPack;
    }

    public String getPathProject() {
        return pathProject;
    }

    public void setPathProject(String path) {
        this.pathProject = path;
    }

    public String getPathTests() {
        return pathTests;
    }

    public void setPathTests(String path) {
        this.pathTests = path;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransPath() {
        return transPath;
    }

    public void setTransPath(String transPath) {
        this.transPath = transPath;
    }

    public String getTransTestsPath() {
        return transTestsPath;
    }

    public void setTransTestsPath(String transTestsPath) {
        this.transTestsPath = transTestsPath;
    }
    
}
