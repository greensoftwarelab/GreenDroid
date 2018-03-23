/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst.visitors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;

import jInst.profiler.MethodOrientedProfiler;
import jInst.profiler.Profiler;
import jInst.profiler.TestOrientedProfiler;
import jInst.transform.InstrumentHelper;
import jInst.visitors.utils.ReturnFlag;
import com.github.javaparser.ASTHelper;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import jInst.visitors.utils.ClassDefs;
import jInst.util.ClassM;
import jInst.util.PackageM;
import java.util.LinkedList;

/**
 *
 * @author User
 */
public class MethodChangerVisitor extends VoidVisitorAdapter {
    private static LinkedList<PackageM> packages = new LinkedList<PackageM>();

    private static final int NUMBER_OF_OPERATIONS = 7;

    public static void restartPackages(){
        packages.clear();
    }

    public static LinkedList<PackageM> getPackages(){
        return packages;
    }

    public static CompilationUnit cu;
    public static boolean tracedMethod;

    public void setTracedMethod(boolean trace){
        tracedMethod= trace;
    }

    public void setCu(CompilationUnit cus) {
        cu = cus;
    }

    private ClassM getClass(String cla, String pack){
        ClassM newC = null; PackageM newP = null;
        for(PackageM p : this.packages){
            if(p.getName().equals(pack)){
                newP = p;
                for(ClassM c : newP.getClasses()){
                    if(c.getName().equals(cla)){
                        newC = c;
                    }
                }
                if(newC == null){
                    newC = new ClassM(cla);
                    newP.getClasses().add(newC);
                }
            }
        }
        if(newP == null){
            newP = new PackageM();
            newP.setName(pack);
            newC = new ClassM(cla);
            newP.getClasses().add(newC);
            packages.add(newP);
        }
        return newC;
    }


    public static MethodDeclaration getMethod( String methodName){

        List<TypeDeclaration> tp = cu.getTypes();
        for (TypeDeclaration type : tp) {
            //System.out.println("tipozinho: " + type); // isto imprime a classe toda.
            List<BodyDeclaration> members = type.getMembers();
            for (BodyDeclaration member : members) {
               // imprime vars instancia, comentarios, metodos com a respetiva anotacao(caso tenha)
                if (member instanceof MethodDeclaration && ((MethodDeclaration) member).getName().equals(methodName)) {
                    return (MethodDeclaration) member;
                }
            }
        }
        return null;
    }


