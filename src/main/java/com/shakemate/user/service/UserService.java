package com.shakemate.user.service;



import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.model.Users;
import com.shakemate.util.PasswordConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UsersRepository usersRepo;

    @Autowired
    private PasswordConvert pc;

    public Users getUserByEmail(String email) {
        return usersRepo.findByEmail(email);
    }

    public boolean login(String email, String inputPassword) {
        Users user = usersRepo.findByEmail(email);
        return user != null && pc.passwordVerify(user.getPwd(), inputPassword);
    }

    @Transactional
    public void signIn(Users user, String[] interestsArr, String[] personalityArr, String imgUrl ){
        String rowPassword = user.getPwd();
        user.setPwd(pc.hashing(rowPassword));
        user.setImg1(imgUrl);
        String interests = String.join(",", interestsArr);
        String personality = String.join(",", personalityArr);
        user.setInterests(interests);
        user.setPersonality(personality);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        user.setCreatedTime(now);
        user.setUpdatedTime(now);
        user.setUserStatus((byte) 0);
        user.setPostStatus(false);
        user.setAtAcStatus(false);
        user.setSellStatus(false);
        usersRepo.save(user);
    }

    public List<Users> getAllUsers() {
        return usersRepo.findAll();
    }

    public Users getUserById(Integer userId){
        return usersRepo.findById(userId).orElse(null);
    }
}

