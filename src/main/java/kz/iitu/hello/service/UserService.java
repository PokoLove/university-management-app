package kz.iitu.hello.service;

import kz.iitu.hello.domain.entity.User;
import kz.iitu.hello.domain.enums.UserRole;
import kz.iitu.hello.domain.repository.UsersRepository;
import kz.iitu.hello.exception.EntityNotFoundException;
import kz.iitu.hello.web.converter.UserConverter;
import kz.iitu.hello.web.dto.form.UserFormDto;
import kz.iitu.hello.web.dto.grid.UserGridDto;
import kz.iitu.hello.web.dto.search.UserSearchForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private static final String DEFAULT_SORT = "createdAt";
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "userName", "email", "createdAt", "role");

    private final UsersRepository usersRepository;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserGridDto> search(UserSearchForm form, Pageable pageable) {
        String userNameFilter = form.getUsername() == null ? "" : form.getUsername();
        String emailFilter = form.getEmail() == null ? "" : form.getEmail();
        UserRole roleFilter = form.getRole();

        Sort.Direction direction = form.getSortDirection() == null ? Sort.Direction.ASC : form.getSortDirection();
        String requestedSortBy = form.getSortBy() == null ? DEFAULT_SORT : form.getSortBy();
        String sortBy = ALLOWED_SORT_FIELDS.contains(requestedSortBy) ? requestedSortBy : DEFAULT_SORT;

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, sortBy));

        if(roleFilter == null)
            return usersRepository
                    .findByUserNameContainingIgnoreCaseAndEmailContainingIgnoreCase(userNameFilter, emailFilter, sortedPageable)
                    .map(userConverter::toGridDto);

        return usersRepository
                .findByUserNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRole(userNameFilter, emailFilter, roleFilter, sortedPageable)
                .map(userConverter::toGridDto);
    }

    @Transactional(readOnly = true)
    public UserFormDto getForm(Long id) {
        return id == null ? new UserFormDto() : userConverter.toFormDto(findById(id));
    }

    public void create(UserFormDto form) {
        log.info("Creating user: username='{}'", form.getUserName());
        User user = new User();
        userConverter.applyFormToEntity(form, user);
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }
        usersRepository.save(user);
        log.info("User created with id={}", user.getId());
    }

    public void update(Long id, UserFormDto form) {
        log.info("Updating user id={}", id);
        User user = findById(id);
        userConverter.applyFormToEntity(form, user);
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }
        usersRepository.save(user);
        log.info("User id={} updated", id);
    }

    public void delete(Long id) {
        log.info("Deleting user id={}", id);
        usersRepository.delete(findById(id));
        log.info("User id={} deleted", id);
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return usersRepository.findByUserName(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }
}
