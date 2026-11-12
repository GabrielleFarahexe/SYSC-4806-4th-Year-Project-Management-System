package _thYearProjectManagamentSystemGroup22;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ApplicationTests {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Test
	void contextLoads() {
	}

	@Test
	void homeRedirectsToAvailability() throws Exception {
		MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(springSecurity()).build()
				.perform(get("/").with(user("student").roles("STUDENT")))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/availability"));
	}
}
