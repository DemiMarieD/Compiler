package yapl.impl;

import yapl.interfaces.Symbol;

import java.util.HashMap;
import java.util.Scanner;

public class Scope {

    private HashMap<String, Symbol> symbols;
    private boolean isGlobal;
    private Symbol parentSymbole;

    public Scope(boolean global){
        this.symbols = new HashMap<>();
        this.isGlobal = global;
    }

    public void addSymbole(Symbol symbol){
        this.symbols.put(symbol.getName(), symbol);
    }


    public Symbol getSymbole(String name){
        if(this.symbols.containsKey(name)){
            return symbols.get(name);
        }else{
            // return null if a symbol of the given name does not exist.
            return null;
        }
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public Symbol getParentSymbole() {
        return parentSymbole;
    }

    public void setParentSymbole(Symbol parentSymbole) {
        this.parentSymbole = parentSymbole;
    }





}
