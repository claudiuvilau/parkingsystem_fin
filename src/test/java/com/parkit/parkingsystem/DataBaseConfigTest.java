package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.util.InputReaderUtil;

/**
 * @author Claudiu
 *
 */
@ExtendWith(MockitoExtension.class)
public class DataBaseConfigTest {

	private static DataBaseConfig dataBaseConfig = new DataBaseConfig();
	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static Connection con;
	private static String user;
	private static String mdp;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeEach
	private void open() throws Exception {
		con = dataBaseTestConfig.getConnection(user, mdp);
	}

	@AfterEach
	private void close() throws Exception {
		con.close();
	}

	@Test
	public void testCloseConnection() throws SQLException {
		dataBaseConfig.closeConnection(con);
		assertEquals(true, con.isClosed());
	}

	@Test
	public void testclosePreparedStatement() throws SQLException {
		PreparedStatement ps = con.prepareStatement("SELECT 1");
		dataBaseConfig.closePreparedStatement(ps);
		assertEquals(true, ps.isClosed());
	}

	@Test
	public void testcloseResultSet() throws SQLException {
		PreparedStatement ps = con.prepareStatement("SELECT 1");
		ResultSet rs = ps.executeQuery();
		dataBaseConfig.closePreparedStatement(ps);
		dataBaseConfig.closeResultSet(rs);
		assertEquals(true, rs.isClosed());
	}

}
