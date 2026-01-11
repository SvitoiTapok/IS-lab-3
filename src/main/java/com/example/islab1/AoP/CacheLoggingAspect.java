package com.example.islab1.AoP;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManagerFactory;

@Aspect
@Component
@Slf4j
public class CacheLoggingAspect {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Around("@annotation(EnableCacheLogging)")
    public Object logCache(ProceedingJoinPoint joinPoint) throws Throwable {
        Statistics stats = entityManagerFactory.unwrap(org.hibernate.SessionFactory.class)
                .getStatistics();

        String[] cacheRegions = stats.getSecondLevelCacheRegionNames();
        for (String region : cacheRegions) {
            CacheRegionStatistics regionStats = stats.getCacheRegionStatistics(region);
            log.info("==============region: {}===============", region);
            log.info("hitCount: {}", regionStats.getHitCount());
            log.info("miss: {}", regionStats.getMissCount());
            log.info("putCount: {}", regionStats.getPutCount());
            log.info("elementCountInMemory {}", regionStats.getElementCountInMemory());
        }

//        log.info("=== QUERY CACHE STATISTICS ===");
//        log.info("Query Cache Hits: {}", stats.getQueryCacheHitCount());
//        log.info("Query Cache Misses: {}", stats.getQueryCacheMissCount());
//        log.info("Query Cache Puts: {}", stats.getQueryCachePutCount());

        long queryHitsBefore = stats.getQueryCacheHitCount();
        long queryMissesBefore = stats.getQueryCacheMissCount();
        long entityHitsBefore = stats.getSecondLevelCacheHitCount();
        long entityMissesBefore = stats.getSecondLevelCacheMissCount();

        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();

        long queryHitsAfter = stats.getQueryCacheHitCount();
        long queryMissesAfter = stats.getQueryCacheMissCount();
        long entityHitsAfter = stats.getSecondLevelCacheHitCount();
        long entityMissesAfter = stats.getSecondLevelCacheMissCount();

        long queryHits = queryHitsAfter - queryHitsBefore;
        long queryMisses = queryMissesAfter - queryMissesBefore;
        long entityHits = entityHitsAfter - entityHitsBefore;
        long entityMisses = entityMissesAfter - entityMissesBefore;

        log.info("=============== METHOD EXECUTION STATISTICS ==================");
        log.info("Method: {}", joinPoint.getSignature().getName());
        log.info("Execution time: {} ms", endTime - startTime);
        log.info("Query Cache - Hits: {}, Misses: {}", queryHits, queryMisses);
        log.info("Entity Cache - Hits: {}, Misses: {}", entityHits, entityMisses);

        return result;
    }
}