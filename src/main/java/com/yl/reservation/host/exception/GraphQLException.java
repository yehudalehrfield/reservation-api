package com.yl.reservation.host.exception;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class GraphQLException extends RuntimeException implements GraphQLError {
    private final HttpStatus errorCode;

    public GraphQLException(String description, HttpStatus errorCode) {
        super(description);
        this.errorCode = errorCode;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return Collections.emptyList();
    }

    @Override
    public ErrorClassification getErrorType() {
        return null;
    }
}
