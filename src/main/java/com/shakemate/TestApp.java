package com.shakemate;


import com.shakemate.shshop.dao.ShShopRepository;
import com.shakemate.shshop.model.ShProd;
import com.shakemate.shshop.service.ShShopService;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class TestApp implements CommandLineRunner {
    public static void main(String[] args){
        SpringApplication.run(TestApp.class, args);
    }

    @Autowired
    ShShopRepository shShopRepository;

    @Autowired
    ShShopService shShopService;

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void run (String... args) throws Exception {

//        System.out.println("getAll" + shShopService.getAll());
//        System.out.println("getOne" +shShopService.getById(1));
//        System.out.println("getAllType" + shShopService.getAllType());
//        System.out.println("getProdsByType" + shShopService.getProdsByType(1));
//        System.out.println("getProdsByUser" + shShopService.getProdsByUser(1));
//        System.out.println("getFriendIds" + shShopService.findFriends(10));
//        System.out.println("getFriendIds" + shShopService.findAvailableProds(10));
//        System.out.println("findAvailableProdsAes" + shShopService.findAvailableProdsPriceAsc(10));
//        System.out.println("findAvailableProdsDes" + shShopService.findAvailableProdsPriceDes(10));
//        System.out.println("findAvailableProdsDesTime" + shShopService.findAvailableProdsTimeDes(10));
//        System.out.println("findAvailableProdsAscTime" + shShopService.findAvailableProdsTimeAsc(10));
//        System.out.println("findAvailableProdsByStr" + shShopService.findAvailableProdsByStr(10, "二"));
//        System.out.println("findAvailableProdsByStr" + shShopService.getFriendsInfo(10));
//        System.out.println("delist" + shShopService.delistProdByUser(2, 19));




    }
}
