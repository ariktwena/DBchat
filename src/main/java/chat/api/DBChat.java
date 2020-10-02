package chat.api;

import chat.core.*;
import chat.domain.messages.MessageService;
import chat.domain.privatemessages.PrivateMessageService;
import chat.domain.room.InvalidRoomName;
import chat.domain.room.RoomAlreadyExistsInDB;
import chat.domain.room.RoomService;
import chat.domain.subscription.SubscriptionService;
import chat.domain.user.*;
import chat.infrastructure.DB;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DBChat {

    private final UserService usersFromSource;
    private final RoomService roomsFromSource;
    private final MessageService messagesFromSource;
    private final PrivateMessageService privateMessagesFromSource;
    private final SubscriptionService subscriptionsFromSource;
//    private final DB db;
    private volatile Set<User> activeUsers = new HashSet<>();

    public DBChat(UserService users, RoomService rooms, MessageService messages, PrivateMessageService privateMessages, SubscriptionService subscriptions) {
        this.usersFromSource = users;
        this.roomsFromSource = rooms;
        this.messagesFromSource = messages;
        this.privateMessagesFromSource = privateMessages;
        this.subscriptionsFromSource = subscriptions;
    }

//    public DBChat(DB db) {
//        this.db = db;
//    }


    public synchronized User createUserInSystemAndDB(String name, String password1, String password2, LocalDateTime date) throws InvalidPassword, UserExists {
        //Generate salt
        byte[] salt = User.generateSalt();
        //Generate secret
        byte[] secret = User.calculateSecret(salt, password1);
        //Create user

        //Validate user input
        if(usersFromSource.userAldreadyExistsInDB(name)){
            throw new UserExists();

        } else if (!password1.equals(password2)){
            throw new InvalidPassword();

        } else {
            //Create user
            User user = new User(name, date, salt, secret);

            //Save/create the user in the DB and return the users (No longer id -1)
            user = usersFromSource.createUser(user);

            //Add user to hashMap
            activeUsers.add(user);

            //Log the user to the DB
            usersFromSource.userLogin(user);
            usersFromSource.userOnline(user);

            return user;
        }

    }

    public synchronized User loginValidation(String name, String password) throws NullPointerException, UserAlreadyLoggedIn, InvalidUsernameOrPassword {
        //Get user from the DB with a specific name
        User user = usersFromSource.getUser(name);


        //Username is null => no user was found in DB
        if(user.getName() == null){
            throw new NullPointerException();
        }
        //if user is already logged in
        else if(userAlreadyLoggedIn(user)){

            throw new UserAlreadyLoggedIn();

        }
        //Validate the DB password with the provided one
        else if (!user.isPasswordCorrect(password)) {

            throw new InvalidUsernameOrPassword();

        } else  {

            //Add user to hashMap
            activeUsers.add(user);

            //Login the user and set status to online
            usersFromSource.userLogin(user);
            usersFromSource.userOnline(user);

            //Return user if password is validated
            return user;
        }
    }

    private synchronized boolean userAlreadyLoggedIn(User user){
//        if(activeUsers.contains(user)){
//            return true;
//        }
//        return false;
        return activeUsers.contains(user);
    }

    public synchronized void removeUserFromActiveUsers(User user){
//        for (User curretUser : activeUsers){
//            if(curretUser.equals(user)){
//                activeUsers.remove(user);
//            }
//        }
        activeUsers.remove(user);
    }

    public synchronized void setOnlineStamp(User user){
        usersFromSource.userOnline(user);
    }

    public synchronized Room getRoomFromDB(String roomName){

        return roomsFromSource.getRoom(roomName);

    }

    public synchronized Room createRoomInSystemAndDB(String roomName) throws RoomAlreadyExistsInDB, InvalidRoomName {

        if(roomExistsInDB(roomName)){

            throw new RoomAlreadyExistsInDB();

        } else if(roomName.equalsIgnoreCase("create")){

            throw new InvalidRoomName();

        } else {

            //We create a room and also create the room in the DB. Thereafter we set and return the room.
            Room room = new Room(roomName);
            room = roomsFromSource.createRoom(room);

            return room;

        }

    }

    private synchronized boolean roomExistsInDB (String roomName){
        if(!roomsFromSource.roomExistsInDB(roomName)){
            return false;
        }
        return true;
    }

    public synchronized ArrayList<String> getListOfAllSubscribingUsersFromARoom(Subscription subscriptionToRoom){

        ArrayList<String> subscriptionUserNames = subscriptionsFromSource.getAllSubscribingUsersFromARoom(subscriptionToRoom);

        return subscriptionUserNames;
    }

    public synchronized ArrayList<String> getAvailableRooNames(){
        return roomsFromSource.getAllRoomNames();
    }

    public synchronized Message createMessage (String messageInput, User user, Room room){

        //Create timestamp
        LocalDateTime localTime = LocalDateTime.now();

        //Create message
        Message message = new Message(messageInput, localTime, user, room);

        //Save message to DB
        message = messagesFromSource.createMessage(message);

        return message;

    }

    public synchronized Private_Message createPrivateMessage (String thePrivateMessage, User user, Room room, String whoToSendTo){

        //Create timestamp
        LocalDateTime localTime = LocalDateTime.now();

        //Create message
        Private_Message privateMessage = new Private_Message(thePrivateMessage, localTime, user, room, whoToSendTo);

        //Get recipient id
        int recipient_id = usersFromSource.getUserId(whoToSendTo);

        //Save message to DB
        privateMessage = privateMessagesFromSource.createPrivateMessage(privateMessage, recipient_id);

        return privateMessage;

    }

    public synchronized ArrayList<String> getThe10LastRoomMessages (Room room, User user){
        return messagesFromSource.getThe10LastRoomMessages(room, user);
    }

    public synchronized ArrayList<String> get10NumberOfRoomPrivateMessages (Room room, User user){
        return privateMessagesFromSource.get10NumberOfRoomPrivateMessages(room, user);
    }

    public synchronized Subscription createSubscription(User user, Room room){

        //Create timestamp
        LocalDateTime localTime = LocalDateTime.now();

        //Create new subscription
        Subscription subscription = new Subscription(localTime, user, room);

        //Add subscription to DB
        subscription = subscriptionsFromSource.createSubscriptionForRoom(subscription);

        return subscription;
    }



}
