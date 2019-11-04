package zz.test.orders.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * RedisAPI
 *
 * @author bdqn_shang
 * @date 2018-1-10
 */
@Component
public class RedisUtils {

    private Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * set key and value to redis
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key, String value) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //设置序列化Value的实例化对象
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        vo.set(key, value);
        return true;
    }

    /**
     * set key and value to redis
     *
     * @param key
     * @param seconds 有效期
     * @param value
     * @return
     */
    public boolean set(String key, long seconds, String value) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //设置序列化Value的实例化对象
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        vo.set(key, value);
        expire(key, seconds);
        return true;
    }

    /**
     * 判断某个key是否存在
     *
     * @param key
     * @return
     */
    public boolean exist(String key) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //设置序列化Value的实例化对象
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        Object value = vo.get(key);
        return EmptyUtils.isEmpty(value) ? false : true;
    }

    public Object get(String key) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //设置序列化Value的实例化对象
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        return vo.get(key);
    }

    public void delete(String key) {
        try {
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            //设置序列化Value的实例化对象
            redisTemplate.setValueSerializer(new StringRedisSerializer());
            redisTemplate.delete(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean setnx(final String key, final String value) throws Exception {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection redisConnection) {
                boolean flag = false;
                try {
                    redisTemplate.setKeySerializer(new StringRedisSerializer());
                    //设置序列化Value的实例化对象
                    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
                    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
                    byte keys[] = stringRedisSerializer.serialize(key);
                    byte values[] = stringRedisSerializer.serialize(value);
                    flag = redisConnection.setNX(keys, values);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    return flag;
                }
            }
        });
    }

    public Boolean expire(final String key, final Long expireTime) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                boolean flag = false;
                try {
                    redisTemplate.setKeySerializer(new StringRedisSerializer());
                    //设置序列化Value的实例化对象
                    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
                    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
                    byte keys[] = stringRedisSerializer.serialize(key);
                    flag = redisConnection.expire(keys, expireTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return flag;
            }
        });
    }

    public boolean lock(String key) {
        boolean flag = false;
        try {
            String lockKey = generateLockKey(key);
            flag = setnx(lockKey, "lock"); // 如果设置了锁，则设置锁的超时
            if (flag) {
                expire(lockKey, Constants.Redis_Expire.DEFAULT_EXPIRE);
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public Object getValueNx(String key) {
        String lockKey = generateLockKey(key);
        Object object = get(lockKey);
        return object;
    }

    public void unlock(String key) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //设置序列化Value的实例化对象
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        String lockKey = generateLockKey(key);
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        connection.del(lockKey.getBytes());
        connection.close();
    }

    private String generateLockKey(String key) {
        return String.format("LOCK:%s", key);
    }

    public boolean validate(String token) {
        return exist(token);
    }
}
