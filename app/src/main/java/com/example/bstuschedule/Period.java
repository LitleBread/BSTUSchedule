package com.example.bstuschedule;

public class Period {
    private String requestValue;
    private String showValue;

    public Period(String requestValue, String showValue) {
        this.requestValue = requestValue;
        this.showValue = showValue;
    }

    public String getRequestValue() {
        return requestValue;
    }

    public String getShowValue() {
        return showValue;
    }
}
