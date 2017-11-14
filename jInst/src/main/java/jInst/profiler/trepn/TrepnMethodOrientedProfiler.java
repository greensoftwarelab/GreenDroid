package jInst.profiler.trepn;

import com.github.javaparser.ASTHelper;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import jInst.profiler.MethodOrientedProfiler;
import jInst.visitors.utils.ClassDefs;

/**
 * Created by rrua on 17/06/17.
 * Created by rrua on 17/06/17.
 */

public class TrepnMethodOrientedProfiler implements MethodOrientedProfiler{


    private static String fulllibrary = "com.greenlab.trepnlib.TrepnLib";
    private static String library = "TrepnLib";
    private static String startProfiling = "startProfiling";
    private static String stopProfiling = "stopProfiling";
    private static String markstartMethod = "updateState";
    private static String markstopMethod = "updateState";
    private static String context = "null";


    public TrepnMethodOrientedProfiler(){}

    @Override
    public MethodCallExpr startProfiler(MethodCallExpr context) {
        MethodCallExpr mcB = new MethodCallExpr();
        mcB.setName( library + "." + startProfiling);
        ASTHelper.addArgument(mcB, context);
        return mcB;
    }

    @Override
    public MethodCallExpr stopProfiler(MethodCallExpr context) {
        MethodCallExpr mcB = new MethodCallExpr();
        mcB.setName( library +"." + stopProfiling);
        ASTHelper.addArgument(mcB, context);
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
    public MethodCallExpr markMethodStart(MethodCallExpr context, String method) {
        MethodCallExpr mcB = new MethodCallExpr();
        mcB.setName( library +"." + markstartMethod);
        ASTHelper.addArgument(mcB, new NameExpr("null"));
        Expression flagE = new IntegerLiteralExpr("1");
        Expression method1 = new StringLiteralExpr( method);
        ASTHelper.addArgument(mcB, flagE);
        ASTHelper.addArgument(mcB, method1);
        return mcB;
    }

    @Override
    public MethodCallExpr marKMethodStop(MethodCallExpr context, String method) {
        MethodCallExpr mcB = new MethodCallExpr();
        mcB.setName( library +"." + markstopMethod);
        //ASTHelper.addArgument(mcB, context);
        ASTHelper.addArgument(mcB, new NameExpr("null"));
        Expression flagE = new IntegerLiteralExpr("0");
        Expression method1 = new StringLiteralExpr( method);
        ASTHelper.addArgument(mcB, flagE);
        ASTHelper.addArgument(mcB, method1);
        return  mcB;
    }
}
