/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid.project;

//import jInst.util.XMLParser;

import greendroid.ProjectAnalyzer;
import greendroid.tools.XMLParser;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



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
    private ProjectAnalyzer analyzer;
    private Map<Integer, TestResult> testsResults;
    

    public Project() {
        this.pack = "";
        this.testPackage = "";
        this.pathProject = "";
        this.pathTests = "";
        this.name = "";
        this.analyzer = new ProjectAnalyzer();
        this.testsResults = new HashMap<>();
    }

    public Project(String name, String pack, String pathP, String pathT, String tName, String localFolder) {
        this.pack = pack;
        this.testPackage = XMLParser.getXmlPropertyValue("manifest", "package", pathT+"AndroidManifest.xml");
        this.name = name;
        this.pathProject = pathP;
        this.pathTests = pathT;
        this.transPath = pathProject+tName; //+"/";
        this.transTestsPath = transPath+"/tests"; //+"/";
        this.analyzer = new ProjectAnalyzer(this.name, this.pack, localFolder+"/");
    }
    
    public Project(String name, String pack, String testPack, String pathP, String pathT, String tName, String localFolder) {
        this.pack = pack;
        this.testPackage = testPack;
        this.name = name;
        this.pathProject = pathP;
        this.pathTests = pathT;
        this.transPath = pathProject+tName; //+"/";
        this.transTestsPath = transPath+"/tests"; //+"/";
        this.analyzer = new ProjectAnalyzer(this.name, this.pack, localFolder+"/");
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

    public Map<Integer, TestResult> getTestsResults() {
        return testsResults;
    }

    public void setTestsResults(Map<Integer, TestResult> testsResults) {
        this.testsResults = testsResults;
    }
    
    public void runAnalyzer() throws IOException{
        this.analyzer.execute();
        this.testsResults=analyzer.getAllTestResults();
    }
    
    public void createFinalResults() throws IOException{
        this.analyzer.createFinalResults();
    }
    
    public String toJSON(){
        //save to JSON format
        StringBuilder builder = new StringBuilder();
        builder.append("{\"package\": \"");
        builder.append(pack);
        builder.append("\",\n\"results\": [\n");
        if(!this.testsResults.isEmpty()){
            for(Integer test : this.testsResults.keySet()){
                TestResult c = testsResults.get(test);
                Long cons = c.getTotalConsumption();
                Double time = c.getTime();
                builder.append("\t{\"test\": ");
                builder.append(test);
                builder.append(", \"consumption\": ");
                builder.append(cons);
                builder.append(", \"time\": \"");
                builder.append(time);
                builder.append("\"},");
            }
            builder.deleteCharAt(builder.lastIndexOf(","));
        }
        builder.append("\n]");
        builder.append("}");
        return builder.toString();
    }
    
    public void createAnalyzer(String folder){
        this.analyzer = new ProjectAnalyzer(this.name, this.pack, folder+"/");
    }
    
    public Project clone(){
        Project res = new Project(name, pack, testPackage, pathProject, pathTests, "", "");
        Map<Integer, TestResult> results = new HashMap<>();
        for(Integer i : testsResults.keySet()){
            results.put(i, testsResults.get(i).clone());
        }
        res.setTestsResults(results);
        return res;
    }
}
