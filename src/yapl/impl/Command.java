package yapl.impl;

public class Command {
    private int byteCode;
    private String label;
    private int addressSize;
    private Boolean codeIsSet;

    public Command(int byteCode){
        this.byteCode = byteCode;
        this.codeIsSet = true;
    }

    // If we dont have the address of the label jet, we save the label and address size
    // and when writing to the outStream (in the method writeObjectFile) we get the address and turn it into byte code.
    public Command(String label, int addressSize){
        this.label = label;
        this.codeIsSet = false;
        this.addressSize = addressSize;
    }

    public String getLabel() {
        return label;
    }

    public int getByteCode() {
        return byteCode;
    }

    public Boolean getCodeIsSet() {
        return codeIsSet;
    }

    public int getAddressSize() {
        return addressSize;
    }
}
