package com.consist.taskboot.exeption;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestInfo {
    @JsonProperty
    private String message;
    @JsonProperty
    private HttpStatus status;
    @JsonProperty
    private String method;
    @JsonProperty
    private String request;

    RequestInfo(String message, HttpStatus status, String method, String request) {
        this.message = message;
        this.status = status;
        this.method = method;
        this.request = request;
    }

    public static RequestInfoBuilder builder() {
        return new RequestInfoBuilder();
    }

    public static class RequestInfoBuilder {
        private String message;
        private HttpStatus status;
        private String method;
        private String request;

        RequestInfoBuilder() {
        }

        public RequestInfoBuilder message(String message) {
            this.message = message;
            return this;
        }

        public RequestInfoBuilder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public RequestInfoBuilder method(String method) {
            this.method = method;
            return this;
        }

        public RequestInfoBuilder request(String request) {
            this.request = request;
            return this;
        }

        public RequestInfo build() {
            return new RequestInfo(message, status, method, request);
        }

        public String toString() {
            return "RequestInfo.RequestInfoBuilder(message=" + this.message + ", status=" + this.status + ", method=" + this.method + ", request=" + this.request + ")";
        }
    }
}
