package deti.tqs.moliceiro_meals.controller.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewSwitcherController {

    @GetMapping("/")
    public String viewSwitcher() {
        return "pages/view-switcher";
    }
    
    @GetMapping("/customer-redirect")
    public String customerRedirect() {
        return "redirect:/customer";
    }
    
    @GetMapping("/staff-redirect")
    public String staffRedirect() {
        return "redirect:/staff";
    }
}