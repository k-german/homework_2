package org.hiber.repository;

import org.hiber.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("""
                update User u
                   set u.name = :name,
                       u.email = :email,
                       u.age = :age
                 where u.id = :id
            """)
    int updateIfExists(@Param("id") Long id,
                       @Param("name") String name,
                       @Param("email") String email,
                       @Param("age") Integer age);
}
