/**
 * Filename: TypeCheckerVisitor
 * Names: Kevin Ahn and Kyle Slager
 * CS461
 * Project 12
 */

package proj12AhnSlager.bantam.semant;
import proj12AhnSlager.bantam.ast.*;
import proj12AhnSlager.bantam.util.ClassTreeNode;
import proj12AhnSlager.bantam.util.SymbolTable;
import proj12AhnSlager.bantam.visitor.Visitor;
import proj12AhnSlager.bantam.util.ErrorHandler;
import proj12AhnSlager.bantam.util.Error;
import java.util.Hashtable;

/**
 *
 * @author KevinAhn, KyleSlager
 */
public class TypeCheckerVisitor extends Visitor
{
    private ClassTreeNode currentClass;
    private SymbolTable currentSymbolTable;
    private ErrorHandler errorHandler;
    private Program program;

    private Hashtable<String, ClassTreeNode> classMap;

    /**
     *
     * @param classMap
     * @param errorHandler
     * @param program
     */
    public TypeCheckerVisitor(Hashtable<String, ClassTreeNode> classMap, ErrorHandler errorHandler, Program program){
        this.classMap = classMap;
        this.errorHandler = errorHandler;
        this.currentSymbolTable = null;
        this.program = program;

    }

    /**
     *
     * @param currentClass
     */
    public void beginTypeChecking(ClassTreeNode currentClass){
        this.currentClass = currentClass;
        this.currentSymbolTable = this.currentClass.getVarSymbolTable();
        this.currentClass.getASTNode().accept(this);
    }

    /**
     * Method returns boolean after determining if a node is a subtype of another
     * @param node1
     * @param node2
     * @return
     */
    public boolean isSubTypeOf(String node1, String node2){
        //so far all attempts to get determine subTypes has revolved around the currentClass
        //but it continues to give us null pointer exceptions that we cannot determine the cause of
        return false;
    }

    /**
     *
     * @param node
     * @return
     */
    public boolean isClassType(String node){
        return  currentClass.getClassMap().containsKey(node) || node.equals("boolean") || node.equals("int") || node.equals("String");
    }

