package com.prady.blogWeb.service;


import com.prady.blogWeb.entity.Article;
import com.prady.blogWeb.exception.ResourceNotFoundException;
import com.prady.blogWeb.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ArticleRepository articleRepository;

    public <T> T get(String key,Class<T> entityClass){
        try{
            Object o = redisTemplate.opsForValue().get(key);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(o.toString(),entityClass);
        }catch(Exception e){
            return null;
        }
    }

    public void set(String key,Object o,Long ttl){
        try{
            ObjectMapper mapper = new ObjectMapper();
            String jsonValue = mapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key,jsonValue,ttl, TimeUnit.SECONDS);
        }catch(Exception e){
            System.out.print(e);
        }
    }


    public void deleteByPrefix(String prefix) {

        RedisConnection connection = null;

        try {
            ScanOptions options = ScanOptions.scanOptions()
                    .match(prefix+"*")
                    .count(100)
                    .build();

            connection = redisTemplate.getConnectionFactory().getConnection();

            try (Cursor<byte[]> cursor = connection.scan(options)) {

                Set<String> keys = new HashSet<>();

                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next()));
                }

                if (!keys.isEmpty()) {
                    redisTemplate.delete(keys);
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public Long getViews(String key){

            return redisTemplate.opsForValue().increment(key,1);

    }

    @Scheduled(fixedRate = 60000)
    public void updateArticleViews(){
        RedisConnection connection = null;

        try {
            ScanOptions options = ScanOptions.scanOptions()
                    .match("article-views:*")
                    .count(100)
                    .build();

            connection = redisTemplate.getConnectionFactory().getConnection();

            try (Cursor<byte[]> cursor = connection.scan(options)) {

                Set<String> keys = new HashSet<>();

                while (cursor.hasNext()) {

                    String key = new String((cursor.next()));
                    Object value = redisTemplate.opsForValue().get(key);
                    if (value == null) {
                        continue;
                    }
                    long views = Long.parseLong(value.toString());
                    if (views > 0) {
                        Long id = Long.parseLong(key.substring(14));
                        articleRepository.incrementViews(id,views);
                        redisTemplate.delete(key);
                    }

                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

}
