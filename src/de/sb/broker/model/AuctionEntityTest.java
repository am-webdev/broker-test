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

		// Create Object ========================

		Auction auction = new Auction();

		// start
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(auction);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			// Freak out
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
			Auction a1 = entityManager.find(Auction.class, 1 /* (auctionIdentity) */);
			// Auction a1 = entityManager.getReference(Auction.class, 1 /*
			// (auctionIdentity) */);
			a1.setTitle("New Auction");
			// entityManager.flush();
			entityManager.getTransaction().commit();
			assertEquals("", a1.getTitle());
			assertEquals("New Auction", a1.getTitle());
		} catch (Exception e) {
			// Freak out
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
			Auction a2 = entityManager.find(Auction.class, 1 /* (auctionIdentity) */);
			entityManager.remove(a2);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			// Freak out
		} finally {
			if (entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
			entityManager.clear();
			entityManager.close();
		}
		// Merge Entities - basic example

		Bid auction3Bid = new Bid();
		Person auction3Person = new Person();
		entityManager = emf.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			Auction a3 = entityManager.find(Auction.class, 1 /* (auctionIdentity) */);
			assertEquals(a3.getBids().size(), 0);
			assertNotEquals(auction3Person, a3.getSeller());
			a3.getBids().add(auction3Bid);
			a3.setSeller(auction3Person);
			entityManager.refresh(a3);
			entityManager.getTransaction().commit();
			assertEquals(a3.getBids().size(), 1);
			assertEquals(auction3Person, a3.getSeller());
		} catch (Exception e) {
			// Freak out
		} finally {
			if (entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
			entityManager.clear();
			entityManager.close();
		}
	}

}