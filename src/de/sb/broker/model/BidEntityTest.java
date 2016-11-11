package de.sb.broker.model;

import static org.junit.Assert.*;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.Test;

public class BidEntityTest extends EntityTest {

	private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("broker");

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
		assertEquals(0, validator.validate(legalValueBid).size());

		legalValueBid.setPrice(Long.MAX_VALUE);
		assertEquals(0, validator.validate(legalValueBid).size());

		// Negative Test Cases
		Bid negativValueBid = new Bid();
		negativValueBid.setPrice(-100);
		assertEquals(1, validator.validate(negativValueBid).size());

		// Price for a bid was set to 0 Test Cases
		Bid toLowValueBid = new Bid();
		toLowValueBid.setPrice(0);
		assertEquals(1, validator.validate(toLowValueBid).size());
	}

	@Test
	public void testLifeCycle() {
		// @param: persistence-unit-name

		// Create Object ========================
		Person bidAuctionPerson = new Person();
		bidAuctionPerson.setAlias("bidAuctionPerson");
		Auction bidAuction = new Auction(bidAuctionPerson);
		Person bidPerson =  new Person();
		bidPerson.setAlias("bidPerson");
		Bid bid = new Bid(bidAuction, bidPerson);
		bid.setPrice(12345);
		assertNotEquals(bidPerson, bidAuctionPerson);
		assertNotEquals(bid.getBidder(), bidAuction.getSeller());
		// start
		EntityManager entityManager = emf.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(bid);
			entityManager.flush();
		} catch (Exception e) {
			assertEquals(null, e);
		} finally {
			if (entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
//			entityManager.clear();
			entityManager.close();
		}
		long bidIdentity = 0;
		// Update Entity ========================
		entityManager = emf.createEntityManager();
		try {

			entityManager.getTransaction().begin();
			entityManager.refresh(entityManager.merge(bid));
			bidIdentity = bid.getIdentity();
			Bid b1 = entityManager.find(Bid.class, bidIdentity);
			b1.setPrice(4711);
			entityManager.getTransaction().commit();
			assertEquals(42, b1.getPrice());
			assertEquals(4711, b1.getPrice());
		} catch (Exception e) {
			assertEquals(null, e);
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
			Bid b2 = entityManager.find(Bid.class, bidIdentity);
			entityManager.remove(b2);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			assertEquals(true, false);
		} finally {
			if (entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
			entityManager.clear();
			entityManager.close();
		}

		// Merge Entities - basic example

		Person bid3AuctionPerson = new Person();
		bid3AuctionPerson.setAlias("bid3AuctionPerson");
		Auction bid3Auction = new Auction(bid3AuctionPerson);
		bid3Auction.setAskingPrice(123);
		bid3Auction.setDescription("some description that is different from others");
		entityManager = emf.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			Bid b3 = entityManager.find(Bid.class, bidIdentity);
			// das hier sollte nicht funktionieren, tut es aber!
			assertNotEquals(b3.getAuction(), bid3Auction);
			assertEquals(b3.getAuction(), bid3Auction);
			entityManager.refresh(b3);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			assertEquals(true, false);
		} finally {
			if (entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
			entityManager.clear();
			entityManager.close();
		}

	}
}