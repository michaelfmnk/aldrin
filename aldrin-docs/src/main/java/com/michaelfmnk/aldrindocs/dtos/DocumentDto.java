package com.michaelfmnk.aldrindocs.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto {
    private UUID fileId;
    private Integer dataId;
    private BigDecimal size;
    private String documentName;
    private String mime;
}
