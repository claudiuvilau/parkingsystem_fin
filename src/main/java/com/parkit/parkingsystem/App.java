package com.parkit.parkingsystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.service.InteractiveShell;

public class App {
    private static final Logger logger = LogManager.getLogger("App");
    public static String user;
    public static String pass;
        
    public static void main(String args[]) throws Exception{
        logger.info("Initializing Parking System");   
        App setpass = new App();
        setpass.setpass();
        InteractiveShell.loadInterface();
    }

    public static void pass_vide(String user_user, String pass_pass) {
    	App.user = user_user;
    	App.pass = pass_pass;
    }

    public void setpass() throws Exception {
    	Password user_pass = new Password();
    	pass_vide(user_pass.password_user(), user_pass.password_pass());        
    }
}
