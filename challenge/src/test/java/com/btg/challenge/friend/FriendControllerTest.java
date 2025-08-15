package com.btg.challenge.friend;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.btg.challenge.shared.config.JwtTokenProvider;
import com.btg.challenge.shared.exception.GlobalExceptionHandler;
import com.btg.challenge.shared.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(FriendController.class)
@Import({FriendController.class, GlobalExceptionHandler.class})
public class FriendControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FriendService friendService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(username = "user")
    public void whenCreateFriendWithValidDataShouldReturnCreatedResponse() throws Exception {
        FriendDto friendDto = new FriendDto();
        friendDto.setName("John Doe");

        when(friendService.save(any(FriendDto.class))).thenReturn(friendDto);

        mockMvc.perform(post("/friends")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(friendDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenGetFriendByIdWithValidIdShouldReturnOkResponse() throws Exception {
        FriendDto friendDto = new FriendDto();
        friendDto.setId(1L);

        when(friendService.findById(1L)).thenReturn(friendDto);

        mockMvc.perform(get("/friends/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenGetFriendByIdWithNonExistentIdShouldReturnNotFound() throws Exception {
        when(friendService.findById(1L)).thenThrow(new ResourceNotFoundException("Friend not found"));

        mockMvc.perform(get("/friends/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenGetAllFriendsShouldReturnOkResponse() throws Exception {
        mockMvc.perform(get("/friends"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenUpdateFriendWithValidDataShouldReturnOkResponse() throws Exception {
        FriendDto friendDto = new FriendDto();
        friendDto.setName("Jane Doe");

        when(friendService.update(eq(1L), any(FriendDto.class))).thenReturn(friendDto);

        mockMvc.perform(put("/friends/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(friendDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenUpdateFriendWithNonExistentIdShouldReturnNotFound() throws Exception {
        FriendDto friendDto = new FriendDto();
        friendDto.setName("Jane Doe");

        doThrow(new ResourceNotFoundException("Friend not found")).when(friendService).update(eq(1L), any(FriendDto.class));

        mockMvc.perform(put("/friends/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(friendDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenDeleteFriendShouldReturnNoContentResponse() throws Exception {
        mockMvc.perform(delete("/friends/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
