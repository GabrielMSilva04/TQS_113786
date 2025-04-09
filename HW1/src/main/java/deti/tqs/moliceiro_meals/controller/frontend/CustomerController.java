package deti.tqs.moliceiro_meals.controller.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    
    // Keep this method to avoid breaking URLs in existing links
    @GetMapping("/reservations")
    public String redirectToReservations() {
        return "redirect:/customer/reservation/s";
    }
}