package com.coldsteelpope.myrestfulservice.repository;

import com.coldsteelpope.myrestfulservice.bean.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>
{

}
