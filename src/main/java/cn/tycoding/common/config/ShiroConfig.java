package cn.tycoding.common.config;

import cn.tycoding.common.dto.SysConstant;
import cn.tycoding.common.realm.AuthRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author tycoding
 * @date 2019-01-20
 */
@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setUnauthorizedUrl("/error/403");

        //配置拦截器链，注意顺序
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/gifCode", "anon");

        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/fonts/**", "anon");
        filterChainDefinitionMap.put("/img/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/lib/**", "anon");

        filterChainDefinitionMap.put("/**", "user");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public Realm realm(HashedCredentialsMatcher matcher) {
        AuthRealm realm = new AuthRealm();
        realm.setCredentialsMatcher(matcher);
        return realm;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRememberMeManager(rememberMeManager());
        securityManager.setSessionManager(sessionManager());
//        securityManager.setCacheManager(cacheManager());
        securityManager.setRealm(realm(hashedCredentialsMatcher()));
        return securityManager;
    }

    @Bean
    public SimpleCookie rememberCookie() {
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        return simpleCookie;
    }

    @Bean
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberCookie());
        cookieRememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
        return cookieRememberMeManager;
    }

    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName(SysConstant.ALGORITHNAME);
        credentialsMatcher.setHashIterations(SysConstant.HASHNUM);
        return credentialsMatcher;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启Shiro的注解支持
     * 比如：@RequireRoles @RequireUsers
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public SessionManager sessionManager() {
        MySessionManager mySessionManager = new MySessionManager();
        mySessionManager.setSessionDAO(redisSessionDAO());
//        mySessionManager.setCacheManager(cacheManager());
        mySessionManager.setSessionIdUrlRewritingEnabled(true);
        return mySessionManager;
    }

    /**
     * 使用shiro-redis配置
     *
     * @return
     */
    @ConfigurationProperties(prefix = "redis.shiro")
    public RedisManager redisManager() {
        return new RedisManager();
    }

    /**
     * redis实现缓存
     *
     * @return
     */
    @Bean
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }

    /**
     * 使用Redis实现 shiro sessionDao
     *
     * @return
     */
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }

//    @Bean
//    public SessionManager sessionManager() {
//        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
//        defaultWebSessionManager.setGlobalSessionTimeout(1800000);
//        defaultWebSessionManager.setSessionIdCookieEnabled(true);
//        defaultWebSessionManager.setSessionIdCookie(simpleCookie());
//        defaultWebSessionManager.setCacheManager(cacheManager());
//        defaultWebSessionManager.setSessionIdUrlRewritingEnabled(true);
//        defaultWebSessionManager.setSessionDAO(sessionDAO());
//        return defaultWebSessionManager;
//    }
//
//    @Bean
//    public SessionDAO sessionDAO() {
//        EnterpriseCacheSessionDAO sessionDAO = new EnterpriseCacheSessionDAO();
//        sessionDAO.setActiveSessionsCacheName("shiro-activeSessionCache");
//        sessionDAO.setSessionIdGenerator(new JavaUuidSessionIdGenerator());
//        return sessionDAO;
//    }
//
//    @Bean
//    public SimpleCookie simpleCookie() {
//        SimpleCookie simpleCookie = new SimpleCookie();
//        simpleCookie.setName("cn.tycoding.id");
//        simpleCookie.setHttpOnly(true);
//        simpleCookie.setMaxAge(180000);
//        return simpleCookie;
//    }
//
//    /**
//     * Shiro本身只提供了Cahche缓存的接口，并不提供实现类。EhCacheManager是Shiro-Cache的一个实现类
//     *
//     * @return
//     */
//    @Bean
//    public CacheManager cacheManager() {
//        EhCacheManager cacheManager = new EhCacheManager();
//        cacheManager.setCacheManagerConfigFile("classpath:config/shiro-ehcache.xml");
//        return new EhCacheManager();
//    }

}
