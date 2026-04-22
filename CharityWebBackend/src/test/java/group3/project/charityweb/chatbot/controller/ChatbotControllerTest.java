package group3.project.charityweb.chatbot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group3.project.charityweb.advice.GlobalExceptionHandler;
import group3.project.charityweb.chatbot.dto.request.ChatAskRequest;
import group3.project.charityweb.chatbot.dto.response.ChatAskResponse;
import group3.project.charityweb.chatbot.dto.response.ChatMeta;
import group3.project.charityweb.chatbot.dto.response.ChatSource;
import group3.project.charityweb.chatbot.dto.response.ChatSuggestionResponse;
import group3.project.charityweb.chatbot.exception.ChatbotRateLimitException;
import group3.project.charityweb.chatbot.exception.ChatbotValidationException;
import group3.project.charityweb.chatbot.service.ChatbotService;
import group3.project.charityweb.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatbotController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ChatbotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatbotService chatbotService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void ask_shouldReturn200() throws Exception {
        ChatAskRequest request = new ChatAskRequest();
        request.setQuestion("Lam sao de donate?");

        ChatAskResponse response = ChatAskResponse.builder()
                .answer("Ban vao trang du an va tao donation.")
                .conversationId("conv-1")
                .sources(List.of(ChatSource.builder().type("PROJECT").id("p1").title("Du an A").build()))
                .meta(ChatMeta.builder().model("gemini-1.5-flash").latencyMs(120).fallbackUsed(false).build())
                .build();

        when(chatbotService.ask(any(ChatAskRequest.class), anyString(), anyBoolean())).thenReturn(response);

        mockMvc.perform(post("/api/v1/chatbot/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("Ban vao trang du an va tao donation."))
                .andExpect(jsonPath("$.conversationId").value("conv-1"));
    }

    @Test
    void ask_whenValidationError_shouldReturn400() throws Exception {
        ChatAskRequest request = new ChatAskRequest();
        request.setQuestion("");

        when(chatbotService.ask(any(ChatAskRequest.class), anyString(), anyBoolean()))
                .thenThrow(new ChatbotValidationException("question khong duoc de trong."));

        mockMvc.perform(post("/api/v1/chatbot/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("question khong duoc de trong."));
    }

    @Test
    void ask_whenRateLimited_shouldReturn429() throws Exception {
        ChatAskRequest request = new ChatAskRequest();
        request.setQuestion("hello");

        when(chatbotService.ask(any(ChatAskRequest.class), anyString(), anyBoolean()))
                .thenThrow(new ChatbotRateLimitException("Ban gui qua nhieu yeu cau. Vui long thu lai sau."));

        mockMvc.perform(post("/api/v1/chatbot/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429));
    }

    @Test
    void getSuggestions_shouldReturn200() throws Exception {
        ChatSuggestionResponse response = ChatSuggestionResponse.builder()
                .items(List.of("Lam sao de donate?", "Du an nay dang o giai doan nao?"))
                .build();

        when(chatbotService.getSuggestions("p1")).thenReturn(response);

        mockMvc.perform(get("/api/v1/chatbot/suggestions").param("projectId", "p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0]").value("Lam sao de donate?"));
    }
}



