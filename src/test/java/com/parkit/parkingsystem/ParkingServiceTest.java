package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

/**
 * @author Claudiu
 *
 */
@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;
	@Mock
	private static ParkingSpot parkingSpot;

	@Test
	public void testProcessExitingVehicle() throws Exception {
		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");
			when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
		parkingService.processExitingVehicle();
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
	}

	@Test
	public void testProcessExitingVehicleWithticketFalseBranche2() throws SQLException {
		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");
			when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();
		verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
	}

	@Test
	public void testProcessExitingVehicleWithException() throws Exception {
		when(ticketDAO.getTicket("ABCDEF")).thenReturn(null);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();
		verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
	}

	@Test
	public void testProcessIncomingVehicleException() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
		when(parkingSpotDAO.updateParking(parkingSpot)).thenThrow(new RuntimeException());
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		try {
			parkingService.processIncomingVehicle();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
		verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
	}

	@Test
	public void testProcessIncomingVehicleCar() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		try {
			parkingService.processIncomingVehicle();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
		verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
	}

	@Test
	public void testProcessIncomingVehicleBike() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(1);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		try {
			parkingService.processIncomingVehicle();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
		verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
	}

	@Test
	public void testgetNextParkingNumberIfAvailableException() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(-1);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		try {
			parkingSpot = parkingService.getNextParkingNumberIfAvailable();
		} catch (Exception ie) {
			ie.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
		assertEquals(null, parkingSpot);
	}

	@Test
	public void testgetNextParkingNumberIfAvailableBrancheParkingNumber0() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		try {
			parkingSpot = parkingService.getNextParkingNumberIfAvailable();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
		assertEquals(null, parkingSpot);
	}

}
