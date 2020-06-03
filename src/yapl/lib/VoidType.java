package yapl.lib;

import java.util.List;

public class
VoidType extends Type{

    public VoidType(){
    }

    @Override
    public boolean isCompatible(Type var1) {
        return false;
    }
}
