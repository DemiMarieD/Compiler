package yapl.compiler;

import com.sun.jdi.BooleanType;
import org.w3c.dom.Attr;
import yapl.impl.AttribImpl;
import yapl.impl.BackendMJ;
import yapl.interfaces.*;
import yapl.lib.*;

import java.io.IOException;
import java.io.OutputStream;

public class CodeGenImpl implements CodeGen {
    private BackendMJ backend;
    private OutputStream outputStream;
    private int labelNum = 0;

    public CodeGenImpl(OutputStream outputStream){
        this.backend = new BackendMJ();
        this.outputStream = outputStream;
    }

    @Override
    public String newLabel() {
        return "L" + this.labelNum++;
    }

    @Override
    public void assignLabel(String label) {
        backend.assignLabel(label);
    }

    public int getVal(Type type){
        int val = 0;
        if (type instanceof IntType){
            val = ((IntType) type).value;
        }else if (type instanceof BoolType){
            boolean b_val = ((BoolType) type).value;
            val = backend.boolValue(b_val);
        }
        return val;
    }

    public void saveAt(Attrib attr){
        if(attr.getKind() == Attrib.ArrayElement) {
            backend.storeArrayElement();
        }else{
            int addr = attr.getOffset();
            if (attr.getIsGlobal()) {
                backend.storeWord(MemoryRegion.STATIC, addr);
            } else {
                backend.storeWord(MemoryRegion.STACK, addr);
            }
        }

    }

    @Override
    public byte loadValue(Attrib attr) throws YAPLException {
          //  int addr = backend.allocStack(1);

            //if global then it is saved in static data area
            switch (attr.getKind()){
                //Variables and Type names are constant
                case Attrib.Constant:
                    if(attr.getOffset() < 0){
                        //if we don't have an offset it is most likely a number not a var to load
                        Type type = attr.getType();
                        int val = getVal(type);
                        backend.loadConst(val);

                    }else if (attr.getIsGlobal()) {
                        backend.loadWord(MemoryRegion.STATIC, attr.getOffset());
                        //else we load Const to stack
                    } else {
                        backend.loadWord(MemoryRegion.STACK, attr.getOffset());
                    }
                    break;

                //Symbols of kind Constant are attributes of type regAddress
                case Attrib.RegAddress:
                    if (attr.getIsGlobal()) {
                        backend.loadWord(MemoryRegion.STATIC, attr.getOffset());
                    } else {
                        backend.loadWord(MemoryRegion.STACK, attr.getOffset());
                    }
                    break;

                case Attrib.ArrayElement:
                    // both base address and index should reside in registers already?!
                    // -> base addr and index value should be on the stack ?!
                    backend.loadArrayElement();
                    break;

                case Attrib.MemoryOperand:
                    //backend.loadWord(MemoryRegion.STACK, attr.getOffset());
                    break;

                case Attrib.RecordField:
                    break;

                case Attrib.RegValue:
                    //do nothing, value is already on the execution stack
                    break;
            }

            //Now that the attribute value is on the stack the kind can be changed
            attr.setKind(Attrib.RegValue);
          //  attr.setOffset(addr);

        //todo return & save current sp (stack pointer) ?!
        return 0;
    }

    @Override
    public byte loadAddress(Attrib attr) throws YAPLException {
        switch (attr.getKind()){


        }
        //Now that the attribute address is on the stack the kind can be changed
        attr.setKind(Attrib.RegAddress);
        //todo return & save current sp (stack pointer) ?!
        return 0;
    }

    @Override
    public void freeReg(Attrib attr) {

    }

    @Override
    public void allocVariable(Symbol sym) throws YAPLException {
        int addr = -1;

        //make room on static data area
        if(sym.isGlobal()){ addr = backend.allocStaticData(1);

        //if not global on stack?!
        }else{ addr = backend.allocStack(1); }

        sym.setOffset(addr);

        //If we have a constant decl we need a value (that will not change)
        if(sym.getKind() == Symbol.Constant){
            Type type = sym.getType();
            int val = getVal(type);
            //loads to expression stack
            backend.loadConst(val);

            if(sym.isGlobal()) {
                //stores top from expression stack in static area at address of Offset
                backend.storeWord(MemoryRegion.STATIC, sym.getOffset());
            }else{
                //stores top from expression stack in stack at address of Offset
                backend.storeWord(MemoryRegion.STACK, sym.getOffset());
            }
        }

    }

    @Override
    public void setFieldOffsets(RecordType record) {

    }

    @Override
    public void storeArrayDim(int dim, Attrib length) throws YAPLException {
        // in the backend our help array is of size 5 => max dim = 5
        if(dim < 6) {
            loadValue(length);
            backend.storeArrayDim(dim);
        }else{
            throw new YAPLException(CompilerError.TooManyDims);
        }
    }

