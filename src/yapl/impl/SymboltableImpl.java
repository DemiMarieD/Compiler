package yapl.impl;

import yapl.compiler.CodeGenImpl;
import yapl.compiler.Token;
import yapl.interfaces.CompilerError;
import yapl.interfaces.Symbol;
import yapl.lib.Type;
import yapl.lib.YAPLException;

import java.util.List;
import java.util.Stack;

public class SymboltableImpl implements yapl.interfaces.Symboltable {

    /* The symbol table should maintain a stack of
     * scopes, where each scope should be implemented by a data structure that
     * supports efficient searching for symbol names (e.g. a HashMap).
     */
    //The top element of the scope stack represents the current scope
    Stack<Scope> scopeStack;
    Boolean debug = false;

    public SymboltableImpl(){
        this.scopeStack = new Stack<>();
    }

    // --------------- NEW ADDITIONAL METHODS -----------------------------------------

    public Symbol makeSymbol(Token token, int kind, Type type) throws YAPLException {
        String name = token.toString();
        Symbol symbol = new SymbolImpl(name);
        symbol.setKind(kind);
        // Set Type int, bool, const, record, (Array?) are given on: TypeDecl() ConstDecl() VarDecl() FormalParam()
        if(type != null){
            symbol.setType(type);
        }
        try{
            addSymbol(symbol);
            return symbol;
        }catch(YAPLException e){
            int error_code = e.errorNumber();
            switch(error_code){
                case CompilerError.SymbolExists:
                    Symbol excistingSymbole = lookup(name);
                    throw new YAPLException(CompilerError.SymbolExists, excistingSymbole, token);

                case CompilerError.Internal:
                    throw new YAPLException(CompilerError.Internal, symbol, token);
            }
        }
        return null;
    }

    public  void checkSymbolKind(Token token, List<Integer> kinds) throws YAPLException{
        String name = token.toString();
        Symbol symbol = new SymbolImpl("");
        try{
            symbol = lookup(name); //throws exception if name is null
            if(symbol == null){ throw new YAPLException(CompilerError.IdentNotDecl); }
            int symbol_kind = symbol.getKind();
            if(! kinds.contains(symbol_kind))  { throw new YAPLException(CompilerError.SymbolIllegalUse); }

        }catch(YAPLException e){
            int error_code = e.errorNumber();
            switch(error_code){
                case CompilerError.Internal:
                    throw new YAPLException(CompilerError.Internal, token);

                case CompilerError.IdentNotDecl:
                    //error: identifier <name> not declared
                    Symbol notfound_symbol = new SymbolImpl(name);
                    throw new YAPLException(CompilerError.IdentNotDecl, notfound_symbol, token);

                case CompilerError.SymbolIllegalUse:
                    //error: illegal use of <kind> <name>
                    throw new YAPLException(CompilerError.SymbolIllegalUse, symbol, token);
            }
        }
    }

    public  void checkProgramEnd(Token token) throws YAPLException{
        String name = token.toString();
        Symbol symbol = lookup(name);
        int kind = Symbol.Program; //"program"
        Symbol nearestParent = getNearestParentSymbol(kind);
        if(symbol == null){
            Symbol notfound_symbol = new SymbolImpl(name);
            notfound_symbol.setKind(Symbol.Program);
            throw new YAPLException(CompilerError.EndIdentMismatch, notfound_symbol, token, nearestParent.getName());

        }else if(nearestParent == null){
            //error: identifier <name> not declared
            throw new YAPLException(CompilerError.IdentNotDecl, symbol, token);

        }else if(!nearestParent.getName().equals(symbol.getName())){      //when using 'name' its not working
            //error: End <name> does not match Program <name>
            throw new YAPLException(CompilerError.EndIdentMismatch, symbol,  token, nearestParent.getName());
        }
    }

    public  void checkProcedureEnd(Token token) throws YAPLException{
        String name = token.toString();
        Symbol symbol = lookup(name);
        int kind = Symbol.Procedure; //"procedure"
        Symbol nearestParent = getNearestParentSymbol(kind);
        if(symbol == null){
            Symbol notfound_symbol = new SymbolImpl(name);
            notfound_symbol.setKind(Symbol.Procedure);
            throw new YAPLException(CompilerError.EndIdentMismatch, notfound_symbol, token, nearestParent.getName());

        }else if (nearestParent == null){
            //todo what error type is this?!
            throw new YAPLException(CompilerError.IdentNotDecl, symbol, token);

        }else if(!nearestParent.getName().equals(symbol.getName())){   //when using 'name' its not working
            //error: End <name> does not match Procedure <name>
            throw new YAPLException(CompilerError.EndIdentMismatch, symbol, token, nearestParent.getName());
        }

    }


