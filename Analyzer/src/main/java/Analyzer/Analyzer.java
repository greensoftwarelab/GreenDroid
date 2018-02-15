package Analyzer;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class Analyzer {


    public static HashMap<String,Integer> allTracedMethods = new HashMap<>(); // Method name -> times invoked
//    public  static Set<String> allTracedMethods = new HashSet<>();
    public  static String resultDirPath = "results/" ;
    public static Double [] returnList = new Double[13];
    public static HashMap<String , List<Double []>>  globalReturnList = new HashMap<>();
    public static String allMethodsDir = "";
    public static List<String> alltests= new ArrayList<>();
    public static HashSet<String> allmethods= new HashSet<>();


    private static List<String> loadTests(String fileDir) throws Exception {
        alltests = new ArrayList<String>();
        Path path = Paths.get(fileDir + "/TracedTests.txt");
        try {
            try (Stream<String> lines = Files.lines (path, StandardCharsets.UTF_8))
            {
                for (String line : (Iterable<String>) lines::iterator)
                {
                    alltests.add(line);
                }
            }
        } catch (IOException e) {
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
            e.printStackTrace();
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


    public static void addList(Map<String, List<Consumption>> h , List<Consumption> l){

        if(h.containsKey(l.get(0).getRunningMethod())){
            h.get(l.get(0).getRunningMethod()).addAll(l);
        }

        else {
            List<Consumption> al = new ArrayList<>();
            al.addAll(l);
            h.put(l.get(0).getRunningMethod(), al);
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


    public static void addSet(Map<String, Set<Consumption>> h , Set<Consumption> l, String method){

        if(h.containsKey(method)){
            h.get(method).addAll(l);
        }

        else {
            Set<Consumption> al = new HashSet<>();
            al.addAll(l);
            h.put(method, al);
        }

    }


    private static void testOriented( List<String> args) throws NullPointerException{
        CsvParserSettings settings = new CsvParserSettings();
        Map<Integer,Integer> timeConsumption = new HashMap<>();
        List <Consumption> consumptionList = new ArrayList<>();
        Set<Integer> timeStates = new TreeSet<>();
        settings.getFormat().setLineSeparator("\n");
        FileWriter fw = null;

        try {
            File f = new File(resultDirPath+"/Testresults.csv");
            if (!f.exists()){
                f.createNewFile();
            }
             fw = new FileWriter(resultDirPath+"/Testresults.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> l = new ArrayList<>();
        l.add("Test Number");l.add("Consumption (J)"); l.add("Time (ms)"); l.add("Method coverage (%)");l.add("Wifi?");l.add("Mobile Data?");l.add("Screen state");l.add("Battery Charging?");l.add("Avg RSSI Level");l.add("Avg Mem Usage");l.add("Bluetooth?");l.add("Avg gpu Load");l.add("Avg CPU Load");l.add("GPS?");
        try {
            write(fw,l);
        } catch (IOException e) {
            e.printStackTrace();
        }
        l.clear();
        for (int j = 0; j <args.size() ; j++) {

            for (int i = 0; i < 13; i++) { returnList[i]=0.0;}

            if(!args.get(j).matches(".*.csv.*") || args.get(j).matches(".*Testresults.csv")) continue;
            System.out.println("--- " + args.get(j) + " ---");
            CsvParser parser = new CsvParser(settings);
            // 3rd, parses all rows of data in selected columns from the CSV file into a matrix
            List<String[]> resolvedData = null;
            try {
                File f  = new File(args.get(j));
                resolvedData = parser.parseAll(new FileReader(f.getAbsolutePath()));
            } catch (FileNotFoundException e) {
                System.out.println("[ANALYZER]: File Not Found: There is no " +args.get(j) +" csv file in directory! to generate results");
                continue;
            }

            HashMap<String, Pair<Integer, Integer>> columns = null;
            try {
                columns= Utils.fetchColumns(resolvedData);
            }
            catch (Exception e){
                System.out.println("[ANALYZER] Error fetching columns. Result csv might have an error");
            }

            String number = args.get(j).replaceAll(".+GreendroidResultTrace(.+)\\..+","$1");

            int timeStart = 0;
            int timeEnd = 0;
            String[] row = new String[32];
            boolean state = false;
            String method = "";
            boolean first= true, stop = true;
            for(int i =4; i< resolvedData.size();i++) {
                row  = resolvedData.get(i);
                if(row.length<=5) break;
                if(row[ Utils.getMatch(columns,Utils.stateDescription).first]!=null&&row[ Utils.getMatch(columns,Utils.stateDescription).second]!=null) { // if this line has state and descrip
                    state = Integer.parseInt(row[Utils.getMatch(columns,Utils.stateInt).second]) > 0;
                    method = new String(row[Utils.getMatch(columns,Utils.stateDescription).second]);

                    if(state&&method.equals("started") && first){
                        //int timeTrepn = Integer.parseInt(row[0]); // removed, might not have
//                        int timeBatttery = Integer.parseInt(row[Utils.getMatch(columns,Utils.batteryPower).first]);
               //         int timeBatttery = Integer.parseInt(row[Utils.getMatch(columns,Utils.batteryPower).first]);
              //          int watts = Integer.parseInt(row[Utils.getMatch(columns,Utils.batteryPower).second]);
//                        int delta = Integer.parseInt(row[8]);
                         timeStart  = Integer.parseInt(row[Utils.getMatch(columns,Utils.stateDescription).first]);
//                         started = new Consumption(0,state,method,0, 0, 0, timeStart);
                         first=false;
                 //       timeConsumption.put(timeBatttery,watts);
                    }
                    else if(!state && method.equals("stopped") ){
//                        int timeTrepn = Integer.parseInt(row[0]);
                          //int timeBatttery = Integer.parseInt(row[Utils.getMatch(columns, Utils.batteryPower).first]);
                         // int watts = Integer.parseInt(row[Utils.getMatch(columns, Utils.batteryPower).second]);
//                        int delta = Integer.parseInt(row[8]);
                          timeEnd  = Integer.parseInt(row[Utils.getMatch(columns,Utils.stateDescription).first]);
//                        ended = new Consumption(0,state,method,0, 0, 0, timeEnd);
                          i= resolvedData.size()+1;
                          stop=false;
//                        consumptionList.add(getDataFromRow(columns,row));
                  //      timeConsumption.put(timeBatttery,watts);
                    }

                    if(Utils.getMatch(columns,Utils.batteryPower)!=null&&row[Utils.getMatch(columns, Utils.batteryPower).first]!=null) {
                        int timeBatttery = Integer.parseInt(row[Utils.getMatch(columns, Utils.batteryPower).first]);
                        int watts = Integer.parseInt(row[Utils.getMatch(columns, Utils.batteryPower).second]);
                        timeConsumption.put(timeBatttery, watts);
                        consumptionList.add(getDataFromRow(columns,row));
                    }
                    timeStates.add(Integer.parseInt(row[Utils.getMatch(columns,Utils.stateDescription).first]));
                }
                if(row[Utils.getMatch(columns,Utils.batteryPower).first]!=null && row[Utils.getMatch(columns,Utils.batteryPower).first]!=null){
                    // add power measures to Map
                    int watts = Integer.parseInt(row[Utils.getMatch(columns,Utils.batteryPower).second]);
                    int timeBatttery = Integer.parseInt(row[Utils.getMatch(columns,Utils.batteryPower).first]);
                    timeConsumption.put(timeBatttery,watts);

                }
                if(!state && method.equals("stopped"))
                    break;
            }
            // calcular a medida de bateria mais aproximada
            int closestStart = 1000000, closestStop = 1000000;
            int difStart = 1000000, diffEnd = 1000000;
            int alternativeEnd = 0, alternativeStart=0;
            for (Integer i : timeConsumption.keySet()) {
                if(Math.abs(timeStart-i) <= difStart){
                    difStart = Math.abs(timeStart-i);
                    alternativeStart =closestStart;
                    closestStart =  i;
                }
                if(Math.abs(timeEnd-i) < diffEnd){
                    if(i<=timeEnd){
                        closestStop = i;
                        diffEnd = Math.abs(timeEnd-i);
                    }
                    else {
                        alternativeEnd = i;
                    }
                }
            }
            if(! timeConsumption.containsKey(closestStart) || ! timeConsumption.containsKey(closestStop)){
                System.out.println("[Analyzer] Warning: Ignoring test " + number + "; Missing columns in test results .csv file");
                continue;
            }

            int startConsum = timeConsumption.get(closestStart);
            int stopConsum  = timeConsumption.get(closestStop);
//            int total = stopConsum-startConsum;
            int total = stopConsum;
            int totaltime = timeEnd-timeStart;


            // if is a relevant test (in terms of time)

            if(total<=0 && alternativeEnd!=0){
                 stopConsum  = timeConsumption.get(alternativeEnd);
//                 total = stopConsum-startConsum;.csv 
                total = startConsum;
                 totaltime = timeEnd-timeStart;
            }
            if(total<=0 && alternativeStart!=0){
                startConsum  = timeConsumption.get(alternativeStart);
//                total = stopConsum-startConsum;
                total = startConsum;
                totaltime = timeEnd-timeStart;
            }
            TreeSet<Integer> ts = (TreeSet<Integer>) timeStates;


            int totalconsum = 0;
            double delta_seconds =0, watt =0;
            int toma =0, delta = 0, ultimo = ts.first();
            for(Integer i : ts){
                toma =i;
                delta = toma -ultimo;
                delta_seconds = ((double)totaltime/((double)1000));
                watt = (double) (closestMemMeasure(timeConsumption,toma))/((double) 1000000);
                //totalconsum+= (delta * (closestMemMeasure(timeConsumption,toma)));
                totalconsum+= (delta_seconds * watt);
                ultimo = i;
            }


            //double watt = (double) total/((double) 1000000);
            double joules = ((double)(((double)totaltime/((double)1000))) * (watt));

            //double joules = totalconsum;
            if (total>0)
            {
                System.out.println("---------"+ " TEST CONSUMPTION" + "-----------");
                //System.out.println("--"+ " Filename: " + args[j] + " -----------");
                System.out.println("--"+ " Test Name: " + getTestName(number)+" --");
                System.out.println("----------------------------------------------");
                System.out.println("| Test Total Consumption (J) : " +joules+ " J|");
               // System.out.println("| Test Total Consumption (W) : " + watt + " W|");
                System.out.println("| Test Total Time (ms)       : " + totaltime + " ms|");
                System.out.println("----------------------------------------------");

            }

            // get TracedMethodsX.txt file in the folder of the csv file
            File f = new File(args.get(j));
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
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            double totalcoverage = (methodCoverageTestOriented(p)*100);
            System.out.println("---------------Method Coverage of Test------------------");
            System.out.println("percentage: " +  totalcoverage+ " %");
            System.out.println("------------------------------------------------");
            showData(consumptionList);

            returnList[10] = ((double) totaltime);
            returnList[11] =  joules;
            returnList[12] =  totalcoverage;

            /* l.add(String.valueOf(getTestName(number)));
            l.add(String.valueOf(total > 0 ? joules : 0));
            l.add( String.valueOf(total > 0 ?totaltime : 0 ));
            l.add(String.valueOf(totalcoverage));
            for (int i = 0; i < hardwareResults.length ; i++) { l.add(String.valueOf(hardwareResults[i])); }
            try {
                write(fw,l);
            } catch (IOException e) {
                e.printStackTrace();
            }
            l.clear();
*/
            copyToGlobalReturnList(getTestName(number));
            // end of csv file processing
        }

        // final Results

        //  print to file the results and name of test

        for (String testName: globalReturnList.keySet()) {

            Double [] testResults = showGlobalData(testName);
            l.add(String.valueOf(testName));
            l.add(String.valueOf(testResults[11]));
            l.add( String.valueOf(testResults[10]));
            l.add( String.valueOf(testResults[12]));
            for (int i = 0; i < 10 ; i++) { l.add(String.valueOf(testResults[i])); }
            try {
                write(fw,l);
            } catch (IOException e) {
                e.printStackTrace();
            }
            l.clear();
        }


        double tc = totalCoverage()*100;
        System.out.println("\n///////////////////////////////////////////////");
        System.out.println("---------------Total Coverage------------------");
        System.out.println("percentage: " + (tc) + " %");
        System.out.println("------------------------------------------------");

        try {
            l.add("Total method coverage"); l.add(String.valueOf(0)); l.add( String.valueOf(0)); l.add(String.valueOf(tc));
            write(fw,l);
            l.clear();
            l.add(""); l.add(""); l.add(""); l.add( "");l.add("");
            write(fw,l);
            l.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        l.add("Class"); l.add("Method"); l.add("Times invoked");
        for ( String s : allTracedMethods.keySet()){
            try {
                write(fw,l);
                l.clear();
                String [] xx = s.split("<");
                l.add(xx[0]); l.add(xx[xx.length-1].replace(">","")) ;l.add(String.valueOf(allTracedMethods.get(s)));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            write(fw,l);
            l.clear();
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Consumption getDataFromRow( HashMap<String, Pair<Integer, Integer>> columns,String[] row) {
        double wifiState = Utils.getMatch(columns, Utils.wifiState)!=null? (row[Utils.getMatch(columns, Utils.wifiState).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.wifiState).second])) : returnList[0]) : returnList[0];
              //  Integer.parseInt(row[Utils.getMatch(columns, Utils.wifiState).second]);
        double mobileData = Utils.getMatch(columns, Utils.mobileData)!=null? (row[Utils.getMatch(columns, Utils.mobileData).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.mobileData).second])) : returnList[1]) : returnList[1];
        double screenState = Utils.getMatch(columns, Utils.screenState)!=null?  (row[Utils.getMatch(columns, Utils.screenState).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.screenState).second])) : returnList[2]) : returnList[2];
        double batteryStatus = Utils.getMatch(columns, Utils.batteryStatus)!=null?  (row[Utils.getMatch(columns, Utils.batteryStatus).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.batteryStatus).second])) : returnList[3]) : returnList[3];
        double wifiRSSI = Utils.getMatch(columns, Utils.wifiRSSILevel)!=null?  (row[Utils.getMatch(columns, Utils.wifiRSSILevel).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.wifiRSSILevel).second])) : returnList[4]) : returnList[4];
        double memUsage = Utils.getMatch(columns, Utils.memory)!=null?  (row[Utils.getMatch(columns, Utils.memory).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.memory).second])) : returnList[5]) : returnList[5];
        double bluetooth = Utils.getMatch(columns, Utils.bluetoothState)!=null?  (row[Utils.getMatch(columns, Utils.bluetoothState).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.bluetoothState).second])) : returnList[6]) : returnList[6];
        double gpuLoad = Utils.getMatch(columns, Utils.gpuLoad)!=null? (row[Utils.getMatch(columns, Utils.gpuLoad).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.gpuLoad).second])) : returnList[7]) : returnList[7];
        double cpuLoadNormalized = Utils.getMatch(columns, Utils.cpuLoadNormalized)!=null?  (row[Utils.getMatch(columns, Utils.cpuLoadNormalized).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.cpuLoadNormalized).second])) : returnList[8]) : returnList[8];
        double gps = Utils.getMatch(columns, Utils.gpsSate)!=null? (row[Utils.getMatch(columns, Utils.gpsSate).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.gpsSate).second])) : returnList[9]) : returnList[9];
        return new Consumption(((int) memUsage), ((int) mobileData), ((int) wifiState), ((int) wifiRSSI), ((int) screenState), 0, 0, ((int) batteryStatus), ((int) bluetooth), ((int) gpuLoad), ((int) gps), ((int) cpuLoadNormalized));

    }



    public static void showData(List<Consumption> list){

        for (Consumption c :list) {
            returnList[0] = (double)((returnList[0] > 1) || (c.getWifiState() > 1)? 1 :0);
            returnList[1] = (double)(returnList[1] > c.getMobileDataState()? returnList[1] : c.getMobileDataState());
            returnList[2] = (double)((returnList[2] > 0) || (c.getScreenState() > 0)? 1 :0);
            returnList[3] = (double)((returnList[3] > 0) || (c.getBatteryStatus() > 0)? 1 :0);
            returnList[4] += c.getWifiRSSILevel();
            returnList[5] += c.getMemUsage();
            returnList[6] = (double)(((returnList[6] > 0) || ((c.getBluetoothState() > 1) )) ? 1 : 0);
            returnList[7] += c.getGpuFreq();
            returnList[8] += c.getCpuLoadNormalized();
            returnList[9] = (double)((returnList[9] > 0) || (c.getGpsState() > 0)? 1 :0);
        }
        returnList[4] = returnList[4] / (list.size()>0? list.size() : 1);
        returnList[5] = returnList[5] / (list.size()>0? list.size() : 1);
        returnList[7] = returnList[7] / (list.size()>0? list.size() : 1);
        returnList[8] = returnList[8] / (list.size()>0? list.size() : 1);

    }


    public static  Double [] showGlobalData(String testName){

        Double [] finalReturnList = new Double[13];
        for (int i = 0; i < finalReturnList.length ; i++) {
            finalReturnList[i] = 0.0;
        }

        for( Double [] list : globalReturnList.get(testName)){
            finalReturnList[0] = (finalReturnList[0] > 1) || (list[0] > 1)? 1 :0.0;
            finalReturnList[1] = finalReturnList[1] > list[1]? finalReturnList[1] :  list[1];
            finalReturnList[2] = (finalReturnList[2] > 1) || (list[2] > 1)? 1 :0.0;
            finalReturnList[3] = (finalReturnList[3] > 1) || (list[3] > 1)? 1 :0.0;
            finalReturnList[4] += list[4];
            finalReturnList[5] += list[5];
            finalReturnList[6] = (finalReturnList[6] > 1) || (list[6] > 1)? 1 :0.0;
            finalReturnList[7] += list[7];
            finalReturnList[8] += list[8];
            finalReturnList[9] = (finalReturnList[9] > 1) || (list[9] > 1)? 1 :0.0;
            finalReturnList[10] += list[10];
            finalReturnList[11] += list[11];
            finalReturnList[12] = finalReturnList[12] > list[12]? finalReturnList[12] :  list[12];

        }

        finalReturnList[4] = finalReturnList[4] / (globalReturnList.get(testName).size()>0? globalReturnList.get(testName).size() : 1);
        finalReturnList[5] = finalReturnList[5] / (globalReturnList.get(testName).size()>0? globalReturnList.get(testName).size() : 1);
        finalReturnList[7] = finalReturnList[7] / (globalReturnList.get(testName).size()>0? globalReturnList.get(testName).size() : 1);
        finalReturnList[8] = finalReturnList[8] / (globalReturnList.get(testName).size()>0? globalReturnList.get(testName).size() : 1);
        finalReturnList[10] = finalReturnList[10] / (globalReturnList.get(testName).size()>0? globalReturnList.get(testName).size() : 1);
        finalReturnList[11] = finalReturnList[11] / (globalReturnList.get(testName).size()>0? globalReturnList.get(testName).size() : 1);

        return finalReturnList;
    }


    public static void addToMap(Map<String,TreeSet<Consumption>> map , Consumption c){

    if (map.containsKey(c.getRunningMethod())){
        map.get(c.getRunningMethod()).add(c);
    }
    else {
        TreeSet<Consumption> tr = new TreeSet<>(new ConsumptionComparator());
        tr.add(c);
        map.put(c.getRunningMethod(),tr);
    }

    }

    public Integer getUltimoIndice (Map<String,TreeSet<Consumption>> map, String metodo){
        return map.get(metodo).first().index;
    }




        public  static void methodOriented(String[] arg1) {
//

            try {
                MethodOriented.methodOriented(arg1);
            } catch (FileNotFoundException e) {
                System.out.println("[ANALYZER]: File Not Found: There is no .csv file in directory! to generate results");
              //s  e.printStackTrace();
            }

        }




    public static void copyToGlobalReturnList(String testName){
        Double [] newReturnList = new Double[13];
        for (int i = 0; i <returnList.length ; i++) {
            newReturnList[i] = returnList[i];
        }
        if(globalReturnList.get(testName)!=null){

            globalReturnList.get(testName).add(newReturnList);
        }
        else {
            List<Double []> l = new ArrayList<>();
            l.add(newReturnList);
            globalReturnList.put(testName, l);
        }
    }


