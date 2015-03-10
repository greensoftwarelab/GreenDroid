/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package instrumentation.transform;

import japa.parser.ASTHelper;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.VoidType;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import instrumentation.visitors.utils.ClassDefs;
import instrumentation.util.FileUtils;
import instrumentation.util.PackageM;
import instrumentation.util.XMLParser;
import instrumentation.visitors.MethodChangerVisitor;
import instrumentation.visitors.MethodVisitor;
import instrumentation.visitors.TestChangerVisitor;

/**
 *
 * @author User
 */
public class InstrumentHelper {
    
    public static String runnerClass = "com.zutubi.android.junitreport.JUnitReportTestRunner";
    private static ArrayList<String> instrumented = new ArrayList<String>();
    private static ArrayList<String> testCase = new ArrayList<String>();
    private static String notTestable = "TestCase";
    
    private String tName;
    private String workspace;
    private String project;
    private String tests;
    private String transFolder;
    private String transTests;
    private String aux;
    private String manifest;
    private String manifestTest;
    private String projectDesc;
    private String devPackage;

    public InstrumentHelper() {
    }

    
    public InstrumentHelper(String tName, String work, String proj, String tests) {
        this.tName = tName;
        this.workspace = work;
        this.project = proj+"/";
        this.tests = tests;
        this.transFolder = project+tName+"/";
        this.transTests = transFolder+"tests"+"/";
        this.manifest = project+"AndroidManifest.xml";
        this.manifestTest = transTests+"AndroidManifest.xml";
        this.projectDesc = project+".project";
        this.aux = transFolder+"_aux_/";
        this.devPackage = "";
        
        instrumented.add("ActivityUnitTestCase");
        instrumented.add("ActivityIntrumentationTestCase2");
        instrumented.add("ActivityTestCase");
        instrumented.add("ProviderTestCase");
        instrumented.add("SingleLaunchActivityTestCase");
        instrumented.add("SyncBaseInstrumentation");
        instrumented.add("ActivityInstrumentationTestCase");
        instrumented.add("ActivityInstrumentationTestCase2");
        //instrumented.add("WizardPageActivityTestBase");

        testCase.add("AndroidTestCase");
        testCase.add("ApplicationTestCase");
        testCase.add("LoaderTestCase");
        testCase.add("ProviderTestCase2");
        testCase.add("ServiceTestCase");
        //testCase.add("TestCase");
        
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
        this.transFolder = project+tName+"/";
        this.transTests = transFolder+"tests"+"/";
        this.manifest = project+"AndroidManifest.xml";
        this.manifestTest = transTests+"AndroidManifest.xml";
        this.devPackage = "";
    }

    public String getTests() {
        return tests;
    }

    public void setTests(String tests) {
        this.tests = tests;
    }

    public void generateTransformedTests() throws Exception{
        File fProject = new File(tests);
        File fTransf = new File(transTests); fTransf.mkdir();
        File[] listOfFiles = fProject.listFiles();
        
        //Copy all the files to the new project folder
        for(File f : listOfFiles){
            if(f.isDirectory()){
                if(!f.getName().equals("src") && !f.getName().equals(tName)){
                    FileUtils.copyFolder(f, new File(transTests+f.getName()));
                }else if(f.getName().equals("src")){
                    //PASS THE FILES THROUGH THE PARSING AND INSTRUMENTATION TOOL
                    this.instrumentSource(f, new File(transTests+"src"));
                }
            }else if(f.isFile()){
                File aux = new File(transTests+f.getName());
                aux.createNewFile();
                FileUtils.copyFile(f, aux);
            }
        }
        this.editProjectDesc();
        
        File libsT = new File(transTests+"libs");
        if(!libsT.exists()){
            libsT.mkdir();
        }
        //File runner = new File(libsT.getAbsolutePath()+"/polidea_test_runner_1.1.jar");
        File runner = new File(libsT.getAbsolutePath()+"/android-junit-report-1.5.8.jar");
        
        runner.createNewFile();
        //FileUtils.copyFile(new File("libsAdded/polidea_test_runner_1.1.jar"), runner);
        FileUtils.copyFile(new File("libsAdded/android-junit-report-1.5.8.jar"), runner);
        
        this.changeRunner();
    }
    
