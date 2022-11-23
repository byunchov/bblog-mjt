package net.byunchov.bblog.posts.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.byunchov.bblog.posts.models.PostDao;

@Repository
public interface PostRepository extends JpaRepository<PostDao, Long> {

}
