package com.sss.sssforum.config.springconfig;

import com.google.common.collect.Maps;
import com.sss.sssforum.utils.MyDateTimeUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lws
 * @date: 2020-08-06 16:36
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHelper {

    /**
     * 秘钥
     * -
     */
    @Value("${jwt.secret}")
    private String secret;
    /**
     * 有效期，单位秒
     * - 默认2周
     */
    @Value("${jwt.expire-time-in-minute}")
    private Long expirationTimeInMinute;

    /**
     * 从token中获取claim
     *
     * @param token token
     * @return claim
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(this.secret.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            log.error("token解析错误", e);
            throw new IllegalArgumentException("Token invalided.");
        }
    }


    /**
     * 获取token的过期时间
     *
     * @param token token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token)
                .getExpiration();
    }

    /**
     * 判断token是否过期
     *
     * @param token token
     * @return 已过期返回true，未过期返回false
     */
    private Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 计算token的过期时间
     *
     * @return 过期时间
     */
    private Date getExpirationTime() {
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(this.expirationTimeInMinute);
        Long milliSecond = MyDateTimeUtils.getMilliSecond(localDateTime);
        return new Date(milliSecond);
    }

    /**
     * 为指定用户生成token
     *
     * @param claims 用户信息
     * @return token
     */
    public String generateToken(Map<String, Object> claims) {
        Date createdTime = new Date();
        Date expirationTime = this.getExpirationTime();


        byte[] keyBytes = secret.getBytes();
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(createdTime)
                .setExpiration(expirationTime)
                // 你也可以改用你喜欢的算法
                // 支持的算法详见：https://github.com/jwtk/jjwt#features
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 判断token是否非法
     *
     * @param token token
     * @return 未过期返回true，否则返回false
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        }catch (Exception e){
            return false;
        }

    }

    /**
     * 判断token是否非法 并忽略过期异常
     *
     * @param token token
     * @return 未过期返回true，否则返回false
     */
    public Boolean validateTokenIgnoreException(String token) {
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(this.secret.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
            Date expiration = body.getExpiration();
            return !expiration.before(new Date());
        } catch ( Exception e) {
            return false;
        }

    }

    public static void main(String[] args) {

        // 1. 初始化
        JwtHelper jwtOperator = new JwtHelper();
       // jwtOperator.expirationTimeInMinute = 1440L;
        jwtOperator.expirationTimeInMinute = 1L;
        jwtOperator.secret = "sadsfsdhffggfjghj54646687867ytihjouilkjlufbvcmnbmn34131321x";

        // 2.设置用户信息
        HashMap<String, Object> objectObjectHashMap = Maps.newHashMap();
        objectObjectHashMap.put("userId", 4);
        objectObjectHashMap.put("openId", "oKDvewjopFLzdAobDPEU3c7gxno8");

        // 测试1: 生成token
       // String token = jwtOperator.generateToken(objectObjectHashMap);
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJvcGVuSWQiOiJvS0R2ZXdqb3BGTHpkQW9iRFBFVTNjN2d4bm84IiwidXNlcklkIjo0LCJpYXQiOjE2MDIyMTM2MTUsImV4cCI6MTYwMjIxMzY3NX0.el6MoNFW8FYiEhUBYkYX-Q9Az-6WvYzzBXWFPNWH1qE";
        // 会生成类似该字符串的内容: eyJhbGciOiJIUzI1NiJ9.eyJpZCI6IjEiLCJpYXQiOjE1NjU1ODk4MTcsImV4cCI6MTU2Njc5OTQxN30.27_QgdtTg4SUgxidW6ALHFsZPgMtjCQ4ZYTRmZroKCQ

        System.out.println(token);
       // System.out.println("过期时间："+jwtOperator.getExpirationDateFromToken(token).getTime());
        System.out.println("Token是否过期："+jwtOperator.validateTokenIgnoreException(token));
//
//        // 将我改成上面生成的token!!!
//        String someToken = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6IjEiLCJpYXQiOjE1NjU1ODk4MTcsImV4cCI6MTU2Njc5OTQxN30.27_QgdtTg4SUgxidW6ALHFsZPgMtjCQ4ZYTRmZroKCQ";
//        // 测试2: 如果能token合法且未过期，返回true
//        Boolean validateToken = jwtOperator.validateToken(someToken);
//        System.out.println(validateToken);
//
//        // 测试3: 获取用户信息
//        Claims claims = jwtOperator.getClaimsFromToken(someToken);
//        System.out.println(claims);
//
//        // 将我改成你生成的token的第一段（以.为边界）
//        String encodedHeader = "eyJhbGciOiJIUzI1NiJ9";
//        // 测试4: 解密Header
//        byte[] header = Base64.decodeBase64(encodedHeader.getBytes());
//        System.out.println(new String(header));
//
//        // 将我改成你生成的token的第二段（以.为边界）
//        String encodedPayload = "eyJpZCI6IjEiLCJpYXQiOjE1NjU1ODk1NDEsImV4cCI6MTU2Njc5OTE0MX0";
//        // 测试5: 解密Payload
//        byte[] payload = Base64.decodeBase64(encodedPayload.getBytes());
//        System.out.println(new String(payload));
//
//        // 测试6: 这是一个被篡改的token，因此会报异常，说明JWT是安全的
//        jwtOperator.validateToken("eyJhbGciOiJIUzI1NiJ9.eyJpZCI6IjEiLCJpYXQiOjE1NjU1ODk3MzIsImV4cCI6MTU2Njc5OTMzMn0.nDv25ex7XuTlmXgNzGX46LqMZItVFyNHQpmL9UQf-aUx");
    }


}
