package tech.makcymal.polylang.security.filter_chain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.makcymal.polylang.security.context.JwtAuthHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static tech.makcymal.polylang.common.CommonUtils.sleep;

@Slf4j
@Component
@RequiredArgsConstructor
public class AntiFraudFilter extends OncePerRequestFilter {

    private static final RequestMatcher[] protectMatchers = new RequestMatcher[]{
            PathPatternRequestMatcher.pathPattern(HttpMethod.POST, "/users/register"),
            PathPatternRequestMatcher.pathPattern(HttpMethod.POST, "/talks/*"),
            PathPatternRequestMatcher.pathPattern(HttpMethod.PUT, "/talks/record/**")
    };

    private final JwtAuthHolder authHolder;

    private final Map<UUID, List<LocalDateTime>> lastClientAccess = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        UUID clientId = authHolder.get().getClientId();

        lastClientAccess.computeIfAbsent(
                clientId,
                _ -> Collections.synchronizedList(Arrays.asList(new LocalDateTime[protectMatchers.length]))
        );
        List<LocalDateTime> lastAccess = lastClientAccess.get(clientId);

        IntStream.range(0, protectMatchers.length)
                .filter(i -> protectMatchers[i].matches(request))
                .findFirst()
                .ifPresent(i -> {
                    if (lastAccess.get(i) != null && lastAccess.get(i).plusSeconds(3).isAfter(LocalDateTime.now())) {
                        sleep(Duration.ofSeconds(2));
                    }
                    lastAccess.set(i, LocalDateTime.now());
                });

        filterChain.doFilter(request, response);
    }

}
