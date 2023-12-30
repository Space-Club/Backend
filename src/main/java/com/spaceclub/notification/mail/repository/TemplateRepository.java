package com.spaceclub.notification.mail.repository;

import com.spaceclub.notification.mail.domain.Template;
import com.spaceclub.notification.mail.domain.TemplateName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TemplateRepository extends JpaRepository<Template, Long> {

    @Query("select distinct t.template from Template t " +
            "join fetch MailHistory m on m.templateId = t.id " +
            "where m.templateName = :templateName")
    String findTemplateByTemplateName(@Param("templateName") TemplateName templateName);

}
