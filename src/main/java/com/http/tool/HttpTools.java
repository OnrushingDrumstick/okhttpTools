package com.http.tool;

import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ID: OnrushingDrumstick
 * Date: 2021/3/21
 * Description:
 **/
public class HttpTools {
    private final Map<String,String> emptyParams = new HashMap<>(0);
    private HttpTools(){}
    private static HttpTools instance;
    public static HttpTools getInstance(){
        synchronized (HttpTools.class){
            if(instance == null){
                instance = new HttpTools();
            }
            return instance;
        }
    }

    public Response call(String url,Method method) throws Exception {
        return call(url,emptyParams,method);
    }

    public void call(String url,Method method,HttpListener listener) throws Exception {
        call(url,emptyParams,method,listener);
    }

    public Response call(String url, Map<String,String> params,Method method) throws Exception {
        return call(url,params,emptyParams,method);
    }

    public void call(String url,Map<String,String> params,Method method ,HttpListener listener) throws Exception {
        call(url,params,emptyParams,method,listener);
    }

    public Response call(String url,Map<String,String> params,Map<String,String> header,Method method) throws Exception {
        return sendRequest(url,params,header,method,null);
    }

    public void call(String url,Map<String,String> params,Map<String,String> header,Method method,HttpListener listener) throws Exception {
        sendRequest(url,params,header,method,listener);
    }

    private Response sendRequest(String url,Map<String,String> params,Map<String,String> header,Method method,HttpListener listener) throws Exception {
        if("".equals(url) || url== null){
            throw new Exception("Request url is null");
        }
        HttpUrl httpUrl;
        if(method == Method.GET){
            httpUrl = createHttpUrl(url,params);
        }else{
            httpUrl = createHttpUrl(url,null);
        }
        RequestBody requestBody = null;
        if(method == Method.POST || method == Method.PUT || method == Method.DELETE){
            FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
            if(params != null && params.size()>0){
                Set<String> keys = params.keySet();
                for (String x : keys){
                    formEncodingBuilder.add(x,params.get(x));
                }
            }
            requestBody = formEncodingBuilder.build();
        }
        if(method == Method.PUT_JSON || method == Method.POST_JSON || method == Method.DELETE_JSON){
            JSONObject jsonObject = new JSONObject();
            if(params != null && params.size()>0){
                Set<String> keys = params.keySet();
                for (String x : keys){
                    jsonObject.put(x,params.get(x));
                }
            }
            requestBody = RequestBody.create(MediaType.parse("application/json"),jsonObject.toJSONString());
        }

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(httpUrl);
        if(header != null && header.size()>0){
            Set<String> keys = header.keySet();
            for (String x :keys){
                requestBuilder.addHeader(x,header.get(x));
            }
        }

        if(method == Method.PUT || method == Method.PUT_JSON){
            requestBuilder.put(requestBody);
        }else if (method == Method.POST || method == Method.POST_JSON){
            requestBuilder.post(requestBody);
        }else if(method == Method.DELETE || method == Method.DELETE_JSON){
            requestBuilder.delete(requestBody);
        }
        return sendRequest(requestBuilder.build(),listener);
    }

    private Response sendRequest(Request request, HttpListener listener) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        if(listener != null){
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    listener.onFailure(request,e);
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    listener.onResponse(response);
                }
            });
        }else{
            return okHttpClient.newCall(request).execute();
        }
        return null;
    }

    private HttpUrl createHttpUrl(String url,Map<String,String> params){
        HttpUrl httpUrl = HttpUrl.parse(url);
        if(params == null || params.size()<=0){
            return httpUrl;
        }else{
            HttpUrl.Builder builder = new HttpUrl.Builder();
            Set<String> keys = params.keySet();
            for (String x : keys){
                builder.addQueryParameter(x,params.get(x));
            }
            return builder.build();
        }
    }
}
