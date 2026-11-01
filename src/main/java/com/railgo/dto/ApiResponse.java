package com.railgo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiResponse {
    private String status;
    private String message;
    @JsonProperty("enum")
    private String enumCode;
    private String action;

    public ApiResponse() {
    }

    public ApiResponse(boolean isSuccess, String message, String enumCode, String action) {
        this.status = isSuccess ? "success" : "error";
        this.message = message;
        this.enumCode = enumCode;
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("enum")
    public String getEnum() {
        return enumCode;
    }

    @JsonProperty("enum")
    public void setEnum(String enumCode) {
        this.enumCode = enumCode;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
