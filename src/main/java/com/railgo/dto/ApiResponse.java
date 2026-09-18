package com.railgo.dto;

public class ApiResponse {
    private String status;
    private String message;

    public ApiResponse(boolean isSuccess, String message) {
        this.status = isSuccess ? "success" : "failure";
        this.message = message;
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
}