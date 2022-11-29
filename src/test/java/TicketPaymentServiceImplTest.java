import org.junit.Test;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

/**
 *
 */
public class TicketPaymentServiceImplTest {

    TicketService ticketService = new TicketServiceImpl();

    /**
     * Positive Scenario without any errors
     */
    @Test
    public void testPaymentSuccess() {

        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[3];
        ticketTypeRequests[0] = buildTicketTypeRequest(TicketTypeRequest.Type.ADULT, 5);
        ticketTypeRequests[1] = buildTicketTypeRequest(TicketTypeRequest.Type.CHILD, 4);
        ticketTypeRequests[2] = buildTicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);

        ticketService.purchaseTickets(Long.valueOf(1), ticketTypeRequests);
    }

    /**
     * Negative Scenario when infant count is more than adult count
     */
    @Test(expected = InvalidPurchaseException.class)
    public void testPaymentFailureInfantCount() {

        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[3];
        ticketTypeRequests[0] = buildTicketTypeRequest(TicketTypeRequest.Type.ADULT, 5);
        ticketTypeRequests[1] = buildTicketTypeRequest(TicketTypeRequest.Type.CHILD, 4);
        ticketTypeRequests[2] = buildTicketTypeRequest(TicketTypeRequest.Type.INFANT, 6);

        ticketService.purchaseTickets(Long.valueOf(1), ticketTypeRequests);
    }

    /**
     * Negative Scenario when ticket count exceeded 20
     */
    @Test(expected = InvalidPurchaseException.class)
    public void testPaymentFailureExceededMaximumCount() {

        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[3];
        ticketTypeRequests[0] = buildTicketTypeRequest(TicketTypeRequest.Type.ADULT, 10);
        ticketTypeRequests[1] = buildTicketTypeRequest(TicketTypeRequest.Type.CHILD, 5);
        ticketTypeRequests[2] = buildTicketTypeRequest(TicketTypeRequest.Type.INFANT, 6);

        ticketService.purchaseTickets(Long.valueOf(1), ticketTypeRequests);
    }

    /**
     * Negative Scenario when child ticket requested without adult ticket
     */
    @Test(expected = InvalidPurchaseException.class)
    public void testPaymentFailureChildWithoutAdult() {

        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[3];
        ticketTypeRequests[0] = buildTicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);
        ticketTypeRequests[1] = buildTicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        ticketTypeRequests[2] = buildTicketTypeRequest(TicketTypeRequest.Type.INFANT, 0);

        ticketService.purchaseTickets(Long.valueOf(1), ticketTypeRequests);
    }

    /**
     * Negative Scenario when infant ticket requested without adult ticket
     */
    @Test(expected = InvalidPurchaseException.class)
    public void testPaymentFailureInfantWithoutAdult() {

        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[3];
        ticketTypeRequests[0] = buildTicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);
        ticketTypeRequests[1] = buildTicketTypeRequest(TicketTypeRequest.Type.CHILD, 0);
        ticketTypeRequests[2] = buildTicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        ticketService.purchaseTickets(Long.valueOf(1), ticketTypeRequests);
    }

    /**
     * Negative Scenario when ticket count is zero
     */
    @Test(expected = InvalidPurchaseException.class)
    public void testPaymentFailureZeroTickets() {

        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[3];
        ticketTypeRequests[0] = buildTicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);
        ticketTypeRequests[1] = buildTicketTypeRequest(TicketTypeRequest.Type.CHILD, 0);
        ticketTypeRequests[2] = buildTicketTypeRequest(TicketTypeRequest.Type.INFANT, 0);

        ticketService.purchaseTickets(Long.valueOf(1), ticketTypeRequests);
    }

    /**
     * private method to build ticket type request object
     * @param type ticket type such as ADULT or CHILD or INFANT
     * @param no_of_tickets number of tickets to buy
     * @return Ticket type Request object
     */
    private TicketTypeRequest buildTicketTypeRequest(TicketTypeRequest.Type type, int no_of_tickets) {
        return new TicketTypeRequest(type, no_of_tickets);
    }
}
