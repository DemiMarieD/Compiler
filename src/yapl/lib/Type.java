package yapl.lib;

public abstract class Type {
    /*  The Java classes belonging to the yapl.lib package and referenced
        by the interfaces need to be implemented by yourself, though.
        In particular, the yapl.lib.Type class can be implemented as an empty class for now.
     */
    protected boolean readonly = false;

    public Type() {
    }

    public abstract boolean isCompatible(Type var1);

    public boolean isReadonly() {
        return this.readonly;
    }

    public void setReadonly(boolean var1) {
        this.readonly = var1;
    }

    public String toString() {
        return "<unknown type>";
    }


}
