package com.michael.expense.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michael.expense.constant.SecurityConstant;
import com.michael.expense.payload.response.ErrorResponseMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpResponse;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        ErrorResponseMessage errorResponseMessage = new ErrorResponseMessage(
                FORBIDDEN.value(),
                FORBIDDEN,
                SecurityConstant.FORBIDDEN_MESSAGE
        );

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, errorResponseMessage);
        outputStream.flush();
    }
}
