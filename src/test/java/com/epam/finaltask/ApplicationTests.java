package com.epam.finaltask;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(
        connection = EmbeddedDatabaseConnection.H2
)
@TestPropertySource(
        properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
)
class ApplicationTests {

  private final TimeZone originalTimeZone =
          TimeZone.getDefault();

  @AfterEach
  void tearDown() {
    TimeZone.setDefault(originalTimeZone);
  }

  @Test
  void contextLoads() {
  }

  @Test
  void configureTimeZone_shouldSetUtc() {

    TimeZone.setDefault(
            TimeZone.getTimeZone("Europe/Warsaw")
    );

    Application.configureTimeZone();

    assertEquals(
            "UTC",
            TimeZone.getDefault().getID()
    );
  }

  @Test
  void main_shouldConfigureTimeZoneAndStartApplication() {

    try (MockedStatic<SpringApplication> springApplication =
                 mockStatic(SpringApplication.class)) {

      Application.main(new String[0]);

      assertEquals(
              "UTC",
              TimeZone.getDefault().getID()
      );

      springApplication.verify(
              () -> SpringApplication.run(
                      Application.class,
                      new String[0]
              )
      );
    }
  }

  @Test
  void constructor_shouldCreateApplication() {

    Application application =
            new Application();

    assertNotNull(application);
  }
}

