package com.gestiontests.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.Map;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AdminJwtAuthFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        if (method != null && method.equalsIgnoreCase("OPTIONS")) {
            return;
        }

        String path = requestContext.getUriInfo().getPath();
        if (path == null) {
            return;
        }

        if (!path.startsWith("admin")) {
            return;
        }

        if (path.equals("admin/login")) {
            return;
        }

        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
            abortUnauthorized(requestContext, "Token manquant");
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        if (token.isEmpty()) {
            abortUnauthorized(requestContext, "Token manquant");
            return;
        }

        try {
            Claims claims = JwtUtil.validateAndGetClaims(token);
            Object role = claims.get("role");
            if (role == null || !"admin".equals(String.valueOf(role))) {
                abortUnauthorized(requestContext, "Rôle invalide");
            }
        } catch (JwtException e) {
            abortUnauthorized(requestContext, "Token invalide");
        }
    }

    private void abortUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
            Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", message))
                .build()
        );
    }
}
