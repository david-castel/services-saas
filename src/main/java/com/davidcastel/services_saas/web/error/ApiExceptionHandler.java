package com.davidcastel.services_saas.web.error;

import com.davidcastel.services_saas.domain.exception.CustomerDeletionConflictException;
import com.davidcastel.services_saas.domain.exception.DuplicateEmailException;
import com.davidcastel.services_saas.domain.exception.InvalidWorkOrderStateException;
import com.davidcastel.services_saas.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

//    @ExceptionHandler(IllegalArgumentException.class)
//    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
//        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
//        pd.setTitle("Bad request");
//        pd.setDetail(ex.getMessage());
//        pd.setInstance(URI.create(request.getRequestURI()));
//        return pd;
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail("Request validation failed");
        pd.setInstance(URI.create(request.getRequestURI()));

        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        pd.setProperty("errors", errors);

        return pd;
    }

//    @ExceptionHandler(IllegalStateException.class)
//    public ProblemDetail handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
//        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
//        pd.setTitle("Invalid state transition");
//        pd.setDetail(ex.getMessage());
//        pd.setInstance(URI.create(request.getRequestURI()));
//        return pd;
//    }

    @ExceptionHandler(InvalidWorkOrderStateException.class)
    public ProblemDetail handleInvalidState(
            InvalidWorkOrderStateException ex,
            HttpServletRequest request) {

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Work order state conflict");
        pd.setDetail(ex.getMessage());
        pd.setInstance(URI.create(request.getRequestURI()));
        return pd;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Resource not found");
        pd.setDetail(ex.getMessage());
        pd.setInstance(URI.create(request.getRequestURI()));
        return pd;
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ProblemDetail handleDuplicateEmail(
            DuplicateEmailException ex,
            HttpServletRequest request) {

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Duplicate resource");
        pd.setDetail(ex.getMessage());
        pd.setInstance(URI.create(request.getRequestURI()));
        return pd;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        String message = "Duplicate resource";

        Throwable violation = findCause(ex, org.hibernate.exception.ConstraintViolationException.class);

        if (violation instanceof org.hibernate.exception.ConstraintViolationException cve) {
            String constraintName = cve.getConstraintName();

            if ("uk_customers_email".equals(constraintName)) {
                message = "Email already in use";
            }
        }

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflict");
        pd.setDetail(message);
        pd.setInstance(URI.create(request.getRequestURI()));

        return pd;
    }

    private Throwable findCause(Throwable ex, Class<?> type) {
        while (ex != null) {
            if (type.isInstance(ex)) {
                return ex;
            }

            ex = ex.getCause();
        }

        return null;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad request");

        String param = ex.getName(); // "status"
        Object value = ex.getValue(); // "INVALID"
        String expected = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "required type";

        pd.setDetail("Invalid value '" + value + "' for parameter '" + param + "'. Expected " + expected + ".");
        pd.setInstance(URI.create(request.getRequestURI()));

        // opcional: meter info estructurada
        pd.setProperty("parameter", param);
        pd.setProperty("rejectedValue", value);
        pd.setProperty("expectedType", expected);

        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            Object[] constants = ex.getRequiredType().getEnumConstants();
            List<String> allowed = Arrays.stream(constants).map(Object::toString).toList();
            pd.setProperty("allowedValues", allowed);
        }

        return pd;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflict");
        pd.setDetail(ex.getMessage());
        pd.setInstance(URI.create(request.getRequestURI()));
        return pd;
    }

    @ExceptionHandler(CustomerDeletionConflictException.class)
    public ProblemDetail handleCustomerDeletionConflict(CustomerDeletionConflictException ex,
                                                        HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflict");
        pd.setDetail(ex.getMessage());
        pd.setInstance(URI.create(request.getRequestURI()));
        return pd;
    }

}