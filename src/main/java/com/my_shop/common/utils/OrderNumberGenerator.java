package com.my_shop.common.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OrderNumberGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final AtomicInteger sequence = new AtomicInteger(0);

    /**
     * 주문번호 생성
     * 형식: ORD + yyyyMMddHHmmss + 4자리 시퀀스
     * 예: ORD202402221234560001
     */
    public String generate() {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        int seq = sequence.incrementAndGet() % 10000;
        return String.format("ORD%s%04d", timestamp, seq);
    }
}
