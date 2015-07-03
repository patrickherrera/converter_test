package converter_test;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
public class MyPersistantObject {
    public Allocation allocation;
    public BigDecimal value;

    public enum Allocation {
        AVAILABLE("V"),
        ALLOCATED("A");

        private final String code;

        Allocation(String code) {
            this.code = code;
        }

        public static Converter<Allocation, String> writer() {
            return new Converter<Allocation, String>() {
                public String convert(Allocation allocation) {
                    return allocation.getCode();
                }
            };
        }

        public static Converter<String, Allocation> reader() {
            return new Converter<String, Allocation>() {
                public Allocation convert(String source) {
                    return Allocation.getByCode(source);
                }
            };
        }

        public static Allocation getByCode(String code) {
            switch (code) {
                case "V":
                    return AVAILABLE;
                case "A":
                    return ALLOCATED;
            }

            throw new IllegalArgumentException("Unable to get Allocation from: " + code);
        }

        public String getCode() {
            return code;
        }
    }
}
