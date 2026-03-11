package hu.bmiklos.bc.model.entity;

import java.math.BigDecimal;
import java.util.UUID;

public record BookWeight(UUID bookId, BigDecimal weight) {}
