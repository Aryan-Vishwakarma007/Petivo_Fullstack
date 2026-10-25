package com.petivo.api.dto;

import com.petivo.api.entity.Service;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ServiceDto {
    private Long id;

    @NotBlank(message = "Service name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    private Boolean active;

    public ServiceDto() {
    }

    public ServiceDto(Long id, String name, String description, BigDecimal price, Boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.active = active != null ? active : true;
    }

    public static ServiceDto fromEntity(Service service) {
        if (service == null) return null;
        return new ServiceDto(
            service.getId(),
            service.getName(),
            service.getDescription(),
            service.getPrice(),
            service.getActive()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
