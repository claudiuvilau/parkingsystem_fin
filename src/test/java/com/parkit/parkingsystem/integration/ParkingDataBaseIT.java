package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.DataBaseTestConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

/**
 * @author Claudiu
 *
 */
@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;
	public static String user;
	public static String pass;
	public static Connection con;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
		con = parkingSpotDAO.dataBaseConfig.getConnection(user, pass);
	}

	@AfterAll
	private static void tearDown() throws SQLException {
		con.close();

	}

	@Test
	public void testParkingACar() throws Exception {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		// TODO: check that a ticket is actually saved in DB and Parking table is
		// updated with availability

		String vehicleRegNumber = inputReaderUtil.readVehicleRegistrationNumber();

		PreparedStatement ps = con
				.prepareStatement("SELECT t.PARKING_NUMBER, p.available, t.VEHICLE_REG_NUMBER FROM ticket t,parking p "
						+ "WHERE p.available = false and isnull(t.out_time) and p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=?");
		ps.setString(1, vehicleRegNumber);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			assertThat(rs.getString(3)).isEqualTo(vehicleRegNumber);
		} else {
			System.out.println("There are not results in the query ! For the test we need the results.");
			assertThat(rs.getString(3)).isEqualTo(vehicleRegNumber);
		}
		parkingSpotDAO.dataBaseConfig.closeResultSet(rs);
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);
	}

	@Test
	public void testParkingLotExit() throws Exception {
		// TODO: check that the fare generated and out time are populated correctly in
		// the database

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		PreparedStatement ps;
		String vehicleRegNumber = inputReaderUtil.readVehicleRegistrationNumber();

		parkingService.processIncomingVehicle();

		// set IN time 1 hour less for calculate the fare for 1 hour or more, if not the
		// time is less 30min so fare 0
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (90 * 60 * 1000));
		ps = con.prepareStatement("UPDATE ticket SET IN_TIME=? WHERE isnull(out_time) and VEHICLE_REG_NUMBER=?");
		ps.setTimestamp(1, new Timestamp(inTime.getTime()));
		ps.setString(2, vehicleRegNumber);
		ps.execute();
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);

		parkingService.processExitingVehicle();

		ps = con.prepareStatement(
				"SELECT p.available, t.PARKING_NUMBER, t.VEHICLE_REG_NUMBER, p.TYPE, t.PRICE, t.IN_TIME, t.OUT_TIME FROM ticket t,parking p "
						+ "WHERE p.available = true and p.parking_number = t.parking_number and t.price > 0 and t.out_time is not null and t.VEHICLE_REG_NUMBER=?");
		ps.setString(1, vehicleRegNumber);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			assertThat(rs.getString(3)).isEqualTo(vehicleRegNumber);
		} else {
			System.out.println("There are not results in the query ! For the test we need the results.");
			assertThat(rs.getString(3)).isEqualTo(vehicleRegNumber);
		}
		parkingSpotDAO.dataBaseConfig.closeResultSet(rs);
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);
	}

	@Test
	public void testParkingBike() throws Exception {

		when(inputReaderUtil.readSelection()).thenReturn(2); // selection a BIKE

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		PreparedStatement ps;
		String vehicleRegNumber = inputReaderUtil.readVehicleRegistrationNumber();

		parkingService.processIncomingVehicle();

		ps = con.prepareStatement(
				"SELECT p.available, p.PARKING_NUMBER, t.PARKING_NUMBER, t.VEHICLE_REG_NUMBER, p.TYPE, t.PRICE, t.IN_TIME, t.OUT_TIME FROM ticket t,parking p WHERE p.available = false and p.parking_number = t.parking_number and isnull(t.out_time) and t.VEHICLE_REG_NUMBER=?");
		ps.setString(1, vehicleRegNumber);
		ResultSet rs = ps.executeQuery();
		assertTrue(rs.next());
		assertThat(rs.getString(1)).isEqualTo("0");
		assertEquals(rs.getString(2), rs.getString(3));
		assertThat(rs.getString(5)).isEqualTo("BIKE");
		parkingSpotDAO.dataBaseConfig.closeResultSet(rs);
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);
	}

	@Test
	public void testParkingCar() throws Exception {

		// selection a CAR to the @BeforeEach

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		PreparedStatement ps;
		String vehicleRegNumber = inputReaderUtil.readVehicleRegistrationNumber();

		parkingService.processIncomingVehicle();

		ps = con.prepareStatement(
				"SELECT p.available, p.PARKING_NUMBER, t.PARKING_NUMBER, t.VEHICLE_REG_NUMBER, p.TYPE, t.PRICE, t.IN_TIME, t.OUT_TIME FROM ticket t,parking p WHERE p.available = false and p.parking_number = t.parking_number and isnull(t.out_time) and t.VEHICLE_REG_NUMBER=?");
		ps.setString(1, vehicleRegNumber);
		ResultSet rs = ps.executeQuery();
		assertTrue(rs.next());
		assertThat(rs.getString(1)).isEqualTo("0");
		assertEquals(rs.getString(2), rs.getString(3));
		assertThat(rs.getString(5)).isEqualTo("CAR");
		parkingSpotDAO.dataBaseConfig.closeResultSet(rs);
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);
	}

	@Test
	public void testParkingRecurringBike() throws Exception {

		when(inputReaderUtil.readSelection()).thenReturn(2); // selection a BIKE

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		PreparedStatement ps;
		String vehicleRegNumber = inputReaderUtil.readVehicleRegistrationNumber();

		Date inTime = new Date();

		parkingService.processIncomingVehicle();
		inTime.setTime(System.currentTimeMillis() - (90 * 60 * 1000)); // 90 instead of 60 because 30 min is free
		ps = con.prepareStatement("UPDATE ticket SET IN_TIME=? WHERE isnull(out_time) and VEHICLE_REG_NUMBER=?");
		ps.setTimestamp(1, new Timestamp(inTime.getTime()));
		ps.setString(2, vehicleRegNumber);
		ps.execute();
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);

		parkingService.processExitingVehicle();

		parkingService.processIncomingVehicle();
		inTime.setTime(System.currentTimeMillis() - (90 * 60 * 1000));
		ps = con.prepareStatement("UPDATE ticket SET IN_TIME=? WHERE isnull(out_time) and VEHICLE_REG_NUMBER=?");
		ps.setTimestamp(1, new Timestamp(inTime.getTime()));
		ps.setString(2, vehicleRegNumber);
		ps.execute();
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);

		parkingService.processExitingVehicle();

		ps = con.prepareStatement(
				"SELECT PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME FROM ticket WHERE price > 0 and VEHICLE_REG_NUMBER=? order by IN_TIME");
		ps.setString(1, vehicleRegNumber);
		ResultSet rs = ps.executeQuery();

		double disc = 0.05;
		Ticket ticket = new Ticket();
		FareCalculatorService fareCalculatorService = new FareCalculatorService();
		ParkingSpot parkingSpot = new ParkingSpot(5, ParkingType.BIKE, true);
		ticket.setParkingSpot(parkingSpot);
		int occ = 2;// the discount begin to the 2 occurrences incoming vehicle so occ = 2
		if (rs.absolute(occ)) {
			rs.previous(); // we stay to the row before the discount to verify the next rows if there are
							// the discount
		}
		while (rs.next()) {
			if (rs.getDouble(3) > 0) {
				ticket.setInTime(rs.getTimestamp(4));
				ticket.setOutTime(rs.getTimestamp(5));
				fareCalculatorService.calculateFare(ticket);
				double price_with_disc = Math.round((ticket.getPrice() - (ticket.getPrice() * disc)) * 100.0) / 100.0;
				assertThat(rs.getDouble(3)).isEqualTo(price_with_disc);
			}
		}
		parkingSpotDAO.dataBaseConfig.closeResultSet(rs);
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);
	}

	@Test
	public void testParkingRecurringCar() throws Exception {

		// selection a CAR in the @BeforeEach

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		PreparedStatement ps;
		String vehicleRegNumber = inputReaderUtil.readVehicleRegistrationNumber();

		Date inTime = new Date();

		parkingService.processIncomingVehicle();
		inTime.setTime(System.currentTimeMillis() - (90 * 60 * 1000)); // 90 instead of 60 because 30 min is free
		ps = con.prepareStatement("UPDATE ticket SET IN_TIME=? WHERE isnull(out_time) and VEHICLE_REG_NUMBER=?");
		ps.setTimestamp(1, new Timestamp(inTime.getTime()));
		ps.setString(2, vehicleRegNumber);
		ps.execute();
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);

		parkingService.processExitingVehicle();

		parkingService.processIncomingVehicle();
		inTime.setTime(System.currentTimeMillis() - (90 * 60 * 1000));
		ps = con.prepareStatement("UPDATE ticket SET IN_TIME=? WHERE isnull(out_time) and VEHICLE_REG_NUMBER=?");
		ps.setTimestamp(1, new Timestamp(inTime.getTime()));
		ps.setString(2, vehicleRegNumber);
		ps.execute();
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);

		parkingService.processExitingVehicle();

		ps = con.prepareStatement(
				"SELECT PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME FROM ticket WHERE price > 0 and VEHICLE_REG_NUMBER=? order by IN_TIME");
		ps.setString(1, vehicleRegNumber);
		ResultSet rs = ps.executeQuery();

		double disc = 0.05;
		Ticket ticket = new Ticket();
		FareCalculatorService fareCalculatorService = new FareCalculatorService();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
		ticket.setParkingSpot(parkingSpot);
		int occ = 2; // the discount begin to the 2 occurrences incoming vehicle so occ = 2
		if (rs.absolute(occ)) {
			rs.previous(); // we stay to the row before the discount to verify the next rows if there are
			// the discount
		}
		while (rs.next()) {
			if (rs.getDouble(3) > 0) {
				ticket.setInTime(rs.getTimestamp(4));
				ticket.setOutTime(rs.getTimestamp(5));
				fareCalculatorService.calculateFare(ticket);
				double price_with_disc = Math.round((ticket.getPrice() - (ticket.getPrice() * disc)) * 100.0) / 100.0;
				assertThat(rs.getDouble(3)).isEqualTo(price_with_disc);
			}
		}
		parkingSpotDAO.dataBaseConfig.closeResultSet(rs);
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);
	}

	@Test
	public void testVerifyRecordsBDAvailableTrue() throws Exception {

		// we check if all parking available => all tickets closed
		testParkingLotExit(); // adding 2 records
		testParkingLotExit();

		PreparedStatement ps;
		ResultSet rs_parking;
		ResultSet rs_ticket;
		ps = con.prepareStatement("SELECT * FROM parking WHERE available = 0"); // any parking available
		rs_parking = ps.executeQuery();

		ps = con.prepareStatement("SELECT * FROM ticket WHERE isnull(out_time)"); // any ticket open
		rs_ticket = ps.executeQuery();

		assertThat(rs_parking.next()).isFalse();
		assertThat(rs_ticket.next()).isFalse();

		parkingSpotDAO.dataBaseConfig.closeResultSet(rs_parking);
		parkingSpotDAO.dataBaseConfig.closeResultSet(rs_ticket);
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);
	}

	@Test
	public void testVerifyRecordsBDAvailableFalse() throws Exception {

		// we check how much parking available = how much tickets opened

		testParkingCar(); // adding 2 records for the car
		testParkingCar();

		PreparedStatement ps;
		ResultSet rs_parking;
		ResultSet rs_ticket;
		int records_parking = 0;
		int records_ticket = 0;
		ps = con.prepareStatement("SELECT * FROM parking WHERE available = 0");
		rs_parking = ps.executeQuery();
		rs_parking.last();
		records_parking = rs_parking.getRow();
		ps = con.prepareStatement("SELECT * FROM ticket WHERE isnull(out_time)");
		rs_ticket = ps.executeQuery();
		rs_ticket.last();
		records_ticket = rs_ticket.getRow();

		assertEquals(records_parking, records_ticket);

		parkingSpotDAO.dataBaseConfig.closeResultSet(rs_parking);
		parkingSpotDAO.dataBaseConfig.closeResultSet(rs_ticket);
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);
	}

	@Test
	public void testVerifyIf2Decimals() throws Exception {

		// we check if more that 2 decimals in the price
		testParkingLotExit(); // adding 2 records for the car
		testParkingLotExit();

		int dec = 0;
		String decString;
		int verify = 0;

		PreparedStatement ps = con.prepareStatement("SELECT price FROM ticket WHERE price > 0");
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			rs.beforeFirst();
		} else {
			System.out.println("There are not results in the query ! For the test we need the results.");
			verify = 1;
		}
		while (rs.next()) {
			decString = "" + rs.getBigDecimal(1);
			dec = decString.indexOf(".");
			if (String.valueOf(decString.substring(dec + 1)).length() > 2) {
				verify = 1; // if number of decimals > 2
			}
		}
		assertEquals(0, verify);

		parkingSpotDAO.dataBaseConfig.closeResultSet(rs);
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);
	}

	@Test
	public void testVerifyIfNoPriceNegative() throws Exception {

		// we check if we don't have the negative price
		testParkingLotExit(); // adding 2 records
		testParkingLotExit();

		PreparedStatement ps = con.prepareStatement("SELECT price FROM ticket WHERE price < 0");
		ResultSet rs = ps.executeQuery();
		assertThat(rs.next()).isFalse();

		parkingSpotDAO.dataBaseConfig.closeResultSet(rs);
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);
	}

	@Test
	public void testVerifyIfFreeParkingFor30Min() throws Exception {

		// we check if we have the price 0 for 30 or less 30 minutes
		// adding 2 records
		// selection a CAR in the @BeforeEach

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		PreparedStatement ps;
		String vehicleRegNumber = inputReaderUtil.readVehicleRegistrationNumber();

		Date inTime = new Date();

		parkingService.processIncomingVehicle();
		inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000)); // 30 min is free
		ps = con.prepareStatement("UPDATE ticket SET IN_TIME=? WHERE isnull(out_time) and VEHICLE_REG_NUMBER=?");
		ps.setTimestamp(1, new Timestamp(inTime.getTime()));
		ps.setString(2, vehicleRegNumber);
		ps.execute();
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);

		parkingService.processExitingVehicle();

		parkingService.processIncomingVehicle();
		inTime.setTime(System.currentTimeMillis() - (20 * 60 * 1000)); // 20 min is free
		ps = con.prepareStatement("UPDATE ticket SET IN_TIME=? WHERE isnull(out_time) and VEHICLE_REG_NUMBER=?");
		ps.setTimestamp(1, new Timestamp(inTime.getTime()));
		ps.setString(2, vehicleRegNumber);
		ps.execute();
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);

		parkingService.processExitingVehicle();

		ps = con.prepareStatement("SELECT price FROM ticket WHERE price = 0");
		ResultSet rs = ps.executeQuery();
		assertThat(rs.absolute(2)).isTrue(); // verify if we have the 2 records added in the result with price 0

		parkingSpotDAO.dataBaseConfig.closeResultSet(rs);
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);
	}
}
