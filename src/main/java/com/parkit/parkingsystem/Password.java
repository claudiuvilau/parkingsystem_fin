package com.parkit.parkingsystem;

import com.parkit.parkingsystem.config.DataBaseConfig;

/**
 * @author Claudiu
 *
 */
public class Password {

	DataBaseConfig db_con = new DataBaseConfig();

	// here we have the user and the password for the opening of the BD
	public static final Password password = new Password();

	private String user;
	private String pass;

	public String password_user() throws Exception {
		String user;
		user = db_con.getUser();
		return user;
	}

	public String password_pass() throws Exception {
		String pass;
		pass = db_con.getPassword();
		return pass;
	}

	/**
	 * @param user_user this is the user of the MySql
	 * @param pass_pass this is the password of the MySql
	 */
	public static void pass_vide(String user_user, String pass_pass) {
		password.user = user_user;
		password.pass = pass_pass;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String newUser) {
		this.user = newUser;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String newPass) {
		this.pass = newPass;
	}
}
