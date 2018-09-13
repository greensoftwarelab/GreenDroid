package Analyzer;


import Analyzer.Results.Results;
import Analyzer.Results.TestResults;
import GreenSourceBridge.GreenSourceAPI;
import Metrics.*;
import Metrics.AndroidProjectRepresentation.*;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class Analyzer {

    public static String projectAppJSONFile ="";
    public static String stopTag="stopped"; // TODO put this in GDconventions
    public static String startTag="started"; // TODO put this in GDconventions
    public static boolean isTestOriented=false;
    public static HashMap<String,Integer> allTracedMethods = new HashMap<>(); // Method name -> times invoked
    public  static String resultDirPath = "results/" ;
    //public static Double [] returnList = new Double[13];
    public static Results actualResult = new Results();
    public static HashMap<String , Results>  globalReturnList = new HashMap<>();
    public static HashMap<String ,String>  testNameNumber = new HashMap<>();
    public static String allMethodsDir = "";
    public static List<String> alltests= new ArrayList<>();
    public static HashSet<String> allmethods= new HashSet<>();
    public static APICallUtil acu = null;

    public static final String testResultsFile = GDConventions.TestOutputName;
    public static final String appResultsFile = GDConventions.AppOutputName;
    public static final String appIssuesFile = GDConventions.IssuesOutputName;
    public static final String serializedFile = GDConventions.fileStreamName;

    public static final String analyzerTag= "[Analyzer] ";
    public static  String folderPrefix= "Test";
    public static int stoppedState = 0;
    public static boolean monkey = false;
    private static boolean mergeOldRuns= false;
    public static String applicationID = "unknown";
    public static List<String> actualTestMethods = new ArrayList<>();
    public static GreenSourceAPI grr = new GreenSourceAPI();
    public static JSONArray energyGreadyAPIS = new JSONArray();
    public static JSONArray methodsInvoked = new JSONArray();

    private static List<String> loadTests(String csvFile) throws Exception {
        alltests = new ArrayList<String>();
        File f = new File(csvFile);
        Path path = Paths.get(f.getAbsoluteFile().getParent());
        Path p = null;
        try{
            DirectoryStream<Path> stream;
            stream = Files.newDirectoryStream(path);
            for (Path entry : stream)
            {
                if(entry.getFileName().toString().matches("TracedTests\\.txt")){
                    p = entry;//break;
                    break;
                }
            }
            stream.close();
            if (p==null)
                throw new IOException("TracedTests not found");
        }
        catch (IOException e)
        {
            System.out.println("TracedTests not found");
            return alltests;
        }

        try (Stream<String> lines = Files.lines (p, StandardCharsets.UTF_8)) {
                for (String line : (Iterable<String>) lines::iterator)
                {
                    alltests.add(line);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        return alltests;
    }


    private static HashSet<String> loadMethods(String allMethods) {
        allmethods = new HashSet<String>();

        Path path = Paths.get(allMethods + "allMethods.txt");
        try {
            try (Stream<String> lines = Files.lines (path, StandardCharsets.UTF_8))
            {
                for (String line : (Iterable<String>) lines::iterator)
                {
                    allmethods.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println(analyzerTag + " : File containing methods (allMethods.txt) not found!");
            // e.printStackTrace();
        }


        
        return allmethods;
    }

    public static void copyMethodMap (HashMap<String, Integer> h) {

        for (String s : h.keySet()) {
            if (allTracedMethods.containsKey(s)){
                int x = allTracedMethods.get(s);
                int y = h.get(s);
                allTracedMethods.put(s, y+x);
            }
            else {
                allTracedMethods.put(s,1);
            }
        }
    }


    public static void write (Writer w, List<String> l) throws IOException {

        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (String value: l)
        {
            if(!first){
                sb.append(",");
                sb.append(value);
            }
            else {
                sb.append(value);
                first=false;
            }
        }

        sb.append("\n");
        w.append(sb.toString());
    }


    private static void testOriented( List<String> args) throws NullPointerException{
        CsvParserSettings settings = new CsvParserSettings();
        Map<Integer,Integer> timeConsumption = new HashMap<>();
        List <Consumption> consumptionList = new ArrayList<>();
        Set<Integer> timeStates = new TreeSet<>();
        settings.getFormat().setLineSeparator("\n");
        FileWriter fw = null;
        FileWriter fwApp = null;

        try {
            File f = new File(resultDirPath+"/" + testResultsFile);
            File f2 = new File(resultDirPath+"/" + appResultsFile);

            if (!f.exists()){
                f.createNewFile();
            }
            if (!f2.exists()){
                f2.createNewFile();
            }
            fw = new FileWriter(f);
            fwApp = new FileWriter(f2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        actualResult = new TestResults();
        List<String> l = ((TestResults) actualResult).metricsName;
        try {
            write(fw,l);
        } catch (IOException e) {
            e.printStackTrace();
        }
        l.clear();

        // for each csv file found
        for (int j = 0; j <args.size() ; j++) {
            actualResult = new TestResults();

            if (!args.get(j).matches(".*.csv.*") || args.get(j).matches(".*Testresults.csv")) continue;
            System.out.println("--- " + args.get(j) + " ---");
            CsvParser parser = new CsvParser(settings);
            // 3rd, parses all rows of data in selected columns from the CSV file into a matrix
            List<String[]> resolvedData = null;
            try {
                File f = new File(args.get(j));
                resolvedData = parser.parseAll(new FileReader(f.getAbsolutePath()));
            } catch (FileNotFoundException e) {
                System.out.println("[ANALYZER]: File Not Found: There is no " + args.get(j) + " csv file in directory! to generate results");
                continue;
            }

            HashMap<String, Pair<Integer, Integer>> columns = null;
            try {
                columns = Utils.fetchColumns(resolvedData);
            } catch (Exception e) {
                System.out.println("[ANALYZER] Error fetching columns. Result csv might have an error");
            }
            String number = args.get(j).replaceAll(".+GreendroidResultTrace(.+)\\..+", "$1");

            String[] row = new String[32];

            // Get data from rows
            for (int i = 4; i < resolvedData.size(); i++) {
                row = resolvedData.get(i);
                if (row.length==0 || row[0]==null)
                    break;
                consumptionList.add(getDataFromRow(columns, row));

            }

            Path p = getRespectiveTracedMethodsFile(args.get(j));
            double totalconsumption = actualResult.getTotalConsumption();
            String [] res = ((TestResults) actualResult).showGlobalData();

            if (actualResult.time==0) {
                System.out.println("[Analyzer] Warning: Ignoring test " + number + "; Missing start or stop tags in resultant .csv file");
                testNameNumber.put(getTestName(number), number);

                double totalcoverage = (methodCoverageTestOriented(p) * 100);
                System.out.println("---------------Method Coverage of Test------------------");
                System.out.println("percentage: " + totalcoverage + " %");
                System.out.println("------------------------------------------------");
                globalReturnList.put(number,actualResult);
               // copyToGlobalReturnList(number);
                continue;
            }

            ((TestResults) actualResult).testName = getTestName(number);
            ((TestResults) actualResult).testId= number;

            try {
                alltests = loadTests(args.get(j));
            } catch (Exception e) {
                System.out.println("[ANALYZER] Error tracing tests... Assuming order of tests instead of names");
            }

            if (actualResult.time > 0) {
                System.out.println("---------" + " TEST CONSUMPTION" + "-----------");
                //System.out.println("--"+ " Filename: " + args[j] + " -----------");
                System.out.println("--" + " Test Name: " + getTestName(number) + " --");
                System.out.println("----------------------------------------------");
                System.out.println("| Test Total Consumption (J) : " + totalconsumption + " J|");
                // System.out.println("| Test Total Consumption (W) : " + watt + " W|");
                System.out.println("| Test Total Time (ms)       : " + actualResult.time + " ms|");
                System.out.println("----------------------------------------------");

            }
            testNameNumber.put(getTestName(number), number);
            double totalcoverage = (methodCoverageTestOriented(p) * 100);
            System.out.println("---------------Method Coverage of Test------------------");
            System.out.println("percentage: " + totalcoverage + " %");
            System.out.println("------------------------------------------------");
            showData(consumptionList);
            //TODO
            //get resume of test results





            globalReturnList.put(number, actualResult);

            grr.testResults = (Utils.getTestResult("0", "nada", grr.getActualTestID(), "greendroid", grr.getActualDeviceID(), "1", "1")); // TODO
            grr.testResults = GreenSourceAPI.sendTestResultToDB(grr.testResults.toJSONString());
            grr.allTestResults.add(grr.testResults);
            grr.testMetrics.addAll(Utils.getTestMetrics(grr.getActualTestResultsID(), res, actualResult.getTotalConsumption(), actualResult.time, ((TestResults) actualResult).getCoverage()));
            grr.methodsInvoked.addAll(Utils.getMethodsInvoked(grr.getActualTestResultsID(), getMethodHashList()));

     // end of csv file processing [END]
        }

        // final Results

        //  print to file the results and name of test
        for (Results resu : globalReturnList.values()) {
            String [] testResults = ((TestResults) resu).showGlobalData();
            for (int i = 0; i < testResults.length ; i++) { l.add(testResults[i]); }
            try {
                write(fw,l);
            } catch (IOException e) {
                e.printStackTrace();
            }
            l.clear();
        }


//        for (String testNr: globalReturnList.keySet()) {
//
//            testResults = showGlobalData(testNr);
//            l.add(testNr);
//            l.add(getTestName(testNr));
//            l.add(String.valueOf(testResults[11]));
//            l.add( String.valueOf(testResults[10]));
//            l.add( String.valueOf(testResults[12]));
//            for (int i = 0; i < 10 ; i++) { l.add(String.valueOf(testResults[i].intValue())); }
//            try {
//                write(fw,l);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            l.clear();
//        }


        double tc = totalCoverage()*100;
        System.out.println("\n///////////////////////////////////////////////");
        System.out.println("---------------Total Coverage------------------");
        System.out.println("percentage: " + (tc) + " %");
        System.out.println("------------------------------------------------");

        try {
            l.add("Total method coverage"); l.add(String.valueOf(0));l.add(String.valueOf(0)); l.add( String.valueOf(0)); l.add(String.valueOf(tc));
            write(fw,l);
            l.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        l.add("Class"); l.add("Method"); l.add("Times invoked");
        l.add("CC");l.add("LoC"); l.add("AndroidAPIs"); l.add("N args"); //l.add("EnergyGreadyAPIS");
        for ( String s : allTracedMethods.keySet()){
            try {
                write(fwApp,l);
                l.clear();
                String [] xx = s.split("<");
                String methodName = xx[xx.length-1].replace(">","");
                l.add(xx[0]); l.add(methodName) ;l.add(String.valueOf(allTracedMethods.get(s)));
                String s1 = s.replaceAll("<.*?>", "");
                MethodInfo mi = acu!=null? acu.getMethodOfClass(methodName,s1) : new MethodInfo();
                methodsInvoked.add(Utils.getMethodAPIS(mi));

                l.add(String.valueOf(mi.cyclomaticComplexity)); l.add(String.valueOf(mi.linesOfCode+(isTestOriented?1:0))); l.add(String.valueOf(mi.androidApi.size())); l.add(String.valueOf(mi.args.size())); //l.add(redapis);

            } catch (IOException  | NullPointerException e) {
                e.printStackTrace();
            }
        }
        try {
            write(fwApp,l);
            l.clear();
            fwApp.flush();
            fwApp.close();

            write(fw,l);
            l.clear();
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            File f3 = new File(resultDirPath+"/" + appIssuesFile);
//            if (!f3.exists()){
//                f3.createNewFile();
//            }
//            FileWriter fwIssues = new FileWriter(f3);
//            l.add("Issue");l.add("Severity");l.add("Category");l.add("Message"); l.add("File"); l.add("Line"); l.add("Explanation");
//            List<Issue> issueList = Utils.parseLintResulsXML()
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //Utils.sendApiCallUtil(acu);

        Utils.writeJSONMethodAPIS(methodsInvoked, resultDirPath+"/" + "methodsInvoked.json" );
        //send results do DBASE

        // send testmetrics
   //     System.out.println("sending " + grr.methodsInvoked.size() + " test metrics");
  //      System.out.println(grr.testMetrics.toJSONString());
        GreenSourceAPI.sendTestsMetricsToDB(grr.testMetrics.toJSONString());
//        //send methods
    //    System.out.println("sending " + grr.methods.size() + " methods");
   //     System.out.println(grr.methods.toJSONString());
        GreenSourceAPI.sendClassesToDB(grr.classes.toJSONString());

        GreenSourceAPI.sendMethodsToDB(grr.methods.toJSONString());
//        // send methods invoked
    //    System.out.println("sending " + grr.methodsInvoked.size() + " methods invoked");
     //   System.out.println(grr.methodsInvoked.toJSONString());
        GreenSourceAPI.sendMethodsInvokedToDB(grr.methodsInvoked.toJSONString());
//        // send methods metrics ??
     //   System.out.println("sending " + grr.methodMetrics.size() + " method metrics");
     //   System.out.println(grr.methodMetrics.toJSONString());
        GreenSourceAPI.sendMethodsMetricsToDB(grr.methodMetrics.toJSONString());

    }


    public static boolean isValidMetric( HashMap<String, Pair<Integer, Integer>> columns,String metricDefinition, String [] rowFromCsv ){

        Pair<Integer,Integer> pair = Utils.getMatch(columns,metricDefinition);
        // if column exists and row contains value for that metric return true
        return (rowFromCsv.length>3 && pair!=null && rowFromCsv.length >=pair.second && rowFromCsv[pair.second]!=null);

    }



//    public static Consumption getDataFromRow( HashMap<String, Pair<Integer, Integer>> columns,String[] row) {
//        double wifiState = Utils.getMatch(columns, Utils.wifiState)!=null? (row[Utils.getMatch(columns, Utils.wifiState).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.wifiState).second])) : returnList[0]) : returnList[0];
//              //  Integer.parseInt(row[Utils.getMatch(columns, Utils.wifiState).second]);
//        double mobileData = Utils.getMatch(columns, Utils.mobileData)!=null? (row[Utils.getMatch(columns, Utils.mobileData).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.mobileData).second])) : returnList[1]) : returnList[1];
//        double screenState = Utils.getMatch(columns, Utils.screenState)!=null?  (row[Utils.getMatch(columns, Utils.screenState).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.screenState).second])) : returnList[2]) : returnList[2];
//        double batteryStatus = Utils.getMatch(columns, Utils.batteryStatus)!=null?  (row[Utils.getMatch(columns, Utils.batteryStatus).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.batteryStatus).second])) : returnList[3]) : returnList[3];
//        double wifiRSSI = Utils.getMatch(columns, Utils.wifiRSSILevel)!=null?  (row[Utils.getMatch(columns, Utils.wifiRSSILevel).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.wifiRSSILevel).second])) : returnList[4]) : returnList[4];
//        double memUsage = Utils.getMatch(columns, Utils.memory)!=null?  (row[Utils.getMatch(columns, Utils.memory).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.memory).second])) : returnList[5]) : returnList[5];
//        double bluetooth = Utils.getMatch(columns, Utils.bluetoothState)!=null?  (row[Utils.getMatch(columns, Utils.bluetoothState).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.bluetoothState).second])) : returnList[6]) : returnList[6];
//        double gpuLoad = Utils.getMatch(columns, Utils.gpuLoad)!=null? (row[Utils.getMatch(columns, Utils.gpuLoad).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.gpuLoad).second])) : returnList[7]) : returnList[7];
//        double cpuLoadNormalized = Utils.getMatch(columns, Utils.cpuLoadNormalized)!=null?  (row[Utils.getMatch(columns, Utils.cpuLoadNormalized).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.cpuLoadNormalized).second])) : returnList[8]) : returnList[8];
//        double gps = Utils.getMatch(columns, Utils.gpsState)!=null? (row[Utils.getMatch(columns, Utils.gpsState).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.gpsState).second])) : returnList[9]) : returnList[9];
//        Consumption c = new  Consumption(((int) memUsage), ((int) mobileData), ((int) wifiState), ((int) wifiRSSI), ((int) screenState), 0, 0, ((int) batteryStatus), ((int) bluetooth), ((int) gpuLoad), ((int) gps), ((int) cpuLoadNormalized));
//        return c;
//    }

    public static Consumption getDataFromRow( HashMap<String, Pair<Integer, Integer>> columns,String[] row) {

        Consumption c = new Consumption();

        Pair<Integer, Integer> wifiState = null, mobileData = null  , screenState = null , batteryStatus =null, wifiRSSI = null,
                memUsage = null, bluetooth = null, gpuLoad = null, cpuLoadNormalized = null, gps=null, power = null, state = null;

        if (isValidMetric(columns,Utils.stateInt,row) && isValidMetric(columns,Utils.stateDescription,row)){
            state = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.stateInt).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.stateInt).second]));
            if (!(actualResult).hasStartTime()&& row[Utils.getMatch(columns, Utils.stateDescription).second].equals(startTag)){
                ((TestResults) actualResult).startTime = state.first;
            }
            if (!actualResult.hasEndTime()&&row[Utils.getMatch(columns, Utils.stateDescription).second].equals(stopTag)){
                ((TestResults) actualResult).stopTime = state.first;
            }

            actualResult.timeSamples.put(state.first,  (state.second));
            c.applicationState = new Pair<Integer, Integer>(state.first,(state.second));
        }


        if (isValidMetric(columns,Utils.batteryPower,row)){
            power = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.batteryPower).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.batteryPower).second]));
            actualResult.powerSamples.put(power.first,  new Double(power.second));
            c.batteryPowerRaw = new Pair<Integer, Double>(power.first, new Double(power.second));
        }


        if (isValidMetric(columns,Utils.wifiState,row)){
            wifiState = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.wifiState).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.wifiState).second]));
            actualResult.wifiStateSamples.put(wifiState.first, wifiState.second);
            c.wifiState = new Pair<Integer, Integer>(wifiState.first, wifiState.second);
        }

        if (isValidMetric(columns,Utils.mobileData,row)){
            mobileData = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.mobileData).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.mobileData).second]));
            actualResult.mobileDataStateSamples.put(mobileData.first, mobileData.second);
            c.mobileDataState = new Pair<Integer, Integer>(mobileData.first, mobileData.second);
        }

        if (isValidMetric(columns,Utils.screenState,row)){
            screenState = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.screenState).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.screenState).second]));
            actualResult.screenStateSamples.put(screenState.first, screenState.second);
            c.screenState = new Pair<Integer, Integer>(screenState.first, screenState.second);
        }

        if (isValidMetric(columns,Utils.batteryStatus,row)){
            batteryStatus = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.batteryStatus).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.batteryStatus).second]));
            actualResult.batteryStatusSamples.put(batteryStatus.first, batteryStatus.second);
            c.batteryStatus = new Pair<Integer, Integer>(batteryStatus.first, batteryStatus.second);
        }

        if (isValidMetric(columns,Utils.wifiRSSILevel,row)){
            wifiRSSI = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.wifiRSSILevel).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.wifiRSSILevel).second]));
            actualResult.rSSILevelSamples.put(wifiRSSI.first, wifiRSSI.second);
            c.rssiLevel = new Pair<Integer, Integer>(wifiRSSI.first, wifiRSSI.second);
        }

        if (isValidMetric(columns,Utils.memory,row)){
            memUsage = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.memory).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.memory).second]));
            actualResult.memorySamples.put(memUsage.first, memUsage.second);
            c.memUsage = new Pair<Integer, Integer>(memUsage.first, memUsage.second);
        }

        if (isValidMetric(columns,Utils.bluetoothState,row)){
            bluetooth = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.bluetoothState).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.bluetoothState).second]));
            actualResult.bluetoothStateSamples.put(bluetooth.first, bluetooth.second);
            c.bluetoothState = new Pair<Integer, Integer>(bluetooth.first, bluetooth.second);
        }
        if (isValidMetric(columns,Utils.gpuLoad,row)){
            gpuLoad = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.gpuLoad).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.gpuLoad).second]));
            actualResult.gpuLoadSamples.put(gpuLoad.first, gpuLoad.second);
            c.gpuLoad = new Pair<Integer, Integer>(gpuLoad.first, gpuLoad.second);
        }

        if (isValidMetric(columns,Utils.cpuLoadNormalized,row)){
            cpuLoadNormalized = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.cpuLoadNormalized).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.cpuLoadNormalized).second]));
            actualResult.addCpuLoadSample(0, cpuLoadNormalized.first, cpuLoadNormalized.second);
            c.cpuLoads.put(0,  new Pair<Integer, Integer>(cpuLoadNormalized.first, cpuLoadNormalized.second));
        }

        if (isValidMetric(columns,Utils.gpsState,row)){
            gps = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.gpsState).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.gpsState).second]));
            actualResult.gPSStateSamples.put(gps.first, gps.second);
            c.gpsState = new Pair<Integer, Integer>(gps.first, gps.second);
        }

        //Consumption c = new  Consumption(((int) memUsage), ((int) mobileData), ((int) wifiState), ((int) wifiRSSI), ((int) screenState), 0, 0, ((int) batteryStatus), ((int) bluetooth), ((int) gpuLoad), ((int) gps), ((int) cpuLoadNormalized));
        return c;
    }


    public static List<String> getMethodHashList(){
        List<String> metodos = new ArrayList<>();
        for(String s: actualTestMethods){
            String [] xx = s.split("<");
            String methodName = xx[xx.length-1].replace(">","");
            String className = s.replaceAll("<.*?>", "");
            MethodInfo mi = acu.getMethodOfClass(methodName,className);
            String metId= mi.getMethodID();
            metodos.add(metId);
            grr.classes.add(mi.ci.toJSONObject(mi.ci.appID));
        }
        return metodos;
    }


    public static String []  showData(List<Consumption> list){

       return  ((TestResults) actualResult).showGlobalData();
    }



    public static Path getRespectiveTracedMethodsFile(String csvFile){
        // get TracedMethodsX.txt file in the folder of the csv file
        String number =csvFile.replaceAll(".+GreendroidResultTrace(.+)\\..+","$1");
        File f = new File(csvFile);
        Path path = Paths.get(f.getAbsoluteFile().getParent());
        Path p = null;
        try{
            DirectoryStream<Path> stream;
            stream = Files.newDirectoryStream(path);
            for (Path entry : stream)
            {
                if(entry.getFileName().toString().matches("TracedMethods"+number+".txt")){
                    p = entry;//break;
                    break;
                }
            }
            stream.close();
        if (p==null)
            throw new IOException("TracedMethods"+number+".txt not found");
        }
        catch (IOException e)
        {
            System.out.println(analyzerTag + " : File containing traced Methods of file (TracedMethods"+number+".txt) not found! Assumed 0 traced methods");
            //e.printStackTrace();
        }
        return p;

    }


    public  static void methodOriented(List<String> arg1) {
       // TODO remove comments
//        try {
//            MethodOriented.methodOriented(arg1);
//        } catch (FileNotFoundException e) {
//            System.out.println("[ANALYZER]: File Not Found: There is no .csv file in directory! to generate results");
//          //s  e.printStackTrace();
//        }
    }

