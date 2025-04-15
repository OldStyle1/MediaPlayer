package com.example.Client.repository;

import com.example.Client.model.AudioStatistic;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDateTime;
import java.util.List;

public interface AudioStatisticRepository extends JpaRepository<AudioStatistic, Long> {
    List<AudioStatistic> findByAudioFileId(Long audioFileId);
    
    List<AudioStatistic> findByPlayDateBetween(LocalDateTime start, LocalDateTime end);
    

}
