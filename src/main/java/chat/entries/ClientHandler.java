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

    public void welcomeMessageUser(String name){
        out.println("Welcome to the chat " + name);
        out.flush();
    }

    public String doYouHaveAProfile(){
        out.println("Do you have a profile Y/n?");
        out.flush();
        return prompt();
    }

    public void userAlreadyExists() {
        out.println("User already exists.. Please choose another username.");
        out.flush();
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
//        showPrompt();
        return waitForLine();
    }


    public String fetchName() {
        out.println("What's your name?");
        out.flush();
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
        out.println(string);
        out.flush();
    }


    /**
     * Room
     */

    public String doYouWantToCreateARoomOrEnterExisting(){
        out.println("Write the name of the room you want to enter enter, or [Create] for a new room.");
        out.flush();
        return prompt();
    }

    public void printRoomName(String string) {
        out.println(string);
        out.flush();
    }

    public void noRoomAvailable(){
        out.println("No rooms are available at the moment :/");
        out.flush();
    }

    public void hereAreTheActiveRooms(){
        out.println("Here are the active rooms :)");
        out.flush();
    }

    public String whatIsTheNewRoomsName() {
        out.println("What is the new rooms name?");
        out.flush();
        return prompt();
    }

    public void UnknownRoomNameOrInput() {
        out.println("Unknown room name or command... :/");
        out.flush();
    }

    public void returnToLobby(String roomName) {
        out.println("You have exited the room: " + roomName + " and returned to the lobby.");
        out.flush();
    }

    public void roomAlreadyExists() {
        out.println("Room already exists.. Please choose another name for the room.");
        out.flush();
    }

    public void youHavEnteredTheRoom(String roomName) {
        out.println("You have entered the room: " + roomName);
        out.println("Write [!help] to see your options");
        out.flush();
    }

    public void help() {
        out.println("[!lobby]\tReturn to Lobby - Create/Choose room)");
        out.println("[!list]\tSee all the clients that are connected to the room");
        out.println("[!room]\tGet the current room name you are in");
        out.println("[!exit]\tExit the chat :(");
        out.flush();
    }

    public void roomName(String userName, String roomName) {
        out.println(userName + " you are currently in the " + roomName + " room.");
        out.flush();
    }
}
