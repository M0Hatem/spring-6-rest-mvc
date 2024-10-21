package guru.springframework.spring6restmvc.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by jt, Spring Framework Guru.
 */
@Data
@Builder
public class CustomerDTO {

    private UUID id;
    private Integer version;

    @NotNull
    @NotBlank
    private String name;

    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
}
