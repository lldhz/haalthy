package com.haalthy.service.controller.Interface;

/**
 * Created by Ken on 2016-01-06.
 */
public class PostResponse {
    private int result;
    private String resultDesp;
    private Object content;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getResultDesp() {
        return resultDesp;
    }

    public void setResultDesp(String resultDesp) {
        this.resultDesp = resultDesp;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
