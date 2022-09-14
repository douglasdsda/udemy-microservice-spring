package br.com.cursoudemy.productapi.modules.produto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class ProductStockDTO {
    private String salesId;
    private List<ProductQuantityDTO> products;
}
