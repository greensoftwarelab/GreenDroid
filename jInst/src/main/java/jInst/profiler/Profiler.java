package jInst.profiler;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

/**
 * Created by rrua on 17/06/17.
 */
public interface Profiler { // abstract Product
    MethodCallExpr startProfiler(MethodCallExpr context);
    MethodCallExpr stopProfiler(MethodCallExpr context);
    ImportDeclaration getLibrary();
    MethodCallExpr getContext();
    MethodCallExpr marKTest(MethodCallExpr context, String method);
}
