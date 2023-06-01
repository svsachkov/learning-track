package hse.sachkov.learningtrackbackend.security.applicationuser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationUserPermission {

    READ("read:user"), EDIT("edit:editor"), ADMIN("admin:admin");

    private final String permission;
}
