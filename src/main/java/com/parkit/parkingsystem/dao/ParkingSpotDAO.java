package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.Password;
import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

/**
 * @author Claudiu
 *
 */
public class ParkingSpotDAO {
	private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	/**
	 * this method verify if there are a available slot
	 * 
	 * @param parkingType this is the type of the vehicle : car or bike
	 * @return the number of the parking
	 */
	public int getNextAvailableSlot(ParkingType parkingType) {
		Connection con = null;
		int result = -1;
		try {
			con = dataBaseConfig.getConnection(Password.password.getUser(), Password.password.getPass());
			PreparedStatement ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
			ps.setString(1, parkingType.toString());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return result;
	}

	/**
	 * this method update the table parking with the new vehicle
	 * 
	 * @param parkingSpot this is the data of the parking spot for the table parking
	 * @return true if the update is success
	 * @throws SQLException if no data for the parking spot
	 */
	public boolean updateParking(ParkingSpot parkingSpot) throws SQLException {
		// update the availability for that parking slot
		boolean result = false;
		Connection con = null;
		PreparedStatement ps = null;
		int updateRowCount = 0;
		try {
			con = dataBaseConfig.getConnection(Password.password.getUser(), Password.password.getPass());
			try {
				ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
				ps.setBoolean(1, parkingSpot.isAvailable());
				ps.setInt(2, parkingSpot.getId());
				updateRowCount = ps.executeUpdate();
				result = updateRowCount == 1;
			} finally {
				if (ps != null) {
					dataBaseConfig.closePreparedStatement(ps);
				}
			}
		} catch (Exception ex) {
			logger.error("Error updating parking info", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return result;
	}

}
