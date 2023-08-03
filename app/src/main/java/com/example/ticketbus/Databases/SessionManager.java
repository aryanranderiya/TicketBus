package com.example.ticketbus.Databases;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences usersSession;
    SharedPreferences.Editor editor;
    Context context;

    public static final String SESSION_USERSESSION = "userLoginSession";
    public static final String SESSION_REMEMBERME = "rememberMe";

    //User Session Variables
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONE = "phone";


    //Remember Me Variables
    private static final String IS_REMEMBERME = "IsRememberMe";
    public static final String KEY_SESSIONEMAIL = "email";
    public static final String KEY_SESSIONPASSWORD = "password";


    public SessionManager(Context _context, String sessionName) {

        context = _context;
        usersSession = context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = usersSession.edit();
    }

    public void createLoginSession(String name, String email, String password, String phone) {

        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_PHONE, phone);

        editor.commit();
    }

    public void createLoginSessionWithEmailPassword(String email, String password) {

        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);

        editor.commit();
    }

    public void createRememberMeSessionWithEmailPassword(String email, String password) {

        editor.putBoolean(IS_REMEMBERME, true);

        editor.putString(KEY_SESSIONEMAIL, email);
        editor.putString(KEY_SESSIONPASSWORD, password);

        editor.commit();
    }

    public HashMap<String, String> getUsersDetailFromSession() {

        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_NAME, usersSession.getString(KEY_NAME, null));
        userData.put(KEY_EMAIL, usersSession.getString(KEY_EMAIL, null));
        userData.put(KEY_PASSWORD, usersSession.getString(KEY_PASSWORD, null));
        userData.put(KEY_PHONE, usersSession.getString(KEY_PHONE, null));

        return userData;
    }

    public HashMap<String, String> getUserEmailPasswordFromSession() {

        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_EMAIL, usersSession.getString(KEY_EMAIL, null));
        userData.put(KEY_PASSWORD, usersSession.getString(KEY_PASSWORD, null));

        return userData;
    }

    public HashMap<String, String> getRememberMeEmailPasswordFromSession() {

        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_SESSIONEMAIL, usersSession.getString(KEY_SESSIONEMAIL, null));
        userData.put(KEY_SESSIONPASSWORD, usersSession.getString(KEY_SESSIONPASSWORD, null));

        return userData;
    }

    public boolean checkLogin() {

        if(usersSession.getBoolean(IS_LOGIN, false  )) {
            return true;
        }
        else
            return false;
    }

    public boolean checkRememberMe() {

        if(usersSession.getBoolean(IS_REMEMBERME, false)) {
            return true;
        }
        else
            return false;
    }

    public void logoutUserFromSession(Context _context, String sessionName) {
        context = _context;
        usersSession = context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = usersSession.edit();
        editor.clear();
        editor.commit();
    }

}
