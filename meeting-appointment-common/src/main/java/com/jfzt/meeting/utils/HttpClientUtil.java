package com.jfzt.meeting.utils;


import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * http请求工具类
 * @author zhenxing.lu
 * @date 2024/05/09
 */
@Component
public class HttpClientUtil {

    /**
     * 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
     */
    private final CloseableHttpClient httpClient = HttpClients.custom().setConnectionManagerShared(true).build();

    /**
     * 编码格式。发送编码格式统一用UTF-8
     */
    private static final String ENCODING = "UTF-8";

    /**
     * 设置连接超时时间，单位毫秒。
     */
    private static final int CONNECT_TIMEOUT = 6000;

    /**
     * 请求获取数据的超时时间(即响应时间)，单位毫秒。
     */
    private static final int SOCKET_TIMEOUT = 6000;

    /**
     * GET有参(方式一：直接拼接URL)
     *
     * @param url    请求的url
     * @param params 拼接好的请求参数
     *               如下：
     *               StringBuffer params = new StringBuffer();
     *               // 字符数据最好encoding以下;这样一来，某些特殊字符才能传过去(如:某人的名字就是“&”,不encoding的话,传不过去)
     *               params.append("name=" + URLEncoder.encode("&", "utf-8"));
     *               params.append("&");
     *               params.append("age=24");
     */
    public String doGet(String url, StringBuffer params) {
        // 创建Get请求
        HttpGet httpGet = new HttpGet(url + "?" + params);
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 将上面的配置信息 运用到这个Get请求里
            httpGet.setConfig(getRequestConfig());
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                String result = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + result);
                return result;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * GET有参(方式一：直接拼接URL)
     *
     * @param url       请求的url
     * @param mapParams 请求参数Map
     */
    public String doGet(String url, HashMap<String, String> mapParams) {
        StringBuffer params = new StringBuffer();
        // 字符数据最好encoding以下(URLEncoder.encode("&", "utf-8"));这样一来，某些特殊字符才能传过去(如:某人的名字就是“&”,不encoding的话,传不过去)
        mapParams.forEach((k, v) -> {
            params.append(k).append("=").append(v).append("&");
        });
        // 移除该字符串的最后一个 "&"
        params.substring(0, params.length() - 1);
        // 创建Get请求
        HttpGet httpGet = new HttpGet(url + "?" + params);
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 将上面的配置信息 运用到这个Get请求里
            httpGet.setConfig(getRequestConfig());
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                String result = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + result);
                return result;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * GET---有参 (方式二:将参数放入键值对类中,再放入URI中,从而通过URI得到HttpGet实例)
     *
     * @param scheme      请求的方式: HTTP、Https
     * @param host        请求的主机
     * @param port        请求的端口
     * @param requestPath 请求的路径 eg: "/doGetControllerTwo"
     * @param paramsMap   请求的参数
     */
    public String doGet(String scheme,
                        String host,
                        Integer port,
                        String requestPath,
                        HashMap<String, String> paramsMap) {
        // 参数
        URI uri = null;
        try {
            List<BasicNameValuePair> params = new ArrayList<>();
            paramsMap.forEach((k, v) -> {
                params.add(new BasicNameValuePair(k, v));
            });
            // 设置uri信息,并将参数集合放入uri;
            // 注:这里也支持一个键值对一个键值对地往里面放setParameter(String key, String value)
            uri = new URIBuilder().setScheme(scheme).setHost(host)
                    .setPort(port).setPath(requestPath)
                    .setParameters((NameValuePair) params).build();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        // 创建Get请求
        HttpGet httpGet = new HttpGet(uri);

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 将上面的配置信息 运用到这个Get请求里
            httpGet.setConfig(getRequestConfig());
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                String result = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + result);
                return result;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * POST---无参
     *
     * @param url 请求地址
     */
    public String doPost(String url) {
        // 创建Post请求
        HttpPost httpPost = new HttpPost(url);
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                String result = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + result);
                return result;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * POST---有参测试(普通参数)
     *
     * @param url       请求地址
     * @param paramsMap 请求参数map
     */
    public String doPost(String url, HashMap<String, String> paramsMap) {
        // 参数
        StringBuffer params = new StringBuffer();
        paramsMap.forEach((k, v) -> {
            params.append(k).append("=").append(v).append("&");
        });
        // 创建Post请求
        HttpPost httpPost = new HttpPost(url + "?" + params);
        // 设置ContentType(注:如果只是传普通参数的话,ContentType不一定非要用application/json)
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                String result = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + result);
                return result;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * POST---有参测试(对象参数)
     *
     * @param url    请求地址
     * @param object 对象参数
     */
    public String doPost(String url, Object object) {
        // 创建Post请求
        HttpPost httpPost = new HttpPost(url);
        // 我这里利用阿里的fastJson，将Object转换为json字符串;
        String jsonString = JSON.toJSONString(object);
        StringEntity entity = new StringEntity(jsonString, ENCODING);
        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                String result = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + result);
                return result;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * requestConfig
     * 请求超时配置
     */
    public RequestConfig getRequestConfig() {
        // 配置信息
        RequestConfig requestConfig = RequestConfig.custom()
                // 设置连接超时时间(单位毫秒)
                .setConnectTimeout(CONNECT_TIMEOUT)
                // 设置请求超时时间(单位毫秒)
                .setConnectionRequestTimeout(SOCKET_TIMEOUT)
                // socket读写超时时间(单位毫秒)
                .setSocketTimeout(SOCKET_TIMEOUT)
                // 设置是否允许重定向(默认为true)
                .setRedirectsEnabled(true).build();
        return requestConfig;
    }

    public static void main(String[] args) {
        HttpClientUtil httpClientUtil = new HttpClientUtil();
//        StringBuffer stringBuffer = new StringBuffer();
//        String code = "beoJD7kI1FBHN4bK0eWrinRy-ib8ZPGIRbQRyLD0h-k";
//        String suite_access_token = "";
//        String url = "https://qyapi.weixin.qq.com/cgi-bin/service/auth/getuserinfo3rd?suite_access_token=" + suite_access_token +
//                "&code=" + code;
//
//        //获取三方access——token
//        String url2 = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
//        HashMap<String, String> map = new HashMap<>();
//        map.put("corpid", "wwc99ae5b81a00bddb");
//        map.put("corpsecret", "3Jv0cyJHk62A2eyfz7LlFrBZyTaO3Tir0_gilp6BNRU");
//        String response = httpClientUtil.doGet(url2, map);
//        org.json.JSONObject jsonObject = new JSONObject(response);
//        String access_token = jsonObject.optString("access_token", null);
//        System.out.println(access_token);
        //获取用户身份
//        String url3 = "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserdetail?access_token=" + "5fVNrV6WTqPCysgdJsHMvWnI40ca1cy7gE3x9TeAl8OaJV1e1cWeu0OsfdrxdthMvk4KRwOqILrDjtfk3VOCnhlWFooKATUhb87lm-Ccf3Qqz5hCBoFQEZvahosAdwZ_CW1EEVKpSqBDm_LjsdOSwgxWcgvsRD-vhUsH1Bdwf0UQu3yLNXRAbQOSkH884YYl8-Lq4idsgG2h7nZvaBgumw";
//        UserTicketVo testLoginEntity = new UserTicketVo("gwn4gh2a5sj3d6slZwJTEX8TJ3_-VkB6zWFDEGguGHSGDdUzkxlbGMFvi-fwf83eaLhAj8R2n2hOetr2zPdH-yXqA1-Ep28W1QUkff75aYg");
//        String s = httpClientUtil.doPost(url3, testLoginEntity);
//        System.out.println(s);
        //获取用户全部信息
//        String accessToken = "5fVNrV6WTqPCysgdJsHMvWnI40ca1cy7gE3x9TeAl8OaJV1e1cWeu0OsfdrxdthMvk4KRwOqILrDjtfk3VOCnhlWFooKATUhb87lm-Ccf3Qqz5hCBoFQEZvahosAdwZ_CW1EEVKpSqBDm_LjsdOSwgxWcgvsRD-vhUsH1Bdwf0UQu3yLNXRAbQOSkH884YYl8-Lq4idsgG2h7nZvaBgumw";
//        String userId = "ab53f229e9e1721d62134bfcd5946817";
//        HashMap<String, String> map = new HashMap<>();
//        map.put("access_token", accessToken);
//        map.put("userid", userId);
//        String url4 = "https://qyapi.weixin.qq.com/cgi-bin/user/get";
//        String s = httpClientUtil.doGet(url4, map);
//        System.out.println(s);
//        String s = httpClientUtil.doGet("https://open.weixin.qq.com/connect/oauth2/authorize?appid=ww8573547260205f74&redirect_uri=http%3A%2F%2Ftest-web.jifuinfo.com&response_type=CODE&scope=snsapi_privateinfo&state=STATE&agentid=1000026", stringBuffer);
//        System.out.println(s);
    }


}
