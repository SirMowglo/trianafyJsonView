package com.salesianostriana.dam.trianafy.dto.songDto;

import com.fasterxml.jackson.annotation.JsonView;
import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.views.SongViews;
import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SongDto {
    @JsonView({SongViews.SongDto.class,
            SongViews.GetSongDto.class})
    private Long id;

    @JsonView({SongViews.SongDto.class,
            SongViews.GetSongDto.class,
            SongViews.CreateSongDto.class})
    private String title;

    @JsonView({SongViews.SongDto.class,
            SongViews.GetSongDto.class,
            SongViews.CreateSongDto.class})
    private String album;

    @JsonView({SongViews.SongDto.class,
            SongViews.GetSongDto.class,
            SongViews.CreateSongDto.class})
    private String year;

    @JsonView({SongViews.GetSongDto.class})
    private String artist_name;

    @JsonView({SongViews.SongDto.class})
    private Artist artist;

    public static SongDto of(Song s){
        String artistName;

        if(s.getArtist() == null){
            artistName = "No existe el artista";
        }else{
            artistName = s.getArtist().getName();
        }

        return SongDto.builder()
                .id(s.getId())
                .title(s.getTitle())
                .album(s.getAlbum())
                .year(s.getYear())
                .artist(s.getArtist())
                .artist_name(artistName)
                .build();
    }
}
