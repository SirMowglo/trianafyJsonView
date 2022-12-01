package com.salesianostriana.dam.trianafy.dto.songDto;

import com.fasterxml.jackson.annotation.JsonView;
import com.salesianostriana.dam.trianafy.model.Song;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Builder
public class GetSongDto {
    @JsonView(Views.Public.class)
    private Long id;

    @JsonView(Views.Public.class)
    private String title;

    @JsonView(Views.Internal.class)
    private String album;

    @JsonView(Views.Internal.class)
    private String year;

    @JsonView(Views.Internal.class)
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
