package jInst.profiler;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

/**
 * Created by rrua on 17/06/17.
 */
public interface TestOrientedProfiler extends Profiler { // abstract product

//    MethodCallExpr startProfiler(MethodCallExpr context);
//    MethodCallExpr stopProfiler(MethodCallExpr context);
    MethodCallExpr markMethod(MethodCallExpr context, String method);
    MethodCallExpr markTest(MethodCallExpr context, String method);


}
