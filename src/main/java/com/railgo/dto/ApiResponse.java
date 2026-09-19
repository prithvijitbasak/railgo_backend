package com.railgo.dto;

public class ApiResponse {
    private String status;
    private String message;
    private String enumValue;
    private String action;


    public ApiResponse(boolean isSuccess, String message, String enumValue, String action) {
        this.status = isSuccess ? "success" : "error";
        this.message = message;
        this.enumValue = enumValue;
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

    public String getEnumValue() {
        return enumValue;
    }

    public void setEnumValue() {
        this.enumValue = enumValue;
    }
    public String getAction() { return action; }

    public void setAction() {
        this.action = action;
    }

  
}