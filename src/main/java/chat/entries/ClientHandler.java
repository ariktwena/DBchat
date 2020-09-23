package chat.entries;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class ClientHandler {

    private final InputStream in;
    private final PrintWriter out;

    public ClientHandler(InputStream in, PrintWriter out) {
        this.in = in;
        this.out = out;
    };

    public void welcomeMessage(){
        out.print("Welcome to the chat.");
        out.flush();
    }

    public void welcomeMessageUser(String name){
        out.print("Welcome to the chat " + name);
        out.flush();
    }

    public String doYouHaveAProfile(){
        out.println("");
        out.print("Do you have a profile Y/n?");
        return prompt();
    }

    public void unknownInput(){
        out.println("Sorry, we couldn't identify your input. Please try again.");
        out.flush();
    }

    public void unknownUsername(){
        out.println("Sorry, we couldn't identify your username");
        out.flush();
    }

    public void userExists(){
        out.println("Sorry the user exists....");
        out.flush();
    }

    public void showPrompt() {
        out.print("> ");
        out.flush();
    }

    public String prompt() {
        showPrompt();
        return waitForLine();
    }


    public String fetchName() {
        out.println("");
        out.print("What's your name?");
        return prompt();
    }

    public String waitForLine() {
        return new Scanner(in).nextLine();
    }

    public boolean hasInput() throws IOException {
        in.readAllBytes();
        return in.available() > 0;
    }

    public void printString(String string) {
        out.print(string);
        out.flush();
    }

}
