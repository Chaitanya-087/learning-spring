package com.learning.spring.social.business;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NeedsAuth {
    String loginPage() default "/login";
}