    @Override
    public Attrib allocArray(ArrayType arrayType) throws YAPLException {
        backend.allocArray(); //The array start address is returned on the expression stack at run-time.

        //attribute representing a register operand holding the array base address
        // kind is regVal because the address is already on the stack.
        Attrib attrib = new AttribImpl(Attrib.RegValue, arrayType, Token.newToken(0)); //token will be overwritten in parser

       return attrib;
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
        // arr = representing the array base address;
        loadValue(arr);

            //todo incase of subarray?!?
       /* if (arr.getKind() == Attrib.ArrayElement) {
            //loadVal(arr.getIndex());
            //backend.loadArrayElement();
        } */

        loadValue(index);

        // kind, type, reg should be updated -> ArrayElement
        // for ArrayElements, both base address and index must reside in registers!
        index.setKind(Attrib.ArrayElement);
        // arr.setType(arrtype.getBase()); //this is done in the parser !!

    }

    @Override
    public void recordOffset(Attrib record, Symbol field) throws YAPLException {

    }

    @Override
    public Attrib arrayLength(Attrib arr) throws YAPLException {
        loadValue(arr); // arr represents an address operand, so load its value!
        backend.arrayLength();
        arr.setType(new IntType());
        arr.setKind(Attrib.RegValue);
        return arr;
    }

    @Override
    public void assign(Attrib lvalue, Attrib expr) throws YAPLException {
            loadValue(expr); //loads to expression stack
            //save top of exprStack in mem at addr of lvalue
            saveAt(lvalue);
    }

    @Override
    public Attrib op1(Token op, Attrib x) throws YAPLException {
        loadValue(x);

        switch(op.kind) {
            case ParserConstants.MINUS:
                backend.neg();
                break;
        }

        return x;
    }

    @Override
    public Attrib op2(Attrib x, Token op, Attrib y) throws YAPLException {

        loadValue(x);
        loadValue(y);

        switch(op.kind) {
            case ParserConstants.PLUS:
                backend.add();
                break;
            case ParserConstants.MINUS:
                backend.sub();
                break;
            case ParserConstants.MULT:
                backend.mul();
                break;
            case ParserConstants.MOD:
                backend.mod();
                break;
            case ParserConstants.DIV:
                backend.div();
                break;
            default:
                throw new YAPLException(YAPLException.IllegalOp2Type, op);
        }

        //setup attr
        x.setConstant(x.isConstant() && y.isConstant());

        return x;
    }

    @Override
    public Attrib relOp(Attrib x, Token op, Attrib y) throws YAPLException {
        loadValue(x);
        loadValue(y);

        //check operator
        switch(op.kind) {
            case ParserConstants.AND:
                backend.and();
                break;
            case ParserConstants.OR:
                backend.or();
                break;
            case ParserConstants.GREATER:
                backend.isGreater();
                break;
            case ParserConstants.GREATER_EQAUL:
                backend.isGreaterOrEqual();
                break;
            case ParserConstants.LESS:
                backend.isLess();
                break;
            case ParserConstants.LESS_EQUAL:
                backend.isLessOrEqual();
                break;
            default:
                throw new YAPLException(YAPLException.IllegalRelOpType, op);
        }

        //change attr
        x.setType(new BoolType());
        x.setConstant(x.isConstant() && y.isConstant());

        return x;
    }

    @Override
    public Attrib equalOp(Attrib x, Token op, Attrib y) throws YAPLException {
        loadValue(x);
        loadValue(y);

        //check operator
        switch(op.kind) {
            case ParserConstants.EQUAL:
                backend.isEqual();
                break;
            case ParserConstants.NOT_EQUAL:
                //todo not equal?!

                break;
            default:
                throw new YAPLException(YAPLException.IllegalRelOpType, op);
        }

        //change attr
        x.setType(new BoolType());
        x.setConstant(x.isConstant() && y.isConstant());

        return x;
    }

    @Override
    public void enterProc(Symbol proc) throws YAPLException {
        int argumentsNum = ((ProcedureType) proc.getType()).getParameterCount();
        boolean main = proc.getKind() == Symbol.Program; //if procedure is program then its main
        backend.enterProc(proc.getName(), argumentsNum, main);
    }

    @Override
    public void exitProc(Symbol proc) throws YAPLException {
         backend.exitProc(proc.getName() + "_end");
         //If we are at the end of the program we write the code file
         if(proc.getKind() == Symbol.Program){
             try {
                 backend.writeObjectFile(outputStream);
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
    }

    @Override
    public void returnFromProc(Symbol proc, Attrib returnVal) throws YAPLException {
           //exit ?
    }

    @Override
    public Attrib callProc(Symbol proc, Attrib[] args) throws YAPLException {
        if(args.length > 0) {
            for (Attrib a : args) {
                loadValue(a);  //load arguments on top of stack
            }
        }
        backend.callProc(proc.getName());
       // Attrib attr = new AttribImpl(proc) //todo create attribute, need token
        return null;
    }

    @Override
    public void writeString(String string) throws YAPLException {
        if (string.length() > 2) {
            // remove quote characters from string
            String s = string.substring(1, string.length() - 1);
            int addr = backend.allocStringConstant(s);
            backend.writeString(addr);
        }
    }

    @Override
    public void branchIfFalse(Attrib condition, String label) throws YAPLException {
        Boolean value = ((BoolType) condition.getType()).value;
        backend.branchIf(value, label);
       // freeReg(condition);
    }

    @Override
    public void jump(String label) {
        backend.jump(label);
    }
}
