package com.shakemate.util;

import com.google.errorprone.annotations.IncompatibleModifiers;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Component;

@Component
public class PasswordConvert {
    Argon2 ag;
    // 建議參數：iterations=3, memory=65536 KB (64MB) (使用的記憶體量（KB），此例為 65536 KB = 64MB。記憶體越大越安全。), parallelism=1(平行處理的執行緒數，通常設為 1 或 CPU 核心數)

    private static final int ITERATIONS = 3;
    private static final int MEMORY = 65536;
    private static final int PARALLELISM = 1;

    public PasswordConvert(){
        this.ag = Argon2Factory.create();

    }

    public String hashing(String password){
        return ag.hash(ITERATIONS, MEMORY, PARALLELISM, password.toCharArray());
    }


    public boolean passwordVerify (String password, String inputPassword){
        return ag.verify(password, inputPassword.toCharArray());
    }
}
