package chat.entries;

import chat.core.*;
import chat.api.*;
import chat.domain.room.InvalidRoomName;
import chat.domain.room.RoomAlreadyExistsInDB;
import chat.domain.user.InvalidPassword;
import chat.domain.user.InvalidUsernameOrPassword;
import chat.domain.user.UserAlreadyLoggedIn;
import chat.domain.user.UserExists;
import chat.infrastructure.DB;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends Thread implements Closeable {
    private final Server server;
    private final Socket socket;
    private String name;
    private final ClientHandler clientHandler;
    private final BlockingQueue<String> messageQueue;
    private volatile Room room;
    private volatile User user;
    private volatile Message message;
    private volatile Private_Message privateMessage;
    private volatile Subscription subscription;
    private volatile boolean userIsValidatedForLoggin;

    private final DBChat api;

    public Client(Server server, Socket socket, String name, DBChat api) throws IOException, ClassNotFoundException {
        this.server = server;
        this.socket = socket;
        this.clientHandler = new ClientHandler(socket.getInputStream(), new PrintWriter(socket.getOutputStream()));
        this.name = name;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.room = null;
        this.user = null;
        this.message = null;
        this.privateMessage = null;
        this.subscription = null;
        this.api = api;
        this.userIsValidatedForLoggin = false;
    }

    @Override
    public void run() {
        Thread t = new Thread(() -> {
            try {
                //Create og choose a room
                chooseCreateEnterRoomAndGetOldMessages();

                //Handle the rest of the user inputs through a loop
                inputHandlerLoop();

            }catch(NoSuchElementException e){
                //We do nothing for this exception, just catch it

            } finally {
                //We send a final message to the messageQueue not to get an exception
                this.messageQueue.add("closeThisSocketWhitLongCode1234567890Crypt");
            }
        });
        try {

            //Create new user or login
            doYouHaveAProfileSwitch();

            if(userIsValidatedForLoggin){
                //Welcome message to the chat
                clientHandler.welcomeMessageUser(user.getName());

                //Start Thread
                t.start();

                while (true) {
                    //We get the inbound message
                    String inbound = messageQueue.take();

                    //We breake the loop if the message is as followed, not to get an exection
                    if(inbound.equals("closeThisSocketWhitLongCode1234567890Crypt")){
                        break;
                    }
                    //We broadcast the message
                    clientHandler.printString(inbound);
                }
            }

        } catch (InterruptedException e) {
            System.out.println(name + " exited with: " + e.getMessage());
        } finally {
            //We remove the client/user from the activeList
            api.removeUserFromActiveUsers(user);
            //We remove the client through the method close(), and close the socket
            try { close(); } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                t.interrupt();
                t.join(1000);
            } catch (InterruptedException e) {
                // t.stop();
            }
        }
    }

    /**
     * Input handler
     */

    private void inputHandlerLoop(){
        while (true) {
            String messageInput = clientHandler.waitForLine();

            //Set new online timestamp
            api.setOnlineStamp(user);

            if (messageInput.startsWith("!lobby")) {

                //Client exit the room announcement
                server.announceExitChat(this, room);

                //Client returns to the lobby
                clientHandler.returnToLobby(room.getName());

                //Reset room variable
                room = null;

                //reset message queue
                messageQueue.clear();

                chooseCreateEnterRoomAndGetOldMessages();

            } else if (messageInput.startsWith("!help")) {

                //Show the help options to the client
                clientHandler.help();

            } else if (messageInput.startsWith("!list")) {

                //Print a user list of the current room
                printUserListFromARoom();

            } else if (messageInput.startsWith("!room")) {

                //Show room name to the client
                clientHandler.roomName(user.getName(), room.getName());


            } else if (messageInput.startsWith("!sendP")) {

                //Send private message
                try {
                    sendAPrivateMessage();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else if (messageInput.startsWith("!getP")) {

                //Load the last 10 messages in the current room
                messagesFromDB(api.get10NumberOfRoomPrivateMessages(room, user));

            } else if (messageInput.startsWith("!exit")) {

                //Client exits the room announcement
                server.announceExitChat(this, room);

                try {
                    //Close client socket
                    close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Break from the thread (else error)
                break;

            } else {

                message = api.createMessage(messageInput, user, room);

                try {
                    //Broadcast message to other clients
                    server.broadcast(message);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Show users in the current room
     */

    private void printUserListFromARoom() {

        clientHandler.roomListMessage(room.getName());

        ArrayList<String> subscriptionUserNames = api.getListOfAllSubscribingUsersFromARoom(subscription);

        for(String name : subscriptionUserNames){
            clientHandler.printRoomUserName(name);
        }

    }

    /**
     * Send private message
     */

    private void sendAPrivateMessage() throws ParseException {

        //Print a user list of the current room
        printUserListFromARoom();

        //Get the recipient username
        clientHandler.whoDoYouWantToSendAPrivateMessageTo();
        String whoToSendTo = clientHandler.waitForLine();

        //ArrayList of all subscribers to a room
        ArrayList<String> allSubscribersToARoom = api.getListOfAllSubscribingUsersFromARoom(subscription);

        //Check if input is a valid user to send to
        int recipientExists = allSubscribersToARoom.lastIndexOf(whoToSendTo);

        if(recipientExists > -1){

            //Get the recipient username
            clientHandler.writeThePrivateMessage();
            String thePrivateMessage = clientHandler.waitForLine();

            privateMessage = api.createPrivateMessage(thePrivateMessage, user, room, whoToSendTo);

            //Send private message
            server.privateBroadcast(privateMessage);


        } else {
            clientHandler.invalidRecipientName();
        }

    }


    /**
     *
     * Welcome to the chat
     *
     */

    private void doYouHaveAProfileSwitch() {
        String password1, password2;

        //Profile Y/n to the client when connecting to the Thread
        String answer = clientHandler.doYouHaveAProfile();

        switch (answer) {
            case "Y":
                //Client input name
                name = clientHandler.fetchName();

                clientHandler.typePassword();
                password1 = clientHandler.fetchPassword();


                //Check the username and password that is provided, and the user from DB
                try {
                    user = api.loginValidation(name, password1);

                    userIsValidatedForLoggin = true;

                    break;

                } catch(NullPointerException | InvalidUsernameOrPassword e){
                    //Unknown username or password
                    clientHandler.unknownUsernameOrPassword();
                    doYouHaveAProfileSwitch();

                } catch (UserAlreadyLoggedIn e){
                    //User is already logged in and goodbye message
                    clientHandler.youAreAldreadyLoogedIn();

                    try {
                        //Close client socket
                        close();
                    } catch (IOException ee) {
                        ee.printStackTrace();
                    }

                }

                //Break from the thread (else error)
                break;


            case "n":
                //Client new input name
                name = clientHandler.fetchName();

                //Create password
                clientHandler.typePassword();
                password1 = clientHandler.fetchPassword();
                clientHandler.typePasswordAgain();
                password2 = clientHandler.fetchPassword();

                //Create timeStamp
                LocalDateTime localTime = LocalDateTime.now();

                //Create user in the api and the the user that is returned returned
                try {
                    user = api.createUserInSystemAndDB(name, password1, password2, localTime);

                    userIsValidatedForLoggin = true;

                    break;

                } catch (UserExists e){
                    //Username already exists in DB
                    clientHandler.userAlreadyExists();

                    //Back to choose user
                    doYouHaveAProfileSwitch();

                } catch (InvalidPassword e){
                    //Password do no match
                    clientHandler.passwordsDoNotMatch();

                    //Back to choose user
                    doYouHaveAProfileSwitch();
                }

            default:
                //Unknown input => redirect to login/create user page
                clientHandler.unknownInput();

                //Restart the login process
                doYouHaveAProfileSwitch();
        }
    }


    /**
     *
     * Choose or create room
     *
     */

    private void chooseCreateEnterRoomAndGetOldMessages() {
        //Create or choose room from list of available rooms
        createOrEnterExistingRoom(showAvailableRooms());

        //Subscription to room
        subscribeToRoom();

        //Enter a new room message
        clientHandler.youHavEnteredTheRoom(room.getName());

        //Announce client username to other clients in the room
        server.announceName(this, room);

        //Load the last 10 messages in the current room
        messagesFromDB(api.getThe10LastRoomMessages(room, user));
    }


    private ArrayList<String> showAvailableRooms(){

        ArrayList<String> listOfRoomNames = new ArrayList<>();
        listOfRoomNames = api.getAvailableRooNames();

        if(listOfRoomNames.size() == 0 || listOfRoomNames.isEmpty()){
            clientHandler.noRoomAvailable();
            return listOfRoomNames;
        } else {
            clientHandler.hereAreTheActiveRooms();
            for(int i = 0 ; i < listOfRoomNames.size() ; i++){
                clientHandler.printRoomName(listOfRoomNames.get(i));
            }
            return listOfRoomNames;
        }
    }

    private void createOrEnterExistingRoom(ArrayList<String> listOfRoomNames){

        //Client enters existing room name or [Create] to make a new room
        String answer = clientHandler.doYouWantToCreateARoomOrEnterExisting();

        //We check if the client input matches the list of room names array
        int roomExists = listOfRoomNames.lastIndexOf(answer);

        if(roomExists > -1){
            //If the room exist we fetch it from the DB and return it
            room = api.getRoomFromDB(answer);

        } else {
            //If the room doesn't exist, we check the client input for [Create]
            if (answer.equalsIgnoreCase("create")){

                while(true) {
                    //Client names the new room
                    String roomName = clientHandler.whatIsTheNewRoomsName();

                    try {
                        //Create the new room i system/DB or throw exception
                        room = api.createRoomInSystemAndDB(roomName);
                        break;

                    } catch (RoomAlreadyExistsInDB e) {

                        clientHandler.roomAlreadyExists();

                    } catch (InvalidRoomName e) {

                        clientHandler.roomNameInvalid();
                    }
                }
            } else {

                clientHandler.UnknownRoomNameOrInput();
                createOrEnterExistingRoom(showAvailableRooms());
            }
        }
    }

    private void subscribeToRoom() {

        subscription = api.createSubscription(user, room);
    }

    public String getUserName() {
        return name;
    }

    public Room getRoom() {
        return room;
    }

    @Override
    public String toString() {
        return "Client{" +
                "socket=" + socket +
                '}';
    }

    @Override
    public void close() throws IOException {
        server.removeClient(this);
        socket.close();
    }

    //The live messages the client is sending
    public void sendMessage(String s) {
        messageQueue.add(s);
    }

    //The stored messages that are recalled when a user enters the room
    public void messagesFromDB(ArrayList<String> s) {
        for(int i = 0 ; i < s.size() ; i++){
            messageQueue.add(s.get(i));
        }
    }

}