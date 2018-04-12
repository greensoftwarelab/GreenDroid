/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst;

import Metrics.APICallUtil;
import Metrics.ClassInfo;
import Metrics.GDConventions;
import jInst.transform.InstrumentGradleHelper;
import jInst.transform.InstrumentHelper;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marco
 */
public class JInst {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String classInfos = GDConventions.fileStreamName;
        String projType = args[0];
        switch (projType){
            case "-sdk":
                if(args.length != 7){
                    System.err.println("[jInst] Error: Bad arguments length for SDK project. Expected 6, got "+args.length+".");
                    return;
                }else{
                    String tName = args[1];
                    String workspace = args[2];
                    String project = args[3];
                    String tests = args[4];
                    boolean testOriented = args[5].equals("-TestOriented");
                    boolean monkeyTest = args[6].length()>6 ? args[6].equals("-Monkey") : false;

                    try {
                        InstrumentHelper helper = new InstrumentHelper(tName, workspace, project, tests,testOriented);
                        helper.monkeyTest = monkeyTest;
                        helper.generateTransformedProject();
                        helper.generateTransformedTests();
                        classInfos = helper.getTransFolder() + classInfos;
                        APICallUtil.serializeAPICallUtil(helper.getAcu(),classInfos );

                    } catch (Exception ex) {
                        Logger.getLogger(JInst.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                break;
            case "-gradle":
                if(args.length != 8){
                    System.err.println("[jInst] Error: Bad arguments length for Gradle project. Expected 7, got "+args.length+".");
                    return;
                }else{
                    String tName = args[1];
                    String workspace = args[2];
                    String project = args[3];
                    String manifestSource = args[4];
                    String manifestTests = args[5];
                    boolean testOriented = args[6].equals("-TestOriented");
                    boolean monkeyTest = args.length>7 ? args[7].equals("-Monkey") : false;

                    try {
                        InstrumentGradleHelper helper = new InstrumentGradleHelper(tName, workspace, project, "", manifestSource, manifestTests, testOriented);
                        helper.monkeyTest = monkeyTest;
                        helper.generateTransformedProject();
                        classInfos = helper.getTransFolder() + classInfos;
                        APICallUtil.serializeAPICallUtil(helper.getAcu(),classInfos );
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
