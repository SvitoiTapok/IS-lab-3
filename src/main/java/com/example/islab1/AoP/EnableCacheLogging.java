package com.example.islab1.AoP;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableCacheLogging {
}
