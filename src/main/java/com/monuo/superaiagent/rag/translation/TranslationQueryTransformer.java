package com.monuo.superaiagent.rag.translation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class TranslationQueryTransformer implements QueryTransformer {

    private final BaiduTranslateUtil baiduTranslateUtil;

    @Value("${spring.ai.rag.translation.from-lang:auto}")
    private String fromLang;

    @Value("${spring.ai.rag.translation.to-lang:zh}")
    private String toLang;

    public TranslationQueryTransformer(BaiduTranslateUtil baiduTranslateUtil) {
        this.baiduTranslateUtil = baiduTranslateUtil;
    }

    @Override
    public Query transform(Query query) {
        String originalText = query.text();

        if (originalText == null || originalText.trim().isEmpty()) {
            return query;
        }

        log.debug("翻译前: {}", originalText);

        String translatedText = baiduTranslateUtil.translate(originalText, fromLang, toLang);

        log.debug("翻译后: {}", translatedText);

        return new Query(translatedText);
    }
}
