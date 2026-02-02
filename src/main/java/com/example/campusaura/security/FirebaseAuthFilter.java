package com.example.campusaura.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
      throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      try {
        FirebaseToken decodedToken =
            FirebaseAuth.getInstance().verifyIdToken(token);

        // Set request attributes
        request.setAttribute("uid", decodedToken.getUid());
        request.setAttribute("email", decodedToken.getEmail());

        // Extract roles from custom claims
        List<GrantedAuthority> authorities = new ArrayList<>();
        Map<String, Object> claims = decodedToken.getClaims();
        
        if (claims.containsKey("role")) {
          String role = (String) claims.get("role");
          authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
        }
        
        // Check if user is admin
        if (claims.containsKey("admin") && (Boolean) claims.get("admin")) {
          authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        
        // Check if user is coordinator
        if (claims.containsKey("coordinator") && (Boolean) claims.get("coordinator")) {
          authorities.add(new SimpleGrantedAuthority("ROLE_COORDINATOR"));
        }
        
        // Default role for authenticated users
        if (authorities.isEmpty()) {
          authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // Create authentication token for Spring Security
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                decodedToken.getUid(),
                null,
                authorities
            );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set authentication in Spring Security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

      } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Invalid or expired token\"}");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}
