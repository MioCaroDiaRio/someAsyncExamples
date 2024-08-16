package com.example.asyncexamples.async_classic;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BasicLoader {
    public List<byte[]> downloadImages() {

        RestTemplate restTemplate = new RestTemplate();
        List<byte[]> imageDataList = new ArrayList<>();
        List<String> urls = Arrays.asList(
                "https://s1.1zoom.ru/big3/399/339975-svetik.jpg",
                "https://s1.1zoom.ru/big3/399/339975-svetik.jpg",
                "https://s1.1zoom.ru/big3/399/339975-svetik.jpg",
                "https://s1.1zoom.ru/big3/399/339975-svetik.jpg",
                "https://s1.1zoom.ru/big3/399/339975-svetik.jpg",
                "https://s1.1zoom.ru/big3/399/339975-svetik.jpg",
                "https://s1.1zoom.ru/big3/399/339975-svetik.jpg",
                "https://s1.1zoom.ru/big3/399/339975-svetik.jpg",
                "https://get.wallhere.com/photo/landscape-mountains-lake-nature-reflection-grass-sky-river-national-park-valley-wilderness-Alps-tree-autumn-leaf-mountain-season-tarn-loch-mountainous-landforms-mountain-range-590185.jpg"
        );
        for (String url : urls) {
            imageDataList.add(restTemplate.getForObject(url, byte[].class));
        }
        return imageDataList;
    }
}