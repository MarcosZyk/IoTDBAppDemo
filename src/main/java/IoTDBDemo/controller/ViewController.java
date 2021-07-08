package IoTDBDemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {
    @RequestMapping(value = "/index")
    public String getIndex() {
        return "monitor";
    }
}
