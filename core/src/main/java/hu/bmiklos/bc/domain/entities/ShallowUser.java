package hu.bmiklos.bc.domain.entities;

import java.util.UUID;

public record ShallowUser(UUID id, String name, Boolean isAdmin, Integer externalId) {}
