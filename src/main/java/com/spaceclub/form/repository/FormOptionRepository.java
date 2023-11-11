package com.spaceclub.form.repository;

import com.spaceclub.form.domain.FormOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormOptionRepository extends JpaRepository<FormOption, Long> {

}
