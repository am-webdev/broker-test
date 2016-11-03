package de.sb.broker.model;

import static org.junit.Assert.*;

import org.junit.Test;

import de.sb.broker.model.Person.Group;

public class PersonEntityTest extends EntityTest {
	
	@Test
	public void testConstraints() {
		// test default constructor values
		Person testPersonDefaultConstructor = new Person();
		assertEquals("", testPersonDefaultConstructor.getAlias());
		assertEquals(null, testPersonDefaultConstructor.getPasswordHash());
		assertEquals(Group.USER, testPersonDefaultConstructor.getGroup());
		assertEquals(null, testPersonDefaultConstructor.getName());
		assertEquals(null, testPersonDefaultConstructor.getAddress());
		assertEquals(null, testPersonDefaultConstructor.getContact());
		assertNotEquals(null, testPersonDefaultConstructor.getAuctions());
		assertNotEquals(null, testPersonDefaultConstructor.getBids());
		
		// basic tests
		testPersonDefaultConstructor.setAlias("Bruce Banner");
		assertEquals("Bruce Banner", testPersonDefaultConstructor.getAlias());
		testPersonDefaultConstructor.setPasswordHash(Person.passwordHash("password"));
		assertEquals(Person.passwordHash("password"), testPersonDefaultConstructor.getPasswordHash());
		testPersonDefaultConstructor.setGroup(Group.ADMIN);
		assertEquals(Group.ADMIN, testPersonDefaultConstructor.getGroup());
		testPersonDefaultConstructor.setName(new Name());
		assertNotEquals(null, testPersonDefaultConstructor.getName());
		testPersonDefaultConstructor.setAddress(new Address());
		assertNotEquals(null, testPersonDefaultConstructor.getAddress());
		testPersonDefaultConstructor.setContact(new Contact());
		assertNotEquals(null, testPersonDefaultConstructor.getContact());
		
		
		Person testBoundsPerson = new Person();

		// low bounds
		testBoundsPerson.setAlias("A");
		
		// in bounds
		testBoundsPerson.setAlias("Abc");
		testBoundsPerson.setPasswordHash(Person.passwordHash("someString")); // can we test different cases for that? we have no control over what is in there

		
		// high bounds
		testBoundsPerson.setAlias("ABCDEFGHIJKLMNOP");

		
		// out of bounds
		testBoundsPerson.setAlias("ABCDEFGHIJKLMNOPQRSTU");

		
		
		// TODO
	}
	
	@Test
	public void testLifeCycle() {
		// TODO
	}

}
