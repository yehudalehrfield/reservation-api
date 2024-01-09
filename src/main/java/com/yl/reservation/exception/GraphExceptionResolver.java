package com.yl.reservation.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class GraphExceptionResolver extends DataFetcherExceptionResolverAdapter {
    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env){
        GraphqlErrorBuilder<?> gqlErrorBuilder = GraphqlErrorBuilder.newError();
        if (ex instanceof ResGraphException resGraphException){
            gqlErrorBuilder.message(resGraphException.getMessage());
            if (resGraphException.getErrorCode().toString().contains(HttpStatus.NOT_FOUND.toString())) {
                gqlErrorBuilder.errorType(ErrorType.NOT_FOUND);
            } else if (resGraphException.getErrorCode().toString().contains(HttpStatus.BAD_REQUEST.toString())) {
                gqlErrorBuilder.errorType(ErrorType.BAD_REQUEST);
            } else {
                gqlErrorBuilder.errorType(ErrorType.INTERNAL_ERROR);
            }
        }
        if (ex instanceof  ResException resException){
            gqlErrorBuilder.message(resException.getMessage());
            if (resException.getStatus().equals(HttpStatus.NOT_FOUND)) {
                gqlErrorBuilder.errorType(ErrorType.NOT_FOUND);
            } else if (resException.getStatus().equals(HttpStatus.BAD_REQUEST)) {
                gqlErrorBuilder.errorType(ErrorType.BAD_REQUEST);
            } else {
                gqlErrorBuilder.errorType(ErrorType.INTERNAL_ERROR);
            }
        }
        return gqlErrorBuilder.build();
    }
}
