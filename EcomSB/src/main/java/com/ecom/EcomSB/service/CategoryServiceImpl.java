package com.ecom.EcomSB.service;

import com.ecom.EcomSB.exception.APIException;
import com.ecom.EcomSB.exception.ResourceNotFoundException;
import com.ecom.EcomSB.model.Category;
import com.ecom.EcomSB.payload.CategoryDTO;
import com.ecom.EcomSB.payload.CategoryResponse;
import com.ecom.EcomSB.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
//    private List<Category> categories = new ArrayList<>();
//    private Long nextId = 1L;

    // todo : Here we use ArrayList but now we can use H2DB JPA and all these things

    // now we need the instance of CategoryRepository so we add here annotation
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) { // yaha per sortOrder to hai ascending and descending ||| sortBy hai kiske hisab se sort karna hai id , price, quantity(but hamaare pass abhi id hi hai numerical values me to hum usi se karenge)
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();


        Pageable pageDetail = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetail);
        List<Category> categories = categoryPage.getContent();

        //todo : If there is no Category Present we wanna see a Message
        if(categories.isEmpty()){
            throw new APIException("!!! No Category Created Till Now !!!");
        }

        // DTO Implementation
        List<CategoryDTO> categoriesDTO = categories.stream()
                .map(cc -> modelMapper.map(cc, CategoryDTO.class)).toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoriesDTO);
        //todo : CategoryResponse file me 3 annotation laga rakhe hai : @Data, @AllArgsConstructor, @NoArgsConstructor to ye getter setters vahi se aa rhe hai
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        //todo : Before saving the category check it is duplicate or NOT
        Category categoryFromDB = categoryRepository.findByCategoryName(category.getCategoryName()); // ye jo method hai findByCategoryName iski koi implementation nahi hai just define hai ctrl+click and you will see there is no definition of method just declare it and JPA will provide you ||| I don't know how this thing is work
        if(categoryFromDB != null){
            throw new APIException("Category with the Name '"+category.getCategoryName() + "' Already Exist" );
        }
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

//    @Override
//    public String deleteCategory(Long categoryId) {
//        List<Category> categories = categoryRepository.findAll();
//
//        Category deletethiscategory = categories.stream()
//                .filter(c -> c.getCategoryId().equals(categoryId))
//                .findFirst()
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found"));
//
//        categoryRepository.delete(deletethiscategory); // this is just a name "deletethiscategory"
//        return "Category with categoryId: " + categoryId + " deleted successfully !!";
//    }

    //todo : Easy Method for Deletion
    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category ", "CategoryID", categoryId));

        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryDTO.class);
    }


    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {

        Category savedCategory = categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId)); // Custom Exception  || See this in ResourceNotFoundException.java file

        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }


}