    public static int countOperations(Node s ) {
        int counter = 0;

        if (s instanceof ExpressionStmt) {
            Expression s1 = ((ExpressionStmt) s).getExpression();
            counter += countOperations(s1);

        } else if (s instanceof WhileStmt) {
            return counter + NUMBER_OF_OPERATIONS;
        } else if (s instanceof ForStmt) {
            return counter + NUMBER_OF_OPERATIONS;
        } else if (s instanceof ForStmt) {
            return counter + NUMBER_OF_OPERATIONS;
        } else if (s instanceof DoStmt) {
            return counter + NUMBER_OF_OPERATIONS;
        } else if (s instanceof ForeachStmt) {
            return counter + NUMBER_OF_OPERATIONS;
        } else if (s instanceof SynchronizedStmt) {
            return counter + NUMBER_OF_OPERATIONS;
        } else if (s instanceof SwitchStmt) {
            // if(((SwitchStmt)s).getEntries().size()>2)
            return counter + NUMBER_OF_OPERATIONS;

        } else if (s instanceof IfStmt) {
            counter += countOperations(((IfStmt) s).getCondition()) + countOperations(((IfStmt) s).getElseStmt()) + countOperations(((IfStmt) s).getThenStmt());
        } else if (s instanceof BlockStmt) {
            if (((BlockStmt) s).getStmts() != null) {
                for (Statement e : ((BlockStmt) s).getStmts()
                        ) {
                    counter +=  countOperations(e);
                }
            }

        } else if (s instanceof ReturnStmt) {
            counter += 1 + countOperations(((ReturnStmt) s).getExpr());
        } else if (s instanceof UnaryExpr) {
            counter = counter + countOperations(((UnaryExpr) s).getExpr());
        } else if (s instanceof BinaryExpr) {
            counter = counter + 1 + countOperations(((BinaryExpr) s).getLeft()) + countOperations(((BinaryExpr) s).getRight());

        } else if (s instanceof ConditionalExpr) {
            counter = counter + 1 + countOperations(((ConditionalExpr) s).getCondition()) + countOperations(((ConditionalExpr) s).getThenExpr()) + countOperations(((ConditionalExpr) s).getElseExpr());

        } else if (s instanceof CastExpr) {
            counter += 1 + countOperations(((CastExpr) s).getExpr());
        } else if (s instanceof ArrayAccessExpr) {
            counter += countOperations(((ArrayAccessExpr) s).getIndex());
        } else if (s instanceof AssignExpr) {
            counter += 1 + countOperations(((AssignExpr) s).getTarget()) + countOperations(((AssignExpr) s).getValue());
        } else if (s instanceof StringLiteralExpr) {
            return counter;
        } else if (s instanceof EnclosedExpr) {
            counter += countOperations(((EnclosedExpr) s).getInner());
        } else if (s instanceof MethodCallExpr) {
            Expression ss = ((MethodCallExpr) s).getScope();
            if (ss instanceof SuperExpr) {
                counter += NUMBER_OF_OPERATIONS;
                return counter;
            } else {
                if (((MethodCallExpr) s).getArgs() != null) {
                    for (Expression sss : ((MethodCallExpr) s).getArgs()) {
                        if (!notCount(sss))
                            counter += countOperations(sss);
                    }
                }

                counter++;
            }
            //counter += countOperations(getMethod(((MethodCallExpr) s).getName()));
            if(counter>NUMBER_OF_OPERATIONS)
                return counter;
        } else if (s instanceof VariableDeclarationExpr) {

            for (VariableDeclarator v : ((VariableDeclarationExpr) s).getVars())
                counter += countOperations(v);
        } else if (s instanceof VariableDeclarator) {
            counter += 1 + countOperations(((VariableDeclarator) s).getInit());
        } else if (s instanceof ObjectCreationExpr) {
            counter++;
            if (((ObjectCreationExpr) s).getArgs() != null) {
                for (Expression a : ((ObjectCreationExpr) s).getArgs()) {
                    counter += countOperations(a);
                }
            }

        } else {
            counter++;
        }


            return counter;
            }





    public  static boolean notCount(Expression s){

        if(s instanceof NameExpr || s instanceof NullLiteralExpr || s instanceof StringLiteralExpr || s instanceof ThisExpr)
            return true;
        else {
            if(s instanceof EnclosedExpr)
                return notCount(((EnclosedExpr)s).getInner());
        }
        return false;
    }


    // TODO save already counted methods and check if it was already called\

