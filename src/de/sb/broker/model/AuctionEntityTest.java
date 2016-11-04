package de.sb.broker.model;

import static org.junit.Assert.*;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.Test;

public class AuctionEntityTest extends EntityTest {
	
	@Test
	public void testConstraints() {
		Auction auction1 = new Auction();
		
		auction1.setTitle("Auction to test");
		auction1.setDescription("My FooBar Description");
		auction1.setClosureTimestamp(1482537600);		// 2016-12-24T00:00:00+00:00 
		auction1.setUnitCount((short) 1);
		auction1.setAskingPrice(1337);
		auction1.setSeller(new Person());
		auction1.setVersion(1);
		
		// Some basic tests
		assertEquals("Auction to test", auction1.getTitle());
		assertEquals("My FooBar Description", auction1.getDescription());
		assertEquals(1482537600, auction1.getClosureTimestamp());
		//assertEquals((short) 1, auction1.getUnitCount());
		assertEquals(1337, auction1.getAskingPrice());
		assertNotEquals(null, auction1.getSeller());
		assertEquals(1, auction1.getVersion());
		// TODO constraintViolations
		
		// Legal Test Cases
		final Validator validator = getEntityValidatorFactory().getValidator();
		auction1.setAskingPrice(1);
	    assertEquals(0 , validator.validate(auction1).size());	

		auction1.setAskingPrice(Long.MAX_VALUE);
		validator.validate( auction1 );
		assertEquals(0 , validator.validate(auction1).size());	// TODO kopieren für alle anderen asserts


	    auction1.setDescription("x");
	    validator.validate( auction1 );
	    assertEquals( 0, validator.validate(auction1).size());
		
		// Negative Test Cases

		auction1.setAskingPrice(0);
		Set<ConstraintViolation<Auction>> constraintViolationsZero = validator.validate( auction1 );
	    assertEquals( 1, constraintViolationsZero.size() );
	    
	    auction1.setAskingPrice(-1);
	    auction1.setUnitCount((short) -1);
		Set<ConstraintViolation<Auction>> constraintViolationsNeg = validator.validate( auction1 );
	    assertEquals( 2, constraintViolationsNeg.size() );
	    
	    auction1.setAskingPrice(1);
	    auction1.setUnitCount((short) 1);
	    auction1.setTitle("");
		Set<ConstraintViolation<Auction>> constraintViolationsEmtpyTitle = validator.validate( auction1 );
	    assertEquals( 1, constraintViolationsEmtpyTitle.size() );
		
	    // TODO
		
		
	}
	
	@Test
	public void testLifeCycle() {
		// TODO
	}


}
