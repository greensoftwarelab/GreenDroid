/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst;

import Metrics.APICallUtil;
import Metrics.AndroidProjectRepresentation.ProjectInfo;
import Metrics.AndroidProjectRepresentation.AppInfo;
import Metrics.GDConventions;

import jInst.transform.InstrumentGradleHelper;
import jInst.transform.InstrumentHelper;
import jInst.util.FileUtils;
import jInst.util.XMLParser;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marco
 */
public class JInst {


    public static String getProjectID(String path){
        String [] x = path.split("/");
        return x.length>0? ( x[x.length-1].equals("latest")? x[x.length-2]:x[x.length-1]) :"unknown";

    }




    public static void main(String[] args) {
        String projType = args[0];
        switch (projType){
            case "-sdk":
                if(args.length != 8){
                    System.err.println("[jInst] Error: Bad arguments length for SDK project. Expected 6, got "+args.length+".");
                    return;
                }else{
                    String tName = args[1];
                    String workspace = args[2];
                    String project = args[3];
                    String tests = args[4];
                    boolean testOriented = args[5].equals("-TestOriented");
                    boolean monkeyTest = args[6].length()>6 ? args[6].equals("-Monkey") : false;
                    String appID = args[7];

                    try {
                        APICallUtil apu = ((APICallUtil) (new APICallUtil().fromJSONObject(new APICallUtil().fromJSONFile(project+"/"+ tName +"/"+appID+".json"))));
                        InstrumentHelper helper = new InstrumentHelper(apu,tName, workspace, project, tests,testOriented);
                        helper.monkeyTest = monkeyTest;
                        helper.projectID = getProjectID(project);
                        helper.generateTransformedProject();
                        XMLParser.buildAppPermissionsJSON(helper.getManifest(),helper.getTransFolder());
                        helper.generateTransformedTests();
                        FileUtils.writeFile(new File( project+"/"+ tName +"/"+appID+".json"), helper.getAcu().toJSONObject(apu.proj.projectID).toJSONString());

                    } catch (Exception ex) {
                        Logger.getLogger(JInst.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                break;
            case "-gradle":
                if(args.length != 9){
                    System.err.println("[jInst] Error: Bad arguments length for Gradle project. Expected 8, got "+args.length+".");
                    return;
                }else{
                    String appID = args[8];
                    String tName = args[1];
                    String workspace = args[2];
                    String project = args[3];
                    String manifestSource = args[4];
                    String manifestTests = args[5];
                    boolean testOriented = args[6].equals("-TestOriented");
                    boolean monkeyTest = args.length>7 ? args[7].equals("-Monkey") : false;

                    try {
                        APICallUtil apu = ((APICallUtil) (new APICallUtil().fromJSONObject(new APICallUtil().fromJSONFile(project+"/"+ tName +"/"+appID+".json"))));
                        apu.proj.projectBuildTool="gradle";
                        apu.proj.projectDescription="";
                        InstrumentGradleHelper helper = new InstrumentGradleHelper(apu,tName, workspace, project, "", manifestSource, manifestTests, testOriented);
                        helper.monkeyTest = monkeyTest;
                        helper.projectID = apu.proj.projectID;
                        helper.generateTransformedProject();
                        helper.addPermission();
                        XMLParser.buildAppPermissionsJSON(manifestSource,helper.getTransFolder());
                        FileUtils.writeFile(new File( project+"/"+ tName +"/"+appID+".json"), helper.getAcu().toJSONObject(apu.proj.projectID).toJSONString());

                        // loadAndSendApplicationJSON(project+"/"+"application.json");
                    } catch (Exception ex) {
                        Logger.getLogger(JInst.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            default : break;
                //
        }

   }
}
