package de.sb.broker.rest;

import static org.junit.Assert.*;

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
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import de.sb.broker.model.Address;
import de.sb.broker.model.Auction;
import de.sb.broker.model.Contact;
import de.sb.broker.model.Document;
import de.sb.broker.model.Name;
import de.sb.broker.model.Person;
import de.sb.broker.model.Person.Group;
import junit.framework.Assert;

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
	public void testAuctionRelationQueries() {
		
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
