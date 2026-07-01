package com.notifyhub.notifyhub.notification.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.notifyhub.notifyhub.notification.service.NotificationNotFoundException;
import java.lang.reflect.Method;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class ApiExceptionHandlerTest {

    final ApiExceptionHandler handler = new ApiExceptionHandler();

    @SuppressWarnings("unused")
    void placeholder() {}

    @Test
    void notFoundMapsTo404WithMessage() {
        UUID id = UUID.randomUUID();

        ProblemDetail problem = handler.handleNotFound(new NotificationNotFoundException(id));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getDetail()).contains(id.toString());
    }

    @Test
    void validationMapsTo400WithFieldErrors() throws Exception {
        Method method = ApiExceptionHandlerTest.class.getDeclaredMethod("placeholder");
        MethodParameter parameter = new MethodParameter(method, -1);
        BindingResult binding = new BeanPropertyBindingResult(new Object(), "request");
        binding.addError(new FieldError("request", "toEmail", "toEmail is required"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, binding);

        ProblemDetail problem = handler.handleValidation(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getProperties()).containsKey("errors").containsKey("timestamp");
        @SuppressWarnings("unchecked")
        var errors = (java.util.Map<String, String>) problem.getProperties().get("errors");
        assertThat(errors).containsEntry("toEmail", "toEmail is required");
    }

    @Test
    void validationDefaultsNullMessageAndMergesDuplicateFields() throws Exception {
        Method method = ApiExceptionHandlerTest.class.getDeclaredMethod("placeholder");
        MethodParameter parameter = new MethodParameter(method, -1);
        BindingResult binding = new BeanPropertyBindingResult(new Object(), "request");
        binding.addError(new FieldError("request", "subject", null)); // null default message
        binding.addError(new FieldError("request", "subject", "second")); // duplicate key -> merge
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, binding);

        ProblemDetail problem = handler.handleValidation(ex);

        @SuppressWarnings("unchecked")
        var errors = (java.util.Map<String, String>) problem.getProperties().get("errors");
        assertThat(errors).containsEntry("subject", "invalid"); // null -> "invalid", first wins on merge
    }
}
