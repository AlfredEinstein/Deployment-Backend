package com.example.demo.Service.TouristSpot;

import com.example.demo.Model.SpotPicture;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.List;

public interface SpotPicturesServices {
    void addSpotPictures(Integer spotId, MultipartFile file);
    List<SpotPicture.SpotPictures> getSpotPictureById(Integer spotId);
}