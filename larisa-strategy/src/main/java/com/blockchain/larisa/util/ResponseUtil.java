package com.blockchain.larisa.util;


import com.blockchain.larisa.domain.Response;

public class ResponseUtil {

    public static boolean isSuccess(Response response) {
        return response != null && response.isSuccess();
    }

    public static Response errorResponse() {
        Response response = new Response();
        response.setTs(System.currentTimeMillis());
        response.setStatus("error");
        return response;
    }

    public static Response errorResponse(Exception e) {
        Response response = new Response();
        response.setTs(System.currentTimeMillis());
        response.setStatus(e.getMessage());
        return response;
    }
}
