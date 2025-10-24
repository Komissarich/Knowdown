package knowdown.user_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import knowdown.user_service.service.JwtService;
import knowdown.user_service.service.UserService;
import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component()
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";

    @Autowired
    private  JwtService jwtService;
    @Autowired
    private  UserService userService;


    @Override
    protected void doFilterInternal(@NonNull  HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        //получаем токен из заголовка
        var authHeader = request.getHeader(HEADER_NAME);
        //проверяем наличие и формат заголовка Authorization
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            //обрезаем префикс и получаем чистый JWT токен
            var jwt = authHeader.substring(BEARER_PREFIX.length()).trim();

            //извлекаем username из токена
            var username = jwtService.extractUsername(jwt);

            //проверяем, что username извлечен и пользователь еще не аутентифицирован
            if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(username);

                //если токен валиден, то аутентифицируем пользователя
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    SecurityContextHolder.setContext(context);
                }
            }
        } catch (Exception e) {
            logger.error("невозможно установить аутентификациб пользователю ", e);
        }
        filterChain.doFilter(request, response);
    }
}
