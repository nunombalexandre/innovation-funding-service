package com.worth.ifs.commons.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.application.service.FutureAdapterWithExceptionHandling;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.util.Either;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static com.worth.ifs.commons.rest.RestResult.fromException;
import static com.worth.ifs.commons.rest.RestResult.fromResponse;
import static com.worth.ifs.commons.security.UidAuthenticationService.AUTH_TOKEN;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class RestTemplateAdaptor {
    private final static Log LOG = LogFactory.getLog(RestTemplateAdaptor.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    private RestTemplate getRestTemplate() {
        return restTemplate;
    }

    private AsyncRestTemplate getAsyncRestTemplate() {
        return asyncRestTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setAsyncRestTemplate(AsyncRestTemplate asyncRestTemplate) {
        this.asyncRestTemplate = asyncRestTemplate;
    }

    // Synchronous public calls
    @RestCacheResult
    public <T> RestResult<T> getWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResult(path, GET, returnType);
    }

    @RestCacheResult
    public <T> RestResult<T> getWithRestResult(String path, Class<T> returnType) {
        return exchangeWithRestResult(path, GET, returnType);
    }

    @RestCacheInvalidateResult
    public <T> RestResult<T> postWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResult(path, POST, returnType, OK, CREATED);
    }

    @RestCacheInvalidateResult
    public <R> RestResult<R> postWithRestResult(String path, Object objectToSend, ParameterizedTypeReference<R> returnType) {
        return exchangeObjectWithRestResult(path, POST, objectToSend, returnType, OK, CREATED);
    }

    @RestCacheInvalidateResult
    public <T> RestResult<T> postWithRestResult(String path, Class<T> returnType) {
        return exchangeWithRestResult(path, POST, returnType, OK, CREATED);
    }

    @RestCacheInvalidateResult
    public <R> RestResult<R> postWithRestResult(String path, Object objectToSend, Class<R> returnType) {
        return exchangeObjectWithRestResult(path, POST, objectToSend, returnType, OK, CREATED);
    }

    @RestCacheInvalidateResult
    public <R> RestResult<R> postWithRestResult(String path, Object objectToSend, Class<R> returnType, HttpStatus expectedStatusCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeObjectWithRestResult(path, POST, objectToSend, returnType, expectedStatusCode, otherExpectedStatusCodes);
    }

    @RestCacheInvalidateResult
    public <R> RestResult<R> postWithRestResult(String path, Object objectToSend, ParameterizedTypeReference<R> returnType, HttpStatus expectedStatusCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeObjectWithRestResult(path, POST, objectToSend, returnType, expectedStatusCode, otherExpectedStatusCodes);
    }

    @RestCacheInvalidateResult
    public <R> RestResult<R> putWithRestResult(String path, Object objectToSend, ParameterizedTypeReference<R> returnType) {
        return exchangeObjectWithRestResult(path, PUT, objectToSend, returnType, OK);
    }

    @RestCacheInvalidateResult
    public <T> RestResult<T> putWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResult(path, PUT, returnType);
    }

    @RestCacheInvalidateResult
    public <T> RestResult<T> putWithRestResult(String path, Class<T> returnType) {
        return exchangeWithRestResult(path, PUT, returnType);
    }

    @RestCacheInvalidateResult
    public <R> RestResult<R> putWithRestResult(String path, Object objectToSend, Class<R> returnType) {
        return exchangeObjectWithRestResult(path, PUT, objectToSend, returnType, OK);
    }

    @RestCacheInvalidateResult
    public <T> RestResult<T> deleteWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResult(path, DELETE, returnType, OK, NO_CONTENT);
    }

    @RestCacheInvalidateResult
    public <T> RestResult<T> deleteWithRestResult(String path, Class<T> returnType) {
        return exchangeWithRestResult(path, DELETE, returnType, OK, NO_CONTENT);
    }

    @RestCacheResult
    public <T> ResponseEntity<T> restGetEntity(String path, Class<T> c) {
        return getRestTemplate().exchange(path, GET, jsonEntity(null), c);
    }

    @RestCacheResult
    public <T> ResponseEntity<T> restGetEntity(String path, Class<T> c, HttpHeaders headers) {
        return getRestTemplate().exchange(path, GET, new HttpEntity<Object>(null, headers), c);
    }

    @RestCacheResult
    public <T> ResponseEntity<T> restGet(String path, ParameterizedTypeReference<T> returnType) {
        return getRestTemplate().exchange(path, GET, jsonEntity(null), returnType);
    }

    @RestCacheInvalidateResult
    public <T> ResponseEntity<T> restPostWithEntity(String path, Object postEntity, Class<T> responseType) {
        RestTemplate restTemplate = getRestTemplate();
        HttpHeaders headers = getHeaders();

        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            headers.set(AUTH_TOKEN, SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        }

        HttpEntity<Object> entity = new HttpEntity<>(postEntity, headers);
        return restTemplate.postForEntity(path, entity, responseType);
    }

    @RestCacheInvalidateResult
    public <T, R> Either<ResponseEntity<R>, ResponseEntity<T>> restPostWithEntity(String path, Object postEntity, Class<T> responseType, Class<R> failureType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        try {
            ResponseEntity<String> asString = restPostWithEntity(path, postEntity, String.class);
            return handleSuccessOrFailureJsonResponse(asString, responseType, failureType, expectedSuccessCode, otherExpectedStatusCodes);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return handleSuccessOrFailureJsonResponse(new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode()), responseType, failureType, expectedSuccessCode, otherExpectedStatusCodes);
        }
    }

    private <T> Either<Void, T> fromJson(String json, Class<T> responseType) {

        if (Void.class.isAssignableFrom(responseType) && isBlank(json)) {
            return right(null);
        }

        try {
            return right(new ObjectMapper().readValue(json, responseType));
        } catch (IOException e) {
            return left();
        }
    }

    @RestCacheInvalidateResult
    public <T> ResponseEntity<T> restPutWithEntity(String path, Class<T> c) {
        return getRestTemplate().exchange(path, PUT, jsonEntity(null), c);
    }

    @RestCacheInvalidateResult
    public <T> ResponseEntity<T> restPutWithEntity(String path, Object putEntity, Class<T> c) {
        return getRestTemplate().exchange(path, PUT, jsonEntity(putEntity), c);
    }

    @RestCacheInvalidateResult
    public <T, R> Either<ResponseEntity<R>, ResponseEntity<T>> restPutWithEntity(String path, Object putEntity, Class<T> responseType, Class<R> failureType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        try {
            ResponseEntity<String> asString = restPutWithEntity(path, putEntity, String.class);
            return handleSuccessOrFailureJsonResponse(asString, responseType, failureType, expectedSuccessCode, otherExpectedStatusCodes);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return handleSuccessOrFailureJsonResponse(new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode()), responseType, failureType, expectedSuccessCode, otherExpectedStatusCodes);
        }
    }

    private <T, R> Either<ResponseEntity<R>, ResponseEntity<T>> handleSuccessOrFailureJsonResponse(ResponseEntity<String> asString, Class<T> responseType, Class<R> failureType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        List<HttpStatus> allExpectedSuccessStatusCodes = combineLists(asList(otherExpectedStatusCodes), expectedSuccessCode);

        if (allExpectedSuccessStatusCodes.contains(asString.getStatusCode())) {
            return fromJson(asString.getBody(), responseType).mapLeftOrRight(
                    failure -> left(new ResponseEntity<>((R) null, INTERNAL_SERVER_ERROR)),
                    success -> right(new ResponseEntity<>(success, asString.getStatusCode()))
            );
        } else {
            return fromJson(asString.getBody(), failureType).mapLeftOrRight(
                    failure -> left(new ResponseEntity<>((R) null, INTERNAL_SERVER_ERROR)),
                    success -> left(new ResponseEntity<>(success, asString.getStatusCode()))
            );
        }
    }

    @RestCacheInvalidateResult
    public void restPut(String path, Object entity) {
        getRestTemplate().exchange(path, PUT, jsonEntity(entity), Void.class);
    }

    @RestCacheInvalidateResult
    public <T> ResponseEntity<T> restPut(String path, Object entity, Class<T> c) {
        return getRestTemplate().exchange(path, PUT, jsonEntity(entity), c);
    }

    @RestCacheInvalidateResult
    public void restDelete(String path) {
        getRestTemplate().exchange(path, DELETE, jsonEntity(null), Void.class);
    }

    // Asynchronous public calls
    @RestCacheResult
    public <T> Future<RestResult<T>> getWithRestResultAsyc(String path, Class<T> returnType) {
        return exchangeWithRestResultAsync(path, GET, returnType);
    }

    @RestCacheResult
    public <T> ListenableFuture<ResponseEntity<T>> restGetAsync(String path, Class<T> clazz) {
        return withEmptyCallback(getAsyncRestTemplate().exchange(path, HttpMethod.GET, jsonEntity(""), clazz));
    }

    // Synchronous private calls
    private <T> RestResult<T> exchangeWithRestResult(String path, HttpMethod method, Class<T> returnType) {
        return exchangeWithRestResult(path, method, returnType, OK);
    }

    private <T> RestResult<T> exchangeWithRestResult(String path, HttpMethod method, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResult(path, method, returnType, OK);
    }

    private <T> RestResult<T> exchangeWithRestResult(String path, HttpMethod method, ParameterizedTypeReference<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResult(() -> getRestTemplate().exchange(path, method, jsonEntity(null), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> RestResult<T> exchangeObjectWithRestResult(String path, HttpMethod method, Object objectToSend, ParameterizedTypeReference<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResult(() -> getRestTemplate().exchange(path, method, jsonEntity(objectToSend), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> RestResult<T> exchangeWithRestResult(String path, HttpMethod method, Class<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResult(() -> getRestTemplate().exchange(path, method, jsonEntity(null), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    public <T> RestResult<T> exchangeObjectWithRestResult(String path, HttpMethod method, Object objectToSend, Class<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResult(() -> getRestTemplate().exchange(path, method, jsonEntity(objectToSend), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> RestResult<T> exchangeWithRestResult(final Supplier<ResponseEntity<T>> exchangeFn, final HttpStatus expectedSuccessCode, final HttpStatus... otherExpectedStatusCodes) {
        try {
            return fromResponse(exchangeFn.get(), expectedSuccessCode, otherExpectedStatusCodes);
        } catch (HttpStatusCodeException e) {
            return fromException(e);
        }
    }

    // Asynchronous private calls
    private <T> Future<RestResult<T>> exchangeWithRestResultAsync(String path, HttpMethod method, Class<T> returnType) {
        return exchangeWithRestResultAsync(path, method, returnType, OK);
    }

    public <T> Future<RestResult<T>> exchangeWithRestResultAsync(String path, HttpMethod method, Class<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResultAsync(() -> getAsyncRestTemplate().exchange(path, method, jsonEntity(null), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> Future<RestResult<T>> exchangeWithRestResultAsync(Supplier<ListenableFuture<ResponseEntity<T>>> exchangeFn, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        Future<ResponseEntity<T>> raw = withEmptyCallback(exchangeFn.get());
        return new FutureAdapterWithExceptionHandling<>(raw,
                response -> fromResponse(response, expectedSuccessCode, otherExpectedStatusCodes),
                e -> e instanceof HttpStatusCodeException ? right(fromException((HttpStatusCodeException) e)) : left());
    }

    private static final <T> ListenableFuture<ResponseEntity<T>> withEmptyCallback(ListenableFuture<ResponseEntity<T>> toAddTo) {
        // If we do not add a callback then the behaviour of calling get on the future becomes serial instead of parallel.
        // I.e. the future will not start execution until get is called. However if an empty callback is added the future
        // will start execution immediately (or as soon as a thread is available) and we can then call get.
        toAddTo.addCallback(new ListenableFutureCallback<ResponseEntity<T>>() {
            @Override
            public void onFailure(final Throwable ex) {
                LOG.error("Failure in the empty callback: " + ex);
            }

            @Override
            public void onSuccess(final ResponseEntity<T> result) {
                // Do nothing
            }
        });
        return toAddTo;
    }

    public static HttpHeaders getJSONHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        headers.setAccept(singletonList(APPLICATION_JSON));
        return headers;
    }

    public static HttpHeaders getHeaders() {
        return getJSONHeaders();
    }

    protected <T> HttpEntity<T> jsonEntity(T entity) {
        HttpHeaders headers = getHeaders();
        if (SecurityContextHolder.getContext() != null &&
                SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getCredentials() != null) {
            headers.set(AUTH_TOKEN, SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        }
        return new HttpEntity<>(entity, headers);
    }


}