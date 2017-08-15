import java.io.IOException;

/**
 * Created by Brendan on 15/8/17.
 */
public class Scanner {

    Input r;
    String buffer;

    Tokenizer tokenizer = new Tokenizer();

    public Scanner(Input input){
        r = input;
    }

    public boolean eof() throws IOException {

        return r.eof();
    }

    public String getToken() throws IOException {

        String character;

        while(true){
            character = r.get();
            switch(character){
                case "a":
                case "b":
                case "c":
                case "d":
                case "e":
                case "f":
                case "g":
                case "h":
                case "i":
                case "j":
                case "k":
                case "l":
                case "m":
                case "n":
                case "o":
                case "p":
                case "q":
                case "r":
                case "s":
                case "t":
                case "u":
                case "v":
                case "w":
                case "x":
                case "y":
                case "z":
                    buffer=character+r.getWord();
                    return tokenizer.getToken(buffer);

                case "1":
                case "2":
                case "3":
                case "4":
                case "5":
                case "6":
                case "7":
                case "8":
                case "9":
                case "0":

                case "\"": //opened up a string
                    //consume until another "
                case"/": //could be start of a comment
                    //check for rest of comment /-- then consume rest of the line




            }

            break;
        }




        return buffer;
    }

}
