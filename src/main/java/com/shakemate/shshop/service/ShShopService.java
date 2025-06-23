package com.shakemate.shshop.service;

import com.shakemate.match.repository.MatchRepository;
import com.shakemate.shshop.dao.ShShopRepository;
import com.shakemate.shshop.dao.ShShopTypeRepository;
import com.shakemate.shshop.dto.ShProdDto;
import com.shakemate.shshop.dto.ShProdTypeDto;
import com.shakemate.shshop.model.ShProd;
import com.shakemate.shshop.model.ShProdPic;
import com.shakemate.shshop.model.ShProdType;
import com.shakemate.shshop.util.CompositeQueryForShshop;
import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.dto.UserDto;
import com.shakemate.user.model.Users;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("ShShop")
public class ShShopService {

    @Autowired
    private ShShopRepository repo;

    @Autowired
    private ShShopTypeRepository typeRepo;

    @Autowired
    private MatchRepository matchRepo;

    @Autowired
    UsersRepository usersRepo;

    @Autowired
    private SessionFactory session;

    // 找尋朋友清單
    @Transactional(readOnly = true)
    public List<Integer> findFriends(Integer userId) {
        return matchRepo.findFriendIdsByUserId(userId);
    }

    // 取得好友資訊(因為不用關聯所以分開查)
    @Transactional(readOnly = true)
    public List<UserDto> getFriendsInfo(Integer userId) {
        List<Integer> list = findFriends(userId);
        List<UserDto> dtoList = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            for (Integer id : list) {
                Users user = usersRepo.findById(id).orElse(null);
                if (user != null) {
                    UserDto dto = new UserDto().SimpleUserDto(user);
                    dtoList.add(dto);
                } else {
                    return null;
                }
            }
            return dtoList;
        }
    }

    // 找尋全部朋友的商品
    @Transactional(readOnly = true)
    public List<ShProdDto> findAvailableProds(Integer userId) {
        List<Integer> friends = findFriends(userId);
        List<ShProdDto> list = new ArrayList<>();
        if (friends == null || friends.size() == 0) {
            return null;
        } else {
            for (Integer friendId : friends) {
                for (ShProd p : repo.getByUserId(friendId)) {
                    if (p.getProdStatus() == (byte) 2) {
                        ShProdDto dto = new ShProdDto(p);
                        list.add(dto);
                    }
                }
            }
            return list;
        }
    }

    // 找尋朋友的商品Ascend(price)
    @Transactional(readOnly = true)
    public List<ShProdDto> findAvailableProdsPriceAsc(Integer userId) {
        List<Integer> friends = findFriends(userId);
        List<ShProdDto> list = new ArrayList<>();
        if (friends == null || friends.size() == 0) {
            return null;
        } else {
            for (Integer friendId : friends) {
                for (ShProd p : repo.getByUserId(friendId)) {
                    if (p.getProdStatus() == (byte) 2) {
                        ShProdDto dto = new ShProdDto(p);
                        list.add(dto);
                    }
                }
            }
            list.sort(Comparator.comparing(ShProdDto::getProdPrice));
            return list;
        }
    }

    // 找尋朋友的商品descend(price)
    @Transactional(readOnly = true)
    public List<ShProdDto> findAvailableProdsPriceDes(Integer userId) {
        List<Integer> friends = findFriends(userId);
        List<ShProdDto> list = new ArrayList<>();
        if (friends == null || friends.size() == 0) {
            return null;
        } else {
            for (Integer friendId : friends) {
                for (ShProd p : repo.getByUserId(friendId)) {
                    if (p.getProdStatus() == (byte) 2) {
                        ShProdDto dto = new ShProdDto(p);
                        list.add(dto);
                    }
                }
            }
            list.sort(Comparator.comparing(ShProdDto::getProdPrice).reversed());
            return list;
        }
    }

    // 找尋朋友的商品Ascend(time)
    @Transactional(readOnly = true)
    public List<ShProdDto> findAvailableProdsTimeAsc(Integer userId) {
        List<Integer> friends = findFriends(userId);
        List<ShProdDto> list = new ArrayList<>();
        if (friends == null || friends.size() == 0) {
            return null;
        } else {
            for (Integer friendId : friends) {
                for (ShProd p : repo.getByUserId(friendId)) {
                    if (p.getProdStatus() == (byte) 2) {
                        ShProdDto dto = new ShProdDto(p);
                        list.add(dto);
                    }
                }
            }
            list.sort(Comparator.comparing(ShProdDto::getUpdatedTime));
            return list;
        }
    }

    // 找尋朋友的商品Ascend(time)
    @Transactional(readOnly = true)
    public List<ShProdDto> findAvailableProdsTimeDes(Integer userId) {
        List<Integer> friends = findFriends(userId);
        List<ShProdDto> list = new ArrayList<>();
        if (friends == null || friends.size() == 0) {
            return null;
        } else {
            for (Integer friendId : friends) {
                for (ShProd p : repo.getByUserId(friendId)) {
                    if (p.getProdStatus() == (byte) 2) {
                        ShProdDto dto = new ShProdDto(p);
                        list.add(dto);
                    }
                }
            }
            list.sort(Comparator.comparing(ShProdDto::getUpdatedTime).reversed());
            return list;
        }
    }

    // 用商品類別尋找
    @Transactional(readOnly = true)
    public List<ShProdDto> findAvailableProdsByType(Integer userId, Integer typeId) {
        List<Integer> friends = findFriends(userId);
        List<ShProdDto> list = new ArrayList<>();
        if (friends == null || friends.size() == 0) {
            return null;
        } else {
            for (Integer friendId : friends) {
                for (ShProd p : repo.getByUserId(friendId)) {
                    if (p.getProdStatus() == (byte) 2 && p.getShProdType().getProdTypeId() == typeId) {
                        ShProdDto dto = new ShProdDto(p);
                        list.add(dto);
                    }
                }
            }
            return list;
        }
    }

    // 用文字尋找
    @Transactional(readOnly = true)
    public List<ShProdDto> findAvailableProdsByStr(Integer userId, String keyStr) {
        List<Integer> friends = findFriends(userId);
        List<ShProdDto> list = new ArrayList<>();
        if (friends == null || friends.size() == 0) {
            return null;
        } else {
            for (Integer friendId : friends) {
                for (ShProd p : repo.getByUserAndKeyStr(friendId, keyStr)) {
                    if (p.getProdStatus() == (byte) 2) {
                        ShProdDto dto = new ShProdDto(p);
                        list.add(dto);
                    }
                }
            }
            return list;
        }
    }

    // 查全部(管理員用，不以會員的session限制能看到的商品) **到時候要補上管理員的session
    @Transactional(readOnly = true)
    public List<ShProdDto> getAll() {
        List<ShProd> list = repo.findAllWithPics();
        List<ShProdDto> dtoList = new ArrayList<>();
        for (ShProd p : list) {
            ShProdDto dto = new ShProdDto(p);
            dtoList.add(dto);
        }
        return dtoList;
    }


    //查一個給商品頁面用
    @Transactional(readOnly = true)
    public ShProdDto getById(int id) {
        ShProd prod = repo.getByID(id);
        ShProdDto dto = null;
        if (prod != null) {
            dto = new ShProdDto(prod);
        }
        return dto;
    }
    // 查一個給更新商品用
    @Transactional(readOnly = true)
    public ShProdDto getByIdForUpdate(int id) {
        ShProd prod = repo.getByID(id);
        ShProdDto dto = null;
        if (prod != null) {
            dto = new ShProdDto().forUpdateDisplay(prod);
        }
        return dto;
    }

    // 計數器
    @Transactional
    public void addViews(int id) {
        ShProd prod = repo.getByID(id);
        if (prod != null) {
            prod.setProdViews(prod.getProdViews() + 1);
            repo.save(prod);
        }
    }

    // 會員下架商品(為安全起見，會員只能下架自己的商品)
    @Transactional
    public ShProdDto delistProdByUser(Integer userId, Integer prodId) {
        List<ShProd> prods = repo.getByUserId(userId);
        ShProdDto prod = null;
        if (prods != null || prods.size() > 0) {
            for (ShProd p : prods) {
                if (p.getProdId() == prodId) {
                    p.setProdStatus((byte) 3);
                    prod =new ShProdDto(repo.save(p));
                    break;
                }
            }
        }
        return prod;
    }

    // 修改商品狀態
    @Transactional
    public void changeProdStatus(int id, byte status) {
        ShProd prod = repo.getByID(id);
        if (prod != null) {
            prod.setProdStatus(status);
            repo.save(prod);
        }
    }

    // 用類別取得商品
    @Transactional(readOnly = true)
    public List<ShProdDto> getProdsByType(Integer typeId) {
        List<ShProd> list = repo.getByType(typeId);
        List<ShProdDto> dtos = new ArrayList<>();
        for (ShProd p : list) {
            ShProdDto dto = new ShProdDto(p);
            dtos.add(dto);
        }
        return dtos;

    }


    @Transactional(readOnly = true)
    public List<ShProdDto> getProdsByUser(Integer userId) {
        List<ShProd> list = repo.getByUserId(userId);
        List<ShProdDto> dtos = new ArrayList<>();
        for (ShProd p : list) {
            ShProdDto dto = new ShProdDto(p);
            dtos.add(dto);
        }
        return dtos;

    }

    @Transactional
    public ShProdDto createNewProduct(Integer userId, ShProd form, List<String> picUrls) {
        ShProd prod = new ShProd();
        Users user = new Users();
        user.setUserId(userId);
        prod.setUser(user);
        prod.setProdName(form.getProdName());
        prod.setProdBrand(form.getProdBrand());
        prod.setProdType(form.getShProdType());
        prod.setProdContent(form.getProdContent());
        prod.setProdStatusDesc(form.getProdStatusDesc());
        prod.setProdPrice(form.getProdPrice());
        prod.setProdCount(form.getProdCount());
        prod.setProdViews(0);
        prod.setProdStatus((byte) 0);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        prod.setProdRegTime(now);
        prod.setUpdatedTime(now);
        List<ShProdPic> picList = shProdPic(picUrls, prod);
        prod.setProdPics(picList);

        return new ShProdDto(repo.save(prod));
    }

    @Transactional
    public ShProdDto updateProd(Integer prodId, Integer userId, ShProd form, List<String> picUrls) {
        ShProd prod = repo.getByID(prodId);
        if (prod != null) {
            if (prod.getUser().getUserId() != userId) { // 這裡確認是否為會員的商品(安全機制)
                return null;
            } else {
                prod.setProdName(form.getProdName());
                prod.setProdBrand(form.getProdBrand());
                prod.setProdType(form.getShProdType());
                prod.setProdContent(form.getProdContent());
                prod.setProdStatusDesc(form.getProdStatusDesc());
                prod.setProdPrice(form.getProdPrice());
                prod.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
                List<ShProdPic> picList = shProdPic(picUrls, prod);
                prod.setProdPics(picList);
                repo.save(prod);
                return new ShProdDto(prod);
            }
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<ShProdTypeDto> getAllType() {
        List<ShProdType> list = typeRepo.findAll();
        List<ShProdTypeDto> dtos = new ArrayList<>();
        for (ShProdType p : list) {
            ShProdTypeDto dto = new ShProdTypeDto(p.getProdTypeId(), p.getProdTypeName());
            dtos.add(dto);
        }
        return dtos;
    }

    public List<ShProdDto> getProdsByCompositeQuery(Map<String, String> paraMap){
        List<Integer> friendIds = matchRepo.findFriendIdsByUserId(Integer.parseInt(paraMap.get("userId")));
        String friendIdStr = friendIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        paraMap.put("friendIds", friendIdStr);
        List<ShProdDto> dtoList =  CompositeQueryForShshop.getAllComposite(paraMap, session.openSession());

        return dtoList;
    }


    private List<ShProdPic> shProdPic(List<String> shProdPicUrl, ShProd shProd) {
        List<ShProdPic> pics = shProd.getProdPics();
        if (pics == null) {
            // 新增時：原 list 為 null，初始化
            pics = new ArrayList<>();
            shProd.setProdPics(pics);
        } else {
            // 更新時：清除舊圖（會觸發 Hibernate orphanRemoval 自動刪除）
            pics.clear();
        }
        // 新增新圖（無論是新增或更新）
        if (shProdPicUrl != null && shProdPicUrl.size() > 0) {
            for (String url : shProdPicUrl) {
                ShProdPic newPic = new ShProdPic();
                newPic.setShProd(shProd);
                newPic.setProdPic(url);
                pics.add(newPic);
            }
        }
        return pics;
    }
}
