package org.wyvie.chehov.database.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wyvie.chehov.database.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Page<UserEntity> findAllByOrderByKarmaDesc(Pageable pageable);
}
