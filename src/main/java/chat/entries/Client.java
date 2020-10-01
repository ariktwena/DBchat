package chat.entries;

import chat.core.*;
import chat.api.*;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends Thread implements Closeable {
    private final Server server;
    private final Socket socket;
    private String name;
    private final ClientHandler clientHandler;
    private final BlockingQueue<String> messageQueue;
    private final DB db;
    private volatile Room room;
    private volatile User user;
    private volatile Message message;
    private volatile Private_Message privateMessage;
    private volatile Subscription subscription;

    private final DBChat api;

    public Client(Server server, Socket socket, String name, DBChat api) throws IOException, ClassNotFoundException {
        this.server = server;
        this.socket = socket;
        this.clientHandler = new ClientHandler(socket.getInputStream(), new PrintWriter(socket.getOutputStream()));
        this.name = name;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.db = new DB();
        this.room = null;
        this.user = null;
        this.message = null;
        this.privateMessage = null;
        this.subscription = null;
        this.api = api;
    }

    @Override
    public void run() {
        Thread t = new Thread(() -> {

            //Create og choose a room
            chooseCreateEnterRoomAndGetOldMessages();

            //Handle the rest of the user inputs through a loop
            inputHandlerLoop();

        });
        try {

            //Create new user or login
            doYouHaveAProfileSwitch();

            //Welcome message to the chat
            clientHandler.welcomeMessageUser(user.getName());

            //Start Thread
            t.start();

            //Client unique id
            System.out.println(t.getId());


            while (true) {
                String inbound = messageQueue.take();
                clientHandler.printString(inbound);
            }

        } catch (InterruptedException e) {
            System.out.println(name + " exited with: " + e.getMessage());
        } finally {
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
            String line = clientHandler.waitForLine();

            if (line.startsWith("!lobby")) {

                //Client exit the room announcement
                server.announceExitChat(this, room);

                //Client returns to the lobby
                clientHandler.returnToLobby(room.getName());

                //Reset room variable
                room = null;

                //reset message queue
                messageQueue.clear();

                chooseCreateEnterRoomAndGetOldMessages();

                //Set new online timestamp
                db.userOnline(user);

            } else if (line.startsWith("!help")) {

                //Show the help options to the client
                clientHandler.help();

                //Set new online timestamp
                db.userOnline(user);

            } else if (line.startsWith("!list")) {

                //Print a user list of the current room
                printUserListFromARoom();

                //Set new online timestamp
                db.userOnline(user);

            } else if (line.startsWith("!room")) {

                //Show room name to the client
                clientHandler.roomName(user.getName(), room.getName());

                //Set new online timestamp
                db.userOnline(user);

            } else if (line.startsWith("!sendP")) {

                //Send private message
                try {
                    sendAPrivateMessage();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Set new online timestamp
                db.userOnline(user);

            } else if (line.startsWith("!getP")) {

                //Load the last 10 messages in the current room
                messagesFromDB(db.get10NumberOfRoomPrivateMessages(room, user));


                //Set new online timestamp
                db.userOnline(user);

            } else if (line.startsWith("!exit")) {

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

                //Create timestamp
                LocalDateTime localTime = LocalDateTime.now();

                //Create message
                message = new Message(line, localTime, user, room);

                //Save message to DB
                message = db.createMessage(message);

                //Set new online timestamp
                db.userOnline(user);

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

//        for(int i = 0 ; i < db.getAllSubscribingUsersFromARoom(subscription).size() ; i++){
//            clientHandler.printRoomUserName(db.getAllSubscribingUsersFromARoom(subscription).get(i));
//        }

        for(String name : db.getAllSubscribingUsersFromARoom(subscription)){
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

        //Check if input is a valid user to send to
        int recipientExists = db.getAllSubscribingUsersFromARoom(subscription).lastIndexOf(whoToSendTo);

        if(recipientExists > -1){

            //Get the recipient username
            clientHandler.writeThePrivateMessage();
            String thePrivateMessage = clientHandler.waitForLine();

            //Create timestamp
            LocalDateTime localTime = LocalDateTime.now();

            //Create message
            privateMessage = new Private_Message(thePrivateMessage, localTime, user, room, whoToSendTo);

            //Get recipient id
            int recipient_id = db.getUserId(whoToSendTo);

            //Save message to DB
            privateMessage = db.createPrivateMessage(privateMessage, recipient_id);

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


                /**
                 * Old code
                 */
//                //Check the username and password that is provided, and the user from DB
//                user = api.loginValidation(name, password1);
////                user = db.getUser(name);
//
//                //Validate DB user_get_request
//                if(user == null){
//                    clientHandler.unknownUsername();
//                    doYouHaveAProfileSwitch();
//
//                }
//
//                //if the user exists in the DB, BUT is not already logged in
//                else if(server.isClientAlreadyLoggedIn(user.getName())){
//
//                    //User is already logged in and goodbye message
//                    clientHandler.youAreAldreadyLoogedIn();
//
//                    try {
//                        //Close client socket
//                        close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    //Break from the thread (else error)
//                    break;
//                }
//
//                else {
//
//
//                    //Login the user and set status to online
//                    db.userLogin(user);
//                    db.userOnline(user);
//
//                    break;
//                }

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

                    db.userLogin(user);
                    db.userOnline(user);

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
        messagesFromDB(db.getThe10LastRoomMessages(room, user));
    }


    private ArrayList<String> showAvailableRooms(){

        ArrayList<String> listOfRoomNames = new ArrayList<>();
        listOfRoomNames = db.getAllRoomNames();

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
            room = db.getRoom(answer);

        } else {
            //If the room doesn't exist, we check the client input for [Create]
            if (answer.equalsIgnoreCase("create")){

                //Client names the new room
                String roomName = clientHandler.whatIsTheNewRoomsName();

                //We loop the name giving of the room name until we get a uniq name
                while(true){
                    if(!db.roomExistsInDB(roomName)){
                        break;
                    } else if (roomName.equalsIgnoreCase("create")){
                        //Client names the new room
                        clientHandler.roomNameInvalid();
                        roomName = clientHandler.whatIsTheNewRoomsName();
                    } else {
                        //Client names the new room
                        clientHandler.roomAlreadyExists();
                        roomName = clientHandler.whatIsTheNewRoomsName();
                    }
                }

                //We create a room and also create the room in the DB. Thereafter we set and return the room.
                room = new Room(roomName);
                room = db.createRoom(room);

            } else {

                clientHandler.UnknownRoomNameOrInput();
                createOrEnterExistingRoom(showAvailableRooms());
            }
        }
    }

    private void subscribeToRoom() {

        //Create timestamp
        LocalDateTime localTime = LocalDateTime.now();

        //Create new subscription
        subscription = new Subscription(localTime, user, room);

        //Add subscription to DB
        db.createSubscriptionForRoom(subscription);
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