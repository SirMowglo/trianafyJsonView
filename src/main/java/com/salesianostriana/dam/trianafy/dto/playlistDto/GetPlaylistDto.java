package com.salesianostriana.dam.trianafy.dto.playlistDto;

import com.salesianostriana.dam.trianafy.dto.songDto.GetSongDto;
import com.salesianostriana.dam.trianafy.model.Playlist;
import com.salesianostriana.dam.trianafy.model.Song;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GetPlaylistDto {
    private Long id;
    private String name;
    private Integer numberOfSongs;

    public static GetPlaylistDto of (Playlist p){
        return GetPlaylistDto
                .builder()
                .id(p.getId())
                .name(p.getName())
                .numberOfSongs(p.getSongs().size())
                .build();
    }
}
