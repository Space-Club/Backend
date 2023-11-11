package com.spaceclub.form.repository;

import com.spaceclub.form.domain.FormOptionUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormOptionUserRepository extends JpaRepository<FormOptionUser, Long> {

}
