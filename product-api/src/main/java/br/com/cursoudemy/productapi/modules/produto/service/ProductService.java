package br.com.cursoudemy.productapi.modules.produto.service;

import br.com.cursoudemy.productapi.config.exceptions.SuccessResponse;
import br.com.cursoudemy.productapi.config.exceptions.ValidationException;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryRequest;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryResponse;
import br.com.cursoudemy.productapi.modules.category.model.Category;
import br.com.cursoudemy.productapi.modules.category.repository.CategoryRepository;
import br.com.cursoudemy.productapi.modules.category.service.CategoryService;
import br.com.cursoudemy.productapi.modules.produto.dto.ProductRequest;
import br.com.cursoudemy.productapi.modules.produto.dto.ProductResponse;
import br.com.cursoudemy.productapi.modules.produto.model.Product;
import br.com.cursoudemy.productapi.modules.produto.repository.ProductRepository;
import br.com.cursoudemy.productapi.modules.supplier.model.Supplier;
import br.com.cursoudemy.productapi.modules.supplier.repository.SupplierRepository;
import br.com.cursoudemy.productapi.modules.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class ProductService {
    private static final Integer ZERO = 0;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SupplierService supplierService;

    public ProductResponse save(ProductRequest request){
        validateProductDataInFormed(request);
        var category = categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());

        var product = productRepository.save(Product.of(request, supplier, category));
        return ProductResponse.of(product);
    }

    public ProductResponse update(ProductRequest request, Integer id){
        validateProductDataInFormed(request);
        var category = categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());
        var product = Product.of(request, supplier, category);
        product.setId(id);
        productRepository.save(product);
        return ProductResponse.of(product);
    }

    public Product findById(Integer id){
        return productRepository.findById(id)
                .orElseThrow(() -> new ValidationException("there's no product for the give ID."));
    }

    private void validateProductDataInFormed(ProductRequest request){
        if(isEmpty(request.getName())){
            throw new ValidationException("The product name was not informed");
        }

        if(isEmpty(request.getCategoryId())){
            throw new ValidationException("The product category was not informed");
        }

        if(isEmpty(request.getSupplierId())){
            throw new ValidationException("The product supplier was not informed");
        }

        if(isEmpty(request.getQuantityAvailable())){
            throw new ValidationException("The product quantity was not informed");
        }

        if(request.getQuantityAvailable() <= ZERO){
            throw new ValidationException("The product quantity <= 0 was not informed");
        }
    }

     // novos


    public List<ProductResponse> findAll(){
        var list = productRepository.findAll();
        return list.stream().map(ProductResponse::of).collect(Collectors.toList());
    }

    public List<ProductResponse> findByName(String name){
        if(isEmpty(name)){
            throw new ValidationException("The product name be informed");
        }
        return productRepository
                .findAll()
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByCategoryId(Integer categoryID){
        validateInformedId(categoryID, "categoryID");
        return productRepository.findByCategoryId(categoryID).stream().map(ProductResponse::of).collect(Collectors.toList());
    }

    public List<ProductResponse> findBySupplierId(Integer supplierID){
        validateInformedId(supplierID, "supplierID");
        return productRepository.findBySupplierId(supplierID).stream().map(ProductResponse::of).collect(Collectors.toList());
    }

    public ProductResponse findByResponse(Integer id){
        return ProductResponse.of(findById(id));
    }

    public Boolean existsByCategoryId(Integer categoryId){
        return productRepository.existsByCategoryId(categoryId);
    }

    public Boolean existsBySupplierId(Integer supplierId){
        return productRepository.existsBySupplierId(supplierId);
    }

    public SuccessResponse delete(Integer id) {
        validateInformedId(id, "product");
        productRepository.deleteById(id);
        return SuccessResponse.create("the product was deleted");
    }

    private void validateInformedId(Integer id, String type){
        if(isEmpty(id)){
            throw new ValidationException("The "+type+" must be informed.");
        }
    }
}
