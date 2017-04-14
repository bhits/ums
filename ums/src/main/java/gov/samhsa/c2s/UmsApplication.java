package gov.samhsa.c2s;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@EnableFeignClients
@EntityScan(basePackageClasses = {UmsApplication.class, Jsr310JpaConverters.class})
@EnableAspectJAutoProxy
public class UmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(UmsApplication.class, args);
	}
}
