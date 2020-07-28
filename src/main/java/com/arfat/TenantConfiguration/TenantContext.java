package com.arfat.TenantConfiguration;

public class TenantContext {
    private static ThreadLocal<String> threadContext = new ThreadLocal<>();

    public static void setCurrentTenant(String tenant) {
        threadContext.set(tenant);
    }

    public static String getCurrentTenant() {
        return threadContext.get();
    }
}
