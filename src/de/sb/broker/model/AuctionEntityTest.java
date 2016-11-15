package de.sb.broker.model;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.validation.Validator;

import org.junit.Test;

public class AuctionEntityTest extends EntityTest {

	private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("broker");

	@Test
	public void testConstraints() {
		Auction auction1 = new Auction();

		auction1.setTitle("Auction to test");
		auction1.setDescription("My FooBar Description");
		auction1.setClosureTimestamp(1482537600); // 2016-12-24T00:00:00+00:00
		auction1.setUnitCount((short) 1);
		auction1.setAskingPrice(1337);
		auction1.setSeller(new Person());
		auction1.setVersion(1);

		// Some basic tests
		assertEquals("Auction to test", auction1.getTitle());
		assertEquals("My FooBar Description", auction1.getDescription());
		assertEquals(1482537600, auction1.getClosureTimestamp());
		// assertEquals((short) 1, auction1.getUnitCount());
		assertEquals(1337, auction1.getAskingPrice());
		assertNotEquals(null, auction1.getSeller());
		assertEquals(1, auction1.getVersion());

		// Legal Test Cases
		Validator validator = getEntityValidatorFactory().getValidator();
		auction1.setAskingPrice(1);
		assertEquals(0, validator.validate(auction1).size());

		auction1.setAskingPrice(Long.MAX_VALUE);
		assertEquals(0, validator.validate(auction1).size());

		auction1.setDescription("x");
		assertEquals(0, validator.validate(auction1).size());

		// Negative Test Cases

		auction1.setAskingPrice(0);
		assertEquals(1, validator.validate(auction1).size());

		auction1.setAskingPrice(-1);
		auction1.setUnitCount((short) -1);
		assertEquals(2, validator.validate(auction1).size());

		auction1.setAskingPrice(1);
		auction1.setUnitCount((short) 1);
		auction1.setTitle("");
		assertEquals(1, validator.validate(auction1).size());

	}

	@Test
	public void testLifeCycle() {
		// @param: persistence-unit-names
		EntityManager entityManager = emf.createEntityManager();

		long auctionIdentity;
		
		// Create Object ========================
		Person auctionPerson = new Person();
		auctionPerson.setAlias("auctionPerson");
		
		auctionPerson.setAvatar(new Document("auctionPersonDoc", "mytype", new byte[32], new byte[32]));
		auctionPerson.setPasswordHash(Person.passwordHash("password"));
		auctionPerson.setContact(new Contact("abc@test.de", "1234"));
		auctionPerson.setAddress(new Address("street", "12346", "Here"));
		auctionPerson.setName(new Name("foo", "bar"));

		Auction auction = new Auction(auctionPerson);
		auction.setAskingPrice(42);
		auction.setDescription("this is an auction description");
		auction.setTitle("this is an auction title");
		auction.setUnitCount((short) 12);
		

		// start
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(auction);
			entityManager.getTransaction().commit();
			auctionIdentity = auction.getIdentity();
			assertNotEquals(0, auctionIdentity);
			this.getWasteBasket().add(auctionIdentity);
		} finally {
			if (entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
			entityManager.clear();
			entityManager.close();
		}

		// Update Entity ========================

		entityManager = emf.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			Auction a1 = entityManager.find(Auction.class, auctionIdentity);
			a1.setTitle("New Auction");
			entityManager.getTransaction().commit();
			assertEquals("", a1.getTitle());
			assertEquals("New Auction", a1.getTitle());
		} finally {
			if (entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
			entityManager.clear();
			entityManager.close();
		}

		// Delete Entity ========================

		entityManager = emf.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			Auction a2 = entityManager.find(Auction.class, auctionIdentity);
			entityManager.remove(a2);
			entityManager.getTransaction().commit();
		} finally {
			if (entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
			entityManager.clear();
			entityManager.close();
		}
		// Merge Entities - basic example

		Person bidPerson =  new Person();
		bidPerson.setAlias("bidPerson");
		
		bidPerson.setAvatar(new Document("bidPersonDoc", "mytype", new byte[32], new byte[32]));
		bidPerson.setPasswordHash(Person.passwordHash("password"));
		bidPerson.setContact(new Contact("abc@test.de", "1234"));
		bidPerson.setAddress(new Address("street", "12346", "Here"));
		bidPerson.setName(new Name("foobidPerson", "barbidPerson"));
		entityManager = emf.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			Auction a3 = entityManager.find(Auction.class, auctionIdentity);			
			Bid bid = new Bid(a3, bidPerson);
			bid.setPrice(12345);
			assertEquals(a3.getBids().size(), 0);
			a3.getBids().add(bid);
			entityManager.refresh(entityManager.merge(a3));
			entityManager.getTransaction().commit();
			assertEquals(a3.getBids().size(), 1);
		} finally {
			if (entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
			entityManager.clear();
			entityManager.close();
		}
	}

}