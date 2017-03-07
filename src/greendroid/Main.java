/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid;

import greendroid.project.Project;
import greendroid.project.TestResult;
import greendroid.tools.FileUtils;
import java.io.IOException;
import java.util.LinkedList;
import greendroid.tools.Util;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class Main {
    private static int extFlag = 0;
    
    public static String tName = "_TRANSFORMED_";
    
    public static String projectsFile = "projects.csv";
    
    private static LinkedList<Project> projects = new LinkedList<Project>();
    
    public static String localFolder = "";
    
    public static long averageSecond = 0;
    
    private static Project getProject(String name, String pack, String testPack, String pathP, String pathT){
        Project res = null;
        for(Project p : projects){
            if(p.getPackage().equals(pack)){
                p.setTestPackage(testPack);
                p.setName(name);
                p.setPathProject(pathP);
                p.setPathTests(pathT);
                p.setTransPath(pathP+tName);
                p.setTransTestsPath(pathP+tName+"/tests");
                p.createAnalyzer(localFolder);
                res = p;
            }
        }
        if (res == null){
            res = new Project(name, pack, testPack, pathP, pathT, tName, localFolder);
            projects.add(res);
        }
        return res;
    }
    
    private static void loadAllProjectsInfo(String filename){
        try {
            //este método vai ler o JSON com os consumo indexados por app
            //e vai criar uma lista de projetos com isso
            projects = new LinkedList<>(Util.getProjectsFromJSON(filename));
        } catch (FileNotFoundException ex) {
            System.out.println("[GD] WARNING: File "+filename+" does not exist. Using empty database.");
            projects = new LinkedList<>();
        } catch (IOException ex) {
            System.out.println("Weird... Shouldn't catch this one...");
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void saveAllProjectsInfo(String file) throws IOException{
        StringBuilder builder = new StringBuilder();
        builder.append("{\"projects\":[\n");
        for(Project p : projects){
            builder.append(p.toJSON());
            builder.append(",\n");
        }
        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append("\n]}");
        FileUtils.writeFile(new File(file), builder.toString());
    }
    
    private static List<Long> getMeans(){
        List<Long> res = new ArrayList<>();
        Map<Integer, TestResult> results;
        for(Project p : projects){
             results = p.getTestsResults();
             for(Integer i : results.keySet()){
                 long c = results.get(i).getTotalConsumption();
                 double t = results.get(i).getTime();
                 long aux = (long)t*c;
                 res.add(aux);
             }
        }
        return res;
    }
    
    public static void main(String[] args){
        try{
            if(args.length != 6){
                System.err.println("[ERROR] Wrong Number of Arguments");
                return;
            }
            String name=args[0];
            String pack=args[1];
            String testPack=args[2];
            String pathP=args[3];
            String pathT=args[4];
            localFolder=args[5];
            String allProjectsFile=localFolder+"/projectsDB.json";

            System.out.println("[GD] GREENDROID - Testing Framework for Android Applications\n");

            loadAllProjectsInfo(allProjectsFile);
            Project p = getProject(name, pack, testPack, pathP, pathT); 

            System.out.println("[GD] Considering "+(projects.size()-1)+" already analyzed projects");
            if(projects.size() > 0){

                System.out.println("[GD] Analyzing "+p.getName()+" project");
                p.runAnalyzer();
                System.gc();
                System.out.println("[GD] DONE!");

                averageSecond = Util.average(getMeans());
                System.out.println("[GD] GETTING FINAL RESULTS");
                System.out.println("[GD] Generating results for " + p.getName());
                p.createFinalResults();
                saveAllProjectsInfo(allProjectsFile);
                System.out.println("[GD] DONE!");
            //load the results???*/
            }
        } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    
}
