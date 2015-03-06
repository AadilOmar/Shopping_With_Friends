package edu.gatech.shoppingwithfriends;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Decker on 2/9/2015.
 */
public class CurrentUser {

    private static CurrentUser user = new CurrentUser();

    private static String email;
    private static String username;
    private static String fName;
    private static String lName;

    private static boolean loggedin;


    private static ArrayList<String> friendReferences = new ArrayList<String>();
    private static ArrayList<String> friends = new ArrayList<String>(); //emails
    private static ArrayList<String> friendNames = new ArrayList<String>(); //names

    public static void updateUser(String _email, String _username, String _fName, String _lName) {
        email = _email;
        username = _username;
        fName = _fName;
        lName = _lName;
    }

    public static CurrentUser getInstance() {
        return user;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        CurrentUser.email = email;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        CurrentUser.username = username;
    }

    public static String getfName() {
        return fName;
    }

    public static void setfName(String fName) {
        CurrentUser.fName = fName;
    }

    public static String getlName() {
        return lName;
    }

    public static void setlName(String lName) {
        CurrentUser.lName = lName;
    }

    public static ArrayList<String> getFriends() {
        return friends;
    }

    public static void setFriends(ArrayList<String> friends) {
        CurrentUser.friends = friends;
    }

    public static String addFriend(String frEmail, String fName, String lName) {
        friends.add(frEmail);
        String friendname = fName + " " + lName;
        friendNames.add(friendname);
        return friendname;
    }

    public static String removeFriend(String frEmail) {
        int ndx = friends.indexOf(frEmail);
        String name = friends.get(ndx);
        friends.remove(frEmail);
        friendNames.remove(ndx);
        return name;
    }

    public static ArrayList<String> getFriendNames() {
        return friendNames;
    }

    public static void setFriendNames(ArrayList<String> friendNames) {
        CurrentUser.friendNames = friendNames;
    }

    public static boolean isLoggedIn() {
        return loggedin;
    }

    public static void setLoggedIn(boolean loggedin) {
        CurrentUser.loggedin = loggedin;
    }

    public static void logout() {
        username = null;
        email = null;
        fName = null;
        lName = null;
        friends.clear();
        friendNames.clear();
        loggedin = false;
    }

}
