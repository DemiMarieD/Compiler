package yapl.lib;

import yapl.compiler.Token;
import yapl.interfaces.CompilerError;
import yapl.interfaces.Symbol;

public class YAPLException extends Exception implements CompilerError {


    private int error;
    public Token currentToken;

    private Symbol symbol;
    private String parentName;
    //CA4
    private String name;
    private int argNum;

    private String message = "";

    //todo No token for errors thrown in symboletable...
    public YAPLException(int errorNumber){
        this.error = errorNumber;
    }

    public YAPLException(int errorNumber, Symbol symbol){
        this.error = errorNumber;
        this.symbol = symbol;
    }

    //in case of internal error no symbol is needed -> no msg will be displayed
    public YAPLException(int errorNumber, Token token){
        this.error = errorNumber;
        this.currentToken = token;
        setMessage();
    }

    // ----  CA4
    public YAPLException(int errorNumber, Token token, String name){
        this.error = errorNumber;
        this.currentToken = token;
        this.name = name;
        setMessage();
    }

    public YAPLException(int errorNumber, Token token, int agrumentNumber, String name){
        this.error = errorNumber;
        this.currentToken = token;
        this.argNum = agrumentNumber;
        this.name = name;
        setMessage();
    }

    // ---------- CA3
    public YAPLException(int errorNumber, Symbol symbol, Token token){
        this.error = errorNumber;
        this.currentToken = token;
        this.symbol = symbol;
        setMessage();
    }

    //in case of EndIdentMismatch we need the "supposed name"
    public YAPLException(int errorNumber, Symbol symbol, Token token, String parent_name){
        this.error = errorNumber;
        this.currentToken = token;
        this.symbol = symbol;
        this.parentName = parent_name;
        setMessage();
    }

    public void setMessage(){
        switch (error){
            case CompilerError.SymbolExists:
                message = "symbol '" + symbol.getName() + "' already declared in current scope (as " + symbol.getKindString() + ")";
                break;
            case CompilerError.IdentNotDecl:
                message = "identifier '" + symbol.getName() + "' not declared";
                break;
            case CompilerError.SymbolIllegalUse:
                message = "illegal use of " + symbol.getKindString() + " '" + symbol.getName() + "'";
                break;
            case CompilerError.EndIdentMismatch:
                message = "End " + symbol.getName() + " does not match "+ symbol.getKindString() + " " + parentName;
                break;

            // CA4
            case CompilerError.SelectorNotArray  :
                message = "expression before ’[’ is not an array type";
                break;
            case CompilerError.BadArraySelector  :
                message = "array index or dimension is not an integer type";
                break;
            case CompilerError.ArrayLenNotArray  :
                message = "expression after ’#’ is not an array type";
                break;
            case CompilerError.SelectorNotRecord  :
                message = "expression before ’.’ is not a record type";
                break;
            case CompilerError.InvalidRecordField  :
                message = "invalid field "+ currentToken.image + " of record " + name;
                break;
            case CompilerError.IllegalOp1Type  :
                message = "illegal operand type for unary operator " + currentToken.image;
                break;
            case CompilerError.IllegalOp2Type  :
                message = "illegal operand types for binary operator " + currentToken.image;
                break;
            case CompilerError.IllegalRelOpType  :
                message = "non-integer operand types for relational operator " + currentToken.image;
                break;
            case CompilerError.IllegalEqualOpType  :
                message = "illegal operand types for equality operator " + currentToken.image;
                break;
            case CompilerError.ProcNotFuncExpr  :
                message = "using procedure "+ name + " (not a function) in expression";
                break;
            case CompilerError.InvalidNewType  :
                message = "invalid type used with ’new’";
                break;
            case CompilerError.TypeMismatchAssign  :
                message = "type mismatch in assignment";
                break;
            case CompilerError.ArgNotApplicable  :
                message = "argument #"+ argNum + " not applicable to procedure " + name;
                break;
            case CompilerError.TooFewArgs  :
                message = "too few arguments for procedure " + name;
                break;
            case CompilerError.CondNotBool  :
                message = "condition is not a boolean expression";
                break;
            case CompilerError.MissingReturn  :
                message = "missing Return statement in function " + name;
                break;
            case CompilerError.InvalidReturnType  :
                message = "returning none or invalid type from function " + name;
                break;
            case CompilerError.IllegalRetValProc  :
                message = "illegal return value in procedure "+ name +" (not a function)";
                break;
            case CompilerError.IllegalRetValMain  :
                message = "illegal return value in main program";
                break;

            default:
                message = "";

        }
    }


    public int errorNumber()
    {
        return error;
    }

    public int line() {
        return (currentToken == null ) ? -1
                : currentToken.beginLine;
    }

    public int column() {
        return (currentToken == null ) ? -1
                : currentToken.beginColumn;
    }

    public String getMessage() {
        return message;
    }
}
