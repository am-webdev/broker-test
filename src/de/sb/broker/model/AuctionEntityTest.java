package de.sb.broker.model;

import static org.junit.Assert.*;

import java.util.Set;

import javax.validation.ConstraintViolation;

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
		
		
		// Legal Test Cases
		
		auction1.setAskingPrice(1);
	    Set<ConstraintViolation<Auction>> constraintViolations = getEntityValidatorFactory().getValidator().validate( auction1 );
	    assertEquals( 0, constraintViolations.size() );	

		auction1.setAskingPrice(Long.MAX_VALUE);
		getEntityValidatorFactory().getValidator().validate( auction1 );
	    assertEquals( 0, constraintViolations.size() );

	    auction1.setDescription("x");
		getEntityValidatorFactory().getValidator().validate( auction1 );
	    assertEquals( 0, constraintViolations.size() );
	    //TODO
		
		// Negative Test Cases

		auction1.setAskingPrice(0);
		Set<ConstraintViolation<Auction>> constraintViolationsZero = getEntityValidatorFactory().getValidator().validate( auction1 );
	    assertEquals( 1, constraintViolationsZero.size() );
	    
	    auction1.setAskingPrice(-1);
	    auction1.setUnitCount((short) -1);
		Set<ConstraintViolation<Auction>> constraintViolationsNeg = getEntityValidatorFactory().getValidator().validate( auction1 );
	    assertEquals( 2, constraintViolationsNeg.size() );
	    
	    auction1.setAskingPrice(1);
	    auction1.setUnitCount((short) 1);
	    auction1.setTitle("");
		Set<ConstraintViolation<Auction>> constraintViolationsEmtpyTitle = getEntityValidatorFactory().getValidator().validate( auction1 );
	    assertEquals( 1, constraintViolationsEmtpyTitle.size() );
		
	    // TODO
		
		
	}
	
	@Test
	public void testLifeCycle() {
		// TODO
	}


}
