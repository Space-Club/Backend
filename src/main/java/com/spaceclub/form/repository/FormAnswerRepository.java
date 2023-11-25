package com.spaceclub.form.repository;

import com.spaceclub.form.domain.FormAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormAnswerRepository extends JpaRepository<FormAnswer, Long> {

    List<FormAnswer> findByFormOption_Form_Id(Long formId);

    Optional<FormAnswer> findByFormOption_IdAndUserId(Long formOptionId, Long userId);

}
