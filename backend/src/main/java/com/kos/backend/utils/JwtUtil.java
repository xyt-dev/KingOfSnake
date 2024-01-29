// 定义一个名为JwtUtil的工具类，用于JWT的创建和解析。
package com.kos.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    // 定义一个常量JWT_TTL，表示JWT的有效期（14天，单位毫秒）。
    // JWT_TTL: "JWT Time To Live"，JWT的存活时间。
    public static final long JWT_TTL = 60 * 60 * 1000L * 24 * 14;

    // 定义一个字符串常量JWT_KEY，用作生成JWT签名的密钥。
    public static final String JWT_KEY = "SDFGjhdsfalshdfHFdsjkdsfds121232131baldfaced";

    // 生成一个去除"-"的UUID字符串作为JWT的唯一标识符。
    // getUUID: "Get Universally Unique Identifier"，获取通用唯一识别码。
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // 创建并返回一个JWT字符串。subject参数是JWT的主题（subject字段）。
    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());
        return builder.compact();
    }

    // 创建并配置JwtBuilder。subject是JWT的主题，ttlMillis是JWT的有效时间，uuid是JWT的唯一标识符。
    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        // 使用HS256算法作为签名算法。
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 生成JWT的签名密钥。
        SecretKey secretKey = generalKey();

        // 获取当前时间作为JWT的发行时间。
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // 如果没有指定有效期，则使用默认的14天。
        if (ttlMillis == null) {
            ttlMillis = JwtUtil.JWT_TTL;
        }

        // 计算JWT的过期时间。
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);

        // 构建JWT，设置其唯一标识符、主题、发行者、发行时间、签名密钥和过期时间。
        return Jwts.builder()
                .setId(uuid)
                .setSubject(subject)
                .setIssuer("sg")
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, secretKey)
                .setExpiration(expDate);
    }

    // 根据JWT_KEY生成JWT的签名密钥。
    // generalKey: "Generate General Key"，生成通用密钥。
    public static SecretKey generalKey() {
        // 对JWT_KEY进行Base64解码以获取密钥的二进制表示。
        byte[] encodeKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);

        // 使用HmacSHA256算法创建一个新的密钥规范。(Spec: Specification)
        return new SecretKeySpec(encodeKey, 0, encodeKey.length, "HmacSHA256");
    }

    // 解析JWT字符串，返回包含JWT声明的Claims对象。
    // parseJWT: "Parse JSON Web Token"，解析JSON网络令牌。
    public static Claims parseJWT(String jwt) throws Exception {
        // 生成JWT的签名密钥。
        SecretKey secretKey = generalKey();

        // 解析JWT字符串，验证其签名，并返回包含声明的Claims对象。
        return Jwts.parserBuilder() // (高版本依赖没有该函数)
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        // 这段代码的功能是从一个JWT字符串中提取并返回包含在该JWT中的声明（Claims）

        // Jwts.parserBuilder(): 这是io.jsonwebtoken库中的一个方法，用于创建一个JwtParserBuilder实例。

        // .setSigningKey(secretKey): 这个方法用于设置解析器的签名密钥。secretKey是一个SecretKey对象，包含用于验证JWT签名的密钥。
        // 在这个上下文中，密钥是从预定义的密钥字符串（JwtUtil.JWT_KEY）生成的。这一步骤是验证JWT签名的关键，确保了JWT的完整性和真实性。

        // .build(): 这个方法用于构建配置好的JWT解析器实例。调用build()后，我们会得到一个JwtParser实例，它根据前面的配置准备好解析JWT。

        // .parseClaimsJws(jwt): 这个方法用于解析传入的JWT字符串。jwt是一个包含JWT的字符串。parseClaimsJws方法会首先验证JWT的签名，然后解析JWT的内容。
        // 如果签名验证失败（比如如果JWT被篡改过），这个方法会抛出异常。

        // .getBody(): 在JWT被成功解析并验证签名之后，这个方法用于获取JWT的主体（body），它通常包含了一系列的声明（Claims）。
        // 这些声明是JWT的负载（payload），包括但不限于用户标识、权限、过期时间等信息。
    }
}