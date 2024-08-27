package com.jmt.board.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmt.board.controllers.BoardDataSearch;
import com.jmt.board.entities.BoardData;
import com.jmt.global.ListData;
import com.jmt.global.Utils;
import com.jmt.global.rests.JSONData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardInfoService {
    private final RestTemplate restTemplate;
    private ObjectMapper om;
    private final Utils utils;


    public ListData<BoardData> getList(BoardDataSearch search)
    {
        String url = utils.url("/board/admin", "api-service");

        HttpHeaders headers = utils.getCommonHeaders("GET");
        int page = Objects.requireNonNullElse(search.getPage(), 1);
        int limit = Objects.requireNonNullElse(search.getLimit(), 20);
        String sopt = Objects.requireNonNullElse(search.getSopt(), "");
        String skey = Objects.requireNonNullElse(search.getSkey(), "");
        String bid = Objects.requireNonNullElse(search.getBid(), "");

        String bids = search.getBids() == null ? "" : search.getBids().stream().map(s-> "bids=" + s)
                .collect(Collectors.joining("&"));

        String sort = Objects.requireNonNullElse(search.getSort(), "");
        url += String.format("?page=%d&limit=%d&sopt=%s&skey=%s&bid=%s&sort=%s&bids=%s", page, limit, sopt, skey, bid, sort, bids);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<JSONData> response = restTemplate.exchange(URI.create(url), HttpMethod.GET, request, JSONData.class);

        if (!response.getStatusCode().is2xxSuccessful() || !response.getBody().isSuccess()) {
            return new ListData<>();
        }

        Object data = response.getBody().getData();
        try {
            return om.readValue(om.writeValueAsString(data), ListData.class);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new ListData<>();
    }


}