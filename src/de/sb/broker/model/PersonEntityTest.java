package de.sb.broker.model;

import static org.junit.Assert.*;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

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
		assertArrayEquals(Person.passwordHash("password"), testPersonDefaultConstructor.getPasswordHash());
		testPersonDefaultConstructor.setGroup(Group.ADMIN);
		assertEquals(Group.ADMIN, testPersonDefaultConstructor.getGroup());
		testPersonDefaultConstructor.setName(new Name());
		assertNotEquals(null, testPersonDefaultConstructor.getName());
		testPersonDefaultConstructor.setAddress(new Address());
		assertNotEquals(null, testPersonDefaultConstructor.getAddress());
		testPersonDefaultConstructor.setContact(new Contact());
		assertNotEquals(null, testPersonDefaultConstructor.getContact());
		
		
		Person testBoundsPerson = new Person();
		Validator validator = this.getEntityValidatorFactory().getValidator();
		
		// low bounds
		testBoundsPerson.setAlias("A");
		Set<ConstraintViolation<Person>> constraintValidationsPerson = validator.validate(testPersonDefaultConstructor);
		assertEquals(0, constraintValidationsPerson.size());
		
		// in bounds
		testBoundsPerson.setAlias("Abc");
		testBoundsPerson.setPasswordHash(Person.passwordHash("someString")); // can we test different cases for that? we have no control over what is in there
		constraintValidationsPerson = validator.validate(testPersonDefaultConstructor);
		assertEquals(0, constraintValidationsPerson.size());
		
		// high bounds
		testBoundsPerson.setAlias("ABCDEFGHIJKLMNOP");
		constraintValidationsPerson = validator.validate(testPersonDefaultConstructor);
		assertEquals(0, constraintValidationsPerson.size());
		
		// out of bounds
		testBoundsPerson.setAlias("ABCDEFGHIJKLMNOPQRSTU");
		constraintValidationsPerson = validator.validate(testPersonDefaultConstructor);
		assertEquals(1, constraintValidationsPerson.size());
		
		// ---------------- Name ---------------- 
		Name testName = new Name();
		// default constructor
		assertEquals("", testName.getFamily());
		assertEquals("", testName.getGiven());
		
		// basic tests
		testName.setFamily("family");
		testName.setGiven("given");
		assertEquals("family", testName.getFamily());
		assertEquals("given", testName.getGiven());
		
		// low bounds
		testName.setFamily("F");
		testName.setGiven("G");
		Set<ConstraintViolation<Name>> constraintValidationsName = validator.validate(testName);
		assertEquals(0, constraintValidationsName.size());
		
		// in bounds
		testName.setFamily("Family");
		testName.setGiven("Given");
		constraintValidationsName = validator.validate(testName);
		assertEquals(0, constraintValidationsName.size());
		
		// high bounds
		testName.setFamily("FamilyFamilyFamilyFamilyFamilyF");
		testName.setGiven("GivenGivenGivenGivenGivenGivenG");
		constraintValidationsName = validator.validate(testName);
		assertEquals(0, constraintValidationsName.size());
		
		// out of bounds
		testName.setFamily("FamilyFamilyFamilyFamilyFamilyFamily");
		testName.setGiven("GivenGivenGivenGivenGivenGivenGiven");
		constraintValidationsName = validator.validate(testName);
		assertEquals(2, constraintValidationsName.size());
		
		// ---------------- Address ---------------- 
		Address testAdress = new Address();
		// default constructor
		assertEquals("", testAdress.getStreet());
		assertEquals("", testAdress.getPostCode());
		assertEquals("", testAdress.getCity());
		
		//basic tests
		testAdress.setStreet("street");
		testAdress.setPostCode("postcode");
		testAdress.setCity("city");
		assertEquals("street", testAdress.getStreet());
		assertEquals("postcode", testAdress.getPostCode());
		assertEquals("city", testAdress.getCity());
		
		// low bounds
		testAdress.setStreet("");
		testAdress.setPostCode("");
		testAdress.setCity("c");
		Set<ConstraintViolation<Address>> constraintValidationsAddress = validator.validate(testAdress);
		assertEquals(0, constraintValidationsAddress.size());
		
		// in bounds
		testAdress.setStreet("street");
		testAdress.setPostCode("postcode");
		testAdress.setCity("city");
		constraintValidationsAddress = validator.validate(testAdress);
		assertEquals(0, constraintValidationsAddress.size());		
		
		// high bounds
		testAdress.setStreet("012345678901234567890123456789012345678901234567890123456789123");
		testAdress.setPostCode("012345678901234");
		testAdress.setCity("012345678901234567890123456789012345678901234567890123456789123");
		constraintValidationsAddress = validator.validate(testAdress);
		assertEquals(0, constraintValidationsAddress.size());		
		
		// out of bounds
		testAdress.setStreet("0123456789012345678901234567890123456789012345678901234567891234");
		testAdress.setPostCode("0123456789012345");
		testAdress.setCity("0123456789012345678901234567890123456789012345678901234567891234");
		constraintValidationsAddress = validator.validate(testAdress);
		assertEquals(3, constraintValidationsAddress.size());
		
		// ---------------- Contact ---------------- 
		Contact testContact = new Contact();
		// default constructor
		assertEquals("", testContact.getEmail());
		assertEquals("", testContact.getPhone());
		
		// basic tests
		testContact.setEmail("this@mail.com");
		testContact.setPhone("01012");
		assertEquals("this@mail.com", testContact.getEmail());
		assertEquals("01012", testContact.getPhone());

		// low bounds
		testContact.setEmail("a@b");
		testContact.setPhone("");
		Set<ConstraintViolation<Contact>> constraintValidationsContact = validator.validate(testContact);
		assertEquals(0, constraintValidationsAddress.size());
		
		
		// in bounds
		testContact.setEmail("broker@models.com");
		testContact.setPhone("00111201230");
		constraintValidationsContact = validator.validate(testContact);
		assertEquals(0, constraintValidationsAddress.size());
		
		
		// high bounds
		testContact.setEmail("012345678901234567890123456789@01234567890123456789012345678901");
		testContact.setPhone("0123456789012345678901234567890");
		constraintValidationsContact = validator.validate(testContact);
		assertEquals(0, constraintValidationsAddress.size());
		
		// out of bounds
		testContact.setEmail("012345678901234567890123456789@012345678901234567890123456789012");
		testContact.setPhone("01234567890123456789012345678901");
		constraintValidationsContact = validator.validate(testContact);
		assertEquals(0, constraintValidationsAddress.size());
		
	}
	
	@Test
	public void testLifeCycle() {
		// TODO
	}

}
