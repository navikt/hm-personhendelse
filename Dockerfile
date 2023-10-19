FROM ghcr.io/navikt/baseimages/temurin:17
COPY build/libs/hm-personhendelse-all.jar app.jar
