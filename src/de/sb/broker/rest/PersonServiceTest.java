package de.sb.broker.rest;

import static org.junit.Assert.*;

<<<<<<< HEAD
import java.util.List;

import javax.persistence.NoResultException;
import javax.transaction.TransactionalException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
=======
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientResponse;
import org.junit.Test;

import de.sb.broker.model.Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.ws.rs.QueryParam;
>>>>>>> c7c88122a8df7b58fcb0e39ed5170306b403e849
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
<<<<<<< HEAD
import javax.ws.rs.client.Invocation.Builder;

import org.junit.Test;

import de.sb.broker.model.Auction;
import de.sb.broker.model.Bid;
=======

import org.junit.Test;

import de.sb.broker.model.Address;
import de.sb.broker.model.Auction;
import de.sb.broker.model.Contact;
import de.sb.broker.model.Document;
import de.sb.broker.model.Name;
import de.sb.broker.model.Person;
import de.sb.broker.model.Person.Group;
import junit.framework.Assert;
>>>>>>> c7c88122a8df7b58fcb0e39ed5170306b403e849

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
	 * TODO GET "services/people/{identity}"
	 * TODO GET "services/people/requester"
	 * TODO GET "services/people/{identity}/avatar"
	 * 
	 * TODO Exceptions nicht vergessen
	 * @author Andreas
	 */
	@Test
	public void testIdentityQueries() {
		
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
			WebTarget webTarget = newWebTarget("sascha", "sascha").path("people/2/auctions").queryParam("title", "Rennrad wie neu");
			Response response = webTarget.request().accept(MediaType.APPLICATION_JSON).get();
			List<Auction> all = response.readEntity(new GenericType<List<Auction>>() {}); //Unmarshalling Error
			assertEquals("Rennrad wie neu", all.get(0).getTitle());
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
			WebTarget webTarget = newWebTarget("sascha", "sascha").path("people/2/bids");
			Response response = webTarget.request().get();
			//System.out.println(response.getStatus());
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
		
		person.setAvatar(new Document("mytype", new byte[32], new byte[32]));
		person.setContact(new Contact("abc@test.de", "1234"));
		person.setAddress(new Address("street", "12346", "Here"));
		person.setName(new Name("foo", "bar"));


		webTarget = newWebTarget("badUsername", "badPassword").path("people/");
		webTarget.request().accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML);
		webTarget.request().header("Set-password", "password");
		Response response = webTarget.request().put(Entity.json(person));
		assertEquals(200, response.getStatus());
	}

}
