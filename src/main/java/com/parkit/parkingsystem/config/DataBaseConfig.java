package com.parkit.parkingsystem.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.util.InputReaderUtil;

public class DataBaseConfig {

	private static final Logger logger = LogManager.getLogger("DataBaseConfig");

	public Connection getConnection() throws Exception {
		logger.info("Create DB connection");
		Class.forName("com.mysql.cj.jdbc.Driver");
		// pour éviter l'erreur message The server time zone value 'Paris, Madrid (heure
		// d'été...
		String url_timezone = "?serverTimezone=" + TimeZone.getDefault().getID();

		String url = "jdbc:mysql://localhost:3306/prod";
		String user = "claudiu";
		String mdp = getPassword();

		return DriverManager.getConnection(url + url_timezone, user, mdp);
	}
	
	public String getPassword() throws Exception {
		System.out.println("Please type the password and press enter key");
		InputReaderUtil read_password = new InputReaderUtil();
		return read_password.readPassword();
	}

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
