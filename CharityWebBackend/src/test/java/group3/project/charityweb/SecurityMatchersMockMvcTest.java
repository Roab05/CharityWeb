package group3.project.charityweb;

import com.fasterxml.jackson.databind.ObjectMapper;
import group3.project.charityweb.chatbot.controller.ChatbotController;
import group3.project.charityweb.chatbot.dto.request.ChatAskRequest;
import group3.project.charityweb.chatbot.dto.response.ChatAskResponse;
import group3.project.charityweb.chatbot.dto.response.ChatMeta;
import group3.project.charityweb.chatbot.dto.response.ChatSuggestionResponse;
import group3.project.charityweb.chatbot.service.ChatbotService;
import group3.project.charityweb.config.SecurityConfig;
import group3.project.charityweb.controller.AdminController;
import group3.project.charityweb.controller.UserController;
import group3.project.charityweb.model.dto.response.SystemStatisticsResponse;
import group3.project.charityweb.model.dto.response.UserProfileResponse;
import group3.project.charityweb.security.CustomAccessDeniedHandler;
import group3.project.charityweb.security.JwtAuthenticationEntryPoint;
import group3.project.charityweb.security.JwtAuthenticationFilter;
import group3.project.charityweb.security.JwtTokenProvider;
import group3.project.charityweb.service.AdminService;
import group3.project.charityweb.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        ChatbotController.class,
        UserController.class,
        AdminController.class
})
@Import({SecurityConfig.class, SecurityMatchersMockMvcTest.TestSecurityBeans.class})
class SecurityMatchersMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatbotService chatbotService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void chatbotAsk_shouldAllowAnonymous() throws Exception {
        ChatAskRequest request = new ChatAskRequest();
        request.setQuestion("hello");

        when(chatbotService.ask(any(ChatAskRequest.class), anyString(), anyBoolean()))
                .thenReturn(ChatAskResponse.builder()
                        .answer("ok")
                        .conversationId("c1")
                        .meta(ChatMeta.builder().model("gemini-1.5-flash").latencyMs(1).fallbackUsed(false).build())
                        .sources(List.of())
                        .build());

        mockMvc.perform(post("/api/v1/chatbot/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void chatbotSuggestions_shouldAllowAnonymous() throws Exception {
        when(chatbotService.getSuggestions(null))
                .thenReturn(ChatSuggestionResponse.builder().items(List.of("q1")).build());

        mockMvc.perform(get("/api/v1/chatbot/suggestions"))
                .andExpect(status().isOk());
    }

    @Test
    void usersMe_shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user1", authorities = "ROLE_INDIVIDUAL")
    void usersMe_shouldAllowAuthenticatedUser() throws Exception {
        when(userService.getMyProfile("user1"))
                .thenReturn(UserProfileResponse.builder().username("user1").roleType("INDIVIDUAL").build());

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", authorities = "ROLE_INDIVIDUAL")
    void adminStatistics_shouldForbidNonAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/statistics"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin1", authorities = "ROLE_ADMIN")
    void adminStatistics_shouldAllowAdmin() throws Exception {
        when(adminService.getStatistics()).thenReturn(SystemStatisticsResponse.builder()
                .totalDonatedAmount(BigDecimal.ZERO)
                .activeProjectsCount(0)
                .pendingOrganizationsCount(0)
                .pendingProjectsCount(0)
                .build());

        mockMvc.perform(get("/api/v1/admin/statistics"))
                .andExpect(status().isOk());
    }

    @TestConfiguration
    static class TestSecurityBeans {
        @Bean
        JwtAuthenticationFilter jwtAuthenticationFilter() {
            return new JwtAuthenticationFilter();
        }

        @Bean
        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
            return new JwtAuthenticationEntryPoint();
        }

        @Bean
        CustomAccessDeniedHandler customAccessDeniedHandler() {
            return new CustomAccessDeniedHandler();
        }
    }
}



