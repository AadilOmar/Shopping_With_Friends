package edu.gatech.shoppingwithfriends;

/**
 * Created by Decker on 2/9/2015.
 */
public class FirebaseSingleton {

    private static FirebaseSingleton singleton = new FirebaseSingleton( );

    private static String userauthurl = "https://flickering-torch-2137.firebaseio.com/";

    private static String userdataurl = "https://flickering-torch-2137.firebaseio.com/android/users";

    private FirebaseSingleton(){

    }

    public static FirebaseSingleton getInstance( ) {
        return singleton;
    }

    public static String getUserAuthURL() {
        return userauthurl;
    }

    public static String getUserDataURL() {
        return userdataurl;
    }

}