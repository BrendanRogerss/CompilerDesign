import java.io.*;
import java.util.Arrays;

/**
 * Created by Brendan on 15/8/17.
 * Student Number"3208972
 *
 * Class that controls the input from the file
 * maintains information of where in the file it is
 * is able to get a single character, word or line
 * can check if it is the end of file
 */
public class InputController {

    private PushbackReader reader = null;
    public int row = 0;

    public InputController(String filename){

        try {
            File file = new File(filename);
            reader = new PushbackReader(new FileReader(file), 100);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //returns the next character
    public String get() throws IOException {
        String input = Character.toString((char)reader.read());
        if(input.equals("\n")){
            row++;
        }
        return input;
    }
    //returns the next 'word', that being a series of alphanumeric characters
    public String getWord() throws IOException {
        String output = "";
        String letter = get();
        while (Character.isAlphabetic(letter.charAt(0)) || Character.isDigit(letter.charAt(0))){
            output+=letter;
            letter = get();
        }
        reader.unread(letter.charAt(0));
        return output.toLowerCase();
    }
    //gets the rest of the line
    public String getLine() throws IOException {
        String line = "";
        String letter = get();
        while(!letter.equals("\n")&&!letter.equals("\r")){
            line+=letter;
            letter=get();
        }
        return line;
    }

    //checks if the file has ended
    public boolean eof() throws IOException {
        int eof = reader.read();
        reader.unread(eof);
        return (eof==65535 || !reader.ready() || eof==-1);
    }

    //returns a character back into the input stream
    public void pushback(String s) throws IOException {
        reader.unread(s.charAt(0));
    }

}
