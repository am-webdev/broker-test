package de.sb.broker.model;

import static org.junit.Assert.*;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Test;

public class BidEntityTest extends EntityTest {
	
	@Test
	public void testConstraints() {
		// TODO
		// Generate a new Bid object
		Bid testBidDefaultConstructor = new Bid();
		testBidDefaultConstructor.setPrice(42);
		
		assertEquals(null, testBidDefaultConstructor.getAuction());
		assertEquals(null, testBidDefaultConstructor.getBidder());
		assertEquals(42, testBidDefaultConstructor.getPrice());
		
		Bid testBid2 = new Bid(new Auction(), new Person());
		testBid2.setPrice(4711);
		
		assertNotEquals(null, testBid2.getAuction());
		assertNotEquals(null, testBid2.getBidder());
		assertEquals(4711, testBid2.getPrice());
		
		
		// Negative Test Cases
		Bid negativValueBid = new Bid();
		negativValueBid.setPrice(-100);

	    Set<ConstraintViolation<Bid>> constraintViolations = super.getEntityValidatorFactory().getValidator().validate( negativValueBid );

	    assertEquals( 1, constraintViolations.size() );
	    assertEquals( "The price needs to start at 1ct", constraintViolations.iterator().next().getMessage());
		
	}
	
	@Test
	public void testLifeCycle() {
		// TODO
	}

}
