/*
 * @(#) TextCheckAPIDemo.java 2016年2月3日
 *
 * Copyright 2010 NetEase.com, Inc. All rights reserved.
 */
package com.netease.is.antispam.demo.text.v5;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.http.Consts;
import org.apache.http.client.HttpClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.netease.is.antispam.demo.utils.HttpClient4Utils;
import com.netease.is.antispam.demo.utils.SignatureUtils;
import com.netease.is.antispam.demo.utils.Utils;

/**
 * 调用易盾反垃圾云服务文本V5异步检测接口API示例，该示例依赖以下jar包： 1. httpclient，用于发送http请求 2. commons-codec，使用md5算法生成签名信息，详细见SignatureUtils.java
 * 3. gson，用于做json解析
 *
 * @author yidun
 * @version 2021年08月31日
 */
public class TextAsyncCheckAPIDemoV5 {
    /**
     * 产品密钥ID，产品标识
     */
    private final static String SECRETID = "your_secret_id";
    /**
     * 产品私有密钥，服务端生成签名信息使用，请严格保管，避免泄露
     */
    private final static String SECRETKEY = "your_secret_key";
    /**
     * 业务ID，易盾根据产品业务特点分配
     */
    private final static String BUSINESSID = "your_business_id";
    /**
     * 易盾反垃圾云服务文本在线检测接口地址
     */
    private final static String API_URL = "http://as.dun.163.com/v5/text/async-check";
    /**
     * 实例化HttpClient，发送http请求使用，可根据需要自行调参
     */
    private static HttpClient httpClient = HttpClient4Utils.createHttpClient(100, 20, 2000, 2000, 2000);

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        // 1.设置公共参数
        params.put("secretId", SECRETID);
        params.put("businessId", BUSINESSID);
        params.put("version", "v5");
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        params.put("nonce", String.valueOf(new Random().nextInt()));
        // MD5, SM3, SHA1, SHA256
        params.put("signatureMethod", "MD5");

        // 2.设置私有参数
        params.put("dataId", "ebfcad1c-dba1-490c-b4de-e784c2691768");
        params.put("content", "易盾v5异步检测接口测试内容！");
        // params.put("dataType", "1");
        // params.put("ip", "123.115.77.137");
        // params.put("account", "java@163.com");
        // params.put("deviceType", "4");
        // params.put("deviceId", "92B1E5AA-4C3D-4565-A8C2-86E297055088");
        // params.put("callback", "ebfcad1c-dba1-490c-b4de-e784c2691768");
        // params.put("publishTime", String.valueOf(System.currentTimeMillis()));
        // 主动回调地址url,如果设置了则走主动回调逻辑
        // params.put("callbackUrl", "http://***");

        // 预处理参数
        params = Utils.pretreatmentParams(params);
        // 3.生成签名信息
        String signature = SignatureUtils.genSignature(SECRETKEY, params);
        params.put("signature", signature);

        // 4.发送HTTP请求，这里使用的是HttpClient工具包，产品可自行选择自己熟悉的工具包发送请求
        String response = HttpClient4Utils.sendPost(httpClient, API_URL, params, Consts.UTF_8);

        // 5.解析接口返回值
        JsonObject jObject = new JsonParser().parse(response).getAsJsonObject();
        int code = jObject.get("code").getAsInt();
        String msg = jObject.get("msg").getAsString();
        if (code == 200) {
            if (jObject.has("result")) {
                JsonObject resultObject = jObject.getAsJsonObject("result");
                long dealingCount = resultObject.get("dealingCount").getAsLong();
                if (resultObject.has("checkTexts")) {
                    JsonArray checkTexts = resultObject.get("checkTexts").getAsJsonArray();
                    System.out.println(String.format("缓冲池剩余待检测量: %s，提交结果: %s", dealingCount, checkTexts));
                    if (checkTexts != null && checkTexts.size() > 0) {
                        for (JsonElement checkTextElement : checkTexts) {
                            JsonObject checkText = checkTextElement.getAsJsonObject();
                            String taskId = checkText.get("taskId").getAsString();
                            String dataId = checkText.get("dataId").getAsString();
                        }
                    }
                }
            }
        } else {
            System.out.println(String.format("ERROR: code=%s, msg=%s", code, msg));
        }
    }
}
