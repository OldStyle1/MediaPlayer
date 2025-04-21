package com.example.Client;

import com.example.Client.controller.AudioFileController;
import com.example.Client.service.AudioFileService;
import com.example.Client.service.UserService;
import com.example.Client.model.AudioFile;
import com.example.Client.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(AudioFileController.class)
public class AudioFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AudioFileService audioFileService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetAllAudioFiles() throws Exception {
        List<AudioFile> audioFiles = Arrays.asList(
            new AudioFile(1L, "test1.mp3", "audio/mp3", 1000L),
            new AudioFile(2L, "test2.mp3", "audio/mp3", 2000L)
        );

        when(audioFileService.findAllAudioFiles()).thenReturn(audioFiles);

        mockMvc.perform(get("/api/audio-files")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].fileName").value("test1.mp3"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUploadAudioFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.mp3",
            "audio/mp3",
            "test content".getBytes()
        );

        AudioFile audioFile = new AudioFile(1L, "test.mp3", "audio/mp3", 1000L);
        User mockUser = new User();
        
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(audioFileService.uploadAudioFile(any(MultipartFile.class), any(User.class)))
            .thenReturn(audioFile);

        mockMvc.perform(multipart("/api/audio-files/upload")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test.mp3"));
    }
}