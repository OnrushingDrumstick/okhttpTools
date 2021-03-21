package com.http.tool;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * ID: OnrushingDrumstick
 * Date: 2021/3/21
 * Description:
 **/
public interface HttpListener {
    void onFailure(Request request, IOException exception);
    void onResponse(Response response);
}
