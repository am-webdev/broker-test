package de.sb.broker.rest;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
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

public class AuctionServiceTest extends ServiceTest {

	/**
	 * Tests for the method AuctionService::getAuctions()
	 * TODO Test the query parameter
	 * TODO Test Exceptions 
	 * @author Thilo
	 */
	@Test
	public void testCriteriaQueries() {
		
		/**
		 * 
		 */
		WebTarget webTarget = newWebTarget("badUsername", "badPassword").path("people/");
		assertEquals(200, webTarget.request().get().getStatus());
		
		Response response = null;
		List<Person> l = null;

		Person person = createValidPerson();

		webTarget = newWebTarget("badUsername", "badPassword").path("people/");
		final Invocation.Builder builder = webTarget.request();
		builder.header("Set-password", "password");
		response = builder.put(Entity.json(person));		
		assertNotEquals(new Long(0), response.readEntity(Long.class));
		assertEquals(200, response.getStatus());
		
		Class<?> c = Person.class;
		List<Field> fields = new ArrayList<Field>();

		do{
			fields.addAll(Arrays.asList(c.getDeclaredFields()));
			c = c.getSuperclass();
		}while(c != null);
		
		for(Field f: fields){
			
			String uppercaseName = f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
			
			if(f.getType() == Integer.TYPE){
				
				// version
				final int lower = 1, upper = 100;
				response = newWebTarget("root", "root")
						.path("people")
						.queryParam("lower" + uppercaseName, lower)
						.queryParam("upper" + uppercaseName, upper)
						.request()
						.accept(MediaType.APPLICATION_JSON)
						.get();
				
				l = response.readEntity(new GenericType<List<Person>>() {});
				for(Person p : l){
//					assertTrue(lower <= p.getClass().getField(f.getName()).getInt(null));
					assertTrue(upper >= p.getVersion());
				}
				
			}
		}
		
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
	}
	
	/**
	 * Tests for all methods using a "identity" as a path parameter
	 * TODO GET "services/auctions/{identity}"
	 * TODO GET "services/auctions/{identity}/bid"
	 * 
	 * TODO Exceptions nicht vergessen
	 * 
	 * @author Andreas
	 */
	@Test
	public void testIdentityQueries() {
		WebTarget wt = null;
		
		// Create new Auction(s) for Test-Person
		Person testPersonTmp = PersonServiceTest.createValidPerson();
		Person testPerson = null;
		
		wt = newWebTarget("sascha", "sascha").path("people/");
		final Invocation.Builder builder = wt.request();
		builder.accept(MediaType.TEXT_PLAIN);
		builder.header("Set-password", "sascha");
		final Response personResponse = builder.put(Entity.json(testPersonTmp));
		getWasteBasket().add(personResponse.readEntity(Long.class));		
		assertNotEquals(new Long(0), personResponse.readEntity(Long.class));
		assertEquals(200, personResponse.getStatus());
		

		wt = newWebTarget("sascha", "sascha").path("people/"+personResponse.readEntity(Long.class));
		final Invocation.Builder actualPersonBuilder = wt.request();
		actualPersonBuilder.accept(MediaType.APPLICATION_JSON);
		final Response actualPersonResponse = actualPersonBuilder.get();
		testPerson = actualPersonResponse.readEntity(Person.class);
		assertEquals(testPersonTmp.getAlias(), testPerson.getAlias());
		

		Auction testAuction = new Auction(testPerson);
		testAuction.setAskingPrice(42);
		testAuction.setDescription("Foobar setDescription");
		testAuction.setTitle("Foobar Auction");
		testAuction.setUnitCount((short) 1);
		testAuction.setClosureTimestamp(System.currentTimeMillis()+(7*24*60*60*1000)); // +7d
		
		wt = newWebTarget("sascha", "sascha").path("auctions/");
		final Invocation.Builder auctionbuilder = wt.request();
		auctionbuilder.accept(MediaType.TEXT_PLAIN);
		auctionbuilder.header("Set-password", "sascha");
		final Response auctionResponse = auctionbuilder.put(Entity.json(testAuction));
		getWasteBasket().add(auctionResponse.readEntity(Long.class));		
		assertNotEquals(new Long(0), auctionResponse.readEntity(Long.class));
		assertEquals(200, auctionResponse.getStatus());
		
		
		
		// TODO Set some bids for the Test Auctions
		
		// TODO Assert on GET methods
	}
	
	/**
	 * Tests for 
	 * TODO POST "services/auctions/{identity}/bid"
	 * TODO PUT "services/auctions/"
	 * 
	 * TODO Exceptions nicht vergessen
	 * 
	 * @author Ahmed
	 */
	@Test
	public void testBidRelations() {
		
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
