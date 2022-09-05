package br.com.cursoudemy.productapi.modules.produto.controller;

import br.com.cursoudemy.productapi.config.exceptions.SuccessResponse;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryRequest;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryResponse;
import br.com.cursoudemy.productapi.modules.category.service.CategoryService;
import br.com.cursoudemy.productapi.modules.produto.dto.ProductRequest;
import br.com.cursoudemy.productapi.modules.produto.dto.ProductResponse;
import br.com.cursoudemy.productapi.modules.produto.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ProductResponse save(@RequestBody ProductRequest request){
        return productService.save(request);
    }

    @PutMapping("{id}")
    public ProductResponse update(@RequestBody ProductRequest request, @PathVariable(name = "id") Integer id ){
        return productService.update(request, id);
    }

    @GetMapping("{id}")
    public ProductResponse findById(@PathVariable(name = "id") Integer id){
        return productService.findByResponse(id);
    }
    @GetMapping("category/{categoryId}")
    public List<ProductResponse> findCategoryId(@PathVariable(name = "categoryId") Integer categoryId){
        return productService.findByCategoryId(categoryId);
    }
    @GetMapping("supplier/{supplierId}")
    public List<ProductResponse> findBySupplierId(@PathVariable(name = "supplierId") Integer supplierId){
        return productService.findBySupplierId(supplierId);
    }

    @GetMapping("name/{name}")
    public List<ProductResponse> findByName(@PathVariable(name = "name") String name){
        return productService.findByName(name);
    }
    @GetMapping()
    public List<ProductResponse> findAll(){
        return productService.findAll();
    }

    @DeleteMapping("{id}")
    public SuccessResponse delete(@PathVariable(name = "id") Integer id){
        return productService.delete(id);
    }
}
