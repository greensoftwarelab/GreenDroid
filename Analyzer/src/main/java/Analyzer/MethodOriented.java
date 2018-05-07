package Analyzer;

import Metrics.GDConventions;
import Metrics.MethodInfo;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by rrua on 22/06/17.
 */
public class MethodOriented {

    //public List<List<Consumption>> states = new ArrayList<>(); // lista para guardar cada uma das execucoes
    public static Map<String, Set<Integer>> nInvocaoes = new HashMap<>(); // <method, states>
    public static PairMetodoInt ultimo = new PairMetodoInt("",0);
    public static Map<String, Double []> methodInfos = new HashMap<>();
    public static final String methodFilename = GDConventions.MethodOutputName;





    // receives filenames as args
    public static void methodOriented (List<String> args) throws FileNotFoundException {

        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");

        for (String file : args ){
            if(!file.matches(".*.csv.*")){
                continue;
            }
            System.out.println("Processing " + file);
            List<List<PairMetodoInt>> states = new ArrayList<>(); // lista para guardar cada uma das execucoes
            List<Consumption>  consumptions = new ArrayList<Consumption>(); // todos os consumos
            CsvParser parser = new CsvParser(settings);
            List<String[]> resolvedData = null;
            resolvedData = parser.parseAll(new FileReader(file));
            String[] row = new String[32];
//            String[] rowj = new String[32];
            HashMap<String, Pair<Integer, Integer>> columns = null;
            try {
                columns= Utils.fetchColumns(resolvedData);
            }
            catch (Exception e){
                System.out.println("[ANALYZER] Error fetching columns. Result csv might have an error");
            }
            List<PairMetodoInt> pares = new ArrayList<PairMetodoInt>();
            for (int i = 4; i < resolvedData.size() && resolvedData.get(i).length>30 && (resolvedData.get(i)[29]!=null) ; i++) {
                row = resolvedData.get(i);
                // se ha amostra de consumo de energia
                if(row[Utils.getMatch(columns,Utils.batteryPower).first]!=null){
                    Consumption com =  Analyzer.getDataFromRow(columns, row);
                    com.setTimeBatteryPowerRaw(Integer.parseInt(row[Utils.getMatch(columns,Utils.batteryPower).first]));
                    com.setBatteryPowerRaw(Integer.parseInt(row[Utils.getMatch(columns,Utils.batteryPower).second]));
                    consumptions.add(com);
                    /*int timeTrepn = Integer.parseInt(row[Utils.getMatch(columns,Utils.batteryRemaing).first]);
                    int timeBatttery = Integer.parseInt(row[Utils.getMatch(columns,Utils.batteryPower).first]);
                    double watts = Double.parseDouble(row[Utils.getMatch(columns,Utils.batteryPower).second]);
                    // int delta = Integer.parseInt(row[8]);
                    int delta = 0;
                    int timeState = Integer.parseInt(row[Utils.getMatch(columns,Utils.stateInt).first]);
                    String method = row[ Utils.getMatch(columns,Utils.stateDescription).second] !=null ? new String(row[Utils.getMatch(columns,Utils.stateDescription).second]) : "";
                    int b = row[Utils.getMatch(columns,Utils.stateInt).second] !=null ? Integer.parseInt(row[Utils.getMatch(columns,Utils.stateInt).first]) : 0;
                    Consumption c = new Consumption(watts, b, method, timeTrepn, timeBatttery, delta, timeState, i-4);
                    consumptions.add(c);*/
                }
                if(row[Utils.getMatch(columns,Utils.stateInt).first] != null && row[Utils.getMatch(columns,Utils.stateDescription).second] != null){
                    //add to invocation list
                    if (nInvocaoes.get(row[Utils.getMatch(columns,Utils.stateDescription).second])!=null){
                        nInvocaoes.get(row[Utils.getMatch(columns,Utils.stateDescription).second]).add(Integer.parseInt(row[Utils.getMatch(columns,Utils.stateInt).second]));
                    }
                    else {
                        Set<Integer> h = new HashSet<>();
                        h.add(Integer.parseInt(row[Utils.getMatch(columns,Utils.stateInt).second]));
                        nInvocaoes.put(row[Utils.getMatch(columns,Utils.stateDescription).second],h);
                    }

                    String method = new String(row[Utils.getMatch(columns,Utils.stateDescription).second]);
                    int state =  Integer.parseInt(row[Utils.getMatch(columns,Utils.stateInt).second]);
                    if (state==0) continue;
                    int j = i+1;
                    String metodoAnterior = method;
                    pares=new ArrayList<PairMetodoInt>();
                    String[] rowj = resolvedData.get(j);
                    int stateAnterior = state;
                    if((igualAnterior(states,method,state)|| fechaAnterior(states,method,state)))
                        continue;

                    if (rowj.length<=Utils.getMatch(columns,Utils.stateDescription).second || rowj[Utils.getMatch(columns,Utils.stateDescription).second] ==null){
                        pares.add(new PairMetodoInt(metodoAnterior,stateAnterior, Integer.parseInt(row[Utils.getMatch(columns,Utils.stateInt).first])));
                        states.add(pares);
                        continue;
                    }
                    String  metodo2 = rowj[Utils.getMatch(columns,Utils.stateDescription).second];
                    int state2 = Integer.parseInt(rowj[Utils.getMatch(columns,Utils.stateInt).second]);
                    int tempo2 = Integer.parseInt(rowj[Utils.getMatch(columns,Utils.stateInt).first]);
                    pares.add(new PairMetodoInt(metodoAnterior,stateAnterior, Integer.parseInt(row[Utils.getMatch(columns,Utils.stateInt).first])));
                    int k = j;
                    while (state2!=stateAnterior-1 && k<resolvedData.size()){

                        pares.add(new PairMetodoInt(metodo2,state2,tempo2));
                        rowj = resolvedData.get(++j);
                        if(rowj.length<Utils.getMatch(columns,Utils.stateInt).second)
                            break;
                        if (rowj[Utils.getMatch(columns,Utils.stateInt).second]==null)
                            break;
                        state2 = Integer.parseInt(rowj[Utils.getMatch(columns,Utils.stateInt).second]);
                        metodo2 = rowj[Utils.getMatch(columns,Utils.stateDescription).second];
                        tempo2 = Integer.parseInt(rowj[Utils.getMatch(columns,Utils.stateInt).first]);
                        k++;
                    }
                    if(state2==stateAnterior-1 &&  metodoAnterior.equals(metodo2))
                        pares.add(new PairMetodoInt(metodo2,state2,tempo2));

                    // adicionar a lista de listas
                    if(pares.size()>0)
                        states.add(pares);
                }
            }
            // calcular consumos

            Map<String,Double> consumos = new HashMap<>();
            for (List<PairMetodoInt> lista : states){
                double potencia =0;
                int tempofrente = 0;
                String metodofrente ="";
                String met = lista.get(0).metodo;
                int tempo = lista.get(0).timeState;
                int estadoantes =0;
                for (int i = 1; i < lista.size(); i++) {
                    //if(estadoantes==lista.get(i-1).state) continue;
                    tempofrente = lista.get(i).timeState;
                    Consumption closest = perto(consumptions,tempofrente);
                    addClosestConsumption(met,closest);
                    double potfrente = closest.getBatteryPowerRaw();
                    double deltat = tempofrente-tempo;
                    deltat = (double) deltat /1000;
                   // potencia += deltat * potfrente;
                    double watt = (double) (potfrente)/((double) 1000000);
                    potencia += ((double) deltat) * (watt);
                    tempo = tempofrente;
                    estadoantes = lista.get(i).state;
                }
                if (!consumos.containsKey(met)){
                    consumos.put(met,potencia);
                    methodInfos.get(met)[12] =1.0;
                }
                else {
                    consumos.put(met,consumos.get(met)+potencia);
                    methodInfos.get(met)[12] +=1.0;
                }
            }

            FileWriter fw = null;
            try {
                File f = new File(Analyzer.resultDirPath + "/" + methodFilename);
                System.out.println(f.getAbsolutePath());
                if (!f.exists()){
                    f.createNewFile();
                }
                fw = new FileWriter(f.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<String> l = new ArrayList<>();
            l.add("Method");l.add("Consumption (J)");
            try {
               Analyzer.write(fw,l);
            } catch (IOException e) {
                e.printStackTrace();
            }
            l.clear();

            System.out.println("-------------Test " + file+ " CONSUMPTION" + "-------------");
            System.out.println("----------------------------------------------------");

            for (String x : consumos.keySet()){
                System.out.println("| Method  " + x +" Total Consumption (J) : " +  consumos.get(x) + " J|");
               // System.out.println("--Method :" + x +"   Total consumption : " + consumos.get(x) + " J --");
                methodInfos.get(x)[10] = consumos.get(x);
                methodInfos.get(x)[10] = consumos.get(x);
                l.add(x); l.add((String.valueOf(consumos.get(x))));
                try {
                    Analyzer.write(fw,l);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                l.clear();

            }

            l.add("Class"); l.add("Method"); l.add("Times invoked");
            l.add("CC");l.add("LoC"); l.add("AndroidAPIs"); l.add("N args");

            FileWriter fwApp = null;
            try {
                File f = new File(Analyzer.resultDirPath + "/" + methodFilename);
                System.out.println(f.getAbsolutePath());
                if (!f.exists()){
                    f.createNewFile();
                }
                fwApp= new FileWriter(f.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            for ( String s : methodInfos.keySet()){
                try {
                    Analyzer.write(fwApp,l);
                    l.clear();
                    String [] xx = s.split("<");
                    String methodName = xx[xx.length-1].replace(">","");
                    l.add(xx[0]); l.add(methodName) ;l.add(String.valueOf(methodInfos.get(s)[12]));
                    String s1 = s.replaceAll("<.*?>", "");
                    MethodInfo mi = Analyzer.acu!=null? Analyzer.acu.getMethodOfClass(methodName,s1) : new MethodInfo();
                    l.add(String.valueOf(mi.cyclomaticComplexity)); l.add(String.valueOf(mi.linesOfCode+(Analyzer.isTestOriented?1:0))); l.add(String.valueOf(mi.androidApi.size())); l.add(String.valueOf(mi.nr_args));

                } catch (IOException  | NullPointerException exc) {
                    exc.printStackTrace();
                }
            }

            try {
                Analyzer.write(fwApp,l);
                l.clear();
                fwApp.flush();
                fwApp.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            System.out.println("\nTests Total coverage : " +  (methodCoverage()*100)+ " %");
            try {
                fw.flush();
                fw.close();
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        }
        nInvocaoes.clear();
    }

    private static void addClosestConsumption(String methodName, Consumption c) {

        if(methodInfos.containsKey(methodName)){
            Double [] returnList = methodInfos.get(methodName);
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
            methodInfos.put(methodName,returnList);
        }
        else {
            Double [] returnList = new Double[13];
            returnList[0] = ((double) ((c.getWifiState() > 1)? 1 :0));
            returnList[1] = ((double) ((c.getMobileDataState() > 1)? 1 :0));
            returnList[2] = ((double) ((c.getScreenState() > 1)? 1 :0));
            returnList[3] = ((double) ((c.getBatteryStatus() > 1)? 1 :0));
            returnList[4] = (double)c.getWifiRSSILevel();
            returnList[5] = (double)c.getMemUsage();
            returnList[6] = ((double) ((c.getBluetoothState() > 1)? 1 :0));
            returnList[7] = (double)c.getGpuFreq();
            returnList[8] = (double) c.getCpuLoadNormalized();
            returnList[9] = ((double) ((c.getGpsState() > 1)? 1 :0));
            methodInfos.put(methodName,returnList);

        }

    }


    public static double methodCoverage( ){
        HashSet<String> set = new HashSet<>();
        Path path = Paths.get(Analyzer.allMethodsDir + "allMethods.txt");
        try {
            try (Stream<String> lines = Files.lines (path, StandardCharsets.UTF_8))
            {
                for (String line : (Iterable<String>) lines::iterator)
                {
                    set.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        double percentageCoverage = ((double) nInvocaoes.size()/(double)(set.size()));
        return percentageCoverage;
    }


    private static boolean fechaAnterior(List<List<PairMetodoInt>> states, String method, int state) {
       boolean b =false;
        if (states.size()>1) {
            b = states.get(states.size() - 1).get(states.get(states.size() - 1).size() - 1).metodo.equals(method) && states.get(states.size() - 1).get(states.get(states.size() - 1).size() - 1).state == state
                    && state == states.get(states.size() - 1).get(0).state;
        }
        if (!b) {
            if (states.size() > 1) {
                for (List<PairMetodoInt> l : states) {
                    if (l.get(0).metodo.equals(method)) {
                        if (l.get(l.size() - 1).state == state && l.get(l.size() - 1).metodo.equals(method))
                            return true;
                    }
                }
            }
        }
         return b;
    }

    private static boolean igualAnterior(List<List<PairMetodoInt>> states, String method, int state) {
        if(ultimo.state==state&&method.equals(ultimo.metodo)){

            ultimo = new PairMetodoInt(method,state);
            return true ;

        }
        else {
            ultimo = new PairMetodoInt(method,state);
            if (states.size() > 1) {
                boolean b = states.get(states.size() - 1).get(0).metodo.equals(method) && states.get(states.size() - 1).get(0).state == state;
                return b;
            } else return false;
        }
    }


    public static Consumption perto( List<Consumption> consumptions, int tempo){
        int maisperto=100000, index =0;
        for (int i = 0; i< consumptions.size();i++){
            if(Math.abs(consumptions.get(i).getTimeBatttery()-tempo) < maisperto){
                maisperto = Math.abs(consumptions.get(i).getTimeBatttery()-tempo);
                //alternativeStart =closestStart;
                index = i;
            }
        }

        return consumptions.get(index);
    }


}
