package chat.entries;

import chat.core.User;
import chat.domain.*;
import chat.infrastructure.DB;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
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
    private final UserRepo userFactory;
    private final SubscriptionRepo subscriptionFactory;
    private final RoomRepo roomFactory;
    private final MessageRepo messageFactory;
    private String room;
    private User user;

    public Client(Server server, Socket socket, String name) throws IOException, ClassNotFoundException {
        this.server = server;
        this.socket = socket;
        this.clientHandler = new ClientHandler(socket.getInputStream(), new PrintWriter(socket.getOutputStream()));
        this.name = name;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.db = new DB();
        this.userFactory = new UserFactory(db);
        this.subscriptionFactory = new SubscriptionFactory(db);
        this.roomFactory = new RoomFactory(db);
        this.messageFactory = new MessageFactory(db);
        this.room = null;
        this.user = null;
    }

    public String getClientName() {
        return name;
    }


    @Override
    public void run() {
        Thread t = new Thread(() -> {
            while (true) {
                clientHandler.showPrompt();
                String line = clientHandler.waitForLine();
                if (line.startsWith("!rename")) {
                    String previousName = name;
                    name = clientHandler.fetchName();
                    server.announceName(this, previousName);
                } else {
                    server.broadcast(this, line);
                }
            }
        });
        try {
            doYouHaveAProfileSwitch();
            clientHandler.welcomeMessageUser(user.getName());



//            server.announceName(this, previousName);
            t.start();

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
            try {
                t.interrupt();
                t.join(1000);
            } catch (InterruptedException e) {
                t.stop();
            }
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

    public void doYouHaveAProfileSwitch(){
        String answer = clientHandler.doYouHaveAProfile();
        String previousName = name;

        switch (answer) {
            case "Y":
                previousName = name;
                name = clientHandler.fetchName();

                user = userFactory.getUser(name);

                if(user == null){
                    clientHandler.unknownUsername();
                    doYouHaveAProfileSwitch();
                }

                break;
            case "n":
                previousName = name;
                name = clientHandler.fetchName();
                Long date = new Date().getTime();

                user = new User(name);

                if(userFactory.userExistsInDB(user.getName())){
                    clientHandler.userExists();
                    doYouHaveAProfileSwitch();
                } else{
                    user = userFactory.createUser(user);
                }

                break;
            default:
                clientHandler.unknownInput();
        }
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