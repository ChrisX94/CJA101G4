package com.shakemate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import com.shakemate.adm.model.AdmService;
import com.shakemate.adm.model.AuthFuncService;
@Component
@SpringBootApplication
public class testApp implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(ShakemateApplication.class, args);
		
	}
	
	@Autowired
	AdmService sr;
	
	@Autowired
	AuthFuncService afs;
	
	@Autowired
    private SessionFactory sessionFactory;
	
    @Override
    public void run(String...args) throws Exception {
    	System.out.println(sr.getAll());
    	
    	System.out.println(afs.getAll());
    	
    	
    	
    	
    }

    
}
