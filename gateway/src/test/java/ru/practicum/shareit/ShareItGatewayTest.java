package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest(classes = ShareItGateway.class)
class ShareItGatewayTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
    }

    @Test
    void mainBeanShouldExistInContext() {
        assertThat(context.containsBean("shareItGateway")).isTrue();
    }

    @Test
    void main_shouldRunApplication() {
        try (var mockedStatic = mockStatic(SpringApplication.class)) {
            ShareItGateway.main(new String[]{});
            mockedStatic.verify(() -> SpringApplication.run(ShareItGateway.class, new String[]{}));
        }
    }

}