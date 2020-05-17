package passwords.encryption;

import java.util.Collections;
import java.util.Map;

public final class EncryptionFactory {

    private final Map<Integer, EncryptionService> registry;

    public EncryptionFactory(Map<Integer, EncryptionService> registry) {
        this.registry = Collections.unmodifiableMap(registry);
    }

    public EncryptionService getService(Integer version) {
        return registry.get(version);
    }

}
