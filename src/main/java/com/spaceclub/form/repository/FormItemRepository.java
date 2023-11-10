package com.spaceclub.form.repository;

import com.spaceclub.form.domain.FormItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormItemRepository extends JpaRepository<FormItem, Long> {

}
