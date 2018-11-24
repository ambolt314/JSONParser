package Lexical_Analyser;

import Structure.Symbol;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import s188219_jsonparser.InvalidJSONException;
import static Lexical_Analyser.Format.*;
import static Structure.Symbol.Type.*;

/**
 *
 * @author S188219
 */
public class S188219_Lexer {

    /**
     * This method converts an input string into a set of symbols.
     *
     * @param s the string input
     * @return - output an ArrayList of symbols. To be handled by parser
     * @throws IOException thrown by PushbackReader
     * @throws InvalidJSONException
     */
    public static ArrayList<Symbol> lexer(String s) throws IOException, InvalidJSONException {

        char c = '\0';

        StringReader sr = new StringReader(s);
        PushbackReader pbReader = new PushbackReader(sr);

        ArrayList<Symbol> lexerOutput = new ArrayList<>();

        do {
            c = (char) pbReader.read();

            switch (c) {

                case '\uffff':
                    break; //end of file reached

                case '{': {
                    Symbol openObjSymbol = new Symbol(OBJ_OPEN);
                    lexerOutput.add(openObjSymbol);
                    break;

                }
                case '}': {
                    Symbol closeObjSymbol = new Symbol(OBJ_CLOSE);
                    lexerOutput.add(closeObjSymbol);
                    break;

                }
                case '[': {
                    Symbol openArraySymbol = new Symbol(ARRAY_OPEN);
                    lexerOutput.add(openArraySymbol);
                    break;

                }
                case ']': {
                    Symbol openArraySymbol = new Symbol(ARRAY_CLOSE);
                    lexerOutput.add(openArraySymbol);
                    break;

                }
                case ':': {
                    Symbol colonSymbol = new Symbol(COLON);
                    lexerOutput.add(colonSymbol);
                    break;
                }

                case ',': {
                    Symbol colonSymbol = new Symbol(COMMA);
                    lexerOutput.add(colonSymbol);
                    break;
                }

                case 't': {
                    boolean isTrue = Format.isWord(c, pbReader, "true");
                    if (isTrue) {
                        Symbol trueSymbol = new Symbol(BOOLEAN, "true");
                        lexerOutput.add(trueSymbol);
                        break;
                    } else {
                        throw new InvalidJSONException("Invalid boolean. Expected TRUE");

                    }
                }

                case 'f': {
                    boolean isFalse = Format.isWord(c, pbReader, "false");
                    if (isFalse) {
                        Symbol trueSymbol = new Symbol(BOOLEAN, "false");
                        lexerOutput.add(trueSymbol);
                        break;
                    } else {
                        throw new InvalidJSONException("Invalid boolean. Expected FALSE");
                    }
                }

                case 'n': {
                    boolean isNull = Format.isWord(c, pbReader, "null");
                    if (isNull) {
                        Symbol trueSymbol = new Symbol(NULL);
                        lexerOutput.add(trueSymbol);
                        break;
                    } else {
                        throw new InvalidJSONException("Invalid. Expected NULL");
                    }
                }

                case '-': {
                    c = (char) pbReader.read();

                    if (isDigit(c)) {
                        //creates a positive integer
                        Symbol numberSymbol = createNumber(c, pbReader);
                        Symbol.Type numberType = numberSymbol.type;
                        String numberValue = numberSymbol.value;

                        //negates the number by adding a negative sign to value
                        Symbol negativeSymbol = new Symbol(numberType, "-" + numberValue);
                        lexerOutput.add(negativeSymbol);

                    } else {
                        throw new InvalidJSONException("Expected number. Got " + c);
                    }

                    break;
                }
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    Symbol numberSymbol = createNumber(c, pbReader);
                    lexerOutput.add(numberSymbol);
                    break;
                }

                case '\"': {
                    StringBuilder sb = new StringBuilder();

                    do {
                        c = (char) pbReader.read();
                        if (c == '\\') {
                            char output = escape(c, pbReader);
                            sb.append(output);
                        }
                        
                        if(c != '\"' && c != '\\') sb.append(c);
                        
                        
                        
                        if(c == '\uffff') break;
                        
                    } while (c != '\"');

                    Symbol stringSymbol = new Symbol(STRING, String.valueOf(sb));
                    lexerOutput.add(stringSymbol);
                    break;
                }

                case ' ':
                case '\t':
                case '\n': {
                    break;
                }

                default: {
                    throw new InvalidJSONException("Lexical analyser error: invalid character " + c);
                }
            }

        } while (c != '\uffff');//iterate until end of file

        return lexerOutput;

    }
}
