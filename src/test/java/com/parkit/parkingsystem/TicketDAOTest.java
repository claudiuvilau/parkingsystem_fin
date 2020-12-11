package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.sql.SQLException;
import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

/**
 * @author Claudiu
 *
 */
public class TicketDAOTest {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		ticketDAO = new TicketDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
	}

	@Test
	public void testSaveTicketDAOException() throws ClassNotFoundException, SQLException {
		// verify if the method saveTicket() return false
		Ticket ticket = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setParkingSpot(parkingSpot);
		Date outTime = new Date();
		ticket.setOutTime(outTime); // if OutTime => Exception err and return false
		boolean is_saved;
		is_saved = ticketDAO.saveTicket(ticket);
		assertEquals(false, is_saved);
	}

	@Test
	public void testSaveTicketDAO() throws ClassNotFoundException, SQLException {
		// verify if the method saveTicket() return true
		Ticket ticket = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setParkingSpot(parkingSpot);
		ticket.setId(1);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setPrice(0);
		Date inTime = new Date();
		ticket.setInTime(inTime);
		ticket.setOutTime(null);
		boolean is_saved;
		is_saved = ticketDAO.saveTicket(ticket);
		assertEquals(true, is_saved);
	}

	@Test
	public void testGetTicketDAO() {
		// verify if the getTicket() is not null
		Ticket ticket = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setParkingSpot(parkingSpot);
		ticket.setId(1);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setPrice(0);
		Date inTime = new Date();
		ticket.setInTime(inTime);
		ticket.setOutTime(inTime);
		Ticket is_getticket = null;
		is_getticket = ticketDAO.getTicket("ABCDEF");
		assertNotEquals(null, is_getticket);
	}

	@Test
	public void testGetTicketDAOBranche2() {
		// no vehicle, so rs.next() = false; getTicket is null
		Ticket ticket = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		Ticket is_getticket = null;
		is_getticket = ticketDAO.getTicket("");
		assertEquals(null, is_getticket);
	}

	@Test
	public void testUpdateTicketDAO() {
		// verify if the method updateTicket() is true
		Ticket ticket = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setParkingSpot(parkingSpot);
		ticket.setId(1);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setPrice(0);
		Date inTime = new Date();
		ticket.setInTime(inTime);
		ticket.setOutTime(inTime);
		boolean update = false;
		update = ticketDAO.updateTicket(ticket);
		assertEquals(true, update);
	}

	@Test
	public void testUpdateTicketDAOException() {
		// verify if the method updateTicket() is false
		Ticket ticket = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setParkingSpot(parkingSpot);
		ticket.setId(1);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setPrice(0);
		Date inTime = new Date();
		ticket.setInTime(inTime);
		ticket.setOutTime(null);
		boolean update = false;
		update = ticketDAO.updateTicket(ticket);
		assertEquals(false, update);
	}
}
