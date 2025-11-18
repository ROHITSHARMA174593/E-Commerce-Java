package com.Social.Media.repositories;

import com.Social.Media.model.SocialUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SocialUserRepository extends JpaRepository<SocialUser,Long> {
}

// for learning purpose you can't make two interface in a interface where the interface extends any class or implements any interface
//public interface Aaa extends JpaRepository<SocialUser, Long>{
//    //todo ::: this is an interface. it has only defining not defination
//}

