package com.parkit.parkingsystem.dao;

public class Discount {

	private int nb_occurence = 1;
	private double disc = 5;

	public void discount_msg(int occurence) {
		if (occurence >= nb_occurence) {
			System.out.println("Welcome back! As a recurring user of our parking lot, you'll benefit from a " + disc
					+ " % discount.");
		}
	}

	public double discount(int occurence) {
		if (occurence < nb_occurence) {
			disc = 0;
		}
		return disc;
	}
}
