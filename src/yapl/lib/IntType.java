package yapl.lib;

public class IntType extends Type{
    @Override
    public boolean isCompatible(Type type) {
        return type instanceof IntType;
    }

    public int value;

    public IntType() {
    }

    public IntType(int var1) {
        this.value = var1;
    }

    public String toString() {
        return "Integer";
    }

}
