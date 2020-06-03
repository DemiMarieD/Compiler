package yapl.impl;

import yapl.interfaces.BackendBinSM;
import yapl.interfaces.MemoryRegion;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackendMJ implements BackendBinSM {

    //start of the main method
    Integer startPC = 0;

    //String = label, Integer = Address of the label -> needed for jumps / backpatching
    Map<String, Integer> labels = new HashMap<>();

    //Each Command in the list (code) represents one byte in the output-file (in the code section)
    //except for Commands initialized with label (they are normally addresses and have two bytes)
    List<Command> code = new ArrayList<>();
    //keeps track on how many bytes have been "written" in the code list.
    Integer currentCodeAddress = 0;


    // Each Data object in the list (staticData) represents one data in the output-file (in the data section)
    // the byte length (in words) of a data (e.g. String) is saved in the data object.
    List<Data> staticData = new ArrayList<>();
    // keeps track of how many words (= 4 bytes) have been written to the static Data section
    Integer currentDataAddress = 0;

    //todo understand
    Integer currentStackAddress = 0; //changed on enter and needed for allocStack
    Integer framePointer = 0; //todo never changes (needed for offset calc)


    private byte[] intToByteArray(int integer, int arraySize){
        if(integer == 0){
            byte[] emptyArray = new byte[arraySize];
            for(int i = 0; i < arraySize; i++)
                emptyArray[i] = 0x00;
            return emptyArray;
        }
        else
            return ByteBuffer.allocate(arraySize).putInt(integer).array();
    }


    private byte[] shortToByteArray(short value, int arraySize){
        if(value == 0){
            byte[] emptyArray = new byte[arraySize];
            for(int i = 0; i < arraySize; i++)
                emptyArray[i] = 0x00;
            return emptyArray;
        }
        return ByteBuffer.allocate(arraySize).putShort(value).array();
    }

    @Override
    public int wordSize() {
        return 4;
    }

    @Override
    public int boolValue(boolean value) {
        if(value){
            return 1;
        }else{
            return 0;
        }
    }

    @Override
    public void assignLabel(String label) {
        labels.put(label, currentCodeAddress);
    }

    @Override
    public void writeObjectFile(OutputStream outStream) throws IOException {
        /* --- WRITE HEADER --- */

        // Magic constant (MJ)
        outStream.write(0x4D);
        outStream.write(0x4A);

        // code size (use currentCodeAddress because the code.size includes some Commands (for addresses)
                                        // are 'worth' more then one byte.
        outStream.write(intToByteArray(currentCodeAddress, 4));

        // data size (dont use static data (word) size because some data might be inputted there later and is not jet in the static data)
        outStream.write(intToByteArray(currentDataAddress, 4));

        // program entry point
        outStream.write(intToByteArray(startPC, 4));

        /* --- WRITE CODE --- */
        for (Command command : code){
            if(command.getCodeIsSet()){
                outStream.write(command.getByteCode());
            }else {
                int addr = labels.get(command.getLabel());
                if(command.getAddressSize() == 2){
                    outStream.write(shortToByteArray((short)addr, 2));
                }else {
                    outStream.write(intToByteArray(addr, command.getAddressSize()));
                }
            }
        }

        /* --- WRITE DATA --- */
        for (Data data : staticData){
            for (Integer b : data.getDataBytes()){
                outStream.write(b);
            }
        }

        // close
        outStream.flush();
        outStream.close();
    }


    @Override
    public int allocStaticData(int words) {
        int addr = currentDataAddress;

        //empty data
        Data data = new Data(wordSize());
        data.createEmptyData(words);
        staticData.add(data);

        currentDataAddress = currentDataAddress + words;
        return addr;
    }

    @Override
    public int allocStringConstant(String string) {
        int addr = currentDataAddress;
        Data data = new Data(wordSize());
        data.setStringData(string);
        staticData.add(data);
        currentDataAddress = currentDataAddress + data.getNumWords();
        return addr;
    }

    @Override
    public int allocStack(int words) {
        int addr =  currentStackAddress;
        //needed??
        currentStackAddress = currentStackAddress+words;
        return addr;
    }

    @Override
    public void allocHeap(int words) {
        //new
        code.add(new Command(0x1F));
        //s16 size
        byte[] size = shortToByteArray((short)words, 2);
        code.add(new Command((int) size[0]));
        code.add(new Command((int) size[1]));
        currentCodeAddress = currentCodeAddress+3;
    }

    //each call creates one dimension/column with given lenght (dim) ?!??
    @Override
    public void storeArrayDim(int dim) {
        //todo
        //pop size from stack for dim
        //store in array descriptor (?) -> just push again?
    }

    //The lengths of the array dimensions must have been
    //    stored in an auxiliary array descriptor by code emitted by storeArrayDim
    //desired number of array dimensions is defined by the number of prior calls of storeArrayDim
    @Override
    public void allocArray() {
        //newarray
        //allocate array with t0 elements of given type on the heap (type = 0: boolean, type ≠ 0: integer)
        code.add(new Command(0x20));
        //s8 type
        code.add(new Command(0x01)); //type int (function description)
        currentCodeAddress = currentCodeAddress+2;
    }

    @Override
    public void loadConst(int value) {
        switch (value) {
            case 0:
                code.add(new Command( 0x0F));
                currentCodeAddress = currentCodeAddress+1;
                break;
            case 1:
                code.add(new Command( 0x10));
                currentCodeAddress = currentCodeAddress+1;
                break;
            case 2:
                code.add(new Command( 0x11));
                currentCodeAddress = currentCodeAddress+1;
                break;
            case 3:
                code.add(new Command( 0x12));
                currentCodeAddress = currentCodeAddress+1;
                break;
            case 4:
                code.add(new Command( 0x13));
                currentCodeAddress = currentCodeAddress+1;
                break;
            case 5:
                code.add(new Command( 0x14));
                currentCodeAddress = currentCodeAddress+1;
                break;
            default:
                //const
                code.add(new Command( 0x16));
                //value s32 = 4 byte
                byte[] bytes = intToByteArray(value, 4);
                for(int i=0; i < bytes.length; i++){
                    code.add(new Command((int) bytes[i]));
                }
                currentCodeAddress = currentCodeAddress+5;
        }

    }

    @Override
    public void loadWord(MemoryRegion region, int offset) {

        if(region == MemoryRegion.HEAP){
            //getfield
            code.add(new Command(0x0D));
            //s16 offset
            byte[] offsets = shortToByteArray((short)offset, 2);
            code.add(new Command((int) offsets[0]));
            code.add(new Command((int) offsets[1]));
            currentCodeAddress = currentCodeAddress+3;


        }else if(region == MemoryRegion.STATIC){
            //getstatic
            code.add(new Command(0x0B));
            //s16 offset
            byte[] offsets = shortToByteArray((short)offset, 2);
            code.add(new Command((int) offsets[0]));
            code.add(new Command((int) offsets[1]));
            currentCodeAddress = currentCodeAddress+3;

        }else if(region == MemoryRegion.STACK) {
            switch (offset) {
                case 0:
                    code.add(new Command(0x02));
                    currentCodeAddress = currentCodeAddress + 1;
                    break;
                case 1:
                    code.add(new Command(0x03));
                    currentCodeAddress = currentCodeAddress + 1;
                    break;
                case 2:
                    code.add(new Command(0x04));
                    currentCodeAddress = currentCodeAddress + 1;
                    break;
                case 3:
                    code.add(new Command(0x05));
                    currentCodeAddress = currentCodeAddress + 1;
                    break;
                default:
                    //load
                    code.add(new Command(0x01));
                    //s8 offset
                    code.add(new Command(offset));
                    currentCodeAddress = currentCodeAddress + 2;
            }
        }
    }

    @Override
    public void storeWord(MemoryRegion region, int offset) {
        if(region == MemoryRegion.HEAP){
            //putfield
            code.add(new Command(0x0E));
            //s16 offset
            byte[] offsets = shortToByteArray((short)offset, 2);
            code.add(new Command((int) offsets[0]));
            code.add(new Command((int) offsets[1]));
            currentCodeAddress = currentCodeAddress+3;


        }else if(region == MemoryRegion.STATIC){
            //store in data segment where we already allocated room.
            //putstatic  (store global variable)
            code.add(new Command(0x0C));
            //s16 offset
            byte[] offsets = shortToByteArray((short)offset, 2);
            code.add(new Command((int) offsets[0]));
            code.add(new Command((int) offsets[1]));

            currentCodeAddress = currentCodeAddress+3;

        }else if(region == MemoryRegion.STACK) {
            switch (offset) {
                case 0:
                    code.add(new Command(0x07));
                    currentCodeAddress = currentCodeAddress + 1;
                    break;
                case 1:
                    code.add(new Command(0x08));
                    currentCodeAddress = currentCodeAddress + 1;
                    break;
                case 2:
                    code.add(new Command(0x09));
                    currentCodeAddress = currentCodeAddress + 1;
                    break;
                case 3:
                    code.add(new Command(0x0A));
                    currentCodeAddress = currentCodeAddress + 1;
                    break;
                default:
                    //store
                    code.add(new Command(0x06));
                    //s8 offset
                    code.add(new Command(offset));
                    currentCodeAddress = currentCodeAddress + 2;
            }
        }
    }

    @Override
    public void loadArrayElement() {
        //aload
        code.add(new Command(0x21));
        currentCodeAddress = currentCodeAddress+1;
        //Runtime effect: i = pop(), a = pop(), push(a[i])
        //load element of integer array at heap address t1 and index t0

        //framePointer = framePointer+1; //with +1 compiles but wrong output
    }

    @Override
    public void storeArrayElement() {
        //astore
        code.add(new Command(0x22));
        currentCodeAddress = currentCodeAddress+1;
        //Runtime effect: v = pop(), i = pop(), a = pop(), a[i] = v
        //store value t0 in integer a
        // Array at heap address t2 and index t1
        //framePointer = framePointer-1; //with -1 compiles but wrong output
    }

    @Override
    public void arrayLength() {
        //arraylenght
        code.add(new Command(0x25));
        currentCodeAddress = currentCodeAddress+1;
    }

    @Override
    public void writeInteger() {
        //const0
        code.add(new Command(0x0F));

        //print
        code.add(new Command(0x33));
        currentCodeAddress = currentCodeAddress + 2;
    }

    @Override
    public void writeString(int addr) {
        //sprint
        code.add(new Command(0x37));

        //offset s16 = 2 bytes -> addr = address of String
        byte[] offset = shortToByteArray((short)addr, 2);
        code.add(new Command((int) offset[0]));
        code.add(new Command((int) offset[1]));

        currentCodeAddress = currentCodeAddress+3;
    }

    @Override
    public void neg() {
        code.add(new Command(0x1C));
        currentCodeAddress = currentCodeAddress+1;
    }

    @Override
    public void add() {
        code.add(new Command(0x17));
        currentCodeAddress = currentCodeAddress+1;
    }

    @Override
    public void sub() {
        code.add(new Command(0x18));
        currentCodeAddress = currentCodeAddress+1;
    }

    @Override
    public void mul() {
        code.add(new Command(0x19));
        currentCodeAddress = currentCodeAddress+1;
    }

    @Override
    public void div() {
        code.add(new Command(0x1A));
        currentCodeAddress = currentCodeAddress+1;
    }

    @Override
    public void mod() {
        //rem
        code.add(new Command(0x1B));
        currentCodeAddress = currentCodeAddress+1;
    }

    @Override
    public void and() {
        add();
        loadConst(2);
        isEqual();
    }

    @Override
    public void or() {
        add();
        loadConst(1);
        isGreaterOrEqual();
    }

    @Override
    public void isEqual() {
        //jeq
        code.add(new Command(0x28));
        currentCodeAddress = currentCodeAddress+1;

        writeBooleanResult();
    }

    @Override
    public void isLess() {
        //jlt
        code.add(new Command(0x2A));
        currentCodeAddress = currentCodeAddress+1;

        writeBooleanResult();
    }

    @Override
    public void isLessOrEqual() {
        //jle
        code.add(new Command(0x2B));
        currentCodeAddress = currentCodeAddress+1;

        writeBooleanResult();
    }

    @Override
    public void isGreater() {
        //jgt
        code.add(new Command(0x2C));
        currentCodeAddress = currentCodeAddress+1;

        writeBooleanResult();
    }

    @Override
    public void isGreaterOrEqual() {
        //jge
        code.add(new Command(0x2D));
        currentCodeAddress = currentCodeAddress+1;

        writeBooleanResult();
    }


    public void writeBooleanResult(){
        /* Example
         * 0. const3
         * 1. const3
         * 2 3 4. jeq 9
         * 5. const0        (false)
         * 6 7 8. jmp 10
         * 9. const1        (true)
         */
        //e.g. current address  = 3
        /* Create address */
        //if true
        int addr_true = currentCodeAddress+6; //3+7 = 9
        byte[] offset = shortToByteArray((short)addr_true, 2);
        code.add(new Command((int) offset[0]));
        code.add(new Command((int) offset[1]));
        currentCodeAddress = currentCodeAddress+2; //current = 5

        /* Create false */
        //const0 = false
        code.add(new Command(0x0F));
        currentCodeAddress = currentCodeAddress+1; //current = 6

        /* Create jmp */
        //jmp
        code.add(new Command(0x27));
        //addr s16
        int addr = currentCodeAddress+4; //6+4 = 10
        byte[] offset_2 = shortToByteArray((short)addr, 2);
        code.add(new Command((int) offset_2[0]));
        code.add(new Command((int) offset_2[1]));
        currentCodeAddress = currentCodeAddress+3;

        /* Create true */
        //const1 = true
        code.add(new Command(0x10));
        currentCodeAddress = currentCodeAddress+1;
    }

    //special case if you want to check if (something is true/false) {}
    @Override
    public void branchIf(boolean value, String label) {
        //this if will compare the top element of the stack with
        //this next const
        if(value){
            //const1 = true
            code.add(new Command(0x10));
        }else{
            //const0 = false
            code.add(new Command(0x0F));
        }
        //jeq
        code.add(new Command(0x28));
        //addr s16
        code.add(new Command(label, 2));

        currentCodeAddress = currentCodeAddress+4;
    }

    @Override
    public void jump(String label) {
        //jmp
        code.add(new Command(0x27));
        //addr s16
        code.add(new Command(label, 2));

        currentCodeAddress = currentCodeAddress+3;
    }

    @Override
    public void callProc(String label) {
        //call
        code.add(new Command(0x2E));

        //addr s16
        code.add(new Command(label, 2));


        currentCodeAddress = currentCodeAddress+3;
    }

    @Override
    public void enterProc(String label, int nParams, boolean main) {
        assignLabel(label);

        if(main){
            startPC = currentCodeAddress;
        }

        //enter
        code.add(new Command(0x30));

        //nparams s8 = 1 byte >nParams + local variables
        code.add(new Command(nParams));

        //framesize s8 = 1 byte  parameter + local vars (right now there can be anything here and it will work)
        //todo +1 not hardcoded -> count local vars!?!
        code.add(new Command(nParams+1));

        currentCodeAddress = currentCodeAddress + 3;
        //so you dont override the parameter?!
        currentStackAddress = nParams;
    }

    @Override
    public void exitProc(String label) {
        if(!labels.containsKey(label)) {
            assignLabel(label);
        }
        //exit
        code.add(new Command(0x31));
        //return
        code.add(new Command(0x2F));
        currentCodeAddress = currentCodeAddress + 2;

    }

    @Override
    public int paramOffset(int index) {
        //I want to load local[index] and index = fp + offset -> return offset
        //todo framePointer never changes?!
        return index-framePointer;
    }
}
