package com.capstone.global.util;

import org.springframework.stereotype.Component;

@Component
public class UrlGenerator {
    // private static final String BASE_URL_TASK = "https://docktalk.co.kr/task/";
    // private static final String BASE_URL_PROJECT = "https://docktalk.co.kr/project/";
    // private static final String BASE_URL_DOCUMENT = "https://docktalk.co.kr/document/";
    private static final String BASE_URL_TASK = "http://180.210.81.232:8080/task/";
    private static final String BASE_URL_PROJECT = "http://180.210.81.232:8080/project/";
    private static final String BASE_URL_DOCUMENT = "http://180.210.81.232:8080/document/";

    public static String createTaskUrl(String id){
        return BASE_URL_TASK + id;
    }

    public static String createProjectUrl(String id){
        return BASE_URL_PROJECT +  id;
    }

    public static String createDocumentUrl(String id){
        return BASE_URL_DOCUMENT +  id;
    }
}
