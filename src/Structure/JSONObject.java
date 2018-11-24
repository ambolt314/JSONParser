package Structure;


import java.util.HashMap;
import s188219_jsonparser.InvalidJSONException;
import static Structure.Symbol.Type.STRING;

/**
 *
 * @author S188219
 */
public class JSONObject implements Structure {

    
    public HashMap<String, Structure> keyValuePair;

    //used in recursion. Each JSON object will be contained inside other JSON objects. 
    //It will need to know its own parents so that it can be referenced
    private Structure parent;
    private Structure child;

    public JSONObject(HashMap<String, Structure> keyValuePair) throws InvalidJSONException {
       this.keyValuePair = keyValuePair;
    }
    
    public JSONObject(String keyContents, Structure value) throws InvalidJSONException {

        keyValuePair.put(keyContents, value);
    }
    
    public JSONObject() throws InvalidJSONException {
        keyValuePair.put(null, null);
    }
    
    

    
    
//    public void setParent(JSONObject json) {
//        this.parent = json;
//    }
//    
//    
//    public void setParent() {
//        this.parent = null;
//    }
//
//    
//   
//    public void setChild(JSONObject json) {
//        this.child = json;
//    }
//    
//   
//    public void setChild() {
//        this.child = null;
//    }
//    
//    
//    public Structure getParent() {
//        return parent;
//    }
//    
//    public Structure getChild() {
//        return child;
//    }
    
    public Structure getValueFromKey(String key) {
        Structure objectValue = keyValuePair.get(key);
        return objectValue;
    }

}
