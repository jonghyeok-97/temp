package airdnb.be.web.member;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import airdnb.be.ControllerTestSupport;
import airdnb.be.web.member.request.EmailRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class MemberControllerTest extends ControllerTestSupport {

    @DisplayName("회원이 존재하면 메시지 바디는 OK_200 이다.")
    @Test
    void existsMemberByEmail() throws Exception {
        // given
        EmailRequest request = new EmailRequest("123@naver.com");
        given(memberService.existsMemberByEmail(request.email()))
                        .willReturn(true);

        // when then
        mockMvc.perform(
                get("/member/exist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @DisplayName("회원이 존재하지 않으면 메시지 바디는 NO_CONTENT_204 이다.")
    @Test
    void notExistsMemberByEmail() throws Exception {
        // given
        EmailRequest request = new EmailRequest("123@naver.com");
        given(memberService.existsMemberByEmail(request.email()))
                .willReturn(false);

        // when then
        mockMvc.perform(
                        get("/member/exist")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.code").value("0204"))
                .andExpect(jsonPath("$.status").value("NO_CONTENT"))
                .andExpect(jsonPath("$.message").value("NO_CONTENT"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}