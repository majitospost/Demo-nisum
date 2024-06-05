package com.example.demo.presentation.presenter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@Schema()
public class PhonePresenter {
    private Long id;
    @Schema(example = "+593958717611")
    private String number;
    @Schema(example = "032")
    private String cityCode;
    @Schema(example = "+593")
    private String countryCode;
}
