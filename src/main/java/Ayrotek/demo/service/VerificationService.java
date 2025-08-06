package Ayrotek.demo.service;
import Ayrotek.demo.repository.VerRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;



import Ayrotek.demo.entity.Info;

@Service
@Slf4j
public class VerificationService{

    private final VerRepository verRepository;

    public VerificationService(VerRepository verRepository) {
        this.verRepository = verRepository;
    }

    public boolean verifyInfo(String plateNumber, String id, String serialNum) {
        final Optional<Info> info = verRepository.findByPlateNumber(plateNumber);
        if (info.isPresent()) {
            // Perform verification logic

            boolean isVerified = (info.get().getId().equals(id) && info.get().getPlateNumber().equals(plateNumber)
            && info.get().getSerialNum().equals(serialNum));

            // Update the verification status
            info.get().setVerified(isVerified);
            verRepository.save(info.get());

            log.info("Found record: plateNumber: {}, id: {}, serialNum: {}", 
            info.get().getPlateNumber(), info.get().getId(), info.get().getSerialNum());
            
            return isVerified;
            
        }
        log.info("No record found for plateNumber: {}", plateNumber);
        return false;
    }

    /*public void updateDb(boolean verifyInfo, String plateNumber){
        // Update the verification status in the database
            Optional<Info> info = verRepository.findByPlateNumber(plateNumber);
            if (info.isPresent()){
                info.get().setVerified(verifyInfo);
                verRepository.save(info.get());
            }
        
    }*/

}