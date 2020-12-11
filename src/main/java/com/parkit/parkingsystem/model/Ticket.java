package com.parkit.parkingsystem.model;

import java.util.Date;

/**
 * @author Claudiu
 *
 */
public class Ticket {
	private int id;
	private ParkingSpot parkingSpot;
	private String vehicleRegNumber;
	private double price;
	private Date inTime;
	private Date outTime;
	private double discount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ParkingSpot getParkingSpot() {
		return parkingSpot;
	}

	public void setParkingSpot(ParkingSpot parkingSpot) {
		this.parkingSpot = parkingSpot;
	}

	public String getVehicleRegNumber() {
		return vehicleRegNumber;
	}

	public void setVehicleRegNumber(String vehicleRegNumber) {
		this.vehicleRegNumber = vehicleRegNumber;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = Math.round(price * 100.0) / 100.0;
	}

	public double getDiscount() {
		return discount;
	}

	/*
	 * public void setDiscount(double price) { this.discount = discount; }
	 */

	public Date getInTime() {
		Date inTime2 = inTime;
		return inTime2;
	}

	public void setInTime(Date inTime) {
		// this.inTime = inTime;
		if (inTime == null) {
			this.inTime = null;
		} else {
			this.inTime = new Date(inTime.getTime());
		}
	}

	public Date getOutTime() {
		Date outTime2 = outTime;
		return outTime2;
	}

	public void setOutTime(Date outTime) {
		if (outTime == null) {
			this.outTime = null;
		} else {
			this.outTime = new Date(outTime.getTime());
		}
		// this.outTime = (Date) outTime.clone();
	}
}
