package IoTDBDemo.controller;

import IoTDBDemo.model.Computer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import IoTDBDemo.service.MonitorService;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseEntity<List<Computer>> query(@RequestParam Timestamp start, @RequestParam Timestamp end){
        try {
            return ResponseEntity.ok(monitorService.query(start.getTime(),end.getTime()));
        }catch (Exception e){
            return ResponseEntity.status(500).body(null);
        }
    }

}
