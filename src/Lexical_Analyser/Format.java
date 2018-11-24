package Lexical_Analyser;

import Structure.Symbol;
import java.io.IOException;
import java.io.PushbackReader;
import s188219_jsonparser.InvalidJSONException;
import static Structure.Symbol.Type.*;

/**
 * 
 *This class handles formatting of digits, escape characters and numbers
 *
 * @author S188219
 */
public class Format {

    /**
     * Checks that a character sequence matches a given word.
     * Used for true, false and null (outside quotes)
     *
     * @param nextChar - the next character to be read
     * @param pb - PushbackReader. Reads one character at a time
     * @param word - word to be matched against
     * @return boolean - whether character sequence matches the word
     * @throws IOException
     */
    public static boolean isWord(char nextChar, PushbackReader pb, String word) throws IOException {
        char[] chars = word.toCharArray();
        int counter = 1;
        boolean isWord = false;
        for (int i = 1; i < word.length(); i++) {
            nextChar = (char) pb.read();
            if (nextChar == chars[i]) {
                counter++;
            } else {
                break;
            }
        }

        isWord = (counter == chars.length);
        return isWord;
    }

    /**
     * Checks whether a character is a number from 0-9
     *
     * @param nextChar - character to be checked
     * @return boolean - whether that character is a number from 0-9
     */
    public static boolean isDigit(char nextChar) {
        boolean isDigit = false;

        switch (nextChar) {
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
                isDigit = true;
                break;
            }
        }

        return isDigit;
    }

    /**
     *Though hex is not a recognised number format in JSON,
     * it is required to handle Unicode characters
     *
     * @param nextChar - the next character to be read
     * @return boolean - whether the next character is a hexadecimal digit or
     * not
     */
    public static boolean isHex(char nextChar) {
        boolean isValidLetter = false;

        switch (nextChar) {
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F': {
                isValidLetter = true;
            }
        }

        return isValidLetter || isDigit(nextChar);
    }

    /**
     * This method handles escape characters within a string
     * of text
     *
     * @param nextChar - the next character read
     * @param pb - PushbackReader. Reads one character at a time. PushbackReader
     * used for unread() method
     * @return Symbol - newly created symbol
     * @throws IOException - thrown by PushbackReader
     * @throws InvalidJSONException
     */
    public static char escape(char nextChar, PushbackReader pb) throws IOException, InvalidJSONException {

        char output = '\0';
        nextChar = (char) pb.read();

        switch (nextChar) {
            case 't': {
                //pb.unread(nextChar);
                output = '\t';//horizontal tab
                break;
            }
            case 'r': {
                //pb.unread(nextChar);
                output = '\r';//carriage return
                break;
            }
            case 'n': {
                //pb.unread(nextChar);
                output = '\n'; //newline
                break;
            }

            case 'b': {
                //pb.unread(nextChar);
                output = '\b'; //backspace
                break;
            }

            case 'f': {
               // pb.unread(nextChar);
                output = '\f'; //formfeed
                break;
            }
            
//            case '\"': {
//                pb.unread(nextChar);
//                output = '\"'; //quote
//                break;
//            }

//            case '\"':
            case '\\':
            case '/': {

                pb.unread(nextChar);
                output = nextChar;
                break;
            }

            //Unicode character
            case 'u': {

                StringBuilder fourDigits = new StringBuilder();

                fourDigits.append("\\u");

                for (int i = 0; i < 4; i++) {
                    nextChar = (char) pb.read();
                    if (isHex(nextChar)) {
                        fourDigits.append(nextChar);
                        System.out.println(fourDigits);
                    } else {
                        throw new InvalidJSONException();
                    }
                }

                String outputString = fourDigits.toString();
                output = (char) Integer.parseInt(outputString.substring(2), 16);
                System.out.println(output);

                break;
            }
        }

        return output;
    }

    /**
     * This method creates a number symbol given an
     * integer, decimal or exponential format.
     *
     * @param nextChar - the next character read
     * @param pb - PushbackReader. Reads one character at a time. PushbackReader
     * used for unread() method
     * @return Symbol - newly created symbol
     * @throws IOException - thrown by PushbackReader
     * @throws InvalidJSONException
     */
    public static Symbol createNumber(char nextChar, PushbackReader pb) throws IOException, InvalidJSONException {
        StringBuilder output = new StringBuilder();

        String integerBlock = createIntegerBlock(nextChar, pb);
        output.append(integerBlock);

        nextChar = (char) pb.read();

        //check that number is a decimal
        if (nextChar == '.') {

            output.append(nextChar);

            nextChar = (char) pb.read();

            if (isDigit(nextChar)) {

                String postDecimalBlock = createIntegerBlock(nextChar, pb);
                output.append(postDecimalBlock);
            } else {
                throw new InvalidJSONException("Number error. expected digit. Got" + nextChar);
            }

        } else {
            pb.unread(nextChar);

        }

        nextChar = (char) pb.read();
        if (nextChar == '.') {
            throw new InvalidJSONException("Number error. number cannot have more than one decimal point");
        }

        //then check number is also in exponential format
        switch (nextChar) {
            case 'e':
            case 'E': {
                //pb.unread(nextChar);
                output.append(nextChar);

                pb.skip(0);
                nextChar = (char) pb.read();

                //E/e must be followed by a sign
                if (nextChar == '+' || nextChar == '-') {

                    output.append(nextChar);

                    nextChar = (char) pb.read();

                    if (isDigit(nextChar)) {

                        String eBlock = createIntegerBlock(nextChar, pb);
                        output.append(eBlock);
                    } else {
                        throw new InvalidJSONException("Number error. Expected digit. Got " + nextChar);
                    }

                } else {
                    throw new InvalidJSONException("Number error. Expected + or -. Got " + nextChar);
                }
            }
            case ',':
            case '}':
            case ']':
                break;
            default: {
                nextChar = (char) pb.read();

                if (nextChar == 'e' || nextChar == 'E') {
                    throw new InvalidJSONException("Number cannot have more than one e");
                }

            }
        }

        pb.unread(nextChar);
        Symbol numberSymbol = new Symbol(NUMBER, String.valueOf(output));
        return numberSymbol;

    }

    /**
     * This method is called by createNumber, and
     * creates a string of integers. Each number format is a block of integers
     * separated by a . or E/e
     *
     * @param nextChar - the next character to be read
     * @param pb - PushbackReader. Reads one character at a time
     * @return output - returns a string consisting of digits
     * @throws IOException - thrown by PushbackReader
     */
    public static String createIntegerBlock(char nextChar, PushbackReader pb) throws IOException {
        StringBuilder output = new StringBuilder();

        pb.unread(nextChar);
        isANumber:
        do {
            nextChar = (char) pb.read();
            if (isDigit(nextChar)) {
                output.append(nextChar);
            } else {
                break isANumber;
            }

        } while (isDigit(nextChar));

        pb.unread(nextChar);

        return String.valueOf(output);
    }

}
