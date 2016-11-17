package de.sb.broker.model;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;
import javax.persistence.Persistence;
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
		bidAuctionPerson.setAvatar(new Document("bidAuctionPersonDoc", "mytype", new byte[32], new byte[32]));
		bidAuctionPerson.setPasswordHash(Person.passwordHash("password"));
		bidAuctionPerson.setContact(new Contact("test1@test.de", "1234"));
		bidAuctionPerson.setAddress(new Address("street", "12346", "Here"));
		bidAuctionPerson.setName(new Name("foo", "bar"));
		
		Auction bidAuction = new Auction(bidAuctionPerson);
		bidAuction.setAskingPrice(42);
		bidAuction.setDescription("this is an auction description");
		bidAuction.setTitle("this is an auction title");
		bidAuction.setUnitCount((short) 12);
		
		Person bidPerson =  new Person();
		bidPerson.setAlias("bidPerson");	
		bidPerson.setAvatar(new Document("bidPersonDoc", "mytype", new byte[32], new byte[32]));
		bidPerson.setPasswordHash(Person.passwordHash("password"));
		bidPerson.setContact(new Contact("test2@test.de", "1234"));
		bidPerson.setAddress(new Address("street", "12346", "Here"));
		bidPerson.setName(new Name("foobidPerson", "barbidPerson"));
		
		Bid bid = new Bid(bidAuction, bidPerson);
		bid.setPrice(12345);
		
		assertNotEquals(bid.getBidder(), bidAuction.getSeller());
		
		// Create Entity =====================
		EntityManager entityManager = emf.createEntityManager();
		try {
			
			entityManager.getTransaction().begin();			
			entityManager.persist(bidAuctionPerson);
			entityManager.getTransaction().commit();
			this.getWasteBasket().add(bidAuctionPerson.getIdentity());
			
			entityManager.getTransaction().begin();			
			entityManager.persist(bidPerson);
			entityManager.getTransaction().commit(); 
			this.getWasteBasket().add(bidPerson.getIdentity());
			
			entityManager.getTransaction().begin();			
			entityManager.persist(bidAuction);
			entityManager.getTransaction().commit();
			this.getWasteBasket().add(bidAuction.getIdentity());
			
			entityManager.getTransaction().begin();			
			entityManager.persist(bid);
			entityManager.getTransaction().commit();
			this.getWasteBasket().add(bid.getIdentity());
			
			Bid instance = entityManager.find(bid.getClass(), bid.getIdentity());
			assertEquals("Insert entity", instance, bid);
		
		} finally {
			
			if (entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().rollback();
			}
		}
		
		// Update Entity ========================
		
		try {
			
			bid.setPrice(100);
			entityManager.getTransaction().begin();			
			entityManager.persist(bid);
			entityManager.getTransaction().commit();
			
			Bid instance = entityManager.find(bid.getClass(), bid.getIdentity());
			assertEquals("Update entity", instance.getPrice(), bid.getPrice());
			
			// Auction Reference
			assertEquals("Auction reference", bidAuction, instance.getAuction());
			
			// Bidder Reference
			assertEquals("Bidder reference", bidPerson, instance.getBidder());
		
		} finally {
			
			if (entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().rollback();
			}
		}
		
		// Delete Entity ========================
		
		try {
			entityManager.getTransaction().begin();
			entityManager.remove(bid);
			entityManager.getTransaction().commit();
		} catch (OptimisticLockException exception) {
			assertTrue("Delete relations failed", true);
		}
		
		try {
			
			entityManager.getTransaction().begin();
			entityManager.remove(bidPerson);
			entityManager.getTransaction().commit();
			
			bidPerson = entityManager.find(bidPerson.getClass(), bidPerson.getIdentity()); 
			assertNull("Delete Person", bidPerson);
			
			entityManager.getTransaction().begin();
			entityManager.remove(bidAuction);
			entityManager.getTransaction().commit();
			
			bidAuction = entityManager.find(bidAuction.getClass(), bidAuction.getIdentity()); 
			assertNull("Delete Auction", bidAuction);
			
			entityManager.getTransaction().begin();
			entityManager.remove(bid);
			entityManager.getTransaction().commit();
			
			Bid instance = entityManager.find(bid.getClass(), bid.getIdentity()); 
			assertNull("Delete Bid", instance);
		
		} finally {
			
			if (entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().rollback();
			}
			entityManager.clear();
			entityManager.close();
		}
	}
}