    public void generateTransformedProject() throws Exception{
        File fProject = new File(project);
        File fTransf = new File(transFolder); fTransf.mkdir();
        File[] listOfFiles = fProject.listFiles();
        
        this.findLauncher();
        
        //Copy all the files to the new project folder
        for(File f : listOfFiles){
            if(f.isDirectory()){
                if(!f.getName().equals("src") && !f.getName().equals(tName) && !tests.contains(f.getAbsolutePath())){
                    FileUtils.copyFolder(f, new File(transFolder+f.getName()));
                }else if(f.getName().equals("src")){
                    //PASS THE FILES THROUGH THE PARSING AND INSTRUMENTATION TOOL
                    this.instrumentSource(f, new File(transFolder+"src"));
                }
            }else if(f.isFile()){
                File aux = new File(transFolder+f.getName());
                aux.createNewFile();
                FileUtils.copyFile(f, aux);
            }
        }
        this.addPermission();
        File libs = new File(transFolder+"libs");
        if (!libs.exists()) {
            libs.mkdir();
        }
        File greenDroid = new File(libs.getAbsolutePath()+"/greendroid.jar");
        greenDroid.createNewFile();
        FileUtils.copyFile(new File("libsAdded/greendroid.jar"), greenDroid);
        
    }
    
    private void findLauncher(){
        XMLParser.parseManifest(manifest);
        //this.devPackage = XMLParser.getDevPackage();
        this.devPackage = XMLParser.getBuildPackage();
    }
    
    private void changeRunner(){
        XMLParser.editRunner(manifestTest);
        String proj = XMLParser.getTestProjName()+".launch";
        XMLParser.editRunConfiguration(workspace+".metadata/.plugins/org.eclipse.debug.core/.launches/"+proj, runnerClass);
    }
    
    private void addPermission(){
        XMLParser.editManifest(transFolder+"AndroidManifest.xml");
    }
    
    private void editProjectDesc(){
        XMLParser.editProjectDesc(this.transFolder+".project");
        XMLParser.editProjectDesc(this.transTests+".project");
        XMLParser.editClasspath(this.transTests+".classpath");
    }
    
    private void instrumentSource(File src, File dest) throws Exception{
        
    	if(src.isDirectory()){
    		//if directory not exists, create it
    		if(!dest.exists()){
    		   dest.mkdir();
    		}
 
    		//list all the directory contents
    		String files[] = src.list();
 
    		for (String file : files) {
    		   //construct the src and dest file structure
    		   File srcFile = new File(src, file);
    		   File destFile = new File(dest, file);
    		   //recursive transform
    		   instrumentSource(srcFile,destFile);
    		}
 
    	}else{
    		//if file, then transform it
                if(src.getAbsolutePath().endsWith(".java")){
                    String res = "";
                    if(src.getAbsolutePath().contains(this.tests) || src.getAbsolutePath().replace('\\', '/').contains(this.tests)){
                        res = transformTest(src.getAbsolutePath());
                    }else{
                        res = transform(src.getAbsolutePath());
                    }
                    if(!res.equals("")){
                        dest.createNewFile();
                        FileUtils.writeFile(dest, res);
                    }else{
                        dest.delete();
                    }
                    
                }else{
                    FileUtils.copyFile(src, dest);
                }
    	}
        String allMethods = "";
        for(PackageM p : MethodChangerVisitor.getPackages()){
            allMethods += p.toString();
        }
        File auxF = new File(aux); auxF.mkdir();
        FileUtils.writeFile(new File(aux+"AllMethods"), allMethods);
    }
    
    
    
