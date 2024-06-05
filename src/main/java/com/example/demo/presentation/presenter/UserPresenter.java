package com.example.demo.presentation.presenter;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@Schema()
public class UserPresenter {
    private UUID id;

    @NotNull
    @Size(min = 2, max = 50)
    @Schema(example = "Juan Rodriguez")
    private String name;

    @NotNull
    @Size(min = 2, max = 50)
    @Schema(example = "juan@rodriguez.org")
    private String email;

    @NotNull
    @Schema(example = "hunter2")
    private String password;

    private Set<PhonePresenter> phones;

    private boolean isActive;
}
