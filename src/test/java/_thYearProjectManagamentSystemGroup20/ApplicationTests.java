package _thYearProjectManagamentSystemGroup20;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
class ApplicationTests {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Test
	void homeRedirectsToStudentDashboard() throws Exception {
		MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build()
				.perform(get("/").with(user("user")))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/student/dashboard"));
	}

	@Test
	void contextLoads() {
	}

}
