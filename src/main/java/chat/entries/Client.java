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
    private Room room;
    private User user;

    public Client(Server server, Socket socket, String name) throws IOException, ClassNotFoundException {
        this.server = server;
        this.socket = socket;
        this.clientHandler = new ClientHandler(socket.getInputStream(), new PrintWriter(socket.getOutputStream()));
        this.name = name;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.db = new DB();
        this.room = null;
        this.user = null;
    }

    public String getClientName() {
        return name;
    }


    @Override
    public void run() {
//        Thread t = new Thread(() -> {
//            while (true) {
//                clientHandler.showPrompt();
//                String line = clientHandler.waitForLine();
//                if (line.startsWith("!rename")) {
//                    name = clientHandler.fetchName();
//                    server.announceName(this);
//                } else {
//                    server.broadcast(this, line);
//                }
//            }
//        });
        try {

            //Create new user or login
            doYouHaveAProfileSwitch();

            //Welcome message to the chat
            clientHandler.welcomeMessageUser(user.getName());

            //Choose or create room
            room = createOrEnterExistingRoom();

            //Enter chat-room
            enterRoom(room);




//            t.start();

            while (true) {
                String inbound = messageQueue.take();
                clientHandler.printString("...");
                clientHandler.printString(inbound);
                clientHandler.showPrompt();
            }
        } catch (InterruptedException e) {
            System.out.println(name + " exited with: " + e.getMessage());
        } finally {
            try { close(); } catch (IOException e) {
                e.printStackTrace();
            }
//            try {
//                t.interrupt();
//                t.join(1000);
//            } catch (InterruptedException e) {
//                t.stop();
//            }
        }
    }



    /**
     * Welcome to the chat
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

                //Create timeStamp
                LocalDateTime localTime = LocalDateTime.now();

                //Crate user
                user = new User(name, localTime);

                //Validate username with DB users table => user_name
                if(db.userExistsInDB(user.getName())){

                    //Message that user already exists in DB
                    clientHandler.userExists();

                    //Redirect client to login/create user page
                    doYouHaveAProfileSwitch();
                } else{
                    //Create user in DB
                    user = db.createUser(user);
                }
                break;

            default:
                //Unknown input => redirect to login/create user page
                clientHandler.unknownInput();
        }
    }


    /**
     * Choose or create room
     */

    public Room createOrEnterExistingRoom(){

        ArrayList<String> listOfRoomNames = new ArrayList<>();
        listOfRoomNames = db.getAllRoomNames();

        if(listOfRoomNames.size() == 0 || listOfRoomNames.isEmpty()){
            clientHandler.noRoomAvailable();
        } else {
            clientHandler.hereAreTheActiveRooms();
            for(int i = 0 ; i < listOfRoomNames.size() ; i++){
                clientHandler.printRoomName(listOfRoomNames.get(i));
            }
        }

        String answer = clientHandler.doYouWantToCreateARoomOrEnterExisting();

        int roomExists = listOfRoomNames.lastIndexOf(answer);
        if(roomExists > -1){

            room = db.getRoom(answer);
            return room;

        } else {

            room = new Room(answer);
            room = db.createRoom(room);
            return room;
        }
    }

    public void enterRoom(Room room){
        Thread t = new Thread(() -> {
            while (true) {
                clientHandler.showPrompt();
                String line = clientHandler.waitForLine();
                if (line.startsWith("!rename")) {
                    name = clientHandler.fetchName();
                    server.announceName(this);
                } else {
                    server.broadcast(this, line);
                }
            }
        });
        t.start();
        try {
            t.interrupt();
            t.join(1000);
        } catch (InterruptedException e) {
            t.stop();
        }
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