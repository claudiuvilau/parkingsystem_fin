package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

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
	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void testParkingACar() throws Exception {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		// TODO: check that a ticket is actualy saved in DB and Parking table is updated
		// with availability

		// GIVEN
		String vehicleRegNumber = inputReaderUtil.readVehicleRegistrationNumber();
		// WHEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // set time 1 hour less
		DataBaseTestConfig db_test = new DataBaseTestConfig();
		Connection con = db_test.getConnection();
		PreparedStatement ps1 = con.prepareStatement(DBConstants.UPDATE_TIME_TICKET);
		ps1.setTimestamp(1, new Timestamp(inTime.getTime()));
		ps1.execute();
		PreparedStatement ps = con.prepareStatement(DBConstants.FIND_IN_TICKET);
		ps.setString(1, vehicleRegNumber);
		ResultSet rs = ps.executeQuery();
		// THEN
		if (rs.next()) {
			assertNotEquals(null, rs.getString(5));
		} else
			assertNotEquals(null, rs.getString(5));
		db_test.closeResultSet(rs);
		db_test.closePreparedStatement(ps);

	}

	@Test
	public void testParkingLotExit() throws Exception {
		testParkingACar();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();
		// TODO: check that the fare generated and out time are populated correctly in
		// the database

		// GIVEN
		String vehicleRegNumber = inputReaderUtil.readVehicleRegistrationNumber();
		// WHEN
		DataBaseTestConfig db_test = new DataBaseTestConfig();
		Connection con = db_test.getConnection();
		PreparedStatement ps = con.prepareStatement(DBConstants.FIND_OUT_TICKET);
		ps.setString(1, vehicleRegNumber);
		ResultSet rs = ps.executeQuery();
		// THEN
		if (rs.next()) {
			assertNotEquals(null, rs.getString(5));
		} else
			assertNotEquals(null, rs.getString(5));

		db_test.closeResultSet(rs);
		db_test.closePreparedStatement(ps);

	}

	@Test
	public void testDiscount() throws Exception {
		testParkingLotExit();
		testParkingLotExit();
		testParkingLotExit();

		// GIVEN
		String vehicleRegNumber = inputReaderUtil.readVehicleRegistrationNumber();

		// WHEN
		DataBaseTestConfig db_test = new DataBaseTestConfig();
		Connection con = db_test.getConnection();
		PreparedStatement ps = con.prepareStatement(DBConstants.COUNT_TICKET_FOR_DISCOUNT);
		ps.setString(1, vehicleRegNumber);
		ResultSet rs = ps.executeQuery();
		int occurence = 0;
		if (rs.next()) {
			occurence = rs.getInt(1);
		}
		boolean occ = false;
		if (occurence >= 1) {
			occ = true;
		}
		// THEN
		assertTrue(occ);

		db_test.closeResultSet(rs);
		db_test.closePreparedStatement(ps);

	}

}
