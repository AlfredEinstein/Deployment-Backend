package com.example.demo.Service.TouristSpot;

import com.example.demo.Model.Event;
import com.example.demo.Model.TouristSpot;
import com.example.demo.Repository.TouristSpotRepository;
import com.example.demo.Service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TouristSpotServiceImpl implements TouristSpotService{
    @Autowired
    private TouristSpotRepository touristSpotRepository;
    @Autowired
    private StorageService storageService;
    @Override
    public List<TouristSpot> getAllSpots() {
        return (List<TouristSpot>) touristSpotRepository.findAll();
    }

    @Override
    public TouristSpot getSpotById(Integer spotId) {
        Optional<TouristSpot> spot=touristSpotRepository.findById(spotId);
        return spot.orElse(null);
    }

    @Override
    public ResponseEntity<?> addSpot(TouristSpot newSpot,MultipartFile spotPicture) {
        Optional<TouristSpot> touristSpot=touristSpotRepository.findBySpotName(newSpot.getSpotName());
        if(touristSpot.isPresent()){
            return ResponseEntity.badRequest().body("spot already present");
        }else{
            touristSpotRepository.save(newSpot);
            String fileName=System.currentTimeMillis()+"_"+newSpot.getSpotId()+"_"+newSpot.getSpotName()+"_Spot";
            if(storageService.uploadFile(fileName,spotPicture)){
                newSpot.setSpotPicture(fileName);
                touristSpotRepository.save(newSpot);
                return new ResponseEntity<>("New spot added",HttpStatus.CREATED);
            }else{
                touristSpotRepository.delete(newSpot);
                return new ResponseEntity<>("Error on Uploading new Spot",HttpStatus.CONFLICT);
            }

        }
    }

    @Override
    public ResponseEntity<TouristSpot> getSpotBySpotName(String spotName) {
        Optional<TouristSpot> spot=touristSpotRepository.findBySpotName(spotName);
        return new ResponseEntity<>(spot.orElse(null),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> uploadSpotPicture(Integer SpotId, MultipartFile file) {
        Optional<TouristSpot> spot=touristSpotRepository.findById(SpotId);
        if(spot.isPresent()){
            String fileName=System.currentTimeMillis()+"_"+SpotId+"_"+spot.get().getSpotName();
            if(storageService.uploadFile(fileName,file)){
                spot.get().setSpotPicture(fileName);
                touristSpotRepository.save(spot.get());
                return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
            }
            return new ResponseEntity<>("Conflict on uploading picture",HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>("Event not found in database",HttpStatus.NOT_FOUND);
    }
    @Override
    public String addAllSpots(List<TouristSpot> spots) {
        touristSpotRepository.saveAll(spots);
        return "Success";
    }

    @Override
    public ResponseEntity<String> removeSpotById(Integer spotId) {
        Optional<TouristSpot> spot=touristSpotRepository.findById(spotId);
        if(spot.isPresent()){
            storageService.deleteFile(spot.get().getSpotPicture());
            touristSpotRepository.deleteById(spotId);
            return ResponseEntity.ok().body("Tourist spot with id "+spotId+" is removed successfully.");
        }
        else{
            return new ResponseEntity<>("spot not found",HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public List<TouristSpot> getAllPopularTouristSpot() {
        List<TouristSpot> popularSpots = touristSpotRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(TouristSpot::getPeopleCount).reversed())
                .toList();

        return popularSpots.stream().limit(5).collect(Collectors.toList());
    }
}
