package com.spaceclub.notification.mail.repository;

import com.spaceclub.notification.mail.domain.MailTracker;
import com.spaceclub.notification.mail.domain.Template;
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
class TemplateRepositoryTest {

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

        MailTracker mailTracker1 = MailTracker.builder()
                .addresses("abcd@naver.com")
                .title("title 1")
                .template("welcome")
                .sentAt(LocalDateTime.now())
                .isSent(true)
                .build();
        MailTracker mailTracker2 = MailTracker.builder()
                .addresses("zxcv@naver.com")
                .title("title 2")
                .template("event-status-change")
                .sentAt(LocalDateTime.now())
                .isSent(true)
                .build();
        MailTracker mailTracker3 = MailTracker.builder()
                .addresses("asdf@naver.com")
                .title("title 1")
                .template("welcome")
                .sentAt(LocalDateTime.now())
                .isSent(false)
                .build();

        em.persist(mailTracker1);
        em.persist(mailTracker2);
        em.persist(mailTracker3);

        em.persist(template1);
        em.persist(template2);

        em.flush();
        em.clear();
    }

    @ParameterizedTest
    @CsvSource({"event-status-change, template 2", "welcome, template 1"})
    void findByTemplateName(String templateName, String expectedTemplate) {
        // when
        String singleResult = em.createQuery("select distinct t.template from Template t " +
                        "join fetch MailTracker m on m.templateId = t.id " +
                        "where m.templateName = :templateName", String.class)
                .setParameter("templateName", templateName)
                .getSingleResult();

        // then
        assertThat(singleResult).isEqualTo(expectedTemplate);
    }
}
