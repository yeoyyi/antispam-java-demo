/*
 * @(#) ImageListSubmitAPIDemo.java 2020-10-28
 *
 * Copyright 2020 NetEase.com, Inc. All rights reserved.
 */

package com.netease.is.antispam.demo.image;

import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.client.HttpClient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.netease.is.antispam.demo.utils.HttpClient4Utils;
import com.netease.is.antispam.demo.utils.Utils;

/**
 * 调用易盾反垃圾云服务图片名单更新接口API示例
 *
 * @author yd-dev
 * @version 2020-10-28
 */
public class ImageListUpdateAPIDemo {

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
     * 易盾反垃圾云服务图片名单更新接口地址
     */
    private final static String API_URL = "http://as.dun.163.com/v1/image/list/update";
    /**
     * 实例化HttpClient，发送http请求使用，可根据需要自行调参
     */
    private static HttpClient httpClient = HttpClient4Utils.createHttpClient(100, 20, 10000, 2000, 2000);

    public static void main(String[] args) {
        // 1. 设置公共参数
        Map<String, String> params = Utils.getCommonParams(SECRETID, BUSINESSID, "v1.0", "MD5");

        // 2. 设置私有参数
        params.put("type", "1");
        params.put("uuid", "512e7771c3074621914ae1894e0df5c1");
        params.put("status", "0");

        // 预处理参数
        params = Utils.pretreatmentParams(params);

        // 3. 生成签名信息
        Utils.sign(params, SECRETKEY);

        // 4. 发送HTTP请求，这里使用的是HttpClient工具包，产品可自行选择自己熟悉的工具包发送请求
        String response = HttpClient4Utils.sendPost(httpClient, API_URL, params, Consts.UTF_8);

        // 5. 解析接口返回值
        JsonObject resultObject = new JsonParser().parse(response).getAsJsonObject();
        int code = resultObject.get("code").getAsInt();
        String msg = resultObject.get("msg").getAsString();
        if (code == 200) {
            JsonObject result = resultObject.getAsJsonObject("result");
            System.out.printf("Update success: %s", result.getAsBoolean());
        } else {
            System.out.printf("ERROR: code=%s, msg=%s%n", code, msg);
        }
    }
}
