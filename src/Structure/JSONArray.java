package Structure;

import java.util.ArrayList;

/**
 *
 * @author S188219
 */
public class JSONArray implements Structure {
    
    /**
     * The contents of an array
     */
    private final ArrayList<Structure> contents;
    
    /**
     * Creates a new instance of the JSONArray class
     * 
     * @param contents the ArrayList to add
     */
    public JSONArray(ArrayList<Structure> contents) {
        this.contents = contents;
    }
    
    /**
     * retrieves the contents of a JSONArray
     * 
     * @return contents - the array that is returned
     */
    public ArrayList<Structure> getContents() {
        return contents;
    }
    
}
