package co.edu.unicauca.saberpro.audit.domain.model;

import java.util.List;

/** Página de resultados con total de registros. */
public record rpsPage<T>(List<T> content, int page, int size, long totalElements) { }