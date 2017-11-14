/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst.visitors;

import jInst.visitors.utils.ClassDefs;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 *
 * @author User
 */
/**
     * Simple visitor implementation for visiting MethodDeclaration nodes. 
     */
public class MethodVisitor extends VoidVisitorAdapter {

    @Override
    public void visit(MethodDeclaration n, Object arg) {
        ClassDefs cd = (ClassDefs)arg;
        if(n.getName().startsWith("test") && n.getType().toString().contains("void") && n.getParameters() == null){
            cd.setTests(true);
        }
    }
}
