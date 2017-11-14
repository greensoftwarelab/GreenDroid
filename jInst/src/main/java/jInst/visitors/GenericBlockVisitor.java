/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst.visitors;

import com.github.javaparser.ast.body.MethodDeclaration;

/**
 *
 * @author User
 */
public class GenericBlockVisitor {
    
    public void visit(MethodDeclaration n, Object arg) {
        new IfChangerVisitor().visit(n, arg);
        new CatchChangerVisitor().visit(n, arg);
        new TryChangerVisitor().visit(n, arg);
        new SwitchEntryChangerVisitor().visit(n, arg);
        new SynchChangerVisitor().visit(n, arg);
    }
    
}
