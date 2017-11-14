package jInst.visitors;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import jInst.transform.InstrumentHelper;
import jInst.visitors.utils.ClassDefs;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by rrua on 06/06/17.
 */
public class ClassOrInterfaceVisitor extends MethodChangerVisitor {



    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg){

        if(n.isInterface()){
            super.visit(n,arg);
            return;
        }
        else {
            //classe java
            ClassDefs cDef = (ClassDefs)arg;

//            if (n.getExtends()!=null)
//                for (ClassOrInterfaceType c: n.getExtends()){
//                    System.out.println("olha os extends sao estes, caso queiras" + c);
//                }

            String appAndroidName = InstrumentHelper.getApplicationFullName();
//            System.out.println("app android -> " + appAndroidName);
//            System.out.println("android name é este zé :" + appAndroidName);
            String thisClassName = cDef.getPack() + "." + cDef.getName();
//            System.out.println("thisclassname -> " +thisClassName);
//            System.out.println("esta é a classe " + thisClassName);
//            System.out.println("resultado = " + thisClassName.equals(appAndroidName));
            if (thisClassName.equals(appAndroidName)){
                // criar variavel context
                FieldDeclaration fieldDeclaration;
                ClassOrInterfaceType tipo1 =  new ClassOrInterfaceType();
                tipo1.setName("Context");
                ReferenceType rt1 = new ReferenceType(tipo1);
                VariableDeclarator vv = new VariableDeclarator( new VariableDeclaratorId("trepContext") );
                fieldDeclaration = new FieldDeclaration(10, rt1, vv); // modifiers = private

                ////// metodo de gets
                MethodDeclaration met = new MethodDeclaration();
                met.setModifiers(9);// public
                // tipo que devolve
                ClassOrInterfaceType tipo =  new ClassOrInterfaceType();
                tipo.setName("Context");
                ReferenceType rt = new ReferenceType();
                rt.setType(tipo);
                met.setType(rt);
                // nome do metodo
                met.setName("getAppContext");
                met.setModifiers(9); // public static
                // bloco cod
                BlockStmt bloco = new BlockStmt();
                List<Statement> l = new LinkedList<>();
                ReturnStmt ret =  new ReturnStmt();
                ret.setExpr(new NameExpr("trepContext"));
                l.add(ret);
                bloco.setStmts(l);
                met.setBody(bloco);
//                met.setModifiers(1);// void
                met.setDefault(false);

                // metodo para fazer set
                MethodDeclaration set = new MethodDeclaration();
                // tipo que devolve
                ClassOrInterfaceType tipoSet =  new ClassOrInterfaceType();
                tipoSet.setName("void");
                ReferenceType rtSet = new ReferenceType();
                rtSet.setType(new VoidType());
                set.setType(new VoidType());
                // nome do metodo
                set.setModifiers(9);
                set.setName("setAppContext");
                // bloco cod
                BlockStmt block = new BlockStmt();
                List<Statement> l1 = new LinkedList<>();
                ExpressionStmt sp = new ExpressionStmt();
                AssignExpr ase = new AssignExpr();
                ase.setTarget(new NameExpr("trepContext"));
                ase.setOperator(AssignExpr.Operator.assign);
                ase.setValue(new NameExpr("trep"));
                sp.setExpression(ase);
                l1.add(sp);
                block.setStmts(l1);
                set.setBody(block);
//                // param
                List<Parameter> params = new LinkedList<>();
                Parameter param = new Parameter();
                ReferenceType ref = new ReferenceType();
                ClassOrInterfaceType cit = new ClassOrInterfaceType();
                cit.setName("Context");
                ref.setType(cit);
                param.setType(ref);
                param.setVarArgs(false);
                param.setModifiers(0);
                param.setId(new VariableDeclaratorId("trep"));
                params.add(param);
                set.setParameters(params);

//                //
                met.setDefault(false);
                n.getMembers().add(fieldDeclaration);
                n.getMembers().add(met);
                n.getMembers().add(set);



            }

//            for(BodyDeclaration s : n.getMembers()){
//                System.out.println(s);
//            }


        }
    }

    @Override
    public void visit(MethodDeclaration n, Object arg) {
        super.visit(n,arg);
    }
}
