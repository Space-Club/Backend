package com.spaceclub.form.repository;

import com.spaceclub.form.domain.FormItemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormItemUserRepository extends JpaRepository<FormItemUser, Long> {

}
