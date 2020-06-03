package yapl.compiler;

import yapl.impl.BackendMJ;
import yapl.interfaces.Attrib;
import yapl.interfaces.CodeGen;
import yapl.interfaces.Symbol;
import yapl.lib.*;

public class CodeGenImpl implements CodeGen {
    private BackendMJ backend;


    @Override
    public String newLabel() {
        return null;
    }

    @Override
    public void assignLabel(String label) {

    }

    @Override
    public byte loadValue(Attrib attr) throws YAPLException {
        return 0;
    }

    @Override
    public byte loadAddress(Attrib attr) throws YAPLException {
        return 0;
    }

    @Override
    public void freeReg(Attrib attr) {

    }

    @Override
    public void allocVariable(Symbol sym) throws YAPLException {

    }

    @Override
    public void setFieldOffsets(RecordType record) {

    }

    @Override
    public void storeArrayDim(int dim, Attrib length) throws YAPLException {

    }

    @Override
    public Attrib allocArray(ArrayType arrayType) throws YAPLException {
       return null;
    }

    @Override
    public Attrib allocRecord(RecordType recordType) throws YAPLException {
        return null;
    }

    @Override
    public void setParamOffset(Symbol sym, int pos) {

    }

    @Override
    public void arrayOffset(Attrib arr, Attrib index) throws YAPLException {

    }

    @Override
    public void recordOffset(Attrib record, Symbol field) throws YAPLException {

    }

    @Override
    public Attrib arrayLength(Attrib arr) throws YAPLException {
        return null;
    }

    @Override
    public void assign(Attrib lvalue, Attrib expr) throws YAPLException {

    }

    @Override
    public Attrib op1(Token op, Attrib x) throws YAPLException {
        // check if int otherwise no comparing possible
        if (!(x.getType() instanceof IntType)) {
            throw new YAPLException(YAPLException.IllegalRelOpType, op);
        }
        // get registers of value of attributes
        byte xReg = loadValue(x);
        switch(op.kind) {
            //todo check backend methods
            case ParserConstants.PLUS:
                break;
            case ParserConstants.MINUS:
                break;
        }
        return null;
    }

    @Override
    public Attrib op2(Attrib x, Token op, Attrib y) throws YAPLException {
        // check if int otherwise no comparing possible
        if (!(x.getType() instanceof IntType && y.getType() instanceof IntType)) {
            throw new YAPLException(YAPLException.IllegalRelOpType, op);
        }
        // get registers of value of attributes
        byte xReg = loadValue(x);
        byte yReg = loadValue(y);

        switch(op.kind) {
            //todo check backend methods
            case ParserConstants.PLUS:
                break;
            case ParserConstants.MINUS:
                break;
            case ParserConstants.MULT:
                break;
            case ParserConstants.MOD:
                break;
            case ParserConstants.DIV:
                break;
        }

        return null;
    }

    @Override
    public Attrib relOp(Attrib x, Token op, Attrib y) throws YAPLException {
        // check if int otherwise no comparing possible
        if (!(x.getType() instanceof IntType && y.getType() instanceof IntType)) {
            throw new YAPLException(YAPLException.IllegalRelOpType, op);
        }
        // get registers of value of attributes
        byte xReg = loadValue(x);
        byte yReg = loadValue(y);
        //check operator
        switch(op.kind) {
            // todo get token number
            case ParserConstants.LESS:
                // compare x and y and save in x
                //todo something
                backend.isLess();
                break;
            //todo check backend methods
            case ParserConstants.AND:
                break;
            case ParserConstants.OR:
                break;
            case ParserConstants.GREATER:
                break;
            case ParserConstants.GREATER_EQAUL:
                break;
            case ParserConstants.LESS_EQUAL:
                break;
            case ParserConstants.EQUAL:
                break;
            case ParserConstants.NOT_EQUAL:
                break;
            default:
                throw new YAPLException(YAPLException.IllegalRelOpType, op);
        }
        // in the register of x will now be a boolean (result of calc) -> change type
        x.setType(new BoolType());
        x.setConstant(x.isConstant() && y.isConstant());
        freeReg(y);
        return x;
    }

    @Override
    public Attrib equalOp(Attrib x, Token op, Attrib y) throws YAPLException {
        return null;
    }

    @Override
    public void enterProc(Symbol proc) throws YAPLException {

    }

    @Override
    public void exitProc(Symbol proc) throws YAPLException {

    }

    @Override
    public void returnFromProc(Symbol proc, Attrib returnVal) throws YAPLException {

    }

    @Override
    public Attrib callProc(Symbol proc, Attrib[] args) throws YAPLException {
        return null;
    }

    @Override
    public void writeString(String string) throws YAPLException {

    }

    @Override
    public void branchIfFalse(Attrib condition, String label) throws YAPLException {

    }

    @Override
    public void jump(String label) {

    }
}
