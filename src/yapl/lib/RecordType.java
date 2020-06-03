package yapl.lib;

import yapl.interfaces.Symbol;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordType extends Type{
    private String record_name;
    private List<RecordField> fields;


    public RecordType (String name){
        this.record_name = name;
    }
    public void setFields(List<Symbol> symbols) {

        fields = new ArrayList<>();
        for (Symbol s : symbols){
            fields.add(new RecordField(s.getName(), s.getType()));
        }

    }

    @Override
    public boolean isCompatible(Type type) {
        if( !(type instanceof RecordType) ) {return false;}
        RecordType otherRecord = (RecordType) type;     //List2
        List<RecordField> otherFields = otherRecord.getFields(); // item , next

        //must have same number of fields
        if( this.fields.size() != otherFields.size()){ return false; }      // 2 = 2
        for (int i = 0; i < this.fields.size(); i++){

            RecordField field = this.fields.get(i); //int item / List next
            RecordField otherfield = otherFields.get(i); //int item     / List next


            if(! field.getName().equals(otherfield.getName()) ){return false;} // item = item / next = next

            if((field.getType() instanceof RecordType) && (otherfield.getType() instanceof RecordType)){    // types = Record type
                RecordType record = (RecordType) field.getType();
                String name = record.getRecord_name();
                RecordType record2= (RecordType) otherfield.getType();
                String name2 = record2.getRecord_name();
                if(! name.equals(name2)){   // list = list
                    return false;
                }

            }else{
                if(! field.getType().isCompatible(otherfield.getType())){return false; }
                // int.compatible(int) = true

            }

        }


        return true;
    }




    public Type getFieldType(String fieldName){
        for (RecordField f: fields){
            if(f.getName().equals(fieldName)){
                return f.getType();
            }
        }
        return null;
    }

    public List<RecordField> getFields() {
        return fields;
    }

    public String getRecord_name() {
        return record_name;
    }


}
