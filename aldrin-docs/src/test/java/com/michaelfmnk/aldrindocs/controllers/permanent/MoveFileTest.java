package com.michaelfmnk.aldrindocs.controllers.permanent;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.michaelfmnk.aldrindocs.BaseTest;
import com.michaelfmnk.aldrindocs.dtos.MoveDocumentDto;
import io.restassured.http.ContentType;
import junit.framework.TestCase;
import org.assertj.db.type.Request;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.shaded.org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.db.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MoveFileTest extends BaseTest {

    private File tmpPdf;

    private final String PDF_ID = "00000000-0000-0000-0000-000000000001";

    @Before
    public void before() throws IOException {
        tmpPdf = new File(temporaryStorage, PDF_ID);
        Files.copy(new File(testDir, "empty-pdf.pdf").toPath(), tmpPdf.toPath());
        TestCase.assertTrue(tmpPdf.exists());
    }


    @Test
    public void shouldMoveFile() throws JsonProcessingException {
        MoveDocumentDto dto = MoveDocumentDto.builder()
                .fileId(UUID.fromString(PDF_ID))
                .dataId(1)
                .build();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(RIGHT_HEADER)
                .body(objectMapper.writeValueAsBytes(Arrays.asList(dto)))
                .when()
                .put("/aldrin-docs-api/permanent")
                .then().extract().response().prettyPeek()
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("", hasSize(1))
                .body("[0].file_id", equalTo("00000000-0000-0000-0000-000000000001"))
                .body("[0].data_id", equalTo(1))
                .body("[0].document_name", equalTo("empty-pdf.pdf"))
                .body("[0].mime", equalTo("application/pdf"));

        assertFalse(tmpPdf.exists());
        assertTrue(new File(permanentStorage, PDF_ID).exists());

        assertThat(new Request(dataSource, "SELECT count(1) " +
                "FROM documents WHERE file_id='00000000-0000-0000-0000-000000000001' " +
                "AND data_id=1")).row(0).value().isEqualTo(1);
    }

    @Test
    public void shouldFailMoveFileOnNotFound() throws JsonProcessingException {
        MoveDocumentDto dto = MoveDocumentDto.builder()
                .fileId(UUID.fromString(UUID.randomUUID().toString()))
                .dataId(1)
                .build();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(RIGHT_HEADER)
                .body(objectMapper.writeValueAsBytes(Arrays.asList(dto)))
                .when()
                .put("/aldrin-docs-api/permanent")
                .then().extract().response().prettyPeek()
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("title", Matchers.equalTo("NOT_FOUND"))
                .body("status", Matchers.equalTo(404))
                .body("detail", Matchers.equalTo("Document not found"))
                .body("timestamp", notNullValue())
                .body("dev_message", Matchers.equalTo("javax.persistence.EntityNotFoundException"));

        assertTrue(tmpPdf.exists());
        assertFalse(new File(permanentStorage, PDF_ID).exists());

    }
}
