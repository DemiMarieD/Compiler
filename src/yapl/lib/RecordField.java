package yapl.lib;

public class RecordField {
    private String name;
    private Type type;

    public RecordField(String name, Type type){
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