    private String transform(String file) throws Exception {
        // creates an input stream for the file to be parsed
        FileInputStream in = new FileInputStream(file);
        //System.out.println("Parsing & Instrumenting "+file);
        CompilationUnit cu;
        try {
            // parse the file
            cu = JavaParser.parse(in);
        } finally {
            in.close();
        }
        //ADD THE IMPORT OF THE STATIC ESTIMATOR
        //ImportDeclaration imp1 = new ImportDeclaration(ASTHelper.createNameExpr("measure.util.SystemInfo"), false, false);
        ImportDeclaration imp2 = new ImportDeclaration(ASTHelper.createNameExpr("com.greendroid.StaticEstimator"), false, false);
        if(cu.getImports() != null){
            //cu.getImports().add(imp1);
            cu.getImports().add(imp2);
        }else{
            cu.setImports(new LinkedList<ImportDeclaration>());
            //cu.getImports().add(imp1);
            cu.getImports().add(imp2);
        }
        String pack = cu.getPackage().getName().toString();
        String cl = cu.getTypes().get(0).getName();
        ClassDefs cDef = new ClassDefs();
        cDef.setName(cl); cDef.setPack(pack);
        // visit and print the methods names
        //new MethodVisitor().visit(cu, null);
        

        // visit and change the methods names and parameters
        String classDec = cu.getTypes().get(0).getClass().getName();
        if(!classDec.contains("ClassOrInterfaceDeclaration")){
            return cu.toString();
        }
        ClassOrInterfaceDeclaration x = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
        /*for(Comment c : cu.getComments()){
            if(c.getContent().contains("LAUNCHER")){
                flags.setLauncher(true);
            }
        }
        */

        String l = pack+"."+cl;
        if(this.devPackage.equals(l)){
            cDef.setLauncher(true);
        }
        
        if(!x.isInterface()){
            if(x.getExtends() != null){
                for(ClassOrInterfaceType ci: x.getExtends()){
                    String name = ci.getName();
                    if(name.contains("Activity")){
                        cDef.setActivity(true);
                    }
                }
            }
            /*
            if(cDef.isActivity()){
                new ActivityChangerVisitor().visit(cu, cDef);
                if(cDef.hasStop() == false){
                    //create the onStop method
                    MethodDeclaration newStop = new MethodDeclaration();
                    newStop.setName("onStop");
                    newStop.setModifiers(ModifierSet.PUBLIC);
                    newStop.setType(new VoidType());
                    LinkedList<AnnotationExpr> anot = new LinkedList<AnnotationExpr>();
                    anot.add(new MarkerAnnotationExpr(new NameExpr("Override")));
                    newStop.setAnnotations(anot);
                    newStop.setBody(new BlockStmt());
                    //add the onStop method
                    MethodCallExpr superStop = new MethodCallExpr();
                    superStop.setName("super.onStop");
                    MethodCallExpr mcStop = new MethodCallExpr();
                    mcStop.setName("StaticEstimator.saveMatrix");
                    ArrayList<Statement> body = new ArrayList<Statement>();
                    body.add(new ExpressionStmt(superStop));
                    body.add(new ExpressionStmt(mcStop));
                    newStop.getBody().setStmts(body);
                    x.getMembers().add(0,newStop);
                }
            }
            */
            new MethodChangerVisitor().visit(cu, cDef);
        }
        if(cu != null){
            // prints the changed compilation unit
            //System.out.println(cu.toString());
        }
        return cu.toString();
    }
    
