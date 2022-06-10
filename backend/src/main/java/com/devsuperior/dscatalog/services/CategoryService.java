package com.devsuperior.dscatalog.services;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

  public String returnMessageCategoryNotFoundException(Long id) {
    return MessageFormat.format("Category with id {0} not found", id);
  }

  @Autowired
  private CategoryRepository repository;

  @Transactional(readOnly = true)
  public List<CategoryDTO> findAll() {
    List<Category> list = repository.findAll();
    return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public CategoryDTO findById(Long id) {
    Optional<Category> obj = repository.findById(id);
    Category entity = obj.orElseThrow(
        () -> new ResourceNotFoundException(returnMessageCategoryNotFoundException(id)));
    return new CategoryDTO(entity);
  }

  @Transactional
  public CategoryDTO insert(CategoryDTO dto) {
    Category entity = new Category(dto);
    entity = repository.save(entity);
    return new CategoryDTO(entity);
  }

  @Transactional
  public CategoryDTO update(Long id, CategoryDTO dto) {
    try {
      Category entity = repository.getById(id);
      entity.setName(dto.getName());
      entity = repository.save(entity);
      return new CategoryDTO(entity);
    } catch (EntityNotFoundException e) {
      throw new ResourceNotFoundException(returnMessageCategoryNotFoundException(id));
    }
  }

  public void delete(Long id) {
    try {
      repository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new ResourceNotFoundException(returnMessageCategoryNotFoundException(id));
    } catch (DataIntegrityViolationException e) {
      throw new DatabaseException("Integrity violation");
    }
  }
}
