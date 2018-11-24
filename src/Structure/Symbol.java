package Structure;

/**
 *
 * @author S188219
 */
public class Symbol implements Structure {

    /**
     * Possible, valid symbol types in JSON
     */
    public enum Type {
        /**
         * Represents an open JSON object bracket: {
         */
        OBJ_OPEN,
        /**
         * Represents a closing JSON object bracket: }
         */
        OBJ_CLOSE,
        /**
         * Represents an open JSON array bracket: [
         */
        ARRAY_OPEN,
        /**
         * Represents a closing JSON array bracket: ]
         */
        ARRAY_CLOSE,
        /**
         * A string is a sequence of zero or more Unicode characters, wrapped in double quotes, using backslash escapes
         */
        STRING,
        /**
         * A number is very much like a C or Java number, except that the octal and hexadecimal formats are not used
         */
        NUMBER,
        /**
         * A value that is either true or false
         */
        BOOLEAN,
        /**
         * An unassigned or empty value
         */
        NULL,
        /**
         * Used in JSON to separate keys and values
         */
        COLON,
        /**
         * Used in JSON to separate elements in an array and key-value pairs
         */
        COMMA
    };

    public Type type;
    public String value;

    /**
     * Creates a new instance of Symbol class with a given type and value
     *
     * @param type the new symbol's type
     * @param value the new symbol's value
     */
    public Symbol(Symbol.Type type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Creates a new instance of Symbol class with a given type and value. In
     * this constructor, the value is inferred from the type.
     *
     * @param type the new symbol's type
     */
    public Symbol(Symbol.Type type) {
        this.type = type;

        switch (this.type) {
            case OBJ_OPEN:
                this.value = "{";
                break;
            case OBJ_CLOSE:
                this.value = "}";
                break;
            case ARRAY_OPEN:
                this.value = "[";
                break;
            case ARRAY_CLOSE:
                this.value = "]";
                break;
            case NULL:
                this.value = "null";
                break;
            case COLON:
                this.value = ":";
                break;
            case COMMA:
                this.value = ",";
                break;
            default:
                this.value = null;
        }
    }

    /**
     * Creates a new instance of Symbol class with a null type and value
     */
    public Symbol() {
        this.type = null;
        this.value = null;
    }

}
