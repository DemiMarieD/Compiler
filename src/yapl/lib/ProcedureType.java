package yapl.lib;

import java.util.List;

public class ProcedureType extends Type{

    private List<Type> parameters;
    private int parameterCount;
    private Type returnType;
    //todo name

    public ProcedureType(Type returnType, List<Type> parameters){
        this.returnType = returnType;
        this.parameters = parameters;
        this.parameterCount = parameters.size();
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Type> getParameters() {
        return parameters;
    }

    @Override
    public boolean isCompatible(Type type) {
        return this.returnType.isCompatible(type);
    }
}
