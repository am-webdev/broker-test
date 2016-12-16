package de.sb.broker.rest;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.NoResultException;
import javax.transaction.TransactionalException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.Invocation.Builder;

import org.junit.Test;

import de.sb.broker.model.Auction;
import de.sb.broker.model.Bid;

public class PersonServiceTest extends ServiceTest {

	/**
	 * Tests for the method PersonService::getPeople()
	 * TODO Test the query parameter
	 * TODO Test Exceptions 
	 * @author Thilo
	 */
	@Test
	public void testCriteriaQueries() {
		
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
		
	}

}
