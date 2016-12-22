package de.sb.broker.rest;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.NoResultException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Test;

import de.sb.broker.model.Address;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;

import de.sb.broker.model.Auction;
import de.sb.broker.model.Bid;

import de.sb.broker.model.Contact;
import de.sb.broker.model.Document;
import de.sb.broker.model.Name;
import de.sb.broker.model.Person;


public class PersonServiceTest extends ServiceTest {
	
	

	/**
	 * Tests for the method PersonService::getPeople()
	 * TODO Test the query parameter
	 * TODO Test Exceptions 
	 * @author Thilo
	 */
	@Test
	public void testCriteriaQueries() {
		/**
		 * test criteria queries
		 */
		Response response = null;
		List<Person> l = null;
		
		// version
		final int lowerVersion = 1, upperVersion = 100;
		response = newWebTarget("root", "root")
				.path("people")
				.queryParam("lowerVersion", lowerVersion)
				.queryParam("upperVersion", upperVersion)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		
		l = response.readEntity(new GenericType<List<Person>>() {});
		for(Person p : l){
			assertTrue(lowerVersion <= p.getVersion());
			assertTrue(upperVersion >= p.getVersion());
		}
		assertEquals(200, response.getStatus());
		
		// creationTimeStamp
		final long lowerCreationTimeStamp = 0, upperCreationTimeStamp = 100;
		response = newWebTarget("root", "root")
				.path("people")
				.queryParam("lowerCreationTimeStamp", lowerCreationTimeStamp)
				.queryParam("upperCreationTimeStamp", upperCreationTimeStamp)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		
		l = response.readEntity(new GenericType<List<Person>>() {});
		for(Person p : l){
			assertTrue(lowerCreationTimeStamp <= p.getCreationTimeStamp());
			assertTrue(upperCreationTimeStamp >= p.getCreationTimeStamp());
		}
		assertEquals(200, response.getStatus());
		
		// alias
		final String alias = "T-High";
		response = newWebTarget("root", "root")
				.path("people")
				.queryParam("alias", alias)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		
		l = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("alias wrong", alias, l.get(0).getAlias());
		assertEquals(200, response.getStatus());
		
	}
	
	/**
	 * Tests for 
	 * GET "services/people/{identity}"
	 * TODO GET "services/people/requester"
	 * GET "services/people/{identity}/avatar"
	 * 
	 * Exceptions nicht vergessen
	 * @author Andreas
	 */
	@Test
	public void testIdentityQueries() {
		
		WebTarget wt = null;
		List<Person> personList = null;
		Response res = null;
		
		res = newWebTarget("root", "root")
				.path("people").request().accept(MediaType.APPLICATION_JSON)
				.get();
		
		personList = res.readEntity(new GenericType<List<Person>>() {});
		
		assertEquals(200, res.getStatus());
		assertNotEquals(0, personList.size());
		
		res = newWebTarget("root", "root")
				.path("people").path(""+personList.get(0).getIdentity()).request().accept(MediaType.APPLICATION_JSON)
				.get();
		Person currentPerson = res.readEntity(Person.class);
		assertEquals(200, res.getStatus());
		assertEquals(personList.get(0).getIdentity(), currentPerson.getIdentity());
		assertEquals(personList.get(0).getAlias(), currentPerson.getAlias());
		
		res = newWebTarget("root", "root")
				.path("people")
				.path("932")
				.path("avatar")
				.request()
				.get();
		
		byte[] currentAvatar = res.readEntity(byte[].class);
		assertEquals(200, res.getStatus());		
		assertNotEquals(0, currentAvatar.length);
		
		res = newWebTarget("root", "root")
				.path("people")
				.path("783")
				.path("avatar")
				.request()
				.get();
		
		currentAvatar = res.readEntity(byte[].class);
		assertEquals(404, res.getStatus());

		res = newWebTarget("root", "root")
				.path("people").path("99999999").request().accept(MediaType.APPLICATION_JSON)
				.get();
		currentPerson = res.readEntity(Person.class);
		assertEquals(404, res.getStatus());
		

		res = newWebTarget("root", "root")
				.path("requester").request().accept(MediaType.APPLICATION_JSON)
				.get();
		currentPerson = res.readEntity(Person.class);
		//assertEquals(200, res.getStatus());
		//assertEquals("root", currentPerson.getAlias());
		
	}
	
	/**
	 * Tests for
	 * TODO GET "services/people/{identity}/auctions"
	 * 
	 * TODO Exceptions nicht vergessen
	 * 
	 * @author Martin
	 */
	@Test
	public void testAuctionRelationQueries() throws Exception {
		try {	
			WebTarget webTarget = newWebTarget("root", "root")
					.path("people/2/auctions");
			Response response = webTarget
					.request()
					.accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
					.get();
			List<Auction> all = response.readEntity(new GenericType<List<Auction>>() {});
			assertEquals("Rennrad wie neu", all.get(2).getTitle());
		} catch(NoResultException e){
			throw new ClientErrorException(e.getMessage(), 404);
		}
	}
	
	/**
	 * Tests for
	 * TODO GET "services/people/{identity}/bids"
	 * 
	 * TODO Exceptions nicht vergessen
	 * 
	 * @author Martin
	 */
	@Test
	public void testBidRelationQueries() {
		try {	
			Response response = newWebTarget("root", "root")
					.path("people/2/bids")
					.request()
					.accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
					.get();
			List<Bid> all = response.readEntity(new GenericType<List<Bid>>() {});
			assertEquals(1, all.get(0).getPrice());
		} catch(NoResultException e){
			throw new ClientErrorException(e.getMessage(), 404);
		}
	}
	
	/**
	 * Tests for
	 * TODO PUT "services/people/"
	 * TODO PUT "services/people/{identity}/avatar"
	 * 
	 * TODO Exceptions nicht vergessen
	 * 
	 * @author Timo
	 */
	@Test
	public void testLifeCycle() {
		WebTarget webTarget = newWebTarget("badUsername", "badPassword").path("people/");
		assertEquals(200, webTarget.request().get().getStatus());

		Person person = new Person();
		person.setAlias("testPerson");
		
		person.setAvatar(new Document("image/png", new byte[32], new byte[32]));
		person.setContact(new Contact("abc@test.de", "1234"));
		person.setAddress(new Address("street", "12346", "Here"));
		person.setName(new Name("foo", "bar"));


		webTarget = newWebTarget("badUsername", "badPassword").path("people/");
		final Invocation.Builder builder = webTarget.request();
		builder.accept(MediaType.TEXT_PLAIN);
		builder.header("Set-password", "password");
		final Response response = builder.put(Entity.json(person));
		getWasteBasket().add(response.readEntity(Long.class));		
		assertNotEquals(new Long(0), response.readEntity(Long.class));
		assertEquals(200, response.getStatus());
	}
	

	
	protected static Person createValidPerson() {
		Person rtn = new Person();
		rtn.setAlias("Tester");
		rtn.setAvatar(new Document("image/png", new byte[32], new byte[32]));
		rtn.setPasswordHash(Person.passwordHash("password"));
		rtn.setContact(new Contact("foo@bar.bf", "1234"));
		rtn.setAddress(new Address("FoobarStreet", "12346", "Fbar"));
		rtn.setName(new Name("Foo", "Bar"));
		
		return rtn;
	}

}
