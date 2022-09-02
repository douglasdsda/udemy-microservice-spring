package br.com.cursoudemy.productapi.modules.produto.dto;

import br.com.cursoudemy.productapi.modules.category.model.Category;
import br.com.cursoudemy.productapi.modules.supplier.model.Supplier;
import lombok.Data;

import javax.persistence.*;

@Data
public class ProductRequest {
    private String name;

    private Integer supplierId;

    private Integer categoryId;

    private Integer quantityAvailable;

}
