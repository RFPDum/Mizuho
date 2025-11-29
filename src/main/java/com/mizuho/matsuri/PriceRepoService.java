package com.mizuho.matsuri;

import org.springframework.stereotype.Service;

@Service
public class PriceRepoService {
    private String data;

    public PriceRepoService() {

    }

    public void acceptData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

}
