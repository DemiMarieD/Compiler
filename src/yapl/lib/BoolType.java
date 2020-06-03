package yapl.lib;

public class BoolType extends Type {
    public boolean value;

    public BoolType() {
    }

    public BoolType(boolean var1) {
        this.value = var1;
    }

    public String toString() {
        return "Boolean";
    }

    public boolean isCompatible(Type type) {
        return type instanceof BoolType;
    }
}
