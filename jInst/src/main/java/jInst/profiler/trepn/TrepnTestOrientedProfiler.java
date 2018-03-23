package jInst.profiler.trepn;

import com.github.javaparser.ASTHelper;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import jInst.profiler.TestOrientedProfiler;

/**
 * Created by rrua on 17/06/17.
 */
public class TrepnTestOrientedProfiler implements TestOrientedProfiler {

    public static String fulllibrary = "com.greenlab.trepnlib.TrepnLib";
    private static String library = "TrepnLib";
    public static String startProfiling = "startProfilingTest";
    public static String stopProfiling = "stopProfilingTest";
    public static String markMethod = "traceMethod";
    public static String markTest = "traceTest";

    public TrepnTestOrientedProfiler(){}


    @Override
    public MethodCallExpr startProfiler(MethodCallExpr context) {
        MethodCallExpr mcB = new MethodCallExpr();
        mcB.setName( library + "." + startProfiling);
        ASTHelper.addArgument(mcB, context);
        //ASTHelper.addArgument(mcB, method);
        return mcB;


    }

    @Override
    public MethodCallExpr stopProfiler(MethodCallExpr context) {
        MethodCallExpr mcB = new MethodCallExpr();
        mcB.setName( library +"." + stopProfiling);
        ASTHelper.addArgument(mcB, context);
        //ASTHelper.addArgument(mcB, method);
        return mcB;
    }

    @Override
    public ImportDeclaration getLibrary() {
        return new ImportDeclaration(new NameExpr(fulllibrary),false,false);
    }

    @Override
    public MethodCallExpr getContext() {
        return null;
    }

    @Override
    public MethodCallExpr marKTest(MethodCallExpr context, String method) {
        MethodCallExpr mcB = new MethodCallExpr();
        mcB.setName( library +"." + markTest);
        Expression method1 = new StringLiteralExpr( method);
        ASTHelper.addArgument(mcB, method1);
        return  mcB;
    }

    @Override
    public MethodCallExpr markMethod(MethodCallExpr context, String method) {
        MethodCallExpr mcB = new MethodCallExpr();
        mcB.setName( library +"." + markMethod);
        Expression method1 = new StringLiteralExpr( method);
        ASTHelper.addArgument(mcB, method1);
        return  mcB;
    }

    @Override
    public MethodCallExpr markTest(MethodCallExpr context, String method) {
        MethodCallExpr mcB = new MethodCallExpr();
        mcB.setName( library +"." + markTest);
        Expression method1 = new StringLiteralExpr( method);
        ASTHelper.addArgument(mcB, method1);
        return  mcB;
    }


}
