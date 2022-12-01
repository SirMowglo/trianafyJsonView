package com.salesianostriana.dam.trianafy.dto.songDto;

import com.fasterxml.jackson.annotation.JsonView;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.views.SongViews;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Builder
public class GetSongDto {
    private Long id;
    private String title;
    private String album;
    private String year;
    private String artist;

    public static GetSongDto of (Song s){
        String artistName;

        if(s.getArtist() == null){
            artistName = "No existe el artist";
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
