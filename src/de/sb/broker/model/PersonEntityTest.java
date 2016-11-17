package de.sb.broker.model;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.validation.Validator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import de.sb.broker.model.Person.Group;

public class PersonEntityTest extends EntityTest {

	private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("broker");
	
	@Test
	public void testConstraints() {
		// test default constructor values
		Person testPersonDefaultConstructor = new Person();
		assertEquals(" ", testPersonDefaultConstructor.getAlias());
		assertEquals(null, testPersonDefaultConstructor.getPasswordHash());
		assertEquals(Group.USER, testPersonDefaultConstructor.getGroup());
		assertNotEquals(null, testPersonDefaultConstructor.getName());
		assertNotEquals(null, testPersonDefaultConstructor.getAddress());
		assertNotEquals(null, testPersonDefaultConstructor.getContact());
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
		
		Validator validator = this.getEntityValidatorFactory().getValidator();

		// low bounds
		testPersonDefaultConstructor.setAlias("A");
		assertEquals(0, validator.validate(testPersonDefaultConstructor).size());

		// in bounds
		testPersonDefaultConstructor.setAlias("Abc");
		assertEquals(0, validator.validate(testPersonDefaultConstructor).size());

		// high bounds
		testPersonDefaultConstructor.setAlias("ABCDEFGHIJKLMNOP");
		assertEquals(0, validator.validate(testPersonDefaultConstructor).size());

		// out of bounds
		testPersonDefaultConstructor.setAlias("ABCDEFGHIJKLMNOPQRSTU");
		assertEquals(1, validator.validate(testPersonDefaultConstructor).size());

		// ---------------- Name ----------------
		Name testName = new Name();
		// default constructor
		assertNull(testName.getFamily());
		assertEquals(null, testName.getGiven());

		// basic tests
		testName.setFamily("family");
		testName.setGiven("given");
		assertEquals("family", testName.getFamily());
		assertEquals("given", testName.getGiven());

		// low bounds
		testName.setFamily("F");
		testName.setGiven("G");
		assertEquals(0, validator.validate(testName).size());

		// in bounds
		testName.setFamily("Family");
		testName.setGiven("Given");
		assertEquals(0, validator.validate(testName).size());

		// high bounds
		testName.setFamily("FamilyFamilyFamilyFamilyFamilyF");
		testName.setGiven("GivenGivenGivenGivenGivenGivenG");
		assertEquals(0, validator.validate(testName).size());

		// out of bounds
		testName.setFamily("FamilyFamilyFamilyFamilyFamilyFamily");
		testName.setGiven("GivenGivenGivenGivenGivenGivenGiven");
		assertEquals(2, validator.validate(testName).size());

		// ---------------- Address ----------------
		Address testAdress = new Address();
		// default constructor
		assertEquals(null, testAdress.getStreet());
		assertEquals(null, testAdress.getPostCode());
		assertEquals(null, testAdress.getCity());

		// basic tests
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
		assertEquals(0, validator.validate(testAdress).size());

		// in bounds
		testAdress.setStreet("street");
		testAdress.setPostCode("postcode");
		testAdress.setCity("city");
		assertEquals(0, validator.validate(testAdress).size());

		// high bounds
		testAdress.setStreet("012345678901234567890123456789012345678901234567890123456789123");
		testAdress.setPostCode("012345678901234");
		testAdress.setCity("012345678901234567890123456789012345678901234567890123456789123");
		assertEquals(0, validator.validate(testAdress).size());

		// out of bounds
		testAdress.setStreet("0123456789012345678901234567890123456789012345678901234567891234");
		testAdress.setPostCode("0123456789012345");
		testAdress.setCity("0123456789012345678901234567890123456789012345678901234567891234");
		assertEquals(3, validator.validate(testAdress).size());

		// ---------------- Contact ----------------
		Contact testContact = new Contact();
		// default constructor
		assertEquals(null, testContact.getEmail());
		assertEquals(null, testContact.getPhone());

		// basic tests
		testContact.setEmail("this@mail.com");
		testContact.setPhone("01012");
		assertEquals("this@mail.com", testContact.getEmail());
		assertEquals("01012", testContact.getPhone());

		// low bounds
		testContact.setEmail("a@b");
		testContact.setPhone("");
		assertEquals(0, validator.validate(testContact).size());

		// in bounds
		testContact.setEmail("broker@models.com");
		testContact.setPhone("00111201230");
		assertEquals(0, validator.validate(testContact).size());

		// high bounds
		testContact.setEmail("012345678901234567890123456789@01234567890123456789012345678901");
		testContact.setPhone("0123456789012345678901234567890");
		assertEquals(0, validator.validate(testContact).size());

		// out of bounds
		testContact.setEmail("012345678901234567890123456789@012345678901234567890123456789012");
		testContact.setPhone("01234567890123456789012345678901");
		assertEquals(2, validator.validate(testContact).size());

	}

	@Test
	public void testLifeCycle() {
		// @param: persistence-unit-name

		// Create Object =========================
		final long personIdentity;
		
		Person person = new Person();
		person.setAlias("person");
		
		person.setAvatar(new Document("name", "mytype", new byte[32], new byte[32]));
		person.setPasswordHash(Person.passwordHash("password"));
		person.setContact(new Contact("abc@test.de", "1234"));
		person.setAddress(new Address("street", "12346", "Here"));
		person.setName(new Name("foo", "bar"));
		
		
		// start
		EntityManager entityManager = emf.createEntityManager();
		try {	
			entityManager.getTransaction().begin();
			entityManager.persist(person);
			entityManager.getTransaction().commit();
			personIdentity = person.getIdentity();
			assertNotEquals(0, personIdentity);
			this.getWasteBasket().add(personIdentity);
		} finally {
			if(entityManager.getTransaction().isActive()) entityManager.getTransaction().rollback();
			entityManager.clear();
			entityManager.close();
		}	

		// Update Entity =========================
		entityManager = emf.createEntityManager();
		try {				
			entityManager.getTransaction().begin();
			Person p1 = entityManager.find(Person.class, personIdentity);
			p1.setAlias("New Alias");			
			entityManager.getTransaction().commit();
			entityManager.clear();
			entityManager.getTransaction().begin();
			
			p1 = entityManager.find(Person.class, personIdentity);
			assertEquals("New Alias", p1.getAlias());
		} finally {
			if(entityManager.getTransaction().isActive()) entityManager.getTransaction().rollback();
			entityManager.clear();
			entityManager.close();
		}	

		// Delete Entity =========================
		entityManager = emf.createEntityManager();
		try {	
			entityManager.getTransaction().begin();
			Person p2 = entityManager.find(Person.class, personIdentity);
			entityManager.remove(p2);
			entityManager.getTransaction().commit();
		} finally {
			if(entityManager.getTransaction().isActive()) entityManager.getTransaction().rollback();
			entityManager.clear();
			entityManager.close();
		}	
	}
}