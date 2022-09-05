package br.com.cursoudemy.productapi.modules.category.controller;

import br.com.cursoudemy.productapi.config.exceptions.SuccessResponse;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryRequest;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryResponse;
import br.com.cursoudemy.productapi.modules.category.service.CategoryService;
import br.com.cursoudemy.productapi.modules.produto.dto.ProductRequest;
import br.com.cursoudemy.productapi.modules.produto.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public CategoryResponse save(@RequestBody CategoryRequest request){
        return categoryService.save(request);
    }

    @PutMapping("{id}")
    public CategoryResponse update(@RequestBody CategoryRequest request, @PathVariable(name = "id") Integer id ){
        return categoryService.update(request, id);
    }
    @GetMapping("{id}")
    public CategoryResponse findById(@PathVariable(name = "id") Integer id){
        return categoryService.findByResponse(id);
    }

    @GetMapping("description/{description}")
    public List<CategoryResponse> findByDescription(@PathVariable(name = "description") String description){

        return categoryService.findByDescription(description);
    }

    @GetMapping()
    public List<CategoryResponse> findAll(){

        return categoryService.findAll();
    }

    @DeleteMapping("{id}")
    public SuccessResponse delete(@PathVariable(name = "id") Integer id){
        return categoryService.delete(id);
    }
}
