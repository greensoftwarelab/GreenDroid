/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import greendroid.tools.Util;
import instrumentation.transform.InstrumentHelper;
import instrumentation.util.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class Main {
    public static String tName = "_TRANSFORMED_";
    
    private static ArrayList<Project> projects = new ArrayList<Project>();
    private static LinkedList<ProjectAnalyser> analysers = new LinkedList<ProjectAnalyser>();
    
    private static String resFolder = "";
    private static String workspace = "";
    
    public static long averageSecond = 0;
    
    public static void instrument(String app, String tests) throws Exception{
        //InstrumentHelper helper = new InstrumentHelper(tName, workspace, app, tests);
        //helper.generateTransformedProject();
        //helper.generateTransformedTests();
    }
    
    private static void executeTests(String pack, String testPack, String pathProject, String pathTests) throws IOException {
        String resDir = "/mnt/sdcard/Pictures/MyFiles/"+pack+"/";
        
        String c0 = "adb shell mkdir "+resDir;
        String c1 = "android update project -p \""+pathProject+"\" -n Green && android update test-project -p \""+pathTests+"\" --main \""+pathProject+"\" && ant -f \""+pathTests+"/build.xml\" clean && ant -f \""+pathTests+"/build.xml\" debug";
        String c2 = "adb install -r \""+pathProject+"/bin/Green-debug.apk\" && adb install -r \""+pathTests+"/bin/GreenTest-debug.apk\"";
        String c3 = "adb shell am instrument -e reportFile ALL-TEST.xml -e reportDir \""+resDir+"\" -e filterTraces false -w "+testPack+"/com.zutubi.android.junitreport.JUnitReportTestRunner";
        String c4 = "adb shell \"echo \"1\" > /mnt/sdcard/Pictures/MyFiles/flag\"";
        String c5 = "adb shell am instrument -w "+testPack+"/com.zutubi.android.junitreport.JUnitReportTestRunner";
        String c6 = "adb shell \"echo \"-1\" > /mnt/sdcard/Pictures/MyFiles/flag\"";
        
        String[] commands = new String[7];
        commands[0]=c0; commands[1]=c1; commands[2]=c2; commands[3]=c3; commands[4]=c4; commands[5]=c5; commands[6]=c6;
        
        for(int i=0; i<7; i++){
            System.out.println("RUNNING: "+commands[i]);
            executeCommand(commands[i]);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        System.out.println("Creating support folder...");
        //executeCommand("mkdir "+resFolder+pack);
        File allFolder = new File(resFolder+pack+"/all"); allFolder.mkdirs();
        //System.out.println("Creating support folder...");
        //executeCommand("mkdir "+resFolder+pack+"/all");
        File source = new File(pathProject+"/_aux_/AllMethods");
        File dest = new File(resFolder+pack+"/all/AllMethods"); dest.createNewFile();
        System.out.println("Copying file with all methods");
        FileUtils.copyFile(source, dest);
        
    }

    public static void executeCommand(String command) throws IOException{
        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec("cmd /c "+command);
        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

        String line=null;

        while((line=input.readLine()) != null) {
            System.out.println(line);
        }
        input.close();
    }
    
    private static void runAnalyser(String name, String pack) throws IOException {
        ProjectAnalyser pa = new ProjectAnalyser(name, pack, resFolder);
        analysers.add(pa);
        pa.execute();
    }

    private static void extractFiles() throws IOException {
        executeCommand("adb pull /mnt/sdcard/Pictures/MyFiles/ "+resFolder);
    }
    
    public static void main(String[] args){
        workspace = "C:/Users/User/workspace/";
        resFolder = "D:/tests/";
        //projects.add(new Project("authenticator", "com.google.android.apps.authenticator2", "C:/Users/User/workspace/Google Authenticator/", "C:/Users/User/workspace/Google Authenticator/tests/", tName));
        //projects.add(new Project("newsblur", "com.newsblur", "C:/Users/User/Desktop/Thesis/App's Source Code/NewsBlur-master/clients/android/NewsBlur/", "C:/Users/User/Desktop/Thesis/App's Source Code/NewsBlur-master/clients/android/NewsBlurTest/", tName));
        //projects.add(new Project("apptracker", "com.nolanlawson.apptracker", "C:/Users/User/Desktop/Thesis/App's Source Code/AppTracker-master/AppTracker/", "C:/Users/User/Desktop/Thesis/App's Source Code/AppTracker-master/AppTrackerTest/", tName));
        //projects.add(new Project("chordreader", "com.nolanlawson.chordreader", "C:/Users/User/Desktop/Thesis/App's Source Code/ChordReaderRoot-master/ChordReader/", "C:/Users/User/Desktop/Thesis/App's Source Code/ChordReaderRoot-master/ChordReaderTest/", tName));
        //projects.add(new Project("logcat", "com.nolanlawson.logcat", "C:/Users/User/Desktop/Thesis/App's Source Code/Catlog-master/Catlog/", "C:/Users/User/Desktop/Thesis/App's Source Code/Catlog-master/CatlogTest/", tName));
        projects.add(new Project("connectbot", "org.connectbot", "C:/Users/User/workspace/connectbot/", "C:/Users/User/workspace/connectbot/tests/", tName));
        
        System.out.println("GREENDROID - Testing Framework for Android Applications\n");
        System.out.println("Using "+projects.size()+" projects to be analyzed");
        
        try{
            //instrument();
            System.out.println("INSTRUMENTATION");
            for(Project p : projects){
                System.out.println("Instrumenting "+p.getName()+" project...");
                instrument(p.getPathProject(), p.getPathTests());
                System.out.println("DONE!");
            }

            //executeTests();
            System.out.println("TEST EXECUTION");
            for(Project p : projects){
                System.out.println("Executing tests for "+p.getName()+" project...");
                executeTests(p.getPackage(), p.getTestPackage(), p.getTransPath(), p.getTransTestsPath());
                System.out.println("DONE!");
            }

            //extractFiles();
            System.out.println("FILE EXTRACTION");
            //extractFiles();
            System.out.println("DONE");
/*
            //getFinalResults();
            System.out.println("ANALIZE RESULTS");
            for(Project p : projects){
                System.out.println("Analyzing "+p.getName()+" project");
                runAnalyser(p.getName(), p.getPackage());
                System.out.println("DONE!");
            }
            averageSecond = Util.readLongsFromFile("D:/meansSecond.txt");
            System.out.println("Average Consumption p/ second: "+averageSecond);

            System.out.println("GETTING FINAL RESULTS");
            for(ProjectAnalyser pa : analysers){
                System.out.println("Generating results for ");
                pa.createFinalResults();
                System.out.println("DONE!");
            }*/
        //load the results???
        } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
}
