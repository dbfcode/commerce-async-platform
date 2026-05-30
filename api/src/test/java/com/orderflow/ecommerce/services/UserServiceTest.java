package com.orderflow.ecommerce.services;

import com.orderflow.ecommerce.auxiliar.Factory;
import com.orderflow.ecommerce.dtos.UserDto;
import com.orderflow.ecommerce.entities.User;
import com.orderflow.ecommerce.exceptions.DuplicateResourceException;
import com.orderflow.ecommerce.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    private Long existingId, nonExistingId, dependentId;
    private String existingUserEmail, nonExistingUserEmail, existingTaxId, nonExistingTaxId;
    private User user;
    private UserDto userDto;
    private PageImpl<User> page;

    @BeforeEach
    void setUp() throws Exception {
        user = Factory.createUser();
        existingId = user.getId();
        nonExistingId = 2L;
        dependentId = 3L;
        existingUserEmail = user.getEmail();
        nonExistingUserEmail = "user@gmail.com";
        existingTaxId = user.getTaxId();
        nonExistingTaxId = "99999999999";
        userDto = Factory.createUserDto();
        page = new PageImpl<>(List.of(user));

        Mockito.when(repository.findByEmailIgnoreCase(existingUserEmail)).thenReturn(Optional.of(user));

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(user));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        Mockito.when(repository.findById(dependentId)).thenReturn(Optional.of(user));
        Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(user);

        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        Mockito.when(repository.existsByEmail(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(repository.existsByTaxId(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(repository.existsByEmailAndIdNot(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(false);
        Mockito.when(repository.existsByTaxIdAndIdNot(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(false);


        Mockito.when(repository.getReferenceById(existingId)).thenReturn(user);

        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(NoSuchElementException.class);
    }

    //#region find

    @Test
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 12);
        Page<UserDto> result = service.findAllPaged(pageable);
        Assertions.assertNotNull(result);
        Mockito.verify(repository, times(1)).findAll(pageable);
    }

    @Test
    public void findByIdShouldReturnUserDtoWhenIdExists() {
        UserDto result = service.findById(existingId);
        Assertions.assertNotNull(result);
    }

    @Test
    public void findByIdShouldThrowNoSuchElementExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            service.findById(nonExistingId);
        });
        Mockito.verify(repository).findById(nonExistingId);
    }

    @Test
    public void findByEmailShouldReturnUserDtoWhenValidEmail() {
        UserDto result = service.findByEmail(existingUserEmail);
        Assertions.assertNotNull(result);
    }

    @Test
    public void findByEmailShouldThrowNoSuchElementExceptionWhenUserNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            service.findByEmail(nonExistingUserEmail);
        });
        Mockito.verify(repository).findByEmailIgnoreCase(nonExistingUserEmail);
    }

    //#endregion

    //#region insert
    @Test
    void insertShouldSaveWhenNoDuplicates() {
        UserDto result = service.insert(userDto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(repository).save(captor.capture());
        User saved = captor.getValue();

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Bob", result.name());
        Assertions.assertEquals("bob@gmail.com", result.email());
        Mockito.verify(repository, times(1)).save(ArgumentMatchers.any(User.class));
    }

    @Test
    void insertShouldThrowDuplicateResourceExceptionWhenEmailDuplicate() {

        Mockito.when(repository.existsByEmail(existingUserEmail)).thenReturn(true);

        Assertions.assertThrows(DuplicateResourceException.class, () -> service.insert(userDto));

        Mockito.verify(repository, times(0)).save(ArgumentMatchers.any());
    }

    @Test
    void insertShouldThrowDuplicateResourceExceptionWhenTaxIdDuplicate() {
        Mockito.when(repository.existsByTaxId(existingTaxId)).thenReturn(true);

        Assertions.assertThrows(DuplicateResourceException.class, () -> service.insert(userDto));

        Mockito.verify(repository, times(0)).save(ArgumentMatchers.any());
    }

    //#endregion

    //#region update
    @Test
    void updateShouldReturnUserDTOWhenIdExistsAndNoDuplicates() {
        UserDto result = service.update(existingId, userDto);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(repository).save(captor.capture());
        User saved = captor.getValue();
        Assertions.assertEquals("Bob", result.name());
        Assertions.assertEquals("bob@gmail.com", result.email());
        Mockito.verify(repository, times(1)).save(ArgumentMatchers.any());
    }

    @Test
    void updateShouldThrowDuplicateResourceExceptionWhenEmailUsedByAnother() {

        UserDto dto = new UserDto(existingId, "Bob New", "someoneelse@example.com", "pw",
                "11111111111", null, null, null, null, null,
                null, null, null, null, null, null, null, null);

        Mockito.when(repository.existsByEmailAndIdNot("someoneelse@example.com", existingId)).thenReturn(true);

        Assertions.assertThrows(DuplicateResourceException.class, () -> service.update(existingId, dto));

        Mockito.verify(repository, times(0)).save(ArgumentMatchers.any());
    }

    @Test
    void updateShouldThrowDuplicateResourceExceptionWhenTaxIdEUsedByAnother() {

        UserDto dto = new UserDto(existingId, "Bob New", "someoneelse@example.com", "pw",
                "11111111111", null, null, null, null, null,
                null, null, null, null, null, null, null, null);

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(user);
        Mockito.when(repository.existsByEmailAndIdNot("bob@gmail.com", existingId)).thenReturn(false);
        Mockito.when(repository.existsByTaxIdAndIdNot("11111111111", existingId)).thenReturn(true);

        Assertions.assertThrows(DuplicateResourceException.class, () -> service.update(existingId, dto));

        Mockito.verify(repository, times(0)).save(ArgumentMatchers.any());
    }

    @Test
    public void updateShouldThrowNoSuchElementExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            service.update(nonExistingId, userDto);
        });
    }
//#endregion

    //#region delete
    @Test
    public void deleteShouldThrowDataIntegrityViolationExceptionWhenDependentId() {

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            service.delete(dependentId, true);
        });
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExistAndVerifyIsTrue() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            service.delete(nonExistingId, true);
        });
    }

    @Test
    public void deleteShouldDoNothingWhenIdDoesNotExistAndVerifyIsFalse() {
        Mockito.doNothing().when(repository).deleteById(nonExistingId);
        Assertions.assertDoesNotThrow(() -> {
            service.delete(nonExistingId, false);
        });
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId, true);
        });
        Mockito.verify(repository, times(1)).deleteById(existingId);
    }
    //#endregion

}
