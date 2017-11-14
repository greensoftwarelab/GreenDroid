package jInst.profiler;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

/**
 * Created by rrua on 17/06/17.
 */
public interface MethodOrientedProfiler extends Profiler{ //  product

//    MethodCallExpr startProfiler(MethodCallExpr context);
//    MethodCallExpr stopProfiler(MethodCallExpr context);

    MethodCallExpr markMethodStart(MethodCallExpr context, String method);



    MethodCallExpr marKMethodStop(MethodCallExpr context, String method);
}
