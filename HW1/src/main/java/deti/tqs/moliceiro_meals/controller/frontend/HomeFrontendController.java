package deti.tqs.moliceiro_meals.controller.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeFrontendController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Home - Moliceiro Meals");
        return "pages/index";
    }
}