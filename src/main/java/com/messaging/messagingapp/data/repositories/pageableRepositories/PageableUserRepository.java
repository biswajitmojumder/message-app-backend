package com.messaging.messagingapp.data.repositories.pageableRepositories;

import com.messaging.messagingapp.data.entities.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageableUserRepository extends PagingAndSortingRepository<UserEntity, Long> {
    List<UserEntity> findAllByUsernameContains(String username, Pageable pageable);
}
