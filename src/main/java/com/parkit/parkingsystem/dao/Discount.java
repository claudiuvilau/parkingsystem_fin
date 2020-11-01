package com.parkit.parkingsystem.dao;

public class Discount {

	private int nb_occurence = 1;
	private double disc = 5;

	public String discount_msg(int occurence) {
		String msg_disc = "";
		if (occurence >= nb_occurence) {
			msg_disc = "Welcome back! As a recurring user of our parking lot, you'll benefit from a " + disc
					+ " % discount.";
		}
		return msg_disc;
	}

	public double discount(int occurence) {
		if (occurence < nb_occurence) {
			disc = 0;
		}
		return disc;
	}
}
