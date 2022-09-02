package br.com.cursoudemy.productapi.modules.supplier.dto;

import br.com.cursoudemy.productapi.modules.category.model.Category;
import br.com.cursoudemy.productapi.modules.supplier.model.Supplier;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class SupplierResponse {
    private Integer id;
    private String name;

    public static SupplierResponse of(Supplier entity){
        var response = new SupplierResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
    
}
