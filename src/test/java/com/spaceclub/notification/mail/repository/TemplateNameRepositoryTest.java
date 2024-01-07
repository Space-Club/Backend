package com.spaceclub.notification.mail.repository;

import com.spaceclub.notification.mail.domain.MailHistory;
import com.spaceclub.notification.mail.domain.Template;
import com.spaceclub.notification.mail.domain.TemplateName;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class TemplateNameRepositoryTest {

    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private MailTrackerRepository mailTrackerRepository;
    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void resetAutoIncrementId() {
        entityManager.createNativeQuery("ALTER TABLE TEMPLATE ALTER COLUMN template_id RESTART WITH 1")
                .executeUpdate();
    }

    @BeforeEach
    void setUp() {
        Template template1 = Template.builder()
                .template("template 1")
                .build();
        Template template2 = Template.builder()
                .template("template 2")
                .build();
        templateRepository.saveAll(List.of(template1, template2));

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

        mailTrackerRepository.saveAll(List.of(mailHistory1, mailHistory2, mailHistory3));
    }

    @ParameterizedTest
    @CsvSource({"EVENT_STATUS_CHANGED,template 2", "WELCOME,template 1"})
    void findByTemplateName(TemplateName templateName, String expectedTemplate) {
        // when
        String actualTemplate = templateRepository.findTemplateByTemplateName(templateName);

        // then
        assertThat(actualTemplate).isEqualTo(expectedTemplate);
    }

}
