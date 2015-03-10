/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid;

import greendroid.tools.Util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 *
 * @author User
 */
public class ProjectAnalyser {
    //Files with the traces of the different tests collected, and with the consumptions
    private String testsFolder;
    private String traceFile;
    private String allFolder;
    private String consumptionFile;
    private String timingFile;
    //File with all the methods to be analysed
    private String allMethodsFile;
    //Files with the SFL matrices
    private String resultsFolder;
    private String fileResults;
    private String fileNumExecutions;
    //File with the result of the SFL similarity calculation
    private String fileSFLResult;
    private String fileJSON;
    //Strings used to temporarily store the SFL matrices
    private String executionBits;
    private String numExecutions;

    private long time;
    private LinkedList<Integer> sfl;
    
    /**                          class          methods    */
    private LinkedHashMap<String, LinkedList<TracedMethod>> all;
    private TestCase tCase;
    private LinkedList<TestCase> traced;
    //private LinkedList<Long> times;
    private LinkedList<Double> times;
    private LinkedList<Consumption> consumptions;
    
    private int flagC;
    private int flagT;
    private int conTrace;
    private int totalMethods;
    private int totalClasses;
    private int totalPackages; 
    
    /**
     * @param args the command line arguments
     */
    
    public ProjectAnalyser(){
        
    }
    
    public ProjectAnalyser(String projectName, String projectPath, String folder){
        String path = folder+projectPath+"//";
        testsFolder = path;
        allFolder = testsFolder+"all//";
        traceFile = allFolder+"traceAll";
        consumptionFile = testsFolder+"measuring";
        timingFile = testsFolder+"ALL-TEST.xml";
        allMethodsFile = allFolder+"AllMethods";
        resultsFolder = testsFolder+"results//";
        fileResults = resultsFolder+"testResults.txt";
        fileNumExecutions = resultsFolder+"countExecutions.txt";
        fileSFLResult = resultsFolder+"SFLResult.txt";
        fileJSON = resultsFolder+projectName+".json";
        executionBits = "";
        numExecutions = "";

        time = 0;
        sfl = new LinkedList<Integer>();

        all = new LinkedHashMap<String, LinkedList<TracedMethod>>();
        tCase = new TestCase();
        traced = new LinkedList<TestCase>();
        //times = new LinkedList<Long>();
        times = new LinkedList<Double>();
        consumptions = new LinkedList<Consumption>();

        flagC = 0;
        flagT = 0;
        conTrace = 0;
        totalMethods = 0;
        totalClasses = 0;
        totalPackages = 0; 
    }
    
    /**Concat the result files into one big file */
    public void concatTests() throws IOException{
        File allF = new File(allFolder);
        if(!allF.exists()) allF.mkdirs();
        File all = new File(traceFile);
        if(all.exists()) all.delete();
        else all.createNewFile();
        File folder = new File(testsFolder);
        File[] listOfFiles = folder.listFiles();
        String line="";
        FileWriter fout = new FileWriter(traceFile, true);
        BufferedWriter out = new BufferedWriter(fout);
        for (File file : listOfFiles) {
            if(file.isFile() && file.getName().startsWith("trace")){
                FileInputStream fstream = new FileInputStream(file);
                // Get the object of DataInputStream
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                while((line=br.readLine()) != null) {
                    out.write(line+"\n");
                    out.flush();
                }
                br.close();
            }
        }
        out.close();
    }
    
    /**Clears the list with all the methods and the list with the traced methods */
    public void clearAll(){
        //all.clear();
        traced.add(tCase.clone());
        //tCase.clear();
        tCase = new TestCase();
        tCase.copyAll(all);
        //executionBits = "";
        //numExecutions = "";
    }
    
