package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.HashMap;

public class TicketServiceImpl implements TicketService {

    private final TicketPaymentService ticketPaymentService = new TicketPaymentServiceImpl();
    private final SeatReservationService seatReservationService  = new SeatReservationServiceImpl();
    /**
     * Should only have private methods other than the one below.
     */

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        HashMap<TicketTypeRequest.Type, Integer> ticketRequestMap = new HashMap<>();
        for(TicketTypeRequest ticketTypeRequest: ticketTypeRequests) {
            buildTicketRequestMap(ticketRequestMap, ticketTypeRequest);
        }
        if(isValidRequest(ticketRequestMap)) {
            seatReservationService.reserveSeat(1, calculateNumberOfSeatsToAllocate(ticketRequestMap));
            ticketPaymentService.makePayment(1, calculatePayment(ticketRequestMap));
        }
        else {
            throw new InvalidPurchaseException();
        }
    }

    /**
     * Calculates the amount that needs to be paid for the requested tickets
     * @param ticketRequestMap map which contains all the ticket types and their count
     * @return amount to be paid
     */
    private int calculatePayment(HashMap<TicketTypeRequest.Type, Integer> ticketRequestMap) {
        int adult_count = ticketRequestMap.get(TicketTypeRequest.Type.ADULT);
        int child_count = ticketRequestMap.get(TicketTypeRequest.Type.CHILD);
        return adult_count*20 + child_count*10;
    }

    /**
     * Calculates the number of seats to be reserved for the requested tickets
     * @param ticketRequestMap map which contains all the ticket types and their count
     * @return number of seats to be reserved
     */
    private int calculateNumberOfSeatsToAllocate(HashMap<TicketTypeRequest.Type, Integer> ticketRequestMap) {
        int adult_count = ticketRequestMap.get(TicketTypeRequest.Type.ADULT);
        int child_count = ticketRequestMap.get(TicketTypeRequest.Type.CHILD);
        return adult_count + child_count;
    }

    /**
     * build ticket request map which holds different ticket types and their count
     * @param ticketRequestMap initialized map or updated map
     * @param ticketTypeRequest ticket request
     */
    private void buildTicketRequestMap(HashMap<TicketTypeRequest.Type, Integer> ticketRequestMap,
                                TicketTypeRequest ticketTypeRequest) {
        int ticket_count = ticketRequestMap.getOrDefault(ticketTypeRequest.getTicketType(), 0);
        ticketRequestMap.put(ticketTypeRequest.getTicketType(), ticketTypeRequest.getNoOfTickets() + ticket_count);
    }

    /**
     * This method contains validation logic for the incoming request
     * @param ticketRequestMap map which contains all the ticket types and their count
     * @return request is valid or not
     */
    private boolean isValidRequest(HashMap<TicketTypeRequest.Type, Integer> ticketRequestMap) {
        boolean isValid = true;
        int no_of_tickets = ticketRequestMap.values().stream().mapToInt(Integer::intValue).sum();
        int adult_count = ticketRequestMap.getOrDefault(TicketTypeRequest.Type.ADULT, 0);
        int child_count = ticketRequestMap.getOrDefault(TicketTypeRequest.Type.CHILD, 0);
        int infant_count = ticketRequestMap.getOrDefault(TicketTypeRequest.Type.INFANT, 0);
        if(no_of_tickets > 20 || no_of_tickets <= 0) {
            return false;
        }
        if(adult_count != infant_count) {
            return false;
        }
        if(child_count>0 || infant_count > 0) {
            isValid = adult_count > 0;
        }

        return isValid;
    }

}
