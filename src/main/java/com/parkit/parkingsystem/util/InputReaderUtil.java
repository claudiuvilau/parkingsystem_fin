package com.parkit.parkingsystem.util;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InputReaderUtil {

	// private static Scanner scan = new Scanner(System.in);
	private static Scanner scan = new Scanner(System.in, "UTF-8");

	private static final Logger logger = LogManager.getLogger("InputReaderUtil");

	public int readSelection() {
		try {
			int input = Integer.parseInt(scan.nextLine());
			return input;
		} catch (Exception e) {
			logger.error("Error while reading user input from Shell", e);
			System.out.println("Error reading input. Please enter valid number for proceeding further");
			return -1;
		}
	}

	public String readVehicleRegistrationNumber() throws Exception {
		try {
			String vehicleRegNumber = scan.nextLine();
			if (vehicleRegNumber == null || vehicleRegNumber.trim().length() == 0) {
				throw new IllegalArgumentException("Invalid input provided");
			}
			return vehicleRegNumber;
		} catch (Exception e) {
			logger.error("Error while reading user input from Shell", e);
			System.out.println("Error reading input. Please enter a valid string for vehicle registration number");
			throw e;
		}
	}

	public String readUser() throws Exception {
		try {
			String user = scan.nextLine();
			if (user == null || user.trim().length() == 0) {
				throw new IllegalArgumentException("Invalid input provided");
			}
			return user;
		} catch (Exception e) {
			logger.error("Error while reading user input from Shell", e);
			System.out.println("Error reading input. Please enter a valid string for the user name");
			throw e;
		}
	}

	public String readPassword() throws Exception {
		try {
			String password = scan.nextLine();
			if (password == null || password.trim().length() == 0) {
				throw new IllegalArgumentException("Invalid input provided");
			}
			return password;
		} catch (Exception e) {
			logger.error("Error while reading user input from Shell", e);
			System.out.println("Error reading input. Please enter a valid string for the password");
			throw e;
		}
	}

}
