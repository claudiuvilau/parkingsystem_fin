package com.parkit.parkingsystem.constants;

public class DBConstants {

	public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";
	public static final String UPDATE_PARKING_SPOT = "update parking set available = ? where PARKING_NUMBER = ?";

	public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) values(?,?,?,?,?)";
	public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? where ID=?";
	public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? and isnull(t.out_time) order by t.IN_TIME  limit 1";

	public static final String FIND_IN_TICKET = "select t.PARKING_NUMBER, p.available, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where p.available = false and isnull(t.out_time) and p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.IN_TIME  limit 1";
	public static final String FIND_OUT_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where p.available = true and t.price <> 0 and t.out_time is not null and p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.IN_TIME  limit 1";
	public static final String FIND_TICKET_FOR_DISCOUNT = "select * from ticket where price > 0 and out_time is not null and VEHICLE_REG_NUMBER=?";
	public static final String COUNT_TICKET_FOR_DISCOUNT = "select count(*) from ticket where price > 0 and out_time is not null and TIMEDIFF(out_time, in_time) >= \"01:00\" and VEHICLE_REG_NUMBER=?";
	public static final String UPDATE_TIME_TICKET = "update ticket set IN_TIME=? where isnull(out_time)";

}
