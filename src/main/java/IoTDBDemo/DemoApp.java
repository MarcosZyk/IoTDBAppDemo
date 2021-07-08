package IoTDBDemo;

import IoTDBDemo.service.MonitorService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication  // 相当于@SpringBootConfiguration、@EnableAutoConfiguration和@ComponentScan三个注解  目的是开启自动配
public class DemoApp extends SpringBootServletInitializer {

    public static void main(String[] args) throws Exception {
//        MonitorService.startMonitor();
        SpringApplication.run(DemoApp.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(DemoApp.class);
    }

}
