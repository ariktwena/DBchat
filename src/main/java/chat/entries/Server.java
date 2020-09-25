package chat.entries;

import chat.core.Room;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private final ServerSocket socket;
    private final List<Client> clients;

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
                Client client = new Client(this, socket.accept(), "anonymous");

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

    public synchronized void broadcast(Client from, Room room, String msg) {
        for (Client c : clients) {

            if(c.getRoom() != null){
                if (c.getRoom().getName().equalsIgnoreCase(room.getName())){

                    c.sendMessage(from.getClientName() + ": " +  msg);
                }
            }

        }
    }

    public synchronized void announceName(Client from, Room room) {

        for (Client c : clients) {

            if(c.getRoom() != null){
                if (c.getRoom().getName().equalsIgnoreCase(room.getName())){

                    c.sendMessage(from.getClientName() + " joined the chat!");
                }
            }
        }
    }

    public synchronized void announcExitChat(Client from, Room room) {

        for (Client c : clients) {

            if(c.getRoom() != null){
                if (c.getRoom().getName().equalsIgnoreCase(room.getName())){

                    c.sendMessage(from.getClientName() + " left the chat!");
                }
            }
        }
    }
}