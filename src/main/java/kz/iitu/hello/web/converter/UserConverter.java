package kz.iitu.hello.web.converter;

import kz.iitu.hello.domain.entity.User;
import kz.iitu.hello.web.dto.form.UserFormDto;
import kz.iitu.hello.web.dto.grid.UserGridDto;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public void applyFormToEntity(UserFormDto form, User user) {
        user.setUserName(form.getUserName());
        user.setEmail(form.getEmail());
        user.setRole(form.getRole());
    }

    public UserFormDto toFormDto(User user) {
        UserFormDto dto = new UserFormDto();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }

    public UserGridDto toGridDto(User user) {
        UserGridDto dto = new UserGridDto();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}
