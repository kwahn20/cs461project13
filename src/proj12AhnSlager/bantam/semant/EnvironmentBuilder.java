/**
 * Filename: EnvironmentBuilder
 * Names: Kevin Ahn and Kyle Slager
 * CS461
 * Project 12
 */
package proj12AhnSlager.bantam.semant;

import proj12AhnSlager.bantam.ast.*;
import proj12AhnSlager.bantam.util.ClassTreeNode;
import proj12AhnSlager.bantam.util.Error;
import proj12AhnSlager.bantam.util.ErrorHandler;
import proj12AhnSlager.bantam.visitor.Visitor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * Class to build the environment that is used for the analyze method in the
 * SemanticAnalyzer
 * @author Kevin Ahn, Kyle Slager
 */
public class EnvironmentBuilder extends Visitor {
    /**
     * Maps class names to ClassTreeNode objects representing the class
     */
    private Hashtable<String, ClassTreeNode> classMap;
    private HashSet<String> illegalNames;
    private ClassTreeNode currentClass;
    private ErrorHandler errorHandler;
    private Program program;

    /**
     * Constructor for the EnvironmentBuilder
     * @param classMap
     * @param currClass
     * @param errorHandler
     * @param program
     */
    public EnvironmentBuilder(Hashtable classMap, ClassTreeNode currClass, ErrorHandler errorHandler, Program program){
        this.classMap = classMap;
        this.currentClass = currClass;
        this.program = program;
        this.errorHandler = errorHandler;
        this.illegalNames = new HashSet<String>(Arrays.asList("null", "this", "super", "void", "int", "boolean"));
    }

    /**
     * build method to set the current class to null and call the programs accept method
     */
    public void build(){
        this.currentClass = null;
        this.program.accept(this);
    }

    /**
     * overrides the Class_ visit method to get the current class'
     * symbol tables
     * @param node the class node
     * @return
     */
    public Object visit(Class_ node){
        currentClass = this.classMap.get(node.getName());
        currentClass.getVarSymbolTable().enterScope();
        currentClass.getMethodSymbolTable().enterScope();
        node.getMemberList().accept(this);
        currentClass.getMethodSymbolTable().exitScope();
        currentClass.getVarSymbolTable().exitScope();

        return null;
    }

    /**
     * overrides the Field visit method to get the current class'
     * symbol tables. Checks against illegal names or previously declared names
     * @param node the field node
     * @return
     */
    public Object visit(Field node) {

        String declaredName = node.getName();

        // checks against the set of illegal names and registers an error if one is found
        if(this.illegalNames.contains(declaredName)){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The name " + declaredName + " is reserved");
        }

        // gets the VarSymbolTable and checks that the first element is not null
        if(this.currentClass.getVarSymbolTable().peek(declaredName) != null){
            // checks against previously declared variables
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The name " + declaredName + " has been declared prior");
        }
        this.currentClass.getVarSymbolTable().add(declaredName, node.getType());

        return null;
    }

    /**
     * overrides the method visit method from the visitor class
     * @param node the method node
     * @return
     */
    public Object visit(Method node){

        String declaredName = node.getName();

        if(this.illegalNames.contains(declaredName)){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The name " + declaredName + " is reserved");
        }

        if(this.currentClass.getMethodSymbolTable().peek(declaredName) != null){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The name " + declaredName + " has been declared prior");
        }

        currentClass.getMethodSymbolTable().add(node.getName(), node);


        currentClass.getMethodSymbolTable().enterScope();
        node.getFormalList().accept(this);
        node.getStmtList().accept(this);
        currentClass.getMethodSymbolTable().exitScope();

        return null;
    }


    /**
     * Overrides the visit method for the WhileStmt
     * enter and exit the proper scopes
     * @param node the WhileStmt node
     * @return
     */
    @Override
    public Object visit(WhileStmt node){
        currentClass.getVarSymbolTable().enterScope();
        super.visit(node);
        currentClass.getVarSymbolTable().exitScope();
        return null;
    }

    /**
     * Overrides the visit method for the ForStmt
     * enter and exit the proper scopes
     * @param node the ForStmt node
     * @return
     */
    @Override
    public Object visit(ForStmt node){
        currentClass.getVarSymbolTable().enterScope();
        super.visit(node);
        currentClass.getVarSymbolTable().exitScope();
        return null;
    }
}
