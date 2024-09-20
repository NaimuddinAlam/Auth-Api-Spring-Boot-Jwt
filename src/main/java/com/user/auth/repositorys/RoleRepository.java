package com.user.auth.repositorys;

import com.user.auth.model.AppRole;
import com.user.auth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository  extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
