package com.cariochi.recordo.mockmvc;

import com.cariochi.recordo.mockmvc.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        userDto.setId(1);
        return userDto;
    }

    @PutMapping
    public UserDto update(@RequestBody UserDto userDto) {
        userDto.setName("Updated");
        return userDto;
    }

    @PatchMapping("/{id}")
    public UserDto patch(@PathVariable int id, @RequestBody UserDto userDto) {
        userDto.setId(id);
        userDto.setName("Updated");
        return userDto;
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable int id,
                           @RequestParam(value = "name", required = false) String name,
                           @RequestHeader(value = "locale", required = false) String locale
    ) {
        return UserDto.builder()
                .id(id)
                .name(name + " " + locale)
                .build();
    }

    @GetMapping
    public Page<UserDto> findAll(@RequestParam(required = false, defaultValue = "2") int count, Pageable pageable) {
        final List<UserDto> users = IntStream.range(1, count + 1)
                .mapToObj(i -> UserDto.builder().id(i).name("user_" + i).build())
                .collect(toList());
        return count == 0 ? Page.empty() : new PageImpl<>(users, pageable, users.size());
    }

    @GetMapping("/slice")
    public Slice<UserDto> getSlice(@RequestParam(required = false, defaultValue = "2") int count, Pageable pageable) {
        final List<UserDto> users = IntStream.range(1, count + 1)
                .mapToObj(i -> UserDto.builder().id(i).name("user_" + i).build())
                .collect(toList());
        return count == 0 ? Page.empty() : new SliceImpl<>(users, pageable, true);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        log.info("User {} deleted", id);
    }

}
