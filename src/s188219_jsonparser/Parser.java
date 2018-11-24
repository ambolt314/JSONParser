package s188219_jsonparser;

import static Lexical_Analyser.S188219_Lexer.lexer;
import Structure.JSONArray;
import Structure.JSONObject;
import Structure.Structure;
import java.util.ArrayList;
import Structure.Symbol;
import static Structure.Symbol.Type.*;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:</p>
 *
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.</p>
 *
 * <p>
 * The Software shall be used for Good, not Evil.</p>
 *
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE. </p>
 *
 * @author s188219
 */
public class Parser {
        

    public JSONObject parseObject(ArrayList<Symbol> parserInput) throws InvalidJSONException, IOException {
        HashMap<String, Structure> objectContents = new HashMap<>();

        String key = null;
        Structure value = null;

        int currentIndex = 1; //converts a type to correct index of array

        if (parserInput.get(0).type == OBJ_OPEN) {
            do {

                Symbol firstSymbol = parserInput.get(currentIndex);

                if (firstSymbol.type == OBJ_CLOSE) {
                    break;
                }

                Symbol secondSymbol = parserInput.get(currentIndex + 1);

                Symbol thirdSymbol = new Symbol();
                if (currentIndex + 2 < parserInput.size()) {
                    thirdSymbol = parserInput.get(currentIndex + 2);
                }
                else {
                    break;
                }

                if (firstSymbol.type == STRING && secondSymbol.type == COLON) {
                    key = firstSymbol.value;

                    switch (thirdSymbol.type) {
                        case STRING:
                        case NUMBER:
                        case BOOLEAN:
                        case NULL: {
                            value = (Symbol) thirdSymbol;
                            break;
                        }

                        case OBJ_OPEN: {
                            int indexOfClosingBracket = indexOfNextType(currentIndex + 2, parserInput, OBJ_CLOSE);
                            ArrayList<Symbol> childInput = new ArrayList<>(parserInput.subList(currentIndex + 2, indexOfClosingBracket + 1));

                            value = parseObject(childInput);
                            break;
                        }

                        case ARRAY_OPEN: {
                            int indexOfClosingBracket = indexOfNextType(currentIndex + 2, parserInput, ARRAY_CLOSE);
                            ArrayList<Symbol> childInput = new ArrayList<>(parserInput.subList(currentIndex + 2, indexOfClosingBracket + 1));

                            

                            value = parseArray(childInput);
                            break;
                        }

                        case OBJ_CLOSE:
                            break;
                    }

                    objectContents.put(key, value);

                }

                currentIndex += 4;
            } while (currentIndex < parserInput.size());
        } else {
            throw new InvalidJSONException("Parser error. Expected {. Got " + parserInput.get(0).type);
        }

        JSONObject output = new JSONObject(objectContents);
        return output;
    }

    public JSONArray parseArray(ArrayList<Symbol> parserInput) throws InvalidJSONException, IOException {
        ArrayList<Structure> arrayContents = new ArrayList<>();

        int currentIndex = 1;

        Structure value;

        arrayParsingLoop:
        if (parserInput.get(0).type == ARRAY_OPEN) {
            do {

                Symbol firstSymbol = parserInput.get(currentIndex);
                Symbol secondSymbol = parserInput.get(currentIndex + 1);

                if (firstSymbol.type == ARRAY_CLOSE) {
                    break;
                }

                if (secondSymbol.type == COMMA || secondSymbol.type == ARRAY_CLOSE) {
                    switch (firstSymbol.type) {
                        case STRING:
                        case NUMBER:
                        case BOOLEAN:
                        case NULL: {
                            value = (Symbol) firstSymbol;
                            arrayContents.add(value);
                            break;
                        }

                        case OBJ_OPEN: {
                            int indexOfClosingBracket = indexOfNextType(currentIndex, parserInput, OBJ_CLOSE);
                            ArrayList<Symbol> childInput = new ArrayList<Symbol>(parserInput.subList(currentIndex, indexOfClosingBracket + 1));

                            for (Symbol symbol : childInput) {
                                System.out.println("This is a symbol with a type of " + symbol.type + " and a value of " + symbol.value);
                            }

                            value = parseObject(childInput);
                            arrayContents.add(value);
                            break;
                        }

                        case ARRAY_OPEN: {
                            int indexOfClosingBracket = indexOfNextType(currentIndex, parserInput, ARRAY_CLOSE);
                            ArrayList<Symbol> childInput = new ArrayList<Symbol>(parserInput.subList(currentIndex, indexOfClosingBracket + 1));
                            value = parseObject(childInput);
                            arrayContents.add(value);
                            break;
                        }

                        case ARRAY_CLOSE:
                            break;
                    }
                }
                currentIndex += 2;
            } while (currentIndex < parserInput.size());
        }
        JSONArray output = new JSONArray(arrayContents);
        
        return output;
    }

