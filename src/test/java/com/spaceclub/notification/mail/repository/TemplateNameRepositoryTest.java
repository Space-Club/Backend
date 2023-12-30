package com.spaceclub.notification.mail.repository;

import com.spaceclub.notification.mail.domain.MailHistory;
import com.spaceclub.notification.mail.domain.Template;
import com.spaceclub.notification.mail.domain.TemplateName;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@DataJpaTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class TemplateNameRepositoryTest {

    @Autowired
    EntityManager em;

    @BeforeEach
    void setUp() {
        Template template1 = Template.builder()
                .template("template 1")
                .build();
        Template template2 = Template.builder()
                .template("template 2")
                .build();

        MailHistory mailHistory1 = MailHistory.builder()
                .addresses("abcd@naver.com")
                .title("title 1")
                .templateName(TemplateName.WELCOME)
                .sentAt(LocalDateTime.now())
                .isSent(true)
                .build();
        MailHistory mailHistory2 = MailHistory.builder()
                .addresses("zxcv@naver.com")
                .title("title 2")
                .templateName(TemplateName.EVENT_STATUS_CHANGED)
                .sentAt(LocalDateTime.now())
                .isSent(true)
                .build();
        MailHistory mailHistory3 = MailHistory.builder()
                .addresses("asdf@naver.com")
                .title("title 1")
                .templateName(TemplateName.WELCOME)
                .sentAt(LocalDateTime.now())
                .isSent(false)
                .build();

        em.persist(mailHistory1);
        em.persist(mailHistory2);
        em.persist(mailHistory3);

        em.persist(template1);
        em.persist(template2);

        em.flush();
        em.clear();
    }

    @ParameterizedTest
    @CsvSource({"EVENT_STATUS_CHANGED,template 2", "WELCOME,template 1"})
    void findByTemplateName(TemplateName templateName, String expectedTemplate) {
        // when
        String singleResult = em.createQuery("select distinct t.template from Template t " +
                        "join fetch MailHistory m on m.templateId = t.id " +
                        "where m.templateName = :templateName", String.class)
                .setParameter("templateName", templateName)
                .getSingleResult();

        // then
        assertThat(singleResult).isEqualTo(expectedTemplate);
    }
}
