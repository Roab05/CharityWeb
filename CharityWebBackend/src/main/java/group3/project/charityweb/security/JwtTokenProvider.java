package group3.project.charityweb.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationMs; // Dành cho Access Token (VD: 15 phút)

    @Value("${app.jwt.refreshExpiration}") // 👈 THÊM DÒNG NÀY
    private int refreshExpirationMs; // Dành cho Refresh Token (VD: 7 ngày)

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // 1. Tạo Access Token (Sống 15 phút) dùng khi Đăng nhập
    public String generateAccessToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return buildToken(userPrincipal.getUsername(), roles, jwtExpirationMs);
    }

    // 2. Tạo Access Token MỚI từ Username (Dùng khi Refresh Token)
    public String generateAccessTokenFromUsername(String username) {
        return buildToken(username, null, jwtExpirationMs);
    }

    // 3. Tạo Refresh Token (Sống 7 ngày)
    public String generateRefreshToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        // Refresh Token không cần lưu Role cho nhẹ, chỉ cần lưu Username
        return buildToken(userPrincipal.getUsername(), null, refreshExpirationMs);
    }

    // Hàm helper dùng chung để build token cho gọn code
    private String buildToken(String username, List<String> roles, int expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        JwtBuilder builder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256);

        if (roles != null && !roles.isEmpty()) {
            builder.claim("roles", roles);
        }

        return builder.compact();
    }

    // Lấy username từ token
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Kiểm tra token có hợp lệ không
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        }
        return false;
    }
}