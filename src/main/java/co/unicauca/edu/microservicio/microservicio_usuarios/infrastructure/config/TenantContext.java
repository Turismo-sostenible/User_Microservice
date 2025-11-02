package co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.config;

import org.modelmapper.internal.util.Assert;

public class TenantContext {

    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setCurrentTenant(String tenantId) {
        Assert.notNull(tenantId, "El TenantId no puede ser nulo");
        currentTenant.set(tenantId);
    }

    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }
 
}
