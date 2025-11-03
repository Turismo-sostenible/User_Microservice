package co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.config.TenantContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class TenantFilter implements Filter{

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        //String tenantId = req.getHeader("tenant_id");

        String tenantId = null;
        // Caso 1: Obtener el tenantId del JWT en el contexto de seguridad
        // Obtener el tenantId del contexto de seguridad si está disponible
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //verificar que el usuario este autenticado y si es un JWT
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            // Extraer el tenantId de los claims del JWT
            tenantId = jwt.getClaimAsString("tenant_id");
        }

        //Caso 2: Obtener el tenantId del header si no se encuentra en el JWT
        if (tenantId == null) {
            tenantId = req.getHeader("tenant_id");
        }

        if (tenantId != null){
            TenantContext.setCurrentTenant(tenantId);
        }
        
        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
    
}
