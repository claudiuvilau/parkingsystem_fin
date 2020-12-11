package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.dao.Discount;

/**
 * @author Claudiu
 *
 */
public class DiscountTest {

	@Test
	public void testCalculateIfDiscount() {
		// test if occurrence 5 we have the discount 5%
		int occurence = 5;
		double disc;
		double pourcentage = 5;
		Discount discount = new Discount();
		disc = discount.discount(occurence);
		assertEquals(pourcentage, disc);
	}

	@Test
	public void testCalculateIfDiscountZero() {
		// test if no occurrence so 0 we have the discount 0%
		int occurence = 0;
		double disc;
		double pourcentage = 0;
		Discount discount = new Discount();
		disc = discount.discount(occurence);
		assertEquals(pourcentage, disc);
	}

	@Test
	public void testIfMsgdiscount() {
		// test if occurrence 1 we have the message inform you for discount
		int occurence = 1;
		String msg = "";
		Discount discount = new Discount();
		msg = discount.discount_msg(occurence);
		assertNotEquals(msg, "");
	}

	@Test
	public void testIfMsgdiscountZero() {
		// test if no occurrence so 0 we don't have the message inform you for discount
		int occurence = 0;
		String msg = "";
		Discount discount = new Discount();
		msg = discount.discount_msg(occurence);
		assertEquals(msg, "");
	}

}
