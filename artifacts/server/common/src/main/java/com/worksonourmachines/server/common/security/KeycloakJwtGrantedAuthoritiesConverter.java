package com.worksonourmachines.server.common.security;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

public class KeycloakJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String REALM_ROLES_CLAIM = "realm_roles";
    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_CLAIM = "roles";

    private final JwtGrantedAuthoritiesConverter scopeAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        Collection<GrantedAuthority> scopeAuthorities = scopeAuthoritiesConverter.convert(jwt);
        if (scopeAuthorities != null) {
            authorities.addAll(scopeAuthorities);
        }

        addRealmRoles(authorities, jwt.getClaim(REALM_ROLES_CLAIM));
        Object realmAccess = jwt.getClaim(REALM_ACCESS_CLAIM);
        if (realmAccess instanceof Map<?, ?> realmAccessMap) {
            addRealmRoles(authorities, realmAccessMap.get(ROLES_CLAIM));
        }
        return authorities;
    }

    private static void addRealmRoles(Set<GrantedAuthority> authorities, Object rolesClaim) {
        if (!(rolesClaim instanceof Collection<?> roles)) {
            return;
        }

        roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .forEach(authorities::add);
    }
}