// mapa timebattery, watts
    // retorna battery mais perto o tempo do metodo
public static double perto(Map<Integer,Double> timeConsumption, int time){
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
                    }
                    else
                        thisTest.put(line,1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // now I have all profiled methods, search for them in the allMethods file

        copyMethodMap(thisTest);
        double percentageCoverage = ((double)thisTest.size()/(double)(set.size()));
        return percentageCoverage;
    }



    public static  String getTestName(String number){
        if(alltests.size()>0 && Integer.parseInt(number)<= alltests.size()){
            return alltests.get(Integer.parseInt(number));
        }
        else
            return number;

    }

    public static double methodCoverage(Map<String,TreeSet<Consumption>> map ){

        ArrayList<String> arrayList = new ArrayList<>();
//        File file = new File("allMethods.txt" );
        Path path = Paths.get(allMethodsDir);
        int totalM = 0;
        try {
            try (Stream<String> lines = Files.lines (path, StandardCharsets.UTF_8))
            {
                for (String line : (Iterable<String>) lines::iterator)
                {
                    if (map.containsKey(line)){
                        arrayList.add(line);
                    }
                    totalM++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("totalm" + totalM);
//        System.out.println("size arr" + arrayList.size());
        double percentageCoverage = ((double) arrayList.size()/(double)(totalM));
    return percentageCoverage;
    }


    public static  List <String> getAllCsvs(String resultDirPath){

        /*File f = new File(resultDirPath);
        Path path = Paths.get(f.getAbsoluteFile().getParent());
        System.out.println("folde3r " + path);

        */
        List <String> list = new ArrayList<>( );
        Path path = Paths.get(resultDirPath);
        try{
            DirectoryStream<Path> stream;
            stream = Files.newDirectoryStream(path);
            // foreach file in resulDir folder
            for (Path entry : stream) {
                if(Files.isDirectory(entry)){ // if is a folder
                    //get files of that folder
                    DirectoryStream<Path> streamChild = null;
                    try {
                        streamChild = Files.newDirectoryStream(Paths.get(entry.toString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return list;
                    }
                    for (Path entry1 : streamChild) { //foreach file of that foler
                        if(entry1.getFileName().toString().matches("GreendroidResultTrace[0-9]+\\.csv")){
                            list.add(entry1.toString());
                        }
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

        return list;
    }

    public static void main(String[] args) {

        System.out.println("-----------");
        if (args.length>1) {
            boolean testOriented = args[0].equals("-TraceMethods");
            resultDirPath =args[1];
            System.out.println(resultDirPath);
            allMethodsDir = resultDirPath + "/all/";
            System.out.println(allMethodsDir);
            allmethods = loadMethods(allMethodsDir);
            try {
                alltests = loadTests(resultDirPath);
            }
            catch (Exception e) {
                System.out.println("[ANALYZER] Error tracing tests... Assuming order of tests instead of names");
            }

            if (testOriented) {
                try {
                    //testOriented(Arrays.copyOfRange(args, 3, args.length));
                    testOriented(getAllCsvs(resultDirPath));

                } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
//                    System.out.println("[Analyzer] Error parsing the file. Please run again or restart Trepn");
                    e.printStackTrace();
                }


            } else {

                methodOriented(Arrays.copyOfRange(args, 3, args.length));
            }
        }
        else {
            System.out.println("Bad argument length for Greendroid Analyzer! Usage ->  -TraceMethods resultsdir [pathAllMethods] [pathTocsv]*");
        }
    }


}
