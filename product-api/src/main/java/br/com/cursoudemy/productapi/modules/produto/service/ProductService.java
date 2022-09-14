package br.com.cursoudemy.productapi.modules.produto.service;

import br.com.cursoudemy.productapi.config.exceptions.SuccessResponse;
import br.com.cursoudemy.productapi.config.exceptions.ValidationException;
import br.com.cursoudemy.productapi.modules.category.service.CategoryService;
import br.com.cursoudemy.productapi.modules.produto.dto.*;
import br.com.cursoudemy.productapi.modules.produto.model.Product;
import br.com.cursoudemy.productapi.modules.produto.repository.ProductRepository;
import br.com.cursoudemy.productapi.modules.sales.client.SalesClient;
import br.com.cursoudemy.productapi.modules.sales.dto.SalesConfirmationDTO;
import br.com.cursoudemy.productapi.modules.sales.enums.SalesStatus;
import br.com.cursoudemy.productapi.modules.sales.rabbitmq.SalesConfirmationSender;
import br.com.cursoudemy.productapi.modules.supplier.service.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;
@Slf4j
@Service
public class ProductService {
    private static final Integer ZERO = 0;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private SalesConfirmationSender salesConfirmationSender;

    @Autowired
    private SalesClient salesClient;

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

    public void updateProductStock(ProductStockDTO product){
        try {
            validateStockUpdateData(product);
            updateStock(product);
        } catch (Exception ex){
               log.error("Error while trying to update stock for message with error: {}", ex.getMessage(), ex);
               var rejectedMessage = new SalesConfirmationDTO(product.getSalesId(), SalesStatus.REJECTED);
               salesConfirmationSender.sendSalesConfirmationMessage(rejectedMessage);
        }
    }
    @Transactional
    private void updateStock(ProductStockDTO product){
         var productsForUpdate = new ArrayList<Product>();
            product.getProducts()
                    .forEach(salesProduct -> {
                        var existingProduct = findById(salesProduct.getProductId());
                        validateQuantityInStock(salesProduct, existingProduct);
                        existingProduct.updateStock(salesProduct.getQuantity());

                        productsForUpdate.add(existingProduct);


                    });
            if(!isEmpty(productsForUpdate)){
                productRepository.saveAll(productsForUpdate);
                var approvedMessage = new SalesConfirmationDTO(product.getSalesId(), SalesStatus.APPROVED);
                salesConfirmationSender.sendSalesConfirmationMessage(approvedMessage);
            }


    }
@Transactional
    private void validateStockUpdateData(ProductStockDTO product){
        if(isEmpty(product) || isEmpty(product.getSalesId())){
            throw new ValidationException("The product data or sales ID must be null.");
        }

        if(isEmpty(product.getProducts())){
            throw new ValidationException("The sales ' products must be informed.");
        }

        product
                .getProducts()
                .forEach(salesProduct -> {
                    if(isEmpty(salesProduct.getQuantity())
                    || isEmpty(salesProduct.getProductId())){
                        throw new ValidationException("The productID and the quantity must be informed.");
                    }
                });
    }

    private void validateQuantityInStock(ProductQuantityDTO salesProduct, Product existingProduct){
        if(salesProduct.getQuantity() > existingProduct.getQuantityAvailable()){
            throw new ValidationException(String.format("The product %s is out of stock.", existingProduct.getId()));
        }
    }

    @PostMapping("check-stock")
    public SuccessResponse checkProductsStock(ProductCheckStockRequest request){
       if(isEmpty(request)){
           throw new ValidationException("The request data and products must be informed.");
       }
       request
               .getProducts()
               .forEach(this::validateStock );
       return SuccessResponse.create("The stock is ok!");
    }

    private void validateStock(ProductQuantityDTO productQuantity){
        if(isEmpty(productQuantity) || isEmpty(productQuantity.getQuantity())){
            throw new ValidationException("Product ID and quantity must be informed.");
        }
        var product = findById(productQuantity.getProductId());
        if(productQuantity.getQuantity() > product.getQuantityAvailable()){
            throw new ValidationException(String.format("THe product %s is out of stock.", product.getId()));
        }
    }

    public ProductSalesResponse findProductSales(Integer id){
        var product = findById(id);
        try {
            var sales = salesClient
                    .findSalesByProductId(product.getId())
                    .orElseThrow(() -> new ValidationException("The sales was not found by this product."));
            return ProductSalesResponse.of(product, sales.getSalesIds());
        } catch(Exception ex){
            throw new ValidationException("There was an error trying to get the product's sales.");
        }
    }
}
