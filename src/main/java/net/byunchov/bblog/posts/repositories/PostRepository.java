package net.byunchov.bblog.posts.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import net.byunchov.bblog.posts.models.PostDao;

@Repository
public interface PostRepository extends PagingAndSortingRepository<PostDao, Long> {
    Page<PostDao> findByTitleContaining(String title, Pageable pageable);
}
