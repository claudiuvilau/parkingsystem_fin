package com.parkit.parkingsystem;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;
	
	/*
	@BeforeEach
	private void setUpPerTest() {
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
	}
*/
	@Test
	public void processExitingVehicleTest() throws Exception {
		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");		
			when(ticketDAO.getTicket(anyString())).thenReturn(ticket); // for processExitingVehicleTest
			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true); // for processExitingVehicleTest
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
	public void processExitingVehicleNoExitingTest() {
		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");		
			when(ticketDAO.getTicket(anyString())).thenReturn(ticket); // for processExitingVehicleTest
			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false); // for processExitingVehicleTest
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
		parkingService.processExitingVehicle();
		verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
	}
	
	  @Test
	    public void processExitingVehicleWithticketFalse()
	    {
	    	//when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
	        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	    	parkingService.processExitingVehicle();
	    	verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
	    }
	    

	    @Test
	    public void processExitingVehicleWithException()
	    {
	    	//when(ticketDAO.updateTicket(any(Ticket.class))).thenThrow(new NullPointerException());
	        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	    	parkingService.processExitingVehicle();
	    	verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));	    	
	    }
	
	@Test
	public void processIncomingVehicleCarTest() throws Exception {
		try {		
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");			
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
		when(inputReaderUtil.readSelection()).thenReturn(1); // for the test processIncomingVehicleTest
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF"); // for the test processIncomingVehicleTest
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1); // for the test processIncomingVehicleTest
		parkingService.processIncomingVehicle();
		verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
	}
	
	@Test
	public void processIncomingVehicleBikeTest() throws Exception {
		try {		
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");			
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
		when(inputReaderUtil.readSelection()).thenReturn(2); // for the test processIncomingVehicleTest
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF"); // for the test processIncomingVehicleTest
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(1); // for the test processIncomingVehicleTest
		parkingService.processIncomingVehicle();
		verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
	}
	
	@Test
	public void processIncomingVehicleParkingSpotNullTest() throws Exception {
		try {		
			parkingService = new ParkingService(inputReaderUtil, null, ticketDAO);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
		when(inputReaderUtil.readSelection()).thenReturn(1); // for the test processIncomingVehicleTest
		parkingService.processIncomingVehicle();
		verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
	}
	
	  @Test
	    public void processIncomingVehicleWithticketFalse()
	    {
	    	//when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
	        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	    	parkingService.processExitingVehicle();
	    	verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
	    }
	    

	    @Test
	    public void processIncomingVehicleWithException()
	    {
	    	//when(ticketDAO.updateTicket(any(Ticket.class))).thenThrow(new NullPointerException());
	        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	    	parkingService.processExitingVehicle();
	    	verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));	    	
	    }

}
