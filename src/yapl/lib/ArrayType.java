package yapl.lib;

public class ArrayType extends Type{
    public int len = -1;
    public Type base = null;        // element data type elem(A)
    private int dim = 1;

    private ArrayType subarray;
    

        /// int [][][][]
    // int[i] = bool[][]
    public ArrayType(Type base, int dim) {
        this.base = base; //int
        this.dim = dim; //4
        if(dim > 1){
            this.subarray = new ArrayType(base, dim-1);  // int[i] = subarray = int[][][]
        }else if(dim == 1){
            this.subarray = new ArrayType(base, 0);
        }
        this.len = -1;
    }

   /* //for subarray with array dim = 1
    public ArrayType(int var1) {
        this.len = var1;
    }

/*
    public ArrayType(Type var1, int var2) {
        this.base = var1;
        this.len = var2;
    } */

   

    public boolean isCompatible(Type type) {
        if (!(type instanceof ArrayType)) {
            return false;
        } else {
            ArrayType other_array = (ArrayType) type;
            return this.base.isCompatible(other_array.base) && (other_array.dim == this.dim);
            //(this.len < 0 || other_array.len < 0 || other_array.len == this.len)
        }
    }

    public int getDim(){
        return dim;
    }

    public Type getBase() {
        return base;
    }

    public ArrayType getSubarray() {
        return subarray;
    }

    public boolean indexIsValid(int var1) {
        return var1 >= 0 && var1 < this.len;
    }
    
    public void setReadonly(boolean var1) {
        this.readonly = var1;
        this.base.setReadonly(var1);
    }

    public String toString() {
        StringBuffer var1 = new StringBuffer();
        var1.append(this.base.toString());
        var1.append('[');
        if (this.len >= 0) {
            var1.append(this.len);
        }

        var1.append(']');
        return var1.toString();
    }


}
