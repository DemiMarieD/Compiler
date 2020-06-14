package yapl.lib;

import yapl.interfaces.Symbol;

import java.util.List;

public class ProcedureType extends Type{

    private List<Symbol> parameters;
    private int parameterCount;
    private Type returnType;
    //todo name

    public ProcedureType(Type returnType, List<Symbol> parameters){
        this.returnType = returnType;
        this.parameters = parameters;
        if (parameters != null) {
            this.parameterCount = parameters.size();
        }else{
            this.parameterCount = 0;
        }
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Symbol> getParameters() {
        return parameters;
    }

    public int getParameterCount() {
        return parameterCount;
    }



    @Override
    public boolean isCompatible(Type type) {
        return this.returnType.isCompatible(type);
    }
}