    /**
     * Visit a field node
     *
     * @param node the field node
     * @return null
     */
    public Object visit(Field node) {
        // The fields should have already been added to the symbol table by the
        // SemanticAnalyzer so the only thing to check is the compatibility of the init
        // expr's type with the field's type.
        if (!isClassType(node.getType())) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The declared type " + node.getType() + " of the field "
                            + node.getName() + " is undefined.");
        }
        Expr initExpr = node.getInit();
        if (initExpr != null) {
            initExpr.accept(this);
            if(!isSubTypeOf(initExpr.getExprType(),node.getType())) {
                System.out.println(initExpr.getExprType());
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                        currentClass.getASTNode().getFilename(), node.getLineNum(),
                        "The type of the initializer is " + initExpr.getExprType()
                         + " which is not compatible with the " + node.getName() +
                         " field's type " + node.getType());
            }
        }
        //Note: if there is no initExpr, then leave it to the Code Generator to
        //      initialize it to the default value since it is irrelevant to the
        //      SemanticAnalyzer.
        return null;
    }

    /**
     * Visit a method node
     *
     * @param node the Method node to visit
     * @return null
     */
    public Object visit(Method node) {
        if (!isClassType(node.getReturnType()) && !node.getReturnType().equals("void")){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The return type " + node.getReturnType() + " of the method "
                            + node.getName() + " is undefined.");
        }

        //create a new scope for the method body
        currentSymbolTable.enterScope();
        node.getFormalList().accept(this);
        node.getStmtList().accept(this);
        currentSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a formal parameter node
     *
     * @param node the Formal node
     * @return null
     */
    public Object visit(Formal node) {
        if (!isClassType(node.getType())) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The declared type " + node.getType() + " of the formal" +
                            " parameter " + node.getName() + " is undefined.");
        }
        // add it to the current scope
        currentSymbolTable.add(node.getName(), node.getType());
        return null;
    }

    /**
     * Visit a while statement node
     *
     * @param node the while statement node
     * @return null
     */
    public Object visit(WhileStmt node) {
        node.getPredExpr().accept(this);
        if(!node.getPredExpr().getExprType().equals("boolean")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The type of the predicate is " + node.getPredExpr().getExprType()
                            + " which is not boolean.");
        }
        currentSymbolTable.enterScope();
        node.getBodyStmt().accept(this);
        currentSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit an if statement node
     *
     * @param node the if statement node
     * @return null
     */
    public Object visit(IfStmt node){
        node.getPredExpr().accept(this);
        if(!node.getPredExpr().getExprType().equals("boolean")){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The type of the predicate is " + node.getPredExpr().getExprType()
                            + " which is not boolean.");
        }
        currentSymbolTable.enterScope();
        node.getElseStmt().accept(this);
        node.getThenStmt().accept(this);
        currentSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a for statement node
     *
     * @param node the for statement node
     * @return
     */
    public Object visit(ForStmt node){
        node.getInitExpr().accept(this);
        node.getPredExpr().accept(this);
        node.getUpdateExpr().accept(this);
        if(!node.getInitExpr().getExprType().equals("int")){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The type of the initialization is " + node.getPredExpr().getExprType()
                            + " which is not int.");
        }
        if(!node.getPredExpr().getExprType().equals("boolean")){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The type of the predicate is " + node.getPredExpr().getExprType()
                            + " which is not boolean.");
        }

        if(!node.getUpdateExpr().getExprType().equals("boolean")){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The type of the update is " + node.getPredExpr().getExprType()
                            + " which is not int.");
        }
        currentSymbolTable.enterScope();
        node.getBodyStmt().accept(this);
        currentSymbolTable.exitScope();
        return null;
    }

    /**
     * Helper for binary arithmetic expression nodes
     *
     * @param node
     * @return
     */
    public Object binaryArithExprHelper(BinaryArithExpr node){
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        String leftExprType = node.getLeftExpr().getExprType();
        String rightExprType = node.getRightExpr().getExprType();
        if(!leftExprType.equals(rightExprType) || !leftExprType.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The values of this Arithmetic Expression are "+ node.getLeftExpr().getExprType()
                            +" and " +node.getRightExpr().getExprType()+ ". Arithmetic Expression must be int int.");
        }
        node.setExprType("int");
        return null;
    }

    /**
     * Executes the binaryArithExprHelper
     *
     * @param node the binary arithmetic divide expression node
     * @return
     */
    public Object visit(BinaryArithDivideExpr node){
        binaryArithExprHelper(node);
        return null;
    }

    /**
     * Executes the binaryArithExprHelper
     *
     * @param node the binary arithmetic minus expression node
     * @return
     */
    public Object visit(BinaryArithMinusExpr node){
        binaryArithExprHelper(node);
        return null;
    }

    /**
     * Executes the binaryArithExprHelper
     *
     * @param node the binary arithmetic modulus expression node
     * @return
     */
    public Object visit(BinaryArithModulusExpr node){
        binaryArithExprHelper(node);
        return null;
    }

    /**
     * Executes the binaryArithExprHelper
     *
     * @param node the binary arithmetic plus expression node
     * @return
     */
    public Object visit(BinaryArithPlusExpr node){
        binaryArithExprHelper(node);
        return null;
    }

    /**
     * Executes the binaryArithExprHelper
     *
     * @param node the binary arithmetic times expression node
     * @return
     */
    public Object visit(BinaryArithTimesExpr node){
        binaryArithExprHelper(node);
        return null;
    }

    /**
     * Helper for binary logic and comparison expressions
     *
     * @param node takes in the node of the expression
     * @param type the type gives the type of expression necessary
     * @param input determines whether an error should be thrown in some cases
     * @return
     */
    public Object binaryLogicandCompHelper(BinaryExpr node, String type, boolean input){
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        String leftExprType = node.getLeftExpr().getExprType();
        String rightExprType = node.getRightExpr().getExprType();
        if(!leftExprType.equals(rightExprType) || !input) {
            if(type == "boolean") {
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                        currentClass.getASTNode().getFilename(), node.getLineNum(),
                        "The values of this Binary Expression are " + leftExprType
                                + " and " + rightExprType + ". Expression must be boolean boolean.");
            }
            else if(type == "int"){
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                        currentClass.getASTNode().getFilename(), node.getLineNum(),
                        "The values of this Binary Expression are " + leftExprType
                                + " and " + rightExprType + ". Expression must be int int.");
            }
            else{
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                        currentClass.getASTNode().getFilename(), node.getLineNum(),
                        "The two values of this Binary Expression are not compatible");
            }
        }
        node.setExprType("boolean");
        return null;
    }

    /**
     * Executes the binaryLogicandCompHelper
     *
     * @param node the binary comparison equals expression node
     * @return
     */
    public Object visit(BinaryCompEqExpr node){
        return binaryLogicandCompHelper(node, "other", true);
    }

    /**
     * Executes the binaryLogicandCompHelper
     *
     * @param node the binary comparison greater to or equal to expression node
     * @return
     */
    public Object visit(BinaryCompGeqExpr node){
        return binaryLogicandCompHelper(node, "int", node.getLeftExpr().getExprType().equals("int"));
    }

    /**
     * Executes the binaryLogicandCompHelper
     *
     * @param node the binary comparison greater than expression node
     * @return
     */
    public Object visit(BinaryCompGtExpr node){
        return binaryLogicandCompHelper(node, "int", node.getLeftExpr().getExprType().equals("int"));
    }

    /**
     * Executes the binaryLogicandCompHelper
     *
     * @param node the binary comparison less than or equal to expression node
     * @return
     */
    public Object visit(BinaryCompLeqExpr node){
        return binaryLogicandCompHelper(node, "int", node.getLeftExpr().getExprType().equals("int"));
    }

    /**
     * Executes the binaryLogicandCompHelper
     *
     * @param node the binary comparison less than expression node
     * @return
     */
    public Object visit(BinaryCompLtExpr node){
        return binaryLogicandCompHelper(node, "int", node.getLeftExpr().getExprType().equals("int"));
    }

    /**
     * Executes the binaryLogicandCompHelper
     *
     * @param node the binary comparison not equals expression node
     * @return
     */
    public Object visit(BinaryCompNeExpr node){
        return binaryLogicandCompHelper(node, "other", true);
    }

    /**
     * Executes the binaryLogicandCompHelper
     *
     * @param node the binary logical AND expression node
     * @return
     */
    public Object visit(BinaryLogicAndExpr node){
        return binaryLogicandCompHelper(node, "boolean", node.getLeftExpr().getExprType().equals("boolean"));
    }

    /**
     * Executes the binaryLogicandCompHelper
     *
     * @param node the binary logical OR expression node
     * @return
     */
    public Object visit(BinaryLogicOrExpr node){
        return binaryLogicandCompHelper(node, "boolean", node.getLeftExpr().getExprType().equals("boolean"));
    }


    /**
     * Visit a return statement
     *
     * @param node the return statement node
     * @return null
     */
    public Object visit(ReturnStmt node){
        node.getExpr().accept(this);
        return null;
    }

    /**
     * Visit an Expression statement
     *
     * @param node the expression statement node
     * @return null
     */
    public Object visit(ExprStmt node){
        if(node.getExpr() != null) {
            node.getExpr().accept(this);
        }
        return null;
    }

    /**
     * Visit a declaration statement
     *
     * @param node the declaration statement node
     * @return null
     */
    public Object visit(DeclStmt node){
        node.getInit().accept(this);
        return null;
    }

    /**
     * Visit a block statement node
     *
     * @param node the block statement node
     * @return null
     */
    public Object visit(BlockStmt node) {
        currentSymbolTable.enterScope();
        node.getStmtList().accept(this);
        currentSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a new expression node
     *
     * @param node the new expression node
     * @return null
     */
    public Object visit(NewExpr node) {
        if(currentClass.getClassMap().get(node.getType()) == null) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The type " + node.getType() + " does not exist.");
            node.setExprType("Object"); // to allow analysis to continue
        }
        else {
            node.setExprType(node.getType());
        }
        return null;
    }

    /**
     * Method for avoiding code duplication in the visit methods for the UnaryExpressions
     *
     * @param node
     * @param type
     * @return null
     */
    public Object unaryExprHelper(UnaryExpr node, String type){
        node.getExpr().accept(this);
        if(!node.getExpr().getExprType().equals(type)) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The " + node.getExpr().getExprType() + " operator applies only to " + type +
                            " expressions, not " + node.getExpr().getExprType() + " expressions.");
        }
        node.setExprType(type);
        return null;
    }

    /**
     * Visit a unary not expression node
     *
     * @param node the unary NOT expression node
     * @return null
     */
    public Object visit(UnaryNotExpr node) {
        return unaryExprHelper(node, "boolean");
    }

    /**
     * Visit a unary negative expression node
     *
     * @param node the unary negation expression node
     * @return
     */
    public Object visit(UnaryNegExpr node){
        return unaryExprHelper(node, "int");
    }

    /**
     * Visit a unary decrement expression node
     *
     * @param node the unary decrement expression node
     * @return
     */
    public Object visit(UnaryDecrExpr node){
        return unaryExprHelper(node, "int");
    }

    /**
     * Visit a unary increment expression node
     *
     * @param node the unary increment expression node
     * @return
     */
    public Object visit(UnaryIncrExpr node){
        return unaryExprHelper(node, "int");
    }

    /**
     * Visit an int constant expression node
     *
     * @param node the int constant expression node
     * @return null
     */
    public Object visit(ConstIntExpr node) {
        node.setExprType("int");
        return null;
    }

    /**
     * Visit a boolean constant expression node
     *
     * @param node the boolean constant expression node
     * @return null
     */
    public Object visit(ConstBooleanExpr node) {
        node.setExprType("boolean");
        return null;
    }

    /**
     * Visit a string constant expression node
     *
     * @param node the string constant expression node
     * @return null
     */
    public Object visit(ConstStringExpr node) {
        node.setExprType("String");
        return null;
    }

    /**
     * Visit a variable expression
     *
     * @param node the variable expression node
     * @return
     */
    public Object visit(VarExpr node){
        Expr varReferenceExpression = node.getRef();
        varReferenceExpression.accept(this);
        if (currentSymbolTable.lookup(node.getName()) != null) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The variable name " + node.getName() + " has already been used in this scope.");
        }

        node.setExprType(varReferenceExpression.getExprType());
        return null;
    }

    /**
     * Visit a cast expression
     *
     * @param node the cast expression node
     * @return
     */
    public Object visit(CastExpr node){
        Expr castReferenceExpression = node.getExpr();
        castReferenceExpression.accept(this);

        //code determining whether an error is given

        node.setExprType(node.getType());
        return null;
    }

    /**
     * Visit an instanceOf expression
     *
     * @param node the instanceof expression node
     * @return
     */
    public Object visit(InstanceofExpr node){
        Expr instanceOfReferenceExpression = node.getExpr();
        instanceOfReferenceExpression.accept(this);

        //code determining whether an error is given

        return null;
    }

    /**
     * Visit a new array expression
     *
     * @param node the new array expression node
     * @return
     */
    public Object visit(NewArrayExpr node){
        Expr arraySize = node.getSize();
        arraySize.accept(this);
        if (!arraySize.getExprType().equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "Array size must be given as an integer value");
            arraySize.setExprType("int");
        }
        else{
            node.setExprType(node.getType());
        }

        return null;
    }

    /**
     * Visit a dispatch expression
     *
     * @param node the dispatch expression node
     * @return
     */
    public Object visit(DispatchExpr node){
        Expr dispatchReferenceExpression = node.getRefExpr();
        dispatchReferenceExpression.accept(this);

        Method currentMethod = (Method) currentClass.getMethodSymbolTable().lookup(node.getMethodName());
        if(node.getActualList().getSize() == currentMethod.getFormalList().getSize()){
            for(int i = 0; i < node.getActualList().getSize(); i++){
                Formal actualList = (Formal) node.getActualList().get(i);
                Formal formalList = (Formal) currentMethod.getFormalList().get(i);
                if(!actualList.getType().equals(formalList.getType()));
                    errorHandler.register(Error.Kind.SEMANT_ERROR,
                        currentClass.getASTNode().getFilename(), node.getLineNum(),
                        "Parameter type does not match expected parameter type");
            }
        }

        return null;
    }

    /**
     * Visit an assignment expression
     *
     * @param node the assignment expression node
     * @return
     */
    public Object visit(AssignExpr node){
        if(currentSymbolTable.lookup(node.getName())==null){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The variable " + node.getName() + " has not been defined");
        }
        return null;
    }

    /**
     * Visit an array assignment expression
     *
     * @param node the array assignment expression node
     * @return
     */
    public Object visit(ArrayAssignExpr node){
        node.getExpr().accept(this);
        node.getIndex().accept(this);
        if(!node.getIndex().getExprType().equals("int")){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The index of assignment is a " + node.getIndex().getExprType() +
                            " and it should be an int.");
        }
        node.getIndex().setExprType("int");
        return null;
    }

}
