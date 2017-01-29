package org.bremersee.fac.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FacExampleLdapApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacExampleLdapApplicationTests {

    @Test
    public void contextLoads() {
    }

}
