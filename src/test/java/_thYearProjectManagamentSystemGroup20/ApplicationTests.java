package _thYearProjectManagamentSystemGroup20;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
class ApplicationTests {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Test
	void homeShowsMainPage() throws Exception {
		MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build()
			.perform(get("/").with(user("user")))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andExpect(content().string(containsString("Fourth-Year Project Management System")))
			.andExpect(content().string(containsString("Professor dashboard")))
			.andExpect(content().string(containsString("Coordinator dashboard")));
	}

	@Test
	void contextLoads() {
	}

}
