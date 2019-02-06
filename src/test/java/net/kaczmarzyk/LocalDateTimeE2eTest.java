/**
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kaczmarzyk;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.kaczmarzyk.spring.data.jpa.Customer;
import net.kaczmarzyk.spring.data.jpa.CustomerRepository;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThan;
import net.kaczmarzyk.spring.data.jpa.domain.LessThan;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

public class LocalDateTimeE2eTest extends E2eTestBase {
    @Controller
    public static class LocalDateSpecsController {

        @Autowired
        CustomerRepository customerRepo;

        @RequestMapping(value = "/customers", params = "lastOrderTimeBefore")
        @ResponseBody
        public Object findCustomersRegisteredBefore(
                @Spec(path="lastOrderTime", params="lastOrderTimeBefore", config="yyyy-MM-dd\'T\'HH:mm:ss", spec= LessThan.class) Specification<Customer> spec) {

            return customerRepo.findAll(spec);
        }

        @RequestMapping(value = "/customers", params = "lastOrderTimeAfter")
        @ResponseBody
        public Object findCustomersRegisteredAfter(
                @Spec(path="lastOrderTime", params="lastOrderTimeAfter", spec= GreaterThan.class) Specification<Customer> spec) {
            return customerRepo.findAll(spec);
        }

    }

    /*The test will fail, there is a bug in hibernate-core v5.0.12 regarding the conversion of LocalDateTime to TIMESTAMP for H2 database
    * See https://stackoverflow.com/questions/44676732/how-to-map-java-time-localdatetime-to-timestamp-in-h2-database-with-hibernate*/
    @Test
    public void findsByDateTimeBeforeWithCustomDateFormat() throws Exception {
        mockMvc.perform(get("/customers")
                                .param("lastOrderTimeBefore", "2016-09-01T00:00:00")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


    }

    @Test
    public void findsByDateTimeAfter() throws Exception {
        mockMvc.perform(get("/customers")
                                .param("lastOrderTimeAfter", "2017-08-22T10:00:00")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
