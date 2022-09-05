package br.com.cursoudemy.productapi.modules.supplier.service;

import br.com.cursoudemy.productapi.config.exceptions.SuccessResponse;
import br.com.cursoudemy.productapi.config.exceptions.ValidationException;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryRequest;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryResponse;
import br.com.cursoudemy.productapi.modules.category.model.Category;
import br.com.cursoudemy.productapi.modules.category.repository.CategoryRepository;
import br.com.cursoudemy.productapi.modules.produto.dto.ProductResponse;
import br.com.cursoudemy.productapi.modules.produto.service.ProductService;
import br.com.cursoudemy.productapi.modules.supplier.dto.SupplierRequest;
import br.com.cursoudemy.productapi.modules.supplier.dto.SupplierResponse;
import br.com.cursoudemy.productapi.modules.supplier.model.Supplier;
import br.com.cursoudemy.productapi.modules.supplier.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductService productService;

    public SupplierResponse save(SupplierRequest request){
        validateNameInFormed(request);
        var supplier = supplierRepository.save(Supplier.of(request));
        return SupplierResponse.of(supplier);
    }

    public SupplierResponse update(SupplierRequest request, Integer id){
        validateNameInFormed(request);
        var supplier = Supplier.of(request);
        supplier.setId(id);
        supplierRepository.save(supplier);
        return SupplierResponse.of(supplier);
    }

    public Supplier findById(Integer id){
        validateInformedId(id);
         return supplierRepository.findById(id)
                 .orElseThrow(() -> new ValidationException("there's no supplier for the give ID."));
    }

    private void validateNameInFormed(SupplierRequest request){
        if(isEmpty(request.getName())){
            throw new ValidationException("The supplier name was not informed");
        }
    }

    // novos

    public List<SupplierResponse> findAll(){
        var list = supplierRepository.findAll();
        return list.stream().map(SupplierResponse::of).collect(Collectors.toList());
    }

    public List<SupplierResponse> findByName(String name){
        if(isEmpty(name)){
            throw new ValidationException("The product name be informed");
        }
        return supplierRepository
                .findAll()
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public SupplierResponse findByResponse(Integer id){
        return SupplierResponse.of(findById(id));
    }

    public SuccessResponse delete(Integer id){
        validateInformedId(id);
        if(productService.existsBySupplierId(id)){
            throw new ValidationException("You cannot delete this supplier because it's already defined by a product.");
        }
        supplierRepository.deleteById(id);
        return SuccessResponse.create("the supplier was deleted");
    }

    private void validateInformedId(Integer id){
        if(isEmpty(id)){
            throw new ValidationException("The supplier ID must be informed.");
        }
    }

}
