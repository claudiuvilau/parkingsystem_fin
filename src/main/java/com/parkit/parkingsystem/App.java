package com.parkit.parkingsystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.service.InteractiveShell;

public class App {
	private static final Logger logger = LogManager.getLogger("App");

	public static void main(String args[]) throws Exception {
		logger.info("Initializing Parking System");

		Password user_pass = new Password();
		user_pass.setUser(user_pass.password_user());
		user_pass.setPass(user_pass.password_pass());
		Password.pass_vide(user_pass.getUser(), user_pass.getPass());

		InteractiveShell.loadInterface();
	}

}
