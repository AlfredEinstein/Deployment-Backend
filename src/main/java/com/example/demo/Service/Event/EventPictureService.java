package com.example.demo.Service.Event;

import com.example.demo.Model.EventPicture;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public interface EventPictureService {
    void addEventPicture(Integer eventId, MultipartFile file) throws MalformedURLException;
    List<EventPicture.EventPictures> getAllPicturesByEventId(Integer eventId);
}