package chat.entries;

import chat.core.Room;
import chat.core.User;
import chat.domain.*;
import chat.infrastructure.DB;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
    private boolean lobby;

    public Client(Server server, Socket socket, String name) throws IOException, ClassNotFoundException {
        this.server = server;
        this.socket = socket;
        this.clientHandler = new ClientHandler(socket.getInputStream(), new PrintWriter(socket.getOutputStream()));
        this.name = name;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.db = new DB();
        this.room = null;
        this.user = null;
        this.lobby = false;
    }

    public String getClientName() {
        return name;
    }


    @Override
    public void run() {
        Thread t = new Thread(() -> {

            //Create or choose room from list of available rooms
            room = createOrEnterExistingRoom(showAvailableRooms());
            System.out.println("The room..." + room);
            server.announceName(this, room);

            while (true) {
//                clientHandler.showPrompt();
                String line = clientHandler.waitForLine();


                if (line.startsWith("!lobby")) {


                    server.announcExitChat(this, room);

                    //Client returns to the lobby
                    clientHandler.returnToLobby(room.getName());

                    //Reset room variable
                    room = null;

                    //reset message queue
                    messageQueue.clear();

                    //Create or choose room from list of available rooms
                    room = createOrEnterExistingRoom(showAvailableRooms());
                    server.announceName(this, room);

                } else if (line.startsWith("!help")) {

                    System.out.println("Help");
                    //Help output here.....

                } else if (line.startsWith("!userList")) {

                    System.out.println("Help");
                    //Help output here.....

                } else if (line.startsWith("!roomName")) {

                    System.out.println("Help");
                    //Help output here.....

                } else {

                    server.broadcast(this, room, line);
                }
            }
        });
        try {

            //Create new user or login
            doYouHaveAProfileSwitch();

            //Welcome message to the chat
            clientHandler.welcomeMessageUser(user.getName());

            //Start Thread
            t.start();


            while (true) {
                String inbound = messageQueue.take();
//                clientHandler.printString("...");
                clientHandler.printString(inbound);
//                clientHandler.showPrompt();
            }

        } catch (InterruptedException e) {
            System.out.println(name + " exited with: " + e.getMessage());
        } finally {
            try { close(); } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                t.interrupt();
                t.join(1000);
            } catch (InterruptedException e) {
                t.stop();
            }
        }
    }



    /**
     *
     * Welcome to the chat
     *
     */

    public void doYouHaveAProfileSwitch(){
        //Profile Y/n to the client when connecting to the Thread
        String answer = clientHandler.doYouHaveAProfile();

        switch (answer) {
            case "Y":
                //Client input name
                name = clientHandler.fetchName();

                //Get user from DB
                user = db.getUser(name);

                //Validate DB user_get_request
                if(user == null){
                    clientHandler.unknownUsername();
                    doYouHaveAProfileSwitch();
                }
                break;

            case "n":
                //Client new input name
                name = clientHandler.fetchName();

                //We loop the name giving of the room name until we get a uniq name
                while(true){
                    if(!db.userExistsInDB(name)){
                        break;
                    } else {
                        //Client names the new room
                        clientHandler.userAlreadyExists();
                        name = clientHandler.fetchName();
                    }
                }

                //Create timeStamp
                LocalDateTime localTime = LocalDateTime.now();

                //Crate user
                user = new User(name, localTime);

                //Create user in DB
                user = db.createUser(user);

                break;


            default:
                //Unknown input => redirect to login/create user page
                clientHandler.unknownInput();
        }
    }


    /**
     *
     * Choose or create room
     *
     */

    public ArrayList<String> showAvailableRooms(){

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

    public Room createOrEnterExistingRoom(ArrayList<String> listOfRoomNames){

        //Client enters existing room name or [Create] to make a new room
        String answer = clientHandler.doYouWantToCreateARoomOrEnterExisting();

        //We check if the client input matches the list of room names array
        int roomExists = listOfRoomNames.lastIndexOf(answer);


        if(roomExists > -1){
            //If the room exist we fetch it from the DB and return it
            room = db.getRoom(answer);
            return room;

        } else {
            //If the room doesn't exist, we check the client input for [Create]
            if (answer.equalsIgnoreCase("create")){

                //Client names the new room
                String roomName = clientHandler.whatIsTheNewRoomsName();

                //We loop the name giving of the room name until we get a uniq name
                while(true){
                    if(!db.roomExistsInDB(roomName)){
                        break;
                    } else {
                        //Client names the new room
                        clientHandler.roomAlreadyExists();
                        roomName = clientHandler.whatIsTheNewRoomsName();
                    }
                }

                //We create a room and also create the room in the DB. Thereafter we set and return the room.
                room = new Room(roomName);
                room = db.createRoom(room);
                return room;

            } else {

                clientHandler.UnknownRoomNameOrInput();
                createOrEnterExistingRoom(listOfRoomNames);
            }
        }

        return null;
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

    public void sendMessage(String s) {
        messageQueue.add(s);
    }

    public void welcomeMessage(){
        clientHandler.welcomeMessage();
    }


//    public class ClientHandler {
//        private final InputStream in;
//        private final PrintWriter out;
//
//        public ClientHandler(InputStream in, PrintWriter out) {
//            this.in = in;
//            this.out = out;
//        };
//
//        private void showPrompt() {
//            out.print("> ");
//            out.flush();
//        }
//
//        private String prompt() {
//            showPrompt();
//            return waitForLine();
//        }
//
//        private String fetchName() {
//            out.println("What's your name, man?");
//            return prompt();
//        }
//
//        private String waitForLine() {
//            return new Scanner(in).nextLine();
//        }
//
//        public boolean hasInput() throws IOException {
//            in.readAllBytes();
//            return in.available() > 0;
//        }
//    }
}