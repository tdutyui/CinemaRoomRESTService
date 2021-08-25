package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class Controller {
    int totalRows = 9;
    int totalColumns = 9;
    List<Seat> purchasedSeats = new ArrayList<>();
    List<Map<String, Object>> tokens = new ArrayList<>();
    int income = 0;

    @GetMapping("/seats")
    public Map<String, Object> getSeatsInfo() {
        Map<String, Object> info = new HashMap<>();
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= totalRows; i++) {
            for (int j = 1; j <= totalColumns; j++) {
                Seat seat = new Seat(i, j);
                if (i <= 4) {
                    seat.setPrice(10);
                } else {
                    seat.setPrice(8);
                }
                seats.add(seat);
            }
        }
        seats.removeAll(purchasedSeats);
        info.put("total_rows", Integer.valueOf(totalRows));
        info.put("total_columns", Integer.valueOf(totalColumns));
        info.put("available_seats", seats);
        return info;
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> returnPrice(@RequestBody Seat seat) {
        if (seat.getRow() > totalRows || seat.getRow() <= 0 ||
                seat.getColumn() > totalColumns || seat.getColumn() <= 0) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "The number of a row or a column is out of bounds!");
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
        if (seat.getRow() <= 4) {
            seat.setPrice(10);
        } else {
            seat.setPrice(8);
        }
        if (purchasedSeats.contains(seat)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "The ticket has been already purchased!");
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        } else {
            Map<String, Object> ticketToken = new LinkedHashMap<>();
            ticketToken.put("token", UUID.randomUUID().toString());
            ticketToken.put("ticket", seat);
            tokens.add(ticketToken);
            purchasedSeats.add(seat);
            income += seat.getPrice();
            return new ResponseEntity(ticketToken, HttpStatus.OK);
        }
    }

    @PostMapping("/return")
    public ResponseEntity<?> refundTicket(@RequestBody Map<String, String> token) {
        Map<String, Seat> refundedTicket = new HashMap<>();
        int found = 0;
        for (Map<String, Object> ticketToken: tokens) {
            if (ticketToken.get("token").equals(token.get("token"))) {
                refundedTicket.put("returned_ticket", (Seat) ticketToken.get("ticket"));
                tokens.remove(ticketToken);
                purchasedSeats.remove(refundedTicket.get("returned_ticket"));
                found++;
                income -= refundedTicket.get("returned_ticket").getPrice();
                break;
            }
        }
        if (found == 0) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Wrong token!");
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(refundedTicket, HttpStatus.OK);
        }
    }
    
    @PostMapping("/stats")
    public ResponseEntity<?> getStats(@RequestParam(required = false) String password) {
        if (password == null || !password.equals("super_secret")) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "The password is wrong!");
            return new ResponseEntity(error, HttpStatus.UNAUTHORIZED);
        } else {
            Map<String, Integer> stats = new HashMap<>();
            stats.put("current_income", income);
            stats.put("number_of_available_seats", 81 - purchasedSeats.size());
            stats.put("number_of_purchased_tickets", purchasedSeats.size());
            return new ResponseEntity<>(stats, HttpStatus.OK);
        }
    }
}
