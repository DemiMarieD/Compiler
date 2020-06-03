package yapl.impl;

import yapl.compiler.Token;
import yapl.interfaces.Attrib;
import yapl.interfaces.CompilerError;
import yapl.interfaces.Symbol;
import yapl.lib.Type;
import yapl.lib.YAPLException;

public class AttribImpl implements Attrib {
    private byte kind;
    private Type type;
    private Boolean isConstant;
    private Boolean isReadonly;
    private Boolean isGlobal;
    private int offset;
    private byte register;

    private String name;
    private Token token;
    // private simple.interfaces.Attrib index = null;
    // todo add token ?!

    public AttribImpl(Symbol symbol, Token t) throws YAPLException{
        switch(symbol.getKind()) {
            case Symbol.Variable:
                this.kind = Attrib.Constant;
                this.isConstant = false;
                break;
            case Symbol.Constant:
                this.kind = Attrib.RegAddress;
                this.isConstant = true;
                break;
            case Symbol.Typename:       // for record
                this.kind = Attrib.Constant;
                this.isConstant = false;
                break;
            default:
               // throw new YAPLException(CompilerError.Internal, t);
        }
        this.type = symbol.getType();
        this.isGlobal = symbol.isGlobal();
        this.offset = symbol.getOffset();
        this.name = symbol.getName();
        this.token = t;
    }


    public AttribImpl(byte kind, Type type, Token t) {
        this.kind = kind;
        this.type = type;
        this.token = t;
    }

    public String getName() {
        return name;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    @Override
    public byte getKind() {
        return kind;
    }

    @Override
    public void setKind(byte kind) {
        this.kind = kind;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean isConstant() {
        return isConstant;
    }

    @Override
    public void setConstant(boolean isConstant) {
        this.isConstant = isConstant;
    }

    @Override
    public boolean isReadonly() {
        return isReadonly;
    }

    @Override
    public void setReadonly(boolean isReadonly) {
        this.isReadonly = isReadonly;
    }

    @Override
    public boolean getIsGlobal() {
        return isGlobal;
    }

    @Override
    public void setIsGlobal(boolean isGlobal) {
        this.isGlobal = isGlobal;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public byte getRegister() {
        return register;
    }

    @Override
    public void setRegister(byte register) {
        this.register = register;
    }
}
