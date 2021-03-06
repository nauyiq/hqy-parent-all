package com.hqy.security.config;

import com.hqy.security.core.client.SecurityClientDetailsServiceImpl;
import com.hqy.security.server.PasswordEnhanceTokenGranter;
import com.hqy.security.server.RedisAuthorizationCodeServer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.ArrayList;
import java.util.List;

/**
 * oauth2????????????????????? ????????????????????????
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 15:25
 */
@Configuration
@RequiredArgsConstructor
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;

    private final SecurityClientDetailsServiceImpl clientDetailsService;

    private final JwtTokenStore tokenStore;

    private final TokenEnhancer tokenEnhancer;

    private final JwtAccessTokenConverter jwtAccessTokenConverter;

    private final RedisAuthorizationCodeServer redisAuthorizationCodeServer;


    /**
     * ?????????????????????
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);
    }

    /**
     * ?????????????????????????????????????????????TokenStore???TokenGranter???OAuth2RequestFactory
     * /oauth/authorize         ??????????????????
     * /auth/token              ????????????
     * /auth/confirm-access     ??????????????????????????????
     * /auth/error              ??????????????????????????????
     * /auth/check_token        ?????????????????????????????????????????????
     * /auth/token_key          ???????????????????????????
     *
     * ???????????????????????????
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        // Token??????
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
        tokenEnhancers.add(tokenEnhancer);
        tokenEnhancers.add(jwtAccessTokenConverter);
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        //token?????????????????? ?????????InMemoryTokenStore????????????????????????
        endpoints.tokenStore(tokenStore);

        //???????????????
        endpoints.authorizationCodeServices(redisAuthorizationCodeServer);

        //??????oauth ??????
        ClientDetailsService clientDetails = endpoints.getClientDetailsService();
        AuthorizationServerTokenServices tokenServices = endpoints.getTokenServices();
        AuthorizationCodeServices authorizationCodeServices = endpoints.getAuthorizationCodeServices();
        OAuth2RequestFactory requestFactory = endpoints.getOAuth2RequestFactory();

        //tokenGranters??????oauth?????? ????????????/oauth/token??????????????????????????????AbstractTokenGranter ??????
        List<TokenGranter> tokenGranters = new ArrayList<>();
        //???????????????   GRANT_TYPE = "client_credentials";
        tokenGranters.add(new ClientCredentialsTokenGranter(tokenServices, clientDetails, requestFactory));
        //????????????	  GRANT_TYPE = "password";
        tokenGranters.add(new PasswordEnhanceTokenGranter(authenticationManager, tokenServices,clientDetails, requestFactory));
        //???????????????   GRANT_TYPE = "authorization_code";
        tokenGranters.add(new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices, clientDetails,requestFactory));
        //????????????	  GRANT_TYPE = "refresh_token";
        tokenGranters.add(new RefreshTokenGranter(tokenServices, clientDetails, requestFactory));
        //????????????	  GRANT_TYPE = "implicit";
        tokenGranters.add(new ImplicitTokenGranter(tokenServices, clientDetails, requestFactory));

        endpoints
                .authenticationManager(authenticationManager)
                .accessTokenConverter(jwtAccessTokenConverter)
                .tokenEnhancer(tokenEnhancerChain)
                .tokenGranter(new CompositeTokenGranter(tokenGranters))
                .tokenServices(tokenServices(endpoints));

    }

    /**
     * ?????????????????????????????? ?????????????????????????????????
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }



    private AuthorizationServerTokenServices tokenServices(AuthorizationServerEndpointsConfigurer endpoints) {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
        tokenEnhancers.add(tokenEnhancer);
        tokenEnhancers.add(jwtAccessTokenConverter);
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        DefaultTokenServices tokenServices = new DefaultTokenServices();
        //??????????????????
        tokenServices.setTokenStore(endpoints.getTokenStore());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setClientDetailsService(clientDetailsService);
        tokenServices.setTokenEnhancer(tokenEnhancerChain);

        /* refresh_token????????????????????????????????????(true)??????????????????(false)????????????true
           1 ???????????????access_token?????????????????? refresh_token?????????????????????????????????????????????????????????
           2 ??????????????????access_token?????????????????? refresh_token????????????????????????refresh_token??????????????????????????????????????????????????????????????????
         */
        tokenServices.setReuseRefreshToken(true);

        return tokenServices;
    }



}
