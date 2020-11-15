package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
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
		// TODO: check that a ticket is actualy saved in DB and Parking table is updated
		// with availability

		String vehicleRegNumber = inputReaderUtil.readVehicleRegistrationNumber();

		PreparedStatement ps = con.prepareStatement(
				"select t.PARKING_NUMBER, p.available, t.VEHICLE_REG_NUMBER from ticket t,parking p where p.available = false and isnull(t.out_time) and p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=?");
		ps.setString(1, vehicleRegNumber);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
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
		ps = con.prepareStatement("update ticket set IN_TIME=? where isnull(out_time) and VEHICLE_REG_NUMBER=?");
		ps.setTimestamp(1, new Timestamp(inTime.getTime()));
		ps.setString(2, vehicleRegNumber);
		ps.execute();
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);

		parkingService.processExitingVehicle();

		ps = con.prepareStatement(
				"select p.available, t.PARKING_NUMBER, t.VEHICLE_REG_NUMBER, p.TYPE, t.PRICE, t.IN_TIME, t.OUT_TIME from ticket t,parking p where p.available = true and p.parking_number = t.parking_number and t.price > 0 and t.out_time is not null and t.VEHICLE_REG_NUMBER=?");
		ps.setString(1, vehicleRegNumber);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			assertThat(rs.getString(3)).isEqualTo(vehicleRegNumber);
		}
		parkingSpotDAO.dataBaseConfig.closeResultSet(rs);
		parkingSpotDAO.dataBaseConfig.closePreparedStatement(ps);
	}
}
