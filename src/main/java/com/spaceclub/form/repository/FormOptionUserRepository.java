package com.spaceclub.form.repository;

import com.spaceclub.form.domain.FormOptionUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormOptionUserRepository extends JpaRepository<FormOptionUser, Long> {

    List<FormOptionUser> findByFormOption_Form_Id(Long formId);

    Optional<FormOptionUser> findByFormOption_IdAndUserId(Long formOptionId, Long userId);

}