    /**
     * For a given index in an array, this method returns the index of the next
     * symbol of a given type
     *
     * @param currentIndex the index of the current item
     * @param als the ArrayList
     * @param type
     * @return int
     */
    public int indexOfNextType(int currentIndex, ArrayList<Symbol> als, Symbol.Type type) {
        int index = currentIndex;

        Symbol currentSymbol = als.get(index);

        do {

            currentSymbol = als.get(index);

            index++;

            if (index >= als.size()) {
                index = als.size();
                break;
            }

        } while (currentSymbol.type != type);

        index--;
        return index;
    }

    /**
     * Extracts the value from a NUMBER symbol as a number
     *
     * @param symbol input symbol
     * @return Number - the symbol's value as a number
     * @throws InvalidJSONException
     */
    public Number parseNumber(Symbol symbol) throws InvalidJSONException {

        if (symbol.type != NUMBER) {
            throw new InvalidJSONException("Expected symbol of type NUMBER. Got " + symbol.type);
        }

        Symbol.Type type = symbol.type;
        String value = symbol.value;
        Number output = null;

        if (type.equals(NUMBER)) {

            if ((value.contains("e") || value.contains("E")) && (value.contains("+") || value.contains("-"))) {
                try {
                    output = Float.parseFloat(value);
                } catch (NumberFormatException e) {
                    output = Double.parseDouble(value);
                }
            } else if (value.contains(".")) {
                output = Double.parseDouble(value);
            } //if none of these, number must be an integer
            else {
                try {
                    output = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    output = Long.parseLong(value);
                }
            }

        }
        return output;
    }

    /**
     * Extracts the value from a STRING symbol as a String
     *
     * @param symbol input symbol
     * @return String - the symbol's value as a string
     * @throws InvalidJSONException
     */
    public String parseString(Symbol symbol) throws InvalidJSONException {
        if (symbol.type != STRING) {
            throw new InvalidJSONException("Expected symbol of type STRING. Got " + symbol.type);
        }
        String value = symbol.value;
        return value;
    }

    /**
     * Extracts the value from a BOOLEAN symbol as a Boolean
     *
     * @param symbol input symbol
     * @return Boolean - the symbol's value as a Boolean
     * @throws InvalidJSONException
     */
    public boolean parseBoolean(Symbol symbol) throws InvalidJSONException {
        if (symbol.type != BOOLEAN) {
            throw new InvalidJSONException("Expected symbol of type BOOLEAN. Got " + symbol.type);
        }

        String value = symbol.value;
        boolean output = false;

        switch (symbol.type) {
            case BOOLEAN: {
                if (value.equals("true")) {
                    output = true;
                } else if (value.equals("false")) {
                    output = false;
                }
                break;
            }
            default: {
                break;
            }
        }

        return output;
    }

    /**
     * Extracts the value from a NULL symbol as a null
     *
     * @param symbol input symbol
     * @return Void - returns a null given a symbol of type null
     * @throws InvalidJSONException
     */
    public Void parseNull(Symbol symbol) throws InvalidJSONException {
        if (symbol.type != NULL) {
            throw new InvalidJSONException("Expected symbol of type NULL. Got " + symbol.type);
        }
        return null;
    }
    
    /**
     * Extracts any error messages. Used to determine validity of JSON.
     * @return message. For errors, this is the error stack trace.
     */
    public String getMessage(String s) throws IOException {
        String message = null;
        
        try {
            ArrayList<Symbol> lexerOutput = lexer(s);
            parseObject(lexerOutput);
            message = "Valid JSON";
        }
        catch(InvalidJSONException e) {
            message = e.getMessage();
            
        }
        
        return message;
    }
    
}
