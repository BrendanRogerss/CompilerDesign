import java.io.CharArrayReader;
import java.io.IOException;

/**
 * Created by Brendan on 15/8/17.
 */
public class Scanner {

    static Input r;
    private Tokenizer tokenizer = new Tokenizer();

    public Scanner(Input input){
        r = input;
    }

    public boolean eof() throws IOException {

        return r.eof();
    }

    public String charType(char input){
        String output = Character.toString(input);
        if(Character.isAlphabetic(input)){
            output = "z";
        }else if(Character.isDigit(input)){
            output = "1";
        }
        return output;
    }

    public String getToken() throws IOException {
        String buffer = "";
        String character;
        while(true){
            character = r.get();
            switch(charType(character.charAt(0))){
                case "z":
                    buffer=character+r.getWord();
                    return tokenizer.getToken(buffer);

                case "1":
                    //starting to get an int
                    while(charType(character.charAt(0)).equals("1")){
                        buffer+=character;
                        character = r.get();
                    }
                    r.pushback(character); //pushback whatever character we grabbed that wasn't a number
                    return tokenizer.getToken(buffer);
                case "0":
                    //TODO: deal with leading 0s

                case "\"": //opened up a string
                    //consume until another "
                    buffer+=character;
                    //TODO: fix up shit code
                    while(true){
                        character = r.get();
                        if(character.equals("\"")){
                            buffer+="\"";
                            return tokenizer.getToken(buffer);
                        }else if(character.equals("\n")){
                            //TODO: some error shit, idk.
                            Assignment1.out.printError("Error on line: "+Integer.toString(r.row)+"Expected \",instead found end of line");
                        }else{
                            buffer+=character;
                        }
                    }
                case "[":return tokenizer.getToken(character);
                case "]":return tokenizer.getToken(character);
                case "(":return tokenizer.getToken(character);
                case ")":return tokenizer.getToken(character);
                case ";":return tokenizer.getToken(character);
                case ",":return tokenizer.getToken(character);
                case ":":return tokenizer.getToken(character);
                case ".":return tokenizer.getToken(character);
                case "=":
                    character = r.get();
                    switch(character){
                        case "=":
                            return tokenizer.getToken("==");
                        default:
                            r.pushback(character);
                            //TODO: whatever happens here
                    }
                case "!":
                    character = r.get();
                    switch(character){
                        case "=":
                            return tokenizer.getToken("!=");
                        default:
                            r.pushback(character);
                            //TODO: whatever happens here
                    }
                case ">":
                    character = r.get();
                    switch(character){
                        case "=": return tokenizer.getToken(">=");
                        case ">": return tokenizer.getToken(">>");
                        default:
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

                case "+":return tokenizer.getToken(character);
                case "-":return tokenizer.getToken(character);
                case "*":return tokenizer.getToken(character);
                case "/":
                    buffer=character+r.get()+r.get();
                    if(buffer.equals("/--")){ // comment found
                        buffer+=r.getLine();
                        //return tokenizer.getToken(buffer);
                        break;
                    }else{ //not a comment
                        //TODO: push back both at same time
                        r.pushback(buffer.substring(2,3));
                        r.pushback(buffer.substring(1,2));
                        return tokenizer.getToken("/");
                    }

                case "%":return tokenizer.getToken(character);
                case "^":return tokenizer.getToken(character);
                case " ":break; //TODO: check if this is the correct way to handle whitespace and \n
                case "\n":break;
                case "\r":break;
                case "\t":break;
                default:return  tokenizer.getToken(character);
            }
        }
    }
}