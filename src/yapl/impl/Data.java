package yapl.impl;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private int wordSize;
    private int numWords;
    private List<Integer> data = new ArrayList<>();

    // sets the word size (here always 4 byte = one word)
    public Data(int wordSize){
        this.wordSize = wordSize;
    }

    //returns the bytes of the data object
    public List<Integer> getDataBytes() {
        return data;
    }

    public void createEmptyData (int words){
        int byteSize = words*wordSize;
        for(int i=0; i<byteSize; i++){
            data.add(0x00);
        }
    }

    //Turn the string into bytes and make sure that the size is right (= only whole words -> %wordsize = 0)
    public void setStringData(String stringData) {
        String str = stringData;
        for (char c : str.toCharArray()) {
            data.add((int) c);
        }
        data.add(0x00); //I need to add (at least) one null-terminal
        int dataSize = data.size();

        //* -- Set Word Size -- *//
        //one word = 4 bytes
        if (dataSize%wordSize == 0){
            numWords = dataSize/wordSize;
        }else{
            //e.g. datasize is 13 = 3 words and 1 byte -> so we need 4 words
            //     the last word then only has 1 byte (out of 4 filled) so we need to fill the rest with null-terminals
            numWords = (dataSize/wordSize) + 1;
            int emptyBytes = numWords *wordSize - dataSize;
            //fill up empty bytes with null-terminals
            for(int i=0; i<emptyBytes; i++){
                data.add(0x00);
            }
        }
    }

    //returns the number of words / aka the size of the data object
    public int getNumWords() {
        return numWords;
    }
}
