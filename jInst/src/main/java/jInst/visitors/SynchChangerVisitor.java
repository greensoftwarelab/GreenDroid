/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst.visitors;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 *
 * @author User
 */
public class SynchChangerVisitor extends VoidVisitorAdapter {

    @Override
    public void visit(SynchronizedStmt n, Object arg) {
        ReturnFinder.verify((BlockStmt)n.getBlock(), (Statement)arg);
    }
    
}