    /**Loads the resulting SFL coefficients for each method from a file */
    public void loadSFLResult(){
        String result="", result2="";
        try{
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(fileSFLResult);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine="";
            String[] x;
            int s=0;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                x=strLine.split("\t");
                for(int i = 0; i< x.length; i++){
                    sfl.add(Integer.parseInt(x[i]));
                }
            }
            //Close the input stream
            in.close();
            }catch (Exception e){//Catch exception if any
                System.err.println("Error: " + e);
            }
    }
    
    /**Loads the measures of each test the specific file */
    public void getMeasures(){
        Consumption cons = new Consumption();
        long l=0, c=0, w=0,g=0,gp=0,a=0;
        try{
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(consumptionFile);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine="";
            String [] x;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                if(strLine.contains("NEW MEASURE") && flagC == 1){
                    consumptions.add(cons);
                    cons = new Consumption();
                }
                if(strLine.contains("[CONSUMPTION]")){
                    flagC = 1;
                    x=strLine.split("[\t]");
                    //[CONSUMPTION]\tLCD:l\tCPU:c\tWIFI:w\t3G:g\tGPS:gp\tAUDIO:a\t
                    
                    if(x.length == 7){
                        String lcd=x[1], cpu=x[2], wifi=x[3], g3=x[4], gps=x[5], audio=x[6];
                        //l += Integer.parseInt(lcd.split(":")[1]);
                        l += 0;
                        c += Integer.parseInt(cpu.split(":")[1]);
                        w += Integer.parseInt(wifi.split(":")[1]);
                        g += Integer.parseInt(g3.split(":")[1]);
                        gp += Integer.parseInt(gps.split(":")[1]);
                        a += Integer.parseInt(audio.split(":")[1]);
                        cons = new Consumption(l, c, w, g, gp, a);
                    }else{
                        System.err.println("Error in measures: Unexpected length");
                    }
                }
            }
            consumptions.add(cons);
            //for(Long px : consumptions) System.out.println(px);
            //Close the input stream
            in.close();
            }catch (Exception e){//Catch exception if any
                System.err.println("Error: " + e);
            }
    }
    
    private void getTimes() {
        times = (LinkedList)Util.getXMLTimes(timingFile);
    }
    
    /**Loads the file containing all the traces */
    public String getTrace(){
        String result="", result2="";
        try{
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(traceFile);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine="";
            String [] x;
            long c=0;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                if(strLine.contains("NEW TRACE")){
                    if(flagT == 1){
                        generateResults();
                        /*tCase.clear();
                        tCase = new TestCase();
                        tCase.copyAll(all);*/
                    }else{
                        tCase.copyAll(all);
                        //tCase.print();
                    }
                }else if(strLine.contains("{")){
                    flagT = 1;
                    time = Long.parseLong(strLine.substring(1, strLine.length()-1));
                    //tCase.setTime(time);
                    tCase.setTime(times.get(conTrace));
                }else if(strLine.contains("(BEGIN)")){
                    flagT = 1;
                    x=strLine.split("[\\)@\\[\\]]");
                    if(x.length == 4){
                        String met=x[1];
                        String cla=x[2];
                        if(!cla.startsWith("<")){
                            cla.lastIndexOf(".");
                            String a = cla.substring(0, cla.lastIndexOf("."));
                            String b = cla.substring(cla.lastIndexOf(".")+1, cla.length());
                            cla = "<"+a+">"+b;
                        }
                        int num_exec = Integer.parseInt(x[3]);
                        TracedMethod tm = new TracedMethod(met, 1, num_exec);
                        if(tCase.containsKey(cla)){
                            if(!tCase.get(cla).contains(tm)){
                                System.out.println("Detected undefined method: "+met+"@"+cla);
                            }else{
                                tCase.getMethod(cla, met).setExecuted(1);
                                tCase.getMethod(cla, met).setNum_exec(num_exec);
                            }
                        }else{
                            System.out.println("Detected undefined class: "+cla);
                        }
                    }else{
                        System.out.println("[Warning] Bad length. Ignoring...");
                    }
                }
            }
            in.close();
            for(TestCase s : traced){
                result += s.toString()+"\n";
            }
            //Close the input stream
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e);
        }
        return result;
    }
    
    /**Loads all the methods of the project in analysis */
    public Map<String, LinkedList<TracedMethod>> loadAllMethods(){
        LinkedHashMap<String, LinkedList<TracedMethod>> all = new LinkedHashMap<String, LinkedList<TracedMethod>>();
        try{
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(allMethodsFile);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine="", pkg="", cl="", meth="";
            String [] x;
            long c=0;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                if(strLine.startsWith("<")){
                    pkg=strLine;
                    totalPackages++;
                }else if(strLine.startsWith("(Class)")){
                    x=strLine.split("\\)");
                    if(x.length == 2){
                        cl=pkg+x[1];
                        totalClasses++;
                    }else{
                        System.err.println("Error: unexpected length");
                        break;
                    }
                }else{
                    LinkedList<TracedMethod> list = new LinkedList<TracedMethod>();
                    x=strLine.split(";");
                    for(int i=0; i<x.length; i++){
                        TracedMethod novo = new TracedMethod(x[i], 0);
                        if(!list.contains(novo)){
                            totalMethods++;
                            list.add(novo);
                        }
                    }
                    all.put(cl, list);
                }
            }
            
            //printAllMethods();
            //Close the input stream
            in.close();
        }catch (Exception ex){//Catch exception if any
            System.err.println("Error: " + ex);
        }
        return all;
    }
    
    /**Print all the methods of the project to the standard output */
    public void printAllMethods(){
        String classes="";
        String methods="";
        for(String c : all.keySet()){
            classes += c;
            for(TracedMethod t : all.get(c)){
                methods += t.getMethod()+"\t";
                classes += "\t";
            }            
        }
        classes = classes.substring(0, classes.length()-1);
        methods = methods.substring(0, methods.length()-1);
        System.out.println(classes);
        System.out.println(methods);
        //return classes+"\n"+methods;
    }
    
    /**Generates the results for a test case and resets */
    private void generateResults(){
        //inspectMethods();
        tCase.setConsumption(consumptions.get(conTrace));
        //times.add(time);
        conTrace++;
        clearAll();
    }
    
    /**Saves the results previously generated into a file that has the SFL Matrix */
    public void saveTestResults() throws IOException{
        int i = 0;
        for(TestCase c : traced){
            Util.saveFile("D://meansSecond.txt", c.getMeanSecond()+"\n", true);
            for(String cl : c.getTraced().keySet()){
                for(TracedMethod t : c.get(cl)){
                    executionBits+=t.getExecuted()+"\t";
                    numExecutions+=t.getNum_exec()+"\t";
                }
            }
            executionBits+=c.getConsumption()+"\t"+c.getTime()+"\n";
            numExecutions = numExecutions.equals("") ? numExecutions : numExecutions.substring(0, numExecutions.length()-1);
            numExecutions+="\n";
        }
        
        //save SFL Matrix
        File dirRes = new File(resultsFolder);
        if(!dirRes.exists()) dirRes.mkdirs();
        File file = new File(fileResults);
        if(!file.exists()) file.createNewFile();

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(executionBits);
        bw.flush();
        bw.close();
        
        //save Nº of Executions /call Matrix
        
        file = new File(fileNumExecutions);

        fw = new FileWriter(file.getAbsoluteFile(), true);
        bw = new BufferedWriter(fw);
        bw.write(numExecutions);
        bw.flush();
        bw.close(); 
   }
    
    /**Does all the steps, in the correct order */
    public void execute() throws IOException{
        all = (LinkedHashMap)loadAllMethods();
        concatTests();
        getTimes();
        getMeasures();
        getTrace();
        /*'generateResults()' is invoked here to assure that the last trace is treated */
        generateResults();
        saveTestResults();
        
    }
    
    /**Calculates the resulting statistics for the project and corresponding files */
    public void createFinalResults() throws IOException{
        calculateSFL();
        /** */
        //loadSFLResult();    //?
        
        Util.toJSON(all, sfl, fileJSON);
    }
    
    private void calculateSFL() {
        int i;
        long totals = 0, totalMean = 0;
        ArrayList<TestCase> excessives = new ArrayList<TestCase>();
        ArrayList<TestCase> normals = new ArrayList<TestCase>();
        
        //totalMean = read from MEANS FILE and divide by the number of lines...
        totalMean = Main.averageSecond;
        
        //DIFFERENT APPROACH
        /*for(TestCase tc : traced){
            long meanSecond = (long)tc.getMeanSecond();
            totals += meanSecond;
        }
        totalMean = (long)totals/consumptions.size();
        */
        //Util.saveFile("D://meansSecond.txt", totalMean+"\n", false);
        for(TestCase tc : traced){
            if(tc.getMeanSecond() > totalMean){
                tc.setExcessive(1);
                excessives.add(tc);
            }else{
                normals.add(tc);
            }
        }
        //Assign SFL coefficient to the methods
        assignCoefficients(excessives, normals);
    }

    private void assignCoefficients(ArrayList<TestCase> excessives, ArrayList<TestCase> normals) {
        int exc = 0, norm = 0, i = 0;
        for(String cl : all.keySet()){
            for(TracedMethod tm : all.get(cl)){
                exc = 0; norm = 0;
                for(TestCase tc : excessives){
                    if(tc.hasMethod(cl, tm.getMethod())){
                        exc++;
                    }
                }
                for(TestCase tc : normals){
                    if(tc.hasMethod(cl, tm.getMethod())){
                        norm++;
                    }
                }
                if(exc == 0){
                    //GREEN!
                    System.out.println("GREEN");
                    sfl.add(1);
                //}else if(exc > 0 && norm < (0.4*traced.size())){
                }else if(exc > 0 && norm < (0.4*(norm+exc))){
                    //RED
                    System.err.println("RED");
                    sfl.add(200);
                }else{
                    //YELLOW
                    System.out.println("YELLOW");
                    sfl.add(100);
                }
                i++;
            }
        }
    }


}
