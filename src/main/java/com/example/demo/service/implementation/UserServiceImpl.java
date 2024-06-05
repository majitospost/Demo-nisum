package com.example.demo.service.implementation;

import com.example.demo.entity.Phone;
import com.example.demo.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.presentation.presenter.UserPresenter;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Value("${password.regex}")
    private String passwordRegex;

    public User createUser(UserPresenter userPresenter) throws CustomException {
        if (!isValidEmail(userPresenter.getEmail())) {
            throw new CustomException("Correo inválido", HttpStatus.BAD_REQUEST);
        }

        if (!isValidPassword(userPresenter.getPassword())) {
            throw new CustomException("Contraseña inválida", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.findByEmail(userPresenter.getEmail()).isPresent()) {
            throw new CustomException("El correo ya está registrado", HttpStatus.CONFLICT);
        }
        Set<Phone> phones = new HashSet<>();
        userPresenter.getPhones().forEach(phonePresenter -> {
            Phone phone = Phone.builder()
                    .number(phonePresenter.getNumber())
                    .cityCode(phonePresenter.getCityCode())
                    .countryCode(phonePresenter.getCountryCode())
                    .build();
            phones.add(phone);
        });
        User user = User.builder()
                .name(userPresenter.getName())
                .email(userPresenter.getEmail())
                .phones(phones)
                .build();
        user.setId(userPresenter.getId() == null ? UUID.randomUUID() : userPresenter.getId());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(userPresenter.getPassword()));

        String token = JWTUtil.generateToken(userPresenter.getEmail());
        user.setToken(token);

        LocalDateTime now = LocalDateTime.now();
        user.setCreated(now);
        user.setModified(now);
        user.setLastLogin(now);

        user.setActive(true);

        User savedUser = userRepository.save(user);
        return savedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAllWithPhones();
    }

    public void deleteUser(UUID id) throws CustomException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("Usuario no encontrado", HttpStatus.NOT_FOUND));
        userRepository.delete(user);
    }

    @Override
    public User updateUser(UserPresenter userPresenter) throws CustomException {
        if (!isValidEmail(userPresenter.getEmail())) {
            throw new CustomException("Correo inválido", HttpStatus.BAD_REQUEST);
        }

        if (!isValidPassword(userPresenter.getPassword())) {
            throw new CustomException("Contraseña inválida", HttpStatus.BAD_REQUEST);
        }

        Optional<User> currentUser = userRepository.findById(userPresenter.getId());

        if (!currentUser.isPresent()) {
            throw new CustomException("Usuario no encontrado", HttpStatus.NOT_FOUND);
        }

        if (userPresenter.getEmail() != null && !userPresenter.getEmail().equals(currentUser.get().getEmail())) {
            if (userRepository.findByEmail(userPresenter.getEmail()).isPresent()) {
                throw new CustomException("El correo ya está registrado", HttpStatus.CONFLICT);
            }
        }

        Set<Phone> phones = new HashSet<>();
        userPresenter.getPhones().forEach(phonePresenter -> {
            Phone phone = Phone.builder()
                    .number(phonePresenter.getNumber())
                    .cityCode(phonePresenter.getCityCode())
                    .countryCode(phonePresenter.getCountryCode())
                    .build();
            phones.add(phone);
        });
        currentUser.get().setName(userPresenter.getName());
        currentUser.get().setEmail(userPresenter.getEmail());
        currentUser.get().setPhones(phones);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        currentUser.get().setPassword(passwordEncoder.encode(userPresenter.getPassword()));

        String token = JWTUtil.generateToken(userPresenter.getEmail());
        currentUser.get().setToken(token);
        currentUser.get().setModified(LocalDateTime.now());

        return userRepository.save(currentUser.get());
    }

    public boolean isValidEmail(String email) {
        return email.matches("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$");
    }

    public boolean isValidPassword(String password) {
        return password.matches(passwordRegex);
    }
}
