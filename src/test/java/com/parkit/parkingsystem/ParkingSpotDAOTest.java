package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;

/**
 * @author Claudiu
 *
 */
public class ParkingSpotDAOTest {

	private static ParkingSpotDAO parkingSpotDAO;

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static TicketDAO ticketDAO;
	private static String user;
	private static String pass;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		ticketDAO = new TicketDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
	}

	@Test
	public void testGetNextAvailableSlotException() throws Exception {
		// verify if Exception so the result of the method getNextAvailableSlot() will
		// be -1
		int result = 1;
		result = parkingSpotDAO.getNextAvailableSlot(null);
		assertEquals(-1, result);
	}

	@Test
	public void testGetNextAvailableSlot() throws Exception {
		// verify if the result of the method getNextAvailableSlot() is 1 = available
		// slot
		int result = -1;
		Connection con = parkingSpotDAO.dataBaseConfig.getConnection(user, pass);
		PreparedStatement ps = con.prepareStatement("UPDATE parking SET available = true");
		ps.executeUpdate();
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);
		con.close();
		result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
		assertEquals(1, result);
	}

	@Test
	public void testGetNoNextAvailableSlot() throws Exception {
		// verify if the result of the method getNextAvailableSlot() is 0 = not
		// available slot
		int result = 1;
		Connection con = parkingSpotDAO.dataBaseConfig.getConnection(user, pass);
		PreparedStatement ps = con.prepareStatement("UPDATE parking SET available = false");
		ps.executeUpdate();
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);
		con.close();
		result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
		assertEquals(0, result);
	}

	@Test
	public void testUpdateParking() throws Exception {
		// verify if the result of the method updateParking() is true
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		boolean result = false;
		result = parkingSpotDAO.updateParking(parkingSpot);
		assertEquals(true, result);
	}

	@Test
	public void testUpdateParkingException() throws Exception {
		// verify if Exception so the result of the method updateParking() is false
		boolean result = true;
		result = parkingSpotDAO.updateParking(null);
		assertEquals(false, result);
	}

}
