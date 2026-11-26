package _thYearProjectManagamentSystemGroup20;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(classes = Application.class)
class ApplicationTests {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Test
	void contextLoads() {
	}

	@Test
	void aboutShowsMainPage() throws Exception {
		MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build()
				.perform(get("/about"))
				.andExpect(status().isOk())
				.andExpect(view().name("index"))
				.andExpect(content().string(containsString("Fourth-Year Project Management System")))
				.andExpect(content().string(containsString("Professor dashboard")))
				.andExpect(content().string(containsString("Coordinator dashboard")));
	}

	@Test
	void homeSendsAnonymousUserToLogin() throws Exception {
		MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build()
				.perform(get("/"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	void homeSendsLoggedInUserToDashboard() throws Exception {
		MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build()
				.perform(get("/").with(user("user").roles("STUDENT")))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/dashboard"));
	}

	@Test
	void loginPageLoads() throws Exception {
		MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build()
				.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Sign in to continue")));
	}

    @Test
    void dashboardRedirectsStudentToStudentDashboard() throws Exception {
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build()
                .perform(get("/dashboard")
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"));
    }

    @Test
    void dashboardRedirectsProfessorToProfessorDashboard() throws Exception {
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build()
                .perform(get("/dashboard")
                        .with(user("professor").roles("PROFESSOR")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professor"));
    }

    @Test
    void dashboardRedirectsCoordinatorToCoordinatorDashboard() throws Exception {
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build()
                .perform(get("/dashboard")
                        .with(user("coordinator").roles("COORDINATOR")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/coordinator/dashboard"));
    }

    @Test
    void dashboardRedirectsAdminToAboutPage() throws Exception {
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build()
                .perform(get("/dashboard")
                        .with(user("admin")
                                .roles("STUDENT", "PROFESSOR", "COORDINATOR")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/about"));
    }

}