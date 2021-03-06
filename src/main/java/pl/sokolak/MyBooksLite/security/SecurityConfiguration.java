package pl.sokolak.MyBooksLite.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Arrays;
import java.util.List;

@EnableWebSecurity
@Configuration
//@Order(1)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_SUCCESS_URL = "/login";
    @Autowired
    private UserDetailsService userDetailsService;
    public static String[] activeProfiles;

    @Value("${spring.profiles.active}")
    public void setDatabase(String[] ap) {
        activeProfiles = ap;
    }

    public static List<String> getActiveProfiles() {
        return Arrays.asList(activeProfiles);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if(getActiveProfiles().contains("dev")) {
            http.csrf().disable()
                    .authorizeRequests()
                    .anyRequest()
                    .permitAll();
        }
        else {
            http.csrf().disable()
                    .requestCache().requestCache(new CustomRequestCache())
                    .and().authorizeRequests()
                    .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
                    .anyRequest().authenticated()
                    .and().formLogin()
                    .loginPage(LOGIN_URL).permitAll()
                    .loginProcessingUrl(LOGIN_PROCESSING_URL)
                    .failureUrl(LOGIN_FAILURE_URL)
                    .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
        }
    }

    @Override
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                "/VAADIN/**",
                "/favicon.ico",
                "/robots.txt",
                "/manifest.webmanifest",
                "/sw.js",
                "/offline.html",
                "/icons/**",
                "/images/**",
                "/styles/**",
                "/h2-console/**");
    }
}