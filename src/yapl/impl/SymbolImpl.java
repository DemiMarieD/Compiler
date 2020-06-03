package yapl.impl;

import yapl.interfaces.Symbol;
import yapl.lib.Type;

public class SymbolImpl implements Symbol {

    // symbol's kind
    private int kind;
    // symbol's name (identifier)
    private String name;
    // symbol's data type
    private Type type;
    // Specify whether this symbol represents a formal parameter passed by reference.
    private boolean isReference;
    // Specify whether this symbol is a readonly variable.
    private boolean isReadOnly;
    // Specify whether this symbol belongs to a global scope.
    private boolean isGlobal;
    // Set the symbol's address offset.
    private int offset;
    // Link another symbol to this one.
    private Symbol nextSymbol;
    /* Procedure symbol: indicate whether the parser has encountered a Return
       statement within the procedure's body. */
    private boolean returnSeen;

    //Todo where/when to set Symbole name?! Init?!
    public SymbolImpl(String name){
        this.name = name;
        this.returnSeen = false;
    }


    @Override
    public int getKind() {
        return kind;
    }

    @Override
    public String getKindString() {
        switch (kind) {
            case 0:
                return "program";
            case 1:
                return "procedure";
            case 2:
                return "variable";
            case 3:
                return "constant";
            case 4:
                return "typename";
            case 5:
                return "field";
            case 6:
                return "parameter";
            default:
                return null;
        }
    }

    @Override
    public void setKind(int kind) {
        this.kind = kind;
    }

    @Override
    public String getName() {
        return name;
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
    //not needed for symbol checking
    public boolean isReference() {
        return isReference;
    }

    @Override
    //not needed for symbol checking
    public void setReference(boolean isReference) {
        this.isReference = isReference;
    }

    @Override
    //not needed for symbol checking
    public boolean isReadonly() {
        return isReadOnly;
    }

    @Override
    //not needed for symbol checking
    public void setReadonly(boolean isReadonly) {
        this.isReadOnly = isReadonly;
    }

    @Override
    public boolean isGlobal() {
        return isGlobal;
    }

    @Override
    public void setGlobal(boolean isGlobal) {
        this.isGlobal = isGlobal;
    }

    @Override
    //not needed for symbol checking
    public int getOffset() {
        return offset;
    }

    @Override
    //not needed for symbol checking
    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    // todo
    public Symbol getNextSymbol() {
        return nextSymbol;
    }

    @Override
    // todo
    public void setNextSymbol(Symbol symbol) {
        this.nextSymbol = symbol;
    }

    @Override
    // todo
    public boolean getReturnSeen() {
        return returnSeen;
    }

    @Override
    // todo
    public void setReturnSeen(boolean seen) {
        this.returnSeen = seen;
    }
}
