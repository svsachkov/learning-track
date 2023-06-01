package hse.sachkov.learningtrackbackend.security.jwt;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();

        jwtInfoToken.setName(claims.getSubject());

        // Retrieve roles from claims
        Set<String> roles = new HashSet<>();
        if (claims.containsKey("authorities")) {
            Object rolesClaim = claims.get("authorities");
            if (rolesClaim instanceof List<?>) {
                List<?> rolesList = (List<?>) rolesClaim;
                for (Object role : rolesList) {
                    if (role instanceof LinkedHashMap<?, ?>) {
                        LinkedHashMap<?, ?> roleMap = (LinkedHashMap<?, ?>) role;
                        Object authority = roleMap.get("authority");
                        if (authority instanceof String) {
                            roles.add((String) authority);
                        }
                    }
                }
            }
        }

        jwtInfoToken.setRoles(roles);

        return jwtInfoToken;
    }
}