    private String transformTest(String file) throws Exception {
        // creates an input stream for the file to be parsed
        FileInputStream in = new FileInputStream(file);
        ClassDefs cDef = new ClassDefs();
        //System.out.println("Parsing & Instrumenting "+file);
        CompilationUnit cu;
        boolean isJunitTest = false;
        boolean isTestable = false;
        try {
            // parse the file
            cu = JavaParser.parse(in);
        } finally {
            in.close();
        }
        //CHECK THE IMPORTS
        
        String pack = cu.getPackage().getName().toString();
        String cl = cu.getTypes().get(0).getName();
        cDef.setName(cl); cDef.setPack(pack); cDef.setAppName(this.devPackage);
        
        //CHECK IF THE TEST CAN BE CONSIDERED FOR ENERGY MONITORING
        ClassOrInterfaceDeclaration x = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
        if(x.getExtends() != null){
            for(ClassOrInterfaceType ci: x.getExtends()){
                String name = ci.getName();
                if(testCase.contains(name)){
                    cDef.setInstrumented(false);
                    isTestable = true;
                }else if(instrumented.contains(name)){
                    cDef.setInstrumented(true);
                    isTestable = true;
                }else if(notTestable.equals(name)){
                    isJunitTest = true;
                }
                /*for(String ext : androidTests){
                    if(name.contains(ext)){
                        isTestable = true;
                    }
                }*/
            }
        }
	if(isTestable){
            new TestChangerVisitor().visit(cu, cDef);
            if(!cDef.isSuite()){
                if(!cDef.hasSetUp()){
                    //create the setUp method
                    LinkedList<NameExpr>_throws = new LinkedList<NameExpr>();
                    _throws.add(new NameExpr("Exception"));
                    MethodDeclaration newSetUp = new MethodDeclaration();
                    newSetUp.setName("setUp");
                    newSetUp.setModifiers(ModifierSet.PUBLIC);
                    newSetUp.setThrows(_throws);
                    newSetUp.setType(new VoidType());
                    LinkedList<AnnotationExpr> anot = new LinkedList<AnnotationExpr>();
                    anot.add(new MarkerAnnotationExpr(new NameExpr("Override")));
                    newSetUp.setAnnotations(anot);
                    newSetUp.setBody(new BlockStmt());

                    //add the setUp method
                    MethodCallExpr superSetUp = new MethodCallExpr();
                    superSetUp.setName("super.setUp");

                    MethodCallExpr mcS = new MethodCallExpr();
                    mcS.setName("StaticEstimator.start");

                    MethodCallExpr mcConfig = new MethodCallExpr();
                    mcConfig.setName("StaticEstimator.config");
                    MethodCallExpr getUid = new MethodCallExpr();
                    getUid.setName("SystemInfo.getInstance().getUidForPid");
                    MethodCallExpr getPid = new MethodCallExpr();
                    getPid.setName("android.os.Process.myPid");
                    MethodCallExpr getContext = new MethodCallExpr();
                    if(!cDef.isInstrumented()){
                        getContext.setName("this.getContext");
                    }else{
                        getContext.setName("this.getInstrumentation().getTargetContext");
                    }
                    ASTHelper.addArgument(getUid, getPid);
                    ASTHelper.addArgument(mcConfig, new StringLiteralExpr(cDef.getAppName()));
                    ASTHelper.addArgument(mcConfig, getUid);
                    ASTHelper.addArgument(mcConfig, getContext);

                    ArrayList<Statement> body = new ArrayList<Statement>();
                    body.add(new ExpressionStmt(mcConfig));
                    body.add(new ExpressionStmt(mcS));
                    body.add(new ExpressionStmt(superSetUp));
                    newSetUp.getBody().setStmts(body);

                    x.getMembers().add(0,newSetUp);

                }
                if(!cDef.hasTearDown()){
                    //create the tearDown method
                    LinkedList<NameExpr>_throws = new LinkedList<NameExpr>();
                    _throws.add(new NameExpr("Exception"));
                    MethodDeclaration newTearDown = new MethodDeclaration();
                    newTearDown.setName("tearDown");
                    newTearDown.setThrows(_throws);
                    newTearDown.setModifiers(ModifierSet.PUBLIC);
                    newTearDown.setType(new VoidType());
                    LinkedList<AnnotationExpr> anot = new LinkedList<AnnotationExpr>();
                    anot.add(new MarkerAnnotationExpr(new NameExpr("Override")));
                    newTearDown.setAnnotations(anot);
                    newTearDown.setBody(new BlockStmt());

                    MethodCallExpr superTearDown = new MethodCallExpr();
                    superTearDown.setName("super.tearDown");
                    MethodCallExpr mcT = new MethodCallExpr();
                    mcT.setName("StaticEstimator.stop");

                    ArrayList<Statement> body = new ArrayList<Statement>();
                    body.add(new ExpressionStmt(mcT));
                    body.add(new ExpressionStmt(superTearDown));
                    newTearDown.getBody().setStmts(body);

                    x.getMembers().add(0,newTearDown);
                }
            }
            ImportDeclaration imp1 = new ImportDeclaration(ASTHelper.createNameExpr("com.greendroid.util.SystemInfo"), false, false);
            ImportDeclaration imp2 = new ImportDeclaration(ASTHelper.createNameExpr("com.greendroid.StaticEstimator"), false, false);
            if(cu.getImports() != null){
                cu.getImports().add(imp1);
                cu.getImports().add(imp2);
            }else{
                cu.setImports(new LinkedList<ImportDeclaration>());
                cu.getImports().add(imp1);
                cu.getImports().add(imp2);
            }
        }else if(x.getExtends() != null){
            if(isJunitTest){
                new MethodVisitor().visit(cu, cDef);
                if(cDef.hasTests()) return "";
            }
        }
        return cu.toString();
    }
}
