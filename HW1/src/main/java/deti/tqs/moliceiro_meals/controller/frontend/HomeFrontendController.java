package deti.tqs.moliceiro_meals.controller.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeFrontendController {

    @GetMapping("/")
    public String home(Model model) {
        // Add any data you want to display on the home page
        model.addAttribute("welcomeMessage", "Welcome to Moliceiro Meals!");
        return "index"; // Renders the Thymeleaf template `index.html`
    }
}