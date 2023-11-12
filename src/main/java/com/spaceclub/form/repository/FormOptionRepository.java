package com.spaceclub.form.repository;

import com.spaceclub.form.domain.FormOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormOptionRepository extends JpaRepository<FormOption, Long> {

    List<FormOption> findByForm_Id(Long formId);

}
