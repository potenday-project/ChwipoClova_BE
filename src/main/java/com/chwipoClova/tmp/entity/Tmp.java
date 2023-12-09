package com.chwipoClova.tmp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Tmp")
@Table(name = "Tmp")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties()
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Tmp VO")
public class Tmp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "fld01", example = "1", name = "fld01")
    private Long fld01;

    @Column(name = "fld02")
    @Schema(description = "fld02", example = "1", name = "fld02")
    private String fld02;

    @Column(name = "fld03")
    @Schema(description = "fld03", example = "1", name = "fld03")
    private String fld03;

    @Column(name = "fld04")
    @Schema(description = "fld04", example = "1", name = "fld04")
    private String fld04;

    @Column(name = "fld05")
    @Schema(description = "fld05", example = "1", name = "fld05")
    private String fld05;
}
