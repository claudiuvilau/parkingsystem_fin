package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.Password;
import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

/**
 * @author Claudiu
 *
 */
public class TicketDAO {

	private static final Logger logger = LogManager.getLogger("TicketDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	/**
	 * this method save the ticket in the table ticket
	 * 
	 * @param ticket there are the data of ticket
	 * @return true if the success saved
	 */
	public boolean saveTicket(Ticket ticket) {
		Connection con = null;
		try {

			con = dataBaseConfig.getConnection(Password.password.getUser(), Password.password.getPass());

			int occurence = 0;
			String msg_disc;
			occurence = occurence_for_disc(ticket);
			Discount discount = new Discount();
			msg_disc = discount.discount_msg(occurence);
			System.out.println(msg_disc);

			PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			// ps.setInt(1,ticket.getId());
			ps.setInt(1, ticket.getParkingSpot().getId());
			ps.setString(2, ticket.getVehicleRegNumber());
			ps.setDouble(3, ticket.getPrice());
			ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
			ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
			ps.execute();
			dataBaseConfig.closePreparedStatement(ps);
			// return ps.execute();
			return true;
		} catch (

		Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return false;

	}

	/**
	 * this method configure the data of the ticket
	 * 
	 * @param vehicleRegNumber this is the number of the vehicle
	 * @return the data of the ticket
	 */
	public Ticket getTicket(String vehicleRegNumber) {
		Connection con = null;
		Ticket ticket = null;
		try {
			con = dataBaseConfig.getConnection(Password.password.getUser(), Password.password.getPass());
			PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			ps.setString(1, vehicleRegNumber);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ticket = new Ticket();
				ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
				ticket.setParkingSpot(parkingSpot);
				ticket.setId(rs.getInt(2));
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(rs.getDouble(3));
				ticket.setInTime(rs.getTimestamp(4));
				ticket.setOutTime(rs.getTimestamp(5));
			}
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return ticket;
	}

	/**
	 * this method update the table ticket with the price and the time out when the
	 * vehicle go out
	 * 
	 * @param ticket this is the data of the ticket
	 * @return true if the update is success
	 */
	public boolean updateTicket(Ticket ticket) {
		Connection con = null;
		try {
			con = dataBaseConfig.getConnection(Password.password.getUser(), Password.password.getPass());

			int occurence = 0;
			occurence = occurence_for_disc(ticket);
			double disc = 0;
			Discount discount = new Discount();
			disc = discount.discount(occurence);

			ticket.setPrice(Math.round((ticket.getPrice() - (ticket.getPrice() * disc / 100)) * 100.0) / 100.0);
			PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
			ps.setDouble(1, ticket.getPrice());
			ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
			ps.setInt(3, ticket.getId());
			ps.execute();
			dataBaseConfig.closePreparedStatement(ps);
			return true;
		} catch (Exception ex) {
			logger.error("Error saving ticket info", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return false;
	}

	/**
	 * this method count the number of the occurrences in the table
	 * 
	 * @param ticket this is the data of the ticket
	 * @return the number of the occurrences
	 */
	private int occurence_for_disc(Ticket ticket) {
		Connection con1 = null;
		int occurence = 0;
		try {
			con1 = dataBaseConfig.getConnection(Password.password.getUser(), Password.password.getPass());
			PreparedStatement ps_disc = con1.prepareStatement(DBConstants.COUNT_TICKET_FOR_DISCOUNT); // if the vehicle
																										// stay 1 hour
																										// or more
			ps_disc.setString(1, ticket.getVehicleRegNumber());
			ResultSet rs = ps_disc.executeQuery();
			if (rs.next()) {
				occurence = rs.getInt(1);
			}
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps_disc);
		} catch (Exception ex) {
			logger.error("Error saving ticket info", ex);
		} finally {
			dataBaseConfig.closeConnection(con1);
		}
		return occurence;
	}

}
