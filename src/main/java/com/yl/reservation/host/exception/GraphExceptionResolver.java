package com.yl.reservation.host.exception;

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
        if (ex instanceof GraphQLException graphQLException){
            if (graphQLException.getErrorCode().toString().contains(HttpStatus.NOT_FOUND.toString())) {
                gqlErrorBuilder
                        .errorType(ErrorType.NOT_FOUND)
                        .message(graphQLException.getMessage());
            } else {
                gqlErrorBuilder
                        .errorType(ErrorType.INTERNAL_ERROR)
                        .message(graphQLException.getMessage());
            }
        }
        return gqlErrorBuilder.build();
    }
}
