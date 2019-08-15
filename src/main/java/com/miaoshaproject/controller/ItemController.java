package com.miaoshaproject.controller;


import com.miaoshaproject.controller.ViewObject.ItemVo;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.model.ItemModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller("/item")
@RequestMapping("/item")
@CrossOrigin(origins = {"*"},allowCredentials = "true")
public class ItemController extends  BaseController {

    @Autowired
    private ItemService itemService;


    @RequestMapping(value="/create",method = {RequestMethod.POST},consumes = CONTENT_TYPE_FORMED)
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name="title")String title,
                                      @RequestParam(name = "description")String description,
                                      @RequestParam(name = "price")BigDecimal price,
                                      @RequestParam(name = "stock")Integer stock,
                                      @RequestParam(name="imgUrl")String imgUrl) throws BusinessException {

        //封装service请求用来创建商品
        ItemModel itemModel=new ItemModel();
        itemModel.setTitle(title);
        itemModel.setStock(stock);
        itemModel.setPrice(price);
        itemModel.setDescription(description);
        itemModel.setImgUrl(imgUrl);
        ItemModel itemModelForReturn=itemService.createItem(itemModel);
        ItemVo itemVo=convertVoFromModel(itemModelForReturn);
        return CommonReturnType.create(itemVo);

    }

    @RequestMapping(value = "/get" ,method={RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name="id") Integer id){
        ItemModel itemModel=itemService.getItemById(id);
        ItemVo itemVo=convertVoFromModel(itemModel);
        return CommonReturnType.create(itemVo);
    }

    @RequestMapping(value = "/list" ,method={RequestMethod.GET})
    @ResponseBody
    public CommonReturnType listItem(){
        List<ItemModel>itemModelList=itemService.listItem();
        List<ItemVo> itemVoList=itemModelList.stream().map(itemModel -> {
            ItemVo itemVo=convertVoFromModel(itemModel);
            return itemVo;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVoList);
    }

    private ItemVo convertVoFromModel(ItemModel itemModel){
        if(itemModel==null){
            return null;
        }
        ItemVo itemVo=new ItemVo();
        BeanUtils.copyProperties(itemModel,itemVo);
        if(itemModel.getPromoModel()!=null){
            //有进行或即将进行的秒杀活动
            itemVo.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVo.setPromoId(itemModel.getPromoModel().getId());
            itemVo.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVo.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else{
            itemVo.setPromoStatus(0);
        }
        return itemVo;
    }



}
