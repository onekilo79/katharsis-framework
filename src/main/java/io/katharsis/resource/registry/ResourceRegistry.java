package io.katharsis.resource.registry;

import io.katharsis.resource.annotations.JsonApiResource;
import io.katharsis.resource.exception.ResourceNotFoundException;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class ResourceRegistry {
    private final Map<Class, RegistryEntry> resources = new HashMap<>();
    private final String serviceUrl;

    public ResourceRegistry(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public <T> void addEntry(Class<T> resource, RegistryEntry<? extends T> registryEntry) {
        resources.put(resource, registryEntry);
    }

    public RegistryEntry getEntry(String searchType) {
        for (Map.Entry<Class, RegistryEntry> entry : resources.entrySet()) {
            String type = getResourceType(entry.getKey());
            if (searchType.equals(type)) {
                return entry.getValue();
            }
        }
        throw new ResourceNotFoundException("Resource of type not found: " + searchType);
    }

    public RegistryEntry getEntry(Class clazz) {
        RegistryEntry registryEntry = resources.get(clazz);
        if (registryEntry != null) {
            return registryEntry;
        }
        throw new ResourceNotFoundException("Resource of class not found: " + clazz.getCanonicalName());
    }

    public String getResourceType(Class clazz) {
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof JsonApiResource) {
                JsonApiResource apiResource = (JsonApiResource) annotation;
                return apiResource.type();
            }
        }
        throw new RuntimeException("Class has no JsonApiResource annotation: " + clazz.getCanonicalName());
    }

    public String getResourceUrl(Class clazz) {
        return serviceUrl + "/" + getResourceType(clazz);
    }
}
