package br.com.cursoudemy.productapi.modules.category.service;

import br.com.cursoudemy.productapi.config.exceptions.SuccessResponse;
import br.com.cursoudemy.productapi.config.exceptions.ValidationException;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryRequest;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryResponse;
import br.com.cursoudemy.productapi.modules.category.model.Category;
import br.com.cursoudemy.productapi.modules.category.repository.CategoryRepository;
import br.com.cursoudemy.productapi.modules.produto.service.ProductService;
import br.com.cursoudemy.productapi.modules.supplier.dto.SupplierRequest;
import br.com.cursoudemy.productapi.modules.supplier.dto.SupplierResponse;
import br.com.cursoudemy.productapi.modules.supplier.model.Supplier;
import br.com.cursoudemy.productapi.modules.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;


    @Autowired
    private ProductService productService;


    public CategoryResponse save(CategoryRequest request){
        validateCategoryNameInFormed(request);
        var category = categoryRepository.save(Category.of(request));
        return CategoryResponse.of(category);
    }
    public CategoryResponse update(CategoryRequest request, Integer id){
        validateCategoryNameInFormed(request);
        var category = Category.of(request);
        category.setId(id);
        categoryRepository.save(category);
        return CategoryResponse.of(category);
    }

    public List<CategoryResponse> findAll(){
        var list = categoryRepository.findAll();
        return list.stream().map(CategoryResponse::of).collect(Collectors.toList());
    }

    public List<CategoryResponse> findByDescription(String description){
        if(isEmpty(description)){
            throw new ValidationException("The category description be informed");
        }
        return categoryRepository
                .findAll()
                .stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }

    public Category findById(Integer id){
        validateInformedId(id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ValidationException("there's no category for the give ID."));
    }



    public CategoryResponse findByResponse(Integer id){

        return CategoryResponse.of(findById(id));
    }

    private void validateCategoryNameInFormed(CategoryRequest request){
        if(isEmpty(request.getDescription())){
            throw new ValidationException("The category description was not informed");
        }
    }

    public SuccessResponse delete(Integer id){
        validateInformedId(id);
        if(productService.existsByCategoryId(id)){
            throw new ValidationException("You cannot delete this category because it's already defined by a product.");
        }
        categoryRepository.deleteById(id);
        return SuccessResponse.create("the category was deleted");
    }

    private void validateInformedId(Integer id){
        if(isEmpty(id)){
            throw new ValidationException("The category ID must be informed.");
        }
    }

}
