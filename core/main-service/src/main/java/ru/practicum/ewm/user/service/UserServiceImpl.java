package ru.practicum.ewm.user.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.user.*;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.exception.DuplicatedEmailException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(AdminUserParam param) {
        QUser user = QUser.user;
        List<BooleanExpression> conditions = new ArrayList<>();

        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(param.getFrom(), param.getSize(), sortById);

        if (param.getIds() != null) {
            for (Long paramId : param.getIds())
                conditions.add(QUser.user.id.eq(paramId));
        } else
            return UserMapper.mapToUserDto(userRepository.findAll(page));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();


        return UserMapper.mapToUserDto(userRepository.findAll(finalCondition, page));
    }

    @Transactional
    @Override
    public UserDto createUser(NewUserRequest userFromRequest) {
        checkDuplicatedEmail(userFromRequest.getEmail());
        User newUser = userRepository.save(UserMapper.mapFromRequest(userFromRequest));

        return UserMapper.mapToUserDto(newUser);
    }

    @Transactional
    @Override
    public void removeUser(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));
        }
        userRepository.deleteById(userId);
    }

    private void checkDuplicatedEmail(String email) {
        if (userRepository.findByEmailLike(email).isPresent())
            throw new DuplicatedEmailException(String.format("Пользователь с email = %s  уже существует", email));
    }
}