  /* isGlobal   set to <code>true</code> iff the new scope represents a global scope,
        i.e. all symbols declared in the new scope are visible within all following
        procedure declarations and within the main program. */



    //For Program and Procedure I need a ParentSymbol to check identifier at END
    public  void openScopeWithParent(boolean global, Symbol s){
        openScope(global);
        setParentSymbol(s);
    }



    // --------------------------------------------------------


    // an identifier occuring in the YAPL source code always refers to its declaration within the most closely nested scope
    // – that is, Record, Block, Procedure, Program, or the global namespace containing predefined procedures
    @Override
    //pushes a new element to the scope stack
    public void openScope(boolean isGlobal) {
        Scope newScope = new Scope(isGlobal);
        scopeStack.push(newScope);
    }

    @Override
    // pops the current scope off the stack
    public void closeScope() {
        scopeStack.pop();
    }

    @Override
    public void addSymbol(Symbol s) throws YAPLException {
        Scope currentScope = scopeStack.peek();
        String name = s.getName();
        if( name == null){
            //YAPLException (Internal) if the new symbol's name is null.
            throw new YAPLException(CompilerError.Internal);
        }

        Symbol excistingSymbole = currentScope.getSymbole(name);
        if(excistingSymbole != null) {
            //todo if global check the whole scope?!? if global at to all scopes?!?

            //YAPLException (SymbolExists) if a symbol of the same name already exists in the current scope
            throw new YAPLException(CompilerError.SymbolExists, excistingSymbole);

        }
        //if we are in a global scope the Symbols will be global
        s.setGlobal(currentScope.isGlobal());
        // Add a new SymbolImpl to the current scope.
        currentScope.addSymbole(s);
        // Sets the global symbol property of the new symbol according to current scope.
        s.setGlobal(currentScope.isGlobal());
    }

    @Override
    // Lookup a symbol in the stack of scopes.
    // Symbols in an inner scope hide symbols of the same name in an outer scope.
    public Symbol lookup(String name) throws YAPLException {
        if(name == null){
            //YAPLException (Internal) if the name is null.
            throw new YAPLException(CompilerError.Internal);
        }

        Scope currentScope = scopeStack.peek();
        Symbol symbol_currentScope = currentScope.getSymbole(name);

        if(symbol_currentScope == null){
            // check global symbols
            for(int i = scopeStack.size() - 1; i >= 0; --i) {
                Scope scope = scopeStack.get(i);
                Symbol s = scope.getSymbole(name);
                if( s!= null ){     //todo && s.isGlobal() ?? put then I can't access parameter in block?!
                    return s;
                }
            }
            // return null if a symbol of the given name does not exist.
            return null;

        } else {
            return symbol_currentScope;
        }
    }


    /*
     *  Some scopes need to be linked to a <em>parent symbol</em> (e.g. a procedure
     * body's scope to its procedure symbol) such that the nearest parent symbol
     * within the stack of scopes can be found within the current scope (e.g. when
     * parsing a Return statement in a procedure body, the procedure symbol needs to
     * be found).
     * */
    @Override
    // Set the parent symbol of the current scope.
    // If a parent symbol has already be set, it will be overwritten.
    public void setParentSymbol(Symbol sym) {
        Scope currentScope = scopeStack.peek();
        currentScope.setParentSymbole(sym);
    }

    @Override
    // Return the nearest parent symbol of the given kind in the stack of scopes.
    public Symbol getNearestParentSymbol(int kind) {
        for(int i = scopeStack.size() - 1; i >= 0; --i) {
            Scope scope = scopeStack.get(i);
            Symbol parentSymbol = scope.getParentSymbole();
            if (parentSymbol != null && parentSymbol.getKind() == kind) {
                return parentSymbol;
            }
        }

        return null;
    }

    @Override
    // Enable/disable debugging output for symbol table operations.
    public void setDebug(boolean on) {
        this.debug = on;
    }
}
