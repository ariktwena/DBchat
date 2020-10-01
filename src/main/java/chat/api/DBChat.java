package chat.api;

import chat.core.Room;
import chat.core.Subscription;
import chat.core.User;
import chat.domain.room.InvalidRoomName;
import chat.domain.room.RoomAlreadyExistsInDB;
import chat.domain.user.*;
import chat.infrastructure.DB;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DBChat {

//    private final UserService users;
//    private final RoomService rooms;
//    private final MessageService messages;
//    private final PrivateMessageService privateMessages;
//    private final SubscriptionService subscriptions;
    private final DB db;
    private volatile Set<User> activeUsers = new HashSet<>();

//    public DBChat(UserRepo users, RoomService rooms, MessageService messages, PrivateMessageService privateMessages, SubscriptionService subscriptions, DB db) {
//        this.users = users;
//        this.rooms = rooms;
//        this.messages = messages;
//        this.privateMessages = privateMessages;
//        this.subscriptions = subscriptions;
//        this.db = db;
//    }

    public DBChat(DB db) {
        this.db = db;
    }


    public synchronized User createUserInSystemAndDB(String name, String password1, String password2, LocalDateTime date) throws InvalidPassword, UserExists {
        //Generate salt
        byte[] salt = User.generateSalt();
        //Generate secret
        byte[] secret = User.calculateSecret(salt, password1);
        //Create user

        //Validate user input
        if(db.userAldreadyExistsInDB(name)){
            throw new UserExists();

        } else if (!password1.equals(password2)){
            throw new InvalidPassword();

        } else {
            //Create user
            User user = new User(name, date, salt, secret);

            //Save/create the user in the DB and return the users (No longer id -1)
            user = db.createUser(user);

            //Add user to hashMap
            activeUsers.add(user);

            //Log the user to the DB
            db.userLogin(user);
            db.userOnline(user);

            return user;
        }

    }

    public synchronized User loginValidation(String name, String password) throws NullPointerException, UserAlreadyLoggedIn, InvalidUsernameOrPassword {
        //Get user from the DB with a specific name
        User user = db.getUser(name);


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
            db.userLogin(user);
            db.userOnline(user);

            //Return user if password is validated
            return user;
        }
    }

    private synchronized boolean userAlreadyLoggedIn(User user){
        for (User curretUser : activeUsers){
            if(curretUser.equals(user)){
                return true;
            }
        }
        return false;
    }


    public synchronized Room getRoomFromDB(String roomName){

        return db.getRoom(roomName);

    }

    public synchronized Room createRoomInSystemAndDB(String roomName) throws RoomAlreadyExistsInDB, InvalidRoomName {

        if(roomExistsInDB(roomName)){

            throw new RoomAlreadyExistsInDB();

        } else if(roomName.equalsIgnoreCase("create")){

            throw new InvalidRoomName();

        } else {

            //We create a room and also create the room in the DB. Thereafter we set and return the room.
            Room room = new Room(roomName);
            room = db.createRoom(room);

            return room;

        }

    }

    private synchronized boolean roomExistsInDB (String roomName){
        if(!db.roomExistsInDB(roomName)){
            return false;
        }
        return true;
    }

    public synchronized ArrayList<String> getListOfAllSubscribingUsersFromARoom(Subscription subscriptionToRoom){

        ArrayList<String> subscriptionUserNames = db.getAllSubscribingUsersFromARoom(subscriptionToRoom);

        return subscriptionUserNames;
    }


}
