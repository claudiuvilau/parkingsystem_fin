package com.parkit.parkingsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class DataBaseTestConfig extends DataBaseConfig {

	private static final Logger logger = LogManager.getLogger("DataBaseTestConfig");

	// @Override
	public Connection getConnection(String user, String mdp) throws Exception {
		logger.info("Create DB connection");
		Class.forName("com.mysql.cj.jdbc.Driver");
		// pour éviter l'erreur message The server time zone value 'Paris, Madrid (heure
		// d'été...
		String url_timezone = "?serverTimezone=" + TimeZone.getDefault().getID();

		String url = "jdbc:mysql://localhost:3306/test";
		user = "claudiu";
		mdp = "java1234*";

		return DriverManager.getConnection(url + url_timezone, user, mdp);
	}

	@Override
	public String getUser() throws Exception {
		System.out.println("Please type the user name and press enter key");
		InputReaderUtil read_user = new InputReaderUtil();
		return read_user.readUser();
	}

	@Override
	public String getPassword() throws Exception {
		System.out.println("Please type the password and press enter key");
		InputReaderUtil read_password = new InputReaderUtil();
		return read_password.readPassword();
	}

	@Override
	public void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
				logger.info("Closing DB connection");
			} catch (SQLException e) {
				logger.error("Error while closing connection", e);
			}
		}
	}

	@Override
	public void closePreparedStatement(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
				logger.info("Closing Prepared Statement");
			} catch (SQLException e) {
				logger.error("Error while closing prepared statement", e);
			}
		}
	}

	@Override
	public void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
				logger.info("Closing Result Set");
			} catch (SQLException e) {
				logger.error("Error while closing result set", e);
			}
		}
	}

}
