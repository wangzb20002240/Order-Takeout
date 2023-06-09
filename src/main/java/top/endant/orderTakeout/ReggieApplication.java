package top.endant.orderTakeout;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
public class OrderTakeoutApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderTakeoutApplication.class,args);
        log.info("服务启动。。。");
    }
}