//    public static void copyToGlobalReturnList(String testName){
//        Double [] newReturnList = new Double[13];
//        for (int i = 0; i <returnList.length ; i++) {
//            newReturnList[i] = returnList[i];
//        }
//        if(globalReturnList.get(testName)!=null){
//
//            globalReturnList.get(testName).add(newReturnList);
//        }
//        else {
//            List<Double []> l = new ArrayList<>();
//            l.add(newReturnList);
//            globalReturnList.put(testName, l);
//        }
//    }

    public static Integer closestMemMeasure(Map<Integer,Integer> timeConsumption, int time){
        // calcular a medida de bateria mais aproximada
        int closestStart = 1000000, closestStop = 1000000;
        int difStart = 1000000, diffEnd = 1000000;
//    int alternativeEnd = 0, alternativeStart=0;
        for (Integer i : timeConsumption.keySet()) {
            if(Math.abs(time-i) < difStart){
                difStart = Math.abs(time-i);
                //alternativeStart =closestStart;
                closestStart = i;
            }

        }

        return timeConsumption.get(closestStart);

    }


    public static double totalCoverage(){

        double percentageCoverage = ((double)allTracedMethods.size()/(double)(allmethods.size()));
        return percentageCoverage;
    }

    // recebe como paramentro o Path para ficheiro TracedMethods[0-9].txt correspondente
    public static double methodCoverageTestOriented(Path pathTraced){
        HashSet<String> set = allmethods;
        actualTestMethods = new ArrayList<>();
        if(pathTraced==null) return 0;

        HashMap<String, Integer> thisTest = new HashMap<>();

        // pass all profiled methods to set
        // TODO change to better approach
        try {
            try (Stream<String> lines = Files.lines (pathTraced, StandardCharsets.UTF_8))
            {
                for (String line : (Iterable<String>) lines::iterator) {

                    if(!allmethods.contains(line))
                        continue;

                    if(thisTest.containsKey(line)){
                        int x = thisTest.get(line);
                        thisTest.put(line,++x);
                        actualTestMethods.add(line);
                    }
                    else{
                        thisTest.put(line,1);
                        actualTestMethods.add(line);
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // now I have all profiled methods, search for them in the allMethods file

        ((TestResults) actualResult).invokedMethods = thisTest;
        copyMethodMap(thisTest);
        double percentageCoverage = ((double)thisTest.size()/(double)(set.size()));
        return percentageCoverage;
    }

    public static  String getTestName(String number){
        if(!alltests.isEmpty() && Integer.parseInt(number)<= alltests.size()){
            return alltests.get(Integer.parseInt(number));
        }
        else
            return number;

    }

    public static  List <String> getAllCsvsAndSendAppToDB(String resultDirPath){

        //File f = new File(resultDirPath);
        //Path path = Paths.get(f.getAbsoluteFile().getParent());
        //System.out.println("folde3r " + path);

        List <String> list = new ArrayList<>( );
        Path path = Paths.get(resultDirPath);
        String pathPermissions = "";
        String pathApp = "";
        String pathDevice = "";
        try{
            DirectoryStream<Path> stream;
            stream = Files.newDirectoryStream(path);
            // foreach file in resulDir folder
            for (Path entry : stream) {
                if(!entry.getFileName().toString().startsWith(folderPrefix)){
                    continue;
                }

                if(Files.isDirectory(entry)) { // if is a folder
                    //get files of that folder
                    DirectoryStream<Path> streamChild = null;
                    try {
                        streamChild = Files.newDirectoryStream(Paths.get(entry.toString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return list;
                    }
                    for (Path entry1 : streamChild) { //foreach file of that folder
                        //System.out.println(entry1.getFileName().toString());
                        if (entry.equals(path))
                            continue;
                        if (entry1.getFileName().toString().matches("GreendroidResultTrace[0-9]+\\.csv")) {
                            list.add(entry1.toString());
                        }
                        if (entry1.getFileName().toString().matches("appPermissions.json")) {
                            pathPermissions = entry1.toString();
                            System.out.println(pathPermissions);
                        }
                        if (entry1.getFileName().toString().contains("#")) {
                            if (entry1.getFileName().toString().contains(".json")){
                                pathApp=entry1.toString();
                                System.out.println(pathApp);
                                projectAppJSONFile =pathApp;
                            }

                        }
                        if (entry1.getFileName().toString().matches("device.json")) {
                            pathDevice= entry1.toString();
                            System.out.println(pathDevice);
                        }
                        if (entry.getFileName().toString().matches(".json")) {
                            pathApp=entry.getFileName().toString();
                            System.out.println(pathApp);
                        }
                    }
                }
                else {

                    if (entry.equals(path))
                        continue;
                    if (entry.getFileName().toString().matches("GreendroidResultTrace[0-9]+\\.csv")) {
                        list.add(entry.toString());
                    }
                    if (entry.getFileName().toString().matches("appPermissions.json")) {
                        pathPermissions = entry.getFileName().toString();
                        System.out.println(pathPermissions);
                    }
                    if (entry.getFileName().toString().contains("#")) {
                        if (entry.getFileName().toString().contains(".json")){
                            pathApp=entry.toString();
                            System.out.println(pathApp);
                            projectAppJSONFile =pathApp;
                        }

                    }
                    if (entry.getFileName().toString().matches("device.json")) {
                        pathDevice= entry.getFileName().toString();
                        System.out.println(pathDevice);
                    }
                }
            }
            stream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
       /* for (String l: list) {
            System.out.println("ficheiro " +l);
        }*/

        if (mergeOldRuns){
            resultDirPath+="oldRuns/";
            mergeOldRuns = false;
            List<String> x = getAllCsvsAndSendAppToDB(resultDirPath);
            list.addAll(x);
            mergeOldRuns = true;
        }


       grr.app=GreenSourceAPI.sendApplicationToDB( GreenSourceAPI.loadApplication(pathApp).toJSONString());
        GreenSourceAPI.sendAppPermissionsToDB( GreenSourceAPI.loadAppPermissions(pathPermissions).toJSONString());
       grr.device=GreenSourceAPI.sendDeviceToDB( GreenSourceAPI.loadDevice(pathDevice).toJSONString());
        return list;
    }

    public static String getApplication(String path){
        String [] x = path.split("/");
        return x.length>0? x[x.length-1]:"unknown";
    }

    public static void main(String[] args) {
        Utils u = new Utils();
        //energyGreadyAPIS = u.parseAndroidApis();
        GreenSourceAPI.operationalBackend = true; //TODO
        mergeOldRuns = false; // TODO
        if (args.length>2) {
            isTestOriented = args[0].equals("-TestOriented");
            if(!isTestOriented)
                folderPrefix="Method";
            resultDirPath =args[1];
            applicationID =getApplication(resultDirPath);
            stoppedState = args[2].equals("-Monkey")?2:0;
            monkey=stoppedState==2;
            folderPrefix = monkey? "Monkey" + folderPrefix : folderPrefix;
           // boolean analyzeOldRuns=  args[2].equals("-oldRuns");
            allMethodsDir = resultDirPath + "/all/";
            allmethods = loadMethods(allMethodsDir);

            if (isTestOriented) {
                try {
                    try{
                        acu = ((APICallUtil) new APICallUtil().fromJSONObject(new APICallUtil().fromJSONFile((projectAppJSONFile))));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    grr.project = ((JSONObject) GreenSourceAPI.sendProjectToDB(acu.proj.toJSONObject("").toJSONString()).get(0));

                    List<String> allcsvs = getAllCsvsAndSendAppToDB(resultDirPath); // this send apppermissions.json
                    grr.test =  Utils.getTest();
                    grr.test = GreenSourceAPI.sendTestToDB(grr.test.toJSONString());

                    grr.methods.addAll(Utils.getAppMethodsAndMetrics(acu));
                    testOriented(allcsvs);

                } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
//                  System.out.println("[Analyzer] Error parsing the file. Please run again or restart Trepn");
                    e.printStackTrace();
                }
            } else {
                List<String> allcsvs = getAllCsvsAndSendAppToDB(resultDirPath);
                try{
                    grr.test =  Utils.getTest();
                    grr.test = GreenSourceAPI.sendTestToDB(grr.test.toJSONString());
                    acu = ((APICallUtil) new APICallUtil().fromJSONObject(new APICallUtil().fromJSONFile((projectAppJSONFile))));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                grr.methods.addAll(Utils.getAppMethodsAndMetrics(acu));
                methodOriented(allcsvs);
                // methodOriented(Arrays.copyOfRange(args, 3, args.length));
            }

            //System.out.println(acu);
        }
        else {
            System.out.println("Bad argument length for Greendroid Analyzer! Usage ->  [-(Test|Method)Oriented] resultsdir [-Monkey|XXX]");
        }
    }

}
