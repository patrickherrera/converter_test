package converter_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.convert.CustomConversions;

import java.util.*;

@SpringBootApplication
public class ConvertorTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConvertorTestApplication.class, args);
    }

    @Bean
    public CustomConversions customConversions() {
        return new CustomConversions(Arrays.asList(
                MyPersistantObject.Allocation.reader(),
                MyPersistantObject.Allocation.writer()
        ));
    }
}
