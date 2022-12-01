package com.salesianostriana.dam.trianafy.dto.songDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesianostriana.dam.trianafy.model.Song;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SongDtoConverter {
    public Song createSongDtoToSong(CreateSongDto s){
        return new Song (
                s.getTitle(),
                s.getAlbum(),
                s.getYear()
        );
    }

    public GetSongDto songToGetSongDto(Song s){
        String artistName;
        ObjectMapper mapper = new ObjectMapper();

        if(s.getArtist() == null){
            artistName = "No existe el artista";
        }else{
            artistName = s.getArtist().getName();
        }
        
        return GetSongDto
                .builder()
                .id(s.getId())
                .title(s.getTitle())
                .album(s.getAlbum())
                .year(s.getYear())
                .artist(artistName)
                .build();
    }
}
