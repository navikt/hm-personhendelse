FROM ghcr.io/navikt/baseimages/temurin:21
COPY build/libs/hm-personhendelse-all.jar app.jar
