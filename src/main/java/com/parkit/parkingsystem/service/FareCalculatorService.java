package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

/**
 * @author Claudiu
 *
 */
public class FareCalculatorService {

	/**
	 * this method calculate the fire
	 * 
	 * @param ticket this is the data of the ticket
	 */
	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		// the duration must be between the time out - the time in and no between the
		// out hour time - in hour time
		// int inHour = ticket.getInTime().getHours();
		// int outHour = ticket.getOutTime().getHours();

		// TODO: Some tests are failing here. Need to check if this logic is correct
		// int duration = outHour - inHour;

		long diff = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
		double duration = (double) diff / (60 * 60) / 1000;

		duration = durationTime(duration);

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
			break;
		}
		case BIKE: {
			ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
	}

	/**
	 * this method configure the duration time to be calculated if there are a free
	 * time parking
	 * 
	 * @param dT this is the duration time between out time and in time
	 * @return the duration time - the free parking
	 */
	private double durationTime(double dT) {
		// story 1 - if less 30 min : 1h (60min) / 2 (30min) = 0.5 => durationTime

		if (dT <= 0.5) {
			dT = 0;
		} else
			dT = dT - 0.5;
		return dT;
	}

}