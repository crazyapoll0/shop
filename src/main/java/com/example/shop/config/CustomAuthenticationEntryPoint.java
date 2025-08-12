package com.example.shop.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.io.PrintWriter;

/*AuthenticationEntryPoint?
* 인증되지 않은 유저가 인증이 필요한 자원을 요청했을 때 처리할 응답을 커스텀 ==> 작성 ==> 필터 체인에 등록 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //응답 객체에 상태코드 401 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter writer = response.getWriter();
        writer.println("<!Document html>");
        writer.println("<html>");
        writer.println("<body>");
        writer.println("<h1>로그인이 필요한 페이지 입니다.</h1>");
        writer.println("</body>");
        writer.println("</html>");
    }
    //인증이 되지 않은 유저가 인증이 필요한 자원을 요청한 경우 기존 : 302반환 후 Location 로그인 페이지 응답
    // => 브라우저가 자동으로 로그인 페이지로 이동
    //TO-BE : 401(UnAuthorized) 상태 코드에 적당한 메시지 반환

}
