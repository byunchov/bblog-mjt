package net.byunchov.bblog.users.converter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.byunchov.bblog.users.dto.UserAuthDto;
import net.byunchov.bblog.users.dto.UserDto;
import net.byunchov.bblog.users.models.UserDao;

@Component
public class UserConverter {
    @Autowired
    private ModelMapper modelMapper;
    
    public UserDto convertEntityToDto(UserDao userDao) {
        // ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(userDao, UserDto.class);
    }

    public UserDao convertDtoToEntity(UserDto userDto) {
        // ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(userDto, UserDao.class);
    }

    public UserAuthDto convertEntityToAuthDto(UserDao userDao) {
        // ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(userDao, UserAuthDto.class);
    }
}
