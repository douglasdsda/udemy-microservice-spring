package br.com.cursoudemy.productapi.modules.supplier.controller;

import br.com.cursoudemy.productapi.config.exceptions.SuccessResponse;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryRequest;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryResponse;
import br.com.cursoudemy.productapi.modules.category.service.CategoryService;
import br.com.cursoudemy.productapi.modules.produto.dto.ProductResponse;
import br.com.cursoudemy.productapi.modules.supplier.dto.SupplierRequest;
import br.com.cursoudemy.productapi.modules.supplier.dto.SupplierResponse;
import br.com.cursoudemy.productapi.modules.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping
    public SupplierResponse save(@RequestBody SupplierRequest request){
        return supplierService.save(request);
    }

    @PutMapping("{id}")
    public SupplierResponse update(@RequestBody SupplierRequest request, @PathVariable(name = "id") Integer id ){
        return supplierService.update(request, id);
    }

    @GetMapping("{id}")
    public SupplierResponse findById(@PathVariable(name = "id") Integer id){
        return supplierService.findByResponse(id);
    }

    @GetMapping("name/{name}")
    public List<SupplierResponse> findByName(@PathVariable(name = "name") String name){
        return supplierService.findByName(name);
    }

    @GetMapping()
    public List<SupplierResponse> findAll(){
        return supplierService.findAll();
    }

    @DeleteMapping("{id}")
    public SuccessResponse delete(@PathVariable(name = "id") Integer id){
        return supplierService.delete(id);
    }
}
