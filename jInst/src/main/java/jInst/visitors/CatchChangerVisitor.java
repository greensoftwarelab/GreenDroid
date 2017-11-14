/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst.visitors;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 *
 * @author User
 */
public class CatchChangerVisitor extends VoidVisitorAdapter {

    @Override
    public void visit(CatchClause n, Object arg) {
        ReturnFinder.verify((BlockStmt)n.getCatchBlock(), (Statement)arg);
    }
    
}
