/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid;

import greendroid.project.Project;
import greendroid.tools.Config;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import greendroid.tools.Util;
import instrumentation.transform.InstrumentHelper;
import instrumentation.util.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class Main {
    private static int extFlag = 0;
    
    public static String tName = "_TRANSFORMED_";
    
    public static String configFile = "config.cfg";
    public static String projectsFile = "projects.csv";
    
    private static ArrayList<Project> projects = new ArrayList<Project>();
    private static LinkedList<ProjectAnalyser> analysers = new LinkedList<ProjectAnalyser>();
    
    //private static String resFolder = "";
    private static Config config;
    private static String workspace = "";
    
    public static long averageSecond = 0;
    
    private static void instrument(String app, String tests) throws Exception{
        InstrumentHelper helper = new InstrumentHelper(tName, workspace, app, tests);
        helper.generateTransformedProject();
        helper.generateTransformedTests();
    }
    
    private static void executeTests(String pack, String testPack, String pathProject, String pathTests) throws IOException {
        //String resDir = "/mnt/sdcard/Pictures/MyFiles/"+pack+"/";
        
        String c0 = "adb shell mkdir "+config.getDeviceResDir();
        String c1 = "android update project -p \""+pathProject+"\" -n Green && android update test-project -p \""+pathTests+"\" --main \""+pathProject+"\" && ant -f \""+pathTests+"/build.xml\" clean && ant -f \""+pathTests+"/build.xml\" debug";
        String c2 = "adb install -r \""+pathProject+"/bin/Green-debug.apk\" && adb install -r \""+pathTests+"/bin/GreenTest-debug.apk\"";
        String c3 = "adb shell am instrument -e reportFile ALL-TEST.xml -e reportDir \""+config.getDeviceResDir()+"\" -e filterTraces false -w "+testPack+"/com.zutubi.android.junitreport.JUnitReportTestRunner";
        String c4 = "adb shell \"echo \"1\" > /mnt/sdcard/Pictures/MyFiles/flag\"";
        String c5 = "adb shell am instrument -w "+testPack+"/com.zutubi.android.junitreport.JUnitReportTestRunner";
        String c6 = "adb shell \"echo \"-1\" > /mnt/sdcard/Pictures/MyFiles/flag\"";
        
        String[] commands = new String[7];
        commands[0]=c0; commands[1]=c1; commands[2]=c2; commands[3]=c3; commands[4]=c4; commands[5]=c5; commands[6]=c6;
        
        for(int i=0; i<7; i++){
            System.out.println("RUNNING: "+commands[i]);
            //Can't figure why the connectbot test project keeps generating a tests-debug.apk
            //This 'if' is to get the only file ending in *-debug.apk
            if(i == 2){
                File apk;
                if((apk = Util.getFileWithName(pathTests+"/bin/", "*-debug.apk")) != null){
                    String ax = apk.getName();
                    commands[i] = "adb install -r \""+pathProject+"/bin/Green-debug.apk\" && adb install -r \""+pathTests+"/bin/"+ax+"\"";
                }
            }
            executeCommand(commands[i]);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        System.out.println("Creating support folder...");
        //executeCommand("mkdir "+resFolder+pack);
        File allFolder = new File(config.getLocalResDir()+pack+"/all"); allFolder.mkdirs();
        //System.out.println("Creating support folder...");
        //executeCommand("mkdir "+resFolder+pack+"/all");
        File source = new File(pathProject+"/_aux_/AllMethods");
        File dest = new File(config.getLocalResDir()+pack+"/all/AllMethods"); dest.createNewFile();
        System.out.println("Copying file with all methods");
        FileUtils.copyFile(source, dest);
        
    }

    private static void executeCommand(String command){
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("cmd /c "+command);
            BufferedReader input;
            BufferedReader errors;
            if(extFlag == 0){
                input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                errors = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
            }else{
                errors = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                input = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
            }
            String line=null, err=null;
            while(((line=input.readLine()) != null) || ((err=errors.readLine()) != null)) {
                if(line != null){
                    System.out.println(line);
                }
            
                if(err != null){
                    System.out.println(err);
                }
            }
            
            errors.close();
            input.close();
            
            pr.waitFor();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void runAnalyser(String name, String pack) throws IOException {
        ProjectAnalyser pa = new ProjectAnalyser(name, pack, config.getLocalResDir());
        analysers.add(pa);
        pa.execute();
    }

    private static void extractFiles() throws IOException {
        executeCommand("adb pull /mnt/sdcard/Pictures/MyFiles/ "+config.getLocalResDir());
    }
    
    public static void main(String[] args){
        workspace = "C:/Users/User/workspace/";
        config = Util.parseConfigs(configFile);
        
        //projects.add(new Project("benchmark", "org.zeroxlab.zeroxbenchmark", "C:/Users/User/workspace/bench/", "C:/Users/User/workspace/bench/tests/", tName));
        
        projects = (ArrayList)Util.parseProjects(projectsFile);
        System.out.println("GREENDROID - Testing Framework for Android Applications\n");
        System.out.println("Using "+projects.size()+" projects to be analyzed");
        if(projects.size() > 0){
            try{
/*            //instrument();
            System.out.println("INSTRUMENTATION");
            for(Project p : projects){
                System.out.println("Instrumenting "+p.getName()+" project...");
                instrument(p.getPathProject(), p.getPathTests());
                System.out.println("DONE!");
            }
/*
            //executeTests();
            System.out.println("TEST EXECUTION");
            for(Project p : projects){
                System.out.println("Executing tests for "+p.getName()+" project...");
                executeTests(p.getPackage(), p.getTestPackage(), p.getTransPath(), p.getTransTestsPath());
                System.out.println("DONE!");
            }

            //extractFiles();
            System.out.println("FILE EXTRACTION");
            extFlag = 1;
            extractFiles();
            extFlag = 0;
            System.out.println("DONE");
/**/
            //This section runs the analyzer for each project, 
//            //parsing the trace files and generating consumption per second
/**/           System.out.println("ANALIZE RESULTS");
            for(Project p : projects){
                System.out.println("Analyzing "+p.getName()+" project");
                runAnalyser(p.getName(), p.getPackage());
                System.gc();
                System.out.println("DONE!");
            }
            List<Long> means = Util.readLongsFromFile("D:/meansSecond.txt");
            averageSecond = Util.average(means);
            System.out.println("Average Consumption p/ second: "+averageSecond);
/**/
            System.out.println("GETTING FINAL RESULTS");
            for(ProjectAnalyser pa : analysers){
                System.out.println("Generating results for ");
                pa.createFinalResults();
                System.out.println("DONE!");
            }
        //load the results???*/
        } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }

    
}
