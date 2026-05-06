package com.yukeshkumar.task_management_service.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getCredentials() != null) {

            String token = auth.getCredentials().toString();

            System.out.println("✅ TOKEN SENT TO PROJECT SERVICE: " + token);

            template.header("Authorization", "Bearer " + token);

        } else {
            System.out.println("❌ TOKEN NOT FOUND IN SECURITY CONTEXT");
        }
    }
}