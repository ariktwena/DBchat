package chat.entries;

import chat.api.DBChat;
import chat.core.Message;
import chat.core.Private_Message;
import chat.core.Room;
import chat.infrastructure.DB;

import java.io.IOException;
import java.net.ServerSocket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private final ServerSocket socket;
    private final List<Client> clients;
    private final DBChat api = new DBChat(new DB());

    //Constructor with full information
    public Server(ServerSocket socket, List<Client> clients) {
        this.socket = socket;
        this.clients = clients;
    }
    //Constructor with minimum required information
    public Server(ServerSocket socket) {
        this(socket, new ArrayList<>());
    }

    @Override
    public void run() {
        System.out.println("Listing for clients at: " + socket);

        //Listen for new connections to the server
        try {
            while (true) {
                //Create new client if the is a connection to the server
                Client client = new Client(this, socket.accept(), "anonymous", api);

                //Add the clien to the clients array
                addClient(client);

                //Start the client of a individual socket connection
                client.start();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        //Create a server on a specific port (1234)
        Server server = new Server(new ServerSocket(1234));

        //Start the server
        server.start();

        System.out.println("Server Started!");
    }

    public synchronized void addClient(Client client) {
        System.out.println("Accepted client: " + client);
        clients.add(client);
    }

    public synchronized void removeClient(Client client) {
        System.out.println("Closed client: " + client);
        clients.remove(client);
    }

    //Search if client/user is logged in already
    public synchronized boolean isClientAlreadyLoggedIn(String userName){
        int count = 0;

        for (Client c : clients) {
            if(c.getUserName().equalsIgnoreCase(userName)){
                count++;
            }
        }

        if(count > 1){
            return true;
        } else {
            return false;
        }
    }

    //Broadcast messages to other clients
    public synchronized void broadcast(Message message) throws ParseException {
        for (Client c : clients) {

            if(c.getRoom() != null){
                if (c.getRoom().getName().equalsIgnoreCase(message.getRoom().getName())){

                    String timeFormat;

                    if(message.getDate().getMinute() < 10){
                        timeFormat = message.getDate().getHour() + ":0" + message.getDate().getMinute();
                    } else {
                        timeFormat = message.getDate().getHour() + ":" + message.getDate().getMinute();
                    }

                    String broadcastMessage = String.format("%-5s %s %s",
                            timeFormat,
                            message.getUser().getName() + ":",
                            message.getContent());

                    c.sendMessage(broadcastMessage);

                }
            }

        }
    }

    //Broadcast private messages to a single client
    public synchronized void privateBroadcast(Private_Message privateMessage) throws ParseException {
        for (Client c : clients) {

            if(c.getRoom() != null){
                if (c.getRoom().getName().equalsIgnoreCase(privateMessage.getRoom().getName())
                        &&
                        c.getUserName().equals(privateMessage.getUserTo())){

                    String timeFormat;

                    if(privateMessage.getDate().getMinute() < 10){
                        timeFormat = privateMessage.getDate().getHour() + ":0" + privateMessage.getDate().getMinute();
                    } else {
                        timeFormat = privateMessage.getDate().getHour() + ":" + privateMessage.getDate().getMinute();
                    }

                    String broadcastMessage = String.format("%-5s %s %s",
                            timeFormat,
                            privateMessage.getUser().getName() + ":",
                            "<private> " + privateMessage.getContent());

                    c.sendMessage(broadcastMessage);

                }
            }

        }
    }

    //Announce the user/client name in the chat room when entering
    public synchronized void announceName(Client from, Room room) {

        for (Client c : clients) {

            if(c.getRoom() != null){
                if (c.getRoom().getName().equalsIgnoreCase(room.getName())){

                    c.sendMessage(from.getUserName() + " joined the chat!");
                }
            }
        }
    }

    //Announce the user/client name in the chat room when entering
    public synchronized void announceExitChat(Client from, Room room) {

        for (Client c : clients) {

            if(c.getRoom() != null){
                if (c.getRoom().getName().equalsIgnoreCase(room.getName())){

                    c.sendMessage(from.getUserName() + " left the chat!");
                }
            }
        }
    }
}