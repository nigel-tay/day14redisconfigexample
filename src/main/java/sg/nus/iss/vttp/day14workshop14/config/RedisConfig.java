package sg.nus.iss.vttp.day14workshop14.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private Integer redisPort;

    @Value("${spring.redis.username}")
    private String redisUser;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Bean
    @Scope("singleton")
    public RedisTemplate<String, Object> getRedisTemplate() {
        final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();

        config.setHostName(redisHost);
        config.setPort(redisPort);

        if (!redisUser.isEmpty() && !redisPassword.isEmpty()) {
            config.setUsername(redisUser);
            config.setPassword(redisPassword);
        }

        config.setDatabase(0);

        final JedisClientConfiguration jedisClient = JedisClientConfiguration.builder()
                                                                            .build();
        final JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
        jedisFac.afterPropertiesSet();
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // Associate to redis connection
        redisTemplate.setConnectionFactory(jedisFac);

        // Set the list key and hash key serialization
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // For list
        redisTemplate.setHashKeySerializer(new StringRedisSerializer()); // For hash

        // Enable redis to store java objects on the value column
        // Enabling the java object to be saved as a value in redis
        RedisSerializer<Object> objSerializer = new JdkSerializationRedisSerializer(getClass().getClassLoader());
        redisTemplate.setValueSerializer(objSerializer); // For list and hash (contact id)
        redisTemplate.setHashValueSerializer(objSerializer); // For hash

        return redisTemplate;
    }
}
