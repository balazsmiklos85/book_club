package hu.bmiklos.bc.controller.dto;

import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * @deprecated Use {@link hu.bmiklos.bc.web.dto.SuggestionReference} from the Thymeleaf module instead.
 */
@Deprecated
public class SuggestionReference {
    private final UUID id;
    private final String name;
    
    public SuggestionReference(@Nullable UUID id, @Nullable String name) {
        this.id = id;
        this.name = name;
    }

    public SuggestionReference(@NonNull String name) {
        this(null, name);
    }

    @Nullable
    public UUID getId() {
        return id;
    }

    @Nullable
    public String getName() {
        return name;
    }
}