    public static int countOperations(MethodDeclaration n){
        int counter = 0;
        if(n==null) return 0;
        if(n.getBody().getStmts() != null) {
            List<Statement> x = n.getBody().getStmts();
            for (Node s :
                    x) {
                if (counter >= NUMBER_OF_OPERATIONS) return counter;
                if (s instanceof ExpressionStmt) {
                    Expression s1 = ((ExpressionStmt) s).getExpression();
                    counter += countOperations(s1);

                } else if (s instanceof WhileStmt) {
                    return counter + NUMBER_OF_OPERATIONS;
                } else if (s instanceof ForStmt) {
                    return counter + NUMBER_OF_OPERATIONS;
                } else if (s instanceof ForStmt) {
                    return counter + NUMBER_OF_OPERATIONS;
                } else if (s instanceof DoStmt) {
                    return counter + NUMBER_OF_OPERATIONS;
                } else if (s instanceof ForeachStmt) {
                    return counter + NUMBER_OF_OPERATIONS;
                } else if (s instanceof SynchronizedStmt) {
                    return counter + NUMBER_OF_OPERATIONS;
                } else if (s instanceof SwitchStmt) {
                    // if(((SwitchStmt)s).getEntries().size()>2)
                    return counter + NUMBER_OF_OPERATIONS;

                } else if (s instanceof IfStmt) {
                    counter += 1+ countOperations(((IfStmt) s).getCondition()) + countOperations(((IfStmt) s).getElseStmt()) + countOperations(((IfStmt) s).getThenStmt());
                } else if (s instanceof BlockStmt) {
                    if (((BlockStmt) s).getStmts() != null) {
                        for (Statement e : ((BlockStmt) s).getStmts()
                                ) {
                            counter += countOperations(e);
                        }
                    }

                } else if (s instanceof ReturnStmt) {
                    counter += 1 + countOperations(((ReturnStmt) s).getExpr());
                } else if (s instanceof UnaryExpr) {
                    counter = counter + countOperations(((UnaryExpr) s).getExpr());
                } else if (s instanceof BinaryExpr) {
                    counter = counter + 1 + countOperations(((BinaryExpr) s).getLeft()) + countOperations(((BinaryExpr) s).getRight());

                } else if (s instanceof ConditionalExpr) {
                    counter = counter + 1 + countOperations(((ConditionalExpr) s).getCondition()) + countOperations(((ConditionalExpr) s).getThenExpr()) + countOperations(((ConditionalExpr) s).getElseExpr());

                } else if (s instanceof CastExpr) {
                    counter += 1 + countOperations(((CastExpr) s).getExpr());
                } else if (s instanceof ArrayAccessExpr) {
                    counter += countOperations(((ArrayAccessExpr) s).getIndex());
                } else if (s instanceof AssignExpr) {
                    counter += 1 + countOperations(((AssignExpr) s).getTarget()) + countOperations(((AssignExpr) s).getValue());
                } else if (s instanceof StringLiteralExpr) {
                    continue;
                } else if (s instanceof EnclosedExpr) {
                    counter += countOperations(((EnclosedExpr) s).getInner());
                } else if (s instanceof MethodCallExpr) {
                    Expression ss = ((MethodCallExpr) s).getScope();
                    if (ss instanceof SuperExpr) {
                        counter += NUMBER_OF_OPERATIONS;
                        return counter;
                    } else {
                        if (((MethodCallExpr) s).getArgs() != null) {
                            for (Expression sss : ((MethodCallExpr) s).getArgs()) {
                                if (!notCount(sss))
                                    counter += countOperations(sss);
                            }
                        }

                        counter++;
                    }
                    counter += countOperations(getMethod(((MethodCallExpr) s).getName()));
                } else if (s instanceof VariableDeclarationExpr) {

                    for (VariableDeclarator v : ((VariableDeclarationExpr) s).getVars())
                        counter += countOperations(v);
                } else if (s instanceof VariableDeclarator) {
                    counter += 1 + countOperations(((VariableDeclarator) s).getInit());
                } else if (s instanceof ObjectCreationExpr) {
                    counter++;
                    if (((ObjectCreationExpr) s).getArgs() != null) {
                        for (Expression a : ((ObjectCreationExpr) s).getArgs()) {
                            counter += countOperations(a);
                        }
                    }

                } else {
                    counter++;
                }

            }
            return counter;
        }
        else return 0;

    }


    @Override
    public void visit(MethodDeclaration n, Object arg) {

        File file = new File("allMethods.txt" );
        if (!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file,true);
        } catch (FileNotFoundException e) {
            System.out.println("[Jinst] Error opening allMethods.txt");
        }
        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        ClassDefs cDef = (ClassDefs)arg;
        String retType = n.getType().getClass().getName();
        int modifiers = n.getModifiers();
        // if is static
       // if((modifiers & Modifier.STATIC ) != 0) return;
        //All the arguments for the function call
        //Expression className = new StringLiteralExpr(cDef.getDescriptor());
        String metodo = ((ClassDefs) arg).getPack() + "." + ((ClassDefs) arg).getName()+"<" +n.getName() + ">";
        Expression method = new StringLiteralExpr( ((ClassDefs) arg).getPack() + "." + ((ClassDefs) arg).getName()+"<" +n.getName() + ">");

//---write the string to the file---
        try {
            osw.write(((ClassDefs) arg).getPack() + "." + ((ClassDefs) arg).getName()+"<" +n.getName() + ">"+ "\n");
            osw.flush();
            osw.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[Jinst] An error occured while appending to allMethods file ");
        }
//        Expression flagB = new IntegerLiteralExpr("0");
//        Expression flagE = new IntegerLiteralExpr("1");

