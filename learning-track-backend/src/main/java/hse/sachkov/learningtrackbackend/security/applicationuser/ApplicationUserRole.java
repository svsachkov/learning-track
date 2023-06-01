package hse.sachkov.learningtrackbackend.security.applicationuser;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum ApplicationUserRole {

    USER(Sets.newHashSet(ApplicationUserPermission.READ)),
    EDITOR(Sets.newHashSet(ApplicationUserPermission.READ, ApplicationUserPermission.EDIT)),
    ADMIN(Sets.newHashSet(ApplicationUserPermission.READ, ApplicationUserPermission.EDIT, ApplicationUserPermission.ADMIN));

    private final Set<ApplicationUserPermission> permissions;
}
