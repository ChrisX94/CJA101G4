package com.shakemate.user.service;



import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.model.Users;
import com.shakemate.util.PasswordConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.List;

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

    public void signIn(String userName, String email, String password, byte gender, Date birthday, String location,
                       String intro, String interests, String personality) throws NoSuchAlgorithmException {
        Users user = new Users();
        user.setUsername(userName);
        user.setEmail(email);
        user.setPwd(pc.hashing(password));
        user.setGender(gender);
        user.setBirthday(birthday);
        user.setLocation(location);
        user.setIntro(intro);
        user.setInterests(interests);
        user.setPersonality(personality);
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

