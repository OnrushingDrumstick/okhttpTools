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
 * Description:封装okhttp 优化常见网络请求接口
 **/
public class HttpClient {
    private static HttpClient client = null;
    private HttpClient(){}
    public static HttpClient getClient(){
        synchronized (HttpClient.class){
            if(client == null){
                client = new HttpClient();
            }
            return client;
        }
    }
    private final Map<String,String> emptyParams = new HashMap<>(0);
    public Response get(String url){
        return get(url,emptyParams);
    }

    public void get(String url,HttpListener listener){
        get(url,emptyParams,listener);
    }

    public Response get(String url,Map<String,String> params){
        return get(url,params,emptyParams);
    }

    public void get(String url,Map<String,String> params,HttpListener listener){
        get(url,params,emptyParams,listener);
    }

    public Response get(String url,Map<String,String> params,Map<String,String> header){
        return sendGetRequest(url,params,header,null);
    }

    public void get(String url,Map<String,String> params,Map<String,String> header,HttpListener listener){
        sendGetRequest(url,params,header,listener);
    }

    public Response post(String url){
        return post(url,emptyParams);
    }

    public void post(String url,HttpListener listener){
        post(url,emptyParams,listener);
    }

    public Response post(String url,Map<String,String> params){
        return post(url,params,emptyParams);
    }

    public void post(String url,Map<String,String> params,HttpListener listener){
        post(url,params,emptyParams,listener);
    }

    public Response post(String url,Map<String,String> params,Map<String,String> header){
        return sendPostRequest(url,params,header,null);
    }

    public void post(String url,Map<String,String> params,Map<String,String> header,HttpListener listener){
        sendPostRequest(url,params,header,listener);
    }

    public Response postJson(String url){
        return postJson(url,emptyParams);
    }

    public void postJson(String url,HttpListener listener){
        postJson(url,emptyParams,listener);
    }

    public Response postJson(String url,Map<String,String> params){
        return postJson(url,params,emptyParams);
    }

    public Response postJson(String url,String jsonString){
        return postJson(url,jsonString,emptyParams);
    }

    public void postJson(String url,Map<String,String> params,HttpListener listener){
        postJson(url,params,emptyParams,listener);
    }

    public void postJson(String url,String jsonString,HttpListener listener){
        postJson(url,jsonString,emptyParams,listener);
    }

    public Response postJson(String url,Map<String,String> params,Map<String,String> header){

        return postJson(url,mapToJson(params),header);
    }

    public Response postJson(String url,String jsonString,Map<String,String> header){
        return sendPostJsonRequest(url,jsonString,header,null);
    }

    public void postJson(String url,Map<String,String> params,Map<String,String> header,HttpListener listener){
        postJson(url,mapToJson(params),header,listener);
    }

    public void postJson(String url,String jsonString,Map<String,String> header,HttpListener listener){
        sendPostJsonRequest(url,jsonString,header,listener);
    }

    private Response sendPostJsonRequest(String url,String jsonString,Map<String,String> header,HttpListener listener){
        if(url == null || url.isEmpty()){
            throw new NullPointerException("URL 不能为空");
        }
        HttpUrl httpUrl = HttpUrl.parse(url);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),jsonString);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(httpUrl);
        requestBuilder.post(requestBody);
        return sendRequest(addHeader(header,requestBuilder),listener);
    }

    private Response sendPostRequest(String url,Map<String,String> params,Map<String,String> header,HttpListener listener){
        if(url == null || url.isEmpty()){
            throw new NullPointerException("URL 不能为空");
        }
        HttpUrl httpUrl = HttpUrl.parse(url);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(httpUrl);
        if(params != null && params.size()>0){
            FormEncodingBuilder builder = new FormEncodingBuilder();
            Set<String> keys = params.keySet();
            for (String x :keys){
                builder.add(x,params.get(x));
            }
            requestBuilder.post(builder.build());
        }
        return sendRequest(addHeader(header,requestBuilder),listener);
    }

    private Response sendGetRequest(String url,Map<String,String> params,Map<String,String> header,HttpListener listener){
        if(url == null || url.isEmpty()){
            throw new NullPointerException("URL is NULL");
        }

        HttpUrl httpUrl = HttpUrl.parse(url);
        HttpUrl.Builder httpBuilder = httpUrl.newBuilder();
        httpBuilder.scheme(httpUrl.scheme());
        httpBuilder.port(httpUrl.port());
        httpBuilder.host(httpUrl.host());
        if(params !=null && params.size()>0){
            Set<String> keys = params.keySet();
            for (String key:keys){
                httpBuilder.addQueryParameter(key,params.get(key));
            }
        }

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(httpBuilder.build());
        return sendRequest(addHeader(header,requestBuilder),listener);

    }

    private String mapToJson(Map<String,String> params){
        JSONObject jsonObject = new JSONObject();
        if(params != null && params.size()>0){
            Set<String> keys = params.keySet();
            for (String key:keys){
                jsonObject.put(key,params.get(key));
            }
        }
        return jsonObject.toJSONString();
    }

    private Request addHeader(Map<String,String> header,Request.Builder builder){
        if(header != null && header.size()>0){
            Set<String> keys = header.keySet();
            for (String key:keys){
                builder.addHeader(key,header.get(key));
            }
        }
        return builder.build();
    }

    private Response sendRequest(Request request,HttpListener listener){
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
            try {
                return okHttpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
