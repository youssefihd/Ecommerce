package ecommerce.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.oauth2.jwt.Jwt;

import java.io.IOException;

    public class JwtLoggingFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            JwtAuthenticationToken authentication = (JwtAuthenticationToken) request.getUserPrincipal();
            if (authentication != null) {
                Jwt jwt = (Jwt) authentication.getCredentials();
                System.out.println("JWT Claims: " + jwt.getClaims());
            }
            filterChain.doFilter(request, response);
        }
    }