        if(n.getBody() != null){
            if(n.getBody().getStmts() != null){
                List<Statement> x = n.getBody().getStmts();

                // then just trace the method
                if (tracedMethod){
                    Profiler p =InstrumentHelper.getProfiler();

//                    mcB.setName("TrepnLib.traceMethod");
                    MethodCallExpr getContext = new MethodCallExpr();
//                    if (!cDef.isInstrumented()) {
//                        getContext.setName("getApplicationContext");
//                    } else {
//                        getContext.setName("getInstrumentation().getTargetContext");
//                    }
                    getContext.setName(InstrumentHelper.getApplicationFullName() + ".getAppContext");

//                    ASTHelper.addArgument(mcB, getContext);
                    MethodCallExpr mcB = ((TestOrientedProfiler) p).markMethod(getContext,metodo);
//                    ASTHelper.addArgument(mcB, method);
                    //ASTHelper.addArgument(mcB, method);
                    int insertIn = 0;
                    x.add(insertIn, new ExpressionStmt(mcB));
                }
                else {

                    //Avoid monitoring getters and setters and simple methods
                    int operations = MethodChangerVisitor.countOperations(n);
                    if(operations>=NUMBER_OF_OPERATIONS){

                        MethodCallExpr getContext = new MethodCallExpr();
//                        if (!cDef.isInstrumented()) {
//                            getContext.setName();
//                        } else {
//                            getContext.setName("getInstrumentation().getTargetContext");
//                        }
                        getContext.setName(InstrumentHelper.getApplicationFullName() + ".getAppContext");

                        MethodCallExpr mcB = ((MethodOrientedProfiler) InstrumentHelper.getProfiler()).markMethodStart(getContext,metodo);
//                        ASTHelper.addArgument(mcB, getContext);

//                        ASTHelper.addArgument(mcB, flagE);
//                        ASTHelper.addArgument(mcB, method);
                        //ASTHelper.addArgument(mcB, method);
                        int insertIn = 0;
//
//                        if(n.getName().equals("onCreate")){
//                            MethodCallExpr cont = new MethodCallExpr();
//                            if (!cDef.isInstrumented()) {
//                                cont.setName("getApplicationContext");
//                            } else {
//                                cont.setName("getInstrumentation().getTargetContext");
//                            }
//
//                            MethodCallExpr setAppContext = new MethodCallExpr();
//                            setAppContext.setName(InstrumentHelper.getApplicationFullName() + ".setAppContext");
//                            ASTHelper.addArgument(setAppContext, cont);
//                            x.add(insertIn++,new ExpressionStmt(setAppContext));
//                            x.add(insertIn, new ExpressionStmt(mcB));
//                        }
//                        else
                            x.add(insertIn, new ExpressionStmt(mcB));

                        MethodCallExpr mcE = ((MethodOrientedProfiler) InstrumentHelper.getProfiler()).marKMethodStop(getContext,metodo);
                        ReturnFlag hasRet = new ReturnFlag();
                        ReturnFlag unReachable = new ReturnFlag();
                        new ReturnVisitor().visit(n, hasRet);
                        new WhileChangerVisitor().visit(n, unReachable);
                        new ForChangerVisitor().visit(n, unReachable);

                        if(hasRet.hasRet()){

                            new GenericBlockVisitor().visit(n, new ExpressionStmt(mcE));
                        }

                        String stm = x.get(x.size()-1).getClass().getName();
                        if(stm.contains("ReturnStmt") || stm.contains("ThrowStmt")){
                            x.add((x.size()-1), new ExpressionStmt(mcE));
                        }else if(retType.contains("VoidType")){
                            if(!unReachable.hasRet()){
                                x.add(new ExpressionStmt(mcE));
                            }
                        }

                        ClassM cm = this.getClass(cDef.getName(), cDef.getPack());
                        cm.getMethods().add(n.getName());
                    }
                    //TODO add annotation to non profiled methods
//                    else {
//                        LinkedList<AnnotationExpr> anot = new LinkedList<AnnotationExpr>();
//                        anot.add(new MarkerAnnotationExpr(new NameExpr("TrepnIgnored")));
//                        n.setAnnotations(anot);
//                    }

                }

            }
        }
    }




}
