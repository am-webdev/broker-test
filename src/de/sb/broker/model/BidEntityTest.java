package de.sb.broker.model;

import static org.junit.Assert.*;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.Test;

public class BidEntityTest extends EntityTest {
	
	@Test
	public void testConstraints() {
		// Generate a new Bid object
		Bid testBidDefaultConstructor = new Bid();
		testBidDefaultConstructor.setPrice(42);

		// Some basic tests
		assertEquals(null, testBidDefaultConstructor.getAuction());
		assertEquals(null, testBidDefaultConstructor.getBidder());
		assertEquals(42, testBidDefaultConstructor.getPrice());
		
		Bid testBid2 = new Bid(new Auction(), new Person());
		testBid2.setPrice(4711);
		
		assertNotEquals(null, testBid2.getAuction());
		assertNotEquals(null, testBid2.getBidder());
		assertEquals(4711, testBid2.getPrice());
		
		// Legal Test Cases
		Bid legalValueBid = new Bid();
		
		Validator validator = getEntityValidatorFactory().getValidator();		
		
		legalValueBid.setPrice(1);
	    Set<ConstraintViolation<Bid>> constraintViolationsMin = validator.validate( legalValueBid );
	    assertEquals( 0, constraintViolationsMin.size() );		
	    
		legalValueBid.setPrice(Long.MAX_VALUE);
		Set<ConstraintViolation<Bid>> constraintViolationsMax = validator.validate( legalValueBid );
	    assertEquals( 0, constraintViolationsMax.size() );		
		
		// Negative Test Cases
		Bid negativValueBid = new Bid();
		negativValueBid.setPrice(-100);

	    Set<ConstraintViolation<Bid>> constraintViolations1 = validator.validate( negativValueBid );
	    assertEquals( 1, constraintViolations1.size() );
		
		// Price for a bid was set to 0 Test Cases
		Bid toLowValueBid = new Bid();
		toLowValueBid.setPrice(0);

	    Set<ConstraintViolation<Bid>> constraintViolations2 = validator.validate( toLowValueBid );
	    assertEquals( 1, constraintViolations2.size() );		
	}
	
	@Test
	public void testLifeCycle() {
		// TODO
	}

}
