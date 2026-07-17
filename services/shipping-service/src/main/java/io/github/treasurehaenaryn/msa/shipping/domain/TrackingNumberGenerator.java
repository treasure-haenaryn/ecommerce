package io.github.treasurehaenaryn.msa.shipping.domain;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 운송장 번호 생성기. 형식: SHIP-yyyyMMdd-XXXXXX (뒤 6자리는 영대문자+숫자 랜덤).
 */
public final class TrackingNumberGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int RANDOM_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate() {
        String datePart = LocalDate.now().format(DATE_FORMAT);
        StringBuilder randomPart = new StringBuilder(RANDOM_LENGTH);
        for (int i = 0; i < RANDOM_LENGTH; i++) {
            randomPart.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return "SHIP-" + datePart + "-" + randomPart;
    }

    private TrackingNumberGenerator() {
    }
}
