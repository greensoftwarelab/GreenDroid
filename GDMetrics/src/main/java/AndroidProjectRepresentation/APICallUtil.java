package AndroidProjectRepresentation;

import Metrics.CyclomaticCalculator;
import Metrics.SourceCodeLineCounter;
import Utils.MethodChangerVisitor;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.QualifiedNameExpr;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.*;

public class APICallUtil extends VoidVisitorAdapter implements Serializable, JSONSerializable {

    public static ProjectInfo proj = new ProjectInfo();

    public static Set<String> processedClasses = new HashSet<>();

    public APICallUtil(){}

    public APICallUtil(ProjectInfo pi ){
        this.proj = pi;
    }



    public void addClassToApp(String app, ClassInfo ci ){
        if (app==null ||app=="" ){
            proj.getCurrentApp().allJavaClasses.add(ci);
        }
    }


    public  void gatherMethods(ClassOrInterfaceDeclaration n ,ClassInfo thisClass , CompilationUnit cu ){
        for (Node x : n.getChildrenNodes()) {
            if (x instanceof MethodDeclaration) {
                MethodInfo mi = new MethodInfo();
                if (((MethodDeclaration) x).getParameters() != null) {
                    for (Parameter m : ((MethodDeclaration) x).getParameters()) {
                        boolean isArray = (m.getType() instanceof ReferenceType) ? ((ReferenceType) m.getType()).getArrayCount() > 0 : false;
                        mi.args.add(new Variable(m.getId().getName(), m.getType().toStringWithoutComments(), isArray));
                    }
                }
                mi.ci = thisClass;
                mi.methodName = ((MethodDeclaration) x).getName();
                mi.setModifiers(((MethodDeclaration) x).getModifiers());
                mi.cyclomaticComplexity = CyclomaticCalculator.cyclomaticAndAPI(x, mi);
                //mi.nr_args = ((MethodDeclaration) x).getParameters() == null? 0 : ((MethodDeclaration) x).getParameters().size();
                try {
                    mi.linesOfCode = ((MethodDeclaration) x).getBody() != null ? SourceCodeLineCounter.getNumberOfLines(((MethodDeclaration) x).getBody().toStringWithoutComments())- 2 : 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                thisClass.classMethods.put(mi.getMethodID(), mi);
            } else if (x instanceof ConstructorDeclaration) {
                // iterate over stmt to sse class/instance vars usage
                // check if methods called are internal or external
                MethodInfo mi = new MethodInfo();
                if (((ConstructorDeclaration) x).getParameters() != null) {
                    for (Parameter m : ((ConstructorDeclaration) x).getParameters()) {
                        boolean isArray = (m.getType() instanceof ReferenceType) ? ((ReferenceType) m.getType()).getArrayCount() > 0 : false;
                        mi.args.add(new Variable(m.getId().getName(), m.getType().toStringWithoutComments(), isArray));
                    }
                }
                mi.ci = thisClass;
                mi.setModifiers(((ConstructorDeclaration) x).getModifiers());
                mi.methodName = ((ConstructorDeclaration) x).getName();
                mi.cyclomaticComplexity = CyclomaticCalculator.cyclomaticAndAPI(x, mi);

                try {
                    mi.linesOfCode = ((ConstructorDeclaration) x).getBlock() != null ? SourceCodeLineCounter.getNumberOfLines(((ConstructorDeclaration) x).getBlock().toStringWithoutComments()) - 2 : 0;
                } catch (IOException e) {
                   mi.linesOfCode=0;
                }
                //mi.nr_args = ((ConstructorDeclaration) x).getParameters() == null? 0 : ((ConstructorDeclaration) x).getParameters().size();
                thisClass.classMethods.put(mi.getMethodID(), mi);

            }
            else if (x instanceof ClassOrInterfaceDeclaration) {
                // has inner class/interface
                ClassInfo ci = null;
                try {
                    ci = processClass(((ClassOrInterfaceDeclaration) x),cu);
                    ci.outClass = thisClass.className;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // MethodClassifier(ci);
                // this.proj.getCurrentApp().allJavaClasses.add(ci);

            }
        }

    }

    public  void gatherMethods(ClassOrInterfaceDeclaration n ,ClassInfo thisClass , CompilationUnit cu, List<BodyDeclaration> list ){
        for (Node x : list) {
            if (x instanceof MethodDeclaration) {
                MethodInfo mi = new MethodInfo();
                if (((MethodDeclaration) x).getParameters() != null) {
                    for (Parameter m : ((MethodDeclaration) x).getParameters()) {
                        boolean isArray = (m.getType() instanceof ReferenceType) ? ((ReferenceType) m.getType()).getArrayCount() > 0 : false;
                        mi.args.add(new Variable(m.getId().getName(), m.getType().toStringWithoutComments(), isArray));
                    }
                }
                mi.ci = thisClass;
                mi.methodName = ((MethodDeclaration) x).getName();
                mi.setModifiers(((MethodDeclaration) x).getModifiers());
                mi.cyclomaticComplexity = CyclomaticCalculator.cyclomaticAndAPI(x, mi);
                //mi.nr_args = ((MethodDeclaration) x).getParameters() == null? 0 : ((MethodDeclaration) x).getParameters().size();
                try {
                    mi.linesOfCode = ((MethodDeclaration) x).getBody() != null ? SourceCodeLineCounter.getNumberOfLines(((MethodDeclaration) x).getBody().toStringWithoutComments())- 2 : 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                thisClass.classMethods.put(mi.getMethodID(), mi);
            } else if (x instanceof ConstructorDeclaration) {
                // iterate over stmt to sse class/instance vars usage
                // check if methods called are internal or external
                MethodInfo mi = new MethodInfo();
                if (((ConstructorDeclaration) x).getParameters() != null) {
                    for (Parameter m : ((ConstructorDeclaration) x).getParameters()) {
                        boolean isArray = (m.getType() instanceof ReferenceType) ? ((ReferenceType) m.getType()).getArrayCount() > 0 : false;
                        mi.args.add(new Variable(m.getId().getName(), m.getType().toStringWithoutComments(), isArray));
                    }
                }
                mi.ci = thisClass;
                mi.setModifiers(((ConstructorDeclaration) x).getModifiers());
                mi.methodName = ((ConstructorDeclaration) x).getName();
                mi.cyclomaticComplexity = CyclomaticCalculator.cyclomaticAndAPI(x, mi);

                try {
                    mi.linesOfCode = ((ConstructorDeclaration) x).getBlock() != null ? SourceCodeLineCounter.getNumberOfLines(((ConstructorDeclaration) x).getBlock().toStringWithoutComments()) - 2 : 0;
                } catch (IOException e) {
                    mi.linesOfCode=0;
                }
                //mi.nr_args = ((ConstructorDeclaration) x).getParameters() == null? 0 : ((ConstructorDeclaration) x).getParameters().size();
                thisClass.classMethods.put(mi.getMethodID(), mi);

            }
            else if (x instanceof ClassOrInterfaceDeclaration) {
                // has inner class/interface
                ClassInfo ci = null;
                try {
                    ci = processClass(((ClassOrInterfaceDeclaration) x),cu);
                    ci.outClass = thisClass.className;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // MethodClassifier(ci);
                // this.proj.getCurrentApp().allJavaClasses.add(ci);

            }
        }

    }



    public void processJavaFile(String javaFilePath) throws IOException, ParseException {

        File file = new File(javaFilePath);
        FileInputStream in = new FileInputStream(file);
        CompilationUnit cu;
        try {
            // parse the file
            cu = JavaParser.parse(in, null, false);
        } finally {
            in.close();

        }
        for (Node n : cu.getChildrenNodes()) {
            if (n instanceof ClassOrInterfaceDeclaration) {
                ClassInfo thisClass = processClass(((ClassOrInterfaceDeclaration) n),cu);
                //MethodClassifier(thisClass);
            }
        }

        //cleanAPI(thisClass);
        //

    }

    public ClassInfo processClass(ClassOrInterfaceDeclaration n , CompilationUnit cu ) throws IOException {

            ClassInfo thisClass = new ClassInfo(proj.getCurrentApp().appID);
            thisClass.extendedClass = ((ClassOrInterfaceDeclaration) n).getExtends() == null ? null : ((ClassOrInterfaceDeclaration) n).getExtends().get(0).getName();
            //allJavaClasses.add(thisClass);
            //add package declaration
            proj.allPackagesOfProject.add(cu.getPackage().getName().toString());
            thisClass.classPackage = cu.getPackage().getName().toString();
            thisClass.className = ((ClassOrInterfaceDeclaration) n).getName();
            thisClass.isInterface = ((ClassOrInterfaceDeclaration) n).isInterface();
            thisClass.setModifiers(((ClassOrInterfaceDeclaration) n).getModifiers());


           if (processedClasses.contains(thisClass.getClassID())){
             try {
                 ClassInfo cc =  this.proj.getCurrentApp().getClass(thisClass);
                 if (cc!=null){
                     gatherMethods(n,cc,cu);
                     return cc;
                 }

             }catch (NullPointerException e){

             }
           }

            //set Imports
            if (cu.getImports() != null) {
                for (ImportDeclaration id : cu.getImports()) {
                    NameExpression ne = new NameExpression(((QualifiedNameExpr) id.getName()).getQualifier().toString(), ((QualifiedNameExpr) id.getName()).getName());
                    thisClass.classImports.add(ne);
                }
            }

            if (((ClassOrInterfaceDeclaration) n).getImplements() != null) {
                String ifaceDef = "";
                for (ClassOrInterfaceType cit : ((ClassOrInterfaceDeclaration) n).getImplements()) {
                    if (cit.getScope() != null) {
                        ifaceDef = getCorrespondantImport(cit.getScope().toStringWithoutComments(), thisClass);
                    }
                    thisClass.interfacesImplemented.add(ifaceDef + "." + cit.getName());
                }
            }

            if (n.getChildrenNodes() != null) {
                //get local vars first , do not assume declaration at beggining
                for (Node x : n.getChildrenNodes()) {
                    if (x instanceof FieldDeclaration) {
                        for (VariableDeclarator vd : ((FieldDeclaration) x).getVariables()) {
                            Variable cv = new Variable();
                            cv.type = ((FieldDeclaration) x).getType().toString();
                            cv.varName = vd.getId().getName();
                            cv.setModifiers(((FieldDeclaration) x).getModifiers());
                            thisClass.classVariables.put(cv.varName, cv);
                        }
                    }
                }

                gatherMethods(n,thisClass,cu);
            }
      //  this.proj.getCurrentApp().allJavaClasses.add(thisClass);
        try {
            addClassToApp( null, thisClass);
        }catch (Exception e){
           e.printStackTrace();
        }
        processedClasses.add(thisClass.getClassID());
        MethodClassifier(thisClass);
        this.visit(n, cu);

        return thisClass;
    }



    private static void cleanAPI(ClassInfo ci) {
        for (MethodInfo m : ci.classMethods.values()) {
            Iterator<MethodOfAPI> it = m.externalApi.iterator();
            while (it.hasNext()) {
                MethodOfAPI ss = it.next();
                if (m.ci.classVariables.containsKey(ss.method) || m.isInDeclaredVars(ss.method)) {
                    ss.method = null;
                }

            }
            it = m.androidApi.iterator();
            while (it.hasNext()) {
                MethodOfAPI ss = it.next();
                if (m.ci.classVariables.containsKey(ss.method) || m.isInDeclaredVars(ss.method)) {
                    ss.method = null;
                }

            }
            it = m.javaApi.iterator();
            while (it.hasNext()) {
                MethodOfAPI ss = it.next();
                if (m.ci.classVariables.containsKey(ss.method) || m.isInDeclaredVars(ss.method)) {
                    ss.method = null;
                }
            }
        }
    }


    private static void MethodClassifier(ClassInfo thisClass) {

        if (thisClass.classMethods.size() > 0) {
            for (MethodInfo m : thisClass.classMethods.values()) {
                Iterator<MethodOfAPI> it = m.unknownApi.iterator();
                while (it.hasNext()) {
                    MethodOfAPI ss = it.next();
                    String s = ss.api;
                    if (s == null || s.equals("")) {
                        it.remove();
                        continue;
                    }

                    String[] x = s.split("<|,");
                    boolean added = false;
                    if (x.length > 1) {
                        for (String st : x) {
                            st = st.replaceAll(">", "").replace(" ", "");

                            if (s.equals("super")) {
                                st = thisClass.extendedClass;
                            }

                            if (!isPrimitiveType(st)) {

                                if (isAndroidApi(st, thisClass)) {

                                    m.androidApi.add(new MethodOfAPI(getCorrespondantImport(st, thisClass).equals("") ? st : getCorrespondantImport(st, thisClass) + "." + st, ss.method));
                                    added = true;

                                } else if (isJavaApi(st, thisClass)) {

                                    m.javaApi.add(new MethodOfAPI(getCorrespondantImport(st, thisClass).equals("") ? st : getCorrespondantImport(st, thisClass) + "." + st, ss.method));
                                    added = true;

                                } else {

                                    m.externalApi.add(new MethodOfAPI(getCorrespondantImport(st, thisClass).equals("") ? st : getCorrespondantImport(st, thisClass) + "." + st, ss.method));
                                    added = true;

                                }
                            }
                        }
                        if (added) {
                            added = false;
                            it.remove();
                        }
                    } else {

                        if (s.equals("super")) {
                            s = thisClass.extendedClass;
                        }

                        if (!isPrimitiveType(s)) {
                            if (isAndroidApi(s, thisClass)) {

                                m.androidApi.add(new MethodOfAPI(getCorrespondantImport(s, thisClass).equals("") ? s : getCorrespondantImport(s, thisClass) + "." + s, ss.method));
                                it.remove();
                            } else if (isJavaApi(s, thisClass)) {

                                m.javaApi.add(new MethodOfAPI(getCorrespondantImport(s, thisClass).equals("") ? s : getCorrespondantImport(s, thisClass) + "." + s, ss.method));
                                it.remove();
                            } else {

                                m.externalApi.add(new MethodOfAPI(getCorrespondantImport(s, thisClass).equals("") ? s : getCorrespondantImport(s, thisClass) + "." + s, ss.method));
                                it.remove();
                            }
                        } else it.remove();
                    }
                }

            }
        }
    }


    private static boolean isJavaApi(String s, ClassInfo thisClass) {
        return getCorrespondantImport(s, thisClass).startsWith("java") || s.equals("Integer") || s.equals("Double") || s.equals("Byte") || s.equals("Short") || s.equals("Long") || s.equals("Float") || s.equals("Character") || s.equals("Boolean") || s.equals("String") || s.startsWith("System");
    }

    private static boolean isPrimitiveType(String s) {
        return s.equals("int") || s.equals("double") || s.equals("byte") || s.equals("short") || s.equals("long") || s.equals("float") || s.equals("char") || s.equals("boolean");
    }

    private static boolean isAndroidApi(String s, ClassInfo ci) {
        return getCorrespondantImport(s, ci).startsWith("android") || getCorrespondantImport(s, ci).startsWith("com.google.android") || getCorrespondantImport(s, ci).startsWith("org.apache.http") || getCorrespondantImport(s, ci).startsWith("org.xml") || getCorrespondantImport(s, ci).startsWith("org.w3c.dom") ||
                getCorrespondantImport(s, ci).startsWith("com.android.internal") || getCorrespondantImport(s, ci).startsWith("dalvik");

    }

    private static String getCorrespondantImport(String apiReference, ClassInfo ci) {
        String s = "";
        for (NameExpression importDec : ci.classImports) {
            if (importDec.name.equals(apiReference))
                return importDec.qualifier;
        }
        return s;
    }


    public static Set<String> getClassesUsed(MethodDeclaration md) {
        List<String> list = new ArrayList<>();
        for (Statement st : md.getBody().getStmts()) {
            if (st instanceof ExpressionStmt) {

                if (((ExpressionStmt) st).getExpression() instanceof MethodCallExpr) {

                    if (((MethodCallExpr) ((ExpressionStmt) st).getExpression()).getScope() != null)
                        list.add(((MethodCallExpr) ((ExpressionStmt) st).getExpression()).getScope().toString());
                }
            }
        }

        return new HashSet<>();

    }

    public MethodInfo getMethodOfClass(String method, String fullClassName) {
        MethodInfo m = new MethodInfo();
        for (ClassInfo ci : proj.getCurrentApp().allJavaClasses) {
            if (ci.getFullClassName().equals(fullClassName)) {
                if (ci.getMethod(method) != null)
                    return ci.getMethod(method);
            }
        }
        return m;
    }


    public static void serializeAPICallUtil(APICallUtil acu, String path) {

        FileOutputStream fout = null;
        ObjectOutputStream oos = null;

        try {

            fout = new FileOutputStream(path);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(acu);

        } catch (Exception ex) {

            ex.printStackTrace();

        } finally {

            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static APICallUtil deserializeAPiCallUtil(String filename) {

        APICallUtil acu = null;

        FileInputStream fin = null;
        ObjectInputStream ois = null;

        try {

            fin = new FileInputStream(filename);
            ois = new ObjectInputStream(fin);
            acu = (APICallUtil) ois.readObject();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return acu;

    }


    @Override
    public JSONObject toJSONObject(String requiredId) {
        return this.proj.toJSONObject(requiredId);
    }

    @Override
    public JSONSerializable fromJSONObject(JSONObject jo) {
        return new APICallUtil(((ProjectInfo) this.proj.fromJSONObject(jo)));
    }

    @Override
    public JSONObject fromJSONFile(String pathToJSONFile) {

        return this.proj.fromJSONFile(pathToJSONFile);

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.proj.toString());
        return sb.toString();
    }



    @Override
    public void visit(MethodDeclaration n, Object arg) {
        CompilationUnit cu = ((CompilationUnit) arg);
        Node x = n.getParentNode();
        while (x!=null && (!(x instanceof ClassOrInterfaceDeclaration))){
            x = x.getParentNode();
        }
        if (x !=null ){
            try {
                ClassInfo ci = processClass(((ClassOrInterfaceDeclaration) x),cu);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void visit(ObjectCreationExpr n, Object arg) {
        CompilationUnit cu = ((CompilationUnit) arg);
        Node x = n.getParentNode();
        while (x!=null && (!(x instanceof ClassOrInterfaceDeclaration))){
            x = x.getParentNode();
        }
        if (x !=null ){
            try {
                ClassInfo ci = processClass(((ClassOrInterfaceDeclaration) x),cu);
                if (n.getAnonymousClassBody()!=null){
                    gatherMethods(((ClassOrInterfaceDeclaration) x), ci, cu, n.getAnonymousClassBody() );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
