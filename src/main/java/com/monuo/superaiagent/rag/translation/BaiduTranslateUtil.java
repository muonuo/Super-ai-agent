package com.monuo.superaiagent.rag.translation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

//@Component
@Slf4j
public class BaiduTranslateUtil {

    @Value("${spring.ai.rag.baidu-translate.app-id:}")
    private String appId;

    @Value("${spring.ai.rag.baidu-translate.app-key:}")
    private String appKey;

    private static final String TRANSLATE_API_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    public String translate(String query, String from, String to) {
        if (appId == null || appId.isEmpty() || appKey == null || appKey.isEmpty()) {
            log.warn("百度翻译 API 密钥未配置，返回原始查询");
            return query;
        }

        try {
            String salt = String.valueOf(System.currentTimeMillis());
            String sign = generateSign(query, salt);

            Map<String, String> params = new HashMap<>();
            params.put("q", query);
            params.put("from", from);
            params.put("to", to);
            params.put("appid", appId);
            params.put("salt", salt);
            params.put("sign", sign);

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.postForObject(
                    TRANSLATE_API_URL, params, Map.class);

            if (response != null && "0".equals(String.valueOf(response.get("error_code")))) {
                @SuppressWarnings("unchecked")
                Map<String, Object> transResult = (Map<String, Object>) response.get("trans_result");
                if (transResult != null && transResult.get("dst") != null) {
                    return (String) transResult.get("dst");
                }
            } else if (response != null) {
                log.error("百度翻译 API 错误: {}", response.get("error_msg"));
            }
        } catch (Exception e) {
            log.error("百度翻译调用失败: {}", e.getMessage());
        }

        return query;
    }

    private String generateSign(String query, String salt) {
        String str = appId + query + salt + appKey;
        return md5(str);
    }

    private String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(StandardCharsets.UTF_8.encode(str));
            return String.format("%032x", new BigInteger(1, md.digest()));
        } catch (Exception e) {
            log.error("MD5 加密失败", e);
            return "";
        }
    }
}
