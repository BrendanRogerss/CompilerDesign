import java.io.IOException;

/**
 * Created by Brendan on 15/8/17.
 * Student Number: 3208972
 *
 * Main class of the scanner
 * will return the next token when requested
 *
 */
public class Scanner {

    private InputController r;
    private Tokenizer tokenizer = new Tokenizer();

    public Scanner(InputController inputController){
        r = inputController;
    }

    public boolean eof() throws IOException {

        return r.eof();
    }

    //checks if a character is undefined
    private boolean isUndefined(String s){
        boolean undefined = true;
        if(Character.isAlphabetic(s.charAt(0)) || Character.isDigit(s.charAt(0))){
            undefined = false;
        }
        //TODO: does = belong here?
        String delimiters = "\"[]();,:.!><+-*/%^ \n\r\t";
        if(delimiters.contains(s)){
            undefined = false;
        }
        return undefined;
    }
    //checks if the input string is alphanumeric
    private String charType(char input){
        String output = Character.toString(input);
        if(Character.isAlphabetic(input)){
            output = "z";
        }else if(Character.isDigit(input)){
            output = "1";
        }
        return output;
    }

    //returns the next token
    public Token getToken() throws IOException {
        String buffer = "";
        String character;

        while(true){
            character = r.get(); //gets the next character
            switch(charType(character.charAt(0))){ //reduces alphabet to just 'z' and numbers to just '1'
                case "z":
                    buffer=character+r.getWord(); //continues to get characters as long as it is alphanumeric
                    return tokenizer.getToken(buffer);

                case "1":
                    //starting to get an int
                    while(charType(character.charAt(0)).equals("1")){ //continues to get input as long as it is a number
                        buffer+=character;
                        character = r.get();
                    }
                    if(character.equals(".")){
                        String floatBuffer = ".";
                        character = r.get();
                        while(charType(character.charAt(0)).equals("1")){ //check for the rest of the float
                            floatBuffer+=character;
                            character = r.get();
                        }
                        r.pushback(character);
                        if(floatBuffer.length()>1){ //check if we got more numbers
                            buffer+=floatBuffer;
                        }else{
                            r.pushback(".");
                        }
                    }else{
                        r.pushback(character); //pushback whatever character we grabbed that wasn't a number
                    }
                    return tokenizer.getToken(buffer);
                case "\"": //opened up a string
                    //consume until another "
                    buffer+=character;
                    while(!r.eof()){
                        character = r.get();
                        switch (character) {
                            case "\"": //found the end of the string
                                buffer += "\"";
                                return tokenizer.getToken(buffer);
                            case "\n":
                            case "\r": //end of line while parsing string
                                A1.out.printError("\nError on line: " + Integer.toString(r.row) + " Expected \" instead found end of line");


                                return null;
                            default:
                                buffer += character;
                                break;
                        }
                    }
                    return null;
                case "=": //could be ==
                    character = r.get(); //get next character
                    switch(character){
                        case "=":
                            return tokenizer.getToken("=="); //if ==
                        default: //if anything else
                            r.pushback(character);
                            return tokenizer.getToken("=");
                    }
                case "!": //could be !=
                    character = r.get();
                    switch(character){
                        case "=":
                            return tokenizer.getToken("!=");
                        default:
                            r.pushback(character);
                            return tokenizer.getToken("!");
                    }
                case ">": //need to check if token is >=, >> or just >
                    character = r.get();
                    switch(character){
                        case "=": return tokenizer.getToken(">=");
                        case ">": return tokenizer.getToken(">>");
                        default: //next character didnt make a new token, push it back and return > token
                            r.pushback(character);
                            return tokenizer.getToken(">");
                    }
                case "<":
                    character = r.get();
                    switch(character){
                        case "=": return tokenizer.getToken("<=");
                        case "<": return tokenizer.getToken("<<");
                        default:
                            r.pushback(character);
                            return tokenizer.getToken("<");
                    }

                case "/": //could be the opening of a comment
                    buffer=character+r.get()+r.get(); //get the next 2 characters
                    if(buffer.equals("/--")) { // comment found
                        r.getLine(); //consume the rest of the line
                        buffer = "";
                        return null;
                    }else if(buffer.equals("/-*")){ //multi line comment
                        int start = r.row;
                        buffer = "";
                        while(!buffer.contains("*-/")){
                            if(r.eof()){ //hit the end of file, ERROR!!1!!!1one
                                A1.out.printError("Reached end of line while parsing multi line comment. Comment starts on line: "+start);
                                return null;
                            }
                            buffer += r.get();
                        }
                        return null;

                    }else{ //not a comment
                        //push back the 2 characters gotten
                        r.pushback(buffer.substring(2,3));
                        r.pushback(buffer.substring(1,2));
                        return tokenizer.getToken("/");
                    }
                //tokenize the remaining delimiters
                case "[":return tokenizer.getToken(character);
                case "]":return tokenizer.getToken(character);
                case "(":return tokenizer.getToken(character);
                case ")":return tokenizer.getToken(character);
                case ";":return tokenizer.getToken(character);
                case ",":return tokenizer.getToken(character);
                case ":":return tokenizer.getToken(character);
                case ".":return tokenizer.getToken(character);
                case "+":return tokenizer.getToken(character);
                case "-":return tokenizer.getToken(character);
                case "*":return tokenizer.getToken(character);
                case "%":return tokenizer.getToken(character);
                case "^":return tokenizer.getToken(character);
                //pass over the whitespace
                case " ":
                case "\n":
                case "\r":
                case "\t":
                    if(r.eof()){ //deal with an end of file error
                        return null;
                    }
                    continue;

                default: //undefined character
                    while(isUndefined(character) && !r.eof()){ //continue getting undefined characters
                        buffer+=character;
                        character=r.get();
                    }
                    r.pushback(character);
                    return  tokenizer.getToken(buffer);
            }
        }
    }
}