package net.byunchov.bblog.posts.converter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.byunchov.bblog.posts.dto.PostDto;
import net.byunchov.bblog.posts.models.PostDao;


@Component
public class PostConverter {
    @Autowired
    private ModelMapper modelMapper;
    
    public PostDto convertEntityToDto(PostDao postDao) {
        // ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(postDao, PostDto.class);
    }

    public PostDao convertDtoToEntity(PostDto postDto) {
        // ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(postDto, PostDao.class);
    }
}
