package de.sb.broker.model;

import static org.junit.Assert.*;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Test;

public class BidEntityTest extends EntityTest {
	
	@Test
	public void testConstraints() {
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
		
		/*
		 * There seams to be an issue within our annotations:
		 * http://forum.spring.io/forum/spring-projects/roo/81387-no-validator-for-type-long
		 * If we remove the @Size(min = 1) the validation factory is able to validate otherwise an error is thrown:
		 * No validator could be found for type: java.lang.Long
		 */
		
		// Legal Test Cases
		Bid legalValueBid = new Bid();
		
		legalValueBid.setPrice(1);
	    Set<ConstraintViolation<Bid>> constraintViolationsMin = getEntityValidatorFactory().getValidator().validate( legalValueBid );
	    assertEquals( 0, constraintViolationsMin.size() );		

	    
		legalValueBid.setPrice(Long.MAX_VALUE);
		Set<ConstraintViolation<Bid>> constraintViolationsMax = getEntityValidatorFactory().getValidator().validate( legalValueBid );
	    assertEquals( 0, constraintViolationsMax.size() );		

		
		// Negative Test Cases
		Bid negativValueBid = new Bid();
		negativValueBid.setPrice(-100);

	    Set<ConstraintViolation<Bid>> constraintViolations1 = getEntityValidatorFactory().getValidator().validate( negativValueBid );

	    assertEquals( 1, constraintViolations1.size() );
	    assertEquals( "The price needs to start at 1ct", constraintViolations1.iterator().next().getMessage());
		
		// Price for a bid was set to 0 Test Cases
		Bid toLowValueBid = new Bid();
		negativValueBid.setPrice(0);

	    Set<ConstraintViolation<Bid>> constraintViolations2 = getEntityValidatorFactory().getValidator().validate( toLowValueBid );

	    assertEquals( 1, constraintViolations2.size() );
	    assertEquals( "The price needs to start at 1ct", constraintViolations2.iterator().next().getMessage());
		
	}
	
	@Test
	public void testLifeCycle() {
		// TODO
	}

}
