package com.parkit.parkingsystem.dao;

/**
 * @author Claudiu
 *
 *         this class configure the number of the occurrences for a discount and
 *         the percentage number for the discount
 */
public class Discount {

	private int nb_occurence = 1; // set the number of the occurrences to be available a discount
	private double disc = 5; // set the discount to make 5 = 5%

	/**
	 * this method verify if the occurrence and write a message to the customer
	 * 
	 * @param occurence this is the number of the occurrence for a discount
	 * @return the message
	 */
	public String discount_msg(int occurence) {
		String msg_disc = "";
		if (occurence >= nb_occurence) {
			msg_disc = "Welcome back! As a recurring user of our parking lot, you'll benefit from a " + disc
					+ " % discount.";
		}
		return msg_disc;
	}

	/**
	 * this method set the discount in percentage
	 * 
	 * @param occurence this is the number of the occurrence for a discount
	 * @return the discount in percentage to use for the final price
	 */
	public double discount(int occurence) {
		if (occurence < nb_occurence) {
			disc = 0;
		}
		return disc;
	}
}
