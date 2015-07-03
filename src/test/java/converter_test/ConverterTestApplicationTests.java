package converter_test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

import static converter_test.MyPersistantObject.Allocation.ALLOCATED;
import static converter_test.MyPersistantObject.Allocation.AVAILABLE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ConvertorTestApplication.class)
public class ConverterTestApplicationTests {

    @Autowired
    private MongoOperations mongoTemplate;
    @Before
    public void setUp() throws Exception {
        mongoTemplate.dropCollection(MyPersistantObject.class);
    }

    @Test
    public void testConversion() {
        Update update;
        Query query;
        MyPersistantObject returned;
        MyPersistantObject myPersistantObject = new MyPersistantObject();
        myPersistantObject.allocation = AVAILABLE;
        myPersistantObject.value = new BigDecimal(1234567);

        mongoTemplate.save(myPersistantObject);

        // Check it was saved correctly - first with invalid allocation to confirm conversion in query
        query = query(where("allocation").is(ALLOCATED));
        assertThat(mongoTemplate.findOne(query, MyPersistantObject.class), is(nullValue()));

        // Check it was saved correctly - now with valid allocation to confirm conversion in query
        query = query(where("allocation").is(AVAILABLE));
        returned = mongoTemplate.findOne(query, MyPersistantObject.class);
        assertThat(returned.allocation, is(AVAILABLE));
        assertThat(returned.value.longValue(), is(1234567L));

        try {
            // Update allocation from constant - will fail
            update = update("allocation", ALLOCATED);
            mongoTemplate.updateMulti(query, update, MyPersistantObject.class);
        } catch (Exception e) {
            System.err.println("failed to convert allocation");
        }

        // Update allocation from string value - succeeds
        update = update("allocation", ALLOCATED.getCode());
        mongoTemplate.updateMulti(query, update, MyPersistantObject.class);
        // Check allocation update
        query = query(where("allocation").is(ALLOCATED));
        returned = mongoTemplate.findOne(query, MyPersistantObject.class);
        assertThat(returned.allocation, is(ALLOCATED));

        // Update value only - will fail: Caused by: java.lang.IllegalArgumentException: Unable to get MyPersistantObject.Allocation from: 54321
        // Tries to use MyPersistantObject.Allocation converter to String
        update = update("value", new BigDecimal(54321));
        mongoTemplate.updateMulti(query, update, MyPersistantObject.class);
        // Check value update
        returned = mongoTemplate.findAll(MyPersistantObject.class).get(0);
        assertThat(returned.value.longValue(), is(54321L));



    }

}